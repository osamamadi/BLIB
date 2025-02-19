package client;

import commonGui.ClientConnectController;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * The ClientUI class serves as the entry point for the client-side application. 
 * It initializes and launches the graphical user interface (GUI) for the client.
 * 
 * @author Your Name
 * @version 1.0
 */
public class ClientUI extends Application {

    /**
     * Controller responsible for managing the connection to the server.
     */
    ClientConnectController clientConnectController;

    /**
     * Static instance of the BraudeLibClient used for client-server communication.
     */
    public static BraudeLibClient LibClient;

    /**
     * Main method that launches the JavaFX application.
     * 
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Starts the JavaFX application and initializes the connection controller.
     * 
     * @param primaryStage The primary stage for this application.
     * @throws Exception If an error occurs during application startup.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        clientConnectController = new ClientConnectController();
        clientConnectController.start(primaryStage);
    }
}
