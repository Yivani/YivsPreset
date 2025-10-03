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

package yiv.yivspresets.apply;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.GraphicsMode;
import net.minecraft.client.option.ParticlesMode;
import yiv.yivspresets.preset.PresetProfiles;

/**
 * Applies preset profiles to Minecraft's GameOptions.
 * 
 * This class handles the complex task of applying performance preset settings
 * to Minecraft's game options. It uses a two-tier approach:
 * 1. Direct API calls for maximum compatibility with Minecraft 1.21.x
 * 2. Reflection-based fallback for handling version differences and edge cases
 * 
 * The class includes safety measures to prevent crashes:
 * - Clamps values to valid ranges
 * - Defers risky operations (like graphics changes) when in-world
 * - Handles missing or changed methods gracefully
 * 
 * @author Yivani
 * @version 1.0.2
 * @since 1.0.0
 */
public final class OptionApplier {
	/** Private constructor to prevent instantiation of utility class */
	private OptionApplier() {}

	/**
	 * Applies a preset profile to the client's game options.
	 * 
	 * This method attempts to apply all settings from the given profile to
	 * Minecraft's GameOptions. It tries direct API calls first for better
	 * performance and reliability, then falls back to reflection if needed.
	 * 
	 * Safety features:
	 * - Values are clamped to valid ranges to prevent crashes
	 * - Risky operations (graphics, VSync, mipmaps) are deferred when in-world
	 * - Missing methods are handled gracefully without crashing
	 * 
	 * @param mc The Minecraft client instance
	 * @param p The preset profile to apply
	 */
	public static void apply(MinecraftClient mc, PresetProfiles.Profile p) {
		if (mc == null || mc.options == null || p == null) return;
		GameOptions go = mc.options;
        boolean inWorld = mc.world != null;

		try {
			boolean skippedRisky = false;
			
			// === INTEGER SETTINGS (only update when changed for performance) ===
			
			// Render Distance: Controls how far the world is rendered (2-32 chunks)
			int rd = clamp(p.renderDistance, 2, 32);
			if (!go.getViewDistance().getValue().equals(rd)) go.getViewDistance().setValue(rd);
			
			// Simulation Distance: Controls entity simulation range (vanilla minimum is 5)
			try {
				int sd = clamp(p.simulationDistance, 5, 32);
				Object sim = byMethods(go, "getSimulationDistance");
				if (sim != null) {
					Object cur = null; 
					try { 
						cur = sim.getClass().getMethod("getValue").invoke(sim); 
					} catch (Throwable ignored2) {}
					if (!sdEquals(cur, sd)) invokeSetValue(sim, Integer.valueOf(sd));
				}
			} catch (Throwable ignored) {}
			
			// Mipmap Levels: Can cause resource reload, so skip while in-world for safety
			int clampedMm = clamp(p.mipmapLevels, 0, 4);
			if (!inWorld) {
				if (!go.getMipmapLevels().getValue().equals(clampedMm)) go.getMipmapLevels().setValue(clampedMm);
			} else {
				skippedRisky = true; // Mark that we skipped a risky operation
			}
			
			// Biome Blend Radius: Controls smoothness of biome transitions (0-7)
			int bb = clamp(p.biomeBlendRadius, 0, 7);
			if (!go.getBiomeBlendRadius().getValue().equals(bb)) go.getBiomeBlendRadius().setValue(bb);
			
			// Max FPS: Frame rate limit (5-260, where 260 = unlimited)
			int fps = clamp(p.maxFps, 5, 260);
			if (!go.getMaxFps().getValue().equals(fps)) go.getMaxFps().setValue(fps);

			// Floats
			double eds = clamp(p.entityDistanceScaling, 0.5f, 5.0f);
			if (!go.getEntityDistanceScaling().getValue().equals(eds)) go.getEntityDistanceScaling().setValue(eds);
			double dist = clamp(p.distortionEffectsScale, 0f, 1f);
			if (!go.getDistortionEffectScale().getValue().equals(dist)) go.getDistortionEffectScale().setValue(dist);

			// Booleans
			if (!go.getEntityShadows().getValue().equals(p.entityShadows)) go.getEntityShadows().setValue(p.entityShadows);
			if (!go.getBobView().getValue().equals(p.viewBobbing)) go.getBobView().setValue(p.viewBobbing);
			// VSync via reflection-friendly path to avoid mapping differences
			try {
				Object vs = byMethods(go, "getVsyncEnabled");
				if (vs == null) vs = byMethods(go, "getEnableVsync");
				if (vs == null) vs = byMethods(go, "getVsync");
				if (vs != null) {
					Object cur = null; try { cur = vs.getClass().getMethod("getValue").invoke(vs); } catch (Throwable ignored2) {}
					// Apply VSync only when not in a world to avoid GPU pipeline reinit mid-frame
					if (!inWorld && (!(cur instanceof Boolean) || ((Boolean) cur) != p.vSync)) {
						invokeSetValue(vs, Boolean.valueOf(p.vSync));
					} else if (inWorld) {
						skippedRisky = true;
					}
				}
			} catch (Throwable ignored) {}

			// Enums
			if (!inWorld) {
				net.minecraft.client.option.GraphicsMode gm = GraphicsMode.FAST;
				switch (p.graphics) {
					case FAST -> gm = GraphicsMode.FAST;
					case FANCY -> gm = GraphicsMode.FANCY;
					case FABULOUS -> gm = GraphicsMode.FABULOUS;
				}
				if (!go.getGraphicsMode().getValue().equals(gm)) go.getGraphicsMode().setValue(gm);
			} else {
				skippedRisky = true; // defer graphics change
			}
			go.getParticles().setValue(mapParticles(p.particles));
			go.getCloudRenderMode().setValue(mapClouds(p.clouds));

			// Persist
			go.write();

			// Inform user if some changes were deferred for safety
			if (skippedRisky) {
				yiv.yivspresets.ui.HudToast.show(mc, net.minecraft.text.Text.literal("Preset applied (graphics/vsync/mipmap queued)"));
			}

			// Try to set Ambient Occlusion (smooth lighting) via reflection-only mapping
			setEnumOption(go, p.smoothLighting ? "MAX" : "OFF",
					new String[]{"ambientOcclusion", "ao", "smoothLighting"},
					new String[]{"net.minecraft.client.option.AmbientOcclusion", "net.minecraft.client.option.AmbientOcclusionOption"});

			// Note: avoid forcing a resource reload here; Minecraft usually handles
			// updates gracefully. Forcing a reload mid-frame can cause crashes on
			// some setups (shader pipeline not ready). If needed, expose a manual
			// reload in the UI instead.
			return; // success on direct path (+ AO via reflection)
		} catch (Throwable ignored) {
			// Fall back to reflection path below
		}

		Object opts = go; // reflection fallback uses Object
		// Direct known getters first (robust for 1.21.x)
		setIntOption(opts, clamp(p.renderDistance, 2, 32), byMethods(opts, "getViewDistance"), "viewDistance");
		if (!inWorld) {
			setIntOption(opts, clamp(p.mipmapLevels, 0, 4), byMethods(opts, "getMipmapLevels"), "mipmapLevels", "mipmapLevel");
		}
		setIntOption(opts, clamp(p.simulationDistance, 5, 32), byMethods(opts, "getSimulationDistance"), "simulationDistance");
		setIntOption(opts, clamp(p.biomeBlendRadius, 0, 7), byMethods(opts, "getBiomeBlendRadius"), "biomeBlendRadius", "biomeBlend");
		setIntOption(opts, clamp(p.maxFps, 5, 260), byMethods(opts, "getMaxFps"), "maxFps", "fps");

		// Floats / Doubles
		setFloatOption(opts, clamp(p.entityDistanceScaling, 0.5f, 5.0f), byMethods(opts, "getEntityDistanceScaling"), "entityDistanceScaling", "entityDistance");
		setFloatOption(opts, clamp(p.distortionEffectsScale, 0f, 1f), byMethods(opts, "getDistortionEffectScale"), "distortionEffectScale", "distortionEffectsScale");

		// Booleans
		setBoolOption(opts, p.entityShadows, byMethods(opts, "getEntityShadows"), "entityShadows");
		setBoolOption(opts, p.viewBobbing, byMethods(opts, "getBobView"), "bobView", "viewBobbing");
		if (!inWorld) setBoolOption(opts, p.vSync, byMethods(opts, "getVsyncEnabled"), "vsync", "vSync", "vSyncEnabled");
		// Smooth lighting in vanilla is ambient occlusion levels; we treat true as MAX, false as OFF
		setEnumOption(opts, p.smoothLighting ? "MAX" : "OFF",
				new String[]{"ambientOcclusion", "ao", "smoothLighting"},
				new String[]{"net.minecraft.client.option.AmbientOcclusionOption"});

		// Enums
	// Graphics: avoid Fabulous while in a world; map to FANCY
	PresetProfiles.Graphics targetG = p.graphics;
	if (inWorld && targetG == PresetProfiles.Graphics.FABULOUS) targetG = PresetProfiles.Graphics.FANCY;
	setEnumOption(opts, targetG.name(), byMethods(opts, "getGraphicsMode"), new String[]{"graphicsMode", "graphics"},
				new String[]{"net.minecraft.client.option.GraphicsMode"});
	setEnumOption(opts, p.particles.name(), byMethods(opts, "getParticles"), new String[]{"particles", "particle"},
				new String[]{"net.minecraft.client.option.ParticlesMode"});
	setEnumOption(opts, p.clouds.name(), byMethods(opts, "getCloudRenderMode"), new String[]{"cloudRenderMode", "clouds"},
				new String[]{"net.minecraft.client.option.CloudRenderMode"});

		// Try to persist options
		try { opts.getClass().getMethod("write").invoke(opts); } catch (Throwable ignored) {}
	}

