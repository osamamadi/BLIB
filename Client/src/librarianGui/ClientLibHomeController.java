package librarianGui;

import java.io.IOException;

import Shared_classes.ClientServerContact;
import Shared_classes.Command;
import Shared_classes.User;
import client.ClientUI;
import commonGui.ClientConnectController;
import commonGui.ClientDisplayBooksController;
import commonGui.ClientDisplayBorrowsController;
import commonGui.ClientDisplayOrdersController;
import commonGui.ClientShowMessagesController;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

/**
 * Controller for the Librarian Home page UI.
 * Provides functionalities for managing subscribers, borrowing/returning books, and navigating to other pages in the library system.
 * 
 * @author Majd Awad
 * @version 1.1dz
 */
public class ClientLibHomeController {

    /**
     * Label that displays server message responses.
     */
    @FXML
    private Label lblServerMessage;

    /**
     * Label that shows a personalized welcome message.
     */
    @FXML
    private Label lblWelcome;

    /**
     * Button to show the list of subscribers.
     * When clicked, it navigates to the subscriber display page.
     */
    @FXML
    private Button btnShowSubscribers;

    /**
     * Button to log out the librarian.
     * When clicked, it navigates to the login page.
     */
    @FXML
    private Button btnLogout;

    /**
     * Button to register a new subscriber.
     * When clicked, it navigates to the page for registering a new subscriber.
     */
    @FXML
    private Button btnRegisterSubscriber;

    /**
     * Button to view reports.
     * When clicked, it navigates to the reports page.
     */
    @FXML
    private Button btnViewReports;

    /**
     * Button to show the server connection page.
     * When clicked, it navigates to the connection page.
     */
    @FXML
    private Button btnShowConnection;

    /**
     * Text field for entering the server IP address.
     * Used to manually input the server connection IP.
     */
    @FXML
    private TextField iptxt;

    /**
     * Server response message to be displayed.
     */
    @FXML
    private String serverMessage;

    /**
     * Button to view messages.
     * When clicked, it navigates to the messages page.
     */
    @FXML
    private Button btnMessages;

    /**
     * Button to show a subscriber's details.
     * When clicked, it navigates to the subscriber details page.
     */
    @FXML
    private Button btnShowSubscriber;

    /**
     * Button to search for a book.
     * When clicked, it navigates to the book search page.
     */
    @FXML
    private Button btnSearchBook;

    /**
     * Button to view reports.
     * When clicked, it navigates to the report viewing page.
     */
    @FXML
    private Button btnViewReport;

    /**
     * Button to return a borrowed book.
     * When clicked, it navigates to the book return page.
     */
    @FXML
    private Button btnReturnBook;

    /**
     * Button to borrow a book.
     * When clicked, it navigates to the book borrowing page.
     */
    @FXML
    private Button btnBorrowBook;

    /**
     * The librarian user object, containing details of the currently logged-in librarian.
     */
    private static User librarian;

    /**
     * Sets the librarian user object.
     * 
     * @param lib The librarian user object to be set.
     */
    public static void setLibrarian(User lib) {
        librarian = lib;
    }

    /**
     * Initializes the UI components and sends an initial request to the server to fetch unread messages count.
     * Displays the librarian's name on the welcome label.
     */
    @FXML
    public void initialize() {
        lblWelcome.setText("Welcome, " + librarian.getFirstName());
        ClientUI.LibClient.setController(this);

        // Initial request to get the unread message count
        ClientServerContact csc = new ClientServerContact(librarian.getId(), Command.GetUnreadMessagesCount);
        ClientUI.LibClient.requestFromServer(csc);        
    }

    /**
     * Updates the message count button with the number of unread messages.
     * 
     * @param unreadCount The count of unread messages to be displayed on the button.
     */
    public void updateMessageCount(Integer unreadCount) {
        Platform.runLater(() -> {
            System.out.println(""+unreadCount);
            btnMessages.setText("Messages(" + unreadCount + ")");
        });
    }

    /**
     * Navigates to the subscriber registration page.
     * 
     * @param event The ActionEvent triggered by the 'Register Subscriber' button click.
     * @throws Exception If there is an error during the page load.
     */
    public void registerSubscriber(ActionEvent event) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        ((Node)event.getSource()).getScene().getWindow().hide(); //hiding primary window
        Stage primaryStage = new Stage();
        Pane root = loader.load(getClass().getResource("/librarianGui/ClientLibAddSub.fxml").openStream());

        ClientLibAddSubController addController = loader.getController();

        if (ClientUI.LibClient != null) {
            ClientUI.LibClient.setController(addController);
        } else {
            System.out.println("Not connected to server");
        }

