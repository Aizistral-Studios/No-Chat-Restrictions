package com.aizistral.nochatrestrictions.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.aizistral.nochatrestrictions.core.NCRCore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.minecraft.client.Minecraft;

public record NCRConfig(boolean allowTelemetry, boolean allowProfanityFilter) {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static @Nullable NCRConfig instance = null;

    private static NCRConfig getDefault() {
	return new NCRConfig(false, false);
    }

    public static NCRConfig getInstance() {
	if (instance == null) {
	    instance = loadConfig("NoChatRestrictions.json");
	}

	return instance;
    }

    private static NCRConfig loadConfig(String fileName) {
	NCRCore.LOGGER.debug("Reading config file {}...", fileName);
	NCRConfig config = readFile(fileName).orElseGet(NCRConfig::getDefault);

	NCRCore.LOGGER.info("Telemetry Allowed: {}", config.allowTelemetry());
	NCRCore.LOGGER.info("Profanity Filter Allowed: {}", config.allowProfanityFilter());

	NCRCore.LOGGER.debug("Writing config file {} back to disk...");
	writeFile(fileName, config);

	return config;
    }

    private static Optional<NCRConfig> readFile(String fileName) {
	Path file = Minecraft.getInstance().gameDirectory.toPath().resolve("config").resolve(fileName);

	if (!Files.isRegularFile(file))
	    return Optional.empty();

	try (BufferedReader reader = Files.newBufferedReader(file)) {
	    return Optional.of(GSON.fromJson(reader, NCRConfig.class));
	} catch (Exception ex) {
	    NCRCore.LOGGER.fatal("Could not read config file: {}", file);
	    NCRCore.LOGGER.fatal("This likely indicates the file is corrupted. "
		    + "You can try deleting it to fix this problem. Full stacktrace below:");
	    NCRCore.LOGGER.catching(ex);
	    throw new RuntimeException("Could not read config file: " + file, ex);
	}
    }

    private static void writeFile(String fileName, NCRConfig config) {
	Path file = Minecraft.getInstance().gameDirectory.toPath().resolve("config").resolve(fileName);

	try {
	    Files.createDirectories(file.getParent());
	    try (BufferedWriter writer = Files.newBufferedWriter(file)) {
		GSON.toJson(config, writer);
	    }
	} catch (Exception ex) {
	    NCRCore.LOGGER.fatal("Could not write config file: {}", file);
	    NCRCore.LOGGER.fatal("Full stacktrace below:");
	    NCRCore.LOGGER.catching(ex);
	    throw new RuntimeException("Could not write config file: " + file,ex);
	}
    }

}
