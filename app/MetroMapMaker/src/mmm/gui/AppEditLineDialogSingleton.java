package mmm.gui;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.stage.Modality;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import mmm.data.mmmLine;

/**
 * This class serves to present custom text messages to the user when
 * events occur. Note that it always provides the same controls, a label
 * with a message, text editor and two buttons. 
 * 
 * @author Myungsuk Moon
 * @coauthor Richard McKenna
 */
public class AppEditLineDialogSingleton extends Stage {
    // HERE'S THE SINGLETON OBJECT
    static AppEditLineDialogSingleton singleton = null;
    
    // HERE ARE THE DIALOG COMPONENTS
    VBox messagePane;
    HBox inputBox;
    HBox buttonBox;
    Scene messageScene;
    Label messageLabel;
    Button enterButton;
    Button cancelButton;
    TextField textField;
    ColorPicker colorPicker;
    CheckBox checkBox;
    String text;
    Color color;
    boolean checked;
    boolean selection;
    
    /**
     * Initializes this dialog so that it can be used repeatedly
     * for all kinds of messages. Note this is a singleton design
     * pattern so the constructor is private.
     * 
     * @param owner The owner stage of this modal dialogue.
     * 
     * @param closeButtonText Text to appear on the close button.
     */
    private AppEditLineDialogSingleton() {}
    
    /**
     * A static accessor method for getting the singleton object.
     * 
     * @return The one singleton dialog of this object type.
     */
    public static AppEditLineDialogSingleton getSingleton() {
	if (singleton == null)
	    singleton = new AppEditLineDialogSingleton();
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
        
        // COLOR PICKER
        colorPicker = new ColorPicker();
        
        // CHECK BOX
        checkBox = new CheckBox("Circular Line");
        
        // ENTER BUTTON
        enterButton = new Button("Enter");
        enterButton.setOnAction(e->{ 
            text = textField.getText();
            color = colorPicker.getValue();
            checked = checkBox.isSelected();
            selection = true;
            AppEditLineDialogSingleton.this.close();
        });
        
        // CANCEL BUTTON
        cancelButton = new Button("Cancel");
        cancelButton.setOnAction(e->{ 
            selection = false;
            AppEditLineDialogSingleton.this.close();
        });

        // NOW ORGANIZE OUR BUTTONS  
        inputBox = new HBox();
        inputBox.getChildren().add(textField);
        inputBox.getChildren().add(colorPicker);
        inputBox.getChildren().add(checkBox);
        inputBox.setAlignment(Pos.CENTER);
        inputBox.setSpacing(10);
        buttonBox = new HBox();
        buttonBox.getChildren().add(enterButton);
        buttonBox.getChildren().add(cancelButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setSpacing(10);
        
        // WE'LL PUT EVERYTHING HERE
        messagePane = new VBox();
        messagePane.setAlignment(Pos.CENTER);
        messagePane.getChildren().add(messageLabel);
        messagePane.getChildren().add(inputBox);
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
    
    /**
     * Accessor method for getting the color user picked.
     * 
     * @return Color
     */
    public Color getColor() {
        return color;
    }
    
    /**
     * Accessor method for getting the selection on check box.
     * 
     * @return boolean
     */
    public boolean isChecked() {
        return checked;
    }
    
    /**
     * Accessor method for getting the selection the user made.
     * 
     * @return Either YES or NO, depending on which
     * button the user selected when this dialog was presented.
     */
    public boolean getSelection() {
        return selection;
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
     * 
     * @param line Line that the user selected
     */
    public void show(String title, String message, mmmLine line) {
	// SET THE DIALOG TITLE BAR TITLE
	setTitle(title);
        
        // SET THE MESSAGE TO DISPLAY TO THE USER
        messageLabel.setText(message);
	
        // CLEAR THE TEXT FIELD IF IT WAS FILLED PREVIOUSLY
        textField.setText(line.getLineName());
        
        // SET THE COLOR PICKER VALUE TO DEFAULT
        colorPicker.setValue(line.getLineColor());
        
        // SET THE CHECK BOX LABEL
        checkBox.setSelected(line.isCircular());
        
	// AND OPEN UP THIS DIALOG, MAKING SURE THE APPLICATION
	// WAITS FOR IT TO BE RESOLVED BEFORE LETTING THE USER
	// DO MORE WORK.
        showAndWait();
    }
}