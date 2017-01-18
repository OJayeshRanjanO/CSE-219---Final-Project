package saf.controller;

import saf.ui.AppYesNoCancelDialogSingleton;
import saf.ui.AppMessageDialogSingleton;
import saf.ui.AppGUI;
import saf.components.AppFileComponent;
import saf.components.AppDataComponent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import properties_manager.PropertiesManager;
import saf.AppTemplate;
import static saf.components.AppStyleArbiter.BUTTON_STYLING;
import static saf.components.AppStyleArbiter.LABEL_STYLING;
import static saf.components.AppStyleArbiter.NEW_MAP_DIALOG_LABEL;
import static saf.components.AppStyleArbiter.POP_UP_BOX;
import static saf.components.AppStyleArbiter.PROGRESS_BAR;
import static saf.components.AppStyleArbiter.PROGRESS_BAR_TRACK;
import static saf.settings.AppPropertyType.APP_CSS;
import static saf.settings.AppPropertyType.APP_LOGO;
import static saf.settings.AppPropertyType.APP_PATH_CSS;
import static saf.settings.AppPropertyType.CHOOSE_MAP;
import static saf.settings.AppPropertyType.CONFIRM_BUTTON_TEXT;
import static saf.settings.AppPropertyType.EXPORT_COMPLETED_MESSAGE;
import static saf.settings.AppPropertyType.EXPORT_COMPLETED_TITLE;
import static saf.settings.AppPropertyType.EXPORT_ERROR_MESSAGE;
import static saf.settings.AppPropertyType.LOAD_ERROR_MESSAGE;
import static saf.settings.AppPropertyType.LOAD_ERROR_TITLE;
import static saf.settings.AppPropertyType.LOAD_WORK_TITLE;
import static saf.settings.AppPropertyType.MAP_POLYGON;
import static saf.settings.AppPropertyType.WORK_FILE_EXT;
import static saf.settings.AppPropertyType.WORK_FILE_EXT_DESC;
import static saf.settings.AppPropertyType.NEW_COMPLETED_MESSAGE;
import static saf.settings.AppPropertyType.NEW_COMPLETED_TITLE;
import static saf.settings.AppPropertyType.NEW_ERROR_MESSAGE;
import static saf.settings.AppPropertyType.NEW_ERROR_TITLE;
import static saf.settings.AppPropertyType.NEW_MAP_DIALOG;
import static saf.settings.AppPropertyType.PARENT_DIRECTORY;
import static saf.settings.AppPropertyType.PARENT_FOLDER;
import static saf.settings.AppPropertyType.REGION_FOLDER;
import static saf.settings.AppPropertyType.SAVE_COMPLETED_MESSAGE;
import static saf.settings.AppPropertyType.SAVE_COMPLETED_TITLE;
import static saf.settings.AppPropertyType.SAVE_ERROR_MESSAGE;
import static saf.settings.AppPropertyType.SAVE_ERROR_TITLE;
import static saf.settings.AppPropertyType.SAVE_UNSAVED_WORK_MESSAGE;
import static saf.settings.AppPropertyType.SAVE_UNSAVED_WORK_TITLE;
import static saf.settings.AppPropertyType.SAVE_WORK_TITLE;
import static saf.settings.AppStartupConstants.FILE_PROTOCOL;
import static saf.settings.AppStartupConstants.PATH_EMPTY;
import static saf.settings.AppStartupConstants.PATH_EXPORT_MAP;
import static saf.settings.AppStartupConstants.PATH_IMAGES;
import static saf.settings.AppStartupConstants.PATH_NEW_DIRECTORY;
import static saf.settings.AppStartupConstants.PATH_NEW_MAP;
import static saf.settings.AppStartupConstants.PATH_SAVE_MAP;
import static saf.settings.AppStartupConstants.PATH_WORK;

/**
 * This class provides the event programmed responses for the file controls that
 * are provided by this framework.
 *
 * @author Richard McKenna
 * @author ?
 * @version 1.0
 */
public class AppFileController {

    // HERE'S THE APP
    AppTemplate app;

    // WE WANT TO KEEP TRACK OF WHEN SOMETHING HAS NOT BEEN SAVED
    boolean saved;

    // THIS IS THE FILE FOR THE WORK CURRENTLY BEING WORKED ON
    File currentWorkFile;

    File selectedFile;
      
    File selectedDirectory;
    
    String regionDirectoryPath;
    
