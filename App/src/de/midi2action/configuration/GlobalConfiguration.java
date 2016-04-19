package de.midi2action.configuration;

import java.util.ArrayList;
import java.util.Collection;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;

final public class GlobalConfiguration {
	public final static GlobalConfiguration INSTANCE = new GlobalConfiguration();

	private final static boolean FOR_INPUT = false;
	
	private GlobalConfiguration() {
	}

	/**
	 * @return the configured MIDI in device or {@code null} otherwise
	 * 
	 * @throws MidiUnavailableException
	 */
	public MidiDevice getMidiInDevice() throws MidiUnavailableException {
		// konfigurierten Port auslesen
		String midiInPortName = ConfigurationProperties.INSTANCE.getProperty(ConfigurationProperties.MIDI_IN_PORT_NAME);
		if (midiInPortName == null || midiInPortName.length() < 1)
		{
			return null;
		}
		
		return getMidiDevice(midiInPortName, FOR_INPUT);
	}

	// grab all MIDI devices
	// is it possible to to grab all MIDI in right with an API call?
	// no, got to get the device first to check if its ready for input
	public MidiDevice[] getAllMidiInPorts() throws MidiUnavailableException {
		Collection<MidiDevice> result = new ArrayList<MidiDevice>();
		
		MidiDevice.Info[] midiInfo = MidiSystem.getMidiDeviceInfo();
		MidiDevice device = null;
		for (int i = 0; i < midiInfo.length; i++) {
			device = MidiSystem.getMidiDevice(midiInfo[i]);
			if (isForInput(device)) {
				result.add(device);
			}
		}
		
		return result.toArray(new MidiDevice[result.size()]);
	}
	
	/**
	 * Retrieves a {@link MidiDevice} for a given name.
	 *
	 * @param strDeviceName
	 *            the name of the device for which an object should be
	 *            retrieved.
	 * 
	 * @param bForOutput
	 *            If true, only output devices are considered. If false, only
	 *            input devices are considered.
	 * 
	 * @return {@link MidiDevice} whose name matches the passed name
	 * 
	 * @throws MidiUnavailableException
	 *             If the device has been found but does not fit
	 *             {@code bForOutput}. Or
	 *             {@link MidiSystem#getMidiDevice(javax.sound.midi.MidiDevice.Info)}
	 *             fails which is very unlikely since the call to
	 *             {@link MidiSystem#getMidiDeviceInfo()} before.
	 */
	public MidiDevice getMidiDevice(String strDeviceName, boolean bForOutput) throws MidiUnavailableException
	{
		MidiDevice.Info[]	aInfos = MidiSystem.getMidiDeviceInfo();
		
		for (int i = 0; i < aInfos.length; i++)
		{
			if (aInfos[i].getName().equals(strDeviceName))
			{
				MidiDevice device = MidiSystem.getMidiDevice(aInfos[i]);
				boolean bAllowsInput = isForInput(device);
				boolean	bAllowsOutput = (device.getMaxReceivers() != 0);
				if ((bAllowsOutput && bForOutput) || (bAllowsInput && !bForOutput))
				{
					return device;
				}
			}
		}
		
		throw new MidiUnavailableException(strDeviceName + " for output: " + bForOutput);
	}

	private boolean isForInput(MidiDevice device) {
		return (device.getMaxTransmitters() != 0);
	}
}
