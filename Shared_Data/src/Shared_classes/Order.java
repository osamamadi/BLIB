package Shared_classes;

import java.io.Serializable;
import java.sql.Date;

/**
 * Represents an Order for a book copy made by a user.
 * The order contains details about the user, the book copy, the order date, and its status.
 * It also supports serialization for object transmission or storage.
 * 
 * @author Majd Awad
 * @version 1.1dz
 */
public class Order implements Serializable {
    
    /**
     * The unique identifier for the order.
     */
    private Integer id;

    /**
     * The unique identifier for the user who made the order.
     */
    private Integer userId;

    /**
     * The unique identifier for the book copy that has been ordered.
     */
    private Integer bookCopyId;

    /**
     * The date when the order was made.
     */
    private Date orderDate;

    /**
     * The status of the order (e.g., pending, completed, canceled).
     */
    private String orderStatus;

    /**
     * The name of the book associated with the order.
     */
    private String bookName;

    /**
     * Constructor to initialize an Order object with all necessary parameters.
     * 
     * @param id The unique identifier for the order.
     * @param userId The ID of the user who made the order.
     * @param bookCopyId The ID of the book copy being ordered.
     * @param orderDate The date when the order was made.
     * @param orderStatus The current status of the order.
     */
    public Order(Integer id, Integer userId, Integer bookCopyId, Date orderDate, String orderStatus) {
        this.id = id;
        this.userId = userId;
        this.bookCopyId = bookCopyId;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
    }

    /**
     * Constructor to initialize an Order object without an ID, typically used for new orders.
     * 
     * @param userId The ID of the user placing the order.
     * @param bookCopyId The ID of the book copy being ordered.
     * @param orderDate The date the order was made.
     * @param orderStatus The status of the order.
     */
    public Order(Integer userId, Integer bookCopyId, Date orderDate, String orderStatus) {
        this.userId = userId;
        this.bookCopyId = bookCopyId;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
    }

    /**
     * Constructor to initialize an Order with just the user ID and book name.
     * 
     * @param userId The ID of the user placing the order.
     * @param bookName The name of the book being ordered.
     */
    public Order(Integer userId, String bookName) {
        this.bookName = bookName;
        this.userId = userId;
    }

    /**
     * Default constructor for creating an empty Order object.
     */
    public Order() {}

    /**
     * Gets the unique identifier for the order.
     * 
     * @return The order ID.
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets the unique identifier for the order.
     * 
     * @param id The order ID to set.
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Gets the unique identifier for the user who made the order.
     * 
     * @return The user ID.
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * Sets the unique identifier for the user who made the order.
     * 
     * @param userId The user ID to set.
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * Gets the unique identifier for the book copy being ordered.
     * 
     * @return The book copy ID.
     */
    public Integer getBookCopyId() {
        return bookCopyId;
    }

    /**
     * Sets the unique identifier for the book copy being ordered.
     * 
     * @param bookCopyId The book copy ID to set.
     */
    public void setBookCopyId(Integer bookCopyId) {
        this.bookCopyId = bookCopyId;
    }

    /**
     * Gets the date when the order was made.
     * 
     * @return The order date.
     */
    public Date getOrderDate() {
        return orderDate;
    }

    /**
     * Sets the date when the order was made.
     * 
     * @param orderDate The order date to set.
     */
    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    /**
     * Gets the current status of the order.
     * 
     * @return The order status.
     */
    public String getOrderStatus() {
        return orderStatus;
    }

    /**
     * Sets the current status of the order.
     * 
     * @param orderStatus The status to set for the order.
     */
    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    /**
     * Gets the name of the book associated with the order.
     * 
     * @return The name of the book.
     */
    public String getBookName() {
        return bookName;
    }

    /**
     * Sets the name of the book associated with the order.
     * 
     * @param bookName The book name to set.
     */
    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    /**
     * Returns a string representation of the Order object, displaying its ID, user ID, book copy ID, order date, and status.
     * 
     * @return A string representation of the Order object.
     */
    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", userId=" + userId +
                ", bookCopyId=" + bookCopyId +
                ", orderDate=" + orderDate +
                ", orderStatus='" + orderStatus + '\'' +
                '}';
    }
}
