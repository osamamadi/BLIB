package server;

import java.io.IOException;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.function.BiConsumer;

import ocsf.server.AbstractServer;
import ocsf.server.ConnectionToClient;
import dbController.CommonController;
import dbController.LibrarianController;
import dbController.OrderCancellationScheduler;
import serverGui.ServerDisplayController;
import dbController.SubscriberController;
import Shared_classes.BorrowedBookReport;
import Shared_classes.ClientServerContact;
import Shared_classes.Command;
import Shared_classes.Message;
import Shared_classes.Order;
import Shared_classes.Borrow;

import Shared_classes.SubscriberStatusReport;
import Shared_classes.User;
import Shared_classes.UserWithSubscriber;
import dbController.ServerDisplayBooksController;
import dbController.ServerLoginController;

/**
 * This class represents the server that handles client requests in the Braude Library system.
 * It processes various commands from clients, manages database connections, and communicates with the client.
 * @author Your Name
 * @version 1.0
 */
public class BraudeLibServer extends AbstractServer {
	/**
     * The default port number used for client-server communication.
     */
    public static final int DEFAULT_PORT = 5555;

    /**
     * The database URL for connecting to the MySQL database.
     * This includes the hostname, port, database name, and server timezone.
     */
    private static final String DB_URL = "jdbc:mysql://localhost:3306/db?serverTimezone=Asia/Jerusalem";

    /**
     * The database username for authentication.
     */
    private static final String DB_USER = "root";

    /**
     * The database password for authentication.
     */
    private static final String DB_PASSWORD = "Aa123456";

    /**
     * The username used for the database connection (can be customized).
     */
    private static String dbUser;

    /**
     * The password used for the database connection (can be customized).
     */
    private static String dbPassword;

    /**
     * The full database URL, which includes connection credentials.
     */
    private static String fullDBUrl;

    /**
     * The database connection object for interacting with the database.
     */
    private static Connection conn = null;

    /**
     * A callback that handles client details, such as user information or connection parameters.
     * This callback is invoked when client details are received.
     */
    private BiConsumer<String, String> clientDetailsCallback;

    /**
     * A callback that handles client disconnection events.
     * This callback is invoked when a client disconnects from the server.
     */
    private Runnable clientDisconnectionCallback;

    /**
     * Constructor to initialize the server with a specified port and database connection.
     * 
     * @param port The port number the server will listen on.
     */
    public BraudeLibServer(int port) {
        super(port);
        initializeDatabaseConnection();
    }

    /**
     * Constructor to initialize the server with a specified port and database connection details.
     * 
     * @param port The port number the server will listen on.
     * @param dbName The name of the database.
     * @param dbUser The database user.
     * @param dbPassword The database password.
     */
    public BraudeLibServer(int port, String dbName, String dbUser, String dbPassword) {
        super(port);
        BraudeLibServer.dbUser = dbUser;
        BraudeLibServer.dbPassword = dbPassword;
        BraudeLibServer.fullDBUrl = DB_URL + dbName + "?serverTimezone=IST";

        try {
            initializeDatabaseConnection();
        } catch (Exception e) {
            System.out.println("Database connection failed: " + e.getMessage());
        }
    }

    /**
     * Initializes the connection to the database.
     * If a connection does not already exist, it creates one.
     */
    private void initializeDatabaseConnection() {
        if (conn == null) {
            try {
                conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                OrderCancellationScheduler.startScheduler();
                System.out.println("Database connection established.");
            } catch (SQLException e) {
                System.err.println("Failed to establish database connection: " + e.getMessage());
            }
        }
    }

    /**
     * Retrieves the database connection.
     * 
     * @return The current database connection.
     */
    public static Connection getConnection() {
        return conn;
    }

    /**
     * Sets a callback for handling client details.
     * 
     * @param callback The callback function to handle client details.
     */
    public void setClientDetailsCallback(BiConsumer<String, String> callback) {
        this.clientDetailsCallback = callback;
    }

    /**
     * Sets a callback for handling client disconnections.
     * 
     * @param callback The callback function to handle client disconnections.
     */
    public void setClientDisconnectionCallback(Runnable callback) {
        this.clientDisconnectionCallback = callback;
    }

    /**
     * Handles when a client connects to the server.
     * 
     * @param client The client connection.
     */
    @Override
    protected void clientConnected(ConnectionToClient client) {
        super.clientConnected(client);
        String clientHostName = client.getInetAddress().getHostName();
        String clientIpAddress = client.getInetAddress().getHostAddress();

        if (clientDetailsCallback != null) {
            clientDetailsCallback.accept(clientHostName, clientIpAddress);
        }
    }

    /**
     * Handles when a client disconnects from the server.
     * 
     * @param client The client connection.
     */
    @Override
    protected void clientDisconnected(ConnectionToClient client) {
        ServerDisplayController.getInstance().updateClientDisconnectionDetails(client);
        super.clientDisconnected(client);

        if (clientDisconnectionCallback != null) {
            clientDisconnectionCallback.run();
        }
    }

