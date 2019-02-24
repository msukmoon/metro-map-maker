package mmm.data;

import djf.AppTemplate;
import djf.components.AppDataComponent;
import static djf.settings.AppPropertyType.LOAD_ERROR_MESSAGE;
import static djf.settings.AppPropertyType.LOAD_ERROR_TITLE;
import static djf.settings.AppStartupConstants.FILE_PROTOCOL;
import static djf.settings.AppStartupConstants.PATH_IMAGES;
import djf.ui.AppMessageDialogSingleton;
import djf.ui.AppTextEnterDialogSingleton;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import javafx.collections.ObservableList;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import jtps.jTPS;
import mmm.gui.AppColumnedMessageDialogSingleton;
import mmm.gui.AppEditLineDialogSingleton;
import mmm.gui.AppTextColorEnterDialogSingleton;
import mmm.gui.AppYesNoDialogSingleton;
import mmm.gui.mmmWorkspace;
import mmm.jtps.AddLineTransaction;
import mmm.jtps.AddStationToLineTransaction;
import mmm.jtps.AddStationTransaction;
import mmm.jtps.RemoveStationFromLineTransaction;
import static mmm.mmmLanguageProperty.GRID_IMAGE;
import properties_manager.PropertiesManager;

/**
 *
 * @author myungsuk
 */
public class mmmData implements AppDataComponent {
    // TRANSACTIONS
    static jTPS jtps;
    
    // FIRST THE THINGS THAT HAVE TO BE SAVED TO FILES
    
    // THESE ARE THE SHAPES TO DRAW
    ObservableList<Node> shapes;
    
    // DATA STRUCTURE TO SAVE METRO
    HashMap<String, mmmLine> lines;
    HashMap<String, DraggableStation> stations;
      
    // THIS IS THE SHAPE CURRENTLY SELECTED
    Shape selectedShape;
    
    // IMAGE FILE STORED FOR IMAGE OVERLAYS
    File imageOverlayFile;
    
    // BACKGROUND FILL AND IMAGE
    BackgroundFill[] backgroundFills;
    BackgroundImage[] backgroundImages;
    
    // CURRENT STATE OF THE APP
    mmmState state;
    
    // THIS IS A SHARED REFERENCE TO THE APPLICATION
    AppTemplate app;
    
    // USE THIS WHEN THE SHAPE IS SELECTED
    Effect highlightedEffect;
       
    // MAP SIZE
    double mapWidth;
    double mapHeight;
    
    // MAP SCALE
    double mapScaleX;
    double mapScaleY;
    
    /**
     * This constructor creates the data manager and sets up the
     *
     *
     * @param initApp The application within which this data manager is serving.
     */
    public mmmData(AppTemplate initApp) {
        // KEEP THE APP FOR LATER
        app = initApp;
        
        // TRANSACTIONS
        jtps = app.getJTPS();
             
        // DATA STRUCTURE TO SAVE METRO
        lines = new HashMap();
        stations = new HashMap();
        
        // INITIALIZE ARRAYS FOR BACKGROUND
        backgroundFills = new BackgroundFill[1];
        backgroundFills[0] = new BackgroundFill(Color.WHITE, null, null);
        backgroundImages = new BackgroundImage[2];
        
        // NO SHAPE STARTS OUT AS SELECTED
        selectedShape = null;  
        
       // THIS IS FOR THE SELECTED SHAPE
        DropShadow dropShadowEffect = new DropShadow();
        dropShadowEffect.setOffsetX(0.0f);
        dropShadowEffect.setOffsetY(0.0f);
        dropShadowEffect.setSpread(0.75);
        dropShadowEffect.setColor(Color.LIGHTSKYBLUE);
        dropShadowEffect.setBlurType(BlurType.GAUSSIAN);
        dropShadowEffect.setRadius(15);
        highlightedEffect = dropShadowEffect;
    }
    
    public ObservableList<Node> getShapes() {
        return shapes;
    }
    
    public void setShapes(ObservableList<Node> initShapes) {
        shapes = initShapes;
    }
    
    public void removeSelectedShape() {
        if (selectedShape != null) {
            shapes.remove(selectedShape);
            selectedShape = null;
        }
    }

    public void unhighlightShape(Shape shape) {
        selectedShape.setEffect(null);
    }

    public void highlightShape(Shape shape) {
        shape.setEffect(highlightedEffect);
    }
    
    public void metroLineSelection(String selection) {
        // GET THE WORKSPACE
        mmmWorkspace workspace = (mmmWorkspace) app.getWorkspaceComponent();
        
        // GET LINE FROM THE MAP
        mmmLine line = lines.get(selection);
        
        // UPDATE THE WORKSPACE
        if (line != null) {
            // GET WIDTH OF THE LINE
            double width = 0;
            Shape s = line.getShapes().get(1);
            if (s instanceof Line) {
                width = ((Line) s).getStrokeWidth() * 5;
            }

            // UPDATE THE SLIDER
            workspace.getChangeLineThicknessSlider().setValue(width);
        }
    }
    
    public void addLine(int x, int y) {        
        // GET THE WORKSPACE
        mmmWorkspace workspace = (mmmWorkspace) app.getWorkspaceComponent();
        
        // GET THE DIALOG SINGLETON
        AppTextColorEnterDialogSingleton dialog = AppTextColorEnterDialogSingleton.getSingleton();
        
        if (!lines.containsKey(dialog.getText())) {
            AddLineTransaction cT = new AddLineTransaction(app, x, y);
            jtps.addTransaction(cT);
        }
        else {
            // GET THE DIALOG SINGLETON
            AppMessageDialogSingleton messageDialog = AppMessageDialogSingleton.getSingleton();

            // CHANGE THE CURSOR
            Scene scene = app.getGUI().getPrimaryScene();
            scene.setCursor(Cursor.DEFAULT);

            // GO INTO SHAPE SIZING MODE
            state = mmmState.SELECTING_MODE;
            
            // POP UP THE DIALOG
            messageDialog.show("Failed to Add Line", 
                    "The line with same name already exists. Please choose another name.");
        }
        
    }
    
