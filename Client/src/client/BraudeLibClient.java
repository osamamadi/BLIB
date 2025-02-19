package client;

import ocsf.client.*;

import subscriberGui.ClientSubPersonalCardController;
import commonGui.*;
import librarianGui.ClientLibAddSubController;
import librarianGui.ClientLibBorrowBookController;
import librarianGui.ClientLibBorrowedDurationReportsController;
import librarianGui.ClientLibDisplaySubController;
import librarianGui.ClientLibHomeController;
import librarianGui.ClientLibPersonalSubCardController;
import librarianGui.ClientLibReturnBookController;
import librarianGui.ClientLibSubscribtionStatusReportsController;
import commonGui.ClientDisplayBooksController;
import subscriberGui.ClientSubHomeController;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import Shared_classes.Report;
import Shared_classes.Message;
import Shared_classes.UserWithSubscriber;
 
/**
 * The BraudeLibClient class represents a client that connects to the Braude Library system.
 * It communicates with the server and handles messages for various controllers in the application.
 *  @author Your Name
 * @version 1.0
 */
public class BraudeLibClient extends AbstractClient implements ChatIF {

    /**
     * The current controller handling the client's operations.
     */
    private Object Controller;

    /**
     * Default port for the client-server communication.
     */
    public static final int DEFAULT_PORT = 5555;

    /**
     * Interface for displaying messages to the user.
     */
    private ChatIF clientUI;

    /**
     * The selected report.
     */
    public static Report chosenReport;

    /**
     * Constructs a new BraudeLibClient.
     * 
     * @param host the hostname or IP address of the server
     * @param port the port number to connect to
     */
    public BraudeLibClient(String host, int port) {
        super(host, port); // Call the superclass constructor
        this.clientUI = this;
        try {
            openConnection();
        } catch (IOException e) {
            System.out.println("Can't open connection.");
        }
    }

    /**
     * Sets the current controller to handle operations.
     * 
     * @param Controller the controller to set
     */
    public void setController(Object Controller) {
        this.Controller = Controller;
    }

    /**
     * Handles messages received from the server.
     * The message is passed to the appropriate controller for further processing.
     * 
     * @param msg the message received from the server
     */
    @Override
    public void handleMessageFromServer(Object msg) {
        if (Controller != null) {
            if (Controller instanceof ClientLibAddSubController)
                ((ClientLibAddSubController) Controller).inputResponse(msg.toString());
            else if (Controller instanceof ClientDisplayReturnsController)
                ((ClientDisplayReturnsController) Controller).inputResponse(msg);
            else if (Controller instanceof ClientShowMessagesController)
                ((ClientShowMessagesController) Controller).displayMessages((List<Message>) msg);
            else if (Controller instanceof ClientSubPersonalCardController)
                ((ClientSubPersonalCardController) Controller).inputResponse(msg);
            else if (Controller instanceof ClientLibPersonalSubCardController)
                ((ClientLibPersonalSubCardController) Controller).inputResponse(msg.toString());
            else if (Controller instanceof ClientLibDisplaySubController)
                ((ClientLibDisplaySubController) Controller).inputResponse(msg);
            else if (Controller instanceof ClientLibBorrowBookController)
                ((ClientLibBorrowBookController) Controller).inputResponse(msg.toString());
            else if (Controller instanceof ClientLibReturnBookController)
                ((ClientLibReturnBookController) Controller).inputResponse(msg.toString());
            else if (Controller instanceof ClientDisplayOrdersController)
                ((ClientDisplayOrdersController) Controller).inputResponse(msg);
            else if (Controller instanceof ClientDisplayBorrowsController)
                ((ClientDisplayBorrowsController) Controller).inputResponse(msg);
            else if (Controller instanceof ClientLibHomeController) {
                if (msg instanceof Integer) {
                    ((ClientLibHomeController) Controller).updateMessageCount((int) msg);
                } else {
                    ((ClientLibHomeController) Controller).inputResponse(msg.toString());
                }
            } else if (Controller instanceof ClientCommonHomeController)
                ((ClientCommonHomeController) Controller).inputResponse(msg);
            else if (Controller instanceof ClientSubHomeController) {
                if (msg instanceof Integer) {
                    ((ClientSubHomeController) Controller).updateMessageCount((int) msg);
                } else {
                    ((ClientSubHomeController) Controller).inputResponse(msg.toString());
                }
            } else if (Controller instanceof ClientDisplayBooksController)
                ((ClientDisplayBooksController) Controller).inputResponse(msg);
            else if (Controller instanceof ClientLibSubscribtionStatusReportsController)
                ((ClientLibSubscribtionStatusReportsController) Controller).inputResponse(msg.toString());
            else if (Controller instanceof ClientLibBorrowedDurationReportsController)
                ((ClientLibBorrowedDurationReportsController) Controller).inputResponse(msg.toString());
        }
    }

    /**
     * Sends a request to the server.
     * 
     * @param Request the request to send
     */
    public void requestFromServer(Object Request) {
        try {
            System.out.println("Controller is : " + Controller);
            if (isConnected()) {
                sendToServer(Request);
            } else {
                System.exit(1);
            }
        } catch (IOException e) {
            System.out.println("Error sending request to server: " + e.getMessage());
        }
    }

    /**
     * Displays a message. (Currently empty implementation)
     * 
     * @param message the message to display
     */
    @Override
    public void display(String message) {
    }

    /**
     * Closes the connection to the server and exits the application.
     */
    public void quit() {
        try {
            closeConnection();
        } catch (IOException e) {
            // Ignore
        }
        System.exit(0);
    }
}
