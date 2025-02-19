package commonGui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Controller class for displaying the content of a message in a dialog.
 * 
 * @author Your Name
 * @version 1.0
 */
public class MessageContentController {

	/**
	 * Text element for displaying or entering a message.
	 */
	@FXML
	private Text messageText;

    /**
     * Sets the content of the message to be displayed in the dialog.
     * 
     * @param content The message content to be displayed.
     */
    public void setMessageContent(String content) {
        messageText.setText(content);
    }

    /**
     * Closes the dialog when the close button is clicked.
     * 
     * @param event The action event triggered by clicking the close button.
     */
    @FXML
    void closeDialog(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
