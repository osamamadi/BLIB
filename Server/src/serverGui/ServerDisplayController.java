package serverGui;
import java.net.InetAddress;
import java.net.URL;
import java.util.ResourceBundle;

import dbController.ClientConnInfo;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ocsf.server.ConnectionToClient;
import javafx.scene.paint.Color;
import server.BraudeLibServer;
import server.ServerUI;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
/**
 * Controller for managing the server's user interface and handling user interactions.
 * It allows the user to connect, disconnect, and manage client connections to the server.
 * 
 * @author YourName
 * @version 1.0
 */
public class ServerDisplayController implements Initializable{
	/** The database connection instance used for interacting with the database. */

	private static ServerDisplayController instance;
	 /**
     * Constructor for the ServerDisplayController, initializing the instance.
     */
	public ServerDisplayController() {
		instance=this;
	}
	 /**
     * Gets the singleton instance of ServerDisplayController.
     * 
     * @return the instance of ServerDisplayController
     */
	public static ServerDisplayController getInstance() {
		return instance;
	}
	 /**
     * The button to connect the server.
     */
    @FXML
    private Button connectBtn = null;

    /**
     * The button to disconnect the server.
     */
    @FXML
    private Button disconnectBtn = null;

    /**
     * The button to close the application or server.
     */
    @FXML
    private Button closeBtn = null;

    /**
     * The text field for the server's port configuration.
     */
    @FXML
    private TextField port;

    /**
     * The text field for the database name configuration.
     */
    @FXML
    private TextField DBname;

    /**
     * The text field for the database username configuration.
     */
    @FXML
    private TextField DBuser;

    /**
     * The text field for the database password configuration.
     */
    @FXML
    private TextField DBpassword;

    /**
     * The label displaying the server configuration heading.
     */
    @FXML
    private Label Server_Configuration;

    /**
     * The label displaying the "IP Address" field title.
     */
    @FXML
    private Label ip_label;

    /**
     * The label displaying the "Port" field title.
     */
    @FXML
    private Label port_label;

    /**
     * The label displaying the "Database" field title.
     */
    @FXML
    private Label DB_label;

    /**
     * The label displaying the "Database User" field title.
     */
    @FXML
    private Label DBuser_label;

    /**
     * The label displaying the "Database Password" field title.
     */
    @FXML
    private Label DBpassword_label;

    /**
     * The label displaying the server status (e.g., connected or disconnected).
     */
    @FXML
    private Label serverStatusLabel;

    /**
     * The label displaying the server's IP address.
     */
    @FXML
    private Label server_ipLabel;

    /**
     * The label displaying the client details section title.
     */
    @FXML
    private Label ClientDetailsLabel;

    /**
     * The label displaying the "Host Name" field title for client details.
     */
    @FXML
    private Label HostNameLabel;

    /**
     * The label displaying the "IP Address" field title for client details.
     */
    @FXML
    private Label IPAddLabel;

    /**
     * The table view for displaying connected client details.
     */
    @FXML
    private TableView<ClientConnInfo> clientTable;

    /**
     * The column for displaying the host name of connected clients.
     */
    @FXML
    private TableColumn<ClientConnInfo, String> hostNameColumn;

    /**
     * The column for displaying the IP address of connected clients.
     */
    @FXML
    private TableColumn<ClientConnInfo, String> ipAddressColumn;

    /**
     * The column for displaying the status of connected clients.
     */
    @FXML
    private TableColumn<ClientConnInfo, String> statusColumn;

