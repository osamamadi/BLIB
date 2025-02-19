// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package commonGui;

/**
 * This interface implements the abstract method used to display
 * objects onto the client or server UIs.
 * 
 * The interface is designed to be implemented by classes that need to provide
 * a mechanism for displaying messages on the user interface. Any class that
 * implements this interface should provide an implementation of the `display`
 * method to show the provided message.
 * 
 * @author Dr Robert Lagani&egrave;re
 * @author Dr Timothy C. Lethbridge
 * @version July 2000
 */
public interface ChatIF 
{
  /**
   * Method that when overridden is used to display objects onto
   * a UI.
   * 
   * The `display` method should be implemented by a class to display the 
   * given message on the user interface. The message passed into this method
   * is typically a string that will be shown to the user.
   * 
   * @param message The message to display on the user interface.
   */
  public abstract void display(String message);
}
