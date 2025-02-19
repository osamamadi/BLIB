package dbController;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Shared_classes.Borrow;
import Shared_classes.Order;
import Shared_classes.Returns;
import Shared_classes.Subscriber;
import Shared_classes.User;
import Shared_classes.UserWithSubscriber;
import ocsf.server.ConnectionToClient;
import server.BraudeLibServer;
import dbController.LibrarianController.BorrowDetails;
/**
 * CommonController class handles various database operations related to 
 * library management such as fetching and updating subscriber details, 
 * handling borrow and return processes, sending reminders, and displaying 
 * orders and returns information. This class interacts with the database 
 * to manage library records and respond to client requests.
 * 
 * @author [Your Name]
 * @version 1.0
 */
public class CommonController {
	/** The database connection instance used for interacting with the database. */

    private static Connection conn = BraudeLibServer.getConnection();
    /**
     * Constructs a new CommonController.
     */
    public CommonController() {
    }
    /**
     * Updates the message date for a borrower if the due date is after the current date.
     * If a reminder message already exists for the borrower, it updates the message date,
     * otherwise it creates a new reminder message for the borrower.
     * 
     * @param details The borrow details for the user.
     * @throws SQLException If there is an error in the SQL query.
     */
    public static void updateMsgDateIfDueDateIsAfter(BorrowDetails details) throws SQLException {
        LocalDate dueDate = details.getLocalDueDate();
        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime dueDateTime = dueDate.atStartOfDay(); // Convert dueDate to LocalDateTime at midnight
        LocalDate newDueDate = dueDate.plusDays(13);  // New due date is 1 week and 6 days after the original one
        LocalDateTime newDueDateTime = newDueDate.atStartOfDay();
        LocalDate oneDayBeforeDueDate = dueDate.minusDays(1);
        LocalDateTime oneDayBeforeDueDateTime = oneDayBeforeDueDate.atStartOfDay(); // Convert to LocalDateTime at midnight
        int borrowId=-1;

        if (dueDateTime.isAfter(currentDateTime)) {
            
            String selectQuery = "SELECT id FROM db.msg WHERE msgType = 'Reminder' AND msgTo = ? AND msg = ? AND msgDate = ?";
            try (PreparedStatement selectStmt = conn.prepareStatement(selectQuery)) {
                // Set parameters
                selectStmt.setInt(1, details.getUserId());
                selectStmt.setString(2, "Tommorrow, you have to return your borrowed book, copy id: " + details.getCopyId());
                selectStmt.setTimestamp(3, Timestamp.valueOf(oneDayBeforeDueDateTime)); // Set new return date as msgDate
                System.out.println("789 "+selectStmt);
                try (ResultSet rs = selectStmt.executeQuery()) {
                	System.out.println("query Executed ");
                    if (rs.next()) {
                    	System.out.println("hS next");
                    	borrowId = rs.getInt("id");
                    	System.out.println("Borrow id will be " +borrowId);
                    }
                }
            }
            String query = "UPDATE db.msg SET msgDate = ? WHERE id = ?";
            try (PreparedStatement selectStmt2 = conn.prepareStatement(query)) {
            	selectStmt2.setTimestamp(1, Timestamp.valueOf(newDueDateTime)); // Set new return date as msgDate
            	selectStmt2.setInt(2, borrowId);
            	selectStmt2.executeUpdate();
            }

            
        } else {
            // Construct the insert query
            String insertQuery = "INSERT INTO db.msg (msgTo, msgType, msg, msgDate) VALUES (?, 'Reminder', ?, ?)";

            try (PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
                // Set parameters
                pstmt.setInt(1, details.getUserId());
                pstmt.setString(2, "Tomorrow, you have to return your borrowed book, copy id: " + details.getCopyId());
                pstmt.setTimestamp(3, Timestamp.valueOf(newDueDateTime)); // Set new return date as msgDate
                // Execute the insert
                System.out.println(pstmt);
                pstmt.executeUpdate();
                System.out.println("New reminder message added.");
            }
        }
    }
    /**
     * Fetches the list of returns for a specific subscriber.
     * 
     * @param client The client connection to send the data.
     * @param msg The subscriber ID as a message.
     */
    public static void displayReturns(ConnectionToClient client, Object msg) {
        String query = """
            SELECT b.id AS borrowId, b.borrow_date, b.due_date,
                   CASE 
                       WHEN r.actual_return_date IS NOT NULL THEN r.actual_return_date 
                       ELSE 'lost' 
                   END AS actual_return_date,
                   CASE 
                       WHEN r.actual_return_date IS NOT NULL AND r.actual_return_date <= b.due_date THEN 'no'
                       ELSE 'yes' 
                   END AS isLate
            FROM db.borrow b
            LEFT JOIN db.return r ON b.id = r.borrow_id
            WHERE b.user_id = ? AND b.status = 'finish'
        """;

        List<Returns> returnsList = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            int subscriberId = Integer.parseInt(msg.toString()); // Parse subscriber ID from the message
            pstmt.setInt(1, subscriberId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int borrowId = rs.getInt("borrowId");
                    Date borrowDate = rs.getDate("borrow_date");
                    Date dueDate = rs.getDate("due_date");
                    String actualReturnDate = rs.getString("actual_return_date"); // Handles both dates and 'lost'
                    String isLate = rs.getString("isLate"); // Returns 'yes' or 'no'

                    Returns returns = new Returns(borrowId, borrowDate, dueDate, actualReturnDate, isLate);
                    returnsList.add(returns);
                }

                client.sendToClient(returnsList);

            } catch (IOException e) {
                e.printStackTrace();
                client.sendToClient("Error fetching returns data: " + e.getMessage());
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            try {
                client.sendToClient("Error executing query: " + e.getMessage());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    /**
     * Fetches subscriber details from the database.
     * 
     * @param client The client connection to send the data.
     * @param username The username of the subscriber (optional).
     * @throws IOException If an error occurs while communicating with the client.
     */
    public static void fetchSubscribersFromDB(ConnectionToClient client, String username) throws IOException {
        String baseQuery = "SELECT u.id, u.username, u.password, u.first_name, u.last_name, " +
                "s.phone_number, s.email, s.status, s.late_returns, s.subscriber_number " +
                "FROM db.user u JOIN db.subscriber s ON u.id = s.id " +
                "WHERE u.user_type = ?";
        String query = username != null && !username.isEmpty() ? baseQuery + " AND u.username = ?" : baseQuery;
        System.out.println("Got into fetch ");
        try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            preparedStatement.setString(1, "subscriber");
            if (username != null && !username.isEmpty()) {
                preparedStatement.setString(2, username);
            }

            ResultSet resultSet = preparedStatement.executeQuery();

            if (username != null && !username.isEmpty()) {
                // If username is provided, expect at most one result
                if (resultSet.next()) {
                    System.out.println("Got into statement 1 ");
                    UserWithSubscriber userWithSubscriber = mapRowToUserWithSubscriber(resultSet);

                    // Check and update frozen status
                    checkAndUpdateFreezeStatus(userWithSubscriber);

                    client.sendToClient(userWithSubscriber); // Send the single object to the client
                } else {
                    client.sendToClient("No subscriber found with the provided username.");
                }
            } else {
                // If username is not provided, return a list of all subscribers
                List<UserWithSubscriber> userWithSubscriberList = new ArrayList<>();
                while (resultSet.next()) {
                    UserWithSubscriber userWithSubscriber = mapRowToUserWithSubscriber(resultSet);

                    // Check and update frozen status for each subscriber
                    checkAndUpdateFreezeStatus(userWithSubscriber);

                    userWithSubscriberList.add(userWithSubscriber);
                }
                client.sendToClient(userWithSubscriberList); // Send the list of subscribers to the client
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new IOException("Error fetching subscribers from the database.", e);
        }
    }
    /**
     * Checks and updates the freeze status of a subscriber.
     * 
     * @param subscriber The subscriber to check and update.
     */
    private static void checkAndUpdateFreezeStatus(UserWithSubscriber subscriber) {
        if ("frozen".equalsIgnoreCase(subscriber.getStatus())) {
            String query = "SELECT todate FROM db.frozen WHERE subscriber_id = ? ORDER BY todate DESC LIMIT 1";
            try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
                preparedStatement.setInt(1, subscriber.getId());
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    Date toDate = resultSet.getDate("todate");
                    LocalDate today = LocalDate.now();

                    if (toDate.toLocalDate().isBefore(today)) {
                        // Update the subscriber's status to active
                        String updateQuery = "UPDATE db.subscriber SET status = 'active' WHERE id = ?";
                        try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                            updateStmt.setInt(1, subscriber.getId());
                            updateStmt.executeUpdate();
                        }
                        // Update the status in the current object
                        subscriber.setStatus("active");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * Maps a row of the result set to a UserWithSubscriber object.
     * 
     * @param resultSet The result set containing the data.
     * @return The UserWithSubscriber object.
     * @throws SQLException If there is an error mapping the result set.
     */
    private static UserWithSubscriber mapRowToUserWithSubscriber(ResultSet rs) throws SQLException {
        User user = new User(
                rs.getInt("id"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("first_name"),
                rs.getString("last_name")
        );

        Subscriber subscriber = new Subscriber(
                rs.getInt("id"),
                rs.getString("status"),
                rs.getString("email"),
                rs.getInt("late_returns"),
                rs.getInt("subscriber_number"),
                rs.getString("phone_number")
        );

        return new UserWithSubscriber(user, subscriber);
    }
    /**
     * Updates the details of a subscriber in the database.
     * 
     * @param subscriber The subscriber object containing updated information.
     * @param client The client connection to send the response to.
     * @throws IOException If an error occurs while sending the response to the client.
     */
	public static void UpdateSubscriber(UserWithSubscriber subscriber, ConnectionToClient client) throws IOException {
        String query = "UPDATE db.user u " +
                       "JOIN db.subscriber s ON u.id = s.id " +
                       "SET " +
                       "u.username = ?, " +
                       "u.password = ?, " +
                       "u.first_name = ?, " +
                       "u.last_name = ?, " +
                       "s.status = ?, " +
                       "s.email = ?, " +
                       "s.late_returns = ?, " +
                       "s.phone_number = ? " +
                       "WHERE u.id = ?";

        try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            // Set parameters
            preparedStatement.setString(1, subscriber.getUsername());
            preparedStatement.setString(2, subscriber.getPassword());
            preparedStatement.setString(3, subscriber.getFirstName());
            preparedStatement.setString(4, subscriber.getLastName());
            preparedStatement.setString(5, subscriber.getStatus());
            preparedStatement.setString(6, subscriber.getEmail());
            preparedStatement.setInt(7, subscriber.getLateReturn());
            preparedStatement.setString(8, subscriber.getPhone());
            preparedStatement.setInt(9, subscriber.getId());

            // Execute update
            preparedStatement.executeUpdate();
            client.sendToClient("Subscriber Updated Successfully");
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                client.sendToClient("Error updating subscriber details");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
	 /**
     * Fetches the borrow details for a specific subscriber and sends them to the client.
     * 
     * @param client The client connection to send the response to.
     * @param msg The message containing the subscriber ID.
     * @throws SQLException If there is an error executing the SQL query.
     * @throws IOException If there is an error while sending the response to the client.
     */
	public static void displayBorrows(ConnectionToClient client, Object msg) throws SQLException, IOException {
	    String query = "SELECT id, borrow_date, due_date, user_id, copy_id,status FROM db.borrow WHERE user_id = ?";

	    List<Borrow> borrowList = new ArrayList<>();

	    try (PreparedStatement pstmt = conn.prepareStatement(query)) {
	        // Set the parameter if filtering by subscriber
        	Integer i =  Integer.parseInt((String) msg);
            pstmt.setInt(1, i);

	        try (ResultSet rs = pstmt.executeQuery()) {
	            while (rs.next()) {
	                int id = rs.getInt("id");
	                Date borrowDate = rs.getDate("borrow_date");
	                Date dueDate = rs.getDate("due_date");
	                int userId = rs.getInt("user_id");
	                int copyId = rs.getInt("copy_id");
	                String status = rs.getString("status");
	                Borrow borrow = new Borrow(id, borrowDate, dueDate, userId, copyId,status);
	                borrowList.add(borrow);
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        client.sendToClient("Error");
	    }
	    client.sendToClient(borrowList);
	}
	  /**
     * Fetches the order details for a specific subscriber (if provided) and sends them to the client.
     * 
     * @param client The client connection to send the response to.
     * @param msg The message containing the subscriber ID, or null if displaying all orders.
     * @throws SQLException If there is an error executing the SQL query.
     * @throws IOException If there is an error while sending the response to the client.
     */
	public static void displayOrders(ConnectionToClient client, Object msg) throws SQLException, IOException {
	    String query;
	    boolean filterBySubscriber = (msg != null);

	    // Define the query based on the condition
	    if (filterBySubscriber) {
	        query = "SELECT id, order_date, order_status, user_id, copy_id FROM db.order WHERE user_id = ?";
	    } else {
	        query = "SELECT id, order_date, order_status, user_id, copy_id FROM db.order";
	    }

	    List<Order> orderList = new ArrayList<>();

	    try (PreparedStatement pstmt = conn.prepareStatement(query)) {
	        // If filtering by subscriber, set the parameter
	        if (filterBySubscriber) {
	            Integer subscriberId = Integer.parseInt((String) msg);
	            pstmt.setInt(1, subscriberId);
	        }
	        System.out.println("RS:");
	        // Execute the query
	        try (ResultSet rs = pstmt.executeQuery()) {
	            while (rs.next()) {
	                int id = rs.getInt("id");
	                Date orderDate = rs.getDate("order_date");
	                String orderStatus = rs.getString("order_status");
	                int userId = rs.getInt("user_id");
	                int bookCopyId = rs.getInt("copy_id");

	                // Create an Order object
	                Order order = new Order(id, userId, bookCopyId, orderDate, orderStatus);
	                orderList.add(order);
	            }
	        }
	    } catch (SQLException e) {
	        // Log the error and notify the client
	        System.err.println("Error fetching orders: " + e.getMessage());
	        e.printStackTrace();
	        client.sendToClient("Error retrieving orders.");
	        return;
	    }
	    System.out.println("SENDDDDDING:"+orderList);
	    // Send the list of orders to the client
	    client.sendToClient(orderList);
	}
	 /**
     * Fetches the availability details of a book, including location on shelf, availability status,
     * and closest return date for borrowed copies.
     * 
     * @param bookId The ID of the book to check availability for.
     * @return A map containing the book details such as availability, location, and closest return date.
     */
	public static Map<String, Object> fetchBookAvailability(int bookId) {
	    String query = """
	        SELECT 
	            b.book_name,
	            CASE
	                WHEN EXISTS (
	                    SELECT 1 
	                    FROM db.CopyOfBook c 
	                    WHERE c.book_id = b.id AND c.status = 'Available'
	                ) THEN b.location_on_shelf
	                ELSE NULL
	            END AS location_on_shelf,
	            CASE
	                WHEN EXISTS (
	                    SELECT 1 
	                    FROM db.CopyOfBook c 
	                    WHERE c.book_id = b.id AND c.status = 'Available'
	                ) THEN 'Available'
	                WHEN EXISTS (
	                    SELECT 1 
	                    FROM db.CopyOfBook c 
	                    WHERE c.book_id = b.id AND c.status = 'Borrowed'
	                ) THEN 'Unavailable - Borrowed'
	                WHEN EXISTS (
	                    SELECT 1 
	                    FROM db.CopyOfBook c 
	                    WHERE c.book_id = b.id AND c.status = 'Lost'
	                ) THEN 'Unavailable - All copies lost'
	                ELSE 'Unavailable'
	            END AS availability,
	            CASE
	                WHEN NOT EXISTS (
	                    SELECT 1 
	                    FROM db.CopyOfBook c 
	                    WHERE c.book_id = b.id AND c.status = 'Available'
	                ) THEN (
	                    SELECT MIN(br.due_date) 
	                    FROM db.CopyOfBook c 
	                    JOIN db.borrow br ON c.id = br.copy_id 
	                    WHERE c.book_id = b.id AND c.status = 'Borrowed'
	                )
	                ELSE NULL
	            END AS closest_return_date
	        FROM db.Book b
	        WHERE b.id = ?;
	    """;

	    try (PreparedStatement pstmt = conn.prepareStatement(query)) {
	        pstmt.setInt(1, bookId);

	        try (ResultSet rs = pstmt.executeQuery()) {
	            if (rs.next()) {
	                Map<String, Object> bookDetails = new HashMap<>();
	                bookDetails.put("book_name", rs.getString("book_name"));
	                bookDetails.put("location_on_shelf", rs.getString("location_on_shelf")); // NULL if unavailable
	                bookDetails.put("availability", rs.getString("availability"));
	                bookDetails.put("closest_return_date", rs.getDate("closest_return_date")); // NULL if available

	                // Additional logic to check for "Lost" status
	                if ("Unavailable - All copies lost".equals(rs.getString("availability"))) {
	                    bookDetails.put("message", "There are no available copies of this book as all copies are marked as Lost.");
	                }

	                return bookDetails;
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return null; // Return null if no data is found or an error occurs
	}

}


