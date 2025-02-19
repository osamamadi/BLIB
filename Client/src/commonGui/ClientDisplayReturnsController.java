package commonGui;

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
 * Controller class for displaying and managing returns in the client application.
 * 
 * @author Your Name
 * @version 1.0
 */
public class ClientDisplayReturnsController {

	/**
	 * Button for navigating back to the previous screen.
	 */
	@FXML
	private Button btnBack;

	/**
	 * Button for performing search operations.
	 */
	@FXML
	private Button btnSearch;

	/**
	 * Button for displaying all return records.
	 */
	@FXML
	private Button btnShowAll;

	/**
	 * Button for marking a book as read.
	 */
	@FXML
	private Button Readbtn;

	/**
	 * TableView displaying a list of return records.
	 */
	@FXML
	private TableView<Returns> ReturnsTable;

	/**
	 * Table column displaying the borrow ID associated with the return record.
	 */
	@FXML
	private TableColumn<Returns, Integer> BorrowIdColumn;

	/**
	 * Table column displaying the date the book was borrowed.
	 */
	@FXML
	private TableColumn<Returns, Date> BorrowDateColumn;

	/**
	 * Table column displaying the due date for returning the book.
	 */
	@FXML
	private TableColumn<Returns, Date> DueDateColumn;

	/**
	 * Table column displaying the actual date the book was returned.
	 */
	@FXML
	private TableColumn<Returns, Date> ActualReturnDateColumn;

	/**
	 * Table column displaying whether the book was returned late (1 for late, 0 for on-time).
	 */
	@FXML
	private TableColumn<Returns, Integer> IsLate;

	/**
	 * TextField for entering or displaying the borrow record ID for returns.
	 */
	@FXML
	private TextField txtBorrowId;

	/**
	 * Static variable representing the current page for navigation purposes.
	 */
	private static String page;

	/**
	 * Static variable holding the ID of the current subscriber.
	 */
	private static String subscriber_id;

	/**
	 * Static variable representing the librarian user currently logged in.
	 */
	private static User librarian;

    /**
     * Sets the librarian user.
     * 
     * @param lib The librarian user to be set.
     */
    public static  void setLib(User lib) {
    	librarian=lib;
    }

    /**
     * Sets the page value.
     * 
     * @param p The page string to be set.
     */
    public static void setPage(String p) {
   	 	page=p;
    }

    /**
     * Sets the subscriber ID.
     * 
     * @param id The subscriber ID to be set.
     */
    public static void setSubscriberId(String id) {
    	subscriber_id = id;
    }

    private ObservableList<Returns> ReturnsData = FXCollections.observableArrayList();
    private String serverMessage;

    /**
     * Initializes the controller by requesting data from the server for the given subscriber ID
     * and setting up the table columns.
     */
    @FXML
    public void initialize() {
    	 if (subscriber_id == null || subscriber_id.isEmpty()) {
    	        System.err.println("Error: Subscriber ID is null or empty.");
    	        return;
    	    }
    	ClientServerContact msg = new ClientServerContact(subscriber_id, Command.DisplayReturns);
        ClientUI.LibClient.setController(this);
        ClientUI.LibClient.requestFromServer(msg);
        
        BorrowIdColumn.setCellValueFactory(new PropertyValueFactory<>("borrowId"));
        BorrowDateColumn.setCellValueFactory(new PropertyValueFactory<>("borrowDate"));
        DueDateColumn.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        ActualReturnDateColumn.setCellValueFactory(new PropertyValueFactory<>("actualReturnDate"));
        IsLate.setCellValueFactory(new PropertyValueFactory<>("isLate"));
    	ReturnsTable.setItems(ReturnsData);
    }

    /**
     * Handles the search button click. Filters the returns based on the borrow ID entered in the text field.
     * 
     * @param event The action event triggered by clicking the search button.
     */
    @FXML
    void getSearchBtn(ActionEvent event) {
        // Get the search input from the TextField
        String searchInput = txtBorrowId.getText().trim();

        if (searchInput.isEmpty()) {
            // If the search field is empty, show all data
        	ReturnsTable.setItems(ReturnsData);
            return;
        }

        try {
            int borrowId = Integer.parseInt(searchInput); // Parse the input as an integer

            // Filter the ObservableList
            ObservableList<Returns> filteredData = ReturnsData.filtered(borrow -> borrow.getBorrowId() == borrowId);

            // Update the TableView with the filtered data
            ReturnsTable.setItems(filteredData);

        } catch (NumberFormatException e) {
            System.out.println("Invalid Borrow ID entered: " + searchInput);
        }
    }

    /**
     * Handles the back button click. Navigates back to the previous screen based on the current page value.
     * 
     * @param event The action event triggered by clicking the back button.
     */
    @FXML
    void getBackBtn(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader();
            Parent previousScreen = null;

            // Handle null or empty page case
            if (page == null || page.isEmpty()) {
                System.out.println("Error: Page is not set.");
                return;
            }

            switch (page) {
                case "Librarian Home Page":
                    loader.setLocation(getClass().getResource("/librarianGui/ClientLibHomePage.fxml"));
                    previousScreen = loader.load();
                    ClientLibHomeController controller = loader.getController();
                    break;

                case "Subscriber Personal Card":
                    loader.setLocation(getClass().getResource("/subscriberGui/ClientSubPersonalCard.fxml"));
                    previousScreen = loader.load();
                    ClientSubPersonalCardController controller2 = loader.getController();
                    break;

                case "Librarian Personal Card":
                    loader.setLocation(getClass().getResource("/librarianGui/ClientLibPersonalSubCard.fxml"));
                    previousScreen = loader.load();
                    ClientLibPersonalSubCardController controller3 = loader.getController();
                    break;

                case "Librarian Display Subscribers":
                    loader.setLocation(getClass().getResource("/librarianGui/ClientLibDisplaySub.fxml"));
                    previousScreen = loader.load();
                    ClientLibDisplaySubController controller4 = loader.getController();
                    break;

                default:
                    System.out.println("Error: Unknown page type.");
                    return;
            }

            // Switch back to the previous scene
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(previousScreen);
            stage.setScene(scene);
            stage.setTitle("Back to Previous Page");
            stage.centerOnScreen();

            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Processes the response from the server and updates the Returns table data.
     * 
     * @param response The response from the server, which can be a list of returns.
     */
    public void inputResponse(Object response) {
        this.serverMessage = response.toString();
        System.out.println("repo:"+ response);
        if (response instanceof List) {
            Platform.runLater(() -> {
            	ReturnsData.clear(); // Clear existing data from the table

                try {
                    @SuppressWarnings("unchecked")
                    List<Returns> Returns = (List<Returns>) response;
                    for (Returns b : Returns) 
                    	ReturnsData.add(b);
                    
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
