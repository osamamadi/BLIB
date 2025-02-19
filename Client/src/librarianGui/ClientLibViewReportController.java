package librarianGui;

import javafx.event.ActionEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import Shared_classes.Report;
import Shared_classes.ReportType;
import client.BraudeLibClient;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * Controller for the View Report page in the Librarian UI.
 * Allows the librarian to choose report parameters (month, year, report type) 
 * and view the corresponding report.
 * 
 * @author Majd Awad
 * @version 1.1dz
 */
public class ClientLibViewReportController implements Initializable {

    /**
     * Button to navigate back to the home page of the librarian interface.
     */
    @FXML
    private Button btnBack;

    /**
     * Button to initiate the viewing of the selected report.
     */
    @FXML
    private Button btnViewReport;

    /**
     * ComboBox for selecting the month for the report.
     */
    @FXML
    private ComboBox<String> cbMonth;

    /**
     * ComboBox for selecting the report type (Subscription Status or Borrowed Duration).
     */
    @FXML
    private ComboBox<String> cbReportType;

    /**
     * ComboBox for selecting the year for the report.
     */
    @FXML
    private ComboBox<String> cbYear;

    /**
     * Label to display messages related to missing fields or error messages.
     */
    @FXML
    private Label libMsg;

    /**
     * Navigates back to the home page when the back button is clicked.
     * 
     * @param event The ActionEvent triggered by the 'Back' button click.
     */
    @FXML
    private void getBackBtn(ActionEvent event) {
        try {
            // Load the FXML for the previous screen
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/librarianGui/ClientLibHomePage.fxml")); // Update the path to your FXML
            Parent previousScreen = loader.load();
            // Get the current stage from the event source
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Set the scene for the stage with the root of the previous screen
            Scene scene = new Scene(previousScreen);
            stage.setScene(scene);
            stage.centerOnScreen();  
            // Optionally, set the title for the stage
            stage.setTitle("Client Home Page");

            // Show the updated stage
            stage.show();
        } catch (Exception e) {
            e.printStackTrace(); // Print the stack trace in case of an exception
        } 
    }

    /**
     * Handles the event triggered by clicking the 'View Report' button.
     * Validates the user input and generates the selected report based on the chosen parameters.
     * 
     * @param event The ActionEvent triggered by the 'View Report' button click.
     * @throws IOException If an error occurs when loading the FXML for the report page.
     */
    @FXML
    private void viewReportBtnOnClickAction(ActionEvent event) throws IOException {
        List<String> missingFields = new ArrayList<>();

        // Validate the required fields: report type, month, and year
        if (cbReportType.getValue() == null) {
            missingFields.add("report type");
        }
        if (cbMonth.getValue() == null) {
            missingFields.add("month");
        }
        if (cbYear.getValue() == null) {
            missingFields.add("year");
        }

        // If any required fields are missing, display an error message
        if (!missingFields.isEmpty()) {
            String errorMessage = "Please choose ";
            if (missingFields.size() == 1) {
                errorMessage += missingFields.get(0);
            } else if (missingFields.size() == 2) {
                errorMessage += missingFields.get(0) + " and " + missingFields.get(1);
            } else {
                errorMessage += String.join(", ", missingFields.subList(0, missingFields.size() - 1)) 
                                + ", and " + missingFields.get(missingFields.size() - 1);
            }
            libMsg.setText(errorMessage + ".");
            return;
        }  
        
        // Clear the error message
        libMsg.setText("");

        // Determine the chosen report type based on the ComboBox value
        ReportType chosenReportType;
        if (cbReportType.getValue().equals("Subcribtion Status Reports")) {
            chosenReportType = ReportType.SubcribtionStatus;
            System.out.println("11");
        } else{
            chosenReportType = ReportType.BorrowedDuration;
            System.out.println("12");
        } 
        System.out.println(chosenReportType);

        // Set the chosen report in the BraudeLibClient object
        BraudeLibClient.chosenReport = new Report(chosenReportType, cbMonth.getValue(), cbYear.getValue());

        // Navigate to the corresponding report page based on the chosen report type
        switch (chosenReportType) {
            case SubcribtionStatus:
                try {
                    // Load the FXML for the subscription status report page
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getResource("/librarianGui/ClientLibSubscribtionStatusReports.fxml"));
                    Parent previousScreen = loader.load();
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

                    Scene scene = new Scene(previousScreen);
                    stage.setScene(scene);
                    stage.centerOnScreen();
                    stage.setTitle("Subscribtion Status Reports Page");
                    stage.show();
                } catch (Exception e) {
                    e.printStackTrace();
                } 
                break;

            case BorrowedDuration:
                try {
                    // Load the FXML for the borrowed duration report page
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getResource("/librarianGui/ClientLibBorrowedDurationReports.fxml"));
                    Parent previousScreen = loader.load();
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

                    Scene scene = new Scene(previousScreen);
                    stage.setScene(scene);
                    stage.centerOnScreen();
                    stage.setTitle("Borrowed Duration Reports Page");
                    stage.show();
                } catch (Exception e) {
                    e.printStackTrace();
                } 
                break;
        }
    }

    /**
     * Initializes the ComboBoxes with the available options for year, report type, and month.
     * 
     * @param arg0 The location used to resolve relative paths for the root object.
     * @param arg1 The resources used to localize the root object.
     */
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        if (cbYear != null) {
            cbYear.getItems().addAll("2014", "2015", "2016", "2017", "2018", "2019", "2020", "2021", "2022",
            "2023", "2024", "2025");
        }
        if (cbReportType != null) {
            cbReportType.getItems().addAll("Subcribtion Status Reports", "Borrowed Duration Reports");
        }
        if (cbMonth != null) {
            cbMonth.getItems().addAll("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12");
        }
    }
}
