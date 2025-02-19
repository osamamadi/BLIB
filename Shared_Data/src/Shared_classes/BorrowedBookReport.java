package Shared_classes;

import java.io.Serializable;
import java.util.Map;

/**
 * Represents a report on borrowed books, including distributions of loan durations and delays.
 * This class extends the `Report` class and adds functionality specific to the borrowed book data.
 * It includes maps for tracking loan duration and delay distributions.
 * Implements Serializable for object serialization.
 * 
 * @author Majd Awad
 * @version 1.1dz
 */
public class BorrowedBookReport extends Report implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * A map that stores loan duration ranges as keys (e.g., "1-7 days") and the corresponding count of loans within each range as values.
     * It represents the distribution of loan durations for borrowed books.
     */
    private Map<String, Integer> loanDurationDistribution;

    /**
     * A map that stores delay ranges as keys (e.g., "1-5 days") and the corresponding count of delayed loans within each range as values.
     * It represents the distribution of delays for borrowed books that were returned late.
     */
    private Map<String, Integer> delayDistribution;

    /**
     * Constructor to initialize a BorrowedBookReport object with specific data for loan durations and delays.
     * 
     * @param reportType The type of the report (e.g., monthly, yearly).
     * @param month The month for which the report is generated.
     * @param year The year for which the report is generated.
     * @param loanDurationDistribution A map of loan duration ranges and their corresponding loan counts.
     * @param delayDistribution A map of delay ranges and their corresponding delay counts.
     */
    public BorrowedBookReport(ReportType reportType, String month, String year, Map<String, Integer> loanDurationDistribution, Map<String, Integer> delayDistribution) {
        super(reportType, month, year);
        this.loanDurationDistribution = loanDurationDistribution;
        this.delayDistribution = delayDistribution;
    }

    /**
     * Gets the distribution of loan durations.
     * 
     * @return A map where the keys are loan duration ranges (e.g., "1 to 7 days") and the values are the number of loans in that range.
     */
    public Map<String, Integer> getLoanDurationDistribution() {
        return loanDurationDistribution;
    }

    /**
     * Sets the distribution of loan durations.
     * 
     * @param loanDurationDistribution A map where the keys are loan duration ranges (e.g., "1-7 days") and the values are the number of loans in that range.
     */
    public void setLoanDurationDistribution(Map<String, Integer> loanDurationDistribution) {
        this.loanDurationDistribution = loanDurationDistribution;
    }

    /**
     * Gets the distribution of loan delays.
     * 
     * @return A map where the keys are delay ranges (e.g., "1-5 days") and the values are the number of delayed loans in that range.
     */
    public Map<String, Integer> getDelayDistribution() {
        return delayDistribution;
    }

    /**
     * Sets the distribution of loan delays.
     * 
     * @param delayDistribution A map where the keys are delay ranges (e.g., "1-5 days") and the values are the number of delayed loans in that range.
     */
    public void setDelayDistribution(Map<String, Integer> delayDistribution) {
        this.delayDistribution = delayDistribution;
    }

    /**
     * Returns a string representation of the BorrowedBookReport object, including the month, year, loan duration distribution, and delay distribution.
     * 
     * @return A string representation of the BorrowedBookReport.
     */
    @Override
    public String toString() {
        return "BorrowedBookReport [month=" + getMonth() + ", year=" + getYear() + ", loanDurationDistribution=" + loanDurationDistribution + ", delayDistribution=" + delayDistribution + "]";
    }
}
