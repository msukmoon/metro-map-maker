package mmm.gui;

import djf.AppTemplate;
import djf.components.AppDataComponent;
import djf.components.AppWorkspaceComponent;
import djf.ui.AppGUI;
import static djf.ui.AppGUI.CLASS_BORDERED_PANE;
import djf.ui.AppMessageDialogSingleton;
import djf.ui.AppYesNoCancelDialogSingleton;
import mmm.data.mmmData;
import java.util.ArrayList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import jtps.jTPS;
import static mmm.css.mmmStyle.CLASS_RENDER_CANVAS;
import static mmm.css.mmmStyle.CLASS_EDIT_TOOLBAR;
import static mmm.css.mmmStyle.CLASS_EDIT_TOOLBAR_ROW;
import static mmm.css.mmmStyle.CLASS_EDIT_TOOLBAR_BUTTON;
import static mmm.css.mmmStyle.CLASS_EDIT_TOOLBAR_LABEL;
import static mmm.mmmLanguageProperty.ABOUT_ICON;
import static mmm.mmmLanguageProperty.ABOUT_TOOLTIP;
import static mmm.mmmLanguageProperty.REDO_ICON;
import static mmm.mmmLanguageProperty.REDO_TOOLTIP;
import static mmm.mmmLanguageProperty.UNDO_ICON;
import static mmm.mmmLanguageProperty.UNDO_TOOLTIP;
import static mmm.mmmLanguageProperty.PLUS_ICON;
import static mmm.mmmLanguageProperty.PLUS_TOOLTIP;
import static mmm.mmmLanguageProperty.MINUS_ICON;
import static mmm.mmmLanguageProperty.MINUS_TOOLTIP;
import static mmm.mmmLanguageProperty.EDIT_ICON;
import static mmm.mmmLanguageProperty.EDIT_TOOLTIP;
import static mmm.mmmLanguageProperty.LIST_ICON;
import static mmm.mmmLanguageProperty.LIST_TOOLTIP;
import static mmm.mmmLanguageProperty.ROTATE_ICON;
import static mmm.mmmLanguageProperty.ROTATE_TOOLTIP;
import static mmm.mmmLanguageProperty.DIRECTION_ICON;
import static mmm.mmmLanguageProperty.DIRECTION_TOOLTIP;
import static mmm.mmmLanguageProperty.BOLD_ICON;
import static mmm.mmmLanguageProperty.BOLD_TOOLTIP;
import static mmm.mmmLanguageProperty.ITALIC_ICON;
import static mmm.mmmLanguageProperty.ITALIC_TOOLTIP;
import static mmm.mmmLanguageProperty.ZOOMIN_ICON;
import static mmm.mmmLanguageProperty.ZOOMIN_TOOLTIP;
import static mmm.mmmLanguageProperty.ZOOMOUT_ICON;
import static mmm.mmmLanguageProperty.ZOOMOUT_TOOLTIP;
import static mmm.mmmLanguageProperty.INCREASE_ICON;
import static mmm.mmmLanguageProperty.INCREASE_TOOLTIP;
import static mmm.mmmLanguageProperty.DECREASE_ICON;
import static mmm.mmmLanguageProperty.DECREASE_TOOLTIP;

/**
 *
 * @author myungsuk
 */
public class mmmWorkspace extends AppWorkspaceComponent {
    // TRANSACTIONS
    static jTPS jtps;
    
    // HERE'S THE APP
    AppTemplate app;

    // IT KNOWS THE GUI IT IS PLACED INSIDE
    AppGUI gui;
    
    // HAS ALL THE CONTROLS FOR EDITING
    VBox editToolbar;    
    
    // FLOWPANE FOR UNDO AND REDO
    FlowPane urToolbar;
    Button undoButton;
    Button redoButton;    
    
    // FLOWPANE FOR SETTINGS AND ABOUT
    FlowPane saToolbar;
    Button aboutButton;
    
    // LIST FOR STORING BUTTONS AND LABELS
    ArrayList<Button> buttons;
    ArrayList<Label> labels;
    
