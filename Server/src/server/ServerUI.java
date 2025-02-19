package server;

import javafx.application.Application;
import javafx.stage.Stage;
import java.io.IOException;
import serverGui.ServerDisplayController;

/**
 * This class represents the server's user interface, extending the JavaFX Application class.
 * It provides a graphical user interface (GUI) for managing the server's lifecycle, 
 * including starting and stopping the server.
 * @author Your Name
 * @version 1.0
 */
public class ServerUI extends Application {
    /** Default port for the server */
    final public static int DEFAULT_PORT = 5555;

    /** The BraudeLibServer instance */
    private static BraudeLibServer server;

    /**
     * Main method that launches the JavaFX application.
     * 
     * @param args Command-line arguments for the application.
     * @throws Exception If an error occurs during application launch.
     */
    public static void main( String args[] ) throws Exception {
        launch(args);
    }

    /**
     * Initializes the user interface and displays the server display frame.
     * 
     * @param primaryStage The primary stage for the JavaFX application.
     * @throws Exception If an error occurs while initializing the UI.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Create and start the ServerDisplayController frame
        ServerDisplayController aFrame = new ServerDisplayController(); 
        aFrame.start(primaryStage);
    }

    /**
     * Starts the server on the specified port if it is not already running.
     * 
     * @param port The port number on which the server should listen for client connections.
     * @param serverInstance The instance of the BraudeLibServer to be started.
     */
    public static void runServer(int port, BraudeLibServer serverInstance) {
        if (server != null && server.isListening()) {
            System.out.println("Server is already running");
            return;
        }

        server = serverInstance;

        try {
            server.listen(); 
            // Start listening for connections
            // System.out.println("Server listening for connections on port " + port);
        } catch (Exception ex) {
            System.out.println("ERROR - Could not listen for clients!");
        }
    }

    /**
     * Stops the server if it is currently running.
     */
    public static void stopServer() {
        if (server != null && server.isListening()) {
            try {
                server.close();
                // System.out.println("Server has stopped listening for connections.");
            } catch (IOException e) {
                System.out.println("Error closing the server: " + e.getMessage());
            }
        }
    }
}
