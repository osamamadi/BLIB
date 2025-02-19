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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Shared_classes.Message;
import Shared_classes.Report;
import Shared_classes.Returns;
import Shared_classes.Borrow;
import Shared_classes.BorrowedBookReport;
import Shared_classes.ClientServerContact;
import Shared_classes.Subscriber;
import Shared_classes.SubscriberStatusReport;
import Shared_classes.User;
import Shared_classes.UserWithSubscriber;
import ocsf.server.ConnectionToClient;
import server.BraudeLibServer;
/**
 * The `LibrarianController` class provides functionality for managing library
 * operations such as manual extensions, borrowing books, and retrieving messages.
 * @author [Your Name]
 * @version 1.0
 */
public class LibrarianController {
	/** The database connection instance used for interacting with the database. */

    private static Connection conn = BraudeLibServer.getConnection();
    /**
     * Constructs a new LibrarianController.
     */
    public LibrarianController() {
    }
    /**
     * Extends the due date of a borrow manually by 2 weeks and notifies the librarian
     * and subscriber.
     *
     * @param borrowId  The ID of the borrow record to extend.
     * @param librarian The librarian performing the extension.
     */
    public static void manual_Extension(int borrowId, User librarian) {
        try {
            // Step 1: Get borrow details (due_date and user_id)
            String getBorrowDetailsQuery = "SELECT copy_id, user_id, due_date, status FROM db.borrow WHERE id = ?";
            int userId = -1,copyId = -1; // Subscriber's ID
            LocalDate dueDate = null;
            String Status = null;
            try (PreparedStatement stmt = conn.prepareStatement(getBorrowDetailsQuery)) {
                stmt.setInt(1, borrowId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                    	copyId = rs.getInt("copy_id");
                        userId = rs.getInt("user_id");
                        dueDate = rs.getDate("due_date").toLocalDate();
                        Status = rs.getString("status");
                    }
                }
            }

            // Check if borrow details were found
            if (userId == -1 || dueDate == null || copyId == -1 || Status.equals("finish") || Status == null) {
                System.out.println("Borrow record not found for ID: " + borrowId);
                return;
            }
            
            BorrowDetails borrow = new BorrowDetails(copyId,dueDate,userId);
            CommonController.updateMsgDateIfDueDateIsAfter(borrow);
            
            // Step 2: Update the due date (extend by 2 weeks)
            LocalDate newDueDate = dueDate.plusWeeks(2);
            String updateDueDateQuery = "UPDATE db.borrow SET due_date = ? WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateDueDateQuery)) {
                stmt.setDate(1, java.sql.Date.valueOf(newDueDate));
                stmt.setInt(2, borrowId);
                stmt.executeUpdate();
            }

            // Step 3: Insert messages into the msg table
            String insertMessageQuery = "INSERT INTO db.msg (msgTo, msgType, msg, isRead, msgDate) VALUES (?, ?, ?, ?, NOW())";

            try (PreparedStatement stmt = conn.prepareStatement(insertMessageQuery)) {
                // Message to the subscriber
            	String extensionDate = LocalDate.now().toString(); // Get the current date as the extension date

            	String subscriberMsg = "Librarian " + librarian.getFirstName() + " " + librarian.getLastName() +
            	                       " performed a manual extension for borrow ID = " + borrowId +
            	                       " on " + extensionDate + 
            	                       ". The new due date is " + newDueDate + ".";

                stmt.setInt(1, userId); // Send to the subscriber
                stmt.setString(2, "Manual Extension");
                stmt.setString(3, subscriberMsg);
                stmt.setInt(4, 0); // Unread message
                stmt.executeUpdate();

                // Message to the librarian
                String librarianMsg = "You performed a manual extension for borrow ID = " + borrowId +
                                      ". The new due date is " + newDueDate + ".";
                stmt.setInt(1, librarian.getId()); // Send to the librarian
                stmt.setString(2, "Manual Extension");
                stmt.setString(3, librarianMsg);
                stmt.setInt(4, 0); // Unread message
                stmt.executeUpdate();
            }

