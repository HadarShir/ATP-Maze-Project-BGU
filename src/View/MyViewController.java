package View;

import Model.IModel;
import Model.MyModel;
import ViewModel.MyViewModel;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.fxml.FXML;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;
import java.util.ResourceBundle;

import static View.MainSceneController.stopMenuMusic;
import static View.MainSceneController.startMenuMusic;

public class MyViewController implements Initializable, Observer ,IView {
    public static int mazeRows;
    public static int mazeCols;
    public static String loadedName = null;
    public static boolean mazeType = true;
    public static MediaPlayer mediaPlayer;
    public MyViewModel myViewModel;
    public MazeDisplay mazeDisplay = new MazeDisplay();
    public MazeDisplay mazeDisplayer; // For FXML binding
    public Pane main_pane;
    public ImageView mainImageView;
    public Button backButton;
    public javafx.scene.control.Label statusLabel;
    public static double x,y;
    private long startTime = 0;
    private long endTime = 0;
    private MediaPlayer winMusicPlayer;

    @FXML
    public Button togglePathButton;
    @FXML
    public Button toggleSolutionButton;
    @FXML
    public Button toggleSoundButton;

    public static boolean isMuted = false;

    public static boolean musicChecked = true;

    StringProperty updatePlayerRow = new SimpleStringProperty();
    StringProperty updatePlayerCol = new SimpleStringProperty();

    /**
     * Returns true if music is enabled and not muted.
     */
    public static boolean isMusic() {
        return musicChecked && !isMuted;
    }

    /**
     * Sets whether the current maze is loaded or newly generated.
     *
     * @param state true if loaded from file, false if newly generated
     */
    public static void setMazeType(boolean state) { //Sets either loaded game or new game
        MyViewController.mazeType = state;
    }

    /**
     * Sets the number of rows in the maze.
     *
     * @param mazeRows number of rows
     */
    public static void setMazeRows(int mazeRows) {
        MyViewController.mazeRows = mazeRows;
    }

    /**
     * Sets the number of columns for the maze.
     */
    public static void setMazeCols(int mazeCols) {
        MyViewController.mazeCols = mazeCols;
    }

    /**
     * Binds the ViewModel to this controller and registers as an observer.
     */
    public void setMazeViewModel(MyViewModel myViewModel) {
        this.myViewModel = myViewModel;
        this.myViewModel.addObserver(this);
    }

    /**
     * Requests focus on the maze display when clicked.
     */
    public void mouseClicked(MouseEvent mouseEvent) {
        mazeDisplayer.requestFocus();
    }

    /**
     * Placeholder method for goal reached event.
     */
    public void goalReached() {}

    /**
     * Placeholder method for music playback.
     */
    public void playMusic() {}

    /**
     * Plays a move sound effect.
     */
    private void playMoveSound() {
        if (isMuted) return;
        if (isMusic()) {
            Media moveSound = new Media(getClass().getResource("/music/Step.mp3").toString());
            MediaPlayer movePlayer = new MediaPlayer(moveSound);
            movePlayer.setCycleCount(1);
            movePlayer.setVolume(0.4);
            movePlayer.play();
        }
    }

    /**
     * Solves the maze and shows an information alert.
     * Plays a click sound and calls the ViewModel to solve the maze.
     *
     * @param actionEvent the triggering action event
     */
    public void solveMaze(ActionEvent actionEvent) {
        mouseAudio();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("Solving maze...");
        alert.show();
        myViewModel.solveMaze();
    }