    public void removeLine() {
        // GET THE WORKSPACE
        mmmWorkspace workspace = (mmmWorkspace) app.getWorkspaceComponent();
        
        // GET THE DIALOG SINGLETON
        AppYesNoDialogSingleton dialog = AppYesNoDialogSingleton.getSingleton();
        
        // IF THE USER SAID YES
        if (dialog.getSelection()) {
            // UNHIGHLIGHT ANY SHAPE SELECTED PREVIOUSLY
            if (selectedShape != null) {
                unhighlightShape(selectedShape);
                selectedShape = null;
            }

            // GET SELECTION FROM THE COMBOBOX THEN GET LINE
            String selection = (String) workspace.getMetroLineComboBox().getValue();
            mmmLine line = lines.get(selection);
                                 
            // THEN REMOVE
            if (selection != null || line != null) {
                // REMOVE FROM COMBOBOX AND MAP
                workspace.getMetroLineComboBox().getItems().remove(selection);
                lines.remove(selection);

                // GET THE LIST FROM THE LINE
                LinkedList<Shape> list = line.getShapes();

                // REMOVE LINES ONLY USING THE ITERATOR
                Iterator<Shape> itr = list.iterator();
                while (itr.hasNext()) {
                    Shape s = itr.next();
                    if (s instanceof Line || s instanceof DraggableEnd) {
                        itr.remove();
                        shapes.remove(s);
                    }
                }
            }
        }
    }
    
    public void editLine() {
        // GET THE WORKSPACE
        mmmWorkspace workspace = (mmmWorkspace) app.getWorkspaceComponent();
        
        // GET THE DIALOG SINGLETON
        AppEditLineDialogSingleton dialog = AppEditLineDialogSingleton.getSingleton();
        
        // GET SELECTION FROM THE COMBOBOX
        String selectedLine = (String) workspace.getMetroLineComboBox().getValue();

        // GET LINE FROM THE MAP
        mmmLine line = lines.get(selectedLine);
        
        // SHOW THE DIALOG
        if (line != null) {
            dialog.show("Edit Line", "Make changes to the selected line:", line);
            
            // CHECK FOR DUPLICATE LINE NAMES
            boolean duplicateName = false;
            if (!selectedLine.equals(dialog.getText())) {
                duplicateName = lines.containsKey(dialog.getText());
            }
            
            // IF THE USER SAID YES, MAKE CHANGES
            if (dialog.getSelection() && !duplicateName) {
                // MAKE CHANGE TO THE LINE NAME
                if (!selectedLine.equals(dialog.getText())) {
                    // SAVE NAMES
                    String newName = dialog.getText();

                    // GET LINE ENDS
                    Shape start = line.getShapes().getFirst();
                    Shape end = line.getShapes().getLast();
                    
                    // MAKE CHANGES
                    if (start instanceof DraggableEnd && end instanceof DraggableEnd) {
                        ((DraggableEnd) start).setText(newName);
                        ((DraggableEnd) end).setText(newName);
                        line.setLineName(newName);
                    }
                    
                    // MAKE CHANGES TO THE MAP
                    lines.remove(selectedLine);
                    lines.put(newName, line);
                    
                    // SYNC WITH WORKSPACE
                    workspace.getMetroLineComboBox().getItems().remove(selectedLine);
                    workspace.getMetroLineComboBox().getItems().add(newName);
                    workspace.getMetroLineComboBox().setValue(newName);
                }

                // MAKE CHANGE TO THE LINE COLOR
                if (!line.getLineColor().equals(dialog.getColor())) {
                    // GET NEW COLOR
                    Color newColor = dialog.getColor();
                    
                    // MAKE CHANGES
                    for (Shape s : line.getShapes()) {
                        if (s instanceof Line) {
                            ((Line) s).setStroke(newColor);
                        }
                    }
                    line.setLineColor(newColor);
                }

                // MAKE CHANGE TO THE CIRCULAR LINE VALUE
                if (dialog.isChecked() && !line.isCircular() && line.getShapes().size() > 4) {
                    // DELETE SHAPES
                    Shape endText = line.getShapes().pollLast();
                    Shape endLine = line.getShapes().pollLast();
                    shapes.remove(endText);
                    shapes.remove(endLine);
                    
                    // THEN BIND END STATION WITH STARTING 
                    Shape startText = line.getShapes().getFirst();
                    Shape startLine = line.getShapes().get(1);
                    Shape endStation = line.getShapes().getLast();
                    
                    if (startText instanceof DraggableEnd && startLine instanceof Line &&
                            endStation instanceof DraggableStation) {
                        ((Line) startLine).startXProperty().unbind();
                        ((Line) startLine).startYProperty().unbind();
                        ((Line) startLine).startXProperty().bind(((DraggableStation) endStation).centerXProperty());
                        ((Line) startLine).startYProperty().bind(((DraggableStation) endStation).centerYProperty());
                        ((DraggableEnd) startText).xProperty().bind(((DraggableStation) endStation).centerXProperty().add(20));
                        ((DraggableEnd) startText).yProperty().bind(((DraggableStation) endStation).centerYProperty().add(20));
                        
                        shapes.remove(endStation);
                        shapes.add(endStation);
                        line.setCircular(true);
                        ((DraggableEnd) startText).setCircular(true);
                    }    
                }
                
                else if (!dialog.isChecked() && line.isCircular() && line.getShapes().size() > 4) {
                    // GET SHAPES
                    Shape startText = line.getShapes().getFirst();
                    Shape startLine = line.getShapes().get(1);
                    Shape endStation = line.getShapes().getLast();
                    
                    // UNBIND AND BIND SHAPES
                    if (startText instanceof DraggableEnd && startLine instanceof Line) {
                        ((DraggableEnd) startText).xProperty().unbind();
                        ((DraggableEnd) startText).yProperty().unbind();
                        ((Line) startLine).startXProperty().unbind();
                        ((Line) startLine).startYProperty().unbind();
                        ((Line) startLine).startXProperty().bind(((DraggableEnd) startText).xProperty());
                        ((Line) startLine).startYProperty().bind(((DraggableEnd) startText).yProperty().add(20));
                        ((DraggableEnd) startText).setCircular(false);
                    }
                    
                    // CREATE NEW SHAPES
                    Line endLine = new Line();
                    endLine.setStroke(dialog.getColor());
                    endLine.setStrokeWidth(5);
                    DraggableEnd endText = null;
                    if (startText instanceof DraggableEnd && endStation instanceof DraggableStation) {
                        double x = ((DraggableStation) endStation).getCenterX();
                        double y = ((DraggableStation) endStation).getCenterY() + 100;
                        endText = new DraggableEnd(x, y);
                        endText.setText(((DraggableEnd) startText).getText());
                        endText.setFont(Font.font("Helvetica"));
                        endText.setTextOrigin(VPos.CENTER);     
                    }
                                   
                    // BIND NEW SHAPES
                    if (endStation instanceof DraggableStation) {
                        endLine.startXProperty().bind(((DraggableStation) endStation).centerXProperty());
                        endLine.startYProperty().bind(((DraggableStation) endStation).centerYProperty());
                        endLine.endXProperty().bind(endText.xProperty());
                        endLine.endYProperty().bind(endText.yProperty().subtract(20));
                        
                        line.getShapes().add(endLine);
                        line.getShapes().add(endText);
                        shapes.add(endLine);
                        shapes.remove(endStation);
                        shapes.add(endStation);
                        shapes.add(endText);
                        ((DraggableEnd) startText).setCircular(false);
                        endText.setCircular(false);
                        line.setCircular(false);
                    }            
                }
            }
            else if (duplicateName) {
                // GET THE DIALOG SINGLETON
                AppMessageDialogSingleton messageDialog = AppMessageDialogSingleton.getSingleton();

                // POP UP THE DIALOG
                messageDialog.show("Failed to Edit Line", 
                        "The line with same name already exists. Please choose another name.");
            }
        }
    }
    
