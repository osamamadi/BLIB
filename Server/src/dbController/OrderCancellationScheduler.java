package dbController;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;

import server.BraudeLibServer;
/**
 * The OrderCancellationScheduler class is responsible for scheduling and executing
 * the task of canceling expired orders and updating the corresponding copies in the system.
 * 
 * @author YourName
 * @version 1.0
 */
public class OrderCancellationScheduler {
	/** The database connection instance used for interacting with the database. */

    private static final Connection conn = BraudeLibServer.getConnection();
 
    /**
     * Constructs a new OrderCancellationScheduler.
     */
    public OrderCancellationScheduler() {
    }
    
    /**
     * Starts the scheduler that periodically checks for expired orders and cancels them.
     * The task runs every 10 seconds.
     */
    public static void startScheduler() {
        Timer timer = new Timer(true); // Daemon thread
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    cancelExpiredOrdersAndUpdateCopies();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }, 0, 10000);
    }
    /**
     * Cancels expired orders and updates the corresponding copies in the system.
     * An order is considered expired if it is marked as 'delivered' and was delivered
     * more than two days ago.
     * 
     * @throws SQLException if a database access error occurs.
     */
    private static void cancelExpiredOrdersAndUpdateCopies() throws SQLException {
        String selectExpiredOrdersQuery = """
            SELECT `id`
            FROM `order`
            WHERE `order_status` = 'delivered'
              AND `delivered_date` <= DATE_SUB(CURDATE(), INTERVAL 2 DAY)
        """;

        try (PreparedStatement selectStmt = conn.prepareStatement(selectExpiredOrdersQuery);
             ResultSet rs = selectStmt.executeQuery()) {

            while (rs.next()) {
                int orderId = rs.getInt("id");
                OrderManager.cancelOrderAndUpdateCopy(orderId);
            }
        }
    }
}
