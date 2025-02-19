package dbController;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import server.BraudeLibServer;
/**
 * The OrderManager class provides functionality for managing orders in the library system,
 * specifically for canceling orders and updating the status of the associated book copies.
 * 
 * @author YourName
 * @version 1.0
 */
public class OrderManager {
	/** The database connection instance used for interacting with the database. */
    private static final Connection conn = BraudeLibServer.getConnection();
    /**
     * Constructs a new OrderManager.
     */
    public OrderManager() {
    }
    /**
     * Cancels the specified order and updates the status of the associated book copy to 'Available'.
     * The order is canceled by updating its status in the database, and the corresponding copy's status is updated.
     * 
     * @param orderId the ID of the order to cancel.
     * @throws SQLException if a database access error occurs.
     */
    public static void cancelOrderAndUpdateCopy(int orderId) throws SQLException {
        String cancelOrderQuery = "UPDATE `order` SET `order_status` = 'cancelled' WHERE `id` = ?";
        String updateCopyStatusQuery = """
            UPDATE `copyofbook`
            SET `status` = 'Available'
            WHERE `id` = (
                SELECT `copy_id`
                FROM `order`
                WHERE `id` = ? AND `order_status` = 'cancelled'
            )
        """;

        try (PreparedStatement cancelStmt = conn.prepareStatement(cancelOrderQuery);
             PreparedStatement updateCopyStmt = conn.prepareStatement(updateCopyStatusQuery)) {

            // Cancel the order
            cancelStmt.setInt(1, orderId);
            int rowsUpdated = cancelStmt.executeUpdate();

            if (rowsUpdated > 0) {
                // Update the copy status if the order was successfully canceled
                updateCopyStmt.setInt(1, orderId);
                updateCopyStmt.executeUpdate();
                System.out.println("Order canceled and copy updated to Available.");
            } else {
                System.out.println("No matching order found to cancel.");
            }
        }
    }
}

