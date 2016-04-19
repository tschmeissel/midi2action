package de.midi2action.test;

import java.io.File;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.Transmitter;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

import de.midi2action.configuration.GlobalConfiguration;
import de.midi2action.midi.EndOfMidiTrackListener;
import de.midi2action.midi.MidiInDeviceCommand;

public class MidiInDeviceProxyTest extends TestCase {
	private final static Logger LOGGER = Logger.getLogger(MidiInDeviceProxyTest.class);
	private static final String MIDI_OUT_PORT_NAME = "loopMIDI Port";
	private final static boolean FOR_OUTPUT = true;
	private final static int END_OF_MIDI_TRACK = 47;
	private final static File MIDI_FILE = new File("town.mid");

	private final Sequencer sequencer;
	
	private EndOfMidiTrackListener endOfMidiTrackListener;
	
	public MidiInDeviceProxyTest() throws MidiUnavailableException {
		this.sequencer = MidiSystem.getSequencer();
	}
	
	public void testStartStopReceiveClose() throws MidiUnavailableException {
		// open proxy device
		MidiInDeviceCommand midiInDeviceProxy = new MidiInDeviceCommand();
		this.endOfMidiTrackListener = midiInDeviceProxy;
		midiInDeviceProxy.start();
		
		// play MIDI file to be received by proxy device
		MidiDevice midiOutDevice = getMidiOutPort();
		midiOutDevice.open();
		assertNotNull(midiOutDevice);
		playMidi(midiOutDevice);

		// run test for 5 s
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			LOGGER.error(e);
		}
		
		midiOutDevice.close();
	}

	private MidiDevice getMidiOutPort() throws MidiUnavailableException {
		return GlobalConfiguration.INSTANCE.getMidiDevice(MIDI_OUT_PORT_NAME, FOR_OUTPUT);
	}

	private void playMidi(final MidiDevice midiOutDevice) {
		/*
		 * We read in the MIDI file to a Sequence object. This object is set at
		 * the Sequencer later.
		 */
		Sequence sequence = null;
		try {
			sequence = MidiSystem.getSequence(MIDI_FILE);
		} catch (Exception e) {
			/*
			 * In case of an exception, we dump the exception including the
			 * stack trace to the console. Then, we exit the program.
			 */
			LOGGER.error(e);
			System.exit(1);
		}
		
		/*
		 *	The Sequencer is still a dead object.
		 *	We have to open() it to become live.
		 *	This is necessary to allocate some ressources in
		 *	the native part.
		 */
		try
		{
			this.sequencer.open();
		}
		catch (MidiUnavailableException e)
		{
			LOGGER.error(e);
			System.exit(1);
		}
		
		/*
		 *	Next step is to tell the Sequencer which
		 *	Sequence it has to play. In this case, we
		 *	set it as the Sequence object created above.
		 */
		try
		{
			this.sequencer.setSequence(sequence);
		}
		catch (InvalidMidiDataException e)
		{
			LOGGER.error(e);
			System.exit(1);
		}
		
		/*
		 *	Now, we set up the destinations the Sequence should be
		 *	played on. Here, we try to use the default
		 *	synthesizer. With some Java Sound implementations
		 *	(Sun jdk1.3/1.4 and others derived from this codebase),
		 *	the default sequencer and the default synthesizer
		 *	are combined in one object. We test for this
		 *	condition, and if it's true, nothing more has to
		 *	be done. With other implementations (namely Tritonus),
		 *	sequencers and synthesizers are always seperate
		 *	objects. In this case, we have to set up a link
		 *	between the two objects manually.
		 *
		 *	By the way, you should never rely on sequencers
		 *	being synthesizers, too; this is a highly non-
		 *	portable programming style. You should be able to
		 *	rely on the other case working. Alas, it is only
		 *	partly true for the Sun jdk1.3/1.4.
		 */
		if (! (this.sequencer instanceof Synthesizer))
		{
			/*
			 *	We try to get the default synthesizer, open()
			 *	it and chain it to the sequencer with a
			 *	Transmitter-Receiver pair.
			 */
			try
			{
				Receiver loopMidiReceiver = midiOutDevice.getReceiver();
				
				Transmitter	seqTransmitter = this.sequencer.getTransmitter();
				
				seqTransmitter.setReceiver(loopMidiReceiver);
			}
			catch (MidiUnavailableException e)
			{
				LOGGER.error(e);
			}
		}

		/*
		 *	There is a bug in the Sun jdk1.3/1.4.
		 *	It prevents correct termination of the VM.
		 *	So we have to exit ourselves.
		 *	To accomplish this, we register a Listener to the Sequencer.
		 *	It is called when there are "meta" events. Meta event
		 *	47 is end of track.
		 *
		 *	Thanks to Espen Riskedal for finding this trick.
		 */
		this.sequencer.addMetaEventListener(new MetaEventListener()
			{
				public void meta(MetaMessage event)
				{
					if (event.getType() == END_OF_MIDI_TRACK)
					{
						sequencer.close();
						if (midiOutDevice != null)
						{
							midiOutDevice.close();
						}
						
						endOfMidiTrackListener.reached();
						
						// Fehler existiert auch noch in C:\Program Files (x86)\Java\jdk1.8.0_25
						System.exit(0);
					}
				}
			});		
		
		/*
		 *	Now, we can start over.
		 */
		this.sequencer.start();
	}
}
