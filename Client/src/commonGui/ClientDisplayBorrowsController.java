package commonGui;

import java.io.IOException;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import librarianGui.ClientLibDisplaySubController;
import librarianGui.ClientLibHomeController;
import librarianGui.ClientLibPersonalSubCardController;
import subscriberGui.ClientSubExtensionPageController;
import subscriberGui.ClientSubHomeController;
import subscriberGui.ClientSubPersonalCardController;
import javafx.scene.control.TextField;
import Shared_classes.Borrow;
import Shared_classes.ClientServerContact;
import Shared_classes.Command;
import Shared_classes.User;
import Shared_classes.UserWithSubscriber;

/**
 * Controller class for managing the display of borrow records in the system.
 * This class handles the interaction between the user interface and the backend services
 * for displaying, searching, and extending borrow records.
 * 
 * Author: [Your Name]
 * Version: 1.0
 */
public class ClientDisplayBorrowsController {
	/**
	 * Button for navigating back to the previous screen.
	 */
	@FXML
	private Button btnBack;

	/**
	 * Button for sending or displaying messages related to borrowing.
	 */
	@FXML
	private Button btnMessage;

	/**
	 * Button for performing search operations based on user input.
	 */
	@FXML
	private Button btnSearch;

	/**
	 * Button for displaying all borrow records.
	 */
	@FXML
	private Button btnShowAll;

	/**
	 * Button for reading specific borrowing information.
	 */
	@FXML
	private Button Readbtn;

	/**
	 * TableView displaying borrowing information for users.
	 */
	@FXML
	private TableView<Borrow> BorrowTable;

	/**
	 * Table column displaying the ID of the borrow record.
	 */
	@FXML
	private TableColumn<Borrow, Integer> BorrowIdColumn;

	/**
	 * Table column displaying the ID of the user who borrowed the book.
	 */
	@FXML
	private TableColumn<Borrow, Integer> UserIdColumn;

	/**
	 * Table column displaying the ID of the borrowed book copy.
	 */
	@FXML
	private TableColumn<Borrow, Integer> CopyIdColumn;

	/**
	 * Table column displaying the date the book was borrowed.
	 */
	@FXML
	private TableColumn<Borrow, Date> BorrowDateColumn;

	/**
	 * Table column displaying the due date for returning the book.
	 */
	@FXML
	private TableColumn<Borrow, Date> DueDateColumn;

	/**
	 * Table column displaying the current status of the borrowing record 
	 * (e.g., active, returned, overdue).
	 */
	@FXML
	private TableColumn<Borrow, String> StatusColumn;

	/**
	 * TextField for entering or displaying the borrow record ID.
	 */
	@FXML
	private TextField txtBorrowId;

	/**
	 * Observable list holding data for the borrow records displayed in the TableView.
	 */
	private ObservableList<Borrow> BorrowData = FXCollections.observableArrayList();

	/**
	 * Static variable representing the current page for navigation purposes.
	 */
	private static String page;

	/**
	 * Message received from the server for communication purposes.
	 */
	private String serverMessage;

	/**
	 * Static variable holding the ID of the current subscriber.
	 */
	private static String subscriber_id;

	/**
	 * Static variable representing the librarian user currently logged in.
	 */
	private static User librarian;

    /**
     * Sets the subscriber ID for the current session.
     * 
     * @param id The subscriber ID to be set.
     */
    public static void setSubscriberId(String id) {
        subscriber_id = id;
    }

    /**
     * Sets the librarian details for the current session.
     * 
     * @param lib The librarian user object to be set.
     */
    public static void setLib(User lib) {
        librarian = lib;
    }

    /**
     * Sets the current page context for navigation purposes.
     * 
     * @param p The page name to be set.
     */
    public static void setPage(String p) {
        page = p;
    }

