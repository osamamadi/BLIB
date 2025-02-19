package subscriberGui;

import Shared_classes.ClientServerContact;
import Shared_classes.Command;
import Shared_classes.User;
import client.ClientUI;
import commonGui.ClientDisplayBooksController;
import commonGui.ClientShowMessagesController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

/**
 * Controller for the Subscriber Home page in the subscriber GUI.
 * This page allows the subscriber to navigate between different pages such as the search books page, personal card page, 
 * and messages page. It also displays the subscriber's welcome message and the unread messages count.
 * 
 * @author Majd Awad
 * @version 1.0
 */
public class ClientSubHomeController {

    /**
     * Button to navigate to the Search Book page.
     */
    @FXML
    private Button Search_Book_Btn;

    /**
     * Button to navigate to the Personal Card page.
     */
    @FXML
    private Button Personal_Card_Btn;

    /**
     * Button to log out of the system and return to the common home page.
     */
    @FXML
    private Button Log_Out_Btn;

    /**
     * Button to navigate to the Login page.
     */
    @FXML
    private Button Log_In_Btn;

    /**
     * Button to view unread messages.
     */
    @FXML
    private Button btnMessages;

    /**
     * Label to display server messages or errors.
     */
    @FXML
    private Label lblServerMessage;

    /**
     * Label to display a personalized welcome message to the subscriber.
     */
    @FXML
    private Label lblWelcomeMessage;

    /**
     * The current subscriber user.
     */
    private static User subscriber;

    /**
     * Sets the current subscriber.
     * 
     * @param sub The subscriber user object.
     */
    public static void setSubscriber(User sub) {
        subscriber = sub;
    }

    /**
     * Initializes the home page with the subscriber's welcome message and sends an initial request to get unread message count.
     */
    @FXML
    public void initialize() {
        lblWelcomeMessage.setText("Welcome, " + subscriber.getFirstName());
        ClientUI.LibClient.setController(this);

        // Initial request to get the unread message count
        ClientServerContact csc = new ClientServerContact(subscriber.getId(), Command.GetUnreadMessagesCount);
        ClientUI.LibClient.requestFromServer(csc);
    }

    /**
     * Updates the unread messages count on the Messages button.
     * 
     * @param unreadCount The number of unread messages.
     */
    public void updateMessageCount(Integer unreadCount) {
        Platform.runLater(() -> {
            btnMessages.setText("Messages(" + unreadCount + ")");
        });
    }

    /**
     * Navigates to the Messages page when the 'Messages' button is clicked.
     * 
     * @param event The ActionEvent triggered by clicking the 'Messages' button.
     */
    @FXML
    void showMessages(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/commonGui/ClientShowMessages.fxml"));
            Parent root = loader.load();

            // Get the controller for the messages page
            ClientShowMessagesController controller = loader.getController();

            // Pass the subscriber ID to fetch messages
            controller.setLibrarianId(subscriber.getId());
            controller.setPage("SubHome");

            // Set the controller for future server-client communication
            ClientUI.LibClient.setController(controller);

            // Load the new scene
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Messages Page");
            stage.centerOnScreen();

            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showErrorMessage("Failed to load Messages Page.");
        }
    }

    /**
     * Navigates to a specified page based on the provided FXML path and page title.
     * 
     * @param event The ActionEvent that triggered the navigation.
     * @param fxmlPath The path to the FXML file to load.
     * @param pageTitle The title to set for the page.
     */
    private void navigateToPage(ActionEvent event, String fxmlPath, String pageTitle) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            if(pageTitle.equals("Search Book Page")) {
                ClientDisplayBooksController controller = loader.getController();
                controller.setPage("HomeSubPage");
                controller.setUsername(subscriber.getUsername());
            }

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(pageTitle);
            stage.centerOnScreen();

            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showErrorMessage("Failed to load page: " + pageTitle);
        }
    }

    /**
     * Logs out the current subscriber and navigates to the common home page.
     * 
     * @param event The ActionEvent triggered by clicking the 'Log Out' button.
     */
    @FXML
    void Logout(ActionEvent event) {
        navigateToPage(event, "/commonGui/ClientCommonHome.fxml", "Home Page");
    }

    /**
     * Navigates to the Search Book page.
     * 
     * @param event The ActionEvent triggered by clicking the 'Search Book' button.
     */
    @FXML
    void gotoSearchBookPage(ActionEvent event) {
        navigateToPage(event, "/commonGui/ClientDisplayBooks.fxml", "Search Book Page");
    }

    /**
     * Navigates to the Personal Card page.
     * 
     * @param event The ActionEvent triggered by clicking the 'Personal Card' button.
     */
    @FXML
    void gotoPersonalCard(ActionEvent event) {
        try {
            ClientSubPersonalCardController.setUser(subscriber);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/subscriberGui/ClientSubPersonalCard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Personal Card Page");
            stage.centerOnScreen();

            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error loading Personal Card: " + e.getMessage());
        }
    }

    /**
     * Navigates to the Login page.
     * 
     * @param event The ActionEvent triggered by clicking the 'Log In' button.
     */
    @FXML
    void gotoLoginPage(ActionEvent event) {
        navigateToPage(event, "/commonGui/ClientLogin.fxml", "Login Page");
    }

    /**
     * Displays an error message on the home page.
     * 
     * @param message The error message to display.
     */
    private void showErrorMessage(String message) {
        Platform.runLater(() -> {
            lblServerMessage.setText(message);
            lblServerMessage.setTextFill(Color.RED);
        });
    }

    /**
     * Processes the server's response and updates the UI with the response message.
     * 
     * @param response The response message from the server.
     */
    public void inputResponse(String response) {
        Platform.runLater(() -> {
            lblServerMessage.setText(response);
        });
    }
}