	private static ParticlesMode mapParticles(PresetProfiles.Particles p) {
		return switch (p) {
			case MINIMAL -> ParticlesMode.MINIMAL;
			case DECREASED -> ParticlesMode.DECREASED;
			case ALL -> ParticlesMode.ALL;
		};
	}

	private static CloudRenderMode mapClouds(PresetProfiles.Clouds c) {
		return switch (c) {
			case OFF -> CloudRenderMode.OFF;
			case FAST -> CloudRenderMode.FAST;
			case FANCY -> CloudRenderMode.FANCY;
		};
	}

	// Try to resolve SimpleOption via a specific zero-arg getter on GameOptions
	private static Object byMethods(Object options, String getterName) {
		try {
			Method m = options.getClass().getMethod(getterName);
			return m.invoke(options);
		} catch (Throwable ignored) { return null; }
	}

	private static void setIntOption(Object options, int value, String... nameHints) {
		Object opt = findSimpleOption(options, nameHints);
		if (opt == null) return;
		invokeSetValue(opt, Integer.valueOf(value));
	}

	private static void setIntOption(Object options, int value, Object resolved, String... nameHints) {
		if (resolved != null) { invokeSetValue(resolved, Integer.valueOf(value)); return; }
		setIntOption(options, value, nameHints);
	}