    TextField regionDirectoryField;

    PropertiesManager props = PropertiesManager.getPropertiesManager();

    boolean parentBoolen = true;
    boolean regionBoolen = true;
    boolean chooseMapBoolean = true;
    boolean workspaceStatus = true; // check if workspace is activated

    Button confirmButton = new Button(props.getProperty(CONFIRM_BUTTON_TEXT));

    ;

    /**
     * This constructor just keeps the app for later.
     *
     * @param initApp The application within which this controller will provide
     * file toolbar responses.
     */
    public AppFileController(AppTemplate initApp) {
        // NOTHING YET
        saved = true;
        app = initApp;
    }

    /**
     * This method marks the appropriate variable such that we know that the
     * current Work has been edited since it's been saved. The UI is then
     * updated to reflect this.
     *
     * @param gui The user interface editing the Work.
     */
    public void markAsEdited(AppGUI gui) {
        // THE WORK IS NOW DIRTY
        saved = false;

        // LET THE UI KNOW
        gui.updateToolbarControls(saved);
    }

    /**
     * This method starts the process of editing new Work. If work is already
     * being edited, it will prompt the user to save it first.
     *
     */
    public void handleNewRequest() {
        AppMessageDialogSingleton dialog = AppMessageDialogSingleton.getSingleton();
        PropertiesManager props = PropertiesManager.getPropertiesManager();

        try {
            // WE MAY HAVE TO SAVE CURRENT WORK
            boolean continueToMakeNew = true;
            if (!saved) {
                // THE USER CAN OPT OUT HERE WITH A CANCEL
                continueToMakeNew = promptToSave();
            }

            // IF THE USER REALLY WANTS TO MAKE A NEW COURSE
            if (continueToMakeNew) {
                // Dialog box for new map pops up as required
                try{
                newMapRequest();}
                catch(Exception e){};
                if (isWorkspaceStatus()) {//if workspace is activated
                    //reset the variables
                    parentBoolen = true;
                    regionBoolen = true;
                    chooseMapBoolean = true;
                    // MAKE SURE THE WORKSPACE IS ACTIVATED
                    app.getWorkspaceComponent().activateWorkspace(app.getGUI().getAppPane());
                    // WORK IS NOT SAVED
                    saved = false;
                    currentWorkFile = null;
                    // REFRESH THE GUI, WHICH WILL ENABLE AND DISABLE THE APPROPRIATE CONTROLS
                    app.getGUI().updateToolbarControls(saved);
                    // TELL THE USER NEW WORK IS UNDERWAY
                    dialog.show(props.getProperty(NEW_COMPLETED_TITLE), props.getProperty(NEW_COMPLETED_MESSAGE));
                }
                workspaceStatus = true; // reset
            }
        } catch (Exception ioe) {
            // SOMETHING WENT WRONG, PROVIDE FEEDBACK
            dialog.show(props.getProperty(NEW_ERROR_TITLE), props.getProperty(NEW_ERROR_MESSAGE));
        }
    }

