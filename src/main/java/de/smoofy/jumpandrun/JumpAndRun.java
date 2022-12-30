package de.smoofy.jumpandrun;

import lombok.AllArgsConstructor;
import lombok.Getter;
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

        EASY(1, "§a"),
        NORMAL(2, "§6"),
        HARD(3, "§c"),
        EXTREME(4, "§b");

        private final int id;
        private final String color;

        public static Difficulty getDifficultyById(int id) {
            for (Difficulty difficulty : values()) {
                if (difficulty.getId() == id) return difficulty;
            }
            return null;
        }
    }
}