    public void listAllStationInLine() {
        // GET THE WORKSPACE
        mmmWorkspace workspace = (mmmWorkspace) app.getWorkspaceComponent();
        
        // GET SELECTION FROM THE COMBOBOX
        String selectedLine = (String) workspace.getMetroLineComboBox().getValue();

        // GET LINE FROM THE MAP
        mmmLine line = lines.get(selectedLine);
        
        // THEN CREATE DIALOG
        if (line != null) {
            // CREATE STRING INPUT
            String input = "Stations at Line " + line.getLineName() + ": ";
            for (Shape s : line.getShapes()) {
                if (s instanceof DraggableStation) {
                    input = input + "\n" + ((DraggableStation) s).getName().getText();
                }
            }
            
            // GET THE DIALOG SINGLETON
            AppMessageDialogSingleton messageDialog = AppMessageDialogSingleton.getSingleton();
            
            // POP UP THE DIALOG
            messageDialog.show("List All Stations in Line", input);
        }   
    }
    
    public void changeLineThickness(double width) {
        // GET THE WORKSPACE
        mmmWorkspace workspace = (mmmWorkspace) app.getWorkspaceComponent();
        
        // GET SELECTION FROM THE COMBOBOX
        String selectedLine = (String) workspace.getMetroLineComboBox().getValue();

        // GET LINE FROM THE MAP
        mmmLine line = lines.get(selectedLine);
        
        if (line != null) {
            // SET NEW WIDTH OF THE LINE
            for (Shape s : line.getShapes()) {
                if (s instanceof Line) {
                    ((Line) s).setStrokeWidth(width);
                }
            }
        }
    }
    
    public void addStationToLine(int x, int y) {
        // LET THE USER SELECT ANY SHAPE
        selectTopShape(x, y);
        
        // THEN SEE IF THE SELECTION IS A STATION OR NOT
        if (!(selectedShape instanceof DraggableStation) ) {
            // CHANGE THE CURSOR
            Scene scene = app.getGUI().getPrimaryScene();
            scene.setCursor(Cursor.DEFAULT);

            // CHANGE THE STATE
            state = mmmState.SELECTING_MODE;
        }
        else {
            AddStationToLineTransaction cT = new AddStationToLineTransaction(app, x, y);
            jtps.addTransaction(cT);
        }
    }
    
    public DraggableStation getClosestStation(DraggableStation station, LinkedList<Shape> list) {
        double originalX = station.getCenterX();
        double originalY = station.getCenterY();
        double temp = 0;
        DraggableStation closeStation = null;
        for (Shape s : list) {
            if (s instanceof DraggableStation) {
                // CALCULATE THE DISTANCE
                double distanceX = ((DraggableStation) s).getCenterX() - originalX;
                double distanceY = ((DraggableStation) s).getCenterY() - originalY;
                double distance = Math.hypot(distanceX, distanceY);
                // THEN COMPARE THE DISTANCE
                if (distance < temp || temp == 0) {
                    temp = distance;
                    closeStation = (DraggableStation) s;
                }
            }
        }
        return (DraggableStation) closeStation;
    }
    
