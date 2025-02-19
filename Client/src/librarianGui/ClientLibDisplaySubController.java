package librarianGui;

import java.util.ArrayList;
import java.util.List;
import client.BraudeLibClient;
import client.ClientUI;
import commonGui.ClientDisplayBooksController;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.control.TextField;
import Shared_classes.Book;
import Shared_classes.ClientServerContact;
import Shared_classes.Command;
import Shared_classes.Subscriber;
import Shared_classes.User;
import Shared_classes.UserWithSubscriber;

/**
 * Controller class responsible for displaying and searching subscriber information.
 * This class handles the UI interactions for displaying a list of subscribers and performing search functionality.
 * 
 * @author Majd Awad
 * @version 1.1dz
 */
public class ClientLibDisplaySubController {

    /**
     * Button to navigate back to the previous page.
     */
    @FXML
    private Button btnBack;

    /**
     * Button to trigger the search functionality.
     */
    @FXML
    private Button btnSearch;

    /**
     * TableView that displays the list of subscribers.
     */
    @FXML
    private TableView<UserWithSubscriber> SubscriberTable;

    /**
     * TableColumn for displaying the subscriber ID.
     */
    @FXML
    private TableColumn<UserWithSubscriber, Integer> idColumn;

    /**
     * TableColumn for displaying the subscriber's username.
     */
    @FXML
    private TableColumn<UserWithSubscriber, String> usernameColumn;

    /**
     * TableColumn for displaying the subscriber's password.
     */
    @FXML
    private TableColumn<UserWithSubscriber, String> passwordColumn;

    /**
     * TableColumn for displaying the subscriber's first name.
     */
    @FXML
    private TableColumn<UserWithSubscriber, String> firstNameColumn;

    /**
     * TableColumn for displaying the subscriber's last name.
     */
    @FXML
    private TableColumn<UserWithSubscriber, String> lastNameColumn;

    /**
     * TableColumn for displaying the subscriber's phone number.
     */
    @FXML
    private TableColumn<UserWithSubscriber, String> phoneNumberColumn;

    /**
     * TableColumn for displaying the subscriber's email address.
     */
    @FXML
    private TableColumn<UserWithSubscriber, String> emailColumn;

    /**
     * TableColumn for displaying the subscriber's status (active, inactive, etc.).
     */
    @FXML
    private TableColumn<UserWithSubscriber, String> statusColumn;

    /**
     * TableColumn for displaying the number of late book returns by the subscriber.
     */
    @FXML
    private TableColumn<UserWithSubscriber, Integer> lateReturnColumn;

    /**
     * TableColumn for displaying the subscriber's number.
     */
    @FXML
    private TableColumn<UserWithSubscriber, Integer> SubscriberNumber;

    /**
     * TextField used for entering search text.
     */
    @FXML
    private TextField txtsearch;

    /**
     * ComboBox to select the search criteria (e.g., ID, phone, status, etc.).
     */
    @FXML
    private ComboBox<String> topicDropdown;

    /**
     * ObservableList of subscribers to populate the table.
     */
    private ObservableList<UserWithSubscriber> SubscribersData = FXCollections.observableArrayList();

    /**
     * Holds the server response message.
     */
    private String serverMessage;

    /**
     * Static reference to the librarian user.
     */
    private static User librarian;

    /**
Default contructor
     */
    public ClientLibDisplaySubController() {
        // No specific initialization needed for the default constructor
    }

    
    
    /**
     * Sets the librarian for the current session.
     * 
     * @param lib the librarian user.
     */
    public static void setLibrarian(User lib) {
        librarian = lib;
    }

