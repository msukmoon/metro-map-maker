package mmm.jtps;

import djf.AppTemplate;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import jtps.jTPS_Transaction;
import mmm.data.DraggableEnd;
import mmm.data.mmmData;
import mmm.data.mmmLine;
import mmm.data.mmmState;
import mmm.gui.AppTextColorEnterDialogSingleton;
import mmm.gui.mmmWorkspace;

/**
 *
 * @author jays
 */
public class AddLineTransaction implements jTPS_Transaction {
    AppTemplate app;
    mmmData dataManager;
    int x;
    int y;
    String lineName;
    Line lineShape;
    DraggableEnd startShape;
    DraggableEnd endShape;
    
    public AddLineTransaction(AppTemplate app, int x, int y) {
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
        AppTextColorEnterDialogSingleton dialog = AppTextColorEnterDialogSingleton.getSingleton();

        // UNHIGHLIGHT ANY SHAPE SELECTED PREVIOUSLY
        if (dataManager.getSelectedShape() != null) {
            dataManager.unhighlightShape(dataManager.getSelectedShape());
            dataManager.setSelectedShape(null);
        }

        // SET LINE
        Line newLine = new Line();
        newLine.setStroke(dialog.getColor());
        newLine.setStrokeWidth(5);

        // SET TEXT
        DraggableEnd newStart = new DraggableEnd(x, y);
        DraggableEnd newEnd = new DraggableEnd(x, y + 100);
        newStart.setText(dialog.getText());
        newEnd.setText(dialog.getText());
        newStart.setFont(Font.font("Helvetica"));
        newEnd.setFont(Font.font("Helvetica"));
        newStart.setTextOrigin(VPos.CENTER);
        newEnd.setTextOrigin(VPos.CENTER);

        // THEN BIND THEM TOGETHER
        newLine.startXProperty().bind(newStart.xProperty());
        newLine.startYProperty().bind(newStart.yProperty().add(20));
        newLine.endXProperty().bind(newEnd.xProperty());
        newLine.endYProperty().bind(newEnd.yProperty().subtract(20));

        // ADD THESE SHAPES TO THE LINKED LIST
        mmmLine line = new mmmLine(dialog.getText(), dialog.getColor());
        line.getShapes().add(newStart);
        line.getShapes().add(newLine);
        line.getShapes().add(newEnd);

        // ADD LINE TO THE MAP
        dataManager.getLines().put(dialog.getText(), line);

        // SYNC WITH WORKSPACE
        workspace.getMetroLineComboBox().getItems().add(dialog.getText());
        workspace.getMetroLineComboBox().setValue(dialog.getText());

        // ADD THESE SHAPES TO THE OBSERVABLE LIST
        dataManager.addShape(newLine);
        dataManager.addShape(newStart);
        dataManager.addShape(newEnd);
        dataManager.setSelectedShape(newLine);
        dataManager.highlightShape(newLine);

        // SAVE FOR TRANSACTIONS
        lineName = dialog.getText();
        lineShape = newLine;
        startShape = newStart;
        endShape = newEnd;

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
        
        // WHEN USER FAILS TO ADD LINE, INVALID TRANSACTION OBJECT GETS PUSHED
        if (!(lineName == null || lineShape == null || startShape == null || startShape == null)) {
            // GET THE WORKSPACE
            mmmWorkspace workspace = (mmmWorkspace) app.getWorkspaceComponent();

            // REMOVE FROM EVERYWHERE
            workspace.getMetroLineComboBox().getItems().remove(lineName);
            dataManager.getLines().remove(lineName);
            dataManager.removeShape(lineShape);
            dataManager.removeShape(startShape);
            dataManager.removeShape(endShape);

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
