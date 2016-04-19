package de.midi2action.view;

import javafx.scene.control.TextArea;
import de.midi2action.midi.EventReceiver;
import de.midi2action.midi.MessageEventListener;

/**
 * Decorates the {@link TextArea} meant for receiving and showing message events.
 * It also implements the interface {@link MessageEventListener} so it can be
 * registered at {@link EventReceiver} as a listener for messages.
 * 
 * @author zaphod
 */
final public class MessagesTextArea implements MessageEventListener {
	private final TextArea textArea;
	
	public MessagesTextArea(TextArea textArea) {
		this.textArea = textArea;
	}

	@Override
	public void sendMessage(String message) {
		this.textArea.appendText(message);
		this.textArea.appendText("\n");
	}
}
