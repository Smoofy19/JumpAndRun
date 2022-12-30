package de.smoofy.jumpandrun.main;

import de.smoofy.jumpandrun.JumpAndRunCommand;
import de.smoofy.jumpandrun.JumpAndRunListener;
import de.smoofy.jumpandrun.JumpAndRunManager;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author - Smoofy
 * @GitHub - https://github.com/Smoofy19
 * @Twitter - https://twitter.com/Smoofy19
 * Erstellt - 30.12.2022 21:19
 */
@Getter
public final class JAR extends JavaPlugin {

    @Getter
    private static JAR instance;

    private JumpAndRunManager jumpAndRunManager;

    @Override
    public void onEnable() {
        instance = this;

        this.jumpAndRunManager = new JumpAndRunManager();

        registerListener();
        registerCommands();
    }

    @Override
    public void onDisable() {

    }

    private void registerListener() {
        new JumpAndRunListener();
    }

    private void registerCommands() {
        new JumpAndRunCommand();
    }

    public static String getPrefix() {
        return "§8[§2JAR§8] ";
    }
}
