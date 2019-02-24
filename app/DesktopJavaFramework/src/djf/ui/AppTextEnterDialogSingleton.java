package djf.ui;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Modality;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

/**
 * This class serves to present custom text messages to the user when
 * events occur. Note that it always provides the same controls, a label
 * with a message, text editor and two buttons. 
 * 
 * @author Myungsuk Moon
 * @coauthor Richard McKenna
 */
public class AppTextEnterDialogSingleton extends Stage {
    // HERE'S THE SINGLETON OBJECT
    static AppTextEnterDialogSingleton singleton = null;
    
    // HERE ARE THE DIALOG COMPONENTS
    VBox messagePane;
    HBox buttonBox;
    Scene messageScene;
    Label messageLabel;
    Button enterButton;
    TextField textField;
    String text;
    
    /**
     * Initializes this dialog so that it can be used repeatedly
     * for all kinds of messages. Note this is a singleton design
     * pattern so the constructor is private.
     * 
     * @param owner The owner stage of this modal dialogue.
     * 
     * @param closeButtonText Text to appear on the close button.
     */
    private AppTextEnterDialogSingleton() {}
    
    /**
     * A static accessor method for getting the singleton object.
     * 
     * @return The one singleton dialog of this object type.
     */
    public static AppTextEnterDialogSingleton getSingleton() {
	if (singleton == null)
	    singleton = new AppTextEnterDialogSingleton();
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
        
        // LABEL TO DISPLAY THE CUSTOM MESSAGE
        messageLabel = new Label();

        // TEXT FIELD
        textField = new TextField();
        
        // ENTER BUTTON
        enterButton = new Button("Enter");
        enterButton.setOnAction(e->{ 
            text = textField.getText();
            AppTextEnterDialogSingleton.this.close();
        });

        // NOW ORGANIZE OUR BUTTONS        
        buttonBox = new HBox();
        buttonBox.getChildren().add(enterButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setSpacing(10);
        
        // WE'LL PUT EVERYTHING HERE
        messagePane = new VBox();
        messagePane.setAlignment(Pos.CENTER);
        messagePane.getChildren().add(messageLabel);
        messagePane.getChildren().add(textField);
        messagePane.getChildren().add(buttonBox);
        
        // MAKE IT LOOK NICE
        messagePane.setPadding(new Insets(60, 80, 60, 80));
        messagePane.setSpacing(20);

        // AND PUT IT IN THE WINDOW
        messageScene = new Scene(messagePane);
        this.setScene(messageScene);
    }
    
    /**
     * Accessor method for getting the text user typed.
     * 
     * @return String
     */
    public String getText() {
        return text;
    }
    
    public boolean clearTextField() {
        textField.clear();
        text = null;
        return true;
    }
    
    public void setTextField(String iText) {
        textField.setText(iText);
        text = iText;
    }
 
    /**
     * This method loads a custom message into the label and
     * then pops open the dialog.
     * 
     * @param title The title to appear in the dialog window.
     * 
     * @param message Message to appear inside the dialog.
     */
    public void show(String title, String message) {
	// SET THE DIALOG TITLE BAR TITLE
	setTitle(title);
        
        // SET THE MESSAGE TO DISPLAY TO THE USER
        messageLabel.setText(message);
        
        // CLEAR THE TEXT FIELD IF IT WAS FILLED PREVIOUSLY
        clearTextField();
	
	// AND OPEN UP THIS DIALOG, MAKING SURE THE APPLICATION
	// WAITS FOR IT TO BE RESOLVED BEFORE LETTING THE USER
	// DO MORE WORK.
        showAndWait();
    }
}