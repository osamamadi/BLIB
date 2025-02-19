package Shared_classes;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * The Message class represents a message with its details such as content, type, read status, and timestamp.
 * This class implements Serializable to allow it to be serialized and deserialized for transmission over a network or saving to a file.
 * 
 * @author Majd Awad
 * @version 1.0
 */
public class Message implements Serializable{
	/**
	 * The serial version UID for this class, used to ensure proper serialization and deserialization.
	 * It is important to maintain this value when changing the class definition to ensure compatibility 
	 * between different versions of the class during serialization.
	 */
	private static final long serialVersionUID = 2L;

	/**
	 * The content of the message. This field holds the actual message text that is sent.
	 */
	private String msg;

	/**
	 * The timestamp indicating when the message was created. It stores the date and time the message was sent.
	 */
	private Timestamp msgDate;

	/**
	 * The type of the message. This field is used to classify the message (e.g., "information", "warning", etc.).
	 */
	private String msgType;

	/**
	 * A flag that indicates whether the message has been read or not.
	 * It is true if the message has been read, and false if it has not been read yet.
	 */
	private boolean isRead;

	/**
	 * A unique identifier for the message. This is used to distinguish one message from another.
	 */
	private int id;


    /**
     * Constructs a Message object with the specified parameters.
     *
     * @param msg the content of the message
     * @param msgDate the timestamp of the message creation
     * @param msgType the type of the message (e.g., information, warning)
     * @param isRead the read status of the message (true if read, false otherwise)
     * @param id the unique identifier of the message
     */
    public Message (String msg, Timestamp msgDate, String msgType, boolean isRead, int id) {
        this.msgType = msgType;
        this.msg = msg;
        this.msgDate = msgDate;
        this.isRead = isRead;
        this.id = id;
    }

    /**
     * Gets the content of the message.
     * 
     * @return the content of the message
     */
    public String getMsg() {
        return msg;
    }

    /**
     * Gets the type of the message.
     * 
     * @return the type of the message
     */
    public String getMsgType() {
        return msgType;
    }

    /**
     * Gets the timestamp of when the message was created.
     * 
     * @return the timestamp of the message creation
     */
    public Timestamp getMsgDate() {
        return msgDate;
    }

    /**
     * Checks if the message has been read.
     * 
     * @return true if the message has been read, false otherwise
     */
    public boolean isRead() {
        return isRead;
    }

    /**
     * Gets the unique identifier of the message.
     * 
     * @return the unique identifier of the message
     */
    public int getid() {
        return id;
    }

    /**
     * Sets the read status of the message.
     *
     * @param set true if the message is marked as read, false otherwise
     */
    public void setRead(boolean set) {
        isRead = set;
    }
}