    public void removeStationFromLine(int x, int y) {
        // LET THE USER SELECT ANY SHAPE
        selectTopShape(x, y);
        
        // THEN SEE IF THE SELECTION IS A STATION OR NOT
        if (!(selectedShape instanceof DraggableStation) ) {
            // CHANGE THE CURSOR
            Scene scene = app.getGUI().getPrimaryScene();
            scene.setCursor(Cursor.DEFAULT);

            // CHANGE THE STATE
            state = mmmState.SELECTING_MODE;
        }
        else {
            // GET THE WORKSPACE
            mmmWorkspace workspace = (mmmWorkspace) app.getWorkspaceComponent();
            
            // IF THERE IS NO ANY SELECTED SHAPE OR SELECTED SHAPE IS NOT A STATION
            if (!(selectedShape instanceof DraggableStation)) {
                selectTopShape(x, y);
            }
  
            // GET SELECTED STATION FROM THE MAP EDITOR
            DraggableStation selectedStation = (DraggableStation) selectedShape;
            String selectedStationName = selectedStation.getName().getText();
            
            // SYNC WITH WORKSPACE
            workspace.getMetroStationComboBox().setValue(selectedStationName);

            // GET SELECTION FROM THE COMBOBOX
            String selectedLine = (String) workspace.getMetroLineComboBox().getValue();

            // GET LINE FROM THE MAP
            mmmLine line = lines.get(selectedLine);
            
            // IF THERE IS NO ANY STATION IN LINE
            if (line.getShapes().size() < 5) {
                AppMessageDialogSingleton messageDialog = AppMessageDialogSingleton.getSingleton();
                messageDialog.show("Failed to Remove Station From Line", 
                        "The selected line does not have any stations. Please choose a correct line.");           
            }
            // IF THERE IS ONE OR MORE STATION IN LINE
            else {
                RemoveStationFromLineTransaction cT = new RemoveStationFromLineTransaction(app, x, y);
                jtps.addTransaction(cT);
            }

        }
    }
   
    public void metroStationSelection(String selection) {
        // GET THE WORKSPACE
        mmmWorkspace workspace = (mmmWorkspace) app.getWorkspaceComponent();
        
        // GET STATION FROM THE MAP
        DraggableStation station = stations.get(selection);
        
        // UPDATE THE COLORPICKER ACCORDINGLY
        if (station != null) {
            double radius = station.getRadius() * 3;
            workspace.getChangeStationRadiusSlider().setValue(radius);
            workspace.getMetroStationColorPicker().setValue((Color) station.getFill());   
        }      
    }
        
    public void addStation(int x, int y) {
        // GET THE WORKSPACE
        mmmWorkspace workspace = (mmmWorkspace) app.getWorkspaceComponent();
        
        // GET THE DIALOG SINGLETON
        AppTextEnterDialogSingleton dialog = AppTextEnterDialogSingleton.getSingleton();
        
        if (!stations.containsKey(dialog.getText())) {
            AddStationTransaction cT = new AddStationTransaction(app, x, y);
            jtps.addTransaction(cT);
        }
        else {
            // GET THE DIALOG SINGLETON
            AppMessageDialogSingleton messageDialog = AppMessageDialogSingleton.getSingleton();

            // CHANGE THE CURSOR
            Scene scene = app.getGUI().getPrimaryScene();
            scene.setCursor(Cursor.DEFAULT);

            // GO INTO SHAPE SIZING MODE
            state = mmmState.SELECTING_MODE;
            
            // POP UP THE DIALOG
            messageDialog.show("Failed to Add Station", 
                    "The station with same name already exists. Please choose another name.");
        }
        
    }
    
    public void removeStation() {
        // GET THE WORKSPACE
        mmmWorkspace workspace = (mmmWorkspace) app.getWorkspaceComponent();
        
        // GET THE DIALOG SINGLETON
        AppYesNoDialogSingleton yesNoDialog = AppYesNoDialogSingleton.getSingleton();
        
        // IF THE USER SAID YES
        if (yesNoDialog.getSelection()) {
            // GET SELECTION FROM THE COMBOBOX
            String selection = (String) workspace.getMetroStationComboBox().getValue();
            
            // GET STATION FROM THE MAP
            DraggableStation station = stations.get(selection);
            
            // MAKE SURE THE STATION SELECTED IS NOT IN LINE
            if (station != null) {
                if (!station.isOnLine()) {
                    // REMOVE FROM EVERYWHERE
                    removeStationFromComboBox(selection);
                    stations.remove(selection);
                    shapes.remove(station);
                    shapes.remove(station.getName());
                }
                else {
                    // GET THE DIALOG SINGLETON
                    AppMessageDialogSingleton messageDialog = AppMessageDialogSingleton.getSingleton();

                    // POP UP THE DIALOG
                    messageDialog.show("Failed to Remove Station", 
                            "The selected station is already in line. Please choose a correct station.");
                }
            }
        }
    }
       
    public void snapToGrid() {        
        // IF THE USER SELECTED LINE 
        if (selectedShape instanceof DraggableEnd) {
            double newX = round(((DraggableEnd) selectedShape).getX());
            double newY = round(((DraggableEnd) selectedShape).getY());
            ((DraggableEnd) selectedShape).move(newX, newY);
        }
        // IF THE USER SELECTED STATION
        else if (selectedShape instanceof DraggableStation) {
            double newX = round(((DraggableStation) selectedShape).getCenterX());
            double newY = round(((DraggableStation) selectedShape).getCenterY());
            ((DraggableStation) selectedShape).move(newX, newY);
        }
        // IF THE USER SELECTED NOTHING
        else {
            // GET THE DIALOG SINGLETON
            AppMessageDialogSingleton messageDialog = AppMessageDialogSingleton.getSingleton();
        
            // POP UP THE DIALOG
            messageDialog.show("Failed to Snap at Grid", 
                        "Failed to snap at grid. Please select either line end or station at the map editor to snap.");
        }
    }
    
