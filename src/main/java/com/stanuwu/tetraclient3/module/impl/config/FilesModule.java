package com.stanuwu.tetraclient3.module.impl.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.stanuwu.tetraclient3.config.ActionRowValue;
import com.stanuwu.tetraclient3.config.EnumValue;
import com.stanuwu.tetraclient3.events.EventSubscriber;
import com.stanuwu.tetraclient3.events.impl.ClientInitEvent;
import com.stanuwu.tetraclient3.module.AbstractModule;
import com.stanuwu.tetraclient3.module.ModuleCategory;
import com.stanuwu.tetraclient3.module.ModuleManager;
import joptsimple.internal.Strings;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class FilesModule extends AbstractModule {
    private enum ConfigFiles {
        DEFAULT,
        CONFIG1,
        CONFIG2,
        CONFIG3,
        CONFIG4,
        CONFIG5
    }

    EnumValue<ConfigFiles> selected = reg(new EnumValue<>("Profile", ConfigFiles.DEFAULT, ConfigFiles.class));

    ActionRowValue save = reg(new ActionRowValue("Save", this::save));
    ActionRowValue load = reg(new ActionRowValue("Load", this::load));

    private final ModuleManager moduleManager;

    public FilesModule(ModuleManager moduleManager) {
        this.moduleManager = moduleManager;
        super(ModuleCategory.CONFIG, "Files");
    }

    @EventSubscriber(event = ClientInitEvent.class)
    private void init(ClientInitEvent event) {
        this.load();
    }

    private String getCurrentFile() {
        String name = selected.getValue().name() + ".json";
        String configFolder = FabricLoader.getInstance().getConfigDir().toString() + "\\tetraclient3";
        boolean createdDir = new File(configFolder).mkdir();
        String fullPath = configFolder + "\\" + name;
        try {
            boolean createdFile = new File(fullPath).createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return fullPath;
    }

    private void load() {
        String path = getCurrentFile();
        try {
            JsonElement json = JsonParser.parseString(Strings.join(Files.readAllLines(Paths.get(path)), ""));
            if (!json.isJsonObject()) return;
            JsonObject object = json.getAsJsonObject();
            for (AbstractModule module : this.moduleManager.getModules()) {
                if (!object.has(module.getName())) continue;
                JsonObject moduleObject = object.getAsJsonObject(module.getName());
                if (!moduleObject.isJsonObject()) continue;
                Map<String, String> hashMap = new HashMap<>();
                for (String s : moduleObject.keySet()) {
                    hashMap.put(s, moduleObject.get(s).getAsString());
                }
                module.loadConfigMap(hashMap);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void save() {
        Map<String, Map<String, String>> data = new HashMap<>();
        for (AbstractModule module : this.moduleManager.getModules()) {
            data.put(module.getName(), module.getConfigMap());
        }
        JsonObject root = new JsonObject();
        for (String s : data.keySet()) {
            JsonObject inner = new JsonObject();
            for (String s2 : data.get(s).keySet()) {
                inner.addProperty(s2, data.get(s).get(s2));
            }
            root.add(s, inner);
        }
        try {
            Files.writeString(Paths.get(getCurrentFile()), root.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
