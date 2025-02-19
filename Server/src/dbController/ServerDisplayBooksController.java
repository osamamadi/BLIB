package dbController;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import Shared_classes.Book;
import server.BraudeLibServer;
import ocsf.server.ConnectionToClient;

/**
 * Controller class for fetching and sending book data to the client.
 * It interacts with the database to retrieve book details and sends it to the client.
 * 
 * @author Your Name
 * @version 1.0
 */
public class ServerDisplayBooksController {
	/** The database connection instance used for interacting with the database. */
    private static Connection conn = BraudeLibServer.getConnection();

 // Default constructor
    /**
     * Default constructor for ServerDisplayBooksController.
     * Initializes the controller instance for handling book display on the server.
     */
    public ServerDisplayBooksController() {
      
    }
    
    /**
     * Fetches all books data from the database and sends it to the client.
     * 
     * @param client The client connection that the data will be sent to.
     */
    public static void fetchBooksFromDB(ConnectionToClient client) {
        // Updated query to include the 'id' field
        String query = "SELECT id, book_name, topic, description, copies FROM db.Book";

        try (PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            List<Book> booksList = new ArrayList<>();

            // Iterate through the result set and create Book objects
            while (rs.next()) {
                int id = rs.getInt("id"); // Fetch 'id' from the database
                String bookName = rs.getString("book_name");
                String topic = rs.getString("topic");
                String description = rs.getString("description");
                int copies = rs.getInt("copies");

                // Create a new Book object with 'id' and add it to the list
                booksList.add(new Book(id, bookName, topic, description, copies));
            }

            // Send the list of books or an error message if no books were found
            if (!booksList.isEmpty()) {
                client.sendToClient(booksList); // Send the list of books to the client
            } else {
                client.sendToClient("No books found in the database.");
            }
        } catch (SQLException e) {
            System.out.println("SQLException occurred while fetching books: " + e.getMessage());
            e.printStackTrace();
            try {
                client.sendToClient("Database error occurred while fetching books.");
            } catch (Exception sendError) {
                System.out.println("Error sending message to client: " + sendError.getMessage());
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
