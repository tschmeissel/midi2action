package de.midi2action.test;

import java.util.UUID;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiUnavailableException;

import junit.framework.TestCase;

import org.junit.Test;

import de.midi2action.configuration.ConfigurationProperties;
import de.midi2action.configuration.GlobalConfiguration;

public class GlobalConfigurationTest extends TestCase {

	@Override
	protected void setUp() throws Exception {
	}

	@Override
	protected void tearDown() throws Exception {
	}

	@Test
	public void testGetMidiInPort() {
		MidiDevice midiInPort = null;
		try {
			midiInPort = GlobalConfiguration.INSTANCE.getMidiInDevice();
		} catch (MidiUnavailableException e) {
			fail();
		}
		
		assertNotNull(midiInPort);
	}
	
	@Test
	public void testGetUnavailableMidiInPort() {
		// set wrong value in configuration
		String midiInPortName = ConfigurationProperties.INSTANCE.getProperty(ConfigurationProperties.MIDI_IN_PORT_NAME);
		assertFalse(midiInPortName == null || midiInPortName.length() < 1);
		
		String tmpMidiInPortName = midiInPortName;
		
		ConfigurationProperties.INSTANCE.setProperty(ConfigurationProperties.MIDI_IN_PORT_NAME, UUID.randomUUID().toString());
		ConfigurationProperties.INSTANCE.store();
		
		MidiDevice midiInPort = null;
		try {
			midiInPort = GlobalConfiguration.INSTANCE.getMidiInDevice();
			fail();
		} catch (MidiUnavailableException e) {
		}
		
		assertNull(midiInPort);
		
		// roll back configuration
		ConfigurationProperties.INSTANCE.setProperty(ConfigurationProperties.MIDI_IN_PORT_NAME, tmpMidiInPortName);
		ConfigurationProperties.INSTANCE.store();
	}
	
	@Test
	// How to test this properly since we don't now if at all and if so how many
	// MIDI in devices the current system offers?
	public void testGetAllMidiInPorts() {
		MidiDevice[] midiInDevices = null;
		try {
			midiInDevices = GlobalConfiguration.INSTANCE.getAllMidiInPorts();
		} catch (MidiUnavailableException e) {
			fail();
		}
		
		assertNotNull(midiInDevices);
		assertTrue(midiInDevices.length > 0);
	}
	
	@Test
	public void testMidiInPortCanReceive() {
		
	}
}
