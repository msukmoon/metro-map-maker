package mmm.jtps;

import djf.AppTemplate;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import jtps.jTPS_Transaction;
import mmm.data.DraggableEnd;
import mmm.data.DraggableStation;
import mmm.data.mmmData;
import mmm.data.mmmLine;
import mmm.data.mmmState;
import mmm.gui.mmmWorkspace;


/**
 *
 * @author jays
 */
public class AddStationToLineTransaction implements jTPS_Transaction {
    AppTemplate app;
    mmmData dataManager;
    int x;
    int y;
    mmmLine lineData;
    DraggableStation stationShape;
    
    public AddStationToLineTransaction(AppTemplate app, int x, int y) {
        this.app = app;
        this.dataManager = (mmmData) app.getDataComponent();
        this.x = x;
        this.y = y;      
    }

    @Override
    public void doTransaction() {
        // LET THE USER SELECT ANY SHAPE
        dataManager.selectTopShape(x, y);
        
        // GET THE WORKSPACE
        mmmWorkspace workspace = (mmmWorkspace) app.getWorkspaceComponent();

        // IF THERE IS NO ANY SELECTED SHAPE OR SELECTED SHAPE IS NOT A STATION
        if (!(dataManager.getSelectedShape() instanceof DraggableStation)) {
            dataManager.selectTopShape(x, y);
        }

        // GET SELECTED STATION FROM THE MAP EDITOR
        DraggableStation selectedStation = (DraggableStation) dataManager.getSelectedShape();
        String selectedStationName = selectedStation.getName().getText();

        // SYNC WITH WORKSPACE
        workspace.getMetroStationComboBox().setValue(selectedStationName);

        // GET SELECTION FROM THE COMBOBOX
        String selectedLine = (String) workspace.getMetroLineComboBox().getValue();

        // GET LINE FROM THE MAP
        mmmLine line = dataManager.getLines().get(selectedLine);

        // IF THERE IS NO ANY STATION IN LINE
        if (line.getShapes().size() < 5) {
            // FIND THE CLOSEST STATION
            DraggableEnd startText = (DraggableEnd) line.getShapes().getFirst();
            int index = line.getShapes().indexOf(startText);     // NEEDS FIX

            // UNBIND TEXT WITH ITS LINE
            Line nextLine = (Line) line.getShapes().get(index + 1);
            nextLine.startXProperty().unbind();
            nextLine.startYProperty().unbind();

            // SET NEW LINE TO BIND
            Line newLine = new Line();
            newLine.setStroke(nextLine.getStroke());
            newLine.setStrokeWidth(5);

            // THEN LINK WITH SELECTED STATION
            newLine.startXProperty().bind(startText.xProperty());
            newLine.startYProperty().bind(startText.yProperty().add(20));
            newLine.endXProperty().bind(selectedStation.centerXProperty());
            newLine.endYProperty().bind(selectedStation.centerYProperty());

            // NOW RELINK OLD LINE TO THE SELECTED STATION
            nextLine.startXProperty().bind(selectedStation.centerXProperty());
            nextLine.startYProperty().bind(selectedStation.centerYProperty());

            // ADD THESE SHAPES TO THE LINKED LIST
            line.getShapes().add(index + 1, newLine);
            line.getShapes().add(index + 2, selectedStation);

            // CHECK IF THE SELECTED STATION IS A TRANSFER STATION
            if (selectedStation.getLines().size() > 0) {
                for (String lineName : selectedStation.getLines()) {
                    line.getTransfers().add(lineName);
                    dataManager.getLines().get(lineName).getTransfers().add(line.getLineName());
                } 
            }

            // NOW THE SELECTED STATION IS IN LINE
            selectedStation.setIsOnLine(true);
            selectedStation.getLines().add(selectedLine);

            // ADD THESE SHAPES TO THE OBSERVABLE LIST
            dataManager.addShape(newLine);
            dataManager.removeShape(selectedStation);
            dataManager.addShape(selectedStation);        
            dataManager.setSelectedShape(selectedStation);
            dataManager.highlightShape(selectedStation); 

            // SAVE FOR TRANSACTIONS
            lineData = line;
            stationShape = selectedStation;
        }
        // IF THERE IS ONE OR MORE STATION IN LINE
        else {
            // FIND THE CLOSEST STATION
            DraggableStation closestStation = dataManager.getClosestStation(selectedStation, line.getShapes());
            int index = line.getShapes().indexOf(closestStation);     // NEEDS FIX

            // UNBIND CLOSEST STATION AND ITS NEXT LINE
            Line nextLine = ((Line) line.getShapes().get(index + 1));
            nextLine.startXProperty().unbind();
            nextLine.startYProperty().unbind();

            // SET NEW LINE TO BIND
            Line newLine = new Line();
            newLine.setStroke(nextLine.getStroke());
            newLine.setStrokeWidth(5);

            // THEN LINK WITH SELECTED STATION
            newLine.startXProperty().bind(closestStation.centerXProperty());
            newLine.startYProperty().bind(closestStation.centerYProperty());
            newLine.endXProperty().bind(selectedStation.centerXProperty());
            newLine.endYProperty().bind(selectedStation.centerYProperty());

            // NOW RELINK OLD LINE TO THE SELECTED STATION
            nextLine.startXProperty().bind(selectedStation.centerXProperty());
            nextLine.startYProperty().bind(selectedStation.centerYProperty());

            // ADD THESE SHAPES TO THE LINKED LIST
            line.getShapes().add(index + 1, newLine);
            line.getShapes().add(index + 2, selectedStation);

            // CHECK IF THE SELECTED STATION IS A TRANSFER STATION
            if (selectedStation.getLines().size() > 0) {
                for (String lineName : selectedStation.getLines()) {
                    line.getTransfers().add(lineName);
                    dataManager.getLines().get(lineName).getTransfers().add(line.getLineName());
                } 
            }

            // NOW THE SELECTED STATION IS IN LINE
            selectedStation.setIsOnLine(true);
            selectedStation.getLines().add(selectedLine);

            // ADD THESE SHAPES TO THE OBSERVABLE LIST
            dataManager.addShape(newLine);
            dataManager.removeShape(closestStation);
            dataManager.addShape(closestStation);
            dataManager.removeShape(selectedStation);  
            dataManager.addShape(selectedStation);        
            dataManager.setSelectedShape(selectedStation);
            dataManager.highlightShape(selectedStation);

            // SAVE FOR TRANSACTIONS
            lineData = line;
            stationShape = selectedStation;
        }

        // ENABLE/DISABLE THE PROPER BUTTONS
        // workspace.reloadWorkspace(this);
    }

