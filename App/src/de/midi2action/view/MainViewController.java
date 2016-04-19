package de.midi2action.view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiUnavailableException;

import org.apache.log4j.Logger;

import de.midi2action.configuration.GlobalConfiguration;
import de.midi2action.midi.MessageEventListener;
import de.midi2action.midi.MidiInDeviceCommand;

public class MainViewController {
	private final static Logger LOGGER = Logger.getLogger(MainViewController.class);

	@FXML
	private MenuItem menuItemExit;
	
	@FXML
	private MenuItem menuItemStart;
	
	@FXML
	private MenuItem menuItemStop;
	
	@FXML
	private Menu menuMidiInPort;

	final private MidiInDeviceCommand midiInDeviceCommand = new MidiInDeviceCommand();
	
	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@FXML
	private void initialize() {
		buildActionMenu();
		buildMidiInPortMenu();
	}
	
	private void buildActionMenu() {
		EventHandler<ActionEvent> exitAction = exit();
		this.menuItemExit.setOnAction(exitAction);
		
		EventHandler<ActionEvent> startAction = start();
		this.menuItemStart.setOnAction(startAction);
		
		EventHandler<ActionEvent> stopAction = stop();
		this.menuItemStop.setOnAction(stopAction);
	}

	private void buildMidiInPortMenu() {
		// alle MidiInDevices holen
		MidiDevice[] midiDevices = null;
		try {
			midiDevices = GlobalConfiguration.INSTANCE.getAllMidiInPorts();
		} catch (MidiUnavailableException e) {
			LOGGER.error(e.getMessage());
			return;
		}
		
		if (midiDevices.length < 1)
		{
			LOGGER.warn("no midi in devices found");
			return;
		}

		this.menuMidiInPort.setDisable(false);
		
		// konfiguriertes MidiInDevice holen
		MidiDevice midiInDevice = null;
		try {
			midiInDevice = GlobalConfiguration.INSTANCE.getMidiInDevice();
		} catch (MidiUnavailableException e) {
			LOGGER.warn(e.getMessage());
			return;
		}
		
		if (midiInDevice == null) {
			LOGGER.warn("no configured midi in device found");
			return;
		}
		
		addMenuItems(midiDevices, midiInDevice);
	}

	// für jedes verfügbare MidiInDevice ein CheckMenuItem hinzufügen
	private void addMenuItems(MidiDevice[] midiDevices, MidiDevice midiInDevice) {
		CheckMenuItem checkMenuItem = null;
		String midiInDeviceName = null;
		String configureMidiDeviceName = midiInDevice.getDeviceInfo().getName();
		for (int i = 0; i < midiDevices.length; i++) {
			midiInDeviceName = midiDevices[i].getDeviceInfo().getName();
			checkMenuItem = new CheckMenuItem(midiInDeviceName);
			if (configureMidiDeviceName.equals(midiInDeviceName)) {
				// das dem Konfigurierten entspricht wird angehakt
				checkMenuItem.setSelected(true);
			}
			
			this.menuMidiInPort.getItems().add(checkMenuItem);
		}
	}

	private EventHandler<ActionEvent> exit() {
        return new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {
            	LOGGER.info("user exit");
            	System.exit(0);
            }
        };
    }
	
	private EventHandler<ActionEvent> start() {
		return new EventHandler<ActionEvent>() {
			
			public void handle(ActionEvent event) {
				LOGGER.info("start listen to MIDI messages");
				// get GUI component as MessageEventListener
				MessageEventListener messageEventListener = GUI.INSTANCE.getMessageEventListener();
				// set MessageEventListener to Event Receiver
				try {
					// start has to be called first
					midiInDeviceCommand.start();
					midiInDeviceCommand.setMessageEventListener(messageEventListener);
				} catch (MidiUnavailableException e) {
					LOGGER.error(e);
				}
			}
		};
	}
	
	private EventHandler<ActionEvent> stop() {
		return new EventHandler<ActionEvent>() {
			
			public void handle(ActionEvent event) {
				LOGGER.info("stop listen to MIDI messages");
				midiInDeviceCommand.stop();
				midiInDeviceCommand.removeMessageEventListener();
			}
		};
	}
}