    /**
     * This function rounds the input into nearest 30.
     */
    private double round(double value) {
        return ((int) value + 15) / 30 * 30;
    }
    
    public void moveStationLabel() {
        // GET THE WORKSPACE
        mmmWorkspace workspace = (mmmWorkspace) app.getWorkspaceComponent();
        
        // GET SELECTION FROM THE COMBOBOX
        String selection = (String) workspace.getMetroStationComboBox().getValue();
        
        // GET STATION FROM THE MAP
        DraggableStation station = stations.get(selection);
        
        // MOVE LABEL OF THE SELECTED STATION
        if (station != null) {
            station.moveLabel();
        }
    }
    
    public void rotateStationLabel() {
        // GET THE WORKSPACE
        mmmWorkspace workspace = (mmmWorkspace) app.getWorkspaceComponent();
        
        // GET SELECTION FROM THE COMBOBOX
        String selection = (String) workspace.getMetroStationComboBox().getValue();
        
        // GET STATION FROM THE MAP
        DraggableStation station = stations.get(selection);
        
        // ROTATE LABEL OF THE SELECTED STATION
        if (station != null) {
            station.rotateLabel();
        }
    }
 
    public void changeStationFillColor(Color color) {
        // GET THE WORKSPACE
        mmmWorkspace workspace = (mmmWorkspace) app.getWorkspaceComponent();
        
        // GET SELECTION FROM THE COMBOBOX
        String selection = (String) workspace.getMetroStationComboBox().getValue();
        
        // GET STATION FROM THE MAP
        DraggableStation station = stations.get(selection);
        
        // SET THE COLOR OF THE SELECTED STATION
        if (station != null) {
            station.setFill(color);
        }
    } 
    
    public void changeStationRadius(double radius) {
        // GET THE WORKSPACE
        mmmWorkspace workspace = (mmmWorkspace) app.getWorkspaceComponent();
        
        // GET SELECTION FROM THE COMBOBOX
        String selection = (String) workspace.getMetroStationComboBox().getValue();
            
        // GET STATION FROM THE MAP
        DraggableStation station = stations.get(selection);
        
        // SET THE RADIUS OF THE SELECTED STATION
        if (station != null) {
            station.setRadius(radius);
        }
    }
    
    /**
     * Finds the minimum transfer route.
     * Algorithm made with the help from Richard McKenna. Written by Myungsuk Moon.
     */
    public void findRoute() {
        // GET THE WORKSPACE
        mmmWorkspace workspace = (mmmWorkspace) app.getWorkspaceComponent();
        
        // GET THE DIALOG SINGLETON FOR LATER
        AppColumnedMessageDialogSingleton columnedDialog = AppColumnedMessageDialogSingleton.getSingleton();
        
        // GET STATION FROM THE COMBOBOX AND MAP
        DraggableStation startStation = stations.get((String) workspace.getStartStationComboBox().getValue());
        DraggableStation endStation = stations.get((String) workspace.getEndStationComboBox().getValue());
        
        // MAKE SURE THE USER PROPERLY SELECTED THE STATION ON COMBOBOXES
        if (startStation instanceof DraggableStation && endStation instanceof DraggableStation) {
            // COUNT NUMBER OF TRANSFERS
            int transferCount = 0;

            // POSSIBLE ROUTES
            LinkedList<mmmRoute> testRoutes = new LinkedList();

            // CREATE NEW ROUTES FOR THE START STATION
            for (String s : startStation.getLines()) {
                mmmRoute newRoute = new mmmRoute(startStation, endStation);
                testRoutes.add(newRoute);
                newRoute.addBoarding(lines.get(s), startStation);
            }

            boolean found = false;
            boolean moreRoutesPossible = true;
            LinkedList<mmmRoute> completedRoutes = new LinkedList();
            while (!found && moreRoutesPossible) {
                LinkedList<mmmRoute> updatedRoutes = new LinkedList();
                for (mmmRoute route : testRoutes) {
                    mmmRoute testRoute = route;

                    // FIRST CHECK IF THE DESTINATION IS ON THE ROUTE
                    if (testRoute.hasLineWithStation(endStation)) {
                        completedRoutes.add(testRoute);
                        found = true;
                        moreRoutesPossible = false;
                    }
                    else if (moreRoutesPossible) {
                        // GET ALL LINES CONNECTED TO THE LAST LINE ON THE TEST ROUTE
                        // WHICH ARE NOT VISITED YET
                        mmmLine lastLine = testRoute.getLines().getLast();
                        for (String lineName : lastLine.getTransfers()) {
                            mmmLine testLine = lines.get(lineName);
                            if(!testRoute.getLines().contains(testLine)) {
                                mmmRoute newRoute = testRoute.clone();
                                DraggableStation transferStation = lastLine.findTransferStation(testLine);
                                if (transferStation != null) {
                                    newRoute.addBoarding(testLine, transferStation);
                                    updatedRoutes.add(newRoute);
                                }
                            }
                            // DEAD ENDS WILL NOT MAKE IT TO THE NEXT ROUND
                        }
                    }
                }
                if (updatedRoutes.size() > 0) {
                    testRoutes = updatedRoutes;
                    transferCount++;
                }
                else {
                    moreRoutesPossible = false;
                }
            }
            // IF THE ROUTE IS FOUND
            if (found) {
                // GET THE MINIMAL TRANSFER ROUTE
                mmmRoute shortestRoute = completedRoutes.getFirst();
                
                // BRIEF DESCRIPTION
                String routeString = "Route from " + startStation.getName().getText() +
                                        " to " + endStation.getName().getText() + ":\n";
                Iterator linesItr = shortestRoute.getLines().iterator();
                Iterator stationsItr = shortestRoute.getTransferStations().iterator(); 
                while (linesItr.hasNext() && stationsItr.hasNext()) {
                    String currentLineName = ((mmmLine) linesItr.next()).getLineName();
                    String currentStationName = ((DraggableStation) stationsItr.next()).getName().getText();
                    routeString = routeString.concat("Board " + currentLineName + " at " + currentStationName + "\n");
                }
                routeString = routeString.concat("Disembark at " + endStation.getName().getText());
                
                // DETAILED DESCRIPTION
                int time = 0;
                String detailedRouteString = "Detailed Route from " + startStation.getName().getText() +
                                        " to " + endStation.getName().getText() + ":\n";
                for (DraggableStation station : shortestRoute.getStationsOnRoute()) {
                     detailedRouteString = detailedRouteString.concat(station.getName().getText() + ", 3 minutes\n");
                     time = time + 3;
                }
                detailedRouteString = detailedRouteString.concat("\nTotal estimated time: " + time + " minutes");

                // POP UP THE DIALOG
                columnedDialog.show("Find Route", routeString, detailedRouteString);
            }
            else {
                // GET THE DIALOG SINGLETON
                AppMessageDialogSingleton messageDialog = AppMessageDialogSingleton.getSingleton();
                
                // POP UP THE DIALOG
                messageDialog.show("Find Route", "Route not found.");
            }
        }
    }
    
