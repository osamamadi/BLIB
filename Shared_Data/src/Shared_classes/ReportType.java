package Shared_classes;

/**
 * Represents different types of reports that can be generated.
 * The possible report types are:
 * - SubscriptionStatus: Report on the subscription status of users.
 * - BorrowedDuration: Report on the duration for which books have been borrowed.
 * 
 * @author Majd Awad
 * @version 1.1dz
 */
public enum ReportType {
    
    /**
     * A report on the subscription status of users.
     */
    SubcribtionStatus,
    
    /**
     * A report on the duration for which books have been borrowed.
     */
    BorrowedDuration;
}
