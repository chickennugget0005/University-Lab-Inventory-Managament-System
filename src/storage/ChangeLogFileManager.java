package src.storage;

import src.model.ChangeLogEntry;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;


public class ChangeLogFileManager {
    public static String filePath = "";
    public static ArrayList<ChangeLogEntry> loadEntries()
    {
      //comma separator, timestamp memberID action details, all strings
      ArrayList<ChangeLogEntry> ChangeLog = new ArrayList<ChangeLogEntry>();
      try(BufferedReader intake = new BufferedReader(new FileReader(filePath)))
      {
         String line;
      while ((line = intake.readLine()) != null)
      {
        line = intake.readLine();
        String[] newItem = line.split(",");
        ChangeLogEntry loadedEntry = new ChangeLogEntry(newItem[0], newItem[1], newItem[2], newItem[3]);
        ChangeLog.add(loadedEntry);
      }
      }
      catch(IOException e)
      {
        //error reading log entries from file
      }
      return ChangeLog;


    }
    public static boolean appendEntry(ChangeLogEntry entry)
    {
      boolean added;
      try()
      {
         if(filePath.canWrite())
         { 
          FileWriter appending = new FileWriter(filePath);
          appending.write(entry.toString());
          appending.close();
          added = true;
         }
         else 
         {
          added = false;
         }
      }
      catch(SecurityException e)
      {
         added = false;
         // TODO: file can't be accessed due to permissions, maybe add some sort of return/printing to main about this error if necessary later. But not critical.
      }
      return added;

    }
}