    public void setBackgroundColor(Color selection) {
        // GET THE WORKSPACE
        mmmWorkspace workspace = (mmmWorkspace) app.getWorkspaceComponent();
        
        // THEN APPLY NEW COLOR
        backgroundFills[0] = new BackgroundFill(selection, null, null);
        workspace.getMapEditor().setBackground(new Background(backgroundFills, backgroundImages));
    }
 
    public void setImageBackground(File selectedFile) {
        // GET THE WORKSPACE
        mmmWorkspace workspace = (mmmWorkspace) app.getWorkspaceComponent();
        
        // THEN APPLY THE IMAGE
        Image image = new Image(selectedFile.toURI().toString());
        backgroundImages[0] = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, 
                BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
        workspace.getMapEditor().setBackground(new Background(backgroundFills, backgroundImages));
    }
    
    public void removeImageBackground() {
        // GET THE WORKSPACE
        mmmWorkspace workspace = (mmmWorkspace) app.getWorkspaceComponent();
        
        // THEN REMOVE THE IMAGE
        backgroundImages[0] = null;
        workspace.getMapEditor().setBackground(new Background(backgroundFills, backgroundImages));
    }
    
    public void addImageOverlay(int x, int y) {
        // WE'LL NEED TO GET CUSTOMIZED STUFF WITH THIS
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        
        if (imageOverlayFile != null) {
            // DESELECT THE SELECTED SHAPE IF THERE IS ONE
            if (selectedShape != null) {
                unhighlightShape(selectedShape);
                selectedShape = null;
            }
            try {
                // CREATE IMAGE
                Image image = new Image(imageOverlayFile.toURI().toString());
                
                // THEN CREATE AND FILL IMAGE SHAPE
                DraggableImage newImage = new DraggableImage(x, y);
                newImage.size(image.getWidth(), image.getHeight());
                newImage.setFill(new ImagePattern(image));
                

                // ADD THE SHAPE TO THE LIST
                shapes.add(newImage);
                selectedShape = newImage;
                highlightShape(newImage);

                // GO INTO SHAPE SIZING MODE
                state = mmmState.DRAGGING_MODE;

                // ENABLE/DISABLE THE PROPER BUTTONS
                // workspace.reloadWorkspace(this);
            } catch (Exception e) {
                AppMessageDialogSingleton dialog = AppMessageDialogSingleton.getSingleton();
                dialog.show(props.getProperty(LOAD_ERROR_TITLE), props.getProperty(LOAD_ERROR_MESSAGE));
            }
        }
    }
    
    public void addLabel(int x, int y) {
        // GET THE DIALOG SINGLETON
        AppTextEnterDialogSingleton dialog = AppTextEnterDialogSingleton.getSingleton();
        
        // UNHIGHLIGHT ANY SHAPE SELECTED PREVIOUSLY
        if (selectedShape != null) {
            unhighlightShape(selectedShape);
            selectedShape = null;
        }

        // ADD TEXT
        DraggableLabel newLabel = new DraggableLabel(x, y);
        newLabel.setText(dialog.getText());
        // Font font = newText.getFont();
        newLabel.setFont(Font.font("Helvetica"));

        // ADD THE SHAPE TO THE LIST
        shapes.add(newLabel);
        selectedShape = newLabel;
        highlightShape(newLabel);
        
        // CHANGE THE CURSOR
	Scene scene = app.getGUI().getPrimaryScene();
	scene.setCursor(Cursor.DEFAULT);

        // GO INTO SHAPE SIZING MODE
        state = mmmState.DRAGGING_MODE;

        // ENABLE/DISABLE THE PROPER BUTTONS
        // workspace.reloadWorkspace(this);
    }
    
    public void removeMapElement() {
        if (selectedShape instanceof DraggableImage || selectedShape instanceof DraggableLabel) {
            unhighlightShape(selectedShape);
            shapes.remove(selectedShape);
            selectedShape = null;     
        }
    }
    
    public void setFontColor(Color selection) {
        if (selectedShape instanceof Text) {
            ((Text) selectedShape).setFill(selection);
        }
    }
    
    public void setBold() {
        if (selectedShape instanceof Text) {
            // SET BOLD
            Font font = ((Text) selectedShape).getFont();
            ((Text) selectedShape).setFont(Font.font(font.getFamily(), FontWeight.EXTRA_BOLD, font.getSize()));
        }
    }
    
    public void setItalic() {
        if (selectedShape instanceof Text) {
            // SET ITALIC
            Font font = ((Text) selectedShape).getFont();
            ((Text) selectedShape).setFont(Font.font(font.getFamily(), FontPosture.ITALIC, font.getSize()));
        }
    }
    
