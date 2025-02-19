package librarianGui;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import client.BraudeLibClient;
import client.ClientUI;
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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import librarianGui.ClientLibHomeController;
import librarianGui.ClientLibPersonalSubCardController;
import subscriberGui.ClientSubHomeController;
import subscriberGui.ClientSubPersonalCardController;
import javafx.scene.control.TextField;
import Shared_classes.Borrow;
import Shared_classes.ClientServerContact;
import Shared_classes.Command;
import Shared_classes.Returns;
import Shared_classes.User;
import Shared_classes.UserWithSubscriber;
import Shared_classes.userType;

/**
 * Controller for the Book Return page in the Librarian UI.
 * Allows librarians to return books, mark them as lost, and navigate back to the previous page.
 * 
 * @author Majd Awad
 * @version 1.1dz
 */
public class ClientLibReturnBookController {

    /**
     * Button to navigate back to the previous page.
     */
    @FXML
    private Button btnBack;

    /**
     * Button to process the return of a book.
     * When clicked, the selected book is returned based on the barcode.
     */
    @FXML
    private Button btnReturn;

    /**
     * Button to mark a book as lost.
     * When clicked, the selected book is marked as lost based on the barcode.
     */
    @FXML
    private Button btnLost;

    /**
     * Label to display the borrow status or error messages related to book return.
     */
    @FXML
    private Label lblBorrow;

    /**
     * TextField for entering the barcode of the book to be returned or marked as lost.
     */
    @FXML
    private TextField txtBarcode;

    /**
     * Label to display server response messages (success or error).
     */
    @FXML
    private Label lblServerMessage;

    /**
     * The server response message that will be displayed to the librarian.
     */
    private String serverMessage;

    /**
     * Marks the book as lost based on the barcode entered in the text field.
     * 
     * @param event The ActionEvent triggered by the 'Mark as Lost' button click.
     */
    @FXML
    void LostBook(ActionEvent event) {
        String Barcode = txtBarcode.getText();
        ClientServerContact csc = new ClientServerContact(Barcode, Command.LostBook);
        ClientUI.LibClient.setController(this);
        ClientUI.LibClient.requestFromServer(csc);
    }

    /**
     * Returns the book based on the barcode entered in the text field.
     * 
     * @param event The ActionEvent triggered by the 'Return Book' button click.
     */
    @FXML
    void ReturnBook(ActionEvent event) {
        String Barcode = txtBarcode.getText();
        ClientServerContact csc = new ClientServerContact(Barcode, Command.ReturnBook);
        ClientUI.LibClient.setController(this);
        ClientUI.LibClient.requestFromServer(csc);
    }

    /**
     * Navigates back to the previous page (Librarian Home page).
     * 
     * @param event The ActionEvent triggered by the 'Back' button click.
     */
    @FXML
    void getBackBtn(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader();
            Parent previousScreen = null;
            loader.setLocation(getClass().getResource("/librarianGui/ClientLibHomePage.fxml"));
            previousScreen = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(previousScreen);
            stage.setScene(scene);
            stage.setTitle("");
            stage.centerOnScreen();

            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the response from the server and updates the server message label.
     * If the book was successfully returned or marked as lost, it shows a success message in green.
     * Otherwise, an error message is displayed in red.
     * 
     * @param response The response message received from the server (success or error).
     */
    public void inputResponse(String response) {
        this.serverMessage = response;
        Platform.runLater(() -> {
            lblServerMessage.setText(serverMessage);
            if ("Book returned successfully.".equals(serverMessage) || "Marked as lost.".equals(serverMessage)) 
                lblServerMessage.setTextFill(Color.GREEN);
            else 
                lblServerMessage.setTextFill(Color.RED);
        });
    }
}