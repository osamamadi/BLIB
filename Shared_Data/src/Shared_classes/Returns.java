package Shared_classes;

import java.io.Serializable;
import java.sql.Date;

/**
 * Represents a record for a returned borrowed book.
 * This class holds details about the borrow and return process, 
 * including the borrow ID, borrow date, due date, actual return date, 
 * and whether the return was late.
 * 
 * @author Majd Awad
 * @version 1.1dz
 */
public class Returns implements Serializable {
    
    /**
     * The unique identifier for the borrow record associated with this return.
     */
    private int borrowId; 
    
    /**
     * The date when the book was borrowed.
     */
    private Date borrowDate;
    
    /**
     * The date when the book was supposed to be returned.
     */
    private Date dueDate;
    
    /**
     * The actual date when the book was returned.
     */
    private String actualReturnDate;
    
    /**
     * A flag indicating whether the return was late:
     * - "1" means the return was late.
     * - "0" means the return was on time.
     */
    private String isLate;

    /**
     * Constructor to initialize the Returns object with given parameters.
     * 
     * @param borrowId The unique identifier of the borrow record.
     * @param borrowDate The date when the book was borrowed.
     * @param dueDate The due date for the book return.
     * @param actualReturnDate The actual date when the book was returned.
     * @param isLate A flag indicating whether the return was late (1 for late, 0 for on time).
     */
    public Returns(int borrowId, Date borrowDate, Date dueDate, String actualReturnDate, String isLate) {
        this.borrowId = borrowId;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.actualReturnDate = actualReturnDate;
        this.isLate = isLate;
    }

    /**
     * Gets the borrow ID associated with this return.
     * 
     * @return The borrow ID.
     */
    public int getBorrowId() {
        return borrowId;
    }

    /**
     * Sets the borrow ID for this return.
     * 
     * @param borrowId The borrow ID to set.
     */
    public void setBorrowId(int borrowId) {
        this.borrowId = borrowId;
    }

    /**
     * Gets the date when the book was borrowed.
     * 
     * @return The borrow date.
     */
    public Date getBorrowDate() {
        return borrowDate;
    }

    /**
     * Sets the date when the book was borrowed.
     * 
     * @param borrowDate The borrow date to set.
     */
    public void setBorrowDate(Date borrowDate) {
        this.borrowDate = borrowDate;
    }

    /**
     * Gets the due date for the book return.
     * 
     * @return The due date.
     */
    public Date getDueDate() {
        return dueDate;
    }

    /**
     * Sets the due date for the book return.
     * 
     * @param dueDate The due date to set.
     */
    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    /**
     * Gets the actual return date of the book.
     * 
     * @return The actual return date.
     */
    public String getActualReturnDate() {
        return actualReturnDate;
    }

    /**
     * Sets the actual return date of the book.
     * 
     * @param actualReturnDate The actual return date to set.
     */
    public void setActualReturnDate(String actualReturnDate) {
        this.actualReturnDate = actualReturnDate;
    }

    /**
     * Gets the flag indicating whether the return was late.
     * 
     * @return "1" if the return was late, "0" if it was on time.
     */
    public String getIsLate() {
        return isLate;
    }

    /**
     * Sets the flag indicating whether the return was late.
     * 
     * @param isLate The flag to set ("1" for late, "0" for on time).
     */
    public void setIsLate(String isLate) {
        this.isLate = isLate;
    }
}
