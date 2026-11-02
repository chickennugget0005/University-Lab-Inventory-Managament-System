package src.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import src.model.Lab;

/**
 * Reads and writes the minimal lab configuration stored in {@code lab_data.txt}.
 */
public class LabFileManager {
    private static final Path LAB_FILE = Paths.get("lab_data.txt");

    /** Creates the file manager. */
    public LabFileManager() {
    }

    /**
     * Loads the lab name from the current working directory's data file.
     *
     * @return a lab containing the saved name, or {@code null} when no usable record is available
     */
    public static Lab loadLab() {
        // A missing or empty file indicates that no saved lab configuration exists yet.
        if (!Files.exists(LAB_FILE)) {
            return null;
        }

        try {
            List<String> lines = Files.readAllLines(LAB_FILE, StandardCharsets.UTF_8);
            if (lines == null || lines.isEmpty()) {
                return null;
            }
            return new Lab(lines.get(0).trim().isEmpty() ? "Lab" : lines.get(0).trim());
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Saves the lab name, replacing the prior file contents.
     *
     * @param lab lab to save
     * @return {@code true} if saved; {@code false} for null input or an I/O failure
     */
    public static boolean saveLab(Lab lab) {
        if (lab == null) {
            return false;
        }

        try {
            // Store the lab name so the application can restore its top-level identity.
            List<String> lines = new ArrayList<>();
            lines.add(lab.getName());
            Files.write(
                LAB_FILE,
                lines,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE
            );
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
