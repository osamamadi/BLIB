package commonGui;

import java.sql.Date;
import java.util.ArrayList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import java.util.List;
import java.util.Map;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import librarianGui.ClientLibHomeController;
import librarianGui.ClientLibPersonalSubCardController;
import subscriberGui.ClientSubHomeController;
import Shared_classes.Book;
import Shared_classes.ClientServerContact;
import Shared_classes.Command;
import Shared_classes.UserWithSubscriber;
/**
 * ClientDisplayBooksController
 * Version: 1.0
 * 
 * This class is responsible for managing the book display interface in the client application.
 * It handles book search functionality, displays book details, and manages navigation between screens.
 * 
 * Features include:
 *  Displaying a list of books in a table.
 *  Filtering books based on user defined criteria (name, topic, description).
 *  Fetching detailed book data from the server.
 *  Navigating back to the previous page or opening a detailed book view.
 *   Author: [Your Name]
 *   version: [Current Date]
 */
public class ClientDisplayBooksController {

	 /**
     * Button to navigate back to the previous page.
     */
    @FXML
    private Button btnBack;

    /**
     * Button to initiate a search action.
     */
    @FXML
    private Button btnSearch;

    /**
     * Button to send or view messages.
     */
    @FXML
    private Button btnMessage;

    /**
     * TextField for entering a name, typically used for search or input purposes.
     */
    @FXML
    private TextField txtName;

    /**
     * TextField for entering search criteria or queries.
     */
    @FXML
    private TextField txtsearch;

    /**
     * The username of the current user, used for identification or personalization.
     */
    private String username;

    /**
     * The name of the current page or section, used for navigation or context tracking.
     */
    private String page;

    /**
     * Sets the page context that the user is navigating from.
     * @param page The name of the previous page.
     */
    public void setPage(String page) {
        this.page = page;
    }

    @FXML
    private TableView<Book> bookTable;

    /**
     * Table column representing the ID of the book.
     */
    @FXML
    private TableColumn<Book, Integer> book_idColumn;

    /**
     * Table column representing the name of the book.
     */
    @FXML
    private TableColumn<Book, String> book_nameColumn;

    /**
     * Table column representing the topic or genre of the book.
     */
    @FXML
    private TableColumn<Book, String> book_topicColumn;

    /**
     * Table column representing the description of the book.
     */
    @FXML
    private TableColumn<Book, String> book_descriptionColumn;

    /**
     * Table column representing the number of available copies of the book.
     */
    @FXML
    private TableColumn<Book, Integer> book_copiesColumn;

    /**
     * Dropdown for selecting book topics to filter the book list.
     */
    @FXML
    private ComboBox<String> topicDropdown;

    /**
     * Observable list that holds the data for the books displayed in the TableView.
     */
    private ObservableList<Book> BookData = FXCollections.observableArrayList();