    // FIRST ROW
    VBox row1Box;
    BorderPane row1ButtonBox1;
    Label row1Label;
    HBox row1ButtonBox2;
    ComboBox metroLine;
    HBox row1ButtonBox3;
    Button addLineButton;
    Button removeLineButton;
    Button editLineButton;
    Button addStationToLineButton;
    Button removeStationFromLineButton;
    Button listAllStationInLineButton;
    Slider changeLineThicknessSlider;

    // SECOND ROW
    VBox row2Box;
    BorderPane row2ButtonBox1;
    Label row2Label;
    HBox row2ButtonBox2;
    ComboBox metroStation;
    ColorPicker metroStationColor;
    HBox row2ButtonBox3;
    Button addStationButton;
    Button removeStationButton;
    Button snapToGridButton;
    Button moveStationLabelButton;
    Button rotateStationLabelButton;
    Slider changeStationRadiusSlider;

    // THIRD ROW
    BorderPane row3Box;
    VBox row3ButtonBox;
    ComboBox startStation;
    ComboBox endStation;
    Button findRouteButton;
    Pane temp;

    // FORTH ROW
    VBox row4Box;
    BorderPane row4ButtonBox1;
    Label row4Label;
    ColorPicker backgroundColor;
    HBox row4ButtonBox2;
    Button setImageBackgroundButton;
    Button addImageOverlayButton;
    Button addLabelButton;
    Button removeMapElementButton;

    // FIFTH ROW
    VBox row5Box;
    BorderPane row5ButtonBox1;
    Label row5Label;
    ColorPicker fontColor;
    HBox row5ButtonBox2;
    Button setBoldButton;
    Button setItalicButton;
    ComboBox fontSize;
    ComboBox fontFamily;

    // SIXTH ROW
    VBox row6Box;
    BorderPane row6ButtonBox1;
    Label row6Label;
    CheckBox showGrid;
    HBox row6ButtonBox2;
    Button zoomInButton;
    Button zoomOutButton;
    Button increaseMapSizeButton;
    Button decreaseMapSizeButton;
    
    // THIS IS WHERE WE'LL RENDER OUR DRAWING, NOTE THAT WE
    // CALL THIS A mapEditor, BUT IT'S REALLY JUST A Pane
    Pane mapEditor;

    // HERE ARE THE CONTROLLERS
    MapEditorController mapEditorController;
    mmmController mmmController;
    
    // HERE ARE OUR DIALOGS
    AppMessageDialogSingleton messageDialog;
    AppYesNoCancelDialogSingleton yesNoCancelDialog;

    // FOR DISPLAYING DEBUG STUFF
    Text debugText;
    
    /**
     * Constructor for initializing the workspace, note that this constructor
     * will fully setup the workspace user interface for use.
     *
     * @param initApp The application this workspace is part of.
     *
     * @throws IOException Thrown should there be an error loading application
     * data for setting up the user interface.
     */
    public mmmWorkspace(AppTemplate initApp) {
        // KEEP THIS FOR LATER
        app = initApp;
        
        // TRANSACTIONS
        jtps = app.getJTPS();

        // KEEP THE GUI FOR LATER
        gui = app.getGUI();

        // LAYOUT THE APP
        initLayout();

        // HOOK UP THE CONTROLLERS
        initControllers();

        // AND INIT THE STYLE FOR THE WORKSPACE
        initStyle();
    }
    
