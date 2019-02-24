package mmm.jtps;

import djf.AppTemplate;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import jtps.jTPS_Transaction;
import mmm.data.mmmData;
import mmm.data.mmmState;


/**
 *
 * @author jays
 */
public class AddLabelTransaction implements jTPS_Transaction {
    AppTemplate app;
    mmmData dataManager;
    int x;
    int y;
    
    public AddLabelTransaction(AppTemplate app, int x, int y) {
        this.app = app;
        this.dataManager = (mmmData) app.getDataComponent();
        this.x = x;
        this.y = y;      
    }

    @Override
    public void doTransaction() {

    }

    @Override
    public void undoTransaction() {

        // EMPTY SELECTED SHAPE
        dataManager.setSelectedShape(null);
           
        // CHANGE THE CURSOR
        Scene scene = app.getGUI().getPrimaryScene();
        scene.setCursor(Cursor.DEFAULT);

        // GO INTO SHAPE SIZING MODE
        dataManager.setState(mmmState.DRAGGING_MODE);
    }
    
}
