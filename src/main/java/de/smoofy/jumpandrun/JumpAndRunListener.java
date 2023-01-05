package de.smoofy.jumpandrun;

import de.smoofy.jumpandrun.main.JAR;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * @author - Smoofy
 * @GitHub - https://github.com/Smoofy19
 * @Twitter - https://twitter.com/Smoofy19
 * Erstellt - 30.12.2022 22:13
 */
public class JumpAndRunListener implements Listener {

    public JumpAndRunListener() {
        Bukkit.getPluginManager().registerEvents(this, JAR.getInstance());
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        JumpAndRunManager jumpAndRunManager = JAR.getInstance().getJumpAndRunManager();
        if (jumpAndRunManager.isInJumpAndRun(player)) {
            Object[] data = jumpAndRunManager.getJumpAndRunData().get(player);
            JumpAndRun playerJumpAndRun = (JumpAndRun) data[0];
            if (player.getLocation().getBlock().getType().equals(Material.HEAVY_WEIGHTED_PRESSURE_PLATE)) {
                if (player.getLocation().getBlockX() == playerJumpAndRun.getEndLocation().getBlockX() &&
                        player.getLocation().getBlockY() == playerJumpAndRun.getEndLocation().getBlockY() &&
                        player.getLocation().getBlockZ() == playerJumpAndRun.getEndLocation().getBlockZ())
                    jumpAndRunManager.finishJumpAndRun(player);
            } else if (player.getLocation().getBlock().getType().equals(Material.LIGHT_WEIGHTED_PRESSURE_PLATE)) {
                if (player.getLocation().getBlockX() == jumpAndRunManager.getCheckpoint(player).getBlockX() &&
                        player.getLocation().getBlockY() == jumpAndRunManager.getCheckpoint(player).getBlockY() &&
                        player.getLocation().getBlockZ() == jumpAndRunManager.getCheckpoint(player).getBlockZ()) return;
                for (Location checkpoint : playerJumpAndRun.getCheckpoints()) {
                    if (player.getLocation().getBlockX() == checkpoint.getBlockX()
                            && player.getLocation().getBlockY() == checkpoint.getBlockY()
                            && player.getLocation().getBlockZ() == checkpoint.getBlockZ())
                        jumpAndRunManager.updateCheckpoint(player, checkpoint.clone());
                }
            }
            return;
        }
        if (player.getLocation().getBlock().getType().equals(Material.OAK_PRESSURE_PLATE)) {
            if (jumpAndRunManager.getJumpAndRun(player.getLocation()) != null)
                jumpAndRunManager.enterJumpAndRun(player, jumpAndRunManager.getJumpAndRun(player.getLocation()));
        }
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        JumpAndRunManager jumpAndRunManager = JAR.getInstance().getJumpAndRunManager();
        if (jumpAndRunManager.getSetup().containsKey(player)) {
            event.setCancelled(true);
            String message = PlainTextComponentSerializer.plainText().serialize(event.message()).split(" ")[0];
            if (event.message().toString().equalsIgnoreCase("cancel")) {
                jumpAndRunManager.getSetup().remove(player);
                player.sendMessage(Component.text(JAR.getPrefix() + "§7Du hast das Setup für das §2§lJumpAndRun §7abgebrochen§8."));
                return;
            }
            switch (jumpAndRunManager.getSetup().get(player)) {
                case NAME -> {
                    jumpAndRunManager.setName(message);
                    player.sendMessage(Component.text(JAR.getPrefix() + "§7Du hast den Namen des §2§lJumpAndRun's §7gesetzt§8."));
                    player.sendMessage(Component.text(JAR.getPrefix() + "§7Tippe den Namen vom Builder des §2§lJumpAndRun's §7in den Chat§8."));
                    jumpAndRunManager.getSetup().put(player, SetupStep.BUILDER);

                }
                case BUILDER -> {
                    jumpAndRunManager.setBuilder(message);
                    player.sendMessage(Component.text(JAR.getPrefix() + "§7Du hast den Builder des §2§lJumpAndRun's §7gesetzt§8."));
                    player.sendMessage(Component.text(JAR.getPrefix() + "§7Tippe die Difficulty-ID des §2§lJumpAndRun's §7in den Chat§8."));
                    for (JumpAndRun.Difficulty difficulty : JumpAndRun.Difficulty.values())
                        player.sendMessage(Component.text(" §8» " + difficulty.getColor() + difficulty.name() + "§8(" +
                                difficulty.getColor() + difficulty.getId() + "§8)"));
                    jumpAndRunManager.getSetup().put(player, SetupStep.DIFFICULTY);
                }
                case DIFFICULTY -> {
                    jumpAndRunManager.setDifficulty(JumpAndRun.Difficulty.getDifficultyById(Integer.parseInt(message)));
                    player.sendMessage(Component.text(JAR.getPrefix() + "§7Du hast die Difficulty des §2§lJumpAndRun's §7gesetzt§8."));
                    player.sendMessage(Component.text(JAR.getPrefix() + "§7Stelle dich auf die Startlocation und tippe §8'§2§lset§8' §7in den Chat§8."));
                    jumpAndRunManager.getSetup().put(player, SetupStep.STARTLOCATION);
                }
                case STARTLOCATION -> {
                    if (!player.getLocation().getBlock().getType().equals(Material.OAK_PRESSURE_PLATE)) {
                        player.sendMessage(Component.text(JAR.getPrefix() + "§cDu musst auf einer §4" + Material.OAK_PRESSURE_PLATE.name() + " §cstehen§8."));
                        return;
                    }
                    jumpAndRunManager.setStartLocation(player.getLocation());
                    player.sendMessage(Component.text(JAR.getPrefix() + "§7Du hast die Startlocation des §2§lJumpAndRun's §7gesetzt§8."));
                    player.sendMessage(Component.text(JAR.getPrefix() + "§7Stelle dich auf die Endlocation und tippe §8'§2§lset§8' §7in den Chat§8."));
                    jumpAndRunManager.getSetup().put(player, SetupStep.ENDLOCATION);
                }
                case ENDLOCATION -> {
                    if (!player.getLocation().getBlock().getType().equals(Material.HEAVY_WEIGHTED_PRESSURE_PLATE)) {
                        player.sendMessage(Component.text(JAR.getPrefix() + "§cDu musst auf einer §4" + Material.HEAVY_WEIGHTED_PRESSURE_PLATE.name() + " §cstehen§8."));
                        return;
                    }
                    jumpAndRunManager.setEndLocation(player.getLocation());
                    player.sendMessage(Component.text(JAR.getPrefix() + "§7Du hast die Endlocation des §2§lJumpAndRun's §7gesetzt§8."));
                    player.sendMessage(Component.text(JAR.getPrefix() + "§7Stelle dich auf einen Checkpoint und tippe §8'§2§lset§8' §7in den Chat§8."));
                    player.sendMessage(Component.text(JAR.getPrefix() + "§7Um das §2§lJumpAndRun §7zu erstellen §8» §c/jar create"));
                    jumpAndRunManager.getSetup().put(player, SetupStep.CHECKPOINTS);
                }
                case CHECKPOINTS -> {
                    if (!player.getLocation().getBlock().getType().equals(Material.LIGHT_WEIGHTED_PRESSURE_PLATE)) {
                        player.sendMessage(Component.text(JAR.getPrefix() + "§cDu musst auf einer §4" + Material.LIGHT_WEIGHTED_PRESSURE_PLATE.name() + " §cstehen§8."));
                        return;
                    }
                    jumpAndRunManager.addCheckpoint(player.getLocation());
                    player.sendMessage(Component.text(JAR.getPrefix() + "§7Du hast den Checkpoint des §2§lJumpAndRun's §7gesetzt§8."));
                    player.sendMessage(Component.text(JAR.getPrefix() + "§7Stelle dich auf einen weiteren Checkpoint und tippe §8'§2§lset§8' §7in den Chat§8."));
                    player.sendMessage(Component.text(JAR.getPrefix() + "§7Um das §2§lJumpAndRun §7zu erstellen §8» §c/jar create"));
                }
            }
        }
    }

    @EventHandler
    public void onAct(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        JumpAndRunManager jumpAndRunManager = JAR.getInstance().getJumpAndRunManager();
        if (event.getItem() == null) return;
        if (!event.getItem().getType().equals(Material.ORANGE_DYE) && !event.getItem().getType().equals(Material.RED_DYE))
            return;
        if (event.getItem().getItemMeta() == null) return;
        if (!event.getItem().getItemMeta().hasDisplayName()) return;
        if (event.getItem().getItemMeta().displayName() == null) return;
        String displayName = LegacyComponentSerializer.legacyAmpersand().serialize(event.getItem().getItemMeta().displayName());
        if (displayName.equals("§6Zurück zum Checkpoint")) {
            jumpAndRunManager.addFail(player);
            player.teleport(jumpAndRunManager.getCheckpoint(player));
            return;
        }
        if (displayName.equals("§cJumpAndRun abbrechen"))
            jumpAndRunManager.abortJumpAndRun(player);
    }
}