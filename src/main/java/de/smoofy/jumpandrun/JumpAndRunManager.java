package de.smoofy.jumpandrun;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import de.smoofy.jumpandrun.main.JAR;
import de.smoofy.jumpandrun.utils.ConfigFile;
import de.smoofy.jumpandrun.utils.Stringify;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.nio.file.Files;
import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * @author - Smoofy
 * @GitHub - https://github.com/Smoofy19
 * @Twitter - https://twitter.com/Smoofy19
 * Erstellt - 30.12.2022 21:53
 */
@Getter
@Setter
public class JumpAndRunManager {

    private String name = null;
    private String builder = null;
    private JumpAndRun.Difficulty difficulty = null;
    private Location startLocation;
    private Location endLocation;
    private List<Location> checkpoints = Lists.newArrayList();

    private final ConfigFile<Map<String, JumpAndRun>> configFile;

    private Map<String, JumpAndRun> jumpAndRuns;
    private final Map<Player, Object[]> jumpAndRunData;
    private final Map<Player, SetupStep> setup;

    private ItemStack backToCheckpointItem;
    private ItemStack abortItem;

    public JumpAndRunManager() {
        File directory = new File(Bukkit.getWorldContainer() + "/plugins/JumpAndRun/");
        if (Files.notExists(directory.toPath())) directory.mkdirs();

        this.configFile = new ConfigFile<>(directory.getPath() + "/JumpAndRuns.json", new TypeToken<Map<String, JumpAndRun>>() {
        }.getType());

        this.jumpAndRuns = Maps.newHashMap();
        this.jumpAndRunData = Maps.newHashMap();
        this.setup = Maps.newHashMap();

        loadJumpAndRuns();
    }

    public boolean isInJumpAndRun(Player player) {
        return jumpAndRunData.containsKey(player);
    }

    public void enterJumpAndRun(Player player, JumpAndRun jumpAndRun) {
        if (!isInJumpAndRun(player)) {
            jumpAndRunData.put(player, createJumpAndRunData(jumpAndRun));
            player.showTitle(Title.title(Component.text(jumpAndRun.getDifficulty().getColor() + "§l" + jumpAndRun.getName()),
                    Component.text("§agestartet"), Title.Times.times(Duration.ofSeconds(2), Duration.ofSeconds(5), Duration.ofSeconds(2))));
            player.sendMessage(Component.text(JAR.getPrefix() + "§7Du hast das §2JumpAndRun §l" + jumpAndRun.getName() +
                    "§7 von §2" + jumpAndRun.getBuilder() + " §7betreten§8."));
            player.sendMessage(Component.text(JAR.getPrefix() + "§7Difficulty§8: " + jumpAndRun.getDifficulty().getColor() +
                    jumpAndRun.getDifficulty().name() + " §8║ §7Checkpoints§8: §2" + jumpAndRun.getCheckpoints().size()));

            backToCheckpointItem = new ItemStack(Material.ORANGE_DYE);
            ItemMeta checkpointMeta = backToCheckpointItem.getItemMeta();
            assert checkpointMeta != null;
            checkpointMeta.displayName(Component.text("§6Zurück zum Checkpoint"));
            backToCheckpointItem.setItemMeta(checkpointMeta);
            abortItem = new ItemStack(Material.RED_DYE);
            ItemMeta abortMeta = abortItem.getItemMeta();
            assert abortMeta != null;
            abortMeta.displayName(Component.text("§cJumpAndRun abbrechen"));
            abortItem.setItemMeta(abortMeta);
            player.getInventory().addItem(backToCheckpointItem, abortItem);
        } else {
            player.sendMessage(Component.text(JAR.getPrefix() + "§7Du musst dein vorheriges JumpAndRun §cbeenden§7, bevor du ein neues JumpAndRun betrittst§8."));
        }
    }

