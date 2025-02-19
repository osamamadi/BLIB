package Shared_classes;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Represents a report on the status of subscribers within a given time period.
 * This class provides details on the days within a month, the percentage of frozen subscribers,
 * and the associated month and year for the report.
 * 
 * @author Majd Awad
 * @version 1.1dz
 */
public class SubscriberStatusReport extends Report implements Serializable {
    
    private static final long serialVersionUID = 1L;

    /**
     * List of days in the month that this report covers.
     */
    private ArrayList<String> days;
    
    /**
     * The month for which the report is generated.
     */
    private String month;
    
    /**
     * The year for which the report is generated.
     */
    private String year;
    
    /**
     * The percentage of subscribers who are frozen in the report period.
     */
    private double frozenPercent;

    /**
     * Constructor to initialize a SubscriberStatusReport object.
     * 
     * @param reportType The type of the report (e.g., SubscriptionStatus).
     * @param month The month for which the report is generated.
     * @param year The year for which the report is generated.
     * @param days The list of days in the month to be included in the report.
     * @param frozenPercent The percentage of frozen subscribers in the report period.
     */
    public SubscriberStatusReport(ReportType reportType, String month, String year, ArrayList<String> days, double frozenPercent) {
        super(reportType, month, year);
        this.days = days;
        this.month = month;
        this.year = year;
        this.frozenPercent = frozenPercent;
    }

    // Getter and Setter methods

    /**
     * Gets the list of days for which the report is generated.
     * 
     * @return The list of days.
     */
    public ArrayList<String> getDays() {
        return days;
    }

    /**
     * Sets the list of days for the report.
     * 
     * @param days The list of days to set.
     */
    public void setDays(ArrayList<String> days) {
        this.days = days;
    }

    /**
     * Gets the month for which the report is generated.
     * 
     * @return The month of the report.
     */
    public String getMonth() {
        return month;
    }

    /**
     * Sets the month for the report.
     * 
     * @param month The month to set.
     */
    @Override
    public void setMonth(String month) {
        this.month = month;
    }

    /**
     * Gets the year for which the report is generated.
     * 
     * @return The year of the report.
     */
    public String getYear() {
        return year;
    }

    /**
     * Sets the year for the report.
     * 
     * @param year The year to set.
     */
    @Override
    public void setYear(String year) {
        this.year = year;
    }

    /**
     * Gets the frozen percentage for the report.
     * 
     * @return The frozen percentage.
     */
    public double getFrozenPercent() {
        return frozenPercent;
    }

    /**
     * Sets the frozen percentage for the report.
     * 
     * @param frozenPercent The frozen percentage to set.
     */
    public void setFrozenPercent(double frozenPercent) {
        this.frozenPercent = frozenPercent;
    }

    /**
     * Provides a string representation of the SubscriberStatusReport object.
     * 
     * @return A string representation of the SubscriberStatusReport object.
     */
    @Override
    public String toString() {
        return "SubscriberStatusReport{" +
                "days=" + days +
                ", month='" + month + '\'' +
                ", year='" + year + '\'' +
                ", frozenPercent=" + frozenPercent +
                "} " + super.toString();
    }
}
