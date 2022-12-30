package de.smoofy.jumpandrun.utils;

/**
 * @author - Smoofy
 * @GitHub - https://github.com/Smoofy19
 * @Twitter - https://twitter.com/Smoofy19
 * Erstellt - 30.12.2022 21:45
 */
public class Stringify {

    public static String time(long time) {
        int minutes = (int) ((time / 1000) / 60);
        int seconds = (int) ((time / 1000) % 60);

        return minutes + ":" + seconds;
    }
}
