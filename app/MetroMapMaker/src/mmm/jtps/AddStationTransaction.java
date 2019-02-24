package mmm.jtps;

import djf.AppTemplate;
import djf.ui.AppTextEnterDialogSingleton;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import jtps.jTPS_Transaction;
import mmm.data.DraggableStation;
import mmm.data.mmmData;
import mmm.data.mmmState;
import mmm.gui.mmmWorkspace;


/**
 *
 * @author jays
 */
public class AddStationTransaction implements jTPS_Transaction {
    AppTemplate app;
    mmmData dataManager;
    int x;
    int y;
    DraggableStation stationShape;
    
    public AddStationTransaction(AppTemplate app, int x, int y) {
        this.app = app;
        this.dataManager = (mmmData) app.getDataComponent();
        this.x = x;
        this.y = y;      
    }

    @Override
    public void doTransaction() {
        // GET THE WORKSPACE
        mmmWorkspace workspace = (mmmWorkspace) app.getWorkspaceComponent();
        
        // GET THE DIALOG SINGLETON
        AppTextEnterDialogSingleton dialog = AppTextEnterDialogSingleton.getSingleton();
        
        // UNHIGHLIGHT ANY SHAPE SELECTED PREVIOUSLY
        if (dataManager.getSelectedShape() != null) {
            dataManager.unhighlightShape(dataManager.getSelectedShape());
            dataManager.setSelectedShape(null);
        }

        // SET STATION
        DraggableStation newStation = new DraggableStation(x, y);
        newStation.setRadius(10);
        newStation.setFill(Color.WHITE);
        newStation.setStrokeWidth(2);
        newStation.setStroke(Color.BLACK);
        newStation.setIsOnLine(false);

        // SET TEXT
        newStation.initNewName();
        newStation.getName().setText(dialog.getText());
        newStation.getName().setFont(Font.font("Helvetica"));
        newStation.getName().setTextOrigin(VPos.CENTER);

        // THEN BIND THEM TOGETHER
        newStation.getName().xProperty().bind(newStation.centerXProperty().add(30));
        newStation.getName().yProperty().bind(newStation.centerYProperty().subtract(30));

        // ADD STATION TO THE MAP
        dataManager.getStations().put(dialog.getText(), newStation);

        // SYNC WITH WORKSPACE
        dataManager.addStationToComboBox(dialog.getText());

        // ADD THESE SHAPES TO THE OBSERVABLE LIST
        dataManager.addShape(newStation);
        dataManager.addShape(newStation.getName());
        dataManager.setSelectedShape(newStation);
        dataManager.highlightShape(newStation);

        // SAVE FOR TRANSACTIONS
        stationShape = newStation;

        // CHANGE THE CURSOR
        Scene scene = app.getGUI().getPrimaryScene();
        scene.setCursor(Cursor.DEFAULT);

        // GO INTO SHAPE SIZING MODE
        dataManager.setState(mmmState.DRAGGING_MODE);

        // ENABLE/DISABLE THE PROPER BUTTONS
        // workspace.reloadWorkspace(this);
        
    }

    @Override
    public void undoTransaction() {
        
        // WHEN USER FAILS TO ADD STATION, INVALID TRANSACTION OBJECT GETS PUSHED
        if (stationShape != null) {
            // REMOVE FROM EVERYWHERE
            dataManager.removeStationFromComboBox(stationShape.getName().getText());
            dataManager.getStations().remove(stationShape.getName().getText());
            dataManager.removeShape(stationShape);
            dataManager.removeShape(stationShape.getName());

            // EMPTY SELECTED SHAPE
            dataManager.setSelectedShape(null);

            // CHANGE THE CURSOR
            Scene scene = app.getGUI().getPrimaryScene();
            scene.setCursor(Cursor.DEFAULT);

            // GO INTO SHAPE SIZING MODE
            dataManager.setState(mmmState.DRAGGING_MODE);
        }
    }
    
}