    // HELPER SETUP METHOD
    private void initLayout() {
        
        // CREATE A TOOLBAR FOR UNDO AND REDO
        urToolbar = new FlowPane();
        urToolbar.getStyleClass().add(CLASS_BORDERED_PANE);
        
        // THEN ADD BUTTONS TO THE TOOLBAR
        undoButton = gui.initChildButton(urToolbar, UNDO_ICON.toString(), UNDO_TOOLTIP.toString(), false);
        redoButton = gui.initChildButton(urToolbar, REDO_ICON.toString(), REDO_TOOLTIP.toString(), false);
        
        // THEN PUT IT ON THE TOP TOOLBAR
        gui.getTopToolbarPane().getChildren().add(urToolbar);
        urToolbar.setAlignment(Pos.CENTER);
        urToolbar.setHgap(1);
        
        // CREATE A TOOLBAR FOR THE SETTINGS AND ABOUT
        saToolbar = new FlowPane();
        saToolbar.getStyleClass().add(CLASS_BORDERED_PANE);

        // THEN ADD BUTTONS TO THE SETTINGS AND ABOUT
        aboutButton = gui.initChildButton(saToolbar, ABOUT_ICON.toString(), ABOUT_TOOLTIP.toString(), false);

        // THEN PUT IT ON THE TOP TOOLBAR
        gui.getTopToolbarPane().getChildren().add(saToolbar);
        saToolbar.setAlignment(Pos.CENTER);
        saToolbar.setHgap(1);
        
        // THIS WILL GO IN THE LEFT SIDE OF THE WORKSPACE
        editToolbar = new VBox();
     
        // LIST FOR STORING BUTTONS AND LABELS
        buttons = new ArrayList();
        labels = new ArrayList();
        
        // FIRST ROW
        row1Box = new VBox();
        row1ButtonBox1 = new BorderPane();
        row1Label = new Label("Metro Lines");
        row1ButtonBox2 = new HBox();
        metroLine = new ComboBox();
        metroLine.setPromptText("Line Name");
        row1ButtonBox3 = new HBox();
        addStationToLineButton = new Button("Add\nStation");
        removeStationFromLineButton = new Button("Remove\nStation");
        changeLineThicknessSlider = new Slider();
        
        row1ButtonBox1.setLeft(row1Label);
        row1ButtonBox2.getChildren().add(metroLine);
        row1ButtonBox1.setRight(row1ButtonBox2);
        addLineButton = gui.initChildButton(row1ButtonBox3, PLUS_ICON.toString(), PLUS_TOOLTIP.toString(), false);
        removeLineButton = gui.initChildButton(row1ButtonBox3, MINUS_ICON.toString(), MINUS_TOOLTIP.toString(), false);
        editLineButton = gui.initChildButton(row1ButtonBox3, EDIT_ICON.toString(), EDIT_TOOLTIP.toString(), false);
        row1ButtonBox3.getChildren().add(addStationToLineButton);
        row1ButtonBox3.getChildren().add(removeStationFromLineButton);
        listAllStationInLineButton = gui.initChildButton(row1ButtonBox3, LIST_ICON.toString(), LIST_TOOLTIP.toString(), false);
        row1Box.getChildren().add(row1ButtonBox1);
        row1Box.getChildren().add(row1ButtonBox3);
        row1Box.getChildren().add(changeLineThicknessSlider);
            
        row1Box.setSpacing(5);
        row1ButtonBox2.setSpacing(5);
        row1ButtonBox3.setSpacing(5);
        row1ButtonBox3.setAlignment(Pos.CENTER);
        
        buttons.add(addLineButton);
        buttons.add(removeLineButton);
        buttons.add(editLineButton);
        buttons.add(addStationToLineButton);
        buttons.add(removeStationFromLineButton);
        buttons.add(listAllStationInLineButton);
        labels.add(row1Label);
        
        // SECOND ROW
        row2Box = new VBox();
        row2ButtonBox1 = new BorderPane();
        row2Label = new Label("Metro Stations ");
        row2ButtonBox2 = new HBox();
        metroStation = new ComboBox();
        metroStation.setPromptText("Station Name");
        metroStationColor = new ColorPicker();
        row2ButtonBox3 = new HBox();
        snapToGridButton = new Button("Snap");
        moveStationLabelButton = new Button("Move\nLabel");
        changeStationRadiusSlider = new Slider();

        row2ButtonBox1.setLeft(row2Label);
        row2ButtonBox2.getChildren().add(metroStation);
        row2ButtonBox2.getChildren().add(metroStationColor);
        row2ButtonBox1.setRight(row2ButtonBox2);
        addStationButton = gui.initChildButton(row2ButtonBox3, PLUS_ICON.toString(), PLUS_TOOLTIP.toString(), false);
        removeStationButton = gui.initChildButton(row2ButtonBox3, MINUS_ICON.toString(), MINUS_TOOLTIP.toString(), false);
        row2ButtonBox3.getChildren().add(snapToGridButton);
        row2ButtonBox3.getChildren().add(moveStationLabelButton);
        rotateStationLabelButton = gui.initChildButton(row2ButtonBox3, ROTATE_ICON.toString(), ROTATE_TOOLTIP.toString(), false);
        row2Box.getChildren().add(row2ButtonBox1);
        row2Box.getChildren().add(row2ButtonBox3);
        row2Box.getChildren().add(changeStationRadiusSlider);
        
        row2Box.setSpacing(5);
        row2ButtonBox2.setSpacing(5);
        row2ButtonBox3.setSpacing(5);
        row2ButtonBox3.setAlignment(Pos.CENTER);
        
        buttons.add(addStationButton);
        buttons.add(removeStationButton);
        buttons.add(snapToGridButton);
        buttons.add(moveStationLabelButton);
        buttons.add(rotateStationLabelButton);
        labels.add(row2Label);       
        
        // THIRD ROW
        row3Box = new BorderPane();
        row3ButtonBox = new VBox();
        startStation = new ComboBox();
        startStation.setPromptText("From");                
        endStation = new ComboBox();
        endStation.setPromptText("To");                
        
        row3ButtonBox.getChildren().add(startStation);
        row3ButtonBox.getChildren().add(endStation);
        row3Box.setLeft(row3ButtonBox);
        temp = new Pane();
        findRouteButton = gui.initChildButton(temp, DIRECTION_ICON.toString(), DIRECTION_TOOLTIP.toString(), false);
        temp.getChildren().removeAll();
        row3Box.setRight(findRouteButton);
                     
        row3ButtonBox.setSpacing(5);
        
        buttons.add(findRouteButton);        
       
        // FORTH ROW
        row4Box = new VBox();
        row4ButtonBox1 = new BorderPane();        
        row4Label = new Label("Decor");
        backgroundColor = new ColorPicker();
        row4ButtonBox2 = new HBox();
        setImageBackgroundButton = new Button("Set Image\nBackground");
        addImageOverlayButton = new Button("Add\nImage");
        addLabelButton = new Button("Add\nLabel");
        removeMapElementButton = new Button("Remove\nElement");

        row4ButtonBox1.setLeft(row4Label);
        row4ButtonBox1.setRight(backgroundColor);        
        row4ButtonBox2.getChildren().add(setImageBackgroundButton);
        row4ButtonBox2.getChildren().add(addImageOverlayButton);
        row4ButtonBox2.getChildren().add(addLabelButton);
        row4ButtonBox2.getChildren().add(removeMapElementButton);
        row4Box.getChildren().add(row4ButtonBox1);
        row4Box.getChildren().add(row4ButtonBox2);
        
        row4Box.setSpacing(5);
        row4ButtonBox2.setSpacing(5);
        row4ButtonBox2.setAlignment(Pos.CENTER);
        
        buttons.add(setImageBackgroundButton);
        buttons.add(addImageOverlayButton);
        buttons.add(addLabelButton);
        buttons.add(removeMapElementButton); 
        labels.add(row4Label);        
        
        // FIFTH ROW
        row5Box = new VBox();
        row5ButtonBox1 = new BorderPane();        
        row5Label = new Label("Font");
        fontColor = new ColorPicker(Color.BLACK);
        row5ButtonBox2 = new HBox();
        fontSize = new ComboBox();
        fontSize.setPromptText("Font Size");
        fontSize.getItems().addAll(
            6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 22, 
            24, 26, 28, 30, 34, 38, 42, 46, 50, 60, 70, 80, 100, 150, 200);        
        fontFamily = new ComboBox();
        fontFamily.setPromptText("Font Family");
        fontFamily.getItems().addAll(
            "Arial",
            "Courier New",
            "Georgia",
            "Helvetica",
            "Trebuchet MS",
            "Times New Roman",
            "Verdana");
        
        row5ButtonBox1.setLeft(row5Label);
        row5ButtonBox1.setRight(fontColor);
        setBoldButton = gui.initChildButton(row5ButtonBox2, BOLD_ICON.toString(), BOLD_TOOLTIP.toString(), false);
        setItalicButton = gui.initChildButton(row5ButtonBox2, ITALIC_ICON.toString(), ITALIC_TOOLTIP.toString(), false);
        row5ButtonBox2.getChildren().add(fontSize);
        row5ButtonBox2.getChildren().add(fontFamily);
        row5Box.getChildren().add(row5ButtonBox1);
        row5Box.getChildren().add(row5ButtonBox2);
        
        row5Box.setSpacing(5);
        row5ButtonBox2.setSpacing(5); 
        row5ButtonBox2.setAlignment(Pos.CENTER);
        
        buttons.add(setBoldButton);        
        buttons.add(setItalicButton); 
        labels.add(row5Label);       
             
        // SIXTH ROW
        row6Box = new VBox();
        row6ButtonBox1 = new BorderPane();        
        row6Label = new Label("Navigation");
        showGrid = new CheckBox("Show Grid");
        row6ButtonBox2 = new HBox();
        
        row6ButtonBox1.setLeft(row6Label);
        row6ButtonBox1.setRight(showGrid);
        zoomInButton = gui.initChildButton(row6ButtonBox2, ZOOMIN_ICON.toString(), ZOOMIN_TOOLTIP.toString(), false);
        zoomOutButton = gui.initChildButton(row6ButtonBox2, ZOOMOUT_ICON.toString(), ZOOMOUT_TOOLTIP.toString(), false);
        increaseMapSizeButton = gui.initChildButton(row6ButtonBox2, INCREASE_ICON.toString(), INCREASE_TOOLTIP.toString(), false);
        decreaseMapSizeButton = gui.initChildButton(row6ButtonBox2, DECREASE_ICON.toString(), DECREASE_TOOLTIP.toString(), false);
        row6Box.getChildren().add(row6ButtonBox1);
        row6Box.getChildren().add(row6ButtonBox2);
        
        row6Box.setSpacing(5);
        row6ButtonBox2.setSpacing(5); 
        row6ButtonBox2.setAlignment(Pos.CENTER);
        
        buttons.add(zoomInButton); 
        buttons.add(zoomOutButton); 
        buttons.add(increaseMapSizeButton); 
        buttons.add(decreaseMapSizeButton);  
        labels.add(row6Label);        
        
        // NOW ORGANIZE THE EDIT TOOLBAR
        editToolbar.getChildren().add(row1Box);
        editToolbar.getChildren().add(row2Box);
        editToolbar.getChildren().add(row3Box);
        editToolbar.getChildren().add(row4Box);
        editToolbar.getChildren().add(row5Box);
        editToolbar.getChildren().add(row6Box);
       
        // WE'LL RENDER OUR STUFF HERE IN THE CANVAS
        mapEditor = new Pane();
        debugText = new Text();
        mapEditor.getChildren().add(debugText);
        debugText.setX(100);
        debugText.setY(100);
        
        // AND MAKE SURE THE DATA MANAGER IS IN SYNC WITH THE PANE
        mmmData data = (mmmData) app.getDataComponent();
        data.setShapes(mapEditor.getChildren());

        // AND NOW SETUP THE WORKSPACE
        workspace = new BorderPane();
        ((BorderPane) workspace).setCenter(mapEditor);
        ((BorderPane) workspace).setLeft(editToolbar);
        
        // ADD BORDER TO THE MAP EDITOR. THE SIZE OF THE MAP WILL MATCH WITH THE SIZE OF THE RECTANGLE.
        mapEditor.setClip(new Rectangle(800, 600));
        data.setMapWidth(800);
        data.setMapHeight(600);
       
        // SET DEFAULT SCALE FOR THE MAP
        data.setMapScaleX(1);
        data.setMapScaleY(1);
    }
    
