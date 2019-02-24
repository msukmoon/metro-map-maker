package mmm.gui;

import djf.AppTemplate;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.shape.Shape;
import mmm.data.Draggable;
import mmm.data.mmmData;
import mmm.data.mmmState;
/**
 *
 * @author myungsuk
 */
public class MapEditorController {
    
    AppTemplate app;
    
    public MapEditorController(AppTemplate initApp) {
        app = initApp;
    }
    
    /**
     * Respond to mouse presses on the rendering surface, which we call MapEditor,
     * but is actually a Pane.
     */
    public void processMapEditorMousePress(int x, int y) {
        mmmData dataManager = (mmmData) app.getDataComponent();
        if (dataManager.isInState(mmmState.SELECTING_MODE)) {
            // SELECT THE TOP ELEMENT
            Shape shape = dataManager.selectTopShape(x, y);
            Scene scene = app.getGUI().getPrimaryScene();

            // AND START DRAGGING IT
            if (shape != null) {
                scene.setCursor(Cursor.MOVE);
                dataManager.setState(mmmState.DRAGGING_MODE);
                app.getGUI().updateToolbarControls(false);
            } else {
                scene.setCursor(Cursor.DEFAULT);
                dataManager.setState(mmmState.DRAGGING_NOTHING);
                app.getWorkspaceComponent().reloadWorkspace(dataManager);
            }
        } else if (dataManager.isInState(mmmState.ADD_LINE_MODE)) {
            dataManager.addLine(x, y);
        } else if (dataManager.isInState(mmmState.ADD_STATION_TO_LINE_MODE)) {
            dataManager.addStationToLine(x, y);
        } else if (dataManager.isInState(mmmState.REMOVE_STATION_FROM_LINE_MODE)) {
            dataManager.removeStationFromLine(x, y);
        } else if (dataManager.isInState(mmmState.ADD_STATION_MODE)) {
            dataManager.addStation(x, y);
        } else if (dataManager.isInState(mmmState.ADD_IMAGE_MODE)) {
            dataManager.addImageOverlay(x, y);
        } else if (dataManager.isInState(mmmState.ADD_LABEL_MODE)) {
            dataManager.addLabel(x, y);
        }   
        mmmWorkspace workspace = (mmmWorkspace) app.getWorkspaceComponent();
        workspace.reloadWorkspace(dataManager);        
    }
    
        /**
     * Respond to mouse dragging on the rendering surface, which we call MapEditor,
     * but is actually a Pane.
     */
    public void processMapEditorMouseDragged(int x, int y) {
        mmmData dataManager = (mmmData) app.getDataComponent();
        if (dataManager.isInState(mmmState.DRAGGING_MODE) && dataManager.getSelectedShape() instanceof Draggable) {
            Draggable selectedDraggableShape = (Draggable) dataManager.getSelectedShape();
            selectedDraggableShape.drag(x, y);
            app.getGUI().updateToolbarControls(false);
        }
    }

    /**
     * Respond to mouse button release on the rendering surface, which we call MapEditor,
     * but is actually a Pane.
     */
    public void processMapEditorMouseRelease(int x, int y) {
        mmmData dataManager = (mmmData) app.getDataComponent();
        if (dataManager.isInState(mmmState.DRAGGING_MODE)) {
            dataManager.setState(mmmState.SELECTING_MODE);
            Scene scene = app.getGUI().getPrimaryScene();
            scene.setCursor(Cursor.DEFAULT);
            app.getGUI().updateToolbarControls(false);
        } else if (dataManager.isInState(mmmState.DRAGGING_NOTHING)) {
            dataManager.setState(mmmState.SELECTING_MODE);
        }
    }
    
    /**
     * Respond to key press on the rendering surface, which we call MapEditor,
     * but is actually a Pane.
     */
    public void processMapEditorKeyPressed(String key) {
        mmmData dataManager = (mmmData) app.getDataComponent();
        dataManager.navigateMap(key.toUpperCase());
    }
    
}
