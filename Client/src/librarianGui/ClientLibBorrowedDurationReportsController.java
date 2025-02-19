package librarianGui;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.HashMap;
import java.util.LinkedHashMap;

import Shared_classes.BorrowedBookReport;
import Shared_classes.ClientServerContact;
import Shared_classes.Command;
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
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * Controller class responsible for handling borrowed book duration and delay reports.
 * This class listens to events, processes the response from the server, and updates the UI with the report data.
 * 
 * @author Majd Awad
 * @version 1.1dz
 */
public class ClientLibBorrowedDurationReportsController implements Initializable {


    /**
     * Button to navigate back to the previous page.
     */
    @FXML
    private Button btnBack;

    /**
     * BarChart displaying the borrowed book durations (loan periods).
     */
    @FXML
    private BarChart<String, Number> bcborrowedbooks;

    /**
     * BarChart displaying the delay distributions (on-time vs late).
     */
    @FXML
    private BarChart<String, Number> bcdelays;

    /**
     * Label to display messages to the user (e.g., success or error messages).
     */
    @FXML
    private Label libMsg;

    /**
     * Default constructor for creating an instance of {@code ClientCommonHomeController}.
     * 
     * This constructor does not initialize any properties, and is typically used for
     * cases where the controller is initialized later or via dependency injection.
     */
    public ClientLibBorrowedDurationReportsController() {
        // Constructor implementation
    }

    
 
    /**
     * Handles the action event of the "Back" button. This button takes the user to the previous page.
     * The button is typically used to navigate back from the borrowed report view to the report page.
     * 
     * @param event the ActionEvent triggered by the user clicking the "Back" button.
     * @exception Exception if any error occurs during scene switching.
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
            e.printStackTrace(); // Print the stack trace in case of an exception
        }
    }

    /**
     * Initializes the controller and requests the borrowed book report data from the server.
     * 
     * @param location the URL location used to resolve relative paths in the FXML.
     * @param resources the resource bundle containing the localized data.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ClientServerContact msg = new ClientServerContact(BraudeLibClient.chosenReport, Command.ViewBorrowedReport);
        ClientUI.LibClient.setController(this); // Set the controller
        ClientUI.LibClient.requestFromServer(msg); // Send request to the server
    }

    /**
     * Processes the server's response and updates the bar charts with the borrowed book report data.
     * The response is parsed, and the data is used to update the charts accordingly.
     * 
     * @param response the server response containing the borrowed book report data.
     */
    public void inputResponse(String response) {
        Platform.runLater(() -> {
            try {
                System.out.println("Received response: " + response);

                BorrowedBookReport borrowedReport = parseResponse(response);

                if (borrowedReport == null) {
                    libMsg.setText("Failed to load the borrowed book report.");
                    return;
                }

                updateCharts(borrowedReport);
            } catch (Exception e) {
                e.printStackTrace();
                libMsg.setText("An error occurred while processing the borrowed book report.");
                libMsg.setTextFill(Color.RED); // Display error message in red
            }
        });
    }

    /**
     * Updates the two bar charts with the data from the borrowed book report.
     * The method configures the X-axis and Y-axis of both charts and populates them with data.
     * 
     * @param report the borrowed book report containing the loan duration and delay distributions.
     */
    private void updateCharts(BorrowedBookReport report) {
        bcborrowedbooks.getData().clear();
        bcdelays.getData().clear();

        CategoryAxis xAxis1 = (CategoryAxis) bcborrowedbooks.getXAxis();
        xAxis1.setCategories(FXCollections.observableArrayList(
            "1-5 days", "6-10 days", "11-15 days", "16-20 days", "21+ days"
        ));

        NumberAxis yAxis1 = (NumberAxis) bcborrowedbooks.getYAxis();
        yAxis1.setTickUnit(1); // Show every value
        yAxis1.setAutoRanging(false);
        yAxis1.setLowerBound(0);
        yAxis1.setUpperBound(getMaxValue(report.getLoanDurationDistribution()));

        XYChart.Series<String, Number> borrowedSeries = new XYChart.Series<>();
        borrowedSeries.setName("Borrowed Book Durations");

        Map<String, Integer> fullDurations = new LinkedHashMap<>();
        fullDurations.put("1-5 days", report.getLoanDurationDistribution().getOrDefault("1-5 days", 0));
        fullDurations.put("6-10 days", report.getLoanDurationDistribution().getOrDefault("6-10 days", 0));
        fullDurations.put("11-15 days", report.getLoanDurationDistribution().getOrDefault("11-15 days", 0));
        fullDurations.put("16-20 days", report.getLoanDurationDistribution().getOrDefault("16-20 days", 0));
        fullDurations.put("21+ days", report.getLoanDurationDistribution().getOrDefault("21+ days", 0));

        fullDurations.forEach((key, value) -> borrowedSeries.getData().add(new XYChart.Data<>(key, value)));
        bcborrowedbooks.getData().add(borrowedSeries);

        CategoryAxis xAxis2 = (CategoryAxis) bcdelays.getXAxis();
        xAxis2.setCategories(FXCollections.observableArrayList(
            "On time", "1 day late", "2 days late", "3 days late", "4+ days late"
        ));

        NumberAxis yAxis2 = (NumberAxis) bcdelays.getYAxis();
        yAxis2.setTickUnit(1); // Show every value
        yAxis2.setAutoRanging(false);
        yAxis2.setLowerBound(0);
        yAxis2.setUpperBound(getMaxValue(report.getDelayDistribution()));

        XYChart.Series<String, Number> delaySeries = new XYChart.Series<>();
        delaySeries.setName("Delays");

        Map<String, Integer> fullDelays = new LinkedHashMap<>();
        fullDelays.put("On time", report.getDelayDistribution().getOrDefault("On time", 0));
        fullDelays.put("1 day late", report.getDelayDistribution().getOrDefault("1 day late", 0));
        fullDelays.put("2 days late", report.getDelayDistribution().getOrDefault("2 days late", 0));
        fullDelays.put("3 days late", report.getDelayDistribution().getOrDefault("3 days late", 0));
        fullDelays.put("4+ days late", report.getDelayDistribution().getOrDefault("4+ days late", 0));

        fullDelays.forEach((key, value) -> delaySeries.getData().add(new XYChart.Data<>(key, value)));
        bcdelays.getData().add(delaySeries);
    }

