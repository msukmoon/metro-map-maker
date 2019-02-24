package djf.ui;

import java.util.Iterator;
import java.util.LinkedList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author Myungsuk Moon
 * @coauthor Richard McKenna
 */
public class AppWelcomeDialogSingleton extends Stage {
    // HERE'S THE SINGLETON OBJECT
    static AppWelcomeDialogSingleton singleton = null;
    
    // HERE ARE THE DIALOG COMPONENTS
    Scene scene;
    BorderPane pane;
    VBox firstColumn;
    Label recentWorkLabel;
    LinkedList<String> recentWorks;
    VBox secondColumn;
    ImageView logo;
    Hyperlink newWork;
    Boolean isNew;
    Boolean isRecent;

    
    /**
     * Initializes this dialog so that it can be used repeatedly.
     * Note this is a singleton design
     * pattern so the constructor is private.
     * 
     * @param owner The owner stage of this modal dialoge.
     * 
     * @param closeButtonText Text to appear on the close button.
     */
    private AppWelcomeDialogSingleton() {}
    
    /**
     * A static accessor method for getting the singleton object.
     * 
     * @return The one singleton dialog of this object type.
     */
    public static AppWelcomeDialogSingleton getSingleton() {
	if (singleton == null)
	    singleton = new AppWelcomeDialogSingleton();
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
           
        // SET UP FIRST COLUMN
        firstColumn = new VBox(10);
        
        // ADD LABEL
        recentWorkLabel = new Label("Recent Work");
        firstColumn.getChildren().add(recentWorkLabel);
                
        // TEMPORARY DUMMY WORKS
        recentWorks = new LinkedList();
        recentWorks.add("Vancouver");
        recentWorks.add("Seoul");
        recentWorks.add("Tokyo");
        recentWorks.add("Incheon");
        recentWorks.add("Beijing");
        recentWorks.add("Hong Kong");
        
        // ADD LIST OF WORKS
        Iterator itr = recentWorks.iterator();
        while (itr.hasNext()) {
            Hyperlink temp = new Hyperlink((String) itr.next());
            
            // TEMPORARY
            isRecent = false;
            temp.setOnAction(e->{
                isRecent = true;
                AppWelcomeDialogSingleton.this.close();
            });   
            
            firstColumn.getChildren().add(temp);
        }
        
        // SET UP SECOND COLUMN
        secondColumn = new VBox(100);
        
        // LOGO
        // FILE NOT FOUND EXCEPTION WILL BE HANDELED BY APPTEMPLATE IN THE FRAMEWORK
        logo = new ImageView(new Image("file:./images/mmmBigLogo.png"));
        secondColumn.getChildren().add(logo);
        
        // HYPERLINK
        newWork = new Hyperlink("Create New Metro Map");
        isNew = false;
        newWork.setOnAction(e->{
            isNew = true;
            AppWelcomeDialogSingleton.this.close();
        });
        secondColumn.getChildren().add(newWork);
        
        // WE'LL PUT EVERYTHING HERE
        pane = new BorderPane();
        pane.setLeft(firstColumn);
        pane.setRight(secondColumn);
               
        // MAKE IT LOOK NICE
        logo.setFitHeight(155);
        logo.setFitWidth(470);
        firstColumn.setStyle("-fx-background-color: #d4e3f0;" +
                                "-fx-border-color: #9ea2ac;" +
                                "-fx-padding: 100 50 100 50;");
        secondColumn.setStyle("-fx-border-color: #9ea2ac;" +
                                "-fx-padding: 100 50 100 50;" +
                                "-fx-alignment: center;");
        
        // AND PUT IT IN THE WINDOW
        scene = new Scene(pane);
        this.setScene(scene);
    }
    
    /**
     * This method pops open the dialog.
     * 
     * @param title The title to appear in the dialog window.
     */
    public void show(String title) {
        // SET THE DIALOG TITLE BAR TITLE
	setTitle(title);
           
	// AND OPEN UP THIS DIALOG, MAKING SURE THE APPLICATION
	// WAITS FOR IT TO BE RESOLVED BEFORE LETTING THE USER
	// DO MORE WORK.
        showAndWait();
    }
    
    public boolean isNew() {
        return isNew;
    }
    
    // TEMPORARY CODE UNTIL FILE HANDLING FUNCTION IS ADDED
    public boolean isRecent() {
        return isRecent;
    } 
}