    // HELPER SETUP METHOD
    private void initControllers() {
        // MAKE THE MMM CONTROLLER
        mmmController = new mmmController(app);
        
        // THEN CONNECT BUTTONS TO ITS HANDLERS
        undoButton.setOnAction(e -> {
            jtps.undoTransaction();
        });
        redoButton.setOnAction(e -> {
            jtps.doTransaction();
        });
        aboutButton.setOnAction(e -> {
            mmmController.processAbout();
        });
        
        metroLine.setOnAction(e -> {
            String selection = (String) metroLine.getValue();
            mmmController.processMetroLineSelection(selection);
        });
        
        addLineButton.setOnAction(e -> {
            mmmController.processAddLine();
        });
        
        removeLineButton.setOnAction(e -> {
            mmmController.processRemoveLine();
        });
        
        editLineButton.setOnAction(e -> {
            mmmController.processEditLine();
        });
        
        listAllStationInLineButton.setOnAction(e -> {
            mmmController.processListAllStationInLine();
        });
        
        changeLineThicknessSlider.valueProperty().addListener(e -> {
            mmmController.processChangeLineThickness();
        });
        
        addStationToLineButton.setOnAction(e -> {
            mmmController.processAddStationToLine();
        });
        
        removeStationFromLineButton.setOnAction(e -> {
            mmmController.processRemoveStationFromLine();
        });
        
        metroStation.setOnAction(e -> {
            String selection = (String) metroStation.getValue();
            mmmController.processMetroStationSelection(selection);
        });
        
        addStationButton.setOnAction(e -> {
            mmmController.processAddStation();
        });        

        removeStationButton.setOnAction(e -> {
            mmmController.processRemoveStation();
        });
               
        snapToGridButton.setOnAction(e -> {
            mmmController.processSnapToGrid();
        });
        
        moveStationLabelButton.setOnAction(e -> {
            mmmController.processMoveStationLabel();
        });
        
        rotateStationLabelButton.setOnAction(e -> {
            mmmController.processRotateStationLabel();
        });
        
        metroStationColor.setOnAction(e -> {
            Color selection = metroStationColor.getValue();
            mmmController.processChangeStationFillColor(selection);
        });
        
        changeStationRadiusSlider.valueProperty().addListener(e -> {
            mmmController.processChangeStationRadius();
        });
        
        findRouteButton.setOnAction(e -> {
            mmmController.processFindRoute();
        });
        
        backgroundColor.setOnAction(e -> {
            Color selection = backgroundColor.getValue();
            mmmController.processSetBackgroundColor(selection);
        });
        
        setImageBackgroundButton.setOnAction(e -> {
            mmmController.processSetImageBackground();
        });
        
        addImageOverlayButton.setOnAction(e -> {
            mmmController.processAddImageOverlay();
        });
        
        addLabelButton.setOnAction(e -> {
            mmmController.processAddLabel();
        });
        
        removeMapElementButton.setOnAction(e -> {
            mmmController.processRemoveMapElement();
        });
        
        fontColor.setOnAction(e -> {
            Color selection = fontColor.getValue();
            mmmController.processFontColorSelection(selection);
        });
        
        setBoldButton.setOnAction(e -> {
            mmmController.processSetBold();
        });
        
        setItalicButton.setOnAction(e -> {
            mmmController.processSetItalic();
        });
        
        fontSize.setOnAction(e -> {
            int selection = (int) fontSize.getValue();
            mmmController.processFontSizeSelection(selection);
        });
        
        fontFamily.setOnAction(e -> {
            String selection = (String) fontFamily.getValue();
            mmmController.processFontFamilySelection(selection);
        });
        
        zoomInButton.setOnAction(e -> {
            mmmController.processZoomIn();
        });
        
        zoomOutButton.setOnAction(e -> {
            mmmController.processZoomOut();
        });
        
        increaseMapSizeButton.setOnAction(e -> {
            mmmController.processIncreaseMapSize();
        });
        
        decreaseMapSizeButton.setOnAction(e -> {
            mmmController.processDecreaseMapSize();
        });
        
        showGrid.setOnAction(e -> {
            boolean selected = showGrid.isSelected();
            mmmController.processShowGrid(selected);
        });
              
        // MAKE THE MAPEDITOR CONTROLLER	
        mapEditorController = new MapEditorController(app);
        mapEditor.setOnMousePressed(e -> {
            mapEditorController.processMapEditorMousePress((int) e.getX(), (int) e.getY());
        });
        mapEditor.setOnMouseReleased(e -> {
            mapEditorController.processMapEditorMouseRelease((int) e.getX(), (int) e.getY());
        });
        mapEditor.setOnMouseDragged(e -> {
            mapEditorController.processMapEditorMouseDragged((int) e.getX(), (int) e.getY());
        });
        gui.getPrimaryScene().setOnKeyPressed(e -> {
            mapEditorController.processMapEditorKeyPressed(e.getText());
        });
        
    }
    