	private static void setFloatOption(Object options, float value, String... nameHints) {
		Object opt = findSimpleOption(options, nameHints);
		if (opt == null) return;
		// Some options are Double
		if (!invokeSetValue(opt, Float.valueOf(value))) {
			invokeSetValue(opt, Double.valueOf(value));
		}
	}

	private static void setFloatOption(Object options, float value, Object resolved, String... nameHints) {
		if (resolved != null) {
			if (!invokeSetValue(resolved, Float.valueOf(value))) invokeSetValue(resolved, Double.valueOf(value));
			return;
		}
		setFloatOption(options, value, nameHints);
	}

	private static void setBoolOption(Object options, boolean value, String... nameHints) {
		Object opt = findSimpleOption(options, nameHints);
		if (opt == null) return;
		invokeSetValue(opt, Boolean.valueOf(value));
	}

	private static void setBoolOption(Object options, boolean value, Object resolved, String... nameHints) {
		if (resolved != null) { invokeSetValue(resolved, Boolean.valueOf(value)); return; }
		setBoolOption(options, value, nameHints);
	}

	private static void setEnumOption(Object options, String enumName, String[] optionNameHints, String[] enumClassCandidates) {
		Object opt = findSimpleOption(options, optionNameHints);
		if (opt == null) return;
		// Try each candidate enum class by name
		for (String cls : enumClassCandidates) {
			try {
				Class<?> ec = Class.forName(cls);
				@SuppressWarnings({"rawtypes", "unchecked"})
				Object enumVal = Enum.valueOf((Class<? extends Enum>) ec, enumName);
				if (invokeSetValue(opt, enumVal)) return;
			} catch (Throwable ignored) {}
		}
	}

