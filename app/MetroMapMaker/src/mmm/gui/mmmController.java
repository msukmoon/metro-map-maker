package mmm.gui;

import static djf.settings.AppPropertyType.LOAD_WORK_TITLE;
import static djf.settings.AppStartupConstants.PATH_WORK;
import properties_manager.PropertiesManager;
import djf.AppTemplate;
import djf.ui.AppMessageDialogSingleton;
import djf.ui.AppTextEnterDialogSingleton;
import java.io.File;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import jtps.jTPS;
import mmm.data.mmmData;
import mmm.data.mmmState;

/**
 *
 * @author myungsuk
 */
public class mmmController {
    // TRANSACTIONS
    static jTPS jtps;
    
    AppTemplate app;
    mmmData dataManager;
    
    public mmmController(AppTemplate initApp) {
        app = initApp;
	dataManager = (mmmData)app.getDataComponent();
        
        // TRANSACTIONS
        jtps = app.getJTPS();
    }
    
    public void processAbout() {
        // GET THE DIALOG SINGLETON
        AppMessageDialogSingleton dialog = AppMessageDialogSingleton.getSingleton();
        
        // POP UP THE DIALOG
        dialog.show("About This Application", "Application Name: Metro Map Maker\n"
                + "Written By: Myungsuk Moon and Richard McKenna\n"
                + "Framework Used: DesktopJavaFramework (by Richard McKenna)\n"
                + "Year of Work: 2017");
    }
    
    public void processMetroLineSelection(String selection) {
        // UPDATE THE WORKSPACE ACCORDINGLY
        dataManager.metroLineSelection(selection);
    }
    
    public void processAddLine() {
        // GET THE DIALOG SINGLETON
        AppTextColorEnterDialogSingleton dialog = AppTextColorEnterDialogSingleton.getSingleton();

        // POP UP THE DIALOG
        dialog.show("Add Metro Line", "Enter Name and Color of the Metro Line:", Color.web("#cccc33"));

        // IF THE USER SAID YES
        if (dialog.getSelection()) {
            // CHANGE THE CURSOR
            Scene scene = app.getGUI().getPrimaryScene();
            scene.setCursor(Cursor.CROSSHAIR);
        
            // CHANGE THE STATE
            dataManager.setState(mmmState.ADD_LINE_MODE);
        }
    }
    
    public void processRemoveLine() {
        // GET THE DIALOG SINGLETON
        AppYesNoDialogSingleton dialog = AppYesNoDialogSingleton.getSingleton();
        
        // POP UP THE DIALOG
        dialog.show("Remove Line", "Are you sure you want to remove this line?");
        
        // DO REMOVE LINE 
        dataManager.removeLine();
    }
    
    public void processEditLine() {
        // DO EDIT LINE
        dataManager.editLine();
    }
    
    public void processListAllStationInLine() {
        // LIST ALL STATIONS
        dataManager.listAllStationInLine();
    }
    
    public void processChangeLineThickness() {
        // GET THE WORKSPACE
        mmmWorkspace workspace = (mmmWorkspace)app.getWorkspaceComponent();
        
        // GET THE VALUE FROM THE WORKSPACE
        // THEN DIVIDE BY THREE BECAUSE THE VALUE IS TOO BIG
	double width = workspace.getChangeLineThicknessSlider().getValue() / 5;
        
        // SET THE THICKNESS IN THE DATA MANAGER
	dataManager.changeLineThickness(width);
    }
    
    public void processAddStationToLine() {
        // CHANGE THE CURSOR
        Scene scene = app.getGUI().getPrimaryScene();
        scene.setCursor(Cursor.CROSSHAIR);
        
        // CHANGE THE STATE
        dataManager.setState(mmmState.ADD_STATION_TO_LINE_MODE);
    }
    
    public void processRemoveStationFromLine() {
        // CHANGE THE CURSOR
        Scene scene = app.getGUI().getPrimaryScene();
        scene.setCursor(Cursor.CROSSHAIR);
        
        // CHANGE THE STATE
        dataManager.setState(mmmState.REMOVE_STATION_FROM_LINE_MODE);
    }
    
    public void processMetroStationSelection(String selection) {
        // UPDATE THE WORKSPACE ACCORDINGLY
        dataManager.metroStationSelection(selection);
    }
    
    public void processAddStation() {
        // GET THE DIALOG SINGLETON
        AppTextEnterDialogSingleton dialog = AppTextEnterDialogSingleton.getSingleton();

        // POP UP THE DIALOG
        dialog.show("Add Metro Station", "Enter Name of the Metro Station:");
        
        // CHANGE THE CURSOR
        Scene scene = app.getGUI().getPrimaryScene();
        scene.setCursor(Cursor.CROSSHAIR);
        
        // CHANGE THE STATE
        dataManager.setState(mmmState.ADD_STATION_MODE);
    }
    
    public void processRemoveStation() {
        // GET THE DIALOG SINGLETON
        AppYesNoDialogSingleton dialog = AppYesNoDialogSingleton.getSingleton();
        
        // POP UP THE DIALOG
        dialog.show("Remove Station", "Are you sure you want to remove this station?");
        
        // DO REMOVE LINE 
        dataManager.removeStation();     
    }
        