    /**
     * This function specifies the CSS style classes for all the UI components
     * known at the time the workspace is initially constructed. Note that the
     * tag editor controls are added and removed dynamicaly as the application
     * runs so they will have their style setup separately.
     */
    public void initStyle() {
        // NOTE THAT EACH CLASS SHOULD CORRESPOND TO
        // A STYLE CLASS SPECIFIED IN THIS APPLICATION'S
        // CSS FILE
        mapEditor.getStyleClass().add(CLASS_RENDER_CANVAS);

        editToolbar.getStyleClass().add(CLASS_EDIT_TOOLBAR);
        row1Box.getStyleClass().add(CLASS_EDIT_TOOLBAR_ROW);
        row2Box.getStyleClass().add(CLASS_EDIT_TOOLBAR_ROW);
        row3Box.getStyleClass().add(CLASS_EDIT_TOOLBAR_ROW);
        row4Box.getStyleClass().add(CLASS_EDIT_TOOLBAR_ROW);
        row5Box.getStyleClass().add(CLASS_EDIT_TOOLBAR_ROW);
        row6Box.getStyleClass().add(CLASS_EDIT_TOOLBAR_ROW);
        
        // APPLY STYLE TO ALL BUTTONS
        for (Button b : buttons) {
            b.getStyleClass().add(CLASS_EDIT_TOOLBAR_BUTTON);
        }
        
        // APPLY STYLE TO ALL LABELS
        for (Label l : labels) {
            l.getStyleClass().add(CLASS_EDIT_TOOLBAR_LABEL);
        }
        
        // WIDTH AND HEIGHT ADJUSTMENT
        startStation.setMaxWidth(Double.MAX_VALUE);
        endStation.setMaxWidth(Double.MAX_VALUE);
        findRouteButton.setMaxHeight(Double.MAX_VALUE);  
        fontSize.setMaxHeight(Double.MAX_VALUE);
        fontFamily.setMaxHeight(Double.MAX_VALUE);
        
        // SET THE MAP EDITOR WHILE INITIALLY
        mapEditor.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
    }
       