    private void createNewMap() {
        // WE'LL NEED TO GET CUSTOMIZED STUFF WITH THIS
        props = PropertiesManager.getPropertiesManager();

        // ONLY OPEN A NEW FILE IF THE USER SAYS OK
        if (selectedFile != null) {
            try {
                AppDataComponent dataManager = app.getDataComponent();
                AppFileComponent fileManager = app.getFileComponent();
                dataManager.reset();
                fileManager.newData(dataManager, selectedFile.getAbsolutePath());
                app.getWorkspaceComponent().reloadWorkspace();

                // MAKE SURE THE WORKSPACE IS ACTIVATED
//                app.getWorkspaceComponent().activateWorkspace(app.getGUI().getAppPane());
//                saved = true;
//                app.getGUI().updateToolbarControls(saved);
            } catch (Exception e) {
                AppMessageDialogSingleton dialog = AppMessageDialogSingleton.getSingleton();
                dialog.show(props.getProperty(LOAD_ERROR_TITLE), props.getProperty(LOAD_ERROR_MESSAGE));
            }
        }
    }
DirectoryChooser chooser;
    public void newMapRequest() {
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        Stage newMapDialogStage = new Stage();
        String appIcon = FILE_PROTOCOL + PATH_IMAGES + props.getProperty(APP_LOGO);
        newMapDialogStage.getIcons().add(new Image(appIcon));
        newMapDialogStage.setTitle(props.getProperty(NEW_MAP_DIALOG));

        confirmButton.setDisable(true);

        //NEW MAP DIALOG
        Label parentDirectoryLabel = new Label(props.getProperty(PARENT_FOLDER));
        parentDirectoryLabel.setPadding(new Insets(0, 20, 0, 0));      
        TextField parentDirectoryField = new TextField();
//        parentDirectoryField.setPadding(new Insets(10,10,10,10));
        parentDirectoryField.setEditable(false);
        HBox parentDirectoryPane = new HBox(parentDirectoryLabel, parentDirectoryField);
        Button parentDirectoryButton = app.getGUI().initChildButton(parentDirectoryPane, PARENT_DIRECTORY.toString(), "Choose parent region", false);

        parentDirectoryPane.setPadding(new Insets(20, 0, 0, 10));
        parentDirectoryButton.setOnAction(f -> {
            chooser = new DirectoryChooser();
            chooser.setTitle(props.getProperty("Choose Directory"));
            chooser.setInitialDirectory(new File(PATH_EXPORT_MAP));
            selectedDirectory = chooser.showDialog(app.getGUI().getWindow());
            parentDirectoryField.setText(selectedDirectory.getName());
            if (!(parentDirectoryField.getText().trim().equals(""))) {
                setParentBoolen(false); // setting it to false to allow disabling of buttons
            }
            if (!(isParentBoolen() || isRegionBoolen() || isChooseMapBoolean())) {
                confirmButton.setDisable(false);
            }
        });

        Label regionDirectoryLabel = new Label(props.getProperty(REGION_FOLDER));
        regionDirectoryLabel.setPadding(new Insets(0, 16, 0, 0));
        regionDirectoryField = new TextField();
        HBox regionDirectoryPane = new HBox(regionDirectoryLabel, regionDirectoryField);
        regionDirectoryPane.setPadding(new Insets(20, 0, 0, 10));
        regionDirectoryField.setOnKeyPressed(e -> {
            if (!(regionDirectoryField.getText().trim().equals(""))) {
                setRegionBoolen(false); // setting it to false to allow disabling of buttons
            }
            if (!(isParentBoolen() || isRegionBoolen() || isChooseMapBoolean())) {
                confirmButton.setDisable(false);
            }
        });

        Button chooseMapButton = new Button(props.getProperty(MAP_POLYGON));
        chooseMapButton.setOnAction(f -> {
            FileChooser fc = new FileChooser();
            fc.setInitialDirectory(new File(PATH_NEW_MAP)); // needs to be fixed
            fc.setTitle(props.getProperty(CHOOSE_MAP));
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json");
            fc.getExtensionFilters().add(extFilter);
            selectedFile = fc.showOpenDialog(app.getGUI().getWindow());
            newMapDialogStage.setAlwaysOnTop(true);
            if (selectedFile != null) {
                setChooseMapBoolean(false);
            }
            if (!(isParentBoolen() || isRegionBoolen() || isChooseMapBoolean())) {
                confirmButton.setDisable(false);
            }
        });
        HBox chooseMapPane = new HBox(chooseMapButton);
        chooseMapPane.setPadding(new Insets(20, 0, 0, 114));

        ProgressBar loadingBar = new ProgressBar(0);
        loadingBar.setPrefWidth(280);
        HBox loadingBarPane = new HBox(loadingBar);
        loadingBarPane.setPadding(new Insets(20, 0, 0, 10));
//        loadingBarPane.setVisible(false);

        confirmButton.setOnAction(f -> {
            try {

                //Making Child Directory
                regionDirectoryPath = selectedDirectory + PATH_NEW_DIRECTORY + regionDirectoryField.getText() + PATH_NEW_DIRECTORY;
                new File(regionDirectoryPath).mkdir();

                app.getDataComponent().setRegion(regionDirectoryField.getText()); // SETTING THE NAME OF THE REGION
                app.getDataComponent().setParentRegion(selectedDirectory.getPath()); // SETTING THE NAME OF THE REGION

                createNewMap();
                newMapDialogStage.close();

            } catch (Exception e) {
                AppMessageDialogSingleton dialog = AppMessageDialogSingleton.getSingleton();
                dialog.show(props.getProperty(NEW_ERROR_TITLE), props.getProperty(NEW_ERROR_MESSAGE));
            }

            //fake progress bar
//            double max = 20000;
//            double perc;
//            for (int i = 0; i < 20000; i++) {
//                perc = i / max;
//                loadingBar.setProgress(perc);
//            }
//            //Fake time lapse
//            Timeline timeline = new Timeline(new KeyFrame(
//                    Duration.millis(1000),
//                    ae -> newMapDialogStage.close()));
//            timeline.play();
        });
        newMapDialogStage.setOnCloseRequest(e -> {
            if (app.getWorkspaceComponent().getWorkspace() != null) {
                newMapDialogStage.close();
                setWorkspaceStatus(false);
            } else {
                System.exit(0);
                setWorkspaceStatus(false);
            }
        });

        HBox confirmBoxPane = new HBox(confirmButton);
        confirmBoxPane.setPadding(new Insets(20, 0, 0, 10));

        Pane chooseMapConfirmBoxPane = new Pane(chooseMapPane, confirmBoxPane);
        VBox newMapDialog = new VBox(parentDirectoryPane, regionDirectoryPane, chooseMapConfirmBoxPane, loadingBarPane);

        // TELL THE USER NEW WORK IS UNDERWAY
        newMapDialogStage.initModality(Modality.APPLICATION_MODAL);
        Scene scene = new Scene(newMapDialog, 350, 200);
        newMapDialogStage.setScene(scene);

        // SELECT THE STYLESHEET
        String stylesheet = props.getProperty(APP_PATH_CSS);
        stylesheet += props.getProperty(APP_CSS);
        URL stylesheetURL = app.getClass().getResource(stylesheet);
        String stylesheetPath = stylesheetURL.toExternalForm();
        scene.getStylesheets().add(stylesheetPath);
        //ADDING STYLES
        newMapDialog.getStyleClass().add(POP_UP_BOX);
        parentDirectoryLabel.getStyleClass().add(NEW_MAP_DIALOG_LABEL);
        regionDirectoryLabel.getStyleClass().add(NEW_MAP_DIALOG_LABEL);
        chooseMapButton.getStyleClass().add(BUTTON_STYLING);
        confirmButton.getStyleClass().add(BUTTON_STYLING);
        loadingBar.getStyleClass().add(PROGRESS_BAR);
        loadingBar.getStyleClass().add(PROGRESS_BAR_TRACK);
        newMapDialogStage.showAndWait();
    }

