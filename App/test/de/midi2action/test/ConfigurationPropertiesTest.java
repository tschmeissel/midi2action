package de.midi2action.test;

import java.util.UUID;

import org.junit.Test;

import de.midi2action.configuration.ConfigurationProperties;
import junit.framework.TestCase;

public class ConfigurationPropertiesTest extends TestCase {
	private final static String TEST_KEY = UUID.randomUUID().toString();

	@Override
	protected void setUp() throws Exception {
		ConfigurationProperties.INSTANCE.addProperty(TEST_KEY);
		ConfigurationProperties.INSTANCE.store();
	}

	@Override
	protected void tearDown() throws Exception {
		ConfigurationProperties.INSTANCE.removeProperty(TEST_KEY);
		ConfigurationProperties.INSTANCE.store();
	}
	
	// Zufälligen Schlüssel
	//  hinzufügen
	//  darauf zugreifen (keine IllegalArgumentException)
	//  entfernen
	@Test
	public void testAddNewKey() {
		String randomKey = UUID.randomUUID().toString();
		
		ConfigurationProperties.INSTANCE.addProperty(randomKey);
		try {
			ConfigurationProperties.INSTANCE.getProperty(randomKey);
		} catch (IllegalArgumentException e) {
			fail();
		}
		
		ConfigurationProperties.INSTANCE.removeProperty(randomKey);
	}

	// Zufälligen Schlüssel
	//  hinzufügen
	//  entfernen
	//  darauf zugreifen
	@Test
	public void testRemoveKey() {
		String randomKey = UUID.randomUUID().toString();
		ConfigurationProperties.INSTANCE.addProperty(randomKey);
		ConfigurationProperties.INSTANCE.removeProperty(randomKey);
		
		assertNull(ConfigurationProperties.INSTANCE.getProperty(randomKey));
	}
	
	// Leeren Wert
	//  schreiben
	//  lesen
	//  entfernen
	@Test
	public void testReadEmptyValueFromExistingKey() {
		ConfigurationProperties.INSTANCE.setProperty(TEST_KEY, "");
		String configValue = ConfigurationProperties.INSTANCE.getProperty(TEST_KEY);
		
		assertNotNull(configValue);
		assertTrue(configValue.length() == 0);
	}
	
	// Zufälligen Wert
	//  schreiben
	//  lesen
	//  löschen
	@Test
	public void testReadValueFromExistingKey() {
		String testValue = UUID.randomUUID().toString();
		
		ConfigurationProperties.INSTANCE.setProperty(TEST_KEY, testValue);
		String configValue = ConfigurationProperties.INSTANCE.getProperty(TEST_KEY);
		
		assertNotNull(configValue);
		assertTrue(configValue.equals(testValue));
	}
	
	// Wie soll sich die Methode verhalten, wenn versucht wird, von einem nicht existierenden Key zu lesen?
	// es soll eine null zurückgegeben werden
	@Test
	public void testReadValueFromNotExistingKey() {
		String configValue = ConfigurationProperties.INSTANCE.getProperty(UUID.randomUUID().toString());
		assertNull(configValue);
	}

	// Zufälligen Wert für Testschlüssel
	//  schreiben
	//  lesen
	//  entfernen
	//  lesen
	@Test
	public void testRemoveValue() {
		String testValue = UUID.randomUUID().toString();
		
		ConfigurationProperties.INSTANCE.setProperty(TEST_KEY, testValue);
		String configValue = ConfigurationProperties.INSTANCE.getProperty(TEST_KEY);
		
		assertNotNull(configValue);
		assertTrue(configValue.equals(testValue));
		
		configValue = null;
		
		ConfigurationProperties.INSTANCE.setProperty(TEST_KEY, "");
		configValue = ConfigurationProperties.INSTANCE.getProperty(TEST_KEY);

		assertTrue(configValue.length() == 0);
	}
	
	@Test
	public void testReload() {
		String testValue = UUID.randomUUID().toString();
		
		ConfigurationProperties.INSTANCE.setProperty(TEST_KEY, testValue);
		String configValue = ConfigurationProperties.INSTANCE.getProperty(TEST_KEY);

		assertNotNull(configValue);
		assertTrue(configValue.equals(testValue));

		ConfigurationProperties.INSTANCE.store();
		ConfigurationProperties.INSTANCE.reload();
		
		configValue = null;
		
		configValue = ConfigurationProperties.INSTANCE.getProperty(TEST_KEY);

		assertNotNull(configValue);
		assertTrue(configValue.equals(testValue));
	}
	
	@Test
	public void testInvalidParameter() {
		try {
			ConfigurationProperties.INSTANCE.getProperty(null);
			fail();
		} catch (IllegalArgumentException e)
		{
		}
		
		try {
			ConfigurationProperties.INSTANCE.getProperty("");
			fail();
		} catch (IllegalArgumentException e)
		{
		}
		
		try {
			ConfigurationProperties.INSTANCE.setProperty(UUID.randomUUID().toString(), null);
			fail();
		} catch (IllegalArgumentException e)
		{
		}
		
		try {
			ConfigurationProperties.INSTANCE.setProperty(null, UUID.randomUUID().toString());
			fail();
		} catch (IllegalArgumentException e)
		{
		}
		
		try {
			ConfigurationProperties.INSTANCE.setProperty("", UUID.randomUUID().toString());
			fail();
		} catch (IllegalArgumentException e)
		{
		}
		
		try {
			ConfigurationProperties.INSTANCE.addProperty(null);
			fail();
		} catch (IllegalArgumentException e)
		{
		}
		
		try {
			ConfigurationProperties.INSTANCE.addProperty("");
			fail();
		} catch (IllegalArgumentException e)
		{
		}
		
		try {
			ConfigurationProperties.INSTANCE.removeProperty(null);
			fail();
		} catch (IllegalArgumentException e)
		{
		}
		
		try {
			ConfigurationProperties.INSTANCE.removeProperty("");
			fail();
		} catch (IllegalArgumentException e)
		{
		}
	}
}