    /**
     * Initializes the table with the appropriate columns and sets up the topic dropdown.
     * Requests book data from the server when the application starts.
     */
    @FXML
    public void initialize() {
        System.out.println("Initialize of DisplayBook");
        
        // Setting the value factories for each column
        book_idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        book_nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        book_topicColumn.setCellValueFactory(new PropertyValueFactory<>("topic"));
        book_descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        book_copiesColumn.setCellValueFactory(new PropertyValueFactory<>("copies"));

        bookTable.setItems(BookData);
        
        // Adding search options to the dropdown
        topicDropdown.getItems().addAll("name",  "topic","description");
        topicDropdown.setValue("name");

        // Requesting book data from the server
        if (ClientUI.LibClient != null) {
            ClientServerContact msg = new ClientServerContact(null, Command.DisplayBooks);
            ClientUI.LibClient.setController(this);
            ClientUI.LibClient.requestFromServer(msg);
        }

        // Double-clicking on a book row opens its detailed view
        bookTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // Detect double-click
                Book selectedBook = bookTable.getSelectionModel().getSelectedItem();
                if (selectedBook != null) {
                    // Send a request to the server to fetch detailed data for the selected book
                    ClientServerContact msg = new ClientServerContact(selectedBook.getId(), Command.BookDetails);
                    ClientUI.LibClient.setController(this);
                    ClientUI.LibClient.requestFromServer(msg);
                }
            }
        });

        System.out.println("End of Initialize of DisplayBook");
    }

    /**
     * Displays an alert to the user.
     * @param type The type of the alert (e.g., ERROR, INFORMATION).
     * @param title The title of the alert.
     * @param content The content of the alert.
     */
    private void showAlert(AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Handles the search button click event.
     * Filters the book list based on the selected criteria (name, topic, or description).
     * @param event The event triggered by the button click.
     */
    @FXML
    void getSearchBtn(ActionEvent event) {
        // Ensure a criteria is selected in the dropdown and the search field is not empty
        String selectedCriteria = topicDropdown.getValue();
        String searchText = txtsearch.getText().trim().toLowerCase();

        if (selectedCriteria == null || searchText.isEmpty()) {
            bookTable.setItems(BookData); // Reset the table if no criteria or text
            return;
        }

        ObservableList<Book> filteredBooks = FXCollections.observableArrayList();

        // Filter books based on the selected criteria
        for (Book book : BookData) {
            switch (selectedCriteria.toLowerCase()) {
                case "name":
                    if (book.getName().toLowerCase().contains(searchText)) {
                        filteredBooks.add(book);
                    }
                    break;
                case "topic":
                    if (book.getTopic().toLowerCase().contains(searchText)) {
                        filteredBooks.add(book);
                    }
                    break;
                case "description":
                    if (book.getDescription().toLowerCase().contains(searchText)) {
                        filteredBooks.add(book);
                    }
                    break;
            }
        }

        // Update the table with the filtered data
        bookTable.setItems(filteredBooks);
    }

    /**
     * Sets the username for the current user.
     * @param username The username of the user.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Opens a detailed view of the selected book in a new window.
     * @param response The book details fetched from the server.
     */
    private void openSubscriberDetails(Object response) {
        if (response == null) {
            return;
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> bookDetails = (Map<String, Object>) response;

        // Extract details from the response
        String bookName = (String) bookDetails.get("book_name");
        String location = (String) bookDetails.get("location_on_shelf"); // Could be null
        String availability = (String) bookDetails.get("availability");
        Date closestReturnDate = (Date) bookDetails.get("closest_return_date"); // Could be null

        // Create a new stage
        Stage stage = new Stage();
        stage.setTitle("Book Details");

        // Create a root AnchorPane
        AnchorPane root = new AnchorPane();
        root.setStyle("-fx-background-color: #f9f9f9; -fx-padding: 20; -fx-border-color: #2b4356; -fx-border-width: 2; -fx-border-radius: 10;");

        // Header label
        Label lblHeader = new Label("Book Details");
        lblHeader.setFont(new Font("Arial Bold", 24));
        lblHeader.setStyle("-fx-text-fill: #2b4356;");
        lblHeader.setLayoutX(20);
        lblHeader.setLayoutY(20);

        // Book Name
        Label lblName = new Label("Book Name: " + bookName);
        lblName.setFont(new Font("Arial", 16));
        lblName.setStyle("-fx-text-fill: #34495e;");
        lblName.setLayoutX(20);
        lblName.setLayoutY(70);

        // Availability
        Label lblAvailability = new Label("Availability: " + availability);
        lblAvailability.setFont(new Font("Arial", 16));
        lblAvailability.setStyle("-fx-text-fill: #34495e;");
        lblAvailability.setLayoutX(20);
        lblAvailability.setLayoutY(110);

        // Location (conditionally shown)
        Label lblLocation = null;
        if (location != null && "Available".equals(availability)) {
            lblLocation = new Label("Location on Shelf: " + location);
            lblLocation.setFont(new Font("Arial", 16));
            lblLocation.setStyle("-fx-text-fill: #34495e;");
            lblLocation.setLayoutX(20);
            lblLocation.setLayoutY(150);
            root.getChildren().add(lblLocation);
        }

        // Closest return date (conditionally shown)
        Label lblReturnDate = null;
        if ("Unavailable - Borrowed".equals(availability)) {
            lblReturnDate = new Label("Closest Return Date: " +
                (closestReturnDate != null ? closestReturnDate.toString() : "N/A"));
            lblReturnDate.setFont(new Font("Arial", 16));
            lblReturnDate.setStyle("-fx-text-fill: #34495e;");
            lblReturnDate.setLayoutX(20);
            lblReturnDate.setLayoutY(lblLocation != null ? 190 : 150);
            root.getChildren().add(lblReturnDate);
        } else if ("Unavailable - All copies lost".equals(availability) || "Unavailable".equals(availability)) {
            lblReturnDate = new Label("No return date available.");
            lblReturnDate.setFont(new Font("Arial", 16));
            lblReturnDate.setStyle("-fx-text-fill: #e74c3c;"); // Red text for emphasis
            lblReturnDate.setLayoutX(20);
            lblReturnDate.setLayoutY(lblLocation != null ? 190 : 150);
            root.getChildren().add(lblReturnDate);
        }

        // Close button
        Button closeButton = new Button("Close");
        closeButton.setLayoutX(120);
        closeButton.setLayoutY(lblLocation != null ? 230 : (lblReturnDate != null ? 230 : 190));
        closeButton.setPrefWidth(100);
        closeButton.setStyle("-fx-background-color: #2b4356; -fx-text-fill: white; -fx-font-size: 14px; -fx-border-radius: 5px;");
        closeButton.setOnAction(event -> stage.close());

        // Add all components to the root
        root.getChildren().addAll(lblHeader, lblName, lblAvailability, closeButton);

        // Create a scene and apply it to the stage
        Scene scene = new Scene(root, 400, lblLocation != null ? 300 : (lblReturnDate != null ? 280 : 240));
        stage.setScene(scene);
        stage.show();
    }


    /**
     * Handles the response received from the server.
     * This method processes different types of responses such as:
     *  List of books: Updates the book table with the received list of books.
     *  Map: Displays detailed information of a selected book.
     *  String: Logs a received message.
     * 
     * @param response The response received from the server. It can be a List of Books, Map String, Object , or String.
     */
    public void inputResponse(Object response) {
        Platform.runLater(() -> {
            if (response instanceof List) {
                try {
                    @SuppressWarnings("unchecked")
                    List<Book> booksList = (List<Book>) response;

                    // Populate the BookData observable list with the received books
                    BookData.clear();
                    BookData.addAll(booksList);
                } catch (ClassCastException e) {
                    System.out.println("Error casting response to List<Book>");
                    e.printStackTrace();
                }
            } else if (response instanceof Map) {
                // Display the book details in the popup
                openSubscriberDetails(response);
            } else if (response instanceof String) {
                System.out.println("Received message: " + response);
            } else {
                System.out.println("Invalid response type. Expected a List<Book>, Map<String, Object>, or a String.");
            }
        });
    }

    /**
     * Handles the "Message" button click event.
     * This method is currently a placeholder and should be implemented as needed.
     * 
     * @param event The event triggered by the "Message" button click.
     */
    @FXML
    void getMessageBtn(ActionEvent event) {
        // Placeholder for handling the "Message" button click.
    }

    /**
     * Handles the "Back" button click event.
     * This method navigates back to the previous page based on the page context.
     * 
     * @param event The event triggered by the "Back" button click.
     */
    @FXML
    void getBackBtn(ActionEvent event) {
        try {
            System.out.println("Clicking on back button in the display BOOKS controller 1");

            FXMLLoader loader = new FXMLLoader();
            Parent previousScreen = null;

            if ("HomeSubPage".equals(page)) {
                loader.setLocation(getClass().getResource("/subscriberGui/ClientSubHomePage.fxml"));
                previousScreen = loader.load();
                ClientSubHomeController controller = loader.getController();
                //controller.setUsername(username);
            }
            else if (page.equals("Librarian Home Page")) {
                loader.setLocation(getClass().getResource("/librarianGui/ClientLibHomePage.fxml"));
                previousScreen = loader.load();
                ClientLibHomeController controller = loader.getController();
            }
            else if (page.equals("CommonHome")) {
                loader.setLocation(getClass().getResource("/commonGui/ClientCommonHome.fxml"));
                previousScreen = loader.load();
                ClientCommonHomeController controller = loader.getController();
            }

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(previousScreen);
            stage.setScene(scene);
            stage.setTitle("Client Common Home Page");
            stage.centerOnScreen();
            stage.show();

            System.out.println("Clicking on back button in the display BOOKS controller 2");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}