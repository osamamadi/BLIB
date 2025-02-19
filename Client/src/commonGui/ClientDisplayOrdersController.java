package commonGui;

import java.sql.Date;
import java.util.List;

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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import librarianGui.ClientLibDisplaySubController;
import librarianGui.ClientLibHomeController;
import librarianGui.ClientLibPersonalSubCardController;
import subscriberGui.ClientSubPersonalCardController;
import Shared_classes.ClientServerContact;
import Shared_classes.Command;
import Shared_classes.Order;

/**
 * Controller for managing the display of orders in the client application.
 * Allows users to view, search, and create orders.
 * 
 * Author: [Your Name]
 * Version: 1.0
 */
public class ClientDisplayOrdersController {

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
	 * Button for creating a new order.
	 */
	@FXML
	private Button btnCreateOrder;

	/**
	 * Button for canceling an existing order.
	 */
	@FXML
	private Button btnCancelOrder;

	/**
	 * Label for displaying messages received from the server.
	 */
	@FXML
	private Label ServerMsg;

	/**
	 * TableView displaying a list of orders.
	 */
	@FXML
	private TableView<Order> ordersTable;

	/**
	 * Table column displaying the ID of the order.
	 */
	@FXML
	private TableColumn<Order, Integer> orderIdColumn;

	/**
	 * Table column displaying the ID of the user who created the order.
	 */
	@FXML
	private TableColumn<Order, Integer> userIdColumn;

	/**
	 * Table column displaying the ID of the book copy associated with the order.
	 */
	@FXML
	private TableColumn<Order, Integer> bookCopyIdColumn;

	/**
	 * Table column displaying the date the order was created.
	 */
	@FXML
	private TableColumn<Order, Date> orderDateColumn;

	/**
	 * Table column displaying the status of the order (e.g., pending, completed, canceled).
	 */
	@FXML
	private TableColumn<Order, String> orderStatusColumn;

	/**
	 * TextField for entering or displaying the order ID.
	 */
	@FXML
	private TextField txtOrderId;

	/**
	 * Observable list holding data for the orders displayed in the TableView.
	 */
	private ObservableList<Order> orderData = FXCollections.observableArrayList();

	/**
	 * Static variable holding the ID of the current subscriber.
	 */
	private static String subscriber_id;

	/**
	 * Static variable representing the librarian user currently logged in.
	 */
	private static String librarian;

	/**
	 * Static variable representing the current page for navigation purposes.
	 */
	private static String page;

	/**
	 * Name of the book associated with the order.
	 */
	private String bookName;


    /**
     * Sets the subscriber ID for the current session.
     * 
     * @param id The subscriber ID.
     */
    public static void setSubscriberId(String id) {
    	subscriber_id = id;
    }
    
    /**
     * Sets the librarian identifier for the current session.
     * 
     * @param lib The librarian identifier.
     */
    public static void setLibrarian(String lib) {
    	librarian = lib;
    }
    
    /**
     * Sets the current page of the application.
     * 
     * @param p The current page.
     */
    public static void setPage(String p) {
   	 	page = p;
    }

    /**
     * Initializes the controller and sets up the UI components.
     */
    @FXML
    public void initialize() {
        // Set up table columns
        orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
        bookCopyIdColumn.setCellValueFactory(new PropertyValueFactory<>("bookCopyId"));
        orderDateColumn.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        orderStatusColumn.setCellValueFactory(new PropertyValueFactory<>("orderStatus"));

        // Bind data to the table
        ordersTable.setItems(orderData);

        // Conditionally show the "Create Order" button based on the librarian status
        if (librarian == null || librarian.isEmpty()) {
            btnCreateOrder.setVisible(true); 
        } else {
            btnCreateOrder.setVisible(false);  
        }

        // Request order data from the server
        fetchDataFromServer();
    }

    /**
     * Fetches order data from the server.
     */
    private void fetchDataFromServer() {
    	System.out.println("Syso in ORder subId: "+subscriber_id);
        ClientServerContact msg = new ClientServerContact(subscriber_id, Command.DisplayOrders);
        ClientUI.LibClient.setController(this);
        ClientUI.LibClient.requestFromServer(msg);
    }

    /**
     * Handles the action for the search button to filter orders by ID.
     * 
     * @param event The action event for the search button.
     */
    @FXML
    void getSearchBtn(ActionEvent event) {
        String searchOrderId = txtOrderId.getText().trim();
        if (searchOrderId.isEmpty()) {
            ordersTable.setItems(orderData); // Reset table if search is empty
            return;
        }

        // Filter order data based on Order ID
        ObservableList<Order> filteredData = FXCollections.observableArrayList();
        for (Order order : orderData) {
            if (String.valueOf(order.getId()).contains(searchOrderId)) {
                filteredData.add(order);
            }
        }

        ordersTable.setItems(filteredData); // Update table with filtered data
    }

