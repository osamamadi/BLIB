package dbController;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import Shared_classes.User;
import Shared_classes.userType;
import server.BraudeLibServer;
import ocsf.server.ConnectionToClient;

/**
 * Controller class for handling user login requests.
 * It validates the username and password against the database and sends the result to the client.
 * 
 * @author Your Name
 * @version 1.0
 */
public class ServerLoginController {
	/** The database connection instance used for interacting with the database. */
    private static Connection conn = BraudeLibServer.getConnection();

    /**
     * Validates the username and password against the database.
     * 
     * @param obj    The login object containing username and password (e.g., a User object).
     * @param client The client connection to which the result will be sent.
     * @throws Exception If an error occurs during database query or connection.
     */
    public static void loginFromDB(Object obj, ConnectionToClient client) throws Exception {
        // Check if the object is a ClientServerContact
        if (!(obj instanceof Shared_classes.ClientServerContact)) {
            client.sendToClient("Invalid data format");
            System.out.println("Invalid data format received.");
            return;
        }

        // Extract the User object from ClientServerContact
        Shared_classes.ClientServerContact contact = (Shared_classes.ClientServerContact) obj;
        Object data = contact.getObj1();

        // Ensure the extracted object is of type User
        if (!(data instanceof Shared_classes.User)) {
            client.sendToClient("Invalid data format");
            System.out.println("Invalid data format inside ClientServerContact.");
            return;
        }

        Shared_classes.User user = (Shared_classes.User) data;
        int id = -1;
        String username = user.getUsername().trim();
        String password = user.getPassword().trim();
        String first_name = "", last_name = "", user_type = "";
        User u;
        System.out.println("Username: " + username);
        System.out.println("Password: " + password);

        // SQL query to validate the username and password and retrieve user type
        String query = "SELECT * FROM db.user WHERE username = ? AND password = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            System.out.println("Q:" + pstmt);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Extract user details from the ResultSet
                    id = rs.getInt("id");
                    first_name = rs.getString("first_name");
                    last_name = rs.getString("last_name");
                    user_type = rs.getString("user_type");
                    // Create User object based on the user type
                    if (user_type.equals("subscriber")) {
                        u = new User(id, username, password, first_name, last_name, userType.Subscriber);
                    } else {
                        u = new User(id, username, password, first_name, last_name, userType.Librarian);
                    }
                    System.out.println("Server is sending : " + u);
                    // Send User object to the client
                    client.sendToClient(u);
                } else {
                    client.sendToClient("Invalid username or password");
                }
            }
        } catch (SQLException e) {
            System.out.println("SQLException occurred: " + e.getMessage());
            client.sendToClient("Database error occurred during login");
        }
    }
}
