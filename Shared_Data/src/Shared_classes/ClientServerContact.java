package Shared_classes;

import java.io.Serializable;

import Shared_classes.Command;

/**
 * Represents a contact between a client and server, containing objects for communication and a command.
 * Implements Serializable for object serialization, enabling sending of these objects over a network or saving to storage.
 * 
 * @author Majd Awad
 * @version 1.1dz
 */
public class ClientServerContact implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * The first object involved in the communication, typically representing data or a request.
     */
    private Object obj1;

    /**
     * The second object involved in the communication, often representing additional data or a response.
     */
    private Object obj2;

    /**
     * The command associated with the communication, typically instructing the server or client on what to do with the objects.
     */
    private Command cmd;

    /**
     * Constructor to initialize the communication with one object and a command.
     * 
     * @param obj1 The first object involved in the communication.
     * @param cmd The command that dictates the action for the communication.
     */
    public ClientServerContact(Object obj1, Command cmd) {
        this.obj1 = obj1;
        this.cmd = cmd;
    }

    /**
     * Constructor to initialize the communication with two objects and a command.
     * 
     * @param obj1 The first object involved in the communication.
     * @param obj2 The second object involved in the communication.
     * @param cmd The command that dictates the action for the communication.
     */
    public ClientServerContact(Object obj1, Object obj2, Command cmd) {
        this.obj1 = obj1;
        this.obj2 = obj2;
        this.cmd = cmd;
    }

    /**
     * Gets the first object involved in the communication.
     * 
     * @return The first object (obj1).
     */
    public Object getObj1() {
        return obj1;
    }

    /**
     * Gets the second object involved in the communication.
     * 
     * @return The second object (obj2).
     */
    public Object getObj2() {
        return obj2;
    }

    /**
     * Sets the second object involved in the communication.
     * 
     * @param obj2 The second object (obj2).
     */
    public void setObj2(Object obj2) {
        this.obj2 = obj2;
    }

    /**
     * Sets the command associated with the communication.
     * 
     * @param cmd The command that dictates the action for the communication.
     */
    public void setCmd(Command cmd) {
        this.cmd = cmd;
    }

    /**
     * Sets the first object involved in the communication.
     * 
     * @param obj1 The first object (obj1).
     */
    public void setObj1(Object obj1) {
        this.obj1 = obj1;
    }

    /**
     * Gets the command associated with the communication.
     * 
     * @return The command (cmd).
     */
    public Command getCmd() {
        return cmd;
    }
}
