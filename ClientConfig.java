package com.voidclient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;

/** Saved to .minecraft/config/voidclient.json */
public class ClientConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger("voidclient");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static final int FOV_MIN = 30, FOV_MAX = 170;
    public static final int ZOOM_MIN = 2, ZOOM_MAX = 10;

    // FOV changer
    public boolean fovEnabled = false;
    public int fov = 90;
    public int zoomFactor = 4;
    public boolean ignoreFovEffects = false;

    // Fullbright
    public boolean fullbrightEnabled = false;
    public int fullbrightPercent = 100;

    // Blur (behind menus)
    public boolean blurEnabled = false;
    public int blurPercent = 50;

    private static Path path() {
        return FabricLoader.getInstance().getConfigDir().resolve("voidclient.json");
    }

    public static ClientConfig load() {
        Path p = path();
        if (Files.exists(p)) {
            try {
                ClientConfig c = GSON.fromJson(Files.readString(p), ClientConfig.class);
                if (c != null) {
                    c.clamp();
                    return c;
                }
            } catch (Exception e) {
                LOGGER.warn("Could not read config, using defaults", e);
            }
        }
        ClientConfig c = new ClientConfig();
        c.save();
        return c;
    }

    public void save() {
        try {
            clamp();
            Files.writeString(path(), GSON.toJson(this));
        } catch (Exception e) {
            LOGGER.warn("Could not save config", e);
        }
    }

    private static int clamp(int v, int lo, int hi) {
        return Math.max(lo, Math.min(hi, v));
    }

    private void clamp() {
        fov = clamp(fov, FOV_MIN, FOV_MAX);
        zoomFactor = clamp(zoomFactor, ZOOM_MIN, ZOOM_MAX);
        fullbrightPercent = clamp(fullbrightPercent, 1, 100);
        blurPercent = clamp(blurPercent, 1, 100);
    }
}