    public void abortJumpAndRun(Player player) {
        if (isInJumpAndRun(player)) {
            Object[] data = jumpAndRunData.get(player);
            JumpAndRun jumpAndRun = (JumpAndRun) data[0];
            player.showTitle(Title.title(Component.text(jumpAndRun.getDifficulty().getColor() + "§l" + jumpAndRun.getName()),
                    Component.text("§cabgebrochen"), Title.Times.times(Duration.ofSeconds(2), Duration.ofSeconds(5), Duration.ofSeconds(2))));
            player.sendMessage(Component.text(JAR.getPrefix() + "§7Du hast das §2JumpAndRun §l" + jumpAndRun.getName() +
                    "§7 von §2" + jumpAndRun.getBuilder() + " §7abgebrochen§8."));
            player.sendMessage(Component.text(JAR.getPrefix() + "§7Difficulty§8: " + jumpAndRun.getDifficulty().getColor() +
                    jumpAndRun.getDifficulty().name() + " §8║ §7Spielzeit§8: §2" + Stringify.time(System.currentTimeMillis() - (long) data[3]) +
                    " §8║ §7Fails§8: §2" + data[1]));
            jumpAndRunData.remove(player);

            player.getInventory().removeItem(backToCheckpointItem, abortItem);
        }
    }

    public void finishJumpAndRun(Player player) {
        if (isInJumpAndRun(player)) {
            Object[] data = jumpAndRunData.get(player);
            JumpAndRun jumpAndRun = (JumpAndRun) data[0];
            player.showTitle(Title.title(Component.text(jumpAndRun.getDifficulty().getColor() + "§l" + jumpAndRun.getName()),
                    Component.text("§ageschafft"), Title.Times.times(Duration.ofSeconds(2), Duration.ofSeconds(5), Duration.ofSeconds(2))));
            player.sendMessage(Component.text(JAR.getPrefix() + "§7Du hast das §2JumpAndRun §l" + jumpAndRun.getName() +
                    "§7 von §2" + jumpAndRun.getBuilder() + " §7geschafft§8."));
            player.sendMessage(Component.text(JAR.getPrefix() + "§7Difficulty§8: " + jumpAndRun.getDifficulty().getColor() +
                    jumpAndRun.getDifficulty().name() + " §8║ §7Spielzeit§8: §2" + Stringify.time(System.currentTimeMillis() - (long) data[3]) +
                    " §8║ §7Fails§8: §2" + data[1]));
            jumpAndRunData.remove(player);

            player.getInventory().removeItem(backToCheckpointItem, abortItem);
        }
    }

    public void addJumpAndRun(String name, String builder, JumpAndRun.Difficulty difficulty, Location startLocation, Location endLocation, List<Location> checkpoints) {
        JumpAndRun jumpAndRun = new JumpAndRun(name, builder, difficulty, startLocation, endLocation, checkpoints);
        jumpAndRuns.put(name, jumpAndRun);
        saveJumpAndRuns();
    }

    public JumpAndRun getJumpAndRun(String name) {
        return jumpAndRuns.get(name);
    }

    public JumpAndRun getJumpAndRun(Location startLocation) {
        for (JumpAndRun jumpAndRun : jumpAndRuns.values())
            if (jumpAndRun.getStartLocation().getBlock().getLocation().equals(startLocation.getBlock().getLocation()))
                return jumpAndRun;
        return null;
    }

    public void deleteJumpAndRun(String name) {
        jumpAndRuns.remove(name);
    }

    public void loadJumpAndRuns() {
        this.configFile.load();
        if (configFile.getContent() != null) jumpAndRuns = this.configFile.getContent();
    }

    public void saveJumpAndRuns() {
        if (jumpAndRuns != null) this.configFile.store(jumpAndRuns);
        else this.configFile.store(Maps.newHashMap());
    }

    private Object[] createJumpAndRunData(JumpAndRun jumpAndRun) {
        return new Object[]{
                jumpAndRun, // JumpAndRun
                0, // Fails
                jumpAndRun.getStartLocation(), // Checkpoint
                System.currentTimeMillis() // Start time
        };
    }

    public void addFail(Player player) {
        Object[] data = jumpAndRunData.get(player);
        jumpAndRunData.remove(player);
        Object[] newData = new Object[]{data[0], (int) data[1] + 1, data[2], data[3]};
        jumpAndRunData.put(player, newData);
    }

    public void updateCheckpoint(Player player, Location location) {
        Object[] data = jumpAndRunData.get(player);
        jumpAndRunData.remove(player);
        Object[] newData = new Object[]{data[0], data[1], location, data[3]};
        jumpAndRunData.put(player, newData);
    }

    public Location getCheckpoint(Player player) {
        return (Location) jumpAndRunData.get(player)[2];
    }

    public void addCheckpoint(Location location) {
        checkpoints.add(location);
    }
}
