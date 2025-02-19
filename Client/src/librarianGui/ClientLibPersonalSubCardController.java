package librarianGui;

import java.io.IOException;
import java.util.Optional;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import Shared_classes.ClientServerContact;
import Shared_classes.Command;
import Shared_classes.Subscriber;
import Shared_classes.User;
import Shared_classes.UserWithSubscriber;

import client.ClientUI;
import commonGui.ClientDisplayBorrowsController;
import commonGui.ClientDisplayOrdersController;
import commonGui.ClientDisplayReturnsController;

/**
 * Controller for managing the personal subscriber card in the librarian's UI.
 * Allows the librarian to view and update subscriber details, manage borrows, orders, and returns.
 * 
 * @author [Your Name]
 * @version 1.0
 */
public class ClientLibPersonalSubCardController {

    /**
     * Label that displays the subscriber's ID.
     */
    @FXML
    private Label lblId;

    /**
     * Text field for the subscriber's username.
     */
    @FXML
    private TextField txtUsername;

    /**
     * Text field for the subscriber's password.
     */
    @FXML
    private TextField txtPassword;

    /**
     * Text field for the subscriber's first name.
     */
    @FXML
    private TextField txtFName;

    /**
     * Text field for the subscriber's last name.
     */
    @FXML
    private TextField txtLName;

    /**
     * Text field for the subscriber's phone number.
     */
    @FXML
    private TextField txtPhoneNum;

    /**
     * Text field for the subscriber's email address.
     */
    @FXML
    private TextField txtEmail;

    /**
     * Label that displays the subscriber's current status.
     */
    @FXML
    private Label lblStatus;

    /**
     * Text field that shows the subscriber's number of late returns.
     */
    @FXML
    private TextField txtLateReturns;

    /**
     * Button to navigate back to the previous page.
     */
    @FXML
    private Button btnBack;

    /**
     * Button to display the subscriber's borrowing history.
     */
    @FXML
    private Button btnDisplayBorrow;

    /**
     * Button to display the subscriber's order history.
     */
    @FXML
    private Button btnDisplayOrders;

    /**
     * Button to update the subscriber's details.
     */
    @FXML
    private Button btnUpdate;

    /**
     * Label to display the subscriber's unique subscriber number.
     */
    @FXML
    private Label lblSubscriberNumber;

    /**
     * Label to welcome the librarian.
     */
    @FXML
    private Label lblWelcome;

    /**
     * Label to display server response messages.
     */
    @FXML
    private Label lblServerMessage;

    /**
     * Static instance of the subscriber associated with the current card.
     */
    private static UserWithSubscriber subscriber;

    /**
     * ID of the currently logged-in librarian.
     */
    private Integer id;

    /**
     * Static instance of the librarian.
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
     * Sets the subscriber user object with associated subscriber information.
     * 
     * @param sub The subscriber user object to be set.
     */
    public static void setSubscriber(UserWithSubscriber sub) {
        subscriber = sub;
    }

    /**
     * Initializes the subscriber's personal card with data from the subscriber object.
     * Displays the librarian's name on the welcome label and sets subscriber data.
     */
    @FXML
    public void initialize() {
        lblWelcome.setText("Welcome, " + librarian.getFirstName());
        setSubscriberData(subscriber);
        System.out.println("Subscriber is : " + subscriber);
        System.out.println("Librarian  is : " + librarian);
    }

    /**
     * Populates the subscriber's personal card with details from the subscriber object.
     * 
     * @param UWS The subscriber object containing user and subscriber details.
     */
    @FXML
    void setSubscriberData(UserWithSubscriber UWS) {
        if (subscriber != null) {
            lblId.setText(String.valueOf(subscriber.getId()));
            txtUsername.setText(subscriber.getUsername());
            txtPassword.setText(subscriber.getPassword());
            txtFName.setText(subscriber.getFirstName());
            txtLName.setText(subscriber.getLastName());
            txtPhoneNum.setText(subscriber.getPhone());
            txtEmail.setText(subscriber.getEmail());
            lblStatus.setText(subscriber.getStatus());
            txtLateReturns.setText(String.valueOf(subscriber.getLateReturn()));
            lblSubscriberNumber.setText(String.valueOf(subscriber.getSubscriberNumber()));
        }
    }