    public void setParentBoolen(boolean parentBoolen) {
        this.parentBoolen = parentBoolen;
    }

    public void setRegionBoolen(boolean regionBoolen) {
        this.regionBoolen = regionBoolen;
    }

    public void setChooseMapBoolean(boolean chooseMapBoolean) {
        this.chooseMapBoolean = chooseMapBoolean;
    }

    public boolean isParentBoolen() {
        return parentBoolen;
    }

    public boolean isRegionBoolen() {
        return regionBoolen;
    }

    public boolean isChooseMapBoolean() {
        return chooseMapBoolean;
    }

    public boolean isWorkspaceStatus() {
        return workspaceStatus;
    }

    public void setWorkspaceStatus(boolean workspaceStatus) {
        this.workspaceStatus = workspaceStatus;
    }

    /**
     * This method lets the user open a Course saved to a file. It will also
     * make sure data for the current Course is not lost.
     *
     * @param gui The user interface editing the course.
     */
    public void handleLoadRequest() {
        try {
            // WE MAY HAVE TO SAVE CURRENT WORK
            boolean continueToOpen = true;
            if (!saved) {
                // THE USER CAN OPT OUT HERE WITH A CANCEL
                continueToOpen = promptToSave();
            }

            // IF THE USER REALLY WANTS TO OPEN A Course
            if (continueToOpen) {
                // GO AHEAD AND PROCEED LOADING A Course
                promptToOpen();
            }
        } catch (Exception ioe) {
            // SOMETHING WENT WRONG
            AppMessageDialogSingleton dialog = AppMessageDialogSingleton.getSingleton();
            PropertiesManager props = PropertiesManager.getPropertiesManager();
            dialog.show(props.getProperty(LOAD_ERROR_TITLE), props.getProperty(LOAD_ERROR_MESSAGE));
        }
    }

