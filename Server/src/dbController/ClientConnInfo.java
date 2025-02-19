package dbController;

import java.io.Serializable;

/**
 * This class represents the connection information of a client.
 * It includes details like the client's host name, IP address, and current connection status.
 * The class implements Serializable to allow it to be serialized and sent over a network.
 * @author Your Name
 * @version 1.0
 */
public class ClientConnInfo implements Serializable {
    /** The host name of the client */
    private String hostName;

    /** The IP address of the client */
    private String ipAddress;

    /** The current connection status of the client */
    private String status;

    /**
     * Constructor to initialize the client connection information.
     * 
     * @param hostName The host name of the client.
     * @param ipAddress The IP address of the client.
     * @param status The current connection status of the client.
     */
    public ClientConnInfo(String hostName, String ipAddress, String status) {
        this.hostName = hostName;
        this.ipAddress = ipAddress;
        this.status = status;
    }
    
    /**
     * Sets the connection status of the client.
     * 
     * @param status The new status to set.
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Gets the host name of the client.
     * 
     * @return The host name of the client.
     */
    public String getHostName() {
        return hostName;
    }

    /**
     * Gets the IP address of the client.
     * 
     * @return The IP address of the client.
     */
    public String getIpAddress() {
        return ipAddress;
    }

    /**
     * Gets the current connection status of the client.
     * 
     * @return The status of the client.
     */
    public String getStatus() {
        return status;
    }
}
