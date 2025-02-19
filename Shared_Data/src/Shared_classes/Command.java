package Shared_classes;

/**
 * Enum representing various commands that can be used for communication between client and server.
 * Each command corresponds to a specific action or request that the client may send to the server, or the server may respond with.
 * 
 * The actions range from displaying data (e.g., subscribers, books, orders) to handling specific tasks (e.g., borrowing a book, updating a subscriber, etc.).
 * 
 * @author Majd Awad
 * @version 1.1dz
 */
public enum Command {

    /**
     * Command to display the list of subscribers.
     */
    DisplaySubscribers,

    /**
     * Command to display the list of borrowed books.
     */
    DisplayBorrows,

    /**
     * Command to display the list of book orders.
     */
    DisplayOrders,

    /**
     * Command to disconnect the current client-server session.
     */
    Disconnect,

    /**
     * Command for user login.
     */
    Login,

    /**
     * Command to display the list of books in the library.
     */
    DisplayBooks,

    /**
     * Command to add a new subscriber to the system.
     */
    AddSubscriber,

    /**
     * Command to show the details of a subscriber, typically in the form of a subscriber card.
     */
    ShowSubscriberCard,

    /**
     * Command to update subscriber details.
     */
    UpdateSubscriber,

    /**
     * Command to fetch the borrow history of a user.
     */
    FetchBorrowHistory,

    /**
     * Command to fetch the order history of a user.
     */
    FetchOrderHistory,

    /**
     * Command to fetch the return history of a user.
     */
    FetchReturnHistory,

    /**
     * Command to borrow a book from the library.
     */
    BorrowBook,

    /**
     * Command to retrieve all messages for the current user.
     */
    GetMessages,

    /**
     * Command to fetch the count of unread messages.
     */
    GetUnreadMessagesCount,

    /**
     * Command to mark a message as read.
     */
    MarkMessageAsRead,

    /**
     * Command to view subscription-related reports.
     */
    ViewSubscriptionReport,

    /**
     * Command to view the borrowed books report.
     */
    ViewBorrowedReport,

    /**
     * Command to display the list of returned books.
     */
    DisplayReturns,

    /**
     * Command to return a borrowed book to the library.
     */
    ReturnBook,

    /**
     * Command to mark a book as lost.
     */
    LostBook,

    /**
     * Command to create a new book order in the system.
     */
    CreateOrder,

    /**
     * Command to get detailed information about a specific book.
     */
    BookDetails,

    /**
     * Command to check if a book has been returned or ordered.
     */
    isReturned_isOrderd,

    /**
     * Command for manual extension of a borrowed book's due date.
     */
    manual_Extension
}
