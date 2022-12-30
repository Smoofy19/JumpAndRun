package de.smoofy.jumpandrun;

import de.smoofy.jumpandrun.main.JAR;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author - Smoofy
 * @GitHub - https://github.com/Smoofy19
 * @Twitter - https://twitter.com/Smoofy19
 * Erstellt - 30.12.2022 22:13
 */
public class JumpAndRunCommand implements CommandExecutor {

    public JumpAndRunCommand() {
        Bukkit.getPluginCommand("jumpandrun").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(Component.text(JAR.getPrefix() + "§cDu musst ein Spieler sein."));
            return false;
        }
        Player player = (Player) commandSender;
        if (!player.hasPermission("jar.setup")) {
            player.sendMessage(Component.text(JAR.getPrefix() + "§cDazu hast du keine Rechte."));
            return false;
        }
        if (args.length != 1 && args.length != 2) {
            player.sendMessage(Component.text(JAR.getPrefix() + "§7Verwende§8: §c/jar [setup,list,create,delete] <Name>"));
            return false;
        }
        JumpAndRunManager jumpAndRunManager = JAR.getInstance().getJumpAndRunManager();
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("setup")) {
                if (jumpAndRunManager.getSetup().containsKey(player)) {
                    player.sendMessage(Component.text(JAR.getPrefix() + "§7Du befindest dich bereits im §2§lJumpAndRun §7Setup§8."));
                    switch (jumpAndRunManager.getSetup().get(player)) {
                        case NAME:
                            player.sendMessage(Component.text(JAR.getPrefix() + "§7Tippe den Namen des §2§lJumpAndRun's §7in den Chat§8."));
                            break;
                        case BUILDER:
                            player.sendMessage(Component.text(JAR.getPrefix() + "§7Tippe den Namen vom Builder des §2§lJumpAndRun's §7in den Chat§8."));
                            break;
                        case DIFFICULTY:
                            player.sendMessage(Component.text(JAR.getPrefix() + "§7Tippe die Difficulty-ID des §2§lJumpAndRun's §7in den Chat§8."));
                            for (JumpAndRun.Difficulty difficulty : JumpAndRun.Difficulty.values())
                                player.sendMessage(Component.text(" §8» " + difficulty.getColor() + difficulty.name() + "§8(" +
                                        difficulty.getColor() + difficulty.getId() + "§8)"));
                            break;
                        case STARTLOCATION:
                            player.sendMessage(Component.text(JAR.getPrefix() + "§7Stelle dich auf die Startlocation und tippe §8'§2§lset§8' §7in den Chat§8."));
                            break;
                        case ENDLOCATION:
                            player.sendMessage(Component.text(JAR.getPrefix() + "§7Stelle dich auf die Endlocation und tippe §8'§2§lset§8' §7in den Chat§8."));
                            break;
                        case CHECKPOINTS:
                            player.sendMessage(Component.text(JAR.getPrefix() + "§7Stelle dich auf einen Checkpoints und tippe §8'§2§lset§8' §7in den Chat§8."));
                            player.sendMessage(Component.text(JAR.getPrefix() + "§7Um das §2§lJumpAndRun §7zu erstellen §8» §c/jar create"));
                            break;
                    }
                    return false;
                }
                jumpAndRunManager.getSetup().put(player, SetupStep.NAME);
                player.sendMessage(Component.text(JAR.getPrefix() + "§7Tippe den Namen des §2§lJumpAndRun's §7in den Chat§8."));
                player.sendMessage(Component.text(JAR.getPrefix() + "§7Du kannst jederzeit das Setup des §2§lJumpAndRun's §7mit §8'§2§lcancel§8' §7abbrechen§8."));
            } else if (args[0].equalsIgnoreCase("list")) {
                if (jumpAndRunManager.getJumpAndRuns().size() == 0) {
                    player.sendMessage(Component.text(JAR.getPrefix() + "§7Es existieren keine §2§lJumpAndRun's§8."));
                    return false;
                }
                player.sendMessage(Component.text(JAR.getPrefix() + "§7JumpAndRun's§8:"));
                for (String name : jumpAndRunManager.getJumpAndRuns().keySet()) {
                    JumpAndRun jumpAndRun = jumpAndRunManager.getJumpAndRun(name);
                    player.sendMessage(Component.text(" §8» §2§l" + jumpAndRun.getName() + "§7 von §2" + jumpAndRun.getBuilder() + " §8║ " + jumpAndRun.getDifficulty().getColor() + jumpAndRun.getDifficulty().name()));
                }
            } else if (args[0].equalsIgnoreCase("create")) {
                if (jumpAndRunManager.getEndLocation() == null) {
                    player.sendMessage(Component.text(JAR.getPrefix() + "§7Du hast das §c§lJumpAndRun §7noch nicht vollständig eingerichtet§8!"));
                    return false;
                }
                jumpAndRunManager.addJumpAndRun(jumpAndRunManager.getName(), jumpAndRunManager.getBuilder(), jumpAndRunManager.getDifficulty(), jumpAndRunManager.getStartLocation(), jumpAndRunManager.getEndLocation(), jumpAndRunManager.getCheckpoints());
                player.sendMessage(Component.text(JAR.getPrefix() + "§7Du hast das §2JumpAndRun §l" + jumpAndRunManager.getName() + "§7 von §2" + jumpAndRunManager.getBuilder() + "§7 erstellt§8."));
                jumpAndRunManager.getSetup().remove(player);
                jumpAndRunManager.setName(null);
                jumpAndRunManager.setBuilder(null);
                jumpAndRunManager.setDifficulty(null);
                jumpAndRunManager.setStartLocation(null);
                jumpAndRunManager.setEndLocation(null);
                jumpAndRunManager.setCheckpoints(null);
            } else {
                player.sendMessage(Component.text(JAR.getPrefix() + "§7Verwende§8: §c/jar [setup,list,create]"));
            }
        } else {
            if (args[0].equalsIgnoreCase("delete")) {
                if (jumpAndRunManager.getJumpAndRun(args[1]) == null) {
                    player.sendMessage(Component.text(JAR.getPrefix() + "§7Das §cJumpAndRun §2§l" + args[1] + "§7 existiert nicht§8."));
                    return false;
                }
                jumpAndRunManager.deleteJumpAndRun(args[1]);
                player.sendMessage(Component.text(JAR.getPrefix() + "§7Du hast das §2JumpAndRun §l" + args[1] + "§7 gelöscht§8."));
            } else {
                player.sendMessage(Component.text(JAR.getPrefix() + "§7Verwende§8: §c/jar [delete] [Name]"));
            }
        }
        return true;
    }
}