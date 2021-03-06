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

package net.fabricmc.fabric.impl.registry;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public interface RegistryListener<T> {
	default void beforeRegistryCleared(Registry<T> registry) {
	}

	default void beforeRegistryRegistration(Registry<T> registry, int id, Identifier identifier, T object, boolean isNew) {
	}

	default void afterRegistryRegistration(Registry<T> registry, int id, Identifier identifier, T object) {
	}
}
