/*
 * Copyright (c) 2024 Yivani
 * 
 * This file is part of Yiv's Presets, a Minecraft Fabric mod.
 * 
 * Yiv's Presets is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package yiv.yivspresets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ModInitializer;

/**
 * Main mod class for Yiv's Presets.
 * 
 * This mod provides client-side performance presets that can be cycled through
 * with a single keybind (F9 by default). Users can create custom presets for
 * different scenarios like building, exploring, or combat, with different
 * render distances, graphics settings, and other performance options.
 * 
 * @author Yivani
 * @version 1.0.2
 * @since 1.0.0
 */
public class YivsPresets implements ModInitializer {
	/** The unique identifier for this mod */
	public static final String MOD_ID = "yivs-presets";

	/**
	 * Logger instance for this mod.
	 * Used to write information, warnings, and errors to the console and log file.
	 * It's considered best practice to use the mod ID as the logger's name
	 * so it's clear which mod wrote each log message.
	 */
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	/**
	 * Called when the mod is initialized.
	 * This code runs as soon as Minecraft is in a mod-load-ready state.
	 * However, some things (like resources) may still be uninitialized.
	 */
	@Override
	public void onInitialize() {
		LOGGER.info("Yiv's Presets initialized successfully!");
		LOGGER.info("Created by Yivani - Performance presets for Minecraft");
	}
}