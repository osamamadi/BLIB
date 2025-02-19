package Shared_classes;

/**
 * The `userType` enum defines the different types of users in the system.
 * It helps categorize users as either Librarians or Subscribers, 
 * with each type having its own set of permissions and roles within the system.
 * 
 * 
 * Librarian: Represents a user with administrative access to the library system.
 * Subscriber:Represents a regular user who can borrow books and interact with library services.
 * 
 * 
 * This enum is used in the User class to determine the role of the user.
 * 
 * @author Majd Awad
 * @version 1.0
 */
public enum userType {
    /**
     * Represents a librarian who has administrative privileges.
     */
    Librarian,

    /**
     * Represents a subscriber who has basic access to the library system.
     */
    Subscriber,
}
