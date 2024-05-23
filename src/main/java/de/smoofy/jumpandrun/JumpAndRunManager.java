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
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.nio.file.Files;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    private NamespacedKey checkpointKey;
    private NamespacedKey abortKey;

    private ItemStack backToCheckpointItem;
    private ItemStack abortItem;

    public JumpAndRunManager() {
        File directory = new File(Bukkit.getWorldContainer() + "/plugins/JumpAndRun/");
        if (Files.notExists(directory.toPath())) directory.mkdirs();

        this.configFile = new ConfigFile<>(directory.getPath() + "/JumpAndRuns.json",
                new TypeToken<Map<String, JumpAndRun>>() {}.getType());

        this.jumpAndRuns = Maps.newHashMap();
        this.jumpAndRunData = Maps.newHashMap();
        this.setup = Maps.newHashMap();

        this.checkpointKey = NamespacedKey.fromString("back_to_checkpoint", JAR.getInstance());
        this.abortKey = NamespacedKey.fromString("abort", JAR.getInstance());

        this.backToCheckpointItem = this.checkpointItem();
        this.abortItem = this.abortItem();

        this.loadJumpAndRuns();
    }

    public boolean isInJumpAndRun(Player player) {
        return this.jumpAndRunData.containsKey(player);
    }

    public void enterJumpAndRun(Player player, JumpAndRun jumpAndRun) {
        if (!this.isInJumpAndRun(player)) {
            this.jumpAndRunData.put(player, this.createJumpAndRunData(jumpAndRun));

            player.showTitle(Title.title(
                    Component.text(jumpAndRun.getName(), jumpAndRun.getDifficulty().getColor(), TextDecoration.BOLD),
                    Component.text("gestartet", NamedTextColor.GREEN),
                    Title.Times.times(Duration.ofSeconds(2), Duration.ofSeconds(5), Duration.ofSeconds(2))));

            player.sendMessage(JAR.getPrefix()
                    .append(Component.text("Du hast das ", NamedTextColor.GRAY))
                    .append(Component.text("JumpAndRun ", NamedTextColor.GREEN))
                    .append(Component.text(jumpAndRun.getName(), NamedTextColor.GREEN, TextDecoration.BOLD))
                    .append(Component.text(" von ", NamedTextColor.GRAY))
                    .append(Component.text(jumpAndRun.getBuilder(), NamedTextColor.GREEN))
                    .append(Component.text(" betreten.", NamedTextColor.GRAY)));

            player.sendMessage(JAR.getPrefix()
                    .append(Component.text("Difficulty", NamedTextColor.GRAY))
                    .append(Component.text(": ", NamedTextColor.DARK_GRAY))
                    .append(Component.text(jumpAndRun.getDifficulty().name(), jumpAndRun.getDifficulty().getColor()))
                    .append(Component.text(" ║ ", NamedTextColor.DARK_GRAY))
                    .append(Component.text("Checkpoints", NamedTextColor.GRAY))
                    .append(Component.text(": ", NamedTextColor.DARK_GRAY))
                    .append(Component.text(jumpAndRun.getCheckpoints().size(), NamedTextColor.DARK_GREEN)));

            player.getInventory().addItem(this.backToCheckpointItem, this.abortItem);
        } else {
            player.sendMessage(JAR.getPrefix()
                    .append(Component.text("Du musst dein vorheriges JumpAndRun ", NamedTextColor.GRAY))
                    .append(Component.text("beenden", NamedTextColor.RED))
                    .append(Component.text(", bevor du ein neues JumpAndRun betrittst.", NamedTextColor.GRAY)));
        }
    }

    public void abortJumpAndRun(Player player) {
        if (this.isInJumpAndRun(player)) {
            Object[] data = this.jumpAndRunData.get(player);
            JumpAndRun jumpAndRun = (JumpAndRun) data[0];

            player.showTitle(Title.title(
                    Component.text(jumpAndRun.getName(), jumpAndRun.getDifficulty().getColor(), TextDecoration.BOLD),
                    Component.text("abgebrochen", NamedTextColor.RED),
                    Title.Times.times(Duration.ofSeconds(2), Duration.ofSeconds(5), Duration.ofSeconds(2))));

            player.sendMessage(JAR.getPrefix()
                    .append(Component.text("Du hast das ", NamedTextColor.GRAY))
                    .append(Component.text("JumpAndRun ", NamedTextColor.GREEN))
                    .append(Component.text(jumpAndRun.getName(), NamedTextColor.GREEN, TextDecoration.BOLD))
                    .append(Component.text(" von ", NamedTextColor.GRAY))
                    .append(Component.text(jumpAndRun.getBuilder(), NamedTextColor.GREEN))
                    .append(Component.text(" abgebrochen.", NamedTextColor.GRAY)));

            player.sendMessage(JAR.getPrefix()
                    .append(Component.text("Difficulty", NamedTextColor.GRAY))
                    .append(Component.text(": ", NamedTextColor.DARK_GRAY))
                    .append(Component.text(jumpAndRun.getDifficulty().name(), jumpAndRun.getDifficulty().getColor()))
                    .append(Component.text(" ║ ", NamedTextColor.DARK_GRAY))
                    .append(Component.text("Spielzeit", NamedTextColor.GRAY))
                    .append(Component.text(": ", NamedTextColor.DARK_GRAY))
                    .append(Component.text(Stringify.time(System.currentTimeMillis() - (long) data[3]), NamedTextColor.DARK_GREEN))
                    .append(Component.text(" ║ ", NamedTextColor.DARK_GRAY))
                    .append(Component.text("Fails", NamedTextColor.GRAY))
                    .append(Component.text(": ", NamedTextColor.DARK_GRAY))
                    .append(Component.text(data[1].toString(), NamedTextColor.DARK_GREEN)));

            this.jumpAndRunData.remove(player);

            player.getInventory().removeItem(this.backToCheckpointItem, this.abortItem);
        }
    }

    public void finishJumpAndRun(Player player) {
        if (this.isInJumpAndRun(player)) {
            Object[] data = this.jumpAndRunData.get(player);
            JumpAndRun jumpAndRun = (JumpAndRun) data[0];
            player.showTitle(Title.title(
                    Component.text(jumpAndRun.getName(), jumpAndRun.getDifficulty().getColor(), TextDecoration.BOLD),
                    Component.text("geschafft", NamedTextColor.GREEN),
                    Title.Times.times(Duration.ofSeconds(2), Duration.ofSeconds(5), Duration.ofSeconds(2))));

            player.sendMessage(JAR.getPrefix()
                    .append(Component.text("Du hast das ", NamedTextColor.GRAY))
                    .append(Component.text("JumpAndRun ", NamedTextColor.GREEN))
                    .append(Component.text(jumpAndRun.getName(), NamedTextColor.GREEN, TextDecoration.BOLD))
                    .append(Component.text(" von ", NamedTextColor.GRAY))
                    .append(Component.text(jumpAndRun.getBuilder(), NamedTextColor.GREEN))
                    .append(Component.text(" geschafft.", NamedTextColor.GRAY)));

            player.sendMessage(JAR.getPrefix()
                    .append(Component.text("Difficulty", NamedTextColor.GRAY))
                    .append(Component.text(": ", NamedTextColor.DARK_GRAY))
                    .append(Component.text(jumpAndRun.getDifficulty().name(), jumpAndRun.getDifficulty().getColor()))
                    .append(Component.text(" ║ ", NamedTextColor.DARK_GRAY))
                    .append(Component.text("Spielzeit", NamedTextColor.GRAY))
                    .append(Component.text(": ", NamedTextColor.DARK_GRAY))
                    .append(Component.text(Stringify.time(System.currentTimeMillis() - (long) data[3]), NamedTextColor.DARK_GREEN))
                    .append(Component.text(" ║ ", NamedTextColor.DARK_GRAY))
                    .append(Component.text("Fails", NamedTextColor.GRAY))
                    .append(Component.text(": ", NamedTextColor.DARK_GRAY))
                    .append(Component.text(data[1].toString(), NamedTextColor.DARK_GREEN)));

            this.jumpAndRunData.remove(player);

            player.getInventory().removeItem(this.backToCheckpointItem, this.abortItem);
        }
    }

    public void addJumpAndRun(String name, String builder, JumpAndRun.Difficulty difficulty, Location startLocation, Location endLocation, List<Location> checkpoints) {
        JumpAndRun jumpAndRun = new JumpAndRun(name, builder, difficulty, startLocation, endLocation, checkpoints);
        this.jumpAndRuns.put(name, jumpAndRun);
        this.saveJumpAndRuns();
    }

    public JumpAndRun getJumpAndRun(String name) {
        return this.jumpAndRuns.get(name);
    }

    public JumpAndRun getJumpAndRun(Location startLocation) {
        for (JumpAndRun jumpAndRun : this.jumpAndRuns.values())
            if (jumpAndRun.getStartLocation().getBlock().getLocation().equals(startLocation.getBlock().getLocation()))
                return jumpAndRun;
        return null;
    }

    public void deleteJumpAndRun(String name) {
        this.jumpAndRuns.remove(name);
    }

    public void loadJumpAndRuns() {
        this.configFile.load();
        if (this.configFile.getContent() != null) this.jumpAndRuns = this.configFile.getContent();
    }

    public void saveJumpAndRuns() {
        this.configFile.store(Objects.requireNonNullElseGet(this.jumpAndRuns, Maps::newHashMap));
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
        Object[] data = this.jumpAndRunData.get(player);
        this.jumpAndRunData.remove(player);
        Object[] newData = new Object[]{data[0], (int) data[1] + 1, data[2], data[3]};
        this.jumpAndRunData.put(player, newData);
    }

    public void updateCheckpoint(Player player, Location location) {
        Object[] data = this.jumpAndRunData.get(player);
        this.jumpAndRunData.remove(player);
        Object[] newData = new Object[]{data[0], data[1], location, data[3]};
        this.jumpAndRunData.put(player, newData);
    }

    public Location getCheckpoint(Player player) {
        return (Location) this.jumpAndRunData.get(player)[2];
    }

    public void addCheckpoint(Location location) {
        this.checkpoints.add(location);
    }

    private ItemStack checkpointItem() {
        ItemStack itemStack = new ItemStack(Material.ORANGE_DYE);
        ItemMeta itemMeta = itemStack.getItemMeta();
        NamespacedKey namespacedKey = this.checkpointKey;

        if (namespacedKey == null) return null;

        itemMeta.getPersistentDataContainer().set(namespacedKey, PersistentDataType.INTEGER, 0);
        itemMeta.displayName(Component.text("Zurück zum Checkpoint", NamedTextColor.GOLD));

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    private ItemStack abortItem() {
        ItemStack itemStack = new ItemStack(Material.RED_DYE);
        ItemMeta itemMeta = itemStack.getItemMeta();
        NamespacedKey namespacedKey = this.abortKey;

        if (namespacedKey == null) return null;

        itemMeta.getPersistentDataContainer().set(namespacedKey, PersistentDataType.INTEGER, 0);
        itemMeta.displayName(Component.text("JumpAndRun abbrechen", NamedTextColor.RED));

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }
}
