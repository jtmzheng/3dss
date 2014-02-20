package system;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;

import org.ini4j.InvalidFileFormatException;
import org.ini4j.Wini;

/*
 * A Singleton class used to retrieve key-value pairs stored in an ini file. Call Settings.loadIni(File file)
 * in the beginning of your application to load any custom ini file. Omit this call to use the default
 * configuration file. Note that if any other filetype other than .ini is used, this will throw a
 * InvalidFileFormatException.
 * 
 * @author Adi
 */
public class Settings extends Wini {
	// True if settings file has been loaded and the instance has been created.
	private static boolean isFileLoaded = false;
	
	// Settings instance.
	private static Settings instance = null;
	
	private Settings (File file) throws InvalidFileFormatException, IOException {
		super(file);
	}
	
	/**
	 * Loads an ini file to use for the application settings.
	 * @param file File to load. 
	 * @throws IOException
	 */
	public static void loadIni (File file) throws IOException {
		if (!file.isFile()) {
			throw new IOException("Cannot find file specified.");
		} else {
			instance = new Settings(file);
			isFileLoaded = true;
		}
	}

	/**
	 * Gets the instance of the settings object. If Settings.loadIni(File file) hasn't been previously called
	 * before this, this will call loadIni to populate the settings with the default ini file.
	 * @return
	 */
	public static Settings getInstance() {
		if (!isFileLoaded) {
			try {
				loadIni(new File("src/config/default.ini"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		} 
		
		return instance;
	}
}