    public void setFontSize(int selection) {
        if (selectedShape instanceof Text) {
            // GET THE WORKSPACE
            mmmWorkspace workspace = (mmmWorkspace) app.getWorkspaceComponent();
            
            // SET NEW FONT SIZE
            Font font = ((Text) selectedShape).getFont();
            ((Text) selectedShape).setFont(Font.font(font.getFamily(), selection));
        }
    }
    
    public void setFontFamily(String selection) {
        if (selectedShape instanceof Text) {
            // GET THE WORKSPACE
            mmmWorkspace workspace = (mmmWorkspace) app.getWorkspaceComponent();
        
            // SET NEW FONT FAMILY
            Font font = ((Text) selectedShape).getFont();
            ((Text) selectedShape).setFont(Font.font(selection, font.getSize()));
        }
    }
    
    public void zoomIn() {
        // GET THE WORKSPACE
        mmmWorkspace workspace = (mmmWorkspace) app.getWorkspaceComponent();
        
        // THEN EXPAND THE MAP EDITOR SCALE
        mapScaleX = mapScaleX * 1.1;
        mapScaleY = mapScaleY * 1.1;
        workspace.getMapEditor().setScaleX(mapScaleX);
        workspace.getMapEditor().setScaleY(mapScaleY);
    }
    
    public void zoomOut() {
        // GET THE WORKSPACE
        mmmWorkspace workspace = (mmmWorkspace) app.getWorkspaceComponent();
        
        // THEN REDUCE THE MAP EDITOR SCALE
        mapScaleX = mapScaleX * 0.9;
        mapScaleY = mapScaleY * 0.9;
        workspace.getMapEditor().setScaleX(mapScaleX);
        workspace.getMapEditor().setScaleY(mapScaleY);
    }
    
    /**
     * This function increases the map size by 10%.
     */
    public void increaseMapSize() {
        // GET THE WORKSPACE
        mmmWorkspace workspace = (mmmWorkspace) app.getWorkspaceComponent();
        
        // THEN EXPAND THE MAP EDITOR FRMAE SIZE
        mapWidth = Math.rint(mapWidth * 1.1);
        mapHeight = Math.rint(mapHeight * 1.1);
        workspace.getMapEditor().setClip(new Rectangle(mapWidth, mapHeight));
    }
    
    /**
     * This function decreases the map size by 10%.
     */
    public void decreaseMapSize() {
        // GET THE WORKSPACE
        mmmWorkspace workspace = (mmmWorkspace) app.getWorkspaceComponent();
        
        // THEN REDUCE THE MAP EDITOR FRMAE SIZE
        if (mapWidth > 200 && mapHeight > 200) {
            mapWidth = Math.rint(mapWidth * 0.9);
            mapHeight = Math.rint(mapHeight * 0.9);
            workspace.getMapEditor().setClip(new Rectangle(mapWidth, mapHeight));
        }
    }
    
    public void navigateMap(String key) {
        // GET THE WORKSPACE
        mmmWorkspace workspace = (mmmWorkspace) app.getWorkspaceComponent();
        
        // TRANSLATE ACCORDING TO THE KEY
        if (key.equals("W")) {
            workspace.getMapEditor().setTranslateY(workspace.getMapEditor().getTranslateY() + 25);
        }
        else if (key.equals("A")) {
            workspace.getMapEditor().setTranslateX(workspace.getMapEditor().getTranslateX() + 25);
        }
        else if (key.equals("S")) {
            workspace.getMapEditor().setTranslateY(workspace.getMapEditor().getTranslateY() - 25);
        }
        else if (key.equals("D")) {
            workspace.getMapEditor().setTranslateX(workspace.getMapEditor().getTranslateX() - 25);
        }
    }
    
