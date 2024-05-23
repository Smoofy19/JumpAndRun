package de.smoofy.jumpandrun;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;

import java.util.List;

/**
 * @author - Smoofy
 * @GitHub - https://github.com/Smoofy19
 * @Twitter - https://twitter.com/Smoofy19
 * Erstellt - 30.12.2022 21:40
 */
@Getter
public class JumpAndRun {

    private final String name;
    private final String builder;

    private final Difficulty difficulty;

    private final Location startLocation;
    private final Location endLocation;
    private List<Location> checkpoints;

    public JumpAndRun(String name, String builder, Difficulty difficulty, Location startLocation, Location endLocation, List<Location> checkpoints) {
        this.name = name;
        this.builder = builder;
        this.difficulty = difficulty;
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        if (checkpoints != null) this.checkpoints = checkpoints;
    }

    @Getter
    @AllArgsConstructor
    public enum Difficulty {

        EASY(1, NamedTextColor.GREEN),
        NORMAL(2, NamedTextColor.GOLD),
        HARD(3, NamedTextColor.RED),
        EXTREME(4, NamedTextColor.AQUA);

        private final int id;
        private final NamedTextColor color;

        public static Difficulty getDifficultyById(int id) {
            for (Difficulty difficulty : values()) {
                if (difficulty.getId() == id) return difficulty;
            }
            return null;
        }
    }
}
