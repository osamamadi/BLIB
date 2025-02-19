package Shared_classes;

import java.io.Serializable;

/**
 * The UserWithSubscriber class represents a combination of a User and Subscriber.
 * It provides methods to retrieve and set user-related and subscriber-related details.
 * Implements Serializable for object serialization.
 * 
 * @author Majd Awad
 * @version 1.1dz
 */
public class UserWithSubscriber implements Serializable {
	/** 
	 * The serialVersionUID is used to ensure the version compatibility during serialization.
	 * It is a unique identifier for this class version.
	 * If the class definition changes (e.g., fields or methods are modified), 
	 * this UID ensures that a serialized object of an older version can be correctly deserialized.
	 * 
	 */
    private static final long serialVersionUID = 1L; 
    /** 
     * The User object that holds information about the user (username, password, etc.).
     */
    private User user; 

    /** 
     * The Subscriber object that holds information about the subscriber (status, phone, email, etc.).
     */
    private Subscriber subscriber; 

    /**
     * Default constructor for UserWithSubscriber.
     */
    public UserWithSubscriber() {}

    /**
     * Constructor for creating a UserWithSubscriber object.
     * 
     * @param user User object containing the user's information.
     * @param subscriber Subscriber object containing the subscriber's information.
     */
    public UserWithSubscriber(User user, Subscriber subscriber) {
        this.user = user;
        this.subscriber = subscriber;
    }

    /**
     * Gets the User object associated with this UserWithSubscriber.
     * 
     * @return the User object.
     */
    public User getUser() {
        return user;
    }

    /**
     * Sets the User object for this UserWithSubscriber.
     * 
     * @param user User object to set.
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Gets the Subscriber object associated with this UserWithSubscriber.
     * 
     * @return the Subscriber object.
     */
    public Subscriber getSubscriber() {
        return subscriber;
    }

    /**
     * Sets the Subscriber object for this UserWithSubscriber.
     * 
     * @param subscriber Subscriber object to set.
     */
    public void setSubscriber(Subscriber subscriber) {
        this.subscriber = subscriber;
    }

    // Derived getters for TableView
    
    /**
     * Gets the ID of the user.
     * 
     * @return the ID of the user. If the user is null, returns 0.
     */
    public int getId() {
        return user != null ? user.getId() : 0;
    }

    /**
     * Sets the ID of the user.
     * 
     * @param id the ID of the user to set.
     */
    public void setId(int id) {
        if (user == null) {
            user = new User();
        }
        user.setId(id);
    }

    /**
     * Gets the username of the user.
     * 
     * @return the username of the user. If the user is null, returns an empty string.
     */
    public String getUsername() {
        return user != null ? user.getUsername() : "";
    }

    /**
     * Sets the username of the user.
     * 
     * @param username the username to set.
     */
    public void setUsername(String username) {
        if (user == null) {
            user = new User();
        }
        user.setUsername(username);
    }

    /**
     * Gets the password of the user.
     * 
     * @return the password of the user. If the user is null, returns an empty string.
     */
    public String getPassword() {
        return user != null ? user.getPassword() : "";
    }

    /**
     * Sets the password of the user.
     * 
     * @param password the password to set.
     */
    public void setPassword(String password) {
        if (user == null) {
            user = new User();
        }
        user.setPassword(password);
    }

    /**
     * Gets the first name of the user.
     * 
     * @return the first name of the user. If the user is null, returns an empty string.
     */
    public String getFirstName() {
        return user != null ? user.getFirstName() : "";
    }

    /**
     * Sets the first name of the user.
     * 
     * @param firstName the first name to set.
     */
    public void setFirstName(String firstName) {
        if (user == null) {
            user = new User();
        }
        user.setFirstName(firstName);
    }

    /**
     * Gets the last name of the user.
     * 
     * @return the last name of the user. If the user is null, returns an empty string.
     */
    public String getLastName() {
        return user != null ? user.getLastName() : "";
    }

    /**
     * Sets the last name of the user.
     * 
     * @param lastName the last name to set.
     */
    public void setLastName(String lastName) {
        if (user == null) {
            user = new User();
        }
        user.setLastName(lastName);
    }

    /**
     * Gets the phone number of the subscriber.
     * 
     * @return the phone number of the subscriber. If the subscriber is null, returns an empty string.
     */
    public String getPhone() {
        return subscriber != null ? subscriber.getPhone_number() : "";
    }

    /**
     * Sets the phone number of the subscriber.
     * 
     * @param phone the phone number to set.
     */
    public void setPhone(String phone) {
        if (subscriber == null) {
            subscriber = new Subscriber();
        }
        subscriber.setPhone_number(phone);
    }

    /**
     * Gets the email of the subscriber.
     * 
     * @return the email of the subscriber. If the subscriber is null, returns an empty string.
     */
    public String getEmail() {
        return subscriber != null ? subscriber.getEmail() : "";
    }

    /**
     * Sets the email of the subscriber.
     * 
     * @param email the email to set.
     */
    public void setEmail(String email) {
        if (subscriber == null) {
            subscriber = new Subscriber();
        }
        subscriber.setEmail(email);
    }

    /**
     * Gets the status of the subscriber.
     * 
     * @return the status of the subscriber. If the subscriber is null, returns an empty string.
     */
    public String getStatus() {
        return subscriber != null ? subscriber.getStatus() : "";
    }

    /**
     * Sets the status of the subscriber.
     * 
     * @param status the status to set.
     */
    public void setStatus(String status) {
        if (subscriber == null) {
            subscriber = new Subscriber();
        }
        subscriber.setStatus(status);
    }

    /**
     * Gets the number of late returns for the subscriber.
     * 
     * @return the number of late returns. If the subscriber is null, returns 0.
     */
    public int getLateReturn() {
        return subscriber != null ? subscriber.getLateReturns() : 0;
    }

    /**
     * Sets the number of late returns for the subscriber.
     * 
     * @param lateReturn the number of late returns to set.
     */
    public void setLateReturn(int lateReturn) {
        if (subscriber == null) {
            subscriber = new Subscriber();
        }
        subscriber.setLateReturns(lateReturn);
    }

    /**
     * Gets the subscriber number.
     * 
     * @return the subscriber number. If the subscriber is null, returns 0.
     */
    public int getSubscriberNumber() {
        return subscriber != null ? subscriber.getSubscriber_number() : 0;
    }

    /**
     * Sets the subscriber number.
     * 
     * @param subscriberNumber the subscriber number to set.
     */
    public void setSubscriberNumber(int subscriberNumber) {
        if (subscriber == null) {
            subscriber = new Subscriber();
        }
        subscriber.setSubscriber_number(subscriberNumber);
    }
}