    /**
     * Initializes the controller and sets up the table columns, event listeners, 
     * and requests the list of borrows from the server.
     */
    @FXML
    public void initialize() {
        // Existing setup for columns
        BorrowIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        UserIdColumn.setCellValueFactory(new PropertyValueFactory<>("user_id"));
        CopyIdColumn.setCellValueFactory(new PropertyValueFactory<>("copyId"));
        BorrowDateColumn.setCellValueFactory(new PropertyValueFactory<>("borrow_date"));
        DueDateColumn.setCellValueFactory(new PropertyValueFactory<>("due_date"));
        StatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        BorrowTable.setItems(BorrowData);

        // Add row click listener
        BorrowTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // Double click
                Borrow selectedBorrow = BorrowTable.getSelectionModel().getSelectedItem();
                if (selectedBorrow != null) {
                    handleRowClick(selectedBorrow);
                }
            }
        });

        // Request borrows data from the server
        ClientServerContact msg = new ClientServerContact(subscriber_id, Command.DisplayBorrows);
        ClientUI.LibClient.setController(this);
        ClientUI.LibClient.requestFromServer(msg);
    }

    /**
     * Handles the event when a row in the borrow table is clicked.
     * Opens the extension page to allow the user to extend the borrow period.
     * 
     * @param selectedBorrow The borrow record that was clicked.
     */
    private void handleRowClick(Borrow selectedBorrow) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/subscriberGui/extensionPage.fxml"));
            Parent root = loader.load();

            // Pass data to the new page's controller
            ClientSubExtensionPageController controller = loader.getController();
            controller.setBorrowData(selectedBorrow, page);
            controller.setLib(librarian);

            // Display the extension page in a new window
            Stage stage = new Stage();
            stage.setScene(new Scene(root, 450, 370)); // Set preferred width and height
            stage.setTitle("Extend Borrow");
            stage.centerOnScreen();
            stage.show();

            // Close current window (optional)
            ((Stage) BorrowTable.getScene().getWindow()).close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the event when the search button is clicked.
     * Filters the borrow records based on the borrow ID entered in the text field.
     * 
     * @param event The event triggered by the search button click.
     */
    @FXML
    void getSearchBtn(ActionEvent event) {
        // Get the search input from the TextField
        String searchInput = txtBorrowId.getText().trim();

        if (searchInput.isEmpty()) {
            // If the search field is empty, show all data
            BorrowTable.setItems(BorrowData);
            return;
        }

        try {
            int borrowId = Integer.parseInt(searchInput); // Parse the input as an integer

            // Filter the ObservableList
            ObservableList<Borrow> filteredData = BorrowData.filtered(borrow -> borrow.getId() == borrowId);

            // Update the TableView with the filtered data
            BorrowTable.setItems(filteredData);

        } catch (NumberFormatException e) {
            System.out.println("Invalid Borrow ID entered: " + searchInput);
        }
    }

    /**
     * Handles the event when the back button is clicked.
     * Navigates back to the previous page based on the current page context.
     * 
     * @param event The event triggered by the back button click.
     */
    @FXML
    void getBackBtn(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader();
            Parent previousScreen = null;

            if (page.equals("Librarian Home Page")) {
                loader.setLocation(getClass().getResource("/librarianGui/ClientLibHomePage.fxml"));
                previousScreen = loader.load();
                ClientLibHomeController controller = loader.getController();
            }
            else if (page.equals("Subscriber Personal Card")) {
                loader.setLocation(getClass().getResource("/subscriberGui/ClientSubPersonalCard.fxml"));
                previousScreen = loader.load();
                ClientSubPersonalCardController controller = loader.getController();
            }
            else if (page.equals("Librarian Personal Card")) {
                loader.setLocation(getClass().getResource("/librarianGui/ClientLibPersonalSubCard.fxml"));
                previousScreen = loader.load();
                ClientLibPersonalSubCardController controller = loader.getController();
            }
            else if (page.equals("Librarian Display Subscribers")) {
                loader.setLocation(getClass().getResource("/librarianGui/ClientLibDisplaySub.fxml"));
                previousScreen = loader.load();
                ClientLibDisplaySubController controller = loader.getController();
            }

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(previousScreen);
            stage.setScene(scene);
            stage.setTitle("ALRIGHTTT");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the response from the server after a request for borrow records.
     * Updates the borrow table with the list of borrows received from the server.
     * 
     * @param response The response received from the server, which is expected to be a list of Borrow objects.
     */
    public void inputResponse(Object response) {
        this.serverMessage = response.toString();
        System.out.println("Response Received: " + response);

        if (response instanceof List) {
            Platform.runLater(() -> {
                BorrowData.clear(); // Clear existing data from the table

                try {
                    @SuppressWarnings("unchecked")
                    List<Borrow> borrows = (List<Borrow>) response;
                    for (Borrow b : borrows) 
                        BorrowData.add(b);

                } catch (ClassCastException e) {
                    System.out.println("Error casting response to List<UserWithSubscriber>");
                    e.printStackTrace();
                }
            });
        } else {
            System.out.println("Invalid response type. Expected a List<UserWithSubscriber>.");
        }
    }
}
