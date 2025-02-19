package commonGui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;

import Shared_classes.ClientServerContact;
import Shared_classes.Command;
import Shared_classes.Message;
import client.ClientUI;

/**
 * Controller class for displaying and managing messages in the client application.
 * 
 * @author Your Name
 * @version 1.0
 */
public class ClientShowMessagesController {

	/**
	 * Button for navigating back to the previous screen.
	 */
	@FXML
	private Button btnBack;

	/**
	 * TableView displaying a list of messages.
	 */
	@FXML
	private TableView<Message> tableMessages;

	/**
	 * Table column displaying the type of the message.
	 */
	@FXML
	private TableColumn<Message, String> colMessageType;

	/**
	 * Table column displaying the date the message was sent.
	 */
	@FXML
	private TableColumn<Message, String> colDate;

	/**
	 * The ID of the librarian currently logged in.
	 */
	private int librarianId;

	/**
	 * A variable representing the current page for navigation purposes.
	 */
	private String page;


    /**
     * Sets the librarian's ID and fetches messages from the server.
     * 
     * @param id The librarian's ID.
     */
    public void setLibrarianId(int id) {
        this.librarianId = id;

        // Fetch messages from the server
        ClientUI.LibClient.requestFromServer(new ClientServerContact(id, Command.GetMessages));
    }

    /**
     * Displays the list of messages in the table.
     * 
     * @param messages The list of messages to be displayed.
     */
    public void displayMessages(List<Message> messages) {
        colMessageType.setCellValueFactory(new PropertyValueFactory<>("msgType"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("msgDate"));

        tableMessages.getItems().addAll(messages);
    }

    /**
     * Initializes the table and sets up row styling for unread messages.
     */
    @FXML
    private void initialize() {
        colMessageType.setCellValueFactory(new PropertyValueFactory<>("msgType"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("msgDate"));

        tableMessages.setRowFactory(tv -> new TableRow<Message>() {
        	@Override
        	protected void updateItem(Message item, boolean empty) {
        	    super.updateItem(item, empty);

        	    // Clear the style if the item is empty
        	    if (item == null || empty) {
        	        setStyle("");
        	    } else {
        	        // Style rows based on the "read" status
        	        if (item.isRead()) {
        	            setStyle(""); // Default style for read messages
        	        } else {
        	            setStyle("-fx-background-color: lightgreen;"); // Highlight unread messages
        	        }
        	    }
        	}

        });

        tableMessages.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // Double-click event
                Message selectedMessage = tableMessages.getSelectionModel().getSelectedItem();
                if (selectedMessage != null) {
                    if (!selectedMessage.isRead()) {
                        updateMessageAsRead(selectedMessage);
                    }
                    showMessageContent(selectedMessage);
                }
            }
        });
    }

    /**
     * Marks the selected message as read and refreshes the table.
     * 
     * @param message The message to be marked as read.
     */
    private void updateMessageAsRead(Message message) {
        try {
            ClientServerContact request = new ClientServerContact(message, Command.MarkMessageAsRead);
            ClientUI.LibClient.requestFromServer(request);

            message.setRead(true);
            tableMessages.refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Displays the content of the selected message in a modal dialog.
     * 
     * @param message The message whose content is to be displayed.
     */
    private void showMessageContent(Message message) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/commonGui/MessageContent.fxml"));
            Parent root = loader.load();

            MessageContentController controller = loader.getController();
            controller.setMessageContent(message.getMsg());

            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setTitle("Message Content");
            dialogStage.setScene(new Scene(root));
            dialogStage.centerOnScreen();

            dialogStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the back button click event. Navigates back to the appropriate page.
     * 
     * @param event The action event triggered by clicking the back button.
     */
    @FXML
    void getBackBtn(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader();
            String fxmlPath = page.equals("SubHome") 
                    ? "/subscriberGui/ClientSubHomePage.fxml" 
                    : "/librarianGui/ClientLibHomePage.fxml";

            loader.setLocation(getClass().getResource(fxmlPath));
            Parent targetScreen = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(targetScreen));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    

    /**
     * Sets the page where the back button should navigate to.
     * 
     * @param page The page to navigate back to.
     */
    public void setPage(String page) {
        this.page = page;
    }
}
