package de.midi2action.midi;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Handles certain type of actions according to a MIDI event.
 * 
 * @author zaphod
 *
 */
final class MidiActionListener {
	 private final static Logger LOGGER = Logger.getLogger(MidiActionListener.class);

	static int counter = 1;
	
	private final List<String> images = new ArrayList<String>();
	
	public MidiActionListener() {
		for (int i = 0; i < 7; i++)
		{
			File file = new File ("images\\00" + (i + 1) + ".jpg");
			images.add(file.getAbsoluteFile().toString());
		}
	}
	
	void performAction(int actionNumber) {
		LOGGER.debug("perform midi action: " + actionNumber);
		
		List<String> args = new ArrayList<String>();
		args.add ("C:\\Program Files (x86)\\IrfanView\\i_view32.exe"); // command name
		String image = this.images.get(counter - 1); 
		LOGGER.debug("loading image: " + image);
		args.add(image);
		
		counter++;
		if (counter == 7)
		{
			counter = 1;
		}
		
		try {
			new ProcessBuilder (args).start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
