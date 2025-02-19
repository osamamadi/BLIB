package librarianGui;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.DateCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.util.Callback;
import javafx.scene.paint.Color; 

import java.sql.Date;
import java.time.LocalDate;
import Shared_classes.Borrow;
import Shared_classes.ClientServerContact;
import Shared_classes.Command;
import client.ClientUI;
import commonGui.ClientDisplayBooksController;

/**
 * Controller class for managing the borrowing of books.
 * @author Majd Awad
 * @version 1.1dz
 */
public class ClientLibBorrowBookController {

    /** TextField for entering the subscriber's ID. */
    @FXML
    private TextField txtSubscriberId;

    /** TextField for entering the barcode of the book. */
    @FXML
    private TextField txtBarcode;

    /** DatePicker for selecting the borrow date. */
    @FXML
    private DatePicker borrowDatePicker;

    /** DatePicker for selecting the return date. */
    @FXML
    private DatePicker returnDatePicker;

    /** Button to submit the borrow action. */
    @FXML
    private Button btnSubmitBorrow;

    /** Button to clear all fields. */
    @FXML
    private Button btnClearFields;

    /** Button to navigate back to the previous page. */
    @FXML
    private Button btnBack;

    /** Label for displaying server messages. */
    @FXML
    private Label lblServerMessage;

    /** Button to scan a book's barcode. */
    @FXML
    private Button btnScanBook;

    /** Variable to hold the barcode value of the book. */
    private String barcode;

    /** Variable to hold the server message. */
    private String serverMessage;

