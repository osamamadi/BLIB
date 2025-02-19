package librarianGui;

import javafx.scene.paint.Color;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import Shared_classes.ClientServerContact;
import Shared_classes.Command;
import Shared_classes.User;
import Shared_classes.userType;
import Shared_classes.Subscriber;
import client.ClientUI;
import commonGui.ClientDisplayBooksController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Node;
import Shared_classes.UserWithSubscriber;

/**
 * Controller for handling the actions related to adding a subscriber in the librarian GUI.
 * @author Majd Awad
 * @version 1.1dz
 */

public class ClientLibAddSubController {

    /** Displays the server response message after an action is completed. */
    @FXML
    private Label lblServerMessage;

    /** Label for subscriber ID input. */
    @FXML
    private Label lblsubscriber_id;

    /** Label for subscriber username input. */
    @FXML
    private Label lblsubscriber_username;

    /** Label for subscriber password input. */
    @FXML
    private Label lblsubscriber_password;

    /** Label for subscriber first name input. */
    @FXML
    private Label lblsubscriber_firstname;

    /** Label for subscriber last name input. */
    @FXML
    private Label lblsubscriber_lastname;

    /** Label for subscriber phone number input. */
    @FXML
    private Label lblsubscriber_phone_number;

    /** Label for subscriber email input. */
    @FXML
    private Label lblsubscriber_email;

    /** Button to navigate back to the previous screen. */
    @FXML
    private Button btnBack;

    /** Button to add a new subscriber. */
    @FXML
    private Button btnAdd;

    /** Button to clear all the input fields. */
    @FXML
    private Button btnClear;

    /** TextField for inputting subscriber ID. */
    @FXML
    private TextField txtsubscriber_id;

    /** TextField for inputting subscriber username. */
    @FXML
    private TextField txtsubscriber_username;

    /** TextField for inputting subscriber password. */
    @FXML
    private TextField txtsubscriber_password;

    /** TextField for inputting subscriber first name. */
    @FXML
    private TextField txtsubscriber_first_name;

    /** TextField for inputting subscriber last name. */
    @FXML
    private TextField txtdetailed_last_name;

    /** TextField for inputting subscriber phone number. */
    @FXML
    private TextField txtsubscriber_phone_number;

    /** TextField for inputting subscriber email. */
    @FXML
    private TextField txtsubscriber_email;

    /** Default status for a new subscriber. */
    private static final String DEFAULT_STATUS = "active";

    /** Default history for a new subscriber. */
    private static final int DEFAULT_HISTORY = 0;

    /** Default late returns for a new subscriber. */
    private static final int DEFAULT_LATE_RETURNS = 0;

    /** Holds the server's response message. */
    private String serverMessage;
    
    
    
    /**
     * default contructor
     */
   public ClientLibAddSubController()
    {
    	
    }

    /**
     * Clears all the input fields when the clear button is clicked.
     */
    @FXML
     void  clearAllFieldsBtn() {
        // Clear all fields
        txtsubscriber_id.clear();
        txtsubscriber_username.clear();
        txtsubscriber_password.clear();
        txtsubscriber_first_name.clear();
        txtdetailed_last_name.clear();
        txtsubscriber_phone_number.clear();
        txtsubscriber_email.clear();
        inputResponse("");
    }

    /**
     * Handles the action when the "Back" button is clicked. 
     * Navigates the user to the previous screen (Client Home Page).
     * @param event the ActionEvent triggered by the user clicking the "Back" button.
     */
    @FXML
     void getBackBtn(ActionEvent event) {
        try {
            // Load the FXML for the previous screen
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/librarianGui/ClientLibHomePage.fxml")); // Update path if needed
            Parent previousScreen = loader.load();

            // Get the current stage from the event source
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Set the scene for the stage with the root of the previous screen
            Scene scene = new Scene(previousScreen);
            stage.setScene(scene);
            stage.centerOnScreen();

            // Optionally, set the title for the stage
            stage.setTitle("Client Home Page");

            stage.show();
        } catch (Exception e) {
            e.printStackTrace(); // Print the stack trace in case of an exception
        }
    }

    /**
     * Handles the action when the "Add Subscriber" button is clicked.
     * Gathers the data from the input fields and sends the request to the server to add the new subscriber.
     * @param event the ActionEvent triggered by the user clicking the "Add Subscriber" button.
     */
    @FXML
    void AddSubscriberBtn(ActionEvent event) {
        try {
            // Gather User Table Data
            String personalId = txtsubscriber_id.getText(); // Personal ID
            String username = txtsubscriber_username.getText(); // Username
            String password = txtsubscriber_password.getText(); // Password
            String firstName = txtsubscriber_first_name.getText(); // First Name
            String lastName = txtdetailed_last_name.getText(); // Last Name
            String phoneNumber = txtsubscriber_phone_number.getText(); // Phone Number
            String email = txtsubscriber_email.getText(); // Email
            
            // Input Validation
            if (personalId.isEmpty() || username.isEmpty() || password.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || phoneNumber.isEmpty() || email.isEmpty()) {
                inputResponse("All fields are required!");
                return;
            }
            // Create User and Subscriber Objects
            User user = new User(
                Integer.parseInt(personalId), // Personal ID
                username,                     // Username
                password,                     // Password
                firstName,                    // First Name
                lastName                     // Last Name
            );
            Subscriber subscriber = new Subscriber(
            		Integer.parseInt(personalId),
            		    DEFAULT_STATUS,
            		    email,
            		    DEFAULT_LATE_RETURNS,
            		    phoneNumber
            );
            UserWithSubscriber userAndSub=new UserWithSubscriber(user,subscriber);
            // Combine the user and subscriber into a single client-server contact
            ClientServerContact msg = new ClientServerContact(userAndSub, Command.AddSubscriber);
            // Send the request to the server
            ClientUI.LibClient.setController(this);
            ClientUI.LibClient.requestFromServer(msg);

        } catch (Exception e) {
            e.printStackTrace();
            inputResponse("An error occurred while adding the subscriber.");
        }
    }

    /**
     * Displays a response message from the server.
     * This method updates the server response message on the UI, and sets the text color to green if the message
     * indicates success, or red if it indicates an error.
     * @param response the response message to display on the UI.
     */
    public void inputResponse(String response) {
        this.serverMessage = response;
        Platform.runLater(() -> {
            lblServerMessage.setText(serverMessage);
            if ("Added Successfully".equals(serverMessage)) {
                lblServerMessage.setTextFill(Color.GREEN);
            } else {
                lblServerMessage.setTextFill(Color.RED);
            }
        });
    }
}