    @Override
    public void undoTransaction() {
        
        // INVALID TRANSACTION OBJECT MIGHT GET PUSHED
        if (!(lineData == null || stationShape == null) && lineData.getShapes().size() > 4) {
            // GET THE INDEX OF THE SELECTED STATION
            int index = lineData.getShapes().indexOf(stationShape);     // NEEDS FIX

            // UNBIND SELECTED STATION WITH NEXT LINE
            Line nextLine = ((Line) lineData.getShapes().get(index + 1));
            nextLine.startXProperty().unbind();
            nextLine.startYProperty().unbind();

            // THEN LINK WITH BEFORE STATION WITH NEXT LINE
            Shape beforeShape = lineData.getShapes().get(index - 2);
            if (beforeShape instanceof DraggableStation) {
                nextLine.startXProperty().bind(((DraggableStation) beforeShape).centerXProperty());
                nextLine.startYProperty().bind(((DraggableStation) beforeShape).centerYProperty());
            }
            else {
                nextLine.startXProperty().bind(((DraggableEnd) beforeShape).xProperty());
                nextLine.startYProperty().bind(((DraggableEnd) beforeShape).yProperty().add(20)); 
            }

            // GET THE INDEX OF OTHER SHAPES
            Line beforeLine = ((Line) lineData.getShapes().get(index - 1));
            Shape afterShape = lineData.getShapes().get(index + 2);

            // ADD THESE SHAPES TO THE LINKED LIST
            lineData.getShapes().remove(beforeLine);
            lineData.getShapes().remove(stationShape);

            // CHECK IF THE SELECTED STATION IS A TRANSFER STATION
            if (stationShape.getLines().size() > 1) {
                for (String lineName : stationShape.getLines()) {
                    if (!lineName.equals(lineData.getLineName())) {
                        lineData.getTransfers().remove(lineName);
                        dataManager.getLines().get(lineName).getTransfers().remove(lineData.getLineName());
                    }
                } 
            }

            // STATION IS NOW NOT ON LINE
            stationShape.setIsOnLine(false);
            stationShape.getLines().remove(lineData.getLineName());

            // ADD THESE SHAPES TO THE OBSERVABLE LIST
            dataManager.removeShape(beforeLine);
            dataManager.removeShape(nextLine);  
            dataManager.addShape(nextLine);
            dataManager.removeShape(beforeShape);
            dataManager.addShape(beforeShape);
            dataManager.removeShape(afterShape);
            dataManager.addShape(afterShape);

            // EMPTY SELECTED SHAPE
            dataManager.setSelectedShape(null);

            // CHANGE THE CURSOR
            Scene scene = app.getGUI().getPrimaryScene();
            scene.setCursor(Cursor.DEFAULT);

            // GO INTO SHAPE SIZING MODE
            dataManager.setState(mmmState.DRAGGING_MODE);

            // ENABLE/DISABLE THE PROPER BUTTONS
            // workspace.reloadWorkspace(this);
        }
    }
    
}
