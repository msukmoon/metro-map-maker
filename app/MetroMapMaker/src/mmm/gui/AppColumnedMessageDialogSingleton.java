package mmm.gui;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Modality;
import static djf.settings.AppStartupConstants.CLOSE_BUTTON_LABEL;
import javafx.scene.layout.HBox;

/**
 * This class serves to present custom text messages to the user when
 * events occur. There are two columns of message area. 
 * Note that it always provides the same controls, a label
 * with a message, and a single Ok button. 
 * 
 * @author Richard McKenna
 * @coauthor Myungsuk Moon
 * @version 1.0
 */
public class AppColumnedMessageDialogSingleton extends Stage {
    // HERE'S THE SINGLETON OBJECT
    static AppColumnedMessageDialogSingleton singleton = null;
    
    // HERE ARE THE DIALOG COMPONENTS
    HBox messageColumns;
    VBox messagePane;
    Scene messageScene;
    Label messageLabel1;
    Label messageLabel2;
    Button closeButton;
    
    /**
     * Initializes this dialog so that it can be used repeatedly
     * for all kinds of messages. Note this is a singleton design
     * pattern so the constructor is private.
     * 
     * @param owner The owner stage of this modal dialoge.
     * 
     * @param closeButtonText Text to appear on the close button.
     */
    private AppColumnedMessageDialogSingleton() {}
    
    /**
     * A static accessor method for getting the singleton object.
     * 
     * @return The one singleton dialog of this object type.
     */
    public static AppColumnedMessageDialogSingleton getSingleton() {
	if (singleton == null)
	    singleton = new AppColumnedMessageDialogSingleton();
	return singleton;
    }
    
    /**
     * This function fully initializes the singleton dialog for use.
     * 
     * @param owner The window above which this dialog will be centered.
     */
    public void init(Stage owner) {
        // MAKE IT MODAL
        initModality(Modality.WINDOW_MODAL);
        initOwner(owner);
        
        // LABELS TO DISPLAY THE CUSTOM MESSAGE
        messageLabel1 = new Label();
        messageLabel2 = new Label();

        // CLOSE BUTTON
        closeButton = new Button(CLOSE_BUTTON_LABEL);
        closeButton.setOnAction(e->{ AppColumnedMessageDialogSingleton.this.close(); });
        
        // MESSAGE COLUMNS
        messageColumns = new HBox();
        messageColumns.setAlignment(Pos.TOP_CENTER);
        messageColumns.getChildren().add(messageLabel1);
        messageColumns.getChildren().add(messageLabel2);
        
        // MAKE IT LOOK NICE
        messageColumns.setSpacing(40);

        // WE'LL PUT EVERYTHING HERE
        messagePane = new VBox();
        messagePane.setAlignment(Pos.CENTER);
        messagePane.getChildren().add(messageColumns);
        messagePane.getChildren().add(closeButton);
        
        // MAKE IT LOOK NICE
        messagePane.setPadding(new Insets(80, 60, 80, 60));
        messagePane.setSpacing(20);

        // AND PUT IT IN THE WINDOW
        messageScene = new Scene(messagePane);
        this.setScene(messageScene);
    }
 
    /**
     * This method loads a custom message into the label and
     * then pops open the dialog.
     * 
     * @param title The title to appear in the dialog window.
     * 
     * @param message1 Message to appear inside the dialog on the first column.
     * 
     * @param message2 Message to appear inside the dialog on the second column.
     */
    public void show(String title, String message1, String message2) {
	// SET THE DIALOG TITLE BAR TITLE
	setTitle(title);
	
	// SET THE MESSAGE TO DISPLAY TO THE USER
        messageLabel1.setText(message1);
        messageLabel2.setText(message2);
	
	// AND OPEN UP THIS DIALOG, MAKING SURE THE APPLICATION
	// WAITS FOR IT TO BE RESOLVED BEFORE LETTING THE USER
	// DO MORE WORK.
        showAndWait();
    }
}
