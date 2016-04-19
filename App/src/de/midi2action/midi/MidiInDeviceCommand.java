package de.midi2action.midi;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Transmitter;

import org.apache.log4j.Logger;

import de.midi2action.configuration.GlobalConfiguration;

/**
 * Gets the configured MIDI IN device and sets the {@link EventReceiver} to
 * receive {@link MidiEvent}'s. It's like a switcher for starting and
 * stopping the reception of {@link MidiEvent}'s. 
 * <p>
 * The {@link EndOfMidiTrackListener} is of interest for situations where
 * the proxy shall be ended automatically, e.g. a unit test. In case of a
 * user input to stop the receiving process it is not necessary. 
 *
 * @author zaphod
 */
final public class MidiInDeviceCommand implements EndOfMidiTrackListener {
	private final static Logger LOGGER = Logger.getLogger(MidiInDeviceCommand.class);
	
	private MidiDevice midiInDevice;
	private EventReceiver eventReceiver;
	
	public void start() throws MidiUnavailableException {
		this.midiInDevice = GlobalConfiguration.INSTANCE.getMidiInDevice();
		this.midiInDevice.open();
		
		this.eventReceiver = new EventReceiver();
		try {
			Transmitter t = this.midiInDevice.getTransmitter();
			t.setReceiver(this.eventReceiver);
		} catch (MidiUnavailableException e) {
			LOGGER.error("wasn't able to connect the device's Transmitter to the Receiver:");
			this.midiInDevice.close();
			this.eventReceiver.close();
			return;
		}
	}
	
	public void stop() {
		if (midiInDevice != null) {
			LOGGER.error("close MIDI in device and belonging receiver");
			this.midiInDevice.close();
		}

		if (this.eventReceiver != null) {
			LOGGER.error("close MIDI event receiver");
			this.eventReceiver.close();
		}
	}

	@Override
	public void reached() {
		stop();
	}

	public void setMessageEventListener(MessageEventListener messageEventListener) {
		this.eventReceiver.setMessageEventListener(messageEventListener);
	}
	
	public void removeMessageEventListener() {
		this.eventReceiver.setMessageEventListener(null);
	}
}