    /**
     * Scans the book barcode by opening a small window for the user to input the barcode.
     * @param event the ActionEvent triggered by the user clicking the "Scan Book" button.
     */
    @FXML
    void scanBook(ActionEvent event) {
        // Create a new Stage for the small window
        Stage stage = new Stage();
        stage.setTitle("Scan Book");

        // Create a layout (AnchorPane)
        AnchorPane root = new AnchorPane();
        root.setStyle("-fx-background-color: #f4f4f4; -fx-padding: 20px; -fx-border-color: #2b4356; -fx-border-radius: 10px;");

        // Create a label for the instruction
        Label label = new Label("Enter Book's Barcode:");
        label.setFont(new Font("Footlight MT Light", 22));
        label.setLayoutX(20);
        label.setLayoutY(20);

        // Create a text field for entering the barcode
        TextField barcodeField = new TextField();
        barcodeField.setPromptText("Barcode...");
        barcodeField.setLayoutX(20);
        barcodeField.setLayoutY(60);
        barcodeField.setPrefWidth(260);

        // Create a submit button with specified styling
        Button submitButton = new Button("Submit Barcode");
        submitButton.setId("btnSubmitBorrow");
        submitButton.setLayoutX(90); // Adjust this as per your layout
        submitButton.setLayoutY(110); // Adjust this as per your layout
        submitButton.setPrefHeight(39.0);
        submitButton.setPrefWidth(139.0);
        submitButton.setStyle("-fx-font-size: 14px; -fx-background-color: #2b4356; -fx-text-fill: white; -fx-cursor: hand; -fx-border-radius: 50px;");
        submitButton.setOpacity(0.72);

        // Add action for the submit button
        submitButton.setOnAction(e -> {
            String barcode = barcodeField.getText().trim();

            if (barcode.isEmpty()) {
                showAlert(AlertType.ERROR, "Validation Error", "Barcode cannot be empty.");
            } else {
                this.barcode = barcode;
                stage.close(); // Close the small window after submission
            }
        });

        // Add components to the layout
        root.getChildren().addAll(label, barcodeField, submitButton);

        // Set the scene and show the stage
        Scene scene = new Scene(root, 300, 180); // Width 300, Height 180
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Clears all input fields and resets the form.
     * @param event the ActionEvent triggered by the user clicking the "Clear Fields" button.
     */
    @FXML
    void clearFields() {
        if (borrowDatePicker != null)
            borrowDatePicker.setValue(null);
        if (returnDatePicker != null)
            returnDatePicker.setValue(null);
        if (txtSubscriberId != null)
            txtSubscriberId.clear();
        if (txtBarcode != null)
            txtBarcode.clear();
        if (lblServerMessage != null)
            lblServerMessage.setText("");
    }

    /**
     * Sets up the return date picker with a maximum date of two weeks from the selected borrow date.
     * @param borrowDatePicker the DatePicker for the borrow date.
     * @param returnDatePicker the DatePicker for the return date.
     */
    private void setupReturnDatePicker(DatePicker borrowDatePicker, DatePicker returnDatePicker) {
        borrowDatePicker.valueProperty().addListener((observable, oldDate, newDate) -> {
            if (newDate != null) {
                // Set the day cell factory to restrict return dates to be up to 2 weeks from the borrow date
                returnDatePicker.setDayCellFactory(getDayCellFactory(newDate.plusWeeks(2)));
                // Clear the value of the return date picker (force user to reselect within the new range)
                returnDatePicker.setValue(null);
            } else {
                // Clear restrictions if borrow date is unselected
                returnDatePicker.setDayCellFactory(null);
            }
        });
    }

    /**
     * Submits the borrow action by validating the input and sending the borrow request to the server.
     * @param event the ActionEvent triggered by the user clicking the "Submit Borrow" button.
     */
    @FXML
    void submitBorrow(ActionEvent event) {
        System.out.println("Starting debugging journey :)");
        String subscriberId = txtSubscriberId.getText().trim();
        LocalDate borrowDate = borrowDatePicker.getValue();
        LocalDate returnDate = returnDatePicker.getValue();

        if (subscriberId.isEmpty() || barcode == null || barcode.isEmpty() || borrowDate == null || returnDate == null) {
            showAlert(AlertType.ERROR, "Validation Error", "please fill all fields.");
            return;
        }

        if (returnDate.isBefore(borrowDate)) {
            showAlert(AlertType.ERROR, "Validation Error", "Return date cannot be before borrow date.");
            return;
        }

        try {
            int subscriberIdInt = Integer.parseInt(subscriberId);

            Borrow b = new Borrow(Date.valueOf(borrowDate), Date.valueOf(returnDate), subscriberIdInt, barcode);
            ClientServerContact csc = new ClientServerContact(b, Command.BorrowBook);
            ClientUI.LibClient.setController(this);
            ClientUI.LibClient.requestFromServer(csc);
        } catch (NumberFormatException e) {
            showAlert(AlertType.ERROR, "Validation Error", "Subscriber ID and Barcode must be numeric.");
        }
    }

    /**
     * Navigates back to the previous screen (Home Page).
     * @param event the ActionEvent triggered by the user clicking the "Back" button.
     */
    @FXML
    void getBackBtn(ActionEvent event) {
        navigateToPage(event, "/librarianGui/ClientLibHomePage.fxml", "Home Page");
    }

    /**
     * Navigates to a specific page by loading the corresponding FXML file.
     * @param event the ActionEvent triggered by the user.
     * @param fxmlPath the path of the FXML file to load.
     * @param pageTitle the title for the page.
     */
    private void navigateToPage(ActionEvent event, String fxmlPath, String pageTitle) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            if (pageTitle.equals("Search Book Page")) {
                ClientDisplayBooksController controller = loader.getController();
                controller.setPage("HomeSubPage");
            }
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(pageTitle);
            stage.centerOnScreen();

            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Shows an alert message to the user.
     * @param type the type of the alert (e.g., ERROR, INFO).
     * @param title the title of the alert.
     * @param content the content of the alert.
     */
    private void showAlert(AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Initializes the date pickers and sets up any necessary event listeners.
     * This method is called when the controller is initialized.
     */
    @FXML
    public void initialize() {
        // Disable past dates for the borrowDatePicker
        borrowDatePicker.setDayCellFactory(getDayCellFactory(LocalDate.now()));
        // Disable past dates for the returnDatePicker
        returnDatePicker.setDayCellFactory(getDayCellFactory(LocalDate.now()));
        setupReturnDatePicker(borrowDatePicker, returnDatePicker);

        // Ensure the returnDatePicker cannot select a date before the borrowDatePicker's value
        borrowDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                returnDatePicker.setDayCellFactory(getDayCellFactory(newValue.plusDays(1)));
            }
        });
    }

    /**
     * Creates a callback for disabling certain days in a DatePicker based on a reference date.
     * @param referenceDate the reference date used to determine the valid date range.
     * @return the DateCell factory for the DatePicker.
     */
    private Callback<DatePicker, DateCell> getDayCellFactory(LocalDate referenceDate) {
        return datePicker -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);

                // Disable past dates for borrowDatePicker (if used for borrowDatePicker)
                if (datePicker.equals(borrowDatePicker) && item.isBefore(referenceDate)) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0cb;"); // Optional: Red background for disabled dates
                }
                // Restrict returnDatePicker to be no more than 2 weeks after the borrow date
                else if (datePicker.equals(returnDatePicker)) {
                    LocalDate maxDate = referenceDate.plusWeeks(2);
                    if (item.isBefore(referenceDate) || item.isAfter(maxDate)) {
                        setDisable(true);
                        setStyle("-fx-background-color: #ffc0cb;"); // Optional: Red background for disabled dates
                    }
                }
            }
        };
    }

    /**
     * Displays the server response message on the UI.
     * @param response the server response message to display.
     */
    public void inputResponse(String response) {
        this.serverMessage = response;
        Platform.runLater(() -> {
            lblServerMessage.setText(serverMessage);
            if ("Borrowed successfully.".equals(serverMessage)) {
                lblServerMessage.setTextFill(Color.GREEN);
            } else {
                lblServerMessage.setTextFill(Color.RED);
            }
        });
    }

}