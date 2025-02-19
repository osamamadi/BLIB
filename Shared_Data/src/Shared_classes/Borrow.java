package Shared_classes;

import java.io.Serializable;
import java.sql.Date;

/**
 * Represents a Borrow transaction for a book.
 * This class contains details about the borrow, including borrow date, due date, user ID, book barcode, 
 * copy ID, and the current status of the borrow (e.g., "active", "returned", etc.).
 * Implements Serializable for object serialization.
 * 
 * @author Majd Awad
 * @version 1.1dz
 */
public class Borrow implements Serializable {
    
    /**
     * The unique identifier for this borrow transaction.
     * This ID is used to uniquely identify a borrow record in the system.
     */
    private Integer id;
    
    /**
     * The date the book was borrowed.
     * Stores the date when the book borrowing transaction was initiated.
     */
    private Date borrow_date;
    
    /**
     * The due date for the borrowed book.
     * Stores the date when the borrowed book must be returned.
     */
    private Date due_date;
    
    /**
     * The unique identifier for the user who borrowed the book.
     * Links the borrow transaction to a specific user in the system.
     */
    private Integer user_id;
    
    /**
     * The barcode of the borrowed book.
     * Used to uniquely identify the book being borrowed, typically a unique code on the book.
     */
    private String Barcode;
    
    /**
     * The unique identifier for the specific copy of the book that was borrowed.
     * Used to track which particular copy of the book was borrowed, especially when multiple copies of the same book exist.
     */
    private Integer copy_id;
    
    /**
     * The status of the borrow transaction.
     * Indicates the current state of the borrow, such as "active", "returned", etc.
     */
    private String status;

    /**
     * Gets the status of the borrow transaction.
     * 
     * @return The current status of the borrow (e.g., "active", "returned").
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status of the borrow transaction.
     * 
     * @param status The status of the borrow to set (e.g., "active", "returned").
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Gets the unique copy ID of the borrowed book.
     * 
     * @return The ID of the specific copy borrowed.
     */
    public Integer getCopyId() {
        return copy_id;
    }

    /**
     * Sets the unique copy ID for the borrowed book.
     * 
     * @param copy_id The ID of the specific copy borrowed.
     */
    public void setCopyId(Integer copy_id) {
        this.copy_id = copy_id;
    }

    /**
     * Gets the unique identifier for this borrow transaction.
     * 
     * @return The borrow transaction ID.
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets the unique identifier for this borrow transaction.
     * 
     * @param id The borrow transaction ID to set.
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Gets the date when the book was borrowed.
     * 
     * @return The borrow date.
     */
    public Date getBorrow_date() {
        return borrow_date;
    }

    /**
     * Sets the date when the book was borrowed.
     * 
     * @param borrow_date The date the book was borrowed.
     */
    public void setBorrow_date(Date borrow_date) {
        this.borrow_date = borrow_date;
    }

    /**
     * Gets the due date for returning the borrowed book.
     * 
     * @return The due date for the borrowed book.
     */
    public Date getDue_date() {
        return due_date;
    }

    /**
     * Sets the due date for the borrowed book.
     * 
     * @param due_date The date when the borrowed book is due for return.
     */
    public void setDue_date(Date due_date) {
        this.due_date = due_date;
    }

    /**
     * Gets the user ID of the person who borrowed the book.
     * 
     * @return The user ID of the borrower.
     */
    public Integer getUser_id() {
        return user_id;
    }

    /**
     * Sets the user ID of the person who borrowed the book.
     * 
     * @param user_id The user ID to set for the borrower.
     */
    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    /**
     * Gets the barcode of the borrowed book.
     * 
     * @return The barcode of the borrowed book.
     */
    public String getBarcode() {
        return Barcode;
    }

    /**
     * Sets the barcode of the borrowed book.
     * 
     * @param Barcode The barcode to set for the borrowed book.
     */
    public void setBarcode(String Barcode) {
        this.Barcode = Barcode;
    }

    /**
     * Constructor to initialize a Borrow object with specific details.
     * 
     * @param id The unique identifier for the borrow transaction.
     * @param borrow_date The date the book was borrowed.
     * @param due_date The date the book is due to be returned.
     * @param user_id The user ID of the borrower.
     * @param Barcode The barcode of the borrowed book.
     */
    public Borrow(Integer id, Date borrow_date, Date due_date, Integer user_id, String Barcode) {
        this.id = id;
        this.borrow_date = borrow_date;
        this.due_date = due_date;
        this.user_id = user_id;
        this.Barcode = Barcode;
    }

    /**
     * Constructor to initialize a Borrow object with specific details, including the status.
     * 
     * @param id The unique identifier for the borrow transaction.
     * @param borrow_date The date the book was borrowed.
     * @param due_date The date the book is due to be returned.
     * @param user_id The user ID of the borrower.
     * @param copy_id The ID of the specific copy borrowed.
     * @param status The status of the borrow transaction (e.g., "active", "returned").
     */
    public Borrow(Integer id, Date borrow_date, Date due_date, Integer user_id, Integer copy_id, String status) {
        this.id = id;
        this.borrow_date = borrow_date;
        this.due_date = due_date;
        this.user_id = user_id;
        this.copy_id = copy_id;
        this.status = status;
    }

    /**
     * Constructor to initialize a Borrow object with specific details without an ID.
     * 
     * @param borrow_date The date the book was borrowed.
     * @param due_date The date the book is due to be returned.
     * @param user_id The user ID of the borrower.
     * @param Barcode The barcode of the borrowed book.
     */
    public Borrow(Date borrow_date, Date due_date, Integer user_id, String Barcode) {
        this.borrow_date = borrow_date;
        this.due_date = due_date;
        this.user_id = user_id;
        this.Barcode = Barcode;
    }
}
