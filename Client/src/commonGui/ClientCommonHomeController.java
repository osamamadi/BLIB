package commonGui;

import javafx.scene.paint.Color;
import javafx.application.Platform;
import javafx.event.ActionEvent;

import java.net.URL;

import Shared_classes.ClientServerContact;
import Shared_classes.Command;
import Shared_classes.User;
import Shared_classes.userType;
import client.ClientUI;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import librarianGui.ClientLibHomeController;
import subscriberGui.ClientSubHomeController;
import javafx.scene.Node;

/**
 * Controller for handling common functionalities on the home page of the client application.
 * This includes login, navigation to the book search page, and clearing input fields.
 * It also handles login validation and redirects to the appropriate page based on user type.
 * 
 * @author Your Name
 * @version 1.0
 */
public class ClientCommonHomeController {
	/** Label to display server messages to the user. */
    @FXML
    private Label lblServerMessage;

    /** Label displaying the "Username" text. */
    @FXML
    private Label lbl_username;

    /** Button to trigger the login process. */
    @FXML
    private Button btnLogin;

    /** Label displaying the "Password" text. */
    @FXML
    private Label lbl_password;

    /** Button to trigger the book search process. */
    @FXML
    private Button btnSearchBook;

    /** TextField for the user to input their username. */
    @FXML
    private TextField txtusername;

    /** TextField for the user to input their password. */
    @FXML
    private TextField txtpassword;

    /** Variable to store messages received from the server. */
    private String serverMessage;

    /**
     * Clears the input fields for username and password, and resets the server message.
     */
    @FXML
    void clearAllFieldsBtn() {
        // Clear all fields
        txtusername.clear();
        txtpassword.clear();
        inputResponse("");
    }

    /**
     * Navigates the user to the book search page.
     * 
     * @param event The action event that triggered the method call.
     */
    @FXML
    void gotoSearchBookPage(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/commonGui/ClientDisplayBooks.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Subscriber Home Page");
            ClientDisplayBooksController controller = loader.getController();
            controller.setUsername(null);
            controller.setPage("CommonHome");
            stage.centerOnScreen();

            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Navigates the user to a specified page based on the provided FXML path and page title.
     * 
     * @param event The action event that triggered the method call.
     * @param fxmlPath The path to the FXML file to load.
     * @param pageTitle The title to set for the new page.
     */
    private void navigateToPage(ActionEvent event, String fxmlPath, String pageTitle) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // Create a new scene
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle(pageTitle);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the login button click event. Retrieves the username and password from the input fields,
     * validates them, and sends a login request to the server.
     * 
     * @param event The action event that triggered the login attempt.
     */
    @FXML
    void LoginBtn(ActionEvent event) {
        try {
            // Retrieve data from TextFields
            String username = txtusername.getText();
            String password = txtpassword.getText();

            System.out.println("Username: " + username);
            System.out.println("Password: " + password);

            // Validate input (ensure fields are not empty)
            if (!username.isEmpty() && !password.isEmpty()) {
                User user = new User(username, password);
                // Send the login command
                ClientServerContact msg = new ClientServerContact(user, Command.Login);
                ClientUI.LibClient.setController(this);
                ClientUI.LibClient.requestFromServer(msg);

            } else {
                inputResponse("Username and password are required");
            }

        } catch (Exception e) {
            e.printStackTrace();
            inputResponse("Error occurred during login.");
        }
    }

    /**
     * Exits the application when the exit button is clicked.
     * 
     * @param event The action event that triggered the exit request.
     */
    @FXML
    void exitBtn(ActionEvent event) {
        System.exit(0);
    }

    /**
     * Handles the server response after login attempt and navigates to the appropriate page
     * based on the user's type. Displays error messages or login success messages.
     * 
     * @param response The response from the server, which could be a string or a User object.
     */
    public void inputResponse(Object response) {
        Platform.runLater(() -> {
            try {
                System.out.println("RESPONE FROM LOGIN: " + response);
                if (response instanceof String) {
                    // Handle error case (response is a String, e.g., "Invalid username or password")
                    serverMessage = (String) response;
                    lblServerMessage.setText(serverMessage);
                    lblServerMessage.setTextFill(Color.RED);
                } else if (response instanceof User) {
                    User u = (User) response;
                    // Open a new window based on the user type
                    Stage primaryStage = new Stage();
                    FXMLLoader loader = new FXMLLoader();
                    Pane root = null;

                    System.out.println("Response: " + response.toString());
                    if (u.getType() == userType.Librarian) {
                        System.out.println("The logged-in user is a librarian.");
                        URL resourceUrl = getClass().getResource("/librarianGui/ClientLibHomePage.fxml");
                        System.out.println("Loading FXML from: " + resourceUrl);
                        if (resourceUrl == null) {
                            System.err.println("The resource path is incorrect or the file is missing.");
                            lblServerMessage.setText("Failed to locate the FXML file.");
                            lblServerMessage.setTextFill(Color.RED);
                            return;
                        }
                        ClientLibHomeController.setLibrarian(u);
                        loader.setLocation(resourceUrl);
                        root = loader.load();
                        primaryStage.setTitle("Librarian Home Page");
                    } else if (u.getType() == userType.Subscriber) {
                        System.out.println("The logged-in user is a subscriber.");
                        URL resourceUrl = getClass().getResource("/subscriberGui/ClientSubHomePage.fxml");
                        System.out.println("Loading FXML from: " + resourceUrl);
                        if (resourceUrl == null) {
                            System.err.println("The resource path is incorrect or the file is missing.");
                            lblServerMessage.setText("Failed to locate the FXML file.");
                            lblServerMessage.setTextFill(Color.RED);
                            return;
                        }
                        ClientSubHomeController.setSubscriber(u);
                        loader.setLocation(resourceUrl);
                        root = loader.load();
                        primaryStage.setTitle("Subscriber Home Page");
                    }

                    if (root != null) {
                        Scene scene = new Scene(root);
                        primaryStage.setScene(scene);
                        primaryStage.show();

                        // Hide the current window only after successfully loading the new window
                        Stage currentStage = (Stage) lblServerMessage.getScene().getWindow();
                        currentStage.hide();
                    } else {
                        System.err.println("Failed to load the new window. Root is null.");
                    }
                } else {
                    // Unexpected response type
                    lblServerMessage.setText("Unexpected response from server.");
                    lblServerMessage.setTextFill(Color.RED);
                    System.err.println("Unexpected response type: " + response.getClass().getName());
                }
            } catch (Exception e) {
                e.printStackTrace();
                lblServerMessage.setText("An error occurred while processing the response.");
                lblServerMessage.setTextFill(Color.RED);
            }
        });
    }
}
