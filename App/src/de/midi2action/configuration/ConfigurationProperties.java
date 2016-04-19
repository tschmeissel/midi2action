package de.midi2action.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * Ein convenience wrapper für custom {@link Properties}.
 * 
 * @author zaphod
 */
final public class ConfigurationProperties {
	public final static ConfigurationProperties INSTANCE = new ConfigurationProperties();
	public final static String MIDI_IN_PORT_NAME = "midi.in.port.name";
	
	private final static String CONFIGURATION_FILE_NAME = "midi2action.properties";
	private final static Logger LOGGER = Logger.getLogger(ConfigurationProperties.class);
	
	private Properties configuration;
	private File propertiesFile;
	
	private ConfigurationProperties() {
	}
	
	/**
	 * @throws IllegalArgumentException
	 */
	public String getProperty(String key) {
		validateNullAndLength(key);
		
		loadConfiguration();

		return this.configuration.getProperty(key);
	}

	/**
	 * @throws IllegalArgumentException
	 */
	public void setProperty(String key, String value) {
		validateNullAndLength(key);
		validateNull(value);
		
		loadConfiguration();

		// null Werte sind in Properties nicht erlaubt
		this.configuration.setProperty(key, value);
	}
	
	/**
	 * @throws IllegalArgumentException
	 */
	public void addProperty(String key) {
		validateNullAndLength(key);

		loadConfiguration();

		// null Werte sind in Properties nicht erlaubt
		this.configuration.setProperty(key, "");
	}

	private void validateNullAndLength(String value) {
		if (value == null || value.length() < 1) {
			throw new IllegalArgumentException("invalid value for configuration value");
		}
	}
	
	private void validateNull(String value) {
		if (value == null) {
			throw new IllegalArgumentException("invalid value for configuration value");
		}
	}

	private void loadConfiguration() {
		if (this.configuration == null) {
			load();
		}
	}

	private void load() {
        InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(getPropertiesFile());
		} catch (FileNotFoundException e) {
			LOGGER.error(e);
			System.exit(-1);
		}

        LOGGER.info("load " + propertiesFile);

		this.configuration = new Properties();
		try {
	        this.configuration.load(inputStream);
		} catch (IOException e) {
			LOGGER.error(e);
		}
	}

	private File getPropertiesFile() {
		
		if (this.propertiesFile == null)
		{
			File jarPath = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
			String propertiesFileName = jarPath.getParentFile().getAbsolutePath() + "\\" + CONFIGURATION_FILE_NAME;
			this.propertiesFile = new File(propertiesFileName);
		}
		
		return this.propertiesFile;
	}
	
	public void removeProperty(String key) {
		validateNullAndLength(key);

		loadConfiguration();

		this.configuration.remove(key);
	}
	
	public void store() {
		
		try {
			Writer writer = new FileWriter(getPropertiesFile());
			this.configuration.store(writer, "midi2action configuration");
			LOGGER.info("store: " + getPropertiesFile().getAbsolutePath());
		} catch (IOException e) {
			LOGGER.error(e);
		}
	}
	
	public void reload() {
		load();
	}
}
