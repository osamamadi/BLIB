package librarianGui;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import Shared_classes.ClientServerContact;
import Shared_classes.Command;
import Shared_classes.SubscriberStatusReport;
import client.BraudeLibClient;
import client.ClientUI;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * Controller for the Subscription Status Reports page in the Librarian UI.
 * Displays the subscription status report using pie and bar charts.
 * 
 * @author Majd Awad
 * @version 1.1dz
 */
public class ClientLibSubscribtionStatusReportsController implements Initializable {

    /**
     * Button to navigate back to the previous page (View Report page).
     */
    @FXML
    private Button btnBack;

    /**
     * Pie chart to display the distribution of frozen and active subscribers.
     */
    @FXML
    private PieChart pcSbscriptionStatus;

    /**
     * Bar chart to display the number of frozen subscribers per day.
     */
    @FXML
    private BarChart<String, Number> lcSubscriptionStatus;

    /**
     * Category axis for the days in the month (1-31).
     */
    @FXML
    private CategoryAxis days;

    /**
     * Number axis for the number of subscribers.
     */
    @FXML
    private NumberAxis numberOfSubscripers;

    /**
     * Label to display server response messages or error messages.
     */
    @FXML
    private Label libMsg;

    /**
     * String to hold the server response message.
     */
    private String serverMessage;

    /**
     * List of all days (01-31) for chart axes.
     */
    private ArrayList<String> allDays;

    /**
     * Default constructor for ClientLibSubscriptionStatusReportsController.
     * Initializes the controller instance for handling subscription status reports.
     */
    public ClientLibSubscribtionStatusReportsController() {
        
    }
    
    
    /**
     * Navigates back to the previous page (View Report page).
     * 
     * @param event The ActionEvent triggered by the 'Back' button click.
     */
    @FXML
    private void getBackBtn(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/LibrarianGui/ClientLibViewReport.fxml"));
            Parent root = loader.load();
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            currentStage.setScene(scene);
            currentStage.setTitle("View Report Page");
            currentStage.centerOnScreen();	

            currentStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Initializes the Subscription Status Reports page.
     * Sets up the day labels for the bar chart and requests data from the server.
     * 
     * @param location The location used to resolve relative paths for the root object.
     * @param resources The resources used to localize the root object.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize the instance variable directly
        pcSbscriptionStatus.setPrefSize(500, 300);
        allDays = new ArrayList<>();
        for (int i = 1; i <= 31; i++) {
            allDays.add(String.format("%02d", i)); // Format as "01", "02", etc.
        }
        days.setCategories(FXCollections.observableArrayList(allDays));

        // Request data from the server
        ClientServerContact msg = new ClientServerContact(BraudeLibClient.chosenReport, Command.ViewSubscriptionReport);
        ClientUI.LibClient.setController(this);
        ClientUI.LibClient.requestFromServer(msg);
    }

    /**
     * Processes the response from the server and updates the charts accordingly.
     * 
     * @param response The server response containing subscription status data.
     */
    public void inputResponse(String response) {
        Platform.runLater(() -> {
            try {
                System.out.println("Received response: " + response);

                SubscriberStatusReport statusReport = parseResponse(response);

                if (statusReport == null) {
                    libMsg.setText("Failed to load the subscription status report.");
                    return;
                }

                // Clear existing data in both charts
                pcSbscriptionStatus.getData().clear();
                lcSubscriptionStatus.getData().clear();

                // Update PieChart
                double frozenPercent = statusReport.getFrozenPercent();
                double restPercent = 100 - frozenPercent;

                // Add PieChart data
                PieChart.Data frozenData = new PieChart.Data("Frozen Subscribers (" + String.format("%.1f", frozenPercent) + "%)", frozenPercent);
                PieChart.Data restData = new PieChart.Data("Active Subscribers (" + String.format("%.1f", restPercent) + "%)", restPercent);

                pcSbscriptionStatus.getData().addAll(frozenData, restData);

                // Disable the labels directly on the pie slices
                pcSbscriptionStatus.setLabelLineLength(0);

                // Set PieChart title
                pcSbscriptionStatus.setTitle("Subscription Status Distribution");

                // Update BarChart
                XYChart.Series<String, Number> frozenSeries = new XYChart.Series<>();
                frozenSeries.setName("Frozen Subscribers");

                for (String day : allDays) {
                    int frozenCount = statusReport.getDays().stream()
                        .filter(data -> data.startsWith(day)) // Find matching day
                        .map(data -> Integer.parseInt(data.split(" ")[2].replace(")", ""))) // Extract count
                        .findFirst()
                        .orElse(0); // Default to 0 if not found

                    frozenSeries.getData().add(new XYChart.Data<>(day, frozenCount));
                }
                lcSubscriptionStatus.getData().add(frozenSeries);

                // Set axis labels and chart title
                days.setLabel("Day");
                numberOfSubscripers.setLabel("Frozen Subscribers Amount");
                lcSubscriptionStatus.setTitle("Frozen Subscribers Per Day");

                // Update the message label
                libMsg.setText("Subscription status report for " + statusReport.getMonth() + "/" + statusReport.getYear() + " loaded successfully.");
            } catch (Exception e) {
                e.printStackTrace();
                libMsg.setText("An error occurred while processing the subscription status report.");
            }
        });
    }

    /**
     * Parses the server response and extracts the subscription status report data.
     * 
     * @param response The server response containing the subscription status data.
     * @return A SubscriberStatusReport object with the extracted data, or null if parsing fails.
     */
    private SubscriberStatusReport parseResponse(String response) {
        try {
            if (response == null || response.isEmpty()) {
                System.err.println("Empty or null response received.");
                return null;
            }

            // Debug: Print the raw response for analysis
            System.out.println("Raw response: " + response);

            // Extract days
            if (!response.contains("days=[")) {
                System.err.println("Response does not contain 'days='.");
                return null;
            }
            String daysSection = response.split("days=\\[")[1].split("]")[0];
            ArrayList<String> days = new ArrayList<>();
            if (!daysSection.isEmpty()) {
                String[] dayEntries = daysSection.split(", ");
                for (String entry : dayEntries) {
                    days.add(entry.trim());
                }
            }

            // Extract month
            if (!response.contains("month='")) {
                System.err.println("Response does not contain 'month='.");
                return null;
            }
            String month = response.split("month='")[1].split("'")[0];

            // Extract year
            if (!response.contains("year='")) {
                System.err.println("Response does not contain 'year='.");
                return null;
            }
            String year = response.split("year='")[1].split("'")[0];

            // Extract frozenPercent
            if (!response.contains("frozenPercent=")) {
                System.err.println("Response does not contain 'frozenPercent='.");
                return null;
            }
            double frozenPercent = Double.parseDouble(response.split("frozenPercent=")[1].split("\\}")[0]);

            return new SubscriberStatusReport(null, month, year, days, frozenPercent);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to parse the response: " + response);
            return null;
        }
    }

}
