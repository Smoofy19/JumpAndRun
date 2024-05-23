package de.smoofy.jumpandrun;

import de.smoofy.jumpandrun.main.JAR;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;

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
                        player.getLocation().getBlockZ() == jumpAndRunManager.getCheckpoint(player).getBlockZ())
                    return;
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
                player.sendMessage(JAR.getPrefix()
                        .append(Component.text("Du hast das Setup für das ", NamedTextColor.GRAY))
                        .append(Component.text("JumpAndRun ", NamedTextColor.DARK_GRAY, TextDecoration.BOLD))
                        .append(Component.text("abgebrochen.", NamedTextColor.GRAY)));

                return;
            }
            switch (jumpAndRunManager.getSetup().get(player)) {
                case NAME -> {
                    jumpAndRunManager.setName(message);

                    player.sendMessage(JAR.getPrefix()
                            .append(Component.text("Du hast den Namen des ", NamedTextColor.GRAY))
                            .append(Component.text("JumpAndRun's ", NamedTextColor.DARK_GREEN, TextDecoration.BOLD))
                            .append(Component.text("gesetzt.", NamedTextColor.GRAY)));

                    player.sendMessage(JAR.getPrefix()
                            .append(Component.text("Tippe den Namen vom Builder des ", NamedTextColor.GRAY))
                            .append(Component.text("JumpAndRun's ", NamedTextColor.DARK_GREEN, TextDecoration.BOLD))
                            .append(Component.text("in den Chat.", NamedTextColor.GRAY)));

                    jumpAndRunManager.getSetup().put(player, SetupStep.BUILDER);
                }
                case BUILDER -> {
                    jumpAndRunManager.setBuilder(message);

                    player.sendMessage(JAR.getPrefix()
                            .append(Component.text("Du hast den Builder des ", NamedTextColor.GRAY))
                            .append(Component.text("JumpAndRun's ", NamedTextColor.DARK_GREEN, TextDecoration.BOLD))
                            .append(Component.text("gesetzt.", NamedTextColor.GRAY)));

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

                    jumpAndRunManager.getSetup().put(player, SetupStep.DIFFICULTY);
                }
                case DIFFICULTY -> {
                    jumpAndRunManager.setDifficulty(JumpAndRun.Difficulty.getDifficultyById(Integer.parseInt(message)));

                    player.sendMessage(JAR.getPrefix()
                            .append(Component.text("Du hast die Difficulty des ", NamedTextColor.GRAY))
                            .append(Component.text("JumpAndRun's ", NamedTextColor.DARK_GREEN, TextDecoration.BOLD))
                            .append(Component.text("gesetzt.", NamedTextColor.GRAY)));

                    player.sendMessage(JAR.getPrefix()
                            .append(Component.text("Stelle dich auf die Startlocation und tippe ", NamedTextColor.GRAY))
                            .append(Component.text("'", NamedTextColor.DARK_GRAY))
                            .append(Component.text("set", NamedTextColor.DARK_GREEN, TextDecoration.BOLD))
                            .append(Component.text("' ", NamedTextColor.DARK_GRAY))
                            .append(Component.text("in den Chat.", NamedTextColor.GRAY)));

                    jumpAndRunManager.getSetup().put(player, SetupStep.START_LOCATION);
                }
                case START_LOCATION -> {
                    if (!player.getLocation().getBlock().getType().equals(Material.OAK_PRESSURE_PLATE)) {
                        player.sendMessage(JAR.getPrefix()
                                .append(Component.text("Du musst auf einer ", NamedTextColor.RED))
                                .append(Component.text(Material.OAK_PRESSURE_PLATE.name(), NamedTextColor.DARK_RED))
                                .append(Component.text(" stehen.", NamedTextColor.RED)));

                        return;
                    }
                    jumpAndRunManager.setStartLocation(player.getLocation());

                    player.sendMessage(JAR.getPrefix()
                            .append(Component.text("Du hast die Startlocation des ", NamedTextColor.GRAY))
                            .append(Component.text("JumpAndRun's ", NamedTextColor.DARK_GREEN, TextDecoration.BOLD))
                            .append(Component.text("gesetzt.", NamedTextColor.GRAY)));

                    player.sendMessage(JAR.getPrefix()
                            .append(Component.text("Stelle dich auf die Endlocation und tippe ", NamedTextColor.GRAY))
                            .append(Component.text("'", NamedTextColor.DARK_GRAY))
                            .append(Component.text("set", NamedTextColor.DARK_GREEN, TextDecoration.BOLD))
                            .append(Component.text("' ", NamedTextColor.DARK_GRAY))
                            .append(Component.text("in den Chat.", NamedTextColor.GRAY)));

                    jumpAndRunManager.getSetup().put(player, SetupStep.END_LOCATION);
                }
                case END_LOCATION -> {
                    if (!player.getLocation().getBlock().getType().equals(Material.HEAVY_WEIGHTED_PRESSURE_PLATE)) {
                        player.sendMessage(JAR.getPrefix()
                                .append(Component.text("Du musst auf einer ", NamedTextColor.RED))
                                .append(Component.text(Material.HEAVY_WEIGHTED_PRESSURE_PLATE.name(), NamedTextColor.DARK_RED))
                                .append(Component.text(" stehen.", NamedTextColor.RED)));

                        return;
                    }
                    jumpAndRunManager.setEndLocation(player.getLocation());

                    player.sendMessage(JAR.getPrefix()
                            .append(Component.text("Du hast die Endlocation des ", NamedTextColor.GRAY))
                            .append(Component.text("JumpAndRun's ", NamedTextColor.DARK_GREEN, TextDecoration.BOLD))
                            .append(Component.text("gesetzt.", NamedTextColor.GRAY)));

                    player.sendMessage(JAR.getPrefix()
                            .append(Component.text("Stelle dich auf einen Checkpoint und tippe ", NamedTextColor.GRAY))
                            .append(Component.text("'", NamedTextColor.DARK_GRAY))
                            .append(Component.text("set", NamedTextColor.DARK_GREEN, TextDecoration.BOLD))
                            .append(Component.text("' ", NamedTextColor.DARK_GRAY))
                            .append(Component.text("in den Chat.", NamedTextColor.GRAY)));

                    player.sendMessage(JAR.getPrefix()
                            .append(Component.text("Um das ", NamedTextColor.GRAY))
                            .append(Component.text("JumpAndRun ", NamedTextColor.DARK_GREEN, TextDecoration.BOLD))
                            .append(Component.text("zu erstellen ", NamedTextColor.GRAY))
                            .append(Component.text("» ", NamedTextColor.DARK_GRAY))
                            .append(Component.text("/jar create", NamedTextColor.RED)));

                    jumpAndRunManager.getSetup().put(player, SetupStep.CHECKPOINTS);
                }
                case CHECKPOINTS -> {
                    if (!player.getLocation().getBlock().getType().equals(Material.LIGHT_WEIGHTED_PRESSURE_PLATE)) {
                        player.sendMessage(JAR.getPrefix()
                                .append(Component.text("Du musst auf einer ", NamedTextColor.RED))
                                .append(Component.text(Material.LIGHT_WEIGHTED_PRESSURE_PLATE.name(), NamedTextColor.DARK_RED))
                                .append(Component.text(" stehen.", NamedTextColor.RED)));

                        return;
                    }
                    jumpAndRunManager.addCheckpoint(player.getLocation());

                    player.sendMessage(JAR.getPrefix()
                            .append(Component.text("Du hast den Checkpoint des ", NamedTextColor.GRAY))
                            .append(Component.text("JumpAndRun's ", NamedTextColor.DARK_GREEN, TextDecoration.BOLD))
                            .append(Component.text("gesetzt.", NamedTextColor.GRAY)));

                    player.sendMessage(JAR.getPrefix()
                            .append(Component.text("Stelle dich auf einen weiteren Checkpoint und tippe ", NamedTextColor.GRAY))
                            .append(Component.text("'", NamedTextColor.DARK_GRAY))
                            .append(Component.text("set", NamedTextColor.DARK_GREEN, TextDecoration.BOLD))
                            .append(Component.text("' ", NamedTextColor.DARK_GRAY))
                            .append(Component.text("in den Chat.", NamedTextColor.GRAY)));

                    player.sendMessage(JAR.getPrefix()
                            .append(Component.text("Um das ", NamedTextColor.GRAY))
                            .append(Component.text("JumpAndRun ", NamedTextColor.DARK_GREEN, TextDecoration.BOLD))
                            .append(Component.text("zu erstellen ", NamedTextColor.GRAY))
                            .append(Component.text("» ", NamedTextColor.DARK_GRAY))
                            .append(Component.text("/jar create", NamedTextColor.RED)));
                }
            }
        }
    }

    @EventHandler
    public void onAct(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        JumpAndRunManager jumpAndRunManager = JAR.getInstance().getJumpAndRunManager();

        if (!jumpAndRunManager.isInJumpAndRun(player)) return;

        ItemStack item = event.getItem();
        if (item == null) return;
        if (!item.getType().equals(Material.ORANGE_DYE) && !event.getItem().getType().equals(Material.RED_DYE)) return;
        if (item.getItemMeta() == null) return;

        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        if (container.has(jumpAndRunManager.getCheckpointKey())) {
            jumpAndRunManager.addFail(player);
            player.teleport(jumpAndRunManager.getCheckpoint(player));
            return;
        }
        if (container.has(jumpAndRunManager.getAbortKey()))
            jumpAndRunManager.abortJumpAndRun(player);
    }
}