    /**
     * Handles the back button action to navigate to the previous page.
     * 
     * @param event The action event for the back button.
     */
    @FXML
    void getBackBtn(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader();
            Parent previousScreen=null;
            
            if (page.equals("Librarian Home Page")) {
            	loader.setLocation(getClass().getResource("/librarianGui/ClientLibHomePage.fxml"));
                previousScreen = loader.load();
                ClientLibHomeController controller = loader.getController();
            }
            else if (page.equals("Subscriber Personal Card")){
                loader.setLocation(getClass().getResource("/subscriberGui/ClientSubPersonalCard.fxml"));
                previousScreen = loader.load();
                ClientSubPersonalCardController controller = loader.getController();
            }
            else if (page.equals("Librarian Personal Card")){
                loader.setLocation(getClass().getResource("/librarianGui/ClientLibPersonalSubCard.fxml"));
                previousScreen = loader.load();
                ClientLibPersonalSubCardController controller = loader.getController();
            }
            else if (page.equals("Librarian Display Subscribers")){
                loader.setLocation(getClass().getResource("/librarianGui/ClientLibDisplaySub.fxml"));
                previousScreen = loader.load();
                ClientLibDisplaySubController controller = loader.getController();
            }
            setLibrarian("");
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
     * Opens a popup window to enter order details and create a new order.
     */
    void getOrderDetails() {
        // Create a new Stage for the popup
        Stage stage = new Stage();
        stage.setTitle("Order Details");

        // Create a layout (AnchorPane)
        AnchorPane root = new AnchorPane();
        root.setStyle("-fx-background-color: #f4f4f4; -fx-padding: 20px; -fx-border-color: #2b4356; -fx-border-radius: 10px;");

        // Create a label for the instruction
        Label label = new Label("Enter Book's Name:");
        label.setFont(new Font("Footlight MT Light", 18));
        label.setLayoutX(20);
        label.setLayoutY(20);

        // Create a text field for entering the book name
        TextField bookNameField = new TextField();
        bookNameField.setPromptText("Book name");
        bookNameField.setLayoutX(20);
        bookNameField.setLayoutY(60);
        bookNameField.setPrefWidth(260);

        // Create a label for error messages
        Label errorMsg = new Label();
        errorMsg.setFont(new Font("Arial", 12));
        errorMsg.setTextFill(Color.RED);
        errorMsg.setLayoutX(20);
        errorMsg.setLayoutY(100);
        errorMsg.setVisible(false); // Initially hidden

        // Create a submit button
        Button submitButton = new Button("Create Order");
        submitButton.setId("btnSubmitOrder");
        submitButton.setLayoutX(90); // Adjust as per your layout
        submitButton.setLayoutY(130); // Adjust as per your layout
        submitButton.setPrefHeight(39.0);
        submitButton.setPrefWidth(139.0);
        submitButton.setStyle("-fx-font-size: 14px; -fx-background-color: #2b4356; -fx-text-fill: white; -fx-cursor: hand; -fx-border-radius: 50px;");
        submitButton.setOpacity(0.72);

        // Add action for the submit button
        submitButton.setOnAction(e -> {
            String bookName = bookNameField.getText().trim();

            if (bookName.isEmpty()) {
                errorMsg.setText("Book name cannot be empty.");
                errorMsg.setVisible(true);
            } else {
                // Send the book name to the server to validate and create an order
                this.bookName = bookName;
                Order newOrder = new Order();
                newOrder.setUserId(Integer.parseInt(subscriber_id));
                newOrder.setBookName(this.bookName);

                ClientServerContact csc = new ClientServerContact(newOrder, Command.CreateOrder);
                ClientUI.LibClient.setController(this);
                ClientUI.LibClient.requestFromServer(csc);

                stage.close(); // Close the popup after submission
            }
        });

        // Add components to the layout
        root.getChildren().addAll(label, bookNameField, submitButton, errorMsg);

        // Set the scene and show the stage
        Scene scene = new Scene(root, 300, 200);
        stage.setScene(scene);
        stage.show();
    }



    /**
     * Handles the action of the "Create Order" button. Opens the order details popup.
     * 
     * @param event The action event for the "Create Order" button.
     */
    @FXML
    private void createOrder(ActionEvent event) {
        getOrderDetails();
    }

    /**
     * Displays an alert dialog with the specified type, title, and message.
     * 
     * @param type The type of the alert (e.g., INFORMATION, ERROR).
     * @param title The title of the alert.
     * @param message The content message of the alert.
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Processes the response received from the server. Updates the UI based on the response type.
     * 
     * @param response The response from the server, either a list of orders or a string message.
     */
    public void inputResponse(Object response) {
        Platform.runLater(() -> {
            if (response instanceof List) { // Handle order data
                try {
                    @SuppressWarnings("unchecked")
                    List<Order> orders = (List<Order>) response;
                    orderData.clear();
                    orderData.addAll(orders); // Update the table data
                } catch (ClassCastException e) {
                    System.out.println("Error casting response to List<Order>");
                    e.printStackTrace();
                }
            } else if (response instanceof String) { // Handle string responses
                String serverMessage = (String) response;

                if (serverMessage.contains("successfully")) { // Successful response
                    ServerMsg.setTextFill(Color.GREEN);
                    ServerMsg.setText(serverMessage);

                    // Fetch updated orders from the server
                    fetchDataFromServer();
                } else { // Error response
                    ServerMsg.setTextFill(Color.RED);
                    ServerMsg.setText(serverMessage);
                }
            } else {
                System.out.println("Invalid response type.");
            }
        });
    }
}