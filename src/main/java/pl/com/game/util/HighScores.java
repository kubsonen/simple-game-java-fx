package pl.com.game.util;

import java.io.*;

/**
 * @author JNartowicz
 */
public class HighScores {

    private static final String FILE_NAME = "high-scores";

    public static File getHighScoreFile() {

        File file = new File(FILE_NAME);
        if (file.exists()) {
            return file;
        } else {
            try {
                file.createNewFile();
                return file;
            } catch (IOException e) {
                return null;
            }
        }

    }

    public static void saveHighScore(String nickname, double score) {

        if (nickname == null && nickname.trim().isEmpty()) return;
        nickname = nickname.trim();

        try {
            File hsFile = getHighScoreFile();
            if (hsFile == null) return;

            hsFile.delete();
            hsFile.createNewFile();


            try (BufferedWriter writer = new BufferedWriter(new FileWriter(hsFile))) {
                writer.write(nickname.replace(":", "-") + ":" + score);
            }

        } catch (Throwable r) {
            r.printStackTrace();
        }

    }

    public static String getHsText() {
        try {
            File hsFile = getHighScoreFile();
            if (hsFile == null) return "";

            String g = "";
            String line;

            try (BufferedReader reader = new BufferedReader(new FileReader(hsFile))) {
                while ((line = reader.readLine()) != null) {
                    g = g + line;
                }
            }

            return g;

        } catch (Throwable r) {
            r.printStackTrace();
            return "";
        }
    }

}
