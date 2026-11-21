package src.storage;

import src.model.ChangeLogEntry;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

// Reads and appends local change-log records for the ULIMS application.
// R8.10: The system shall record successful user management changes in the system's change log.
// Permission checks are performed by AppController, not by this file manager.
public class ChangeLogFileManager {
    // Store logs alongside members.txt and lab_data.txt in the working directory.
    // Keep this field for compatibility with the original version of the class.
    public static String filePath = "change_log.csv";

    
    // Reloads all stored audit records in chronological file order.
    // A missing file means that no actions have been recorded yet.
    public static ArrayList<ChangeLogEntry> loadEntries() {
        ArrayList<ChangeLogEntry> changeLog = new ArrayList<>();
        Path logFile = Paths.get(filePath);

        if (!Files.exists(logFile)) {
            return changeLog;
        }

        try {
            // Read one line at a time: no skipped records.
            for (String line : Files.readAllLines(logFile, StandardCharsets.UTF_8)) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                // Fields are timestamp, acting member ID, action, and details.
                // CSV parsing must allow commas and quotation marks in details.
                List<String> fields = parseCsvLine(line);
                if (fields.size() != 4) {
                    throw new IllegalStateException("Invalid change-log record: expected four fields.");
                }

                ChangeLogEntry loadedEntry = new ChangeLogEntry(
                    fields.get(0), fields.get(1), fields.get(2), fields.get(3)
                );
                changeLog.add(loadedEntry);
            }
        } catch (IOException | SecurityException e) {
            // Do not pretend the log is empty when it could not be read.
            throw new IllegalStateException("Unable to read the change log.", e);
        }

        return changeLog;
    }

    // R8.10: The system shall record successful user management changes in the system's change log.
    // The controller checks this return value and handles an unsuccessful save.
    public static boolean appendEntry(ChangeLogEntry entry) {
        if (entry == null) {
            return false;
        }

        try {
            Files.write(
                Paths.get(filePath),
                (entry.toCsvLine() + System.lineSeparator()).getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
            );
            return true;
        } catch (IOException | SecurityException e) {
            return false;
        }
    }

    // Parses one CSV row, supporting quoted commas and doubled quotation marks.
    // Newlines in fields are normalized by ChangeLogEntry.toCsvLine().
    
    private static List<String> parseCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean insideQuotes = false;
        boolean afterClosingQuote = false;
        boolean atFieldStart = true;

        for (int index = 0; index < line.length(); index++) {
            char character = line.charAt(index);

            if (insideQuotes) {
                if (character == '"') {
                    if (index + 1 < line.length() && line.charAt(index + 1) == '"') {
                        current.append('"');
                        index++;
                    } else {
                        insideQuotes = false;
                        afterClosingQuote = true;
                    }
                } else {
                    current.append(character);
                }
            } else if (character == ',') {
                fields.add(current.toString());
                current.setLength(0);
                atFieldStart = true;
                afterClosingQuote = false;
            } else if (character == '"' && atFieldStart) {
                insideQuotes = true;
                atFieldStart = false;
            } else if (afterClosingQuote || character == '"') {
                throw new IllegalStateException("Malformed quoted field in change log.");
            } else {
                current.append(character);
                atFieldStart = false;
            }
        }

        if (insideQuotes) {
            throw new IllegalStateException("Unclosed quotation mark in change log.");
        }
        fields.add(current.toString());
        return fields;
    }
}
