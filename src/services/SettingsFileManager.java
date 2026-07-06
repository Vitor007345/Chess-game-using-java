package services;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import chessgame.Settings;
import services.errors.*;

public class SettingsFileManager {
	private static final String SETTINGS_FOLDER = "settings";
	private static final String SETTINGS_FILE = "settings/settings.txt";
	
	
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
