package dbController;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import Shared_classes.ClientServerContact;
import Shared_classes.Subscriber;
import Shared_classes.User;
import Shared_classes.UserWithSubscriber;
import ocsf.server.ConnectionToClient;
import server.BraudeLibServer;
import dbController.LibrarianController.BorrowDetails;
/**
 * The SubscriberController class handles operations related to subscribers' borrow requests and orders.
 * It provides methods for checking borrow request statuses, placing orders, and handling the interaction
 * with the database for borrowing books, checking due dates, and managing orders.
 * 
 * The class performs various database queries to check for conditions such as borrow returns, orders, 
 * and subscription status, and updates messages accordingly. It also facilitates placing new orders for books
 * by checking if conditions such as available copies, borrow status, and subscriber's status are met.
 * 
 * @author Your Name
 * @version 1.0
 */
public class SubscriberController {
	/** The database connection instance used for interacting with the database. */

    private static Connection conn = BraudeLibServer.getConnection();
    
    /**
     * Constructs a new SubscriberController.
     */
    public SubscriberController() {
    }
    /**
     * This method checks the status of a borrow request for a particular borrow ID.
     * It checks if the borrow has been returned, if there is an existing order for the book,
     * and if the borrow date is within the legal range (7 days).
     * It also updates the 'msg' table with appropriate messages based on the conditions.
     *
     * @param borrowId the ID of the borrow to check
     */
    public static void isReturned_isOrderd(int borrowId) {
        boolean isReturned = false;
        boolean isOrdered = false;
        boolean isLegal = false;

        try {
            // Step 1: Check if the due_date is legal (within the next 7 days)
            String checkLegalDateQuery = "SELECT COUNT(*) FROM db.borrow WHERE status = 'active' AND id = ? AND due_date >= CURDATE() AND due_date <= DATE_ADD(CURDATE(), INTERVAL 7 DAY)";
            try (PreparedStatement stmt = conn.prepareStatement(checkLegalDateQuery)) {
                stmt.setInt(1, borrowId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        isLegal = rs.getInt(1) > 0; // True if the due_date is legal
                    }
                }
            }

            // Step 2: Check if the borrow is returned
            String checkReturnQuery = "SELECT COUNT(*) FROM db.return WHERE borrow_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(checkReturnQuery)) {
                stmt.setInt(1, borrowId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        isReturned = rs.getInt(1) > 0; // True if the borrow is returned
                    }
                }
            }

            // Step 3: Get details of the borrow (copy_id, user_id, and due_date)
            String getBorrowDetailsQuery = "SELECT copy_id, user_id, due_date FROM db.borrow WHERE id = ? AND status = 'active'";
            int copyId = -1; // Default invalid value
            int userId = -1; // Default invalid value
            LocalDate dueDate = null;

            try (PreparedStatement stmt = conn.prepareStatement(getBorrowDetailsQuery)) {
                stmt.setInt(1, borrowId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        copyId = rs.getInt("copy_id");
                        userId = rs.getInt("user_id");
                        dueDate = rs.getDate("due_date").toLocalDate();
                    }
                }
            }

            // Step 4: Check if there is a pending order for this copy_id
            if (copyId != -1) {
                String checkOrderQuery = "SELECT COUNT(*) FROM db.order WHERE copy_id = ? AND order_status = 'pending'";
                try (PreparedStatement stmt = conn.prepareStatement(checkOrderQuery)) {
                    stmt.setInt(1, copyId);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            isOrdered = rs.getInt(1) > 0; // True if there is a pending order
                        }
                    }
                }
            }
            
            // Step 5: Insert appropriate messages into the msg table
            String insertMessageQuery = "INSERT INTO db.msg (msgTo, msgType, msg, isRead, msgDate) VALUES (?, ?, ?, ?, NOW())";
            try (PreparedStatement stmt = conn.prepareStatement(insertMessageQuery)) {
                if (isReturned) {
                	String getFinishUserIdQuery = "SELECT user_id FROM db.borrow WHERE id = ? AND status = 'finish'";
                    try (PreparedStatement stmt2 = conn.prepareStatement(getFinishUserIdQuery)) {
                    	stmt2.setInt(1, borrowId);
                        try (ResultSet rs = stmt2.executeQuery()) {
                            if (rs.next()) 
                                userId = rs.getInt("user_id");
                        }
                    }
                	
                    // Message to user who made the borrow: Rejection because the book is returned
                    stmt.setInt(1, userId);
                    stmt.setString(2, "Deny Extension");
                    stmt.setString(3, "Extension rejected for borrow ID: " + borrowId + " because the book already returned.");
                    stmt.setInt(4, 0); // Unread message
                    System.out.println("The query is: " + stmt);
                    stmt.executeUpdate();
                } else if (isOrdered) {
                    // Message to user who made the borrow: Rejection because of an awaiting order
                    stmt.setInt(1, userId);
                    stmt.setString(2, "Deny Extension");
                    stmt.setString(3, "Extension rejected for borrow ID: " + borrowId + " because there is an awaiting order for the book.");
                    stmt.setInt(4, 0); // Unread message
                    stmt.executeUpdate();
                } else if (!isReturned && !isOrdered && isLegal) {
                    // Extend the due date by 2 weeks
                    LocalDate newDueDate = dueDate.plusWeeks(2);
                    String updateDueDateQuery = "UPDATE db.borrow SET due_date = ? WHERE id = ? AND status = 'active'";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateDueDateQuery)) {
                        updateStmt.setDate(1, java.sql.Date.valueOf(newDueDate));
                        updateStmt.setInt(2, borrowId);
                        updateStmt.executeUpdate();
                    }

                    // Message to user who made the borrow: Approval
                    stmt.setInt(1, userId);
                    stmt.setString(2, "Accept Extension");
                    stmt.setString(3, "Extension approved for borrow ID: " + borrowId + ". The new due date is " + newDueDate + ".");
                    stmt.setInt(4, 0); // Unread message
                    stmt.executeUpdate();
                    BorrowDetails borrow = new BorrowDetails(copyId,dueDate,userId);
                    CommonController.updateMsgDateIfDueDateIsAfter(borrow);
                    // Message to all librarians: Approval notification
                    String getLibrariansQuery = "SELECT id FROM db.user WHERE user_type = 'librarian'";
                    try (PreparedStatement librarianStmt = conn.prepareStatement(getLibrariansQuery);
                         ResultSet rs = librarianStmt.executeQuery()) {
                        while (rs.next()) {
                            int librarianId = rs.getInt("id");
                            stmt.setInt(1, librarianId);
                            stmt.setString(2, "Accept Extension");
                            stmt.setString(3, "Extension approved for borrow ID: " + borrowId + ". The new due date is " + newDueDate + ".");
                            stmt.setInt(4, 0); // Unread message
                            stmt.executeUpdate();
                        }
                    }
                } else if (!isLegal) {
                    // Message for an illegal due date
                    stmt.setInt(1, userId);
                    stmt.setString(2, "Deny Extension");
                    stmt.setString(3, "Extension rejected for borrow ID: " + borrowId + " because the due date is not within the legal range.");
                    stmt.setInt(4, 0); // Unread message
                    stmt.executeUpdate();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Is Returned: " + isReturned);
        System.out.println("Is Ordered: " + isOrdered);
        System.out.println("Is Legal: " + isLegal);

       
    }
    /**
     * Creates an order for a subscriber for a specific book.
     * This method checks the subscriber's status, whether they already have an active order,
     * whether they have borrowed the book, and if there are available copies to borrow.
     * If everything is valid, an order is placed for the book.
     *
     * @param bookName the name of the book for which to create an order
     * @param userId the ID of the subscriber creating the order
     * @return a message indicating the result of the operation
     */
    public static String createOrder(String bookName, int userId) {
        // Query to get the book ID from the book name
        String getBookIdQuery = """
            SELECT id , copies
            FROM db.Book 
            WHERE book_name = ?
        """;

        // Query to check subscriber status
        String checkSubscriberStatusQuery = """
            SELECT status 
            FROM db.subscriber 
            WHERE id = ?
        """;

        // Query to check if the subscriber already has an order for this book
        String checkExistingOrderQuery = """
            SELECT 1
            FROM db.order o
            JOIN db.copyofbook c ON o.copy_id = c.id
            WHERE o.user_id = ? AND c.book_id = ? AND o.order_status IN ('pending', 'delivered')
            LIMIT 1
        """;

        // Query to check if the subscriber has already borrowed any copy of this book
        String checkBorrowedByUserQuery = """
            SELECT 1
            FROM db.borrow br
            JOIN db.copyofbook c ON br.copy_id = c.id
            WHERE br.user_id = ? AND c.book_id = ? AND c.status = 'Borrowed' AND br.status = 'active'
            LIMIT 1
        """;

        // Query to check if all valid copies are borrowed (exclude 'Lost')
        String checkAllBorrowedQuery = """
            SELECT 
                SUM(CASE WHEN status = 'Borrowed' THEN 1 ELSE 0 END) AS borrowedCopies,
                SUM(CASE WHEN status = 'Available' THEN 1 ELSE 0 END) AS availableCopies
            FROM db.copyofbook
            WHERE book_id = ?
        """;

        // Query to get the closest return date for a borrowed copy that is not already ordered
        String getClosestReturnQuery = """
            SELECT br.copy_id, br.due_date 
            FROM db.borrow br
            JOIN db.copyofbook c ON br.copy_id = c.id
            WHERE c.book_id = ? 
              AND c.status = 'Borrowed' AND br.status = 'active'
              AND NOT EXISTS (
                  SELECT 1 
                  FROM db.order o 
                  WHERE o.copy_id = br.copy_id 
                    AND o.order_status IN ('pending', 'delivered')
              )
            ORDER BY br.due_date ASC
            LIMIT 1
        """;

        // Query to place a new order
        String placeOrderQuery = """
            INSERT INTO db.order (user_id, copy_id, order_date, order_status)
            VALUES (?, ?, CURDATE(), 'pending')
        """;

        try (PreparedStatement pstmtGetBookId = conn.prepareStatement(getBookIdQuery);
             PreparedStatement pstmtCheckStatus = conn.prepareStatement(checkSubscriberStatusQuery);
             PreparedStatement pstmtCheckExistingOrder = conn.prepareStatement(checkExistingOrderQuery);
             PreparedStatement pstmtCheckBorrowedByUser = conn.prepareStatement(checkBorrowedByUserQuery);
             PreparedStatement pstmtCheckBorrowed = conn.prepareStatement(checkAllBorrowedQuery);
             PreparedStatement pstmtGetClosestReturn = conn.prepareStatement(getClosestReturnQuery);
             PreparedStatement pstmtPlaceOrder = conn.prepareStatement(placeOrderQuery)) {

            // Step 1: Get the book ID from the book name
            pstmtGetBookId.setString(1, bookName);
            ResultSet rsBookId = pstmtGetBookId.executeQuery();
            if (!rsBookId.next()) {
                return "Book not found.";
            }
            int bookId = rsBookId.getInt("id");
            int copies = rsBookId.getInt("copies");

            // Step 2: Check if the subscriber's status is not frozen
            pstmtCheckStatus.setInt(1, userId);
            ResultSet rsStatus = pstmtCheckStatus.executeQuery();
            if (rsStatus.next() && rsStatus.getString("status").equals("frozen")) {
                return "Your subscription is frozen. Cannot place an order.";
            }

            // Step 3: Check if the subscriber already has an active order for this book
            pstmtCheckExistingOrder.setInt(1, userId);
            pstmtCheckExistingOrder.setInt(2, bookId);
            ResultSet rsExistingOrder = pstmtCheckExistingOrder.executeQuery();
            if (rsExistingOrder.next()) {
                return "You already have an active order for this book.";
            }

            // Step 4: Check if the subscriber has already borrowed any copy of this book
            pstmtCheckBorrowedByUser.setInt(1, userId);
            pstmtCheckBorrowedByUser.setInt(2, bookId);
            ResultSet rsBorrowedByUser = pstmtCheckBorrowedByUser.executeQuery();
            if (rsBorrowedByUser.next()) {
                return "You have already borrowed a copy of this book.";
            }

            // Step 5: Check if all valid copies of the book are borrowed
            pstmtCheckBorrowed.setInt(1, bookId);
            ResultSet rsBorrowed = pstmtCheckBorrowed.executeQuery();
            if (rsBorrowed.next()) {
                int borrowedCopies = rsBorrowed.getInt("borrowedCopies");
                int availableCopies = rsBorrowed.getInt("availableCopies");

                // If there are available copies
                if (availableCopies > 0) {
                    return "There are available copies. Please borrow the book instead.";
                }
            }

            // Step 6: Get the closest return date for a borrowed copy
            pstmtGetClosestReturn.setInt(1, bookId);
            ResultSet rsClosestReturn = pstmtGetClosestReturn.executeQuery();
            if (rsClosestReturn.next()) {
                int copyId = rsClosestReturn.getInt("copy_id");
                Date closestReturnDate = rsClosestReturn.getDate("due_date");

                // Step 7: Place the order
                pstmtPlaceOrder.setInt(1, userId);
                pstmtPlaceOrder.setInt(2, copyId);
                int rowsAffected = pstmtPlaceOrder.executeUpdate();

                if (rowsAffected > 0) {
                    return "Order placed successfully. The book will be available after " + closestReturnDate + ".";
                } else {
                    return "Failed to place the order.";
                }
            } else {
                return "All the copies of this book are ordered.";
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return "An error occurred while processing the order.";
        }
    }
}
