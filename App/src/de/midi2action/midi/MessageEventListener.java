package de.midi2action.midi;

/**
 * Implementor is interested in a message. He is the listener. Listener
 * can register them with a talker as the opposite. The talker will
 * call a method to inform the listener about the event.
 * 
 * @author zaphod
 */
public interface MessageEventListener {
	public void sendMessage(String message);
}
