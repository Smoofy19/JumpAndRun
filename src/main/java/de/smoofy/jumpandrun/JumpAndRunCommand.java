package de.smoofy.jumpandrun;

import de.smoofy.jumpandrun.main.JAR;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author - Smoofy
 * @GitHub - https://github.com/Smoofy19
 * @Twitter - https://twitter.com/Smoofy19
 * Erstellt - 30.12.2022 22:13
 */
public class JumpAndRunCommand implements CommandExecutor {

    public JumpAndRunCommand() {
        Objects.requireNonNull(Bukkit.getPluginCommand("jumpandrun")).setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, String[] args) {
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage(JAR.getPrefix().append(Component.text("Du musst ein Spieler sein.", NamedTextColor.RED)));
            return false;
        }
        if (!player.hasPermission("jar.setup")) {
            player.sendMessage(JAR.getPrefix().append(Component.text("Dazu hast du keine Rechte.", NamedTextColor.RED)));
            return false;
        }
        if (args.length != 1 && args.length != 2) {
            this.sendUsage(player);
            return false;
        }
        JumpAndRunManager jumpAndRunManager = JAR.getInstance().getJumpAndRunManager();
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("setup")) {
                if (jumpAndRunManager.getSetup().containsKey(player)) {
                    player.sendMessage(JAR.getPrefix()
                            .append(Component.text("Du befindest dich bereits im ", NamedTextColor.GRAY))
                            .append(Component.text("JumpAndRun ", NamedTextColor.DARK_GREEN, TextDecoration.BOLD))
                            .append(Component.text("Setup.", NamedTextColor.GRAY)));

                    switch (jumpAndRunManager.getSetup().get(player)) {
                        case NAME -> player.sendMessage(JAR.getPrefix()
                                .append(Component.text("Tippe den Namen des ", NamedTextColor.GRAY)
                                        .append(Component.text("JumpAndRun's ", NamedTextColor.DARK_GREEN, TextDecoration.BOLD))
                                        .append(Component.text("in den Chat.", NamedTextColor.GRAY))));

                        case BUILDER -> player.sendMessage(JAR.getPrefix()
                                .append(Component.text("Tippe den Namen vom Builder des ", NamedTextColor.GRAY)
                                        .append(Component.text("JumpAndRun's ", NamedTextColor.DARK_GREEN, TextDecoration.BOLD))
                                        .append(Component.text("in den Chat.", NamedTextColor.GRAY))));

                        case DIFFICULTY -> {
                            player.sendMessage(JAR.getPrefix()
                                    .append(Component.text("Tippe die Difficulty-ID des ", NamedTextColor.GRAY))
                                    .append(Component.text("JumpAndRun's ", NamedTextColor.DARK_GREEN, TextDecoration.BOLD))
                                    .append(Component.text("in den Chat.", NamedTextColor.GRAY)));

                            for (JumpAndRun.Difficulty difficulty : JumpAndRun.Difficulty.values()) {
                                player.sendMessage(Component.text(" » ", NamedTextColor.DARK_GRAY)
                                        .append(Component.text(difficulty.name(), difficulty.getColor()))
                                        .append(Component.text("(", NamedTextColor.DARK_GRAY))
                                        .append(Component.text(difficulty.getId(), difficulty.getColor()))
                                        .append(Component.text(")", NamedTextColor.DARK_GRAY)));
                            }
                        }

                        case START_LOCATION -> player.sendMessage(JAR.getPrefix()
                                .append(Component.text("Stelle dich auf die Startlocation und tippe ", NamedTextColor.GRAY))
                                .append(Component.text("'", NamedTextColor.DARK_GRAY))
                                .append(Component.text("set", NamedTextColor.DARK_GREEN, TextDecoration.BOLD))
                                .append(Component.text("' ", NamedTextColor.DARK_GREEN))
                                .append(Component.text("in den Chat.", NamedTextColor.GRAY)));

                        case END_LOCATION -> player.sendMessage(JAR.getPrefix()
                                .append(Component.text("Stelle dich auf die Endlocation und tippe ", NamedTextColor.GRAY))
                                .append(Component.text("'", NamedTextColor.DARK_GRAY))
                                .append(Component.text("set", NamedTextColor.DARK_GREEN, TextDecoration.BOLD))
                                .append(Component.text("' ", NamedTextColor.DARK_GREEN))
                                .append(Component.text("in den Chat.", NamedTextColor.GRAY)));

                        case CHECKPOINTS -> {
                            player.sendMessage(JAR.getPrefix()
                                    .append(Component.text("Stelle dich auf einen Checkpoint und tippe ", NamedTextColor.GRAY))
                                    .append(Component.text("'", NamedTextColor.DARK_GRAY))
                                    .append(Component.text("set", NamedTextColor.DARK_GREEN, TextDecoration.BOLD))
                                    .append(Component.text("' ", NamedTextColor.DARK_GREEN))
                                    .append(Component.text("in den Chat.", NamedTextColor.GRAY)));

                            player.sendMessage(JAR.getPrefix()
                                    .append(Component.text("Um das ", NamedTextColor.GRAY))
                                    .append(Component.text("JumpAndRun ", NamedTextColor.DARK_GREEN, TextDecoration.BOLD))
                                    .append(Component.text("zu erstellen ", NamedTextColor.GRAY))
                                    .append(Component.text("» ", NamedTextColor.DARK_GRAY))
                                    .append(Component.text("/jar create", NamedTextColor.RED)));
                        }
                    }
                    return false;
                }
                jumpAndRunManager.getSetup().put(player, SetupStep.NAME);

                player.sendMessage(JAR.getPrefix()
                        .append(Component.text("Tippe den Namen des ", NamedTextColor.GRAY))
                        .append(Component.text("JumpAndRun's ", NamedTextColor.DARK_GREEN, TextDecoration.BOLD))
                        .append(Component.text("in den Chat.", NamedTextColor.GRAY)));

                player.sendMessage(JAR.getPrefix()
                        .append(Component.text("Du kannst jederzeit das Setup des ", NamedTextColor.GRAY))
                        .append(Component.text("JumpAndRun's ", NamedTextColor.DARK_GREEN, TextDecoration.BOLD))
                        .append(Component.text("mit ", NamedTextColor.GRAY))
                        .append(Component.text("'", NamedTextColor.DARK_GRAY))
                        .append(Component.text("cancel", NamedTextColor.DARK_GREEN, TextDecoration.BOLD))
                        .append(Component.text("' ", NamedTextColor.DARK_GRAY))
                        .append(Component.text("abbrechen.", NamedTextColor.GRAY)));

            } else if (args[0].equalsIgnoreCase("list")) {
                if (jumpAndRunManager.getJumpAndRuns().isEmpty()) {
                    player.sendMessage(JAR.getPrefix()
                            .append(Component.text("Es existieren keine ", NamedTextColor.GRAY))
                            .append(Component.text("JumpAndRun's", NamedTextColor.DARK_GREEN, TextDecoration.BOLD))
                            .append(Component.text(".", NamedTextColor.GRAY)));

                    return false;
                }
                player.sendMessage(JAR.getPrefix()
                        .append(Component.text("JumpAndRun's", NamedTextColor.GRAY))
                        .append(Component.text(":", NamedTextColor.DARK_GRAY)));

                for (String name : jumpAndRunManager.getJumpAndRuns().keySet()) {
                    JumpAndRun jumpAndRun = jumpAndRunManager.getJumpAndRun(name);
                    player.sendMessage(Component.text(" » ", NamedTextColor.DARK_GRAY)
                            .append(Component.text(jumpAndRun.getName(), NamedTextColor.DARK_GREEN, TextDecoration.BOLD))
                            .append(Component.text(" von ", NamedTextColor.GRAY))
                            .append(Component.text(jumpAndRun.getBuilder(), NamedTextColor.DARK_GREEN))
                            .append(Component.text(" ║ ", NamedTextColor.DARK_GRAY))
                            .append(Component.text(jumpAndRun.getDifficulty().name(), jumpAndRun.getDifficulty().getColor())));
                }
            } else if (args[0].equalsIgnoreCase("create")) {
                if (jumpAndRunManager.getEndLocation() == null) {
                    player.sendMessage(JAR.getPrefix()
                            .append(Component.text("Du hast das ", NamedTextColor.GRAY))
                            .append(Component.text("JumpAndRun ", NamedTextColor.RED, TextDecoration.BOLD))
                            .append(Component.text("noch nicht vollständig eingerichtet!", NamedTextColor.GRAY)));

                    return false;
                }
                jumpAndRunManager.addJumpAndRun(jumpAndRunManager.getName(), jumpAndRunManager.getBuilder(),
                        jumpAndRunManager.getDifficulty(), jumpAndRunManager.getStartLocation(),
                        jumpAndRunManager.getEndLocation(), jumpAndRunManager.getCheckpoints());

                player.sendMessage(JAR.getPrefix()
                        .append(Component.text("Du hast das ", NamedTextColor.GRAY))
                        .append(Component.text("JumpAndRun ", NamedTextColor.DARK_GREEN))
                        .append(Component.text(jumpAndRunManager.getName(), NamedTextColor.DARK_GREEN, TextDecoration.BOLD))
                        .append(Component.text(" von ", NamedTextColor.GRAY))
                        .append(Component.text(jumpAndRunManager.getBuilder(), NamedTextColor.DARK_GREEN))
                        .append(Component.text(" erstellt.", NamedTextColor.GRAY)));

                jumpAndRunManager.getSetup().remove(player);
                jumpAndRunManager.setName(null);
                jumpAndRunManager.setBuilder(null);
                jumpAndRunManager.setDifficulty(null);
                jumpAndRunManager.setStartLocation(null);
                jumpAndRunManager.setEndLocation(null);
                jumpAndRunManager.setCheckpoints(null);
            } else {
                this.sendUsage(player);
            }
        } else {
            if (args[0].equalsIgnoreCase("delete")) {
                if (jumpAndRunManager.getJumpAndRun(args[1]) == null) {
                    player.sendMessage(JAR.getPrefix()
                            .append(Component.text("Das ", NamedTextColor.GRAY))
                            .append(Component.text("JumpAndRun ", NamedTextColor.RED, TextDecoration.BOLD))
                            .append(Component.text(args[1], NamedTextColor.RED, TextDecoration.BOLD))
                            .append(Component.text(" existiert nicht.", NamedTextColor.GRAY)));

                    return false;
                }
                jumpAndRunManager.deleteJumpAndRun(args[1]);

                player.sendMessage(JAR.getPrefix()
                        .append(Component.text("Du hast das ", NamedTextColor.GRAY))
                        .append(Component.text("JumpAndRun ", NamedTextColor.DARK_GREEN))
                        .append(Component.text(args[1], NamedTextColor.DARK_GREEN, TextDecoration.BOLD))
                        .append(Component.text(" gelöscht.", NamedTextColor.GRAY)));

            } else {
                this.sendUsage(player);
            }
        }
        return true;
    }

    private void sendUsage(Player player) {
        player.sendMessage(JAR.getPrefix()
                .append(Component.text("Verwende", NamedTextColor.GRAY))
                .append(Component.text(": ", NamedTextColor.DARK_GRAY))
                .append(Component.text("/jar [setup,list,create,delete] <Name>", NamedTextColor.RED)));
    }
}