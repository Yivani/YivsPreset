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

package yiv.yivspresets.preset;

/**
 * Defines preset profiles and their configuration options.
 * 
 * This class contains the default preset profiles (Build, Explore, Combat)
 * and the enums for various graphics and performance settings that can be
 * configured in each preset.
 * 
 * @author Yivani
 * @version 1.0.2
 * @since 1.0.0
 */
public final class PresetProfiles {
    /** Graphics quality levels available in presets */
    public enum Graphics { FAST, FANCY, FABULOUS }
    
    /** Particle rendering levels available in presets */
    public enum Particles { MINIMAL, DECREASED, ALL }
    
    /** Cloud rendering modes available in presets */
    public enum Clouds { OFF, FAST, FANCY }

    /** Build preset: Optimized for building with lower render distance and minimal effects */
    public static final Profile BUILD = new Profile(
            8, 8,                                    // Render/Simulation distance: 8 chunks
            Graphics.FAST, Particles.MINIMAL, Clouds.OFF,  // Minimal graphics for performance
            false, false, false, 0.75f,             // No smooth lighting, VSync, or entity shadows
            2, 0, false,                            // Mipmap level 2, no biome blend, no view bobbing
            0f, 80                                  // No distortion effects, 80 FPS limit
    );

    /** Explore preset: Balanced for exploration with moderate settings */
    public static final Profile EXPLORE = new Profile(
            12, 12,                                 // Render/Simulation distance: 12 chunks
            Graphics.FAST, Particles.DECREASED, Clouds.FAST,  // Moderate graphics settings
            true, true, true, 1.0f,                // Smooth lighting, VSync, entity shadows enabled
            2, 2, true,                            // Mipmap level 2, biome blend 2, view bobbing
            0.0f, 120                              // No distortion effects, 120 FPS limit
    );

    /** Combat preset: Optimized for PvP with maximum performance and unlimited FPS */
    public static final Profile COMBAT = new Profile(
            8, 8,                                   // Render/Simulation distance: 8 chunks
            Graphics.FAST, Particles.MINIMAL, Clouds.OFF,  // Minimal graphics for maximum FPS
            false, false, false, 1.0f,             // No smooth lighting, VSync, or entity shadows
            2, 0, false,                           // Mipmap level 2, no biome blend, no view bobbing
            0.0f, 260                              // No distortion effects, unlimited FPS (260)
    );

    /**
     * Represents a complete preset configuration with all performance settings.
     * 
     * This class contains all the settings that can be configured in a preset,
     * including render distances, graphics options, and performance settings.
     * All fields are final to ensure immutability once created.
     * 
     * @author Yivani
     * @version 1.0.2
     * @since 1.0.0
     */
    public static final class Profile {
        /** Render distance in chunks (2-32) */
        public final int renderDistance;
        
        /** Simulation distance in chunks (5-32, must be <= renderDistance) */
        public final int simulationDistance;
        
        /** Graphics quality level */
        public final Graphics graphics;
        
        /** Particle rendering level */
        public final Particles particles;
        
        /** Cloud rendering mode */
        public final Clouds clouds;
        
        /** Whether smooth lighting (ambient occlusion) is enabled */
        public final boolean smoothLighting;
        
        /** Whether VSync is enabled */
        public final boolean vSync;
        
        /** Whether entity shadows are enabled */
        public final boolean entityShadows;
        
        /** Entity distance scaling factor (0.5-5.0) */
        public final float entityDistanceScaling;
        
        /** Mipmap levels for textures (0-4) */
        public final int mipmapLevels;
        
        /** Biome blend radius for smooth transitions (0-7) */
        public final int biomeBlendRadius;
        
        /** Whether view bobbing is enabled */
        public final boolean viewBobbing;
        
        /** Distortion effects scale (0.0-1.0) */
        public final float distortionEffectsScale;
        
        /** Maximum FPS limit (5-260, where 260 = unlimited) */
        public final int maxFps;

        /**
         * Creates a new preset profile with the specified settings.
         * 
         * @param renderDistance Render distance in chunks (2-32)
         * @param simulationDistance Simulation distance in chunks (5-32)
         * @param graphics Graphics quality level
         * @param particles Particle rendering level
         * @param clouds Cloud rendering mode
         * @param smoothLighting Whether smooth lighting is enabled
         * @param vSync Whether VSync is enabled
         * @param entityShadows Whether entity shadows are enabled
         * @param entityDistanceScaling Entity distance scaling factor (0.5-5.0)
         * @param mipmapLevels Mipmap levels for textures (0-4)
         * @param biomeBlendRadius Biome blend radius for smooth transitions (0-7)
         * @param viewBobbing Whether view bobbing is enabled
         * @param distortionEffectsScale Distortion effects scale (0.0-1.0)
         * @param maxFps Maximum FPS limit (5-260, where 260 = unlimited)
         */
        public Profile(int renderDistance, int simulationDistance,
                       Graphics graphics, Particles particles, Clouds clouds,
                       boolean smoothLighting, boolean vSync, boolean entityShadows, float entityDistanceScaling,
                       int mipmapLevels, int biomeBlendRadius, boolean viewBobbing,
                       float distortionEffectsScale, int maxFps) {
            this.renderDistance = renderDistance;
                        this.simulationDistance = simulationDistance;
            this.graphics = graphics;
            this.particles = particles;
            this.clouds = clouds;
            this.smoothLighting = smoothLighting;
                        this.vSync = vSync;
            this.entityShadows = entityShadows;
            this.entityDistanceScaling = entityDistanceScaling;
            this.mipmapLevels = mipmapLevels;
            this.biomeBlendRadius = biomeBlendRadius;
            this.viewBobbing = viewBobbing;
            this.distortionEffectsScale = distortionEffectsScale;
            this.maxFps = maxFps;
        }
    }
}
