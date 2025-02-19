package Shared_classes;

import java.io.Serializable;

/**
 * Represents a report object that contains information about a report's type, month, and year.
 * Implements Serializable for object serialization, allowing the report data to be transmitted or stored.
 * 
 * @author Majd Awad
 * @version 1.1dz
 */
public class Report implements Serializable {
    
    /**
     * A unique identifier for serialization compatibility.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The type of report (e.g., borrowed books, subscriptions, etc.).
     */
    private ReportType reportType;

    /**
     * The month for which the report is generated.
     */
    private String month;

    /**
     * The year for which the report is generated.
     */
    private String Year;

    /**
     * Constructor to initialize a Report object with given parameters.
     * 
     * @param reportType The type of report (e.g., borrowed books, subscriptions, etc.).
     * @param month The month the report is for.
     * @param year The year the report is for.
     */
    public Report(ReportType reportType, String month, String year) {
        super();
        this.reportType = reportType;
        this.month = month;
        Year = year;
    }

    /**
     * Gets the type of the report.
     * 
     * @return The report type.
     */
    public ReportType getReportType() {
        return reportType;
    }

    /**
     * Sets the type of the report.
     * 
     * @param reportType The report type to set.
     */
    public void setReportType(ReportType reportType) {
        this.reportType = reportType;
    }

    /**
     * Gets the month for which the report is generated.
     * 
     * @return The month.
     */
    public String getMonth() {
        return month;
    }

    /**
     * Sets the month for which the report is generated.
     * 
     * @param month The month to set.
     */
    public void setMonth(String month) {
        this.month = month;
    }

    /**
     * Gets the year for which the report is generated.
     * 
     * @return The year.
     */
    public String getYear() {
        return Year;
    }

    /**
     * Sets the year for which the report is generated.
     * 
     * @param year The year to set.
     */
    public void setYear(String year) {
        Year = year;
    }

    /**
     * Returns a string representation of the Report object, displaying its type, month, and year.
     * 
     * @return A string representation of the Report.
     */
    @Override
    public String toString() {
        return "Report{" +
                "reportType=" + reportType +
                ", month='" + month + '\'' +
                ", Year='" + Year + '\'' +
                '}';
    }
}
