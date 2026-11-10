package src.storage;

import src.model.ChangeLogEntry;
import java.util.ArrayList;
import java.io.File;


public class ChangeLogFileManager {
    public String filePath = "";
    public static ArrayList<ChangeLogEntry> loadEntries()
    {
      //comma separator, timestamp memberID action details, all strings
      ArrayList<ChangeLogEntry> ChangeLog = new ArrayList<ChangeLogEntry>();
      //bufferedReader


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