    /**
     * This method will save the current course to a file. Note that we already
     * know the name of the file, so we won't need to prompt the user.
     *
     *
     * @param courseToSave The course being edited that is to be saved to a
     * file.
     */
    public void handleSaveRequest() {
        // WE'LL NEED THIS TO GET CUSTOM STUFF
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        try {
            // MAYBE WE ALREADY KNOW THE FILE
            if (currentWorkFile != null) {
                saveWork(currentWorkFile);
            } // OTHERWISE WE NEED TO PROMPT THE USER
            else {
                // PROMPT THE USER FOR A FILE NAME
                FileChooser fc = new FileChooser();
                fc.setInitialDirectory(new File(PATH_WORK));
                fc.setTitle(props.getProperty(SAVE_WORK_TITLE));
                FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json");
                fc.getExtensionFilters().add(extFilter);

                File selectedFile = fc.showSaveDialog(app.getGUI().getWindow());
                if (selectedFile != null) {
                    saveWork(selectedFile);
                }
            }
        } catch (IOException ioe) {
            AppMessageDialogSingleton dialog = AppMessageDialogSingleton.getSingleton();
            dialog.show(props.getProperty(LOAD_ERROR_TITLE), props.getProperty(LOAD_ERROR_MESSAGE));
        }
    }

    // HELPER METHOD FOR SAVING WORK
    private void saveWork(File selectedFile) throws IOException {
        // SAVE IT TO A FILE
        app.getFileComponent().saveData(app.getDataComponent(), selectedFile.getPath());

        // MARK IT AS SAVED
        currentWorkFile = selectedFile;
        saved = true;

        // TELL THE USER THE FILE HAS BEEN SAVED
        AppMessageDialogSingleton dialog = AppMessageDialogSingleton.getSingleton();
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        dialog.show(props.getProperty(SAVE_COMPLETED_TITLE), props.getProperty(SAVE_COMPLETED_MESSAGE));

        // AND REFRESH THE GUI, WHICH WILL ENABLE AND DISABLE
        // THE APPROPRIATE CONTROLS
        app.getGUI().updateToolbarControls(saved);
    }

    public void handleExportRequest() throws IOException {
        props = PropertiesManager.getPropertiesManager();

        if (selectedFile != null) { // FIXME./Europe/
            AppFileComponent fileManager = app.getFileComponent();
            AppDataComponent dataManager = app.getDataComponent();
//            String regionFolder = selectedFile.getName().substring(0, selectedFile.getName().lastIndexOf("."));
//            System.out.println(selectedFile.getName().substring(0,selectedFile.getName().lastIndexOf(".")) + ".rvm");
//            File exportFile = new File(PATH_EXPORT_MAP + "./The World/./" + dataManager.getParentRegion()
//                    + "/./" + regionFolder + "/./" + selectedFile.getName().substring(0, selectedFile.getName().lastIndexOf("."))
//                    + props.getProperty(WORK_FILE_EXT));
//System.out.println(dataManager.getParentRegion());
//            System.out.println(dataManager.getRegion());
//System.out.println();
            File exportFile = new File(dataManager.getParentRegion() +"\\"+ dataManager.getRegion()+"\\"+ dataManager.getRegion() + props.getProperty(WORK_FILE_EXT));
            fileManager.exportData(dataManager, exportFile.getPath());
            AppMessageDialogSingleton dialog = AppMessageDialogSingleton.getSingleton();
            dialog.show(props.getProperty(EXPORT_COMPLETED_TITLE), props.getProperty(EXPORT_COMPLETED_MESSAGE));
        } else {
            AppMessageDialogSingleton dialog = AppMessageDialogSingleton.getSingleton();
            dialog.show(props.getProperty(EXPORT_COMPLETED_TITLE), props.getProperty(EXPORT_ERROR_MESSAGE));
        }

    }

    /**
     * This method will exit the application, making sure the user doesn't lose
     * any data first.
     *
     */
    public void handleExitRequest() {
        try {
            // WE MAY HAVE TO SAVE CURRENT WORK
            boolean continueToExit = true;
            if (!saved) {
                // THE USER CAN OPT OUT HERE
                continueToExit = promptToSave();
            }

            // IF THE USER REALLY WANTS TO EXIT THE APP
            if (continueToExit) {
                // EXIT THE APPLICATION
                System.exit(0);
            }
        } catch (Exception ioe) {
            AppMessageDialogSingleton dialog = AppMessageDialogSingleton.getSingleton();
            PropertiesManager props = PropertiesManager.getPropertiesManager();
            dialog.show(props.getProperty(SAVE_ERROR_TITLE), props.getProperty(SAVE_ERROR_MESSAGE));
        }
    }