    /**
     * Handles the action of solving the maze and displaying the solution.
     * Disables the player path and updates the status label.
     * If no maze is present, shows a warning alert.
     *
     * @param actionEvent the triggering action event
     */
    public void handleShowSolution(ActionEvent actionEvent) {
        mouseAudio();
        if (mazeDisplayer != null) {
            myViewModel.solveMaze();
            // Hide player path when showing solution
            mazeDisplayer.setShowPlayerPath(false);
            statusLabel.setText("Solution displayed - Follow the yellow path to the goal! Click on maze to continue playing.");
            
            // Ensure focus returns to mazeDisplay for keyboard input
            if (mazeDisplayer != null) {
                mazeDisplayer.requestFocus();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Maze");
            alert.setHeaderText("No maze to solve");
            alert.setContentText("Please generate or load a maze first.");
            alert.showAndWait();
        }
    }

    /**
     * Hides the solution from the maze and re-enables the player path display.
     * Updates the status label and returns focus to the maze display.
     *
     * @param actionEvent the triggering action event
     */
    public void handleHideSolution(ActionEvent actionEvent) {
        mouseAudio();
        if (mazeDisplayer != null) {
            mazeDisplayer.setSolution(null);
            // Show player path again when hiding solution
            mazeDisplayer.setShowPlayerPath(true);
            statusLabel.setText("Solution hidden - Find your own path! Click on maze to continue playing.");
            
            // Ensure focus returns to mazeDisplay for keyboard input
            if (mazeDisplayer != null) {
                mazeDisplayer.requestFocus();
            }
        }
    }

    /**
     * Toggles the display of the player's path on the maze.
     * Updates the toggle button text accordingly.
     *
     * @param actionEvent the triggering action event
     */
    public void handleTogglePlayerPath(ActionEvent actionEvent) {
        boolean currentState = mazeDisplayer.isShowPlayerPath();
        mazeDisplayer.setShowPlayerPath(!currentState);
        if (mazeDisplayer.isShowPlayerPath()) {
            togglePathButton.setText("Hide Path");
        } else {
            togglePathButton.setText("Show Path");
        }
    }

    /**
     * Toggles the display of the solution on the maze.
     * Solves the maze if the solution is not already available.
     *
     * @param actionEvent the triggering action event
     */
    public void handleToggleSolution(ActionEvent actionEvent) {
        boolean isCurrentlyShown = mazeDisplayer.getSolution() != null;
        if (isCurrentlyShown) {
            mazeDisplayer.setSolution(null);
            toggleSolutionButton.setText("Show Solution");
        } else {
            if (myViewModel.getSolution() == null) {
                myViewModel.solveMaze(); // solution will be set and button updated in mazeSolved()
            } else {
                mazeDisplayer.setSolution(myViewModel.getSolution());
                toggleSolutionButton.setText("Hide Solution");
            }
        }
    }

    /**
     * Plays a sound effect for mouse interactions if sound is enabled.
     */
    public void mouseAudio(){
        if (isMuted) return;
        if (isMusic()){
            Media mouseClicked = new Media(getClass().getResource("/music/Click.mp3").toString());
            MediaPlayer mediaPlayer2 = new MediaPlayer(mouseClicked);
            mediaPlayer2.setCycleCount(1);
            mediaPlayer2.play();
            mediaPlayer2.setVolume(0.3);
        }
    }

    /**
     * Prompts the user to name and save the current maze.
     * Validates input and confirms successful save.
     *
     * @param actionEvent the triggering action event
     */
    public void SaveMaze(ActionEvent actionEvent){
        mouseAudio();
        TextInputDialog textInputDialog = new TextInputDialog();
        textInputDialog.setGraphic(null);
        textInputDialog.setHeaderText("Saving Maze:");
        textInputDialog.setTitle("Saving Maze");
        textInputDialog.setContentText("Please enter the saved maze name:");
        Optional<String> result = textInputDialog.showAndWait();
        if (textInputDialog.getResult() != null){
            if (result.get().getClass().equals(String.class) && result.get() != ""){
                myViewModel.saveMaze(result.get());
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Maze saved");
                alert.setContentText("Maze successfully saved");
                alert.setGraphic(null);
                alert.setHeaderText("");
                alert.show();
            }
            else{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Wrong input");
                alert.setContentText("Please enter a valid name");
                alert.show();
            }
        }



    }

    /**
     * Handles keyboard input for player movement.
     * If the player reaches the goal, triggers the win event.
     *
     * @param keyEvent the keyboard input event
     */
    public void keyPressed(KeyEvent keyEvent) {
        if (myViewModel.getMaze() == null) return; // prevent crash

        myViewModel.movePlayer(keyEvent);
        keyEvent.consume();

        if (myViewModel.getPlayerRow() == myViewModel.getMaze().getGoalPosition().getRowIndex() &&
                myViewModel.getPlayerCol() == myViewModel.getMaze().getGoalPosition().getColumnIndex()) {
            won();
        }
    }

    /**
     * Updates the player's position in the display and properties.
     *
     * @param row the new row index
     * @param col the new column index
     */
    public void setPlayerPosition(int row, int col){
        mazeDisplayer.setPlayerPosition(row, col);
        setUpdatePlayerRow(row);
        setUpdatePlayerCol(col);
    }

    /**
     * Retrieves an image resource from the classpath.
     *
     * @param imagePath path to the image resource
     * @return an InputStream of the resource
     */
    private InputStream getImageResourceAsStream(String imagePath){
        return getClass().getClassLoader().getResourceAsStream(imagePath);
    }

    /**
     * Handles the game winning event.
     * Plays win music, displays a winning alert, and gives the user options.
     */
    private void won(){
        // Stop menu music and play win music
        stopMenuMusic();
        if (!isMuted) {
            try {
                Media winMusic = new Media(getClass().getResource("/music/GoalReached.mp3").toString());
                winMusicPlayer = new MediaPlayer(winMusic);
                winMusicPlayer.setCycleCount(1);
                winMusicPlayer.setVolume(0.3);
                winMusicPlayer.play();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        endTime = System.currentTimeMillis();
        long duration = (endTime - startTime) / 1000;
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("You have won !");
        alert.setHeaderText("");
        alert.setContentText("");
        alert.setHeaderText("You finished in " + duration + " seconds!");
        Image image = new Image(getImageResourceAsStream("images/Win.gif"));
        ImageView imageView = new ImageView(image);
        alert.setGraphic(imageView);
        ButtonType returnButton = new ButtonType("New");
        ButtonType restartButton = new ButtonType("Restart");
        alert.getButtonTypes().setAll(returnButton, restartButton);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            if (result.get() == returnButton) {
                // Stop win music and restart menu music
                if (winMusicPlayer != null) winMusicPlayer.stop();
                startMenuMusic();
                try {
                    Parent root = FXMLLoader.load(getClass().getResource("/View/MainScene.fxml"));
                    Stage stage = (Stage) main_pane.getScene().getWindow();
                    stage.setScene(new Scene(root));
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (result.get() == restartButton) {
                if (winMusicPlayer != null) winMusicPlayer.stop();
                restartMaze();
            }
        }
    }

    /**
     * Updates the row property of the player's position.
     *
     * @param updatePlayerRow the new row index as integer
     */
    public void setUpdatePlayerRow(int updatePlayerRow) {
        this.updatePlayerRow.set(updatePlayerRow + "");
    }

    /**
     * Updates the column property of the player's position.
     *
     * @param updatePlayerCol the new column index as integer
     */
    public void setUpdatePlayerCol(int updatePlayerCol) {
        this.updatePlayerCol.set(updatePlayerCol + "");
    }

    /**
     * Receives updates from the ViewModel and dispatches the appropriate handler.
     *
     * @param o    the observable object
     * @param arg  the update argument, indicating the type of update
     */
    @Override
    public void update(Observable o, Object arg) {
        String change = (String) arg;

        switch (change) {
            case "maze generated" -> mazeGenerated();
            case "player moved" -> playerMoved();
            case "maze solved" -> mazeSolved();

            default -> System.out.println("Not implemented change: " + change);
        }
    }

    /**
     * Constructs the controller, sets the ViewModel, and registers as observer.
     *
     * @throws IOException if the model or view model fail to initialize
     */
    public MyViewController() throws IOException{
        IModel model = new MyModel();
        myViewModel = new MyViewModel(model);
        setMazeViewModel(myViewModel);
    }

    /**
     * Generates a new maze using the stored dimensions.
     */
    public void generateNewMaze(){
        System.out.println("generateNewMaze: called");
        this.myViewModel.generateMaze(mazeRows, mazeCols);
        System.out.println("generateNewMaze: after myViewModel.generateMaze");
    }

    /**
     * Initializes the view components after FXML is loaded.
     * Sets up event handlers, music, maze state, and binds UI elements.
     *
     * @param url The location used to resolve relative paths for the root object.
     * @param resourceBundle The resources used to localize the root object.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Connect FXML mazeDisplayer with our mazeDisplay
        if (mazeDisplayer != null) {
            mazeDisplay = mazeDisplayer;
        }

        // האזנה לאירועי מקלדת על ה-Scene עצמו
        mazeDisplayer.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.addEventHandler(KeyEvent.KEY_PRESSED, this::keyPressed);
                newScene.getRoot().requestFocus();
            }
        });
        
        // Add keyboard event handler directly to mazeDisplay
        if (mazeDisplayer != null) {
            mazeDisplayer.setOnKeyPressed(this::keyPressed);
            mazeDisplayer.setFocusTraversable(true);
            mazeDisplayer.setOnMouseClicked(this::mouseClicked);
        }

        if (mazeType == true){
            generateNewMaze();
        }

        if (main_pane != null && mainImageView != null) {
            mainImageView.fitWidthProperty().bind(main_pane.widthProperty());
            mainImageView.fitHeightProperty().bind(main_pane.heightProperty());
        }

        playMusic();
        
        // Initialize status label
        if (statusLabel != null) {
            statusLabel.setText("Game started! Use arrow keys to move your character. Click on maze to focus.");
        }

        mazeDisplayer.setShowPlayerPath(true);
        mazeDisplayer.setSolution(null);
        if (togglePathButton != null) togglePathButton.setText("Hide Path");
        if (toggleSolutionButton != null) toggleSolutionButton.setText("Show Solution");
    }

    /**
     * Displays the maze solution and updates toggle button if present.
     */
    private void mazeSolved() {
        mazeDisplayer.setSolution(myViewModel.getSolution());
        if (toggleSolutionButton != null) {
            toggleSolutionButton.setText("Hide Solution");
        }
    }

    /**
     * Updates the player's position and UI after movement.
     */
    private void playerMoved() {
        setPlayerPosition(myViewModel.getPlayerRow(), myViewModel.getPlayerCol());
        playMoveSound();
        statusLabel.setText("Player moved to position: (" + myViewModel.getPlayerRow() + ", " + myViewModel.getPlayerCol() + ")");
    }

    /**
     * Handles the state after a new maze is generated.
     * Resets relevant flags, updates UI, and sets initial time.
     */
    private void mazeGenerated() {
        System.out.println("mazeGenerated: called");
        mazeDisplayer.drawMaze(myViewModel.getMaze());
        System.out.println("mazeGenerated: after drawMaze");
        mazeDisplayer.setSolution(null);
        System.out.println("mazeGenerated: after setSolution(null)");
        mazeDisplayer.setShowPlayerPath(true);
        System.out.println("mazeGenerated: after setShowPlayerPath(true)");
        mazeDisplayer.clearPlayerPath();
        System.out.println("mazeGenerated: after clearPlayerPath");
        if (togglePathButton != null) togglePathButton.setText("Hide Path");
        if (toggleSolutionButton != null) toggleSolutionButton.setText("Show Solution");
        startTime = System.currentTimeMillis();
        statusLabel.setText("New maze generated! Use arrow keys or drag to move.");
        System.out.println("mazeGenerated: finished");
    }

    /**
     * Saves the current maze state using a wrapper handler.
     *
     * @param actionEvent the triggering action event
     */
    public void handleSaveMaze(ActionEvent actionEvent) {
        SaveMaze(actionEvent);
    }

    /**
     * Shows the application's properties loaded from config.properties.
     *
     * @param actionEvent the triggering action event
     */
    public void handleProperties(ActionEvent actionEvent) {
        mouseAudio();
        try {
            java.util.Properties properties = new java.util.Properties();
            java.io.InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties");
            StringBuilder content = new StringBuilder();
            if (input != null) {
                properties.load(input);
                input.close();
                content.append("📋 APPLICATION PROPERTIES\n\n");
                for (String key : properties.stringPropertyNames()) {
                    content.append("• ").append(key).append(": ").append(properties.getProperty(key)).append("\n");
                }
            } else {
                content.append("Could not load config.properties file");
            }
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Properties");
            alert.setHeaderText("Current Application Properties");
            alert.setContentText(content.toString());
            alert.setResizable(true);
            alert.getDialogPane().setPrefWidth(500);
            alert.showAndWait();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Configuration Error");
            alert.setContentText("Error reading configuration file: " + e.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * Displays a help window with game rules and controls.
     *
     * @param actionEvent the triggering action event
     */
    public void handleHelp(ActionEvent actionEvent) {
        mouseAudio();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Rules");
        alert.setHeaderText("Maze Game - How to Play");
        alert.setContentText(
            "🎮 GAME RULES:\n\n" +
            "• Use ARROW KEYS or WASD to move your character\n" +
            "• Navigate through the maze from START (green) to GOAL (red)\n" +
            "• Avoid walls and find the shortest path\n" +
            "• Use mouse wheel to zoom in/out\n" +
            "• Click and drag to pan around the maze\n\n" +
            "🎯 OBJECTIVE:\n" +
            "Reach the goal in the shortest time possible!\n\n" +
            "⚙️ CONTROLS:\n" +
            "• Arrow Keys / WASD: Move character\n" +
            "• Mouse Wheel: Zoom in/out\n" +
            "• Mouse Drag: Pan around maze\n" +
            "• ESC: Return to menu\n\n" +
            "🏆 TIPS:\n" +
            "• Use the 'Show Solution' button to see the solution\n" +
            "• Try different maze sizes for more challenge\n" +
            "• Choose your favorite character color!"
        );
        alert.setResizable(true);
        alert.getDialogPane().setPrefWidth(500);
        alert.showAndWait();
    }

    /**
     * Displays an About dialog showing developers and maze solving algorithms.
     *
     * @param actionEvent the triggering action event
     */
    public void handleAbout(ActionEvent actionEvent) {
        mouseAudio();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("Maze Game - About");
        alert.setContentText(
            "Developers:\nAviel Yitzhak, Hadar Shir.\n\n" +
            "Algorithm used to solve the maze:\n" +
            "- Breadth First Search (BFS): An algorithm for traversing or searching tree or graph data structures. It starts at the tree root (or some arbitrary node of a graph), and explores all of the neighbor nodes at the present depth prior to moving on to the nodes at the next depth level.\n\n" +
            "- Depth First Search (DFS): An algorithm for traversing or searching tree or graph data structures. The algorithm starts at the root node (selecting some arbitrary node as the root node in the case of a graph) and explores as far as possible along each branch before backtracking.\n\n" +
            "- Best First Search (Best): Best-first search is a search algorithm which explores a graph by expanding the most promising node chosen according to a specified rule."
        );
        alert.setResizable(true);
        alert.getDialogPane().setPrefWidth(500);
        alert.showAndWait();
    }

    /**
     * Restarts the maze from the start position.
     * Resets player location, solution, and starts timer/music.
     */
    private void restartMaze() {
        int startRow = myViewModel.getStartPosition().getRowIndex();
        int startCol = myViewModel.getStartPosition().getColumnIndex();

        myViewModel.setPlayerRow(startRow);
        myViewModel.setPlayerCol(startCol);
        mazeDisplayer.setSolution(null);
        mazeDisplayer.clearPlayerPath();
        startTime = System.currentTimeMillis();



        setPlayerPosition(startRow, startCol);

        mazeDisplayer.drawMaze(myViewModel.getMaze());
        playMusic();
    }

    /**
     * Handles user request to go back to the main menu.
     *
     * @param event the triggering event
     * @throws IOException if FXML loading fails
     */
    @FXML
    public void handleBack(ActionEvent event) throws IOException {
        mouseAudio();
        Parent root = FXMLLoader.load(getClass().getResource("/View/MainScene.fxml"));
        Stage stage = (Stage) main_pane.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    /**
     * Toggles the game sound on/off and updates button label.
     *
     * @param event the triggering action event
     */
    @FXML
    public void handleToggleSound(ActionEvent event) {
        isMuted = !isMuted;
        if (isMuted) {
            if (MainSceneController.backgroundMusicPlayer != null)
                MainSceneController.backgroundMusicPlayer.setVolume(0.0);
            if (winMusicPlayer != null)
                winMusicPlayer.setVolume(0.0);
            toggleSoundButton.setText("Unmute");
        } else {
            if (MainSceneController.backgroundMusicPlayer != null)
                MainSceneController.backgroundMusicPlayer.setVolume(0.2);
            if (winMusicPlayer != null)
                winMusicPlayer.setVolume(0.3);
            toggleSoundButton.setText("Mute");
        }
    }

}

