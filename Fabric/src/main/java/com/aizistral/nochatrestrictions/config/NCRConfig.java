package com.aizistral.nochatrestrictions.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.aizistral.nochatrestrictions.core.NCRCore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.fabricmc.loader.api.FabricLoader;

public final class NCRConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static @Nullable NCRConfig instance = null;

    private final boolean enableMod;
    private final boolean allowTelemetry;
    private final boolean allowProfanityFilter;

    private NCRConfig(boolean enableMod, boolean allowTelemetry, boolean allowProfanityFilter) {
	this.enableMod = enableMod;
	this.allowTelemetry = allowTelemetry;
	this.allowProfanityFilter = allowProfanityFilter;
    }

    public boolean enableMod() {
	return this.enableMod;
    }

    public boolean allowTelemetry() {
	return this.allowTelemetry;
    }

    public boolean allowProfanityFilter() {
	return this.allowProfanityFilter;
    }

    private static NCRConfig getDefault() {
	return new NCRConfig(true, false, false);
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

	NCRCore.LOGGER.info("Mod Enabled: {}", config.enableMod());
	NCRCore.LOGGER.info("Telemetry Allowed: {}", config.allowTelemetry());
	NCRCore.LOGGER.info("Profanity Filter Allowed: {}", config.allowProfanityFilter());

	NCRCore.LOGGER.debug("Writing config file {} back to disk...", fileName);
	writeFile(fileName, config);

	return config;
    }

    private static Path getConfigFile(String fileName) {
	return FabricLoader.getInstance().getConfigDir().resolve(fileName);
    }

    private static Optional<NCRConfig> readFile(String fileName) {
	Path file = getConfigFile(fileName);

	if (!Files.isRegularFile(file))
	    return Optional.empty();

	try (BufferedReader reader = Files.newBufferedReader(file)) {
	    JsonObject json = GSON.toJsonTree(getDefault()).getAsJsonObject();

	    for (Map.Entry<String, JsonElement> entry : JsonParser.parseReader(reader).getAsJsonObject().entrySet()) {
		json.add(entry.getKey(), entry.getValue());
	    }

	    return Optional.of(GSON.fromJson(json, NCRConfig.class));
	} catch (Exception ex) {
	    NCRCore.LOGGER.fatal("Could not read config file: {}", file);
	    NCRCore.LOGGER.fatal("This likely indicates the file is corrupted. "
		    + "You can try deleting it to fix this problem. Full stacktrace below:");
	    NCRCore.LOGGER.catching(ex);
	    throw new RuntimeException("Could not read config file: " + file, ex);
	}
    }

    private static void writeFile(String fileName, NCRConfig config) {
	Path file = getConfigFile(fileName);

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
