package subscriberGui;

import javafx.scene.control.TextField;
import java.util.Optional;
import Shared_classes.ClientServerContact;
import Shared_classes.Command;
import Shared_classes.Subscriber;
import Shared_classes.User;
import Shared_classes.UserWithSubscriber;
import client.ClientUI;
import commonGui.ClientDisplayBorrowsController;
import commonGui.ClientDisplayOrdersController;
import commonGui.ClientDisplayReturnsController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * Controller class for managing a subscriber's personal card page, where they can 
 * update their personal details, view borrows, orders, and returns.
 * 
 * @author Majd Awad
 * @version 1.1dz
 */
public class ClientSubPersonalCardController {

    // Input fields for subscriber data modification
    /**
     * TextField for updating subscriber's password.
     * Used when the subscriber wants to change their password.
     */
    @FXML
    private TextField txtPassword;  

    /**
     * TextField for updating subscriber's first name.
     * Used when the subscriber wants to change their first name.
     */
    @FXML
    private TextField txtFName;    

    /**
     * TextField for updating subscriber's last name.
     * Used when the subscriber wants to change their last name.
     */
    @FXML
    private TextField txtLName;    

    /**
     * TextField for updating subscriber's phone number.
     * Used when the subscriber wants to change their phone number.
     */
    @FXML
    private TextField txtPhoneNum; 

    /**
     * TextField for updating subscriber's email address.
     * Used when the subscriber wants to change their email address.
     */
    @FXML
    private TextField txtEmail;    

    // Labels for displaying subscriber information
    /**
     * Label to display the subscriber's ID.
     */
    @FXML
    private Label lblId;           

    /**
     * Label to display the subscriber's username.
     */
    @FXML
    private Label lblUsername;     

    /**
     * Label to display the subscriber's first name.
     */
    @FXML
    private Label lblFName;        

    /**
     * Label to display the subscriber's last name.
     */
    @FXML
    private Label lblLName;        

    /**
     * Label to display the subscriber's phone number.
     */
    @FXML
    private Label lblPhoneNum;     

    /**
     * Label to display the subscriber's email address.
     */
    @FXML
    private Label lblEmail;        

    /**
     * Label to display the subscriber's status (active/inactive).
     */
    @FXML
    private Label lblStatus;       

    /**
     * Label to display the number of late returns.
     */
    @FXML
    private Label lblLateReturns;  

    /**
     * Label for displaying system messages or error messages to the subscriber.
     */
    @FXML
    private Label lblMessage;      

    /**
     * Label displaying a personalized welcome message to the subscriber.
     */
    @FXML
    private Label lblWelcome;      

    /**
     * Label to display the subscriber number.
     */
    @FXML
    private Label lblSubscriberNumber; 

    /**
     * Label used to display server response messages (success/failure messages).
     */
    @FXML
    private Label lblServerMessage;    

    // Buttons for performing actions
    /**
     * Button for updating subscriber information.
     * When clicked, updates the subscriber's details in the system.
     */
    @FXML
    private Button btnUpdate;      

    /**
     * Button to view the subscriber's order history.
     * When clicked, navigates to the order history page.
     */
    @FXML
    private Button OrderHistory;   

    /**
     * Button to view the subscriber's borrow history.
     * When clicked, navigates to the borrow history page.
     */
    @FXML
    private Button BorrowHistory;  

    /**
     * Button to view the subscriber's return history.
     * When clicked, navigates to the return history page.
     */
    @FXML
    private Button ReturnHistory;  

    /**
     * Button to navigate back to the previous page.
     * When clicked, returns to the previous page (e.g., Subscriber Home Page).
     */
    @FXML
    private Button btnBack;        

    /**
     * Button to navigate to the messages page.
     * When clicked, opens the messages page to view unread messages.
     */
    @FXML
    private Button btnMessage;    

    // Static fields for holding the current subscriber and user information
    /**
     * Static field to store the subscriber information.
     */
    private static UserWithSubscriber subscriber;

    /**
     * Static field to store the user information.
     */
    private static User user;

    /**
     * Sets the user data for the current session.
     * 
     * @param u The user object to be set.
     */
    public static void setUser(User u) {
        user = u;
    }

    /**
     * Sets the subscriber data for the current session.
     * 
     * @param sub The subscriber object to be set.
     */
    public static void setSubscriber(UserWithSubscriber sub) {
        subscriber = sub;
    }

    /**
     * Initializes the page and sets up the subscriber information.
     * Requests the subscriber data from the server and populates the fields.
     * 
     * @throws InterruptedException If the thread is interrupted during a sleep.
     */
    @FXML
    public void initialize() throws InterruptedException {
        lblWelcome.setText("Welcome, " + user.getFirstName());
        if (user == null) 
            return;
        
        // Request subscriber data from the server
        ClientServerContact csc = new ClientServerContact(user.getUsername(), Command.DisplaySubscribers);
        ClientUI.LibClient.setController(this);
        ClientUI.LibClient.requestFromServer(csc);

        setSubscriberData(subscriber);
    }

