package mmm;

import djf.AppTemplate;
import java.util.Locale;
import static javafx.application.Application.launch;
import mmm.data.mmmData;
import mmm.file.mmmFiles;
import mmm.gui.AppColumnedMessageDialogSingleton;
import mmm.gui.AppEditLineDialogSingleton;
import mmm.gui.mmmWorkspace;
import mmm.gui.AppTextColorEnterDialogSingleton;
import mmm.gui.AppYesNoDialogSingleton;

/**
 *
 * @author myungsuk
 */
public class mmmApp extends AppTemplate {

    /**
     * This hook method must initialize all three components in the
     * proper order ensuring proper dependencies are respected, meaning
     * all proper objects are already constructed when they are needed
     * for use, since some may need others for initialization.
     */
    @Override
    public void buildAppComponentsHook() {  
        // BUILD THREE COMPONENTS OF THE APP
        fileComponent = new mmmFiles(this);
        dataComponent = new mmmData(this);      
        workspaceComponent = new mmmWorkspace(this);
        
        // THEN INITIALIZE ALL SINGLETONS IN THE MMM
        AppTextColorEnterDialogSingleton textColorDialog = AppTextColorEnterDialogSingleton.getSingleton();
        textColorDialog.init(super.getGUI().getWindow());
        AppEditLineDialogSingleton textColorCheckDialog = AppEditLineDialogSingleton.getSingleton();
        textColorCheckDialog.init(super.getGUI().getWindow());
        AppYesNoDialogSingleton yesNoDialog = AppYesNoDialogSingleton.getSingleton();
        yesNoDialog.init(super.getGUI().getWindow());
        AppColumnedMessageDialogSingleton columnedMessageDialog = AppColumnedMessageDialogSingleton.getSingleton();
        columnedMessageDialog.init(super.getGUI().getWindow());
    }
    
    /**
     * This is where program execution begins. Since this is a JavaFX app it
     * will simply call launch, which gets JavaFX rolling, resulting in sending
     * the properly initialized Stage (i.e. window) to the start method inherited
     * from AppTemplate, defined in the Desktop Java Framework.
     */
    public static void main(String[] args) {
        Locale.setDefault(Locale.US);
        launch(args);
    }
    
}