            System.out.println("Manual extension successfully completed for borrow ID: " + borrowId);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
 /**
  * Retrieves messages from the database where a sender is specified.
  *
  * @return A list of maps containing subscriber IDs and their corresponding messages.
  * @throws SQLException If a database access error occurs.
  */
    public static List<Map<Integer, String>> getMessagesFromDatabase() throws SQLException {
        String query = "SELECT msgFrom, msg FROM db.msg WHERE msgFrom IS NOT NULL";

        List<Map<Integer, String>> messages = new ArrayList<>();

        try (PreparedStatement preparedStatement = conn.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                Map<Integer, String> messageData = new HashMap<>();
                int subscriberId = resultSet.getInt("msgFrom");
                String message = resultSet.getString("msg");
                messageData.put(subscriberId, message);
                messages.add(messageData);
            }
        }
        return messages;
    }
    /**
     * Checks if a given barcode exists in the database.
     *
     * @param barcode The barcode to check.
     * @return True if the barcode exists, false otherwise.
     */
    private static boolean doesBarcodeExist(String barcode) {
        boolean exists = false;
        String query = "SELECT 1 FROM db.copyofbook WHERE barcode = ? LIMIT 1";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, barcode);
            try (ResultSet rs = stmt.executeQuery()) {
                exists = rs.next(); // If a row exists, the barcode exists
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return exists;
    }
    /**
     * Checks if a subscriber exists in the database.
     *
     * @param id The ID of the subscriber to check.
     * @return True if the subscriber exists, false otherwise.
     */
    private static boolean doesSubExist(Integer id) {
        boolean exists = false;
        String query = "SELECT 1 FROM db.subscriber WHERE id = ? LIMIT 1";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                exists = rs.next(); // If a row exists, the barcode exists
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return exists;
    }
    /**
     * Handles the borrowing of a book for a subscriber.
     *
     * @param b      The `Borrow` object containing borrowing details.
     * @param client The client connection to send responses to.
     * @throws IOException  If an I/O error occurs.
     * @throws SQLException If a database access error occurs.
     */
    public static void BorrowBook(Borrow b, ConnectionToClient client) throws IOException, SQLException {
    		
    	    if(doesSubExist(b.getUser_id())==false) {
    	    	client.sendToClient("No existing subscriber with this id");
    	    	return;
    	    }    		
    		if(doesBarcodeExist(b.getBarcode())==false) {
    			client.sendToClient("No existing book with this barcode");
    			return;
    		}    		
    	
            String CheckQuery = "SELECT status FROM db.subscriber WHERE id = ?";            
            try (PreparedStatement query1 = conn.prepareStatement(CheckQuery)) {
                query1.setInt(1, b.getUser_id());
                try (ResultSet rs = query1.executeQuery()) {
                    if (rs.next()) {
                        String status = rs.getString("status");
                        if(status.equals("frozen")) {
                        	client.sendToClient("Cant Borrow, Subscriber is Frozen");
                        	return;
                        }
                    }
                }
            }
            int copy_id = -1;
            String CheckQuery1 = "SELECT id,status FROM db.copyofbook WHERE barcode = ?";
            try (PreparedStatement query2 = conn.prepareStatement(CheckQuery1)) {
                query2.setString(1, b.getBarcode());
                System.out.println("Query 1 :" + query2);
                try (ResultSet rs = query2.executeQuery()) {
                    if (rs.next()) {
                        String status = rs.getString("status");
                        copy_id = rs.getInt("id");
                        if(status.equals("Borrowed") || status.equals("Lost")) {
                        	System.out.println("Cant Borrow, Book is "+status);
                        	client.sendToClient("Cant Borrow, Book is "+status);
                        	return;
                        }
                        else if (status.equals("Reserved")) {
                        	if(DoesOrderExist(b.getUser_id(), copy_id)==false) {
                        		System.out.println("Cant Borrow, Book is "+status);
                            	client.sendToClient("Cant Borrow, Book is "+status);
                            	return;
                        	}
                        	else {
                        		String updateOrderStatus = "UPDATE db.order " +
                                        "SET order_status = 'finished' " +
                                        "WHERE user_id = ? AND copy_id = ?";
                        		try (PreparedStatement orderStmt = conn.prepareStatement(updateOrderStatus)) {
                        			orderStmt.setInt(1, b.getUser_id());
                        			orderStmt.setInt(2, copy_id);
                                    orderStmt.executeUpdate();
                                }
                        	}
                        }
                    }
                }
            }

            if(DoesBorrowExist(copy_id, b.getUser_id()) == true) {
            	client.sendToClient("You already borrowed a copy for this book");
            	return;
            }
            
            // Step 2: Perform the INSERT using the retrieved copy_id
            String insertQuery = "INSERT INTO db.borrow (borrow_date, due_date, user_id, copy_id) VALUES (?, ?, ?, ?)";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                String sqlBorrowDate = b.getBorrow_date().toString(); // Ensure date formatting is correct
                String sqlDueDate = b.getDue_date().toString();
                insertStmt.setString(1, sqlBorrowDate);  
                insertStmt.setString(2, sqlDueDate);     
                insertStmt.setInt(3, b.getUser_id());   
                insertStmt.setInt(4, copy_id); // Use the retrieved copy_id
                insertStmt.executeUpdate();
            }
            // UPDATE query
            String updateQuery = "UPDATE db.copyofbook SET status = 'Borrowed' WHERE barcode = ?";
            try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                updateStmt.setString(1, b.getBarcode()); // Set barcode for UPDATE query
                updateStmt.executeUpdate();
            }
            client.sendToClient("Borrowed successfully.");
            insertMessage(b.getUser_id(), "Reminder", "Tommorrow, you have to return your borrowed book, copy id: "+copy_id, getYesterday(b.getDue_date()));                             
    }
        
    /**
     * Returns the date for the day before the given date.
     *
     * @param givenDate The date to calculate yesterday from.
     * @return The date representing yesterday.
     */
    private static Date getYesterday(Date givenDate) {
        // Use a Calendar instance to manipulate the given date
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(givenDate); // Set the given date
        calendar.add(Calendar.DAY_OF_MONTH, -1); // Subtract 1 day to get yesterday
        // Convert the result back to java.sql.Date
        return new Date(calendar.getTimeInMillis());
    }
    
    
    
    
    
    
    
    /**
     * Checks if an order exists for a given user and copy.
     * @param userId the ID of the user.
     * @param copyId the ID of the copy.
     * @return true if the order exists and is delivered, false otherwise.
     * @throws SQLException if a database access error occurs.
     */
    private static boolean DoesOrderExist(int userId, int copyId) throws SQLException {
        String query = "SELECT EXISTS ( " +
                       "    SELECT 1 " +
                       "    FROM db.order " +
                       "    WHERE user_id = ? " +
                       "      AND copy_id = ? " +
                       "      AND order_status = 'delivered' " +
                       ") AS order_exists";
        
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, copyId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean("order_exists");
                }
            }
        }
        return false;  // If no record is found, return false.
    }

    /**
     * Checks if there is an active borrow for a given copy ID and user ID.
     * @param copyId the ID of the copy.
     * @param userId the ID of the user.
     * @return true if an active borrow exists, false otherwise.
     * @throws SQLException if a database access error occurs.
     */
    private static Boolean DoesBorrowExist(int copyId,int userId) throws SQLException {
        // Query to get the book_id for the given copy
        String getBookIdQuery = "SELECT book_id FROM db.copyofbook WHERE id = ?";

        // Query to get all copy_ids for the same book_id
        String getAllCopiesQuery = "SELECT id FROM db.copyofbook WHERE book_id = ?";

        // Query to check if a copy_id has an active borrow
        String checkBorrowStatusQuery = """
            SELECT EXISTS (
                SELECT 1 
                FROM db.borrow 
                WHERE copy_id = ? AND user_id = ? AND status = 'active'
            ) AS is_active
        """;
        
        Integer bookId = null;

        // Step 1: Get the book_id for the given copyId
        try (PreparedStatement getBookIdStmt = conn.prepareStatement(getBookIdQuery)) {
            getBookIdStmt.setInt(1, copyId);
            try (ResultSet rs = getBookIdStmt.executeQuery()) {
                if (rs.next()) {
                    bookId = rs.getInt("book_id");
                } else {
                    throw new SQLException("No book found for the given copy_id: " + copyId);
                }
            }
        }

        // Step 2: Get all copy_ids for the retrieved book_id
        if (bookId == null) 
            return false; // Return false if no book_id found
        
        
        try (PreparedStatement getAllCopiesStmt = conn.prepareStatement(getAllCopiesQuery)) {
            getAllCopiesStmt.setInt(1, bookId);
            try (ResultSet rsCopies = getAllCopiesStmt.executeQuery()) {
                while (rsCopies.next()) {
                    int currentCopyId = rsCopies.getInt("id");

                    // Step 3: Check if the current copy_id does not have an active borrow
                    try (PreparedStatement checkBorrowStmt = conn.prepareStatement(checkBorrowStatusQuery)) {
                        checkBorrowStmt.setInt(1, currentCopyId);
                        checkBorrowStmt.setInt(2, userId);
                        try (ResultSet rsCheck = checkBorrowStmt.executeQuery()) {
                            if (rsCheck.next() && rsCheck.getBoolean("is_active")) {
                                return true; // Return true if an active borrow exists for this copy
                            }
                        }
                    }
                }
            }
        }

        return false; // Return false if no active borrow exists for any copy
    }



    /**
     * Retrieves the count of unread messages for a specific librarian.
     * @param librarianId the ID of the librarian.
     * @return the number of unread messages.
     */
    public static int getUnreadMessageCount(int librarianId) {
        String query = "SELECT COUNT(*) FROM db.msg WHERE msgTo = ? AND isRead = 0 AND msgDate <= NOW()";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, librarianId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    /**
     * Retrieves all messages for a specific librarian.
     * @param librarianId the ID of the librarian.
     * @return a list of messages.
     */
    public static List<Message> getMessagesForLibrarian(int librarianId) {
        List<Message> messages = new ArrayList<>();
        
        
        String selectQuery = "SELECT msg, msgDate, msgType, isRead, id " +
                "FROM db.msg " +
                "WHERE msgTo = ? AND msgDate <= NOW() " +
                "ORDER BY msgDate DESC";
        
        try (PreparedStatement selectStmt = conn.prepareStatement(selectQuery);
             ) {
            
            // Fetch messages for the given librarian ID
            selectStmt.setInt(1, librarianId);
            ResultSet rs = selectStmt.executeQuery();
            while (rs.next()) {
                messages.add(new Message(
                    rs.getString("msg"),
                    rs.getTimestamp("msgDate"),
                    rs.getString("msgType"),
                    rs.getBoolean("isRead"),
                    rs.getInt("id")
                ));
            }
            
         
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }
    /**
     * Marks a specific message as read.
     * @param msg the message to mark as read.
     */
    public static void MarkMessageAsRead(Message msg) {
    	String updateQuery = "UPDATE db.msg SET isRead = 1 WHERE id = ? AND isRead = 0";
    	try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery);){
    		// Update messages to mark them as read
            updateStmt.setInt(1, msg.getid());
            updateStmt.executeUpdate(); // Mark messages as isRead = 1
    	}
    	
    catch (SQLException e) {
        e.printStackTrace();
    	}
    }
    /**
     * Generates a subscriber status report for a given month and year.
     * If the report exists in the database, it retrieves and returns it.
     * Otherwise, it calculates the report based on frozen data and saves it to the database.
     * @param msg a message object containing the report details (month and year).
     * @return the generated or retrieved subscriber status report.
     * @throws SQLException if a database access error occurs.
     */
    public static SubscriberStatusReport viewSubscriberStatusReport(ClientServerContact msg) throws SQLException {
        Report report = (Report) msg.getObj1();
        String month = report.getMonth();
        String year = report.getYear();

        // Step 1: Check if the report exists in the subscriberstatusreport table
        String checkReportQuery = """
            SELECT day, frozenpercent, frozen_subscribers 
            FROM db.subscriberstatusreport 
            WHERE month = ? AND year = ?
        """;
        PreparedStatement checkStmt = conn.prepareStatement(checkReportQuery);
        checkStmt.setString(1, month);
        checkStmt.setString(2, year);

        ResultSet reportResultSet = checkStmt.executeQuery();
        ArrayList<String> days = new ArrayList<>();
        double frozenPercent = 0.0;

        if (reportResultSet.next()) {
            // Report exists, populate the result from subscriberstatusreport
            int totalFrozenSubscribers = 0;
            int totalDaysWithData = 0;

            do {
                String day = reportResultSet.getString("day");
                int frozenSubscribers = reportResultSet.getInt("frozen_subscribers");
                totalFrozenSubscribers += frozenSubscribers;
                totalDaysWithData++;
                days.add(day + " (Frozen: " + frozenSubscribers + ")");
            } while (reportResultSet.next());

            // Calculate frozen percent
            frozenPercent = totalDaysWithData > 0 ? (double) totalFrozenSubscribers / totalDaysWithData : 0.0;

            return new SubscriberStatusReport(report.getReportType(), month, year, days, frozenPercent);
        }

        // Step 2: If the report is not found, calculate data from the frozen table
        String queryDailyFrozenSubscribers = """
            WITH RECURSIVE days AS (
                SELECT 1 AS day
                UNION ALL
                SELECT day + 1
                FROM days
                WHERE day < 31
            )
            SELECT 
                d.day,
                COUNT(f.subscriber_id) AS frozen_count
            FROM 
                days d
            LEFT JOIN db.frozen f
                ON d.day BETWEEN DAY(f.fromdate) AND DAY(f.todate)
                AND MONTH(f.fromdate) = ? AND YEAR(f.fromdate) = ?
            GROUP BY d.day 
            ORDER BY d.day;
        """;

        PreparedStatement dailyFrozenStmt = conn.prepareStatement(queryDailyFrozenSubscribers);
        dailyFrozenStmt.setString(1, month);
        dailyFrozenStmt.setString(2, year);

        ResultSet dailyFrozenResultSet = dailyFrozenStmt.executeQuery();

        // Populate days and calculate frozen percent
        int totalFrozenSubscribers = 0;
        int totalDaysWithData = 0;
        ArrayList<DayReport> dayReports = new ArrayList<>(); // To store data for saving later

        while (dailyFrozenResultSet.next()) {
            String day = dailyFrozenResultSet.getString("day");
            int frozenCount = dailyFrozenResultSet.getInt("frozen_count");
            totalFrozenSubscribers += frozenCount;
            totalDaysWithData++;
            days.add(day + " (Frozen: " + frozenCount + ")");
            dayReports.add(new DayReport(day, month, year, frozenCount)); // Storing for later saving
        }

        // Calculate frozen percent as the average number of frozen subscribers per day
        frozenPercent = totalDaysWithData > 0 ? (double) totalFrozenSubscribers / totalDaysWithData : 0.0;

        // Step 3: Save the calculated data into the subscriberstatusreport table
        String insertReportQuery = """
            INSERT INTO db.subscriberstatusreport (day, month, year, frozenpercent, frozen_subscribers) 
            VALUES (?, ?, ?, ?, ?)
        """;
        PreparedStatement insertStmt = conn.prepareStatement(insertReportQuery);

        for (DayReport dayReport : dayReports) {
            insertStmt.setString(1, dayReport.day);
            insertStmt.setString(2, dayReport.month);
            insertStmt.setString(3, dayReport.year);
            insertStmt.setDouble(4, frozenPercent);
            insertStmt.setInt(5, dayReport.frozenSubscribers);
            insertStmt.addBatch(); // Add to batch for bulk insert
        }

        // Execute batch insert
        insertStmt.executeBatch();

        // Return the newly calculated report
        return new SubscriberStatusReport(report.getReportType(), month, year, days, frozenPercent);
    }

    /**
     * Helper class to store daily frozen subscriber data before saving it to the database.
     */
    private static class DayReport {
    	/**
         * The day of the month.
         */
        String day;
        /**
         * The month of the report.
         */
        String month;
        /**
         * The year of the report.
         */
        String year;
        /**
         * The number of frozen subscribers for the day.
         */
        int frozenSubscribers;
        /**
         * Constructs a new DayReport instance.
         * @param day the day of the report.
         * @param month the month of the report.
         * @param year the year of the report.
         * @param frozenSubscribers the number of frozen subscribers.
         */
        DayReport(String day, String month, String year, int frozenSubscribers) {
            this.day = day;
            this.month = month;
            this.year = year;
            this.frozenSubscribers = frozenSubscribers;
        }
    }
    /**
     * Generates a borrowed book report for a given month and year.
     * Calculates loan duration distribution and delay statistics for the specified period.
     * @param msg a message object containing the report details (month and year).
     * @return the generated borrowed book report.
     * @throws SQLException if a database access error occurs.
     */
    public static BorrowedBookReport viewBorrowedBookReport(ClientServerContact msg) throws SQLException {
        Report report = (Report) msg.getObj1();
        String month = report.getMonth();
        String year = report.getYear();

        // Query to calculate data from the borrow table
        String calculateDataQuery = """
        	    SELECT 
        	        CASE 
        	            WHEN DATEDIFF(b.due_date, b.borrow_date) BETWEEN 1 AND 5 THEN '1-5 days'
        	            WHEN DATEDIFF(b.due_date, b.borrow_date) BETWEEN 6 AND 10 THEN '6-10 days'
        	            WHEN DATEDIFF(b.due_date, b.borrow_date) BETWEEN 11 AND 15 THEN '11-15 days'
        	            WHEN DATEDIFF(b.due_date, b.borrow_date) BETWEEN 16 AND 20 THEN '16-20 days'
        	            ELSE '21+ days'
        	        END AS loan_duration_range,
        	        CASE 
        	            WHEN DATEDIFF(r.actual_return_date, b.due_date) > 0 THEN CONCAT(DATEDIFF(r.actual_return_date, b.due_date), ' days late')
        	            ELSE 'On time'
        	        END AS delay_range,
        	        COUNT(*) AS count
        	    FROM db.borrow b
        	    LEFT JOIN db.return r ON b.id = r.borrow_id
        	    WHERE MONTH(b.borrow_date) = ? AND YEAR(b.borrow_date) = ?
        	    GROUP BY loan_duration_range, delay_range;
        	""";


        PreparedStatement calculateStmt = conn.prepareStatement(calculateDataQuery);
        calculateStmt.setString(1, month);
        calculateStmt.setString(2, year);

        ResultSet dataResultSet = calculateStmt.executeQuery();
        Map<String, Integer> loanDurationDistribution = new HashMap<>();
        Map<String, Integer> delayDistribution = new HashMap<>();

        while (dataResultSet.next()) {
            String loanDurationRange = dataResultSet.getString("loan_duration_range");
            String delayRange = dataResultSet.getString("delay_range");
            int count = dataResultSet.getInt("count");

            loanDurationDistribution.put(loanDurationRange, loanDurationDistribution.getOrDefault(loanDurationRange, 0) + count);
            delayDistribution.put(delayRange, delayDistribution.getOrDefault(delayRange, 0) + count);
        }

        // Return the calculated report
        return new BorrowedBookReport(report.getReportType(), month, year, loanDurationDistribution, delayDistribution);
    }
    
    
    /**
     * Handles the return process for a borrowed book, including updating the return status,
     * handling late returns, deleting reminders, and managing orders and copy statuses.
     *
     * @param barcode The barcode of the book being returned.
     * @return A message indicating the result of the return process.
     * @throws SQLException If a database error occurs.
     */
    public static String returnBook(String barcode) throws SQLException {
        Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());
        int borrowId = getActiveBorrowId(barcode);

        if (borrowId == -1 || !isBorrowActive(borrowId)) {
            return "Book is not Borrowed";
        }

        processReturn(borrowId, timestamp);
        BorrowDetails borrowDetails = getBorrowDetails(borrowId);
        handleLateReturn(borrowDetails, timestamp);
        if(borrowDetails.getDueDate().compareTo(timestamp) > 0)
        	deleteReminderMessage(borrowDetails);
        handleOrdersAndCopyStatus(borrowDetails, timestamp);

        return "Book returned successfully.";
    }
    /**
     * Retrieves the active borrow ID for a book, based on its barcode.
     *
     * @param barcode The barcode of the book.
     * @return The active borrow ID, or -1 if no active borrow is found.
     * @throws SQLException If a database error occurs.
     */
    private static int getActiveBorrowId(String barcode) throws SQLException {
        String query = """
            SELECT b.id AS borrow_id
            FROM borrow b
            WHERE b.copy_id = (
                SELECT c.id
                FROM CopyOfBook c
                WHERE c.barcode = ?
            ) 
            AND b.status = 'active';
        """;

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, barcode);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("borrow_id");
            }
        }
        return -1;
    }
    /**
     * Processes the return of a borrowed book by inserting the return record and updating the borrow status.
     *
     * @param borrowId The ID of the borrow record.
     * @param timestamp The timestamp of the return.
     * @throws SQLException If a database error occurs.
     */
    private static void processReturn(int borrowId, Timestamp timestamp) throws SQLException {
        String insertQuery = "INSERT INTO db.return (borrow_id, actual_return_date) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
            pstmt.setInt(1, borrowId);
            pstmt.setTimestamp(2, timestamp);
            pstmt.executeUpdate();
        }

        String updateQuery = "UPDATE db.borrow SET status = 'finish' WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {
            pstmt.setInt(1, borrowId);
            pstmt.executeUpdate();
        }
    }
    /**
     * Retrieves the details of a borrow record, including copy ID, due date, and user ID.
     *
     * @param borrowId The ID of the borrow record.
     * @return A BorrowDetails object containing the borrow details.
     * @throws SQLException If a database error occurs.
     */
    private static BorrowDetails getBorrowDetails(int borrowId) throws SQLException {
        String query = "SELECT b.copy_id, b.due_date, b.user_id FROM db.borrow b WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, borrowId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new BorrowDetails(
                    rs.getInt("copy_id"),
                    rs.getDate("due_date"),
                    rs.getInt("user_id")
                );
            }
        }
        return null;
    }
    /**
     * Handles late return logic, including freezing the subscriber or incrementing their late return count.
     *
     * @param details The details of the borrow record.
     * @param timestamp The timestamp of the return.
     * @throws SQLException If a database error occurs.
     */
    private static void handleLateReturn(BorrowDetails details, Timestamp timestamp) throws SQLException {
        long differenceInDays = Math.abs(details.dueDate.getTime() - timestamp.getTime()) / (1000 * 60 * 60 * 24);
        System.out.println("Answer is: " + differenceInDays);
        if (details.dueDate.getTime() < timestamp.getTime()) { // Due date is before the timestamp
            if (differenceInDays >= 7) {
                freezeSubscriber(details.userId);
            } else if (differenceInDays > 0) {
                incrementLateReturns(details.userId);
            }
        }
    }
    /**
     * Freezes a subscriber for late returns exceeding 7 days.
     *
     * @param userId The ID of the subscriber to freeze.
     * @throws SQLException If a database error occurs.
     */
    private static void freezeSubscriber(int userId) throws SQLException {
        String updateQuery = "UPDATE db.subscriber SET status = 'frozen', late_returns = late_returns + 1 WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
        }

        String insertQuery = "INSERT INTO db.frozen (subscriber_id, fromdate, todate) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
            pstmt.setInt(1, userId);
            pstmt.setDate(2, java.sql.Date.valueOf(LocalDate.now()));
            pstmt.setDate(3, java.sql.Date.valueOf(LocalDate.now().plusMonths(1)));
            pstmt.executeUpdate();
        }
    }
    /**
     * Increments the late return count for a subscriber.
     *
     * @param userId The ID of the subscriber.
     * @throws SQLException If a database error occurs.
     */
    private static void incrementLateReturns(int userId) throws SQLException {
        String query = "UPDATE db.subscriber SET late_returns = late_returns + 1 WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
        }
    }
    /**
     * Deletes a reminder message related to a borrow record.
     *
     * @param details The details of the borrow record.
     * @throws SQLException If a database error occurs.
     */
    private static void deleteReminderMessage(BorrowDetails details) throws SQLException {
        String query = "DELETE FROM db.msg WHERE msgType = 'Reminder' AND msgTo = ? AND msg = ? AND msgDate = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, details.userId);
            pstmt.setString(2, "Tommorrow, you have to return your borrowed book, copy id: " + details.copyId);
            pstmt.setDate(3, getYesterday(details.getDueDate()));
            pstmt.executeUpdate();
        }
    }
    /**
     * Handles the logic for orders and updating copy statuses during the return process.
     *
     * @param details The details of the borrow record.
     * @param timestamp The timestamp of the return.
     * @throws SQLException If a database error occurs.
     */
    private static void handleOrdersAndCopyStatus(BorrowDetails details, Timestamp timestamp) throws SQLException {
        if (isOrderActive(details.copyId)) {
            updateOrderStatus(details.copyId,timestamp);
            notifyNextUser(details.copyId, timestamp);
        } else {
            updateCopyStatusToAvailable(details.copyId);
        }
    }
    /**
     * Checks if there is an active order for a specific book copy.
     *
     * @param copyId The ID of the book copy.
     * @return True if an active order exists, false otherwise.
     * @throws SQLException If a database error occurs.
     */
    private static boolean isOrderActive(int copyId) throws SQLException {
        String query = "SELECT EXISTS (SELECT 1 FROM db.order o WHERE o.copy_id = ? AND o.order_status = 'pending') AS order_exists";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, copyId);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() && rs.getInt("order_exists") == 1;
        }
    }
    /**
     * Updates the order status to 'delivered' and sets the copy status to 'Reserved'.
     *
     * @param copyId The ID of the book copy.
     * @param timestamp The timestamp of the return.
     * @throws SQLException If a database error occurs.
     */
    private static void updateOrderStatus(int copyId,Timestamp timestamp) throws SQLException {
    	String updateOrderQuery = "UPDATE db.order SET order_status = 'delivered', delivered_date = ? WHERE copy_id = ?";
    	try (PreparedStatement pstmt = conn.prepareStatement(updateOrderQuery)) {
    	    pstmt.setTimestamp(1, timestamp);
    	    pstmt.setInt(2, copyId);
    	    pstmt.executeUpdate();
    	}

        String updateCopyQuery = "UPDATE db.copyofbook SET status = 'Reserved' WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(updateCopyQuery)) {
            pstmt.setInt(1, copyId);
            pstmt.executeUpdate();
        }
    }
    /**
     * Notifies the next user in the order queue about the availability of the book copy.
     *
     * @param copyId The ID of the book copy.
     * @param timestamp The timestamp of the return.
     * @throws SQLException If a database error occurs.
     */
    private static void notifyNextUser(int copyId, Timestamp timestamp) throws SQLException {
        String query = "SELECT user_id FROM db.order WHERE copy_id = ?";
        int nextUserId = -1;
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, copyId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                nextUserId = rs.getInt("user_id");
            }
        }

        String insertMessageQuery = "INSERT INTO db.msg (msgTo, msgType, msg, isRead, msgDate) VALUES (?, 'Book Arrival', ?, 0, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(insertMessageQuery)) {
            pstmt.setInt(1, nextUserId);
            pstmt.setString(2, "The book you ordered is waiting for you until 2 days, copy id: " + copyId);
            pstmt.setTimestamp(3, timestamp);
            pstmt.executeUpdate();
        }
    }
    /**
     * Updates the status of a book copy to 'Available'.
     *
     * @param copyId The ID of the book copy to update.
     * @throws SQLException If a database error occurs.
     */
    private static void updateCopyStatusToAvailable(int copyId) throws SQLException {
        String query = "UPDATE db.copyofbook SET status = 'Available' WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, copyId);
            pstmt.executeUpdate();
        }
    }
    /**
     * A helper class to store borrow details including copy ID, due date, and user ID.
     */
    static class BorrowDetails {
        private final int copyId;
        private final Date dueDate;
        private final int userId;
        private final LocalDate LocaldueDate;
        /**
         * Constructs a BorrowDetails instance with a Date due date.
         *
         * @param copyId The ID of the borrowed copy.
         * @param dueDate The due date of the borrow (as a Date).
         * @param userId The ID of the user who borrowed the copy.
         */
        public BorrowDetails(int copyId, Date dueDate, int userId) {
            this.copyId = copyId;
            this.dueDate = dueDate;
            this.userId = userId;
			this.LocaldueDate = null;
        }
        /**
         * Constructs a BorrowDetails instance with a LocalDate due date.
         *
         * @param copyId The ID of the borrowed copy.
         * @param dueDate The due date of the borrow (as a LocalDate).
         * @param userId The ID of the user who borrowed the copy.
         */
        public BorrowDetails(int copyId, LocalDate dueDate, int userId) {
            this.copyId = copyId;
			this.dueDate = null;
            this.LocaldueDate = dueDate;
            this.userId = userId;
        }
        /**
         * Gets the copy ID of the item.
         * 
         * @return the copy ID.
         */
        public int getCopyId() {
            return copyId;
        }
        /**
         * Gets the due date of the item.
         * 
         * @return the due date.
         */
        public Date getDueDate() {
            return dueDate;
        }
        /**
         * Gets the local due date of the item.
         * 
         * @return the local due date.
         */
        public LocalDate getLocalDueDate() {
            return LocaldueDate;
        }
        /**
         * Gets the user ID associated with the item.
         * 
         * @return the user ID.
         */
        public int getUserId() {
            return userId;
        }
    }

    
    /**
     * Checks if a borrow record is active.
     *
     * @param borrowId The ID of the borrow record to check.
     * @return True if the borrow record is active, false otherwise.
     * @throws SQLException If a database error occurs.
     */
    private static boolean isBorrowActive(int borrowId) throws SQLException {
        String checkStatusQuery = "SELECT status FROM db.borrow WHERE id = ?";

        // Check if the borrow status is 'active' for the given borrowId
        try (PreparedStatement checkStatusStmt = conn.prepareStatement(checkStatusQuery)) {
            checkStatusStmt.setInt(1, borrowId);
            try (ResultSet rs = checkStatusStmt.executeQuery()) {
                if (rs.next()) {
                    String status = rs.getString("status"); // Get the status of the borrow
                    return "active".equalsIgnoreCase(status); // Return true if the status is 'active'
                } else {
                    System.out.println("No borrow record found for the given borrowId: " + borrowId);
                    return false; // If no record is found, return false
                }
            }
        }
    }
    /**
     * Inserts a message into the database.
     *
     * @param userId The recipient's user ID.
     * @param messageType The type of the message.
     * @param messageContent The content of the message.
     * @param messageDate The date of the message.
     * @throws SQLException If a database error occurs.
     */
    public static void insertMessage(int userId, String messageType, String messageContent, Date messageDate) throws SQLException {
        String MessageQuery = "INSERT INTO db.msg (msgTo, msgType, msg, isRead, msgDate) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement InsertReminderMessageQuery = conn.prepareStatement(MessageQuery)) {
            // Set parameters for the query
            InsertReminderMessageQuery.setInt(1, userId);             // Set recipient user_id
            InsertReminderMessageQuery.setString(2, messageType);      // Set message type (e.g., "Reminder")
            InsertReminderMessageQuery.setString(3, messageContent);   // Set message content (e.g., reminder text)
            InsertReminderMessageQuery.setBoolean(4, false);           // Set 'isRead' to false
            InsertReminderMessageQuery.setDate(5, messageDate);        // Set the message date
            // Debugging: Print out the query to see it
            System.out.println("Executing query: " + InsertReminderMessageQuery);
            // Execute the insert query
            InsertReminderMessageQuery.executeUpdate();
        }
    }
    /**
     * Marks a book as lost.
     *
     * @param barcode The barcode of the book.
     * @return A message indicating the result of the operation.
     * @throws SQLException If a database error occurs.
     */
    public static String lostBook(String barcode) throws SQLException {
        Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());
        int borrowId = getActiveBorrowId(barcode);
        if (borrowId == -1 || !isBorrowActive(borrowId)) {
            return "Book is not Borrowed";
        }

        BorrowDetails borrowDetails = getBorrowDetails(borrowId);
        if (borrowDetails == null) {
            return "Borrow details not found.";
        }

        if (isOrderActive(borrowDetails.getCopyId())) {
            handleActiveOrder(borrowDetails);
        }

        updateCopyStatusToLost(borrowDetails.getCopyId());
        decrementBookCopies(borrowDetails.getCopyId());
        handleSubscriberLateReturn(borrowDetails.getUserId());
        finishBorrowRecord(borrowId);
        if(borrowDetails.getDueDate().compareTo(timestamp) > 0)
        	deleteReminderMessage(borrowDetails);

        return "Marked as lost.";
    }
    /**
     * Updates the status of a book copy to 'Lost'.
     *
     * @param copyId The ID of the book copy.
     * @throws SQLException If a database error occurs.
     */
    private static void updateCopyStatusToLost(int copyId) throws SQLException {
        String query = "UPDATE copyofbook SET status = 'Lost' WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, copyId);
            pstmt.executeUpdate();
        }
    }
    
    /**
     * Decrements the total number of copies for a book.
     *
     * @param copyId The ID of the book copy.
     * @throws SQLException If a database error occurs.
     */
	private static void decrementBookCopies(int copyId) throws SQLException {
	    String query = "UPDATE book SET copies = copies - 1 WHERE id = (SELECT book_id FROM copyofbook WHERE id = ?)";
	    try (PreparedStatement pstmt = conn.prepareStatement(query)) {
	        pstmt.setInt(1, copyId);
	        pstmt.executeUpdate();
	    }
	}
	/**
	 * Handles late returns for a subscriber by freezing their account.
	 *
	 * @param userId The ID of the subscriber.
	 * @throws SQLException If a database error occurs.
	 */
	private static void handleSubscriberLateReturn(int userId) throws SQLException {
	    String query = "UPDATE subscriber SET status = 'frozen' WHERE id = ?";
	    try (PreparedStatement pstmt = conn.prepareStatement(query)) {
	        pstmt.setInt(1, userId);
	        pstmt.executeUpdate();
	    }
	}
	/**
	 * Marks a borrow record as finished.
	 *
	 * @param borrowId The ID of the borrow record.
	 * @throws SQLException If a database error occurs.
	 */
	private static void finishBorrowRecord(int borrowId) throws SQLException {
	    String query = "UPDATE borrow SET status = 'finish' WHERE id = ?";
	    try (PreparedStatement pstmt = conn.prepareStatement(query)) {
	        pstmt.setInt(1, borrowId);
	        pstmt.executeUpdate();
	    }
	}
	/**
	 * Handles active orders for a lost book by notifying the user and canceling the order.
	 *
	 * @param borrowDetails The details of the borrow record.
	 * @throws SQLException If a database error occurs.
	 */
	private static void handleActiveOrder(BorrowDetails borrowDetails) throws SQLException {
	    String messageContent = "The book you ordered (" + borrowDetails.getCopyId() + ") was marked as lost. Please try to order again.";
	    Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());
	    insertMessage(borrowDetails.getUserId(), "lost book", messageContent, new Date(timestamp.getTime()));

	    String query = "UPDATE db.order SET order_status = 'cancelled' WHERE copy_id = ?";
	    try (PreparedStatement pstmt = conn.prepareStatement(query)) {
	        pstmt.setInt(1, borrowDetails.getCopyId());
	        pstmt.executeUpdate();
	    }
	}

	/**
	 * Adds a subscriber to the database after validation.
	 *
	 * @param msg The message object containing the subscriber information.
	 * @param client The client connection to send responses to.
	 * @throws IOException If an I/O error occurs.
	 * @throws SQLException If a database error occurs.
	 */
    public static void addSubscriberToDB(Object msg, ConnectionToClient client) throws IOException, SQLException {
        if (msg instanceof UserWithSubscriber) {
            UserWithSubscriber userWithSubscriber = (UserWithSubscriber) msg;
            User user = userWithSubscriber.getUser();
            Subscriber subscriber = userWithSubscriber.getSubscriber();

            try {
                // Start a transaction
                conn.setAutoCommit(false);
                
                String checkQuery = "SELECT u.id AS userId, u.username AS userName, s.email AS userEmail FROM db.user u LEFT JOIN db.subscriber s ON u.id = s.id WHERE u.id = ? OR u.username = ? OR s.email = ?";
                try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                    checkStmt.setInt(1, user.getId());
                    checkStmt.setString(2, user.getUsername());
                    checkStmt.setString(3, subscriber.getEmail());

                    try (ResultSet rs = checkStmt.executeQuery()) {
                        if (rs.next()) {
                            StringBuilder errorMessage = new StringBuilder();
                            
                            // Use the column aliases here
                            if (rs.getInt("userId") == user.getId()) 
                                errorMessage.append("ID ");
                            if (rs.getString("userName") != null && rs.getString("userName").equals(user.getUsername())) 
                                errorMessage.append("Username ");
                            if (rs.getString("userEmail") != null && rs.getString("userEmail").equals(subscriber.getEmail())) 
                                errorMessage.append("Email ");
                            errorMessage.append("is used, choose another one");
                            client.sendToClient(errorMessage.toString().trim());
                            return;
                        }
                    }
                }

                // Insert the user
                String userQuery = "INSERT INTO db.user (id, username, password, first_name, last_name, user_type) " +
                        "VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement userStmt = conn.prepareStatement(userQuery)) {
                    userStmt.setInt(1, user.getId());
                    userStmt.setString(2, user.getUsername());
                    userStmt.setString(3, user.getPassword());
                    userStmt.setString(4, user.getFirstName());
                    userStmt.setString(5, user.getLastName());
                    userStmt.setString(6, "subscriber");
                    userStmt.executeUpdate();
                }

                // Insert the subscriber
                String subscriberQuery = "INSERT INTO db.subscriber (id, status, email, late_returns, phone_number) " +
                        "VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement subscriberStmt = conn.prepareStatement(subscriberQuery)) {
                    subscriberStmt.setInt(1, subscriber.getSubscriberId());
                    subscriberStmt.setString(2, subscriber.getStatus());
                    subscriberStmt.setString(3, subscriber.getEmail());
                    subscriberStmt.setInt(4, subscriber.getLateReturns());
                    subscriberStmt.setString(5, subscriber.getPhone_number());
                    subscriberStmt.executeUpdate();
                }
                // Commit the transaction
                conn.commit();
                client.sendToClient("Added Successfully");
            } catch (SQLException e) {
                try {
                    conn.rollback();
                    client.sendToClient("Failed to add subscriber: " + e.getMessage());
                } catch (IOException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
                e.printStackTrace();
            } finally {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } else {
            try {
                client.sendToClient("Invalid data received. Expected UserWithSubscriber object.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
}