    /**
     * Handles incoming messages from the client. It processes the message based on its command.
     * 
     * @param msg The message object received from the client.
     * @param client The client connection.
     */
    @Override
    protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
        try {
            if (msg instanceof ClientServerContact) {
                ClientServerContact csc = (ClientServerContact) msg;
                Command cmd = csc.getCmd();
                System.out.println("Command received: " + cmd);

                switch (cmd) {
                    case Login:
                        ServerLoginController.loginFromDB(msg, client);
                        break;

                    case DisplayBorrows:
                        CommonController.displayBorrows(client, csc.getObj1());
                        break;

                    case DisplayOrders:
                        CommonController.displayOrders(client, csc.getObj1());
                        break;

                    case ReturnBook:
                        client.sendToClient(LibrarianController.returnBook((String) csc.getObj1()));
                        break;

                    case LostBook:
                        client.sendToClient(LibrarianController.lostBook((String) csc.getObj1()));
                        break;

                    case BorrowBook:
                        LibrarianController.BorrowBook((Borrow) csc.getObj1(), client);
                        break;

                    case DisplaySubscribers:
                        String username = csc.getObj1() instanceof String ? (String) csc.getObj1() : null;
                        CommonController.fetchSubscribersFromDB(client, username);
                        break;

                    case AddSubscriber:
                        LibrarianController.addSubscriberToDB(csc.getObj1(), client);
                        break;

                    case UpdateSubscriber:
                        CommonController.UpdateSubscriber((UserWithSubscriber) (csc.getObj1()), client);
                        break;

                    case BookDetails:
                        client.sendToClient(CommonController.fetchBookAvailability((int) csc.getObj1()));
                        break;

                    case CreateOrder:
                        if (csc.getObj1() instanceof Order) {
                            Order orderDetails = (Order) csc.getObj1(); // Cast obj1 to Order
                            String result = SubscriberController.createOrder(orderDetails.getBookName(), orderDetails.getUserId());
                            client.sendToClient(result);
                        } else {
                            client.sendToClient("Invalid order details.");
                        }
                        break;

                    case isReturned_isOrderd:
                        SubscriberController.isReturned_isOrderd((int) csc.getObj1());
                        break;

                    case manual_Extension:
                        LibrarianController.manual_Extension((int) csc.getObj1(), (User) csc.getObj2());
                        break;

                    case GetUnreadMessagesCount:
                        int unreadCount = LibrarianController.getUnreadMessageCount((int) csc.getObj1());
                        System.out.println("Unread messages count for librarian " + csc.getObj1() + ": " + unreadCount);
                        client.sendToClient(unreadCount);
                        break;

                    case GetMessages:
                        List<Message> messages = LibrarianController.getMessagesForLibrarian((int) csc.getObj1());
                        client.sendToClient(messages);
                        break;

                    case MarkMessageAsRead:
                        LibrarianController.MarkMessageAsRead((Message) csc.getObj1());
                        break;

                    case DisplayBooks:
                        ServerDisplayBooksController.fetchBooksFromDB(client);
                        break;

                    case DisplayReturns:
                        CommonController.displayReturns(client, csc.getObj1());
                        break;

                    case ViewSubscriptionReport:
                        SubscriberStatusReport statusReport = LibrarianController.viewSubscriberStatusReport(csc);
                        System.out.println("status report: " + statusReport.toString());
                        client.sendToClient(statusReport);
                        break;

                    case ViewBorrowedReport:
                        BorrowedBookReport report = LibrarianController.viewBorrowedBookReport(csc);
                        System.out.println("borrowed book report: " + report.toString());
                        client.sendToClient(report);
                        break;

                    default:
                        System.out.println("Unknown command: " + cmd);
                        client.sendToClient("Unknown command: " + cmd);
                        break;
                }
            } else {
                System.out.println("Unrecognized message type: " + msg.getClass().getName());
                client.sendToClient("Unrecognized message type: " + msg.getClass().getName());
            }
        } catch (Exception e) {
            System.err.println("Exception occurred: " + e.getMessage());
            try {
                client.sendToClient("An error occurred while processing your request.");
            } catch (IOException ioException) {
                System.err.println("Failed to send error message to client: " + ioException.getMessage());
            }
        }
    }

    /**
     * Starts the server, listening on the specified port.
     * 
     * @param args Command-line arguments for port configuration.
     */
    public static void main(String[] args) {
        int port = DEFAULT_PORT;
        try {
            port = Integer.parseInt(args[0]);
        } catch (Exception ignored) {
            // Ignore
        }

        BraudeLibServer server = new BraudeLibServer(port);
        try {
            server.listen();
            System.out.println("Server is listening on port " + port);
        } catch (IOException e) {
            System.err.println("ERROR - Could not listen for clients!");
        }
    }
}
