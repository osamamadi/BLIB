package Shared_classes;

import java.io.Serializable;

/**
 * The Subscriber class represents a subscriber in a system.
 * It stores personal and subscription-related details such as ID, status, late returns, phone number, email, 
 * and subscriber number, and provides methods to access and modify these details.
 * This class also ensures that subscriber details are valid and supports serialization for persistent storage.
 * 
 * @author Majd Awad
 * @version 1.1dz
 */
public class Subscriber implements Serializable {
    
    /** 
     * The serialVersionUID is used to ensure the version compatibility during serialization.
     * It is a unique identifier for this class version.
     * If the class definition changes (e.g., fields or methods are modified), 
     * this UID ensures that a serialized object of an older version can be correctly deserialized.
     * 
     * @version 1.1dz
     */
    private static final long serialVersionUID = 1L; // Add this for serialization

    /** subscriber_id */
    private int id; 
    
    /** Subscriber status, can be 'active' or 'frozen' */
    private String status; 
    
    /** Number of late returns by the subscriber */
    private int lateReturns; 
    
    /** Subscriber's phone number */
    private String phone_number;
    
    /** Subscriber's email address */
    private String email;
    
    /** Subscriber's unique subscriber number */
    private int subscriber_number;

    // Constructors

    /**
     * Default constructor for creating an empty Subscriber object.
     */
    public Subscriber() {
    }

    /**
     * Constructor for initializing a Subscriber with id, status, email, late returns, and phone number.
     * 
     * @param id the unique subscriber id
     * @param status the status of the subscriber (either 'active' or 'frozen')
     * @param email the email address of the subscriber
     * @param lateReturns the number of late returns for the subscriber
     * @param phone_number the phone number of the subscriber
     */
    public Subscriber(int id, String status, String email, int lateReturns, String phone_number) {
        this.id = id;
        this.status = status;
        this.lateReturns = lateReturns;
        this.phone_number = phone_number;
        this.email = email;
    }

    /**
     * Constructor for initializing a Subscriber with id, status, email, late returns, subscriber number, and phone number.
     * 
     * @param id the unique subscriber id
     * @param status the status of the subscriber (either 'active' or 'frozen')
     * @param email the email address of the subscriber
     * @param lateReturns the number of late returns for the subscriber
     * @param subscriber_number the unique subscriber number
     * @param phone_number the phone number of the subscriber
     */
    public Subscriber(int id, String status, String email, int lateReturns, int subscriber_number, String phone_number) {
        this.id = id;
        this.status = status;
        this.lateReturns = lateReturns;
        this.phone_number = phone_number;
        this.email = email;
        this.subscriber_number = subscriber_number;
    }

    /**
     * Gets the unique id of the subscriber.
     * 
     * @return the unique id of the subscriber
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the unique id of the subscriber.
     * 
     * @param id the unique subscriber id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the phone number of the subscriber.
     * 
     * @return the phone number of the subscriber
     */
    public String getPhone_number() {
        return phone_number;
    }

    /**
     * Sets the phone number of the subscriber.
     * 
     * @param phone_number the phone number of the subscriber
     */
    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    /**
     * Gets the email address of the subscriber.
     * 
     * @return the email address of the subscriber
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address of the subscriber.
     * 
     * @param email the email address of the subscriber
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the unique subscriber number.
     * 
     * @return the unique subscriber number
     */
    public int getSubscriber_number() {
        return subscriber_number;
    }

    /**
     * Sets the unique subscriber number.
     * 
     * @param subscriber_number the unique subscriber number
     */
    public void setSubscriber_number(int subscriber_number) {
        this.subscriber_number = subscriber_number;
    }

    /**
     * Gets the subscriber's id (same as getId()).
     * 
     * @return the subscriber's id
     */
    public int getSubscriberId() {
        return id;
    }

    /**
     * Sets the subscriber's id.
     * 
     * @param subscriberId the unique subscriber id
     */
    public void setSubscriberId(int subscriberId) {
        this.id = subscriberId;
    }

    /**
     * Gets the status of the subscriber.
     * 
     * @return the status of the subscriber ('active' or 'frozen')
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status of the subscriber. The status must be either 'active' or 'frozen'.
     * 
     * @param status the status of the subscriber ('active' or 'frozen')
     * @throws IllegalArgumentException if the status is neither 'active' nor 'frozen'
     */
    public void setStatus(String status) {
        if (status.equals("active") || status.equals("frozen")) {
            this.status = status;
        } else {
            throw new IllegalArgumentException("Invalid status. Must be 'active' or 'frozen'.");
        }
    }

    /**
     * Gets the number of late returns made by the subscriber.
     * 
     * @return the number of late returns
     */
    public int getLateReturns() {
        return lateReturns;
    }

    /**
     * Sets the number of late returns for the subscriber.
     * 
     * @param lateReturns the number of late returns
     */
    public void setLateReturns(int lateReturns) {
        this.lateReturns = lateReturns;
    }

    /**
     * Validates the subscriber's details to ensure they are correct.
     * 
     * @return true if the subscriber's id is greater than 0 and the status is either 'active' or 'frozen'; false otherwise
     */
    public boolean areDetailsValid() {
        return id > 0 && (status.equals("active") || status.equals("frozen"));
    }

    /**
     * Returns a string representation of the Subscriber object.
     * 
     * @return a string representing the Subscriber object
     */
    @Override
    public String toString() {
        return "Subscriber{" +
                "subscriberId=" + id +
                ", status='" + status + '\'' +
                ", lateReturns=" + lateReturns +
                '}';
    }
}
