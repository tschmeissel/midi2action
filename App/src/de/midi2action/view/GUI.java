package de.midi2action.view;

import java.io.IOException;

import org.apache.log4j.Logger;

import de.midi2action.MainFX;
import de.midi2action.midi.MessageEventListener;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * There shall be only one GUI so it is implemented as singleton.
 * 
 * @author zaphod
 */
final public class GUI {
	private final static Logger LOGGER = Logger.getLogger(GUI.class);
	
	private final static String APP_TITLE = "midi2action"; 
	private BorderPane rootLayout;
	private MessagesTextArea messageTextArea;
	
	private GUI() {
	}

	public static GUI INSTANCE = new GUI(); 
	
	public void build(Stage primaryStage) {
        primaryStage.setTitle(APP_TITLE);
        
        try {
            // Load the root layout from the fxml file
            FXMLLoader loader = new FXMLLoader(MainFX.class.getResource("view/MainView.fxml"));
            this.rootLayout = (BorderPane) loader.load();
            Scene scene = new Scene(this.rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            LOGGER.error(e);
        }
        
        setMessagesTextArea();
	}
	
    private void setMessagesTextArea() {
        try {
            // Load the fxml file and set into the center of the main layout
            FXMLLoader loader = new FXMLLoader(MainFX.class.getResource("view/TextAreaView.fxml"));
            AnchorPane textAreaPane = (AnchorPane) loader.load();
            
    		TextArea textArea = (TextArea)textAreaPane.getChildren().get(0);
    		this.messageTextArea = new MessagesTextArea(textArea);
    		this.messageTextArea.sendMessage("hello messages text area");
            this.rootLayout.setCenter(textAreaPane);

        } catch (IOException e) {
            LOGGER.error(e);
        }
    }

    /**
     * @return not {@code null} but always an instance of the current {@link MessageEventListener}
     */
	public MessageEventListener getMessageEventListener() {
		return this.messageTextArea;
	}
}