    /**
     * Sets the subscriber data in the form fields.
     * 
     * @param subscriber The subscriber object containing the data to be displayed.
     */
    @FXML
    void setSubscriberData(UserWithSubscriber subscriber) {
        if (subscriber != null) {
            lblId.setText(String.valueOf(subscriber.getId()));
            lblUsername.setText(subscriber.getUsername());
            txtPassword.setText(subscriber.getPassword());
            txtFName.setText(subscriber.getFirstName());
            txtLName.setText(subscriber.getLastName());
            txtPhoneNum.setText(subscriber.getPhone());
            txtEmail.setText(subscriber.getEmail());
            lblStatus.setText(subscriber.getStatus());
            lblLateReturns.setText(String.valueOf(subscriber.getLateReturn()));
            lblSubscriberNumber.setText(String.valueOf(subscriber.getSubscriberNumber()));
        }
    }

    /**
     * Retrieves the updated subscriber data from the form fields.
     * 
     * @return A UserWithSubscriber object containing the updated data.
     */
    private UserWithSubscriber getSubscriberDataFromFields() {
        UserWithSubscriber subscriber = new UserWithSubscriber();
        
        // Create User and Subscriber objects if needed
        User user = new User();
        Subscriber sub = new Subscriber();

        // Populate the User object
        user.setId(Integer.parseInt(lblId.getText().trim())); // Assuming ID is always valid
        user.setUsername(lblUsername.getText().trim());
        user.setPassword(txtPassword.getText().trim());
        user.setFirstName(txtFName.getText().trim());
        user.setLastName(txtLName.getText().trim());

        // Populate the Subscriber object
        sub.setPhone_number(txtPhoneNum.getText().trim());
        sub.setEmail(txtEmail.getText().trim());
        sub.setStatus(lblStatus.getText().trim());
        sub.setLateReturns(Integer.parseInt(lblLateReturns.getText().trim())); // Assuming valid integer
        sub.setSubscriber_number(Integer.parseInt(lblSubscriberNumber.getText().trim())); // Assuming valid integer
        // Associate the User and Subscriber objects with the UserWithSubscriber instance
        subscriber.setUser(user);
        subscriber.setSubscriber(sub);
        return subscriber;
    }

    /**
     * Handles the "Update" button click event.
     * Updates the subscriber's details and sends the data to the server.
     * 
     * @param event The ActionEvent triggered by the button click.
     * @throws InterruptedException If the thread is interrupted during a sleep.
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
     * Navigates to the Subscriber Home Page.
     * 
     * @param event The ActionEvent triggered by the "Back" button click.
     */
    @FXML
    void gotoSubHomePage(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/subscriberGui/ClientSubHomePage.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Subscriber Home Page");
            stage.centerOnScreen();

            ClientSubHomeController controller = loader.getController();
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Navigates to the "Borrows" page to view the subscriber's borrow history.
     * 
     * @param event The ActionEvent triggered by the "View Borrows" button click.
     */
    @FXML
    void viewBorrows(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/commonGui/ClientDisplayBorrows.fxml"));
            ClientDisplayBorrowsController.setPage("Subscriber Personal Card");
            ClientDisplayBorrowsController.setSubscriberId(Integer.toString(user.getId()));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Borrows Page");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Navigates to the "Returns" page to view the subscriber's return history.
     * 
     * @param event The ActionEvent triggered by the "View Returns" button click.
     */
    @FXML
    void viewReturns(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/commonGui/ClientDisplayReturns.fxml"));
            ClientDisplayReturnsController.setPage("Subscriber Personal Card");
            ClientDisplayReturnsController.setSubscriberId(Integer.toString(user.getId()));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Returns Page");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Navigates to the "Orders" page to view the subscriber's order history.
     * 
     * @param event The ActionEvent triggered by the "Order History" button click.
     */
    @FXML
    void gotoOrderHistory(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/commonGui/ClientDisplayOrders.fxml"));
            ClientDisplayOrdersController.setPage("Subscriber Personal Card");
            ClientDisplayOrdersController.setSubscriberId(Integer.toString(subscriber.getId()));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Orders Page");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Processes the server response and updates the subscriber data or displays a message.
     * 
     * @param response The response object from the server (either a message or UserWithSubscriber).
     */
    public void inputResponse(Object response) {
        Platform.runLater(() -> {
            if (response instanceof String) {
                System.out.println("Input " + response.toString());
                lblServerMessage.setText(response.toString());
                if ("Subscriber Updated Successfully".equals(response.toString())) 
                    lblServerMessage.setTextFill(Color.GREEN);
                else 
                    lblServerMessage.setTextFill(Color.RED);
            } else if (response instanceof UserWithSubscriber) {
                subscriber = (UserWithSubscriber) response;
                setSubscriberData(subscriber);
            }
        });
    }
}