    /**
     * Initializes the scene by setting up the columns of the SubscriberTable, dropdown items,
     * and sending a request to the server to load the subscribers.
     * Also sets up event listeners and other necessary components.
     * 
     */
    @FXML
    public void initialize() {
        // Set up the columns for the SubscriberTable
        SubscriberTable.getStylesheets().add(getClass().getResource("/commonGui/style.css").toExternalForm());
        topicDropdown.getItems().addAll("id",  "phone","status","subscriberNumber");
        topicDropdown.setValue("subscriberNumber");

        // Initialize columns for subscriber table
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        passwordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        phoneNumberColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        lateReturnColumn.setCellValueFactory(new PropertyValueFactory<>("lateReturn"));
        SubscriberNumber.setCellValueFactory(new PropertyValueFactory<>("subscriberNumber"));
        SubscriberTable.setItems(SubscribersData);

        // Send a request to the server to fetch the subscriber list
        List<UserWithSubscriber> serializableList = new ArrayList<>(SubscribersData);
        ClientServerContact msg = new ClientServerContact(serializableList, Command.DisplaySubscribers);
        ClientUI.LibClient.setController(this);
        ClientUI.LibClient.requestFromServer(msg);

        // Add double-click event to table rows
        SubscriberTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // Detect double-click
                UserWithSubscriber selectedSubscriber = SubscriberTable.getSelectionModel().getSelectedItem();
                if (selectedSubscriber != null) {
                    ClientLibPersonalSubCardController.setSubscriber(selectedSubscriber);
                    ClientLibPersonalSubCardController.setLibrarian(librarian);
                    openSubscriberDetails(selectedSubscriber);
                }
            }
        });
    }

    /**
     * Opens a new scene to display detailed subscriber information in a personal subscriber card view.
     * 
     * @param subscriber the selected subscriber whose details will be displayed.
     * @return void
     * @exception Exception if an error occurs while loading the new scene.
     */
    private void openSubscriberDetails(UserWithSubscriber subscriber) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/librarianGui/ClientLibPersonalSubCard.fxml"));
            Parent root = loader.load();

            // Get the controller and pass subscriber data
            ClientLibPersonalSubCardController controller = loader.getController();
            controller.setSubscriberData(subscriber);

            // Open the new scene
            Stage stage = (Stage) SubscriberTable.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Lib Subscriber Personal Card");
            stage.centerOnScreen();
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the search button click event. Filters subscribers based on selected criteria and search text.
     * 
     * @param event the ActionEvent triggered by the user clicking the search button.
     * @return void
     */
    @FXML
    void getSearchBtn(ActionEvent event) {
        String selectedCriteria = topicDropdown.getValue();
        String searchText = txtsearch.getText().trim();

        if (selectedCriteria == null || searchText.isEmpty()) {
            SubscriberTable.setItems(SubscribersData); // Reset the table if no criteria or text
            return;
        }

        // Declare the filtered list
        ObservableList<UserWithSubscriber> filteredSubscribers = FXCollections.observableArrayList();

        // Filter subscribers based on selected criteria
        for (UserWithSubscriber sub : SubscribersData) {
            switch (selectedCriteria.toLowerCase()) {
                case "id":
                    if (String.valueOf(sub.getUser().getId()).contains(searchText)) {
                        filteredSubscribers.add(sub);
                    }
                    break;
                case "phone":
                    if (sub.getSubscriber().getPhone_number() != null &&
                        sub.getSubscriber().getPhone_number().toLowerCase().contains(searchText.toLowerCase())) {
                        filteredSubscribers.add(sub);
                    }
                    break;
                case "status":
                    if (sub.getSubscriber().getStatus() != null &&
                        sub.getSubscriber().getStatus().toLowerCase().contains(searchText.toLowerCase())) {
                        filteredSubscribers.add(sub);
                    }
                    break;
                case "subscribernumber":
                    if (String.valueOf(sub.getSubscriber().getSubscriber_number()).contains(searchText)) {
                        filteredSubscribers.add(sub);
                    }
                    break;
                default:
                    break;
            }
        }

        // Update the table with filtered data
        SubscriberTable.setItems(filteredSubscribers);
    }

    /**
     * Handles the back button click event. Navigates the user back to the home page.
     * 
     * @param event the ActionEvent triggered by the user clicking the back button.
     * @return void
     */
    @FXML
    void getBackBtn(ActionEvent event) {
        navigateToPage(event, "/librarianGui/ClientLibHomePage.fxml", "Librarian Home Page");
    }

    /**
     * Navigates to a specified page.
     * 
     * @param event the ActionEvent that triggered the navigation.
     * @param fxmlPath the path to the FXML file of the page.
     * @param pageTitle the title to be set for the page.
     * @return void
     * @exception Exception if an error occurs while navigating to the page.
     */
    private void navigateToPage(ActionEvent event, String fxmlPath, String pageTitle) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(pageTitle);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Processes the server's response and updates the subscriber list displayed in the table.
     * 
     * @param response the server response containing the list of subscribers.
     */
    public void inputResponse(Object response) {
        this.serverMessage = response.toString();
        if (response instanceof List) {
            Platform.runLater(() -> {
                SubscribersData.clear(); // Clear existing data from the table

                try {
                    @SuppressWarnings("unchecked")
                    List<UserWithSubscriber> subscribers = (List<UserWithSubscriber>) response;

                    for (UserWithSubscriber userWithSubscriber : subscribers) {
                        User user = userWithSubscriber.getUser();
                        Subscriber subscriber = userWithSubscriber.getSubscriber();
                        SubscribersData.add(new UserWithSubscriber(user, subscriber));
                    }
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