    public void showGrid(boolean selected) {
        // GET THE PROPERTIES MANAGER
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        
        // GET THE WORKSPACE
        mmmWorkspace workspace = (mmmWorkspace) app.getWorkspaceComponent();
        
        if (selected) {
            // THEN APPLY THE GRID
            String imagePath = FILE_PROTOCOL + PATH_IMAGES + props.getProperty(GRID_IMAGE.toString());
            Image image = new Image(imagePath);
            backgroundImages[1] = new BackgroundImage(image, BackgroundRepeat.REPEAT, 
                    BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
            workspace.getMapEditor().setBackground(new Background(backgroundFills, backgroundImages));
        }
        else {
            // THEN GET RID OF THE IMAGE
            backgroundImages[1] = null;
            workspace.getMapEditor().setBackground(new Background(backgroundFills, backgroundImages));
        }
    }
    
    public WritableImage takeSnapShot() {
        // GET THE WORKSPACE
        mmmWorkspace workspace = (mmmWorkspace) app.getWorkspaceComponent();
        
        // GET THE MAP EDITOR
	Pane canvas = workspace.getMapEditor();
        
        // RETURN IMAGE
	return canvas.snapshot(new SnapshotParameters(), null);
    }
            
    public void addShape(Shape shapeToAdd) {
        shapes.add(shapeToAdd);
    }

    public void removeShape(Shape shapeToRemove) {
        shapes.remove(shapeToRemove);
    }
    
        public void moveSelectedShapeToBack() {
        if (selectedShape != null) {
            shapes.remove(selectedShape);
            if (shapes.isEmpty()) {
                shapes.add(selectedShape);
            } else {
                ArrayList<Node> temp = new ArrayList();
                temp.add(selectedShape);
                for (Node node : shapes) {
                    temp.add(node);
                }
                shapes.clear();
                for (Node node : temp) {
                    shapes.add(node);
                }
            }
        }
    }

    public void moveSelectedShapeToFront() {
        if (selectedShape != null) {
            shapes.remove(selectedShape);
            shapes.add(selectedShape);
        }
    }
    
    public Shape selectTopShape(int x, int y) {
        // GET THE TOP SHAPE
        Shape shape = getTopShape(x, y);
        if (shape == selectedShape) {
            return shape;
        }
        
        // HIGHLIGHT ACCORDINGLY
        if (selectedShape != null) {
            unhighlightShape(selectedShape);
        }
        if (shape != null) {
            highlightShape(shape);
        }
        selectedShape = shape;
        
        // GET WORKSPACE AND LINE NAME OF THE SELECTED LINE END
        mmmWorkspace workspace = (mmmWorkspace) app.getWorkspaceComponent();
        
        // WORK ON SHAPE ACCORDINGLY
        if (shape instanceof DraggableStation) {    
            // GET STATION NAME OF THE SELECTED STATION
            String stationName = (((DraggableStation) shape)).getName().getText();
            
            // UPDATE THE WORKSPACE
            workspace.getMetroStationComboBox().setValue(stationName);
        }
        else if (shape instanceof DraggableEnd) {
            // GET LINE NAME OF THE SELECTED LINE END
            String lineName = (((DraggableEnd) shape)).getText();

            // UPDATE THE WORKSPACE
            workspace.getMetroLineComboBox().setValue(lineName);
        }
        if (shape instanceof Text) {
            // GET FONT OF THE SELECTED TEXT
            Font font = ((Text) shape).getFont();
            
            // UPDATE THE WORKSPACE
            workspace.getFontColorPicker().setValue((Color) shape.getFill());
            workspace.getFontSizeComboBox().setValue((int) font.getSize());
            workspace.getFontFamilyComboBox().setValue(font.getFamily());
        }
        if (shape instanceof Draggable) {
            ((Draggable) shape).update(x, y);
        }
        
        return shape;
    }
    
    public Shape getTopShape(int x, int y) {
        for (int i = shapes.size() - 1; i >= 0; i--) {
            Shape shape = (Shape) shapes.get(i);
            if (shape.contains(x, y)) {
                return shape;
            }
        }
        return null;
    }
    
    public void addStationToComboBox(String station) {
        // GET WORKSPACE        
        mmmWorkspace workspace = (mmmWorkspace) app.getWorkspaceComponent();
        
        // THEN ADD TO ALL COMBOBOXES
        workspace.getMetroStationComboBox().getItems().add(station);
        workspace.getMetroStationComboBox().setValue(station);
        workspace.getStartStationComboBox().getItems().add(station);
        workspace.getEndStationComboBox().getItems().add(station);
    }
    
    public void removeStationFromComboBox(String station) {
        // GET WORKSPACE        
        mmmWorkspace workspace = (mmmWorkspace) app.getWorkspaceComponent();
        
        // THEN REMOVE FROM ALL COMBOBOXES
        workspace.getMetroStationComboBox().getItems().remove(station);
        workspace.getStartStationComboBox().getItems().remove(station);
        workspace.getEndStationComboBox().getItems().remove(station);
    }
    
    public HashMap<String, mmmLine> getLines() {
        return lines;
    } 
    
    public HashMap<String, DraggableStation> getStations() {
        return stations;
    }

    public Shape getSelectedShape() {
        return selectedShape;
    }

    public void setSelectedShape(Shape initSelectedShape) {
        selectedShape = initSelectedShape;
    }
    
    public mmmState getState() {
        return state;
    }
        
    public void setState(mmmState initState) {
        state = initState;
    }

    public boolean isInState(mmmState testState) {
        return state == testState;
    }
        
    public ComboBox getMetroLineComboBox() {
        mmmWorkspace workspace = (mmmWorkspace) app.getWorkspaceComponent();
        return workspace.getMetroLineComboBox();
    }
    
    public ComboBox getMetroStationComboBox() {
        mmmWorkspace workspace = (mmmWorkspace) app.getWorkspaceComponent();
        return workspace.getMetroStationComboBox();
    }
    
    public ColorPicker getMetroStationColorPicker() {
        mmmWorkspace workspace = (mmmWorkspace) app.getWorkspaceComponent();
        return workspace.getMetroStationColorPicker();
    }
    
    public void setImageOverlayFile(File input) {
        imageOverlayFile = input;
    }
    
    public void setMapScaleX(int newMapScaleX) {
        mapScaleX = newMapScaleX;
    }
    
    public void setMapScaleY(int newMapScaleY) {
        mapScaleY = newMapScaleY;
    }
    
    public void setMapWidth(int newMapWidth) {
        mapWidth = newMapWidth;
    }
    
    public void setMapHeight(int newMapHeight) {
        mapHeight = newMapHeight;
    }
        
    @Override
    public void resetData() {
        // GET THE WORKSPACE
        mmmWorkspace workspace = (mmmWorkspace) app.getWorkspaceComponent();
        
        // CHANGE INTO DEFAULT MODE AND CURSOR
        Scene scene = app.getGUI().getPrimaryScene();
	scene.setCursor(Cursor.DEFAULT);
        state = mmmState.SELECTING_MODE;
        
        // CLEAR METRO MAP NAME
        app.getGUI().getFileController().setFileName(null);
        
        // CLEAR SELECTED SHAPE
	selectedShape = null;
        
        // CLEAR OUT DATAS
        lines.clear();
        stations.clear();
	shapes.clear();
        
        // CLEAR OUT COMBO BOXES
        workspace.getMetroLineComboBox().getItems().clear();
        workspace.getMetroStationComboBox().getItems().clear();
        workspace.getStartStationComboBox().getItems().clear();
        workspace.getEndStationComboBox().getItems().clear();
        workspace.getMetroStationColorPicker().setValue(Color.WHITE);
        
        // CLEAR OUT THE MAP EDITOR
	workspace.getMapEditor().getChildren().clear();
    }
}