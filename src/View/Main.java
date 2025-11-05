package View;

import Server.*;
import Server.ServerStrategyGenerateMaze;
import Server.ServerStrategySolveSearchProblem;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
public class Main extends Application {

    private static Server mazeGeneratingServer;
    private static Server solveSearchProblemServer;

    /**
     * Starts the JavaFX application by loading the main FXML scene.
     * Sets up the primary stage, including window title and close behavior.
     *
     * @param primaryStage The main application window
     * @throws Exception If FXML loading fails
     */
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/View/MainScene.fxml"));
        Scene scene = new Scene(root);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Maze Game");
        
        // Handle window close event for clean shutdown
        primaryStage.setOnCloseRequest(event -> {
            shutdownApplication();
        });
        
        primaryStage.show();
    }


    /**
     * Gracefully shuts down the application by stopping running servers
     * and exiting the platform.
     */
    private void shutdownApplication() {
        // Stop servers
        if (mazeGeneratingServer != null) {
            mazeGeneratingServer.stop();
        }
        if (solveSearchProblemServer != null) {
            solveSearchProblemServer.stop();
        }
        
        // Exit application
        Platform.exit();
        System.exit(0);
    }

    /**
     * Main method. Entry point of the application.
     * Initializes configuration, starts the servers, and launches the JavaFX UI.
     *
     * @param args Command-line arguments
     */
    public static void main(String[] args) {
        Configurations config = Configurations.getInstance();
        config.setConfigPath("resources/config.properties");
        
        // Start servers
        mazeGeneratingServer = new Server(5400, 1000, new ServerStrategyGenerateMaze());
        solveSearchProblemServer = new Server(5401, 1000, new ServerStrategySolveSearchProblem());
        solveSearchProblemServer.start();
        mazeGeneratingServer.start();
        
        // Launch JavaFX application
        launch(args);
        
        // Clean shutdown (this will be called when application exits)
        if (mazeGeneratingServer != null) {
            mazeGeneratingServer.stop();
        }
        if (solveSearchProblemServer != null) {
            solveSearchProblemServer.stop();
        }
    }
}
