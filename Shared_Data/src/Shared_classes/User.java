package Shared_classes;

import java.io.Serializable;

/**
 * Represents a User object with properties such as personal details and user type.
 * Implements Serializable for object serialization.
 * 
 * The User class is used to store and manage information about users in the system.
 * This includes their username, password, name, and user type (e.g., admin or regular user).
 * 
 * @author Majd Awad
 * @version 1.1dz
 */
public class User implements Serializable {

    private static final long serialVersionUID = 1L; // Add this for version control during serialization
    
    /**
     * The unique identifier for the user (Primary Key).
     */
    private int id;
    
    /**
     * The username of the user for login purposes.
     */
    private String username;
    
    /**
     * The password of the user for authentication.
     */
    private String password;
    
    /**
     * The first name of the user.
     */
    private String firstName;
    
    /**
     * The last name of the user.
     */
    private String lastName;
    
    /**
     * The type of the user (e.g., ADMIN, REGULAR, etc.), defined by the `userType` enum.
     */
    private userType user_type;
    
    /**
     * Default constructor for the User class.
     */
    public User() {}
    
    /**
     * Constructor to initialize the User object with the provided parameters.
     * 
     * @param id The unique identifier for the user.
     * @param username The username of the user.
     * @param password The password of the user.
     * @param firstName The first name of the user.
     * @param lastName The last name of the user.
     * @param user_type The type of the user.
     */
    public User(int id, String username, String password, String firstName, String lastName, userType user_type) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.user_type = user_type;
    }
    
    /**
     * Constructor to initialize the User object with the provided parameters excluding `user_type`.
     * 
     * @param id The unique identifier for the user.
     * @param username The username of the user.
     * @param password The password of the user.
     * @param firstName The first name of the user.
     * @param lastName The last name of the user.
     */
    public User(int id, String username, String password, String firstName, String lastName) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }
    
    /**
     * Constructor to initialize the User object with only the username and password.
     * 
     * @param username The username of the user.
     * @param password The password of the user.
     */
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getters and Setters
    
    /**
     * Gets the unique identifier for the user.
     * 
     * @return The user ID.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the unique identifier for the user.
     * 
     * @param id The user ID to set.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the username of the user.
     * 
     * @return The username of the user.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of the user.
     * 
     * @param username The username to set.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the password of the user.
     * 
     * @return The password of the user.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password of the user.
     * 
     * @param password The password to set.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the first name of the user.
     * 
     * @return The first name of the user.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the first name of the user.
     * 
     * @param firstName The first name to set.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Gets the last name of the user.
     * 
     * @return The last name of the user.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the last name of the user.
     * 
     * @param lastName The last name to set.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Gets the user type (e.g., admin, regular).
     * 
     * @return The user type.
     */
    public userType getType() {
        return this.user_type;
    }

    // Validation Method
    
    /**
     * Validates the user details, ensuring that the ID is greater than 0, and both username and password are not null or empty.
     * 
     * @return True if the details are valid, false otherwise.
     */
    public boolean areDetailsValid() {
        return id > 0 && username != null && !username.isEmpty() && password != null && !password.isEmpty();
    }

    /**
     * Provides a string representation of the User object.
     * 
     * @return A string representation of the User object.
     */
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}