    /**
     * An observable list that holds the client connection information
     * for display in the table view.
     */
    private static ObservableList<ClientConnInfo> clientData;
    /**
     * Initializes the server display by setting default values for the database connection
     * and populating the client table.
     *
     * @param url the location used to resolve relative paths for the root object
     * @param resourceBundle the resources used to localize the root object
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize method for Initializable interface, if you are implementing it
        port.setText("5555");
        DBname.setText("db");
        DBuser.setText("root");
        DBpassword.setText("Aa123456");
        
        // Get the local IP address
        try {
            String ipAddress = InetAddress.getLocalHost().getHostAddress();
            server_ipLabel.setText(ipAddress);
            hostNameColumn.setCellValueFactory(new PropertyValueFactory<>("hostName"));
            ipAddressColumn.setCellValueFactory(new PropertyValueFactory<>("ipAddress"));
            statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

            // Initialize the data list and set it to the table
            clientData = FXCollections.observableArrayList();
            clientTable.setItems(clientData);
        } catch (Exception e) {
            server_ipLabel.setText("Unable to get IP address");
        }
    }
    /**
     * Starts the server menu by loading the FXML and displaying it in the provided stage.
     * 
     * @param primaryStage the primary stage for the application
     * @throws Exception if an error occurs while loading the FXML
     */
    public void start(Stage primaryStage) throws Exception {	
		Parent root = FXMLLoader.load(getClass().getResource("/serverGui/ServerDisplay.fxml"));
				
		Scene scene = new Scene(root);
		primaryStage.setTitle("Server menu");
		primaryStage.setScene(scene);
		
		primaryStage.show();
		
	}
    /**
     * Handles the connect button action by starting the server with the provided configuration.
     * 
     * @param event the action event triggered by the user
     */
    @FXML
    public void connect(ActionEvent event) {
        String dbName = DBname.getText();
        String dbUser = DBuser.getText();
        String dbPassword = DBpassword.getText();
    	
        int portNumber;
        try {
            portNumber = Integer.parseInt(port.getText());
        } catch (NumberFormatException e) {
            serverStatusLabel.setText("Invalid port number");
            serverStatusLabel.setTextFill(Color.RED);
            serverStatusLabel.setVisible(true);
            return;
        }
        
        // Create a new BraudeLibServer instance with database credentials
        BraudeLibServer serverInstance = new BraudeLibServer(portNumber, dbName, dbUser, dbPassword);
        
     // Sets the callback function for the server instance to the updateClientDetails method of this controller.
     // This ensures that when a client connects to the server, the updateClientDetails method is called with the client's
     // hostname and IP address.
        serverInstance.setClientDetailsCallback(this::updateClientDetails);
        //serverInstance.setClientDisconnectionCallback(this::updateClientDisconnectionDetails);
        
        // Pass the server instance to ServerUI.runServer
        ServerUI.runServer(portNumber, serverInstance);
        serverStatusLabel.setText("Server is listening for connections on port " + portNumber);
        serverStatusLabel.setTextFill(Color.GREEN);
        serverStatusLabel.setVisible(true);

        disableButton(connectBtn, true);
        disableButton(disconnectBtn, false);
    }
    /**
     * Handles the disconnect button action by stopping the server and updating the status label.
     * 
     * @param event the action event triggered by the user
     */
    @FXML
    public void disconnect(ActionEvent event) {
        ServerUI.stopServer();
        serverStatusLabel.setText("Server disconnected");
        serverStatusLabel.setTextFill(Color.RED);
        serverStatusLabel.setVisible(true);
        
        disableButton(connectBtn, false);
        disableButton(disconnectBtn, true);
    }
    /**
     * Closes the application when the close button is pressed.
     * 
     * @param event the action event triggered by the user
     * @throws Exception if an error occurs while closing the application
     */
    @FXML
    public void getcloseBtn(ActionEvent event) throws Exception {
    	System.exit(0);
    }
    /**
     * Disables the provided button and adjusts its opacity based on the disable flag.
     * 
     * @param button the button to disable or enable
     * @param disable flag indicating whether the button should be disabled
     */
    private void disableButton(Button button, boolean disable) {
        button.setDisable(disable);
        button.setOpacity(disable ? 0.5 : 1.0);
    }
    
    /**
     * Updates the client details on the GUI by adding the client's host name and IP address.
     * 
     * @param clientHostName the hostname of the connected client
     * @param clientIpAddress the IP address of the connected client
     */
    public void updateClientDetails(String clientHostName, String clientIpAddress) {
        Platform.runLater(() -> { // Ensure the GUI update runs on the JavaFX Application Thread
        	ClientConnInfo newClient = new ClientConnInfo(clientHostName, clientIpAddress, "connect");
            clientData.add(newClient);
            //Enter to table row
            
        });
    }
    /**
     * Updates the client disconnection details when a client disconnects from the server.
     * 
     * @param conn the ConnectionToClient object representing the disconnected client
     */
    public void updateClientDisconnectionDetails(ConnectionToClient conn) {
    	String[] details = conn.getInetAddress().toString().split("/");
        Platform.runLater(() -> {
	        for(ClientConnInfo c : clientData) {
	        	if(c.getHostName().equals(details[0])) {
	        		c.setStatus("Disconnected");
	        		clientTable.refresh();
	        	}
	        }
        });
    }
}
