/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.fabricmc.fabric.impl.build;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.api.tasks.TaskAction;
import org.gradle.work.DisableCachingByDefault;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import net.fabricmc.classtweaker.api.ClassTweakerReader;
import net.fabricmc.classtweaker.api.visitor.AccessWidenerVisitor;
import net.fabricmc.classtweaker.api.visitor.ClassTweakerVisitor;

@DisableCachingByDefault
public abstract class CheckInjectedInterfaceDefaultMethodsTask extends DefaultTask {
	@InputFile
	@PathSensitive(PathSensitivity.NONE)
	public abstract RegularFileProperty getModJar();

	@InputFiles
	@PathSensitive(PathSensitivity.ABSOLUTE)
	public abstract ConfigurableFileCollection getSourceRoots();

	@Input
	public abstract Property<Boolean> getDisplayGitHubAnnotations();

	public CheckInjectedInterfaceDefaultMethodsTask() {
		getDisplayGitHubAnnotations().convention(true);
	}

	@TaskAction
	protected void check() throws IOException {
		List<Violation> violations = new ArrayList<>();

		try (var zip = new ZipFile(getModJar().get().getAsFile())) {
			Collection<String> injectedInterfaces = findInjectedInterfaces(zip);

			for (String itf : injectedInterfaces) {
				ZipEntry classEntry = zip.getEntry(itf + ".class");

				try (InputStream in = zip.getInputStream(classEntry)) {
					checkInjectedInterface(in.readAllBytes(), violations::add);
				}
			}
		}

		if (!violations.isEmpty()) {
			if (getDisplayGitHubAnnotations().get()) {
				for (Violation violation : violations) {
					if (violation.sourceFile == null) {
						continue;
					}

					String directory = violation.itf.substring(0, violation.itf.lastIndexOf('/') + 1);
					String relativeSourcePath = directory + violation.sourceFile;

					for (File sourceRoot : getSourceRoots()) {
						File sourceFile = new File(sourceRoot, relativeSourcePath);

						if (sourceFile.exists()) {
							System.out.printf("::error file=%s::Injected interface has abstract method %s%n", escapeGitHubActionsProperty(sourceFile.getAbsolutePath()), violation.method);
							break;
						}
					}
				}
			}

			var messageBuilder = new StringBuilder("Found abstract methods in injected interfaces:");

			for (Violation violation : violations) {
				messageBuilder.append("\n - ").append(violation.itf).append('.').append(violation.method);
			}

			throw new RuntimeException(messageBuilder.toString());
		}
	}

	private static String escapeGitHubActionsProperty(String value) {
		// See https://github.com/actions/toolkit/blob/0786132e6a1a4451c2d392bbbf481c2b172d4312/packages/core/src/command.ts#L110
		return value
				.replace("%", "%25")
				.replace("\r", "%0D")
				.replace("\n", "%0A")
				.replace(":", "%3A")
				.replace(",", "%2C");
	}

	private Set<String> findInjectedInterfaces(ZipFile zip) throws IOException {
		ZipEntry fmjEntry = zip.getEntry("fabric.mod.json");
		JsonObject fabricModJson;

		try (InputStream in = zip.getInputStream(fmjEntry); var reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
			fabricModJson = new Gson().fromJson(reader, JsonObject.class);
		}

		if (!fabricModJson.has("accessWidener")) {
			// No class tweaker => no injected interfaces
			return Set.of();
		}

		String accessWidener = fabricModJson.getAsJsonPrimitive("accessWidener").getAsString();
		ZipEntry ctEntry = zip.getEntry(accessWidener);
		Set<String> injectedInterfaces = new HashSet<>();
		ClassTweakerVisitor visitor = new ClassTweakerVisitor() {
			@Override
			public void visitInjectedInterface(String owner, String iface, boolean transitive) {
				int genericsIndex = iface.indexOf('<');

				if (genericsIndex >= 0) {
					iface = iface.substring(0, genericsIndex);
				}

				injectedInterfaces.add(iface);
			}

			@Override
			public AccessWidenerVisitor visitAccessWidener(String owner) {
				return new AccessWidenerVisitor() {
				};
			}
		};

		try (InputStream in = zip.getInputStream(ctEntry)) {
			ClassTweakerReader.create(visitor).read(in.readAllBytes());
		}

		return injectedInterfaces;
	}

	private void checkInjectedInterface(byte[] classBytes, Consumer<Violation> violationConsumer) {
		ClassVisitor visitor = new ClassVisitor(Opcodes.ASM9) {
			private String className;
			private String sourceFile;

			@Override
			public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
				className = name;
			}

			@Override
			public void visitSource(String source, String debug) {
				if (source != null) {
					sourceFile = source;
				}
			}

			@Override
			public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
				if ((access & Opcodes.ACC_ABSTRACT) != 0) {
					violationConsumer.accept(new Violation(className, name + descriptor, sourceFile));
				}

				return null;
			}
		};
		new ClassReader(classBytes).accept(visitor, ClassReader.SKIP_CODE | ClassReader.SKIP_FRAMES);
	}

	private record Violation(String itf, String method, String sourceFile) {
	}
}
