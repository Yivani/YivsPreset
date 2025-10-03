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

import org.lwjgl.glfw.GLFW;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import yiv.yivspresets.apply.OptionApplier;
import yiv.yivspresets.config.UserConfig;
import yiv.yivspresets.ui.HudToast;

/**
 * Client-side initialization for Yiv's Presets.
 * 
 * This class handles all client-side functionality including:
 * - Keybind registration for cycling presets
 * - HUD toast rendering setup
 * - Preset cycling logic
 * - Integration with Minecraft's client tick events
 * 
 * @author Yivani
 * @version 1.0.2
 * @since 1.0.0
 */
public class YivsPresetsClient implements ClientModInitializer {
	/** Keybinding for cycling through presets (default: F9) */
	private KeyBinding toggleKey;

	/**
	 * Initializes the client-side components of the mod.
	 * Called when the client starts up.
	 */
	@Override
	public void onInitializeClient() {
		// Initialize the HUD toast renderer for showing preset change notifications
		HudToast.init();
		
		// Register the keybinding for cycling presets
		toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.yivspresets.cycle", 
				InputUtil.Type.KEYSYM, 
				GLFW.GLFW_KEY_F9, 
				"Yiv's Presets"
		));

		// Register a tick event listener to handle keybind presses
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			// Check if the toggle key was pressed this tick
			while (toggleKey.wasPressed()) {
				cyclePreset(client);
			}
		});
	}

	/**
	 * Cycles to the next preset in the list and applies it.
	 * 
	 * This method:
	 * 1. Loads the current user configuration
	 * 2. Finds the current preset index
	 * 3. Moves to the next preset (wrapping around to the first if at the end)
	 * 4. Saves the new default preset
	 * 5. Applies the preset settings to the game
	 * 6. Shows a HUD toast notification
	 * 
	 * @param mc The Minecraft client instance
	 */
	private void cyclePreset(MinecraftClient mc) {
		UserConfig cfg = UserConfig.loadOrCreate();
		if (cfg.presets.isEmpty()) return;
		
		// Find the current preset index
		String current = cfg.defaultPresetId;
		int idx = 0;
		for (int i = 0; i < cfg.presets.size(); i++) {
			if (cfg.presets.get(i).id.equals(current)) { 
				idx = i; 
				break; 
			}
		}
		
		// Move to the next preset (with wraparound)
		idx = (idx + 1) % cfg.presets.size();
		cfg.defaultPresetId = cfg.presets.get(idx).id;
		cfg.save();

		// Apply the new preset settings and show notification
		OptionApplier.apply(mc, cfg.presets.get(idx).profile);
		HudToast.show(mc, net.minecraft.text.Text.literal("Preset: " + cfg.presets.get(idx).name));
	}
}