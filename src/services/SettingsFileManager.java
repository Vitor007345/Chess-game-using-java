package services;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import chessgame.Settings;
import services.errors.*;

/**
 * Utility class responsible for saving and loading the user's game settings
 * to and from the local file system.
 * The settings are stored as a simple comma-separated string in a text file.
 */
public class SettingsFileManager {
    
    /** The relative path to the folder where user settings are stored. */
    private static final String SETTINGS_FOLDER = "settings";
    
    /** The specific file path used to store the settings configuration. */
    private static final String SETTINGS_FILE = "settings/settings.txt";
    
    /**
     * Saves the provided settings configuration to a local text file.
     * The values are written as a single comma-separated line (e.g., "true,false,true").
     * * @param newSettings The {@link Settings} object containing the preferences to be saved.
     * @throws SavingException If an I/O error occurs while creating the folder or writing to the file.
     */
    public static void saveSettings(Settings newSettings) throws SavingException{
        File folder = new File(SETTINGS_FOLDER);
        if (!folder.exists()) {
            folder.mkdir();
        }

        try (FileWriter writer = new FileWriter(SETTINGS_FILE)) {
            
            String data = newSettings.isAutoReverseBoard() + "," +
                          newSettings.isAutoPromoteQueen() + "," +
                          newSettings.isShowCoordinates();
            writer.write(data);
            
        } catch (IOException e) {
            throw new SavingException(e);
        }
    }
    
    /**
     * Loads the user's settings from the local text file.
     * If the file does not exist, it returns a new {@link Settings} object with default values.
     * * @return A {@link Settings} object populated with the user's saved preferences.
     * @throws LoadingException If the file is empty, does not contain exactly three values, 
     * or if an I/O error occurs during the read process.
     */
    public static Settings loadSettings() throws LoadingException{
        File file = new File(SETTINGS_FILE);
        if (!file.exists()) {
            return new Settings();
        }

        try (Scanner scanner = new Scanner(file)) {
            if (!scanner.hasNextLine()) {
                throw new LoadingException("File is empty");
            }
            
            String[] values = scanner.nextLine().split(",");
            if (values.length != 3) {
                throw new LoadingException("Corrupted File");
            }
            
            return new Settings(Boolean.parseBoolean(values[0]), Boolean.parseBoolean(values[1]), Boolean.parseBoolean(values[2]));
            
        } catch (IOException e) {
            throw new LoadingException(e);
        }
    }

}