	private static void setEnumOption(Object options, String enumName, Object resolved, String[] optionNameHints, String[] enumClassCandidates) {
		if (resolved != null) {
			for (String cls : enumClassCandidates) {
				try {
					Class<?> ec = Class.forName(cls);
					@SuppressWarnings({"rawtypes", "unchecked"})
					Object enumVal = Enum.valueOf((Class<? extends Enum>) ec, enumName);
					if (invokeSetValue(resolved, enumVal)) return;
				} catch (Throwable ignored) {}
			}
		}
		setEnumOption(options, enumName, optionNameHints, enumClassCandidates);
	}

	private static Object findSimpleOption(Object options, String... nameHints) {
		Class<?> c = options.getClass();
		// 1) Try getters like getXxx
		for (Method m : c.getMethods()) {
			if (m.getParameterCount() != 0) continue;
			String n = m.getName().toLowerCase();
			if (!n.startsWith("get")) continue;
			for (String h : nameHints) {
				if (n.contains(h.toLowerCase())) {
					try {
						Object opt = m.invoke(options);
						if (hasSetValue(opt)) return opt;
					} catch (Throwable ignored) {}
				}
			}
		}
		// 2) Try fields named like hints
		for (Field f : c.getFields()) {
			String n = f.getName().toLowerCase();
			for (String h : nameHints) {
				if (n.contains(h.toLowerCase())) {
					try {
						Object opt = f.get(options);
						if (hasSetValue(opt)) return opt;
					} catch (Throwable ignored) {}
				}
			}
		}
		return null;
	}

	private static boolean hasSetValue(Object opt) {
		if (opt == null) return false;
		for (Method m : opt.getClass().getMethods()) {
			if (m.getName().equals("setValue") && m.getParameterCount() == 1) return true;
		}
		return false;
	}

	private static boolean invokeSetValue(Object opt, Object value) {
		if (opt == null) return false;
		for (Method m : opt.getClass().getMethods()) {
			if (!m.getName().equals("setValue") || m.getParameterCount() != 1) continue;
			try { m.invoke(opt, value); return true; } catch (Throwable ignored) {}
		}
		return false;
	}

	private static int clamp(int v, int min, int max) { return Math.max(min, Math.min(max, v)); }
	private static float clamp(float v, float min, float max) { return Math.max(min, Math.min(max, v)); }

	private static boolean sdEquals(Object current, int target) {
		if (current == null) return false;
		if (current instanceof Integer i) return i == target;
		try { return Integer.parseInt(current.toString()) == target; } catch (Throwable ignored) { return false; }
	}
}
