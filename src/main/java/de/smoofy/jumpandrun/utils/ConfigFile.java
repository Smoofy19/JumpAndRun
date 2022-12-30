package de.smoofy.jumpandrun.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.smoofy.jumpandrun.main.JAR;
import org.bukkit.Location;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;

/**
 * @author - Smoofy
 * @GitHub - https://github.com/Smoofy19
 * @Twitter - https://twitter.com/Smoofy19
 * Erstellt - 30.12.2022 21:44
 */
public class ConfigFile<T> {

    private final Path path;
    private final T defaultContent;
    private final Type contentType;
    private T content;

    public ConfigFile(String fileName, T content, T defaultContent, Type contentType) {
        this.content = content;
        this.defaultContent = defaultContent;
        this.contentType = contentType;
        this.path = Paths.get(fileName);
    }

    public ConfigFile(String fileName, Type type) {
        this(fileName, null, null, type);
    }

    public void load() {
        try {
            if (path.toFile().exists()) {
                Reader reader = Files.newBufferedReader(path);
                Gson gson = new GsonBuilder().registerTypeAdapter(Location.class, new GsonLocationAdapter()).create();
                this.content = gson.fromJson(reader, contentType);
            } else {
                store(defaultContent);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void store(T content) {
        if (!path.toFile().exists()) {
            try {
                if (!path.toFile().createNewFile())
                    JAR.getInstance().getLogger().log(Level.CONFIG, "Could not create config " + path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            Gson gson = new GsonBuilder().registerTypeAdapter(Location.class, new GsonLocationAdapter()).create();
            gson.toJson(content, this.contentType, writer);
            this.content = content;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public T getContent() {
        if (content == null) JAR.getInstance().getLogger().log(Level.CONFIG, "No Content founded!");
        return content;
    }
}