    /**
     * This helper method verifies that the user really wants to save their
     * unsaved work, which they might not want to do. Note that it could be used
     * in multiple contexts before doing other actions, like creating new work,
     * or opening another file. Note that the user will be presented with 3
     * options: YES, NO, and CANCEL. YES means the user wants to save their work
     * and continue the other action (we return true to denote this), NO means
     * don't save the work but continue with the other action (true is
     * returned), CANCEL means don't save the work and don't continue with the
     * other action (false is returned).
     *
     * @return true if the user presses the YES option to save, true if the user
     * presses the NO option to not save, false if the user presses the CANCEL
     * option to not continue.
     */
    private boolean promptToSave() throws IOException {
        PropertiesManager props = PropertiesManager.getPropertiesManager();

        // CHECK TO SEE IF THE CURRENT WORK HAS
        // BEEN SAVED AT LEAST ONCE
        // PROMPT THE USER TO SAVE UNSAVED WORK
        AppYesNoCancelDialogSingleton yesNoDialog = AppYesNoCancelDialogSingleton.getSingleton();
        yesNoDialog.show(props.getProperty(SAVE_UNSAVED_WORK_TITLE), props.getProperty(SAVE_UNSAVED_WORK_MESSAGE));

        // AND NOW GET THE USER'S SELECTION
        String selection = yesNoDialog.getSelection();

        // IF THE USER SAID YES, THEN SAVE BEFORE MOVING ON
        if (selection.equals(AppYesNoCancelDialogSingleton.YES)) {
            // SAVE THE DATA FILE
            AppDataComponent dataManager = app.getDataComponent();
//            saveWork(currentWorkFile);
            saved = true;handleSaveRequest();
                saveWork(currentWorkFile);
                saved = true;
        } // IF THE USER SAID CANCEL, THEN WE'LL TELL WHOEVER
        // CALLED THIS THAT THE USER IS NOT INTERESTED ANYMORE
        else if (selection.equals(AppYesNoCancelDialogSingleton.CANCEL)) {
            return false;
        }

        // IF THE USER SAID NO, WE JUST GO ON WITHOUT SAVING
        // BUT FOR BOTH YES AND NO WE DO WHATEVER THE USER
        // HAD IN MIND IN THE FIRST PLACE
        return true;
    }

    /**
     * This helper method asks the user for a file to open. The user-selected
     * file is then loaded and the GUI updated. Note that if the user cancels
     * the open process, nothing is done. If an error occurs loading the file, a
     * message is displayed, but nothing changes.
     */
    private void promptToOpen() {
        // WE'LL NEED TO GET CUSTOMIZED STUFF WITH THIS
        props = PropertiesManager.getPropertiesManager();

        // AND NOW ASK THE USER FOR THE FILE TO OPEN
        FileChooser fc = new FileChooser();
        fc.setInitialDirectory(new File(PATH_WORK));
        fc.setTitle(props.getProperty(LOAD_WORK_TITLE));
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json");
        fc.getExtensionFilters().add(extFilter);
        selectedFile = fc.showOpenDialog(app.getGUI().getWindow());

        // ONLY OPEN A NEW FILE IF THE USER SAYS OK
        if (selectedFile != null) {
            try {
                AppDataComponent dataManager = app.getDataComponent();
                AppFileComponent fileManager = app.getFileComponent();
                dataManager.reset();
                fileManager.loadData(dataManager, selectedFile.getAbsolutePath());
                app.getWorkspaceComponent().reloadWorkspace();

                // MAKE SURE THE WORKSPACE IS ACTIVATED
                app.getWorkspaceComponent().activateWorkspace(app.getGUI().getAppPane());
                saved = true;
                app.getGUI().updateToolbarControls(saved);
            } catch (Exception e) {
                AppMessageDialogSingleton dialog = AppMessageDialogSingleton.getSingleton();
                dialog.show(props.getProperty(LOAD_ERROR_TITLE), props.getProperty(LOAD_ERROR_MESSAGE));
            }
        }
    }

    /**
     * This mutator method marks the file as not saved, which means that when
     * the user wants to do a file-type operation, we should prompt the user to
     * save current work first. Note that this method should be called any time
     * the course is changed in some way.
     */
    public void markFileAsNotSaved() {
        saved = false;
    }

    /**
     * Accessor method for checking to see if the current work has been saved
     * since it was last edited.
     *
     * @return true if the current work is saved to the file, false otherwise.
     */
    public boolean isSaved() {
        return saved;
    }
}