    @Override
    public void resetWorkspace() {
        // WE ARE NOT USING THIS, THOUGH YOU MAY IF YOU LIKE
    }

    @Override
    public void reloadWorkspace(AppDataComponent dataComponent) {
        // NOT SUPPORTED YET
    }
    
    /**
     * Note that this is for displaying text during development.
     */
    public void setDebugText(String text) {
        debugText.setText(text);
    }
    
     public Pane getMapEditor() {
        return mapEditor;
    }
    
    public ComboBox getMetroLineComboBox() {
        return metroLine;
    }
    
    public Slider getChangeLineThicknessSlider() {
        return changeLineThicknessSlider;
    }

    public ComboBox getMetroStationComboBox() {
        return metroStation;
    }
    
    public ColorPicker getMetroStationColorPicker() {
        return metroStationColor;
    }
    
    public Slider getChangeStationRadiusSlider() {
        return changeStationRadiusSlider;
    }
    
    public ComboBox getStartStationComboBox() {
        return startStation;
    }
    
    public ComboBox getEndStationComboBox() {
        return endStation;
    }
  
    public ColorPicker getBackgroundColorPicker() {
        return backgroundColor;
    }
    
    public ColorPicker getFontColorPicker() {
        return fontColor;
    }
    
    public ComboBox getFontSizeComboBox() {
        return fontSize;
    }
    
    public ComboBox getFontFamilyComboBox() {
        return fontFamily;
    }
        
}