        Scene scene = new Scene(root);            
        primaryStage.setTitle("Add subscriber");
        primaryStage.setScene(scene);        
        primaryStage.show();
    }

    /**
     * Handles the response from the server and updates the server message label.
     * 
     * @param response The response message received from the server.
     */
    public void inputResponse(String response) {
        this.serverMessage = response;
        Platform.runLater(() -> {
            lblServerMessage.setText(serverMessage);
        });
    }

    /**
     * Navigates to the server connection page.
     * 
     * @param event The ActionEvent triggered by the 'Show Connection' button click.
     * @throws IOException If there is an error during the page load.
     */
    public void ShowConnection(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        ((Node)event.getSource()).getScene().getWindow().hide(); //hiding primary window
        Stage primaryStage = new Stage();
        Pane root = loader.load(getClass().getResource("/commonGui/ClientConnect.fxml").openStream());

        ClientConnectController ConnController = loader.getController();

        ClientUI.LibClient.setController(ConnController);
        ClientUI.LibClient.requestFromServer("ShowConnection");
    }

    /**
     * Navigates to the report viewing page.
     * 
     * @param event The ActionEvent triggered by the 'View Report' button click.
     * @throws Exception If there is an error during the page load.
     */
    @FXML
    public void viewReport(ActionEvent event) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/LibrarianGui/ClientLibViewReport.fxml"));
        Parent root = loader.load();
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        currentStage.setScene(scene);
        currentStage.setTitle("View Report Page");
        currentStage.centerOnScreen();

        currentStage.show();
    }

    /**
     * Navigates to the messages page to view all messages.
     * 
     * @param event The ActionEvent triggered by the 'Show Messages' button click.
     */
    @FXML
    void showMessages(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/commonGui/ClientShowMessages.fxml"));
            Parent root = loader.load();

            // Get the controller for the messages page
            ClientShowMessagesController controller = loader.getController();

            // Pass the librarian ID to fetch messages
            controller.setLibrarianId(librarian.getId());
            controller.setPage("LibHome");

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
     * Navigates to the book search page.
     * 
     * @param event The ActionEvent triggered by the 'Search Book' button click.
     */
    @FXML
    void searchBook(ActionEvent event) {
        navigateToPage(event, "/commonGui/ClientDisplayBooks.fxml", "Search Book Page");
    }

    /**
     * Navigates to the subscribers display page.
     * 
     * @param event The ActionEvent triggered by the 'Show Subscribers' button click.
     */
    @FXML
    private void ShowSubscribers(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/librarianGui/ClientLibDisplaySub.fxml"));
            Parent root = loader.load();
            ClientLibDisplaySubController.setLibrarian(this.librarian);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Show Subscribers");
            stage.centerOnScreen();

            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showErrorMessage("Failed to load page: " + "Show Subscribers");
        }
    }

    /**
     * Navigates to the login page.
     * 
     * @param event The ActionEvent triggered by the 'Logout' button click.
     */
    @FXML
    void gotoLoginPage(ActionEvent event) {
        navigateToPage(event, "/commonGui/ClientCommonHome.fxml", "Login Page");
    }

    /**
     * Navigates to the book borrowing page.
     * 
     * @param event The ActionEvent triggered by the 'Borrow Book' button click.
     */
    @FXML
    void BorrowBook(ActionEvent event) {
        navigateToPage(event, "/librarianGui/ClientLibBorrowBook.fxml", "Borrow Page");
    }

    /**
     * Navigates to the book return page.
     * 
     * @param event The ActionEvent triggered by the 'Return Book' button click.
     */
    @FXML 
    void returnBook(ActionEvent event) {
        navigateToPage(event, "/librarianGui/ClientLibReturnBook.fxml", "Return Page");
    }

    /**
     * Navigates to a page based on the provided FXML path and page title.
     * 
     * @param event The ActionEvent that triggered the navigation.
     * @param fxmlPath The path to the FXML file to load.
     * @param pageTitle The title to set for the new page.
     */
    private void navigateToPage(ActionEvent event, String fxmlPath, String pageTitle) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            if(pageTitle.equals("Search Book Page")) {
                ClientDisplayBooksController controller = loader.getController();
                controller.setPage("Librarian Home Page");
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
     * Displays an error message on the server message label in red.
     * 
     * @param message The error message to be displayed.
     */
    private void showErrorMessage(String message) {
        Platform.runLater(() -> {
            lblServerMessage.setText(message);
            lblServerMessage.setTextFill(Color.RED);
        });
    }
}
