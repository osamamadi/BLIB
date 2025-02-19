package commonGui;

import client.BraudeLibClient;
import client.ClientUI;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * The ClientConnectController class is responsible for managing the client-side connection 
 * between the client and the server. It handles user interactions on the connection page,
 * including connecting to the server and exiting the application.
 * 
 * @author Your Name
 * @version 1.0
 */
public class ClientConnectController implements Initializable {

	 /**
     * The server connection data (e.g., host address) input by the user.
     */
    private String server_connection_data;

    /** 
     * Button to trigger the action for connecting to the server. 
     */
    @FXML
    private Button btnConnect;

    /** 
     * Label to display the current connection status to the user. 
     */
    @FXML
    private Label conStatus;

    /** 
     * Button to trigger the action for exiting the application. 
     */
    @FXML
    private Button btnExit;

    /** 
     * TextField for the user to input their identification (e.g., user ID). 
     */
    @FXML
    private TextField txtId;
    /**
     * Initializes the ClientConnectController, setting up the primary stage for the client connection UI.
     * 
     * @param primaryStage The primary stage for this application.
     * @throws Exception If an error occurs while setting up the stage.
     */
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/commonGui/ClientConnect.fxml"));

        Scene scene = new Scene(root);
        primaryStage.setTitle("Client Connect");
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();

        primaryStage.show();
    }

    /**
     * Handles the exit button click event. Exits the application when the user clicks "Exit".
     * 
     * @param event The action event triggered by the exit button.
     * @throws Exception If an error occurs while exiting the application.
     */
    @FXML
    void getExitBtn(ActionEvent event) throws Exception {
        System.exit(0);
    }

    /**
     * Handles the connect button click event. Establishes a connection to the server 
     * and loads the next page (CommonHome) upon successful connection.
     * 
     * @param event The action event triggered by the connect button.
     */
    @FXML
    void getBtnConnect(ActionEvent event) {
        server_connection_data = txtId.getText();
        try {
            // Establish connection to the server
            ClientUI.LibClient = new BraudeLibClient(server_connection_data, 5555);
            // Load the FXML for the next page (CommonHome)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/commonGui/ClientCommonHome.fxml"));
            Parent root = loader.load();
            // Get the current stage
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            // Set the scene for the stage with the new FXML
            Scene scene = new Scene(root);
            currentStage.setScene(scene);
            currentStage.setTitle("Client Home Page");
            currentStage.show();
        } catch (Exception e) {
            // Update the connection status label if an error occurs
            conStatus.setText("Couldn't Connect to Server");
            e.printStackTrace(); // Print the stack trace for debugging
        }
    }

    /**
     * Initializes the text field with the default value ("localhost") when the page is loaded.
     * 
     * @param location The location used to resolve relative paths for the root object, or null if the location is not known.
     * @param resources The resources used to localize the root object, or null if not needed.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set the default value for the TextField
        txtId.setText("localhost");
    }
}