    public void processSnapToGrid() {
        // DO SNAP TO GRID
        dataManager.snapToGrid();
    }
    
    public void processMoveStationLabel() {
        // MOVE LABEL
        dataManager.moveStationLabel();
    }
    
    public void processRotateStationLabel() {
        // ROTATE LABEL
        dataManager.rotateStationLabel();
    }
    
    public void processChangeStationFillColor(Color selection) {
        // CHANGE STATION COLOR
        dataManager.changeStationFillColor(selection);
    }
    
    public void processChangeStationRadius() {
        // GET THE WORKSPACE
        mmmWorkspace workspace = (mmmWorkspace)app.getWorkspaceComponent();
        
        // GET THE VALUE FROM THE WORKSPACE
        // THEN DIVIDE BY THREE BECAUSE THE VALUE IS TOO BIG
	double radius = workspace.getChangeStationRadiusSlider().getValue() / 3;
        
        // SET THE THICKNESS IN THE DATA MANAGER
	dataManager.changeStationRadius(radius);
    }
    
    public void processFindRoute() {
        // FIND ROUTE
	dataManager.findRoute();
    }
    
    public void processSetBackgroundColor(Color selection) {
        // CHANGE BACKGROUND COLOR
        dataManager.setBackgroundColor(selection);
    }
    
    public void processSetImageBackground() {
        // GET THE DIALOG SINGLETON
        AppYesNoDialogSingleton dialog = AppYesNoDialogSingleton.getSingleton();
        
        // POP UP THE DIALOG
        dialog.show("Set Image Background", "Would you like to have a background image?");
        
        // IF USER CHOSE YES
        if (dialog.getSelection()) {
            // WE'LL NEED TO GET CUSTOMIZED STUFF WITH THIS
            PropertiesManager props = PropertiesManager.getPropertiesManager();

            // AND NOW ASK THE USER FOR THE FILE TO OPEN
            FileChooser fc = new FileChooser();
            fc.setInitialDirectory(new File(PATH_WORK));
            fc.setTitle(props.getProperty(LOAD_WORK_TITLE));
            File selectedFile = fc.showOpenDialog(app.getGUI().getWindow());
            
            // SET THE BACKGROUND IMAGE
            dataManager.setImageBackground(selectedFile);
        }
        else {
            // REMOVE THE BACKGROUND IMAGE
            dataManager.removeImageBackground();
        }  
    }
    
    public void processAddImageOverlay() {
        // WE'LL NEED TO GET CUSTOMIZED STUFF WITH THIS
        PropertiesManager props = PropertiesManager.getPropertiesManager();

        // AND NOW ASK THE USER FOR THE FILE TO OPEN
        FileChooser fc = new FileChooser();
        fc.setInitialDirectory(new File(PATH_WORK));
        fc.setTitle(props.getProperty(LOAD_WORK_TITLE));
        File imageOverlayFile = fc.showOpenDialog(app.getGUI().getWindow());
        
        // SEND THE IMAGE FILE TO DATA MANAGER
        dataManager.setImageOverlayFile(imageOverlayFile);
        
        // CHANGE THE CURSOR
        Scene scene = app.getGUI().getPrimaryScene();
        scene.setCursor(Cursor.CROSSHAIR);
        
        // CHANGE THE STATE
        dataManager.setState(mmmState.ADD_IMAGE_MODE);
    }
    
    public void processAddLabel() {
        // GET THE DIALOG SINGLETON
        AppTextEnterDialogSingleton dialog = AppTextEnterDialogSingleton.getSingleton();

        // POP UP THE DIALOG
        dialog.show("Add Label", "Enter text of the label to add:");
        
        // CHANGE THE CURSOR
        Scene scene = app.getGUI().getPrimaryScene();
        scene.setCursor(Cursor.CROSSHAIR);
        
        // CHANGE THE STATE
        dataManager.setState(mmmState.ADD_LABEL_MODE);
    }
    
    public void processRemoveMapElement() {
        // REMOVE MAP ELEMENT
        dataManager.removeMapElement();
    }
    
    public void processFontColorSelection(Color selection) {
        // CHANGE THE TEXT COLOR
        dataManager.setFontColor(selection);
    }
    
    public void processSetBold() {
        // SET BOLD
        dataManager.setBold();
    }
    
    public void processSetItalic() {
        // SET ITALIC
        dataManager.setItalic();
    }
    
    public void processFontSizeSelection(int selection) {
        // SET FONT SIZE
        dataManager.setFontSize(selection);
    }
    
    public void processFontFamilySelection(String selection) {
        // SET FONT FAMILY
        dataManager.setFontFamily(selection);
    }

    public void processZoomIn() {
        // ZOOM IN
        dataManager.zoomIn();
    }
    
    public void processZoomOut() {
        // ZOOM OUT
        dataManager.zoomOut();
    }
    
    public void processIncreaseMapSize() {
        // INCREASE MAP SIZE
        dataManager.increaseMapSize();
    }
    
    public void processDecreaseMapSize() {
        // DECREASE MAP SIZE
        dataManager.decreaseMapSize();
    }
    
    public void processShowGrid(boolean selected) {
        // SHOW OR DO NOT SHOW GRID
        dataManager.showGrid(selected);
    }
}