    /**
     * Retrieves the maximum value from the provided data map, used to set the Y-axis upper bound.
     * 
     * @param data the map containing the data to extract the maximum value from.
     * @return the maximum value found in the map.
     */
    private int getMaxValue(Map<String, Integer> data) {
        return data.values().stream().max(Integer::compareTo).orElse(10); // Default to 10 if no data
    }

    /**
     * Displays the value above the corresponding bar in the bar chart.
     * 
     * @param node the node representing the bar in the chart.
     * @param value the value to be displayed above the bar.
     */
    private void displayBarValue(Node node, Number value) {
        javafx.scene.control.Label label = new javafx.scene.control.Label(value.toString());
        label.setStyle("-fx-font-size: 12px; -fx-text-fill: black; -fx-font-weight: bold;");
        label.setTranslateY(-20); // Position label above the bar
        ((javafx.scene.layout.StackPane) node).getChildren().add(label);
    }

    /**
     * Parses the server response into a BorrowedBookReport object.
     * 
     * @param response the server response containing the borrowed book report data.
     * @return a BorrowedBookReport object, or null if parsing fails.
     * @exception Exception if there is an error while parsing the response.
     */
    private BorrowedBookReport parseResponse(String response) {
        try {
            if (response == null || response.isEmpty()) {
                System.err.println("Empty or null response received.");
                return null;
            }

            if (!response.startsWith("BorrowedBookReport")) {
                System.err.println("Unexpected response format: " + response);
                return null;
            }

            String month = extractValue(response, "month=", ",");
            String year = extractValue(response, "year=", ",");

            Map<String, Integer> loanDurationDistribution = extractMap(response, "loanDurationDistribution=");
            Map<String, Integer> delayDistribution = extractMap(response, "delayDistribution=");

            if (loanDurationDistribution.isEmpty()) {
                System.out.println("Loan duration distribution map is empty.");
            }
            if (delayDistribution.isEmpty()) {
                System.out.println("Delay distribution map is empty.");
            }

            return new BorrowedBookReport(null, month, year, loanDurationDistribution, delayDistribution);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to parse the response: " + response);
            return null;
        }
    }

    /**
     * Extracts a value from the response string based on the specified key and delimiter.
     * 
     * @param response the response string to extract the value from.
     * @param key the key to search for in the response.
     * @param delimiter the delimiter used to separate the value from the key.
     * @return the extracted value as a string, or null if extraction fails.
     * @exception Exception if an error occurs during extraction.
     */
    private String extractValue(String response, String key, String delimiter) {
        try {
            String[] parts = response.split(key);
            if (parts.length > 1) {
                return parts[1].split(delimiter)[0].trim();
            }
        } catch (Exception e) {
            System.err.println("Failed to extract value for key: " + key);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Extracts a map (key-value pairs) from the response string based on the specified key.
     * 
     * @param response the response string to extract the map from.
     * @param key the key that indicates the start of the map.
     * @return a map containing the extracted key-value pairs, or an empty map if extraction fails.
     * @exception Exception if an error occurs during map extraction.
     */
    private Map<String, Integer> extractMap(String response, String key) {
        Map<String, Integer> map = new HashMap<>();
        try {
            if (!response.contains(key)) {
                System.err.println("Key not found in response: " + key);
                return map;
            }

            String mapSection = response.split(key + "\\{")[1].split("}")[0];

            if (mapSection.isEmpty()) {
                System.out.println("Empty map for key: " + key);
                return map;
            }

            String[] entries = mapSection.split(", ");
            for (String entry : entries) {
                String[] parts = entry.split("=");
                if (parts.length == 2) {
                    String mapKey = parts[0].trim();
                    int mapValue = Integer.parseInt(parts[1].trim());
                    map.put(mapKey, mapValue);
                } else {
                    System.err.println("Malformed map entry: " + entry);
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to extract map for key: " + key);
            e.printStackTrace();
        }
        return map;
    }
}