    /**
     * Retrieves subscriber data from the text fields and returns a new UserWithSubscriber object.
     * 
     * @return A new UserWithSubscriber object populated with data from the text fields.
     */
    private UserWithSubscriber getSubscriberDataFromFields() {
        UserWithSubscriber subscriber = new UserWithSubscriber();

        // Create User and Subscriber objects if needed
        User user = new User();
        Subscriber sub = new Subscriber();

        // Populate the User object
        user.setId(Integer.parseInt(lblId.getText().trim())); // Assuming ID is always valid
        user.setUsername(txtUsername.getText().trim());
        user.setPassword(txtPassword.getText().trim());
        user.setFirstName(txtFName.getText().trim());
        user.setLastName(txtLName.getText().trim());

        // Populate the Subscriber object
        sub.setPhone_number(txtPhoneNum.getText().trim());
        sub.setEmail(txtEmail.getText().trim());
        sub.setStatus(lblStatus.getText().trim());
        sub.setLateReturns(Integer.parseInt(txtLateReturns.getText().trim())); // Assuming valid integer
        sub.setSubscriber_number(Integer.parseInt(lblSubscriberNumber.getText().trim())); // Assuming valid integer

        // Associate the User and Subscriber objects with the UserWithSubscriber instance
        subscriber.setUser(user);
        subscriber.setSubscriber(sub);
        return subscriber;
    }

    /**
     * Navigates back to the subscriber display page.
     * 
     * @param event The ActionEvent triggered by the 'Back' button click.
     * @throws IOException If there is an error during the page load.
     */
    @FXML
    void goBack(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        ((Node) event.getSource()).getScene().getWindow().hide(); // Hiding primary window
        Stage primaryStage = new Stage();
        Pane root = loader.load(getClass().getResource("/librarianGui/ClientLibDisplaySub.fxml").openStream());
        ClientDisplayBorrowsController.setPage("Librarian Display Subscribers");

        // Get the controller for ClientDisplay.fxml
        ClientLibDisplaySubController displayController = loader.getController();
        ClientLibDisplaySubController.setLibrarian(this.librarian);

        // Create the client and set the display controller
        if (ClientUI.LibClient != null) {
            ClientUI.LibClient.setController(displayController);
        } else {
            System.out.println("Not connected to server");
        }

        Scene scene = new Scene(root);
        primaryStage.setTitle("Display Tool");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Handles the update button click to update the subscriber's details on the server.
     * 
     * @param event The ActionEvent triggered by the 'Update' button click.
     * @throws InterruptedException If the thread is interrupted during the update process.
     */
    @FXML
    void getUpdateBtn(ActionEvent event) throws InterruptedException {
        setSubscriber(getSubscriberDataFromFields());
        ClientServerContact csc = new ClientServerContact(subscriber, Command.UpdateSubscriber);
        ClientUI.LibClient.setController(this);
        ClientUI.LibClient.requestFromServer(csc);
        Thread.sleep(100);
        setSubscriberData(subscriber);
    }

    /**
     * Navigates to the borrows page to view the subscriber's borrowing history.
     * 
     * @param event The ActionEvent triggered by the 'View Borrows' button click.
     */
    @FXML
    void viewBorrows(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/commonGui/ClientDisplayBorrows.fxml"));
            ClientDisplayBorrowsController.setPage("Librarian Personal Card");
            ClientDisplayBorrowsController.setLib(librarian);
            ClientDisplayBorrowsController.setSubscriberId(Integer.toString(subscriber.getId()));

            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Borrows Page");
            stage.centerOnScreen();

            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Navigates to the orders page to view the subscriber's order history.
     * 
     * @param event The ActionEvent triggered by the 'View Orders' button click.
     */
    @FXML
    void gotoOrderHistory(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/commonGui/ClientDisplayOrders.fxml"));
            ClientDisplayOrdersController.setPage("Librarian Personal Card");
            ClientDisplayOrdersController.setSubscriberId(Integer.toString(subscriber.getId()));
            ClientDisplayOrdersController.setLibrarian("librarian");

            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Orders Page");
            stage.centerOnScreen();

            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Navigates to the returns page to view the subscriber's return history.
     * 
     * @param event The ActionEvent triggered by the 'View Returns' button click.
     */
    @FXML
    void viewReturns(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/commonGui/ClientDisplayReturns.fxml"));
            ClientDisplayReturnsController.setPage("Librarian Personal Card");
            ClientDisplayReturnsController.setLib(librarian);
            ClientDisplayReturnsController.setSubscriberId(Integer.toString(subscriber.getId()));

            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Returns Page");
            stage.centerOnScreen();

            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Displays the server response message on the UI.
     * If the update is successful, the message is displayed in green, else in red.
     * 
     * @param response The server response message to be displayed.
     */
    public void inputResponse(String response) {
        Platform.runLater(() -> {
            lblServerMessage.setText(response);
            if ("Subscriber Updated Successfully".equals(response)) {
                lblServerMessage.setTextFill(Color.GREEN);
            } else {
                lblServerMessage.setTextFill(Color.RED);
            }
        });
    }
}
