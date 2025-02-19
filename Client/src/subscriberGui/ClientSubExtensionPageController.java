package subscriberGui;

import java.time.LocalDate;

import Shared_classes.Borrow;
import Shared_classes.ClientServerContact;
import Shared_classes.Command;
import Shared_classes.User;
import client.ClientUI;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * Controller for the Subscriber Extension Page in the subscriber UI.
 * This page allows the subscriber or librarian to extend a borrow and view borrow details.
 * 
 * @author Majd Awad
 * @version 1.0
 */
public class ClientSubExtensionPageController {

    /**
     * Label that displays the borrow details (ID and Due Date).
     */
    @FXML
    private Label lblBorrowDetails;

    /**
     * Label that provides a description of the page's functionality.
     */
    @FXML
    private Label lblPageDescription;

    /**
     * Button to trigger the extension of a borrow.
     */
    @FXML
    private Button btnExtend;

    /**
     * Button to navigate back to the previous page.
     */
    @FXML
    private Button btnBack;

    /**
     * The current borrow object that contains the borrow details.
     */
    private Borrow borrow;

    /**
     * The page type that determines whether the request is for a subscriber or librarian.
     */
    private String page;

    /**
     * The librarian associated with the manual extension process.
     */
    private User librarian;

    /**
     * Sets the borrow details and page description based on the borrow and page type.
     * 
     * @param borrow The borrow object containing borrow details.
     * @param page The page type (either "Subscriber Personal Card" or "Librarian Personal Card").
     */
    public void setBorrowData(Borrow borrow, String page) {
        this.borrow = borrow;
        this.page = page;

        // Set the borrow details in the label
        lblBorrowDetails.setText("Borrow ID: " + borrow.getId() + ",\nDue Date: " + borrow.getDue_date());

        // Set the page description based on the page type
        if ("Subscriber Personal Card".equals(page)) {
            lblPageDescription.setText("Requesting an extension for the borrow.");
        } else if ("Librarian Personal Card".equals(page)) {
            lblPageDescription.setText("Manually extend the borrow.");
        }
    }

    /**
     * Sets the librarian object for the manual extension process.
     * 
     * @param lib The librarian user who is manually extending the borrow.
     */
    public void setLib(User lib) {
        librarian = lib;
    }

    /**
     * Handles the event triggered when the extend borrow button is clicked.
     * Sends a request to the server to extend the borrow, depending on the page type.
     * Updates the borrow details label with a confirmation message.
     * 
     * @param event The ActionEvent triggered by clicking the 'Extend' button.
     */
    @FXML
    void extendBorrow(ActionEvent event) {
        if ("Subscriber Personal Card".equals(page)) {
            // Request from the subscriber to extend the borrow
            ClientServerContact msg = new ClientServerContact(borrow.getId(), Command.isReturned_isOrderd);
            ClientUI.LibClient.requestFromServer(msg);
        } else if ("Librarian Personal Card".equals(page)) {
            // Request from the librarian to manually extend the borrow
            ClientServerContact msg = new ClientServerContact(borrow.getId(), librarian, Command.manual_Extension);
            ClientUI.LibClient.requestFromServer(msg);
        }

        // Update the label after sending the request
        lblBorrowDetails.setText("Check your messages about the extension!");
    }

    /**
     * Handles the event triggered when the back button is clicked.
     * Navigates back to the previous page displaying the list of borrows.
     * 
     * @param event The ActionEvent triggered by clicking the 'Back' button.
     */
    @FXML
    void goBack(ActionEvent event) {
        try {
            // Navigate back to the previous page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/commonGui/ClientDisplayBorrows.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnBack.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
            stage.setTitle("Previous Page");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
