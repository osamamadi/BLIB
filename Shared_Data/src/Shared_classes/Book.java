package Shared_classes;

import java.io.Serializable;

/**
 * Represents a Book object with properties such as ID, name, topic, description, and number of copies.
 * Implements Serializable for object serialization.
 * 
 * This class is used to model a book in the library system, containing information such as the book's unique 
 * identifier (ID), name, genre, description, and the number of copies available in the library.
 * 
 * @author Majd Awad
 * @version 1.1dz
 */
public class Book implements Serializable {
    
    /**
     * The unique identifier of the book.
     * This ID is used to distinguish one book from another in the library's database.
     */
    private int id;
    
    /**
     * The name or title of the book.
     * This is the title of the book, for example "To Kill a Mockingbird".
     */
    private String name;

    /**
     * The topic or genre of the book.
     * This indicates the genre or category the book belongs to, such as "Fiction", "Non-Fiction", "Science", etc.
     */
    private String topic;

    /**
     * A short description or summary of the book.
     * This provides a brief description of the content or story of the book.
     */
    private String description;

    /**
     * The number of available copies of the book in the library.
     * This represents how many physical or digital copies of the book are available for borrowing.
     */
    private int copies;

    /**
     * Constructor to initialize a Book object with given parameters.
     * 
     * This constructor sets the ID, name, topic, description, and the number of available copies of the book.
     * 
     * @param id The unique identifier of the book.
     * @param name The name or title of the book.
     * @param topic The topic or genre of the book.
     * @param description A short description of the book.
     * @param copies The number of available copies of the book.
     */
    public Book(int id, String name, String topic, String description, int copies) {
        this.id = id;
        this.name = name;
        this.topic = topic;
        this.description = description;
        this.copies = copies;
    }

    /**
     * Gets the unique identifier of the book.
     * 
     * Returns the ID of the book.
     * 
     * @return The book ID.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the book.
     * 
     * This method allows setting a new ID for the book.
     * 
     * @param id The book ID to set.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the name or title of the book.
     * 
     * Returns the name of the book, which is its title.
     * 
     * @return The book name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name or title of the book.
     * 
     * This method allows setting a new name (title) for the book.
     * 
     * @param name The book name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the topic or genre of the book.
     * 
     * Returns the genre or category of the book, for example, "Science Fiction", "History", etc.
     * 
     * @return The book topic.
     */
    public String getTopic() {
        return topic;
    }

    /**
     * Sets the topic or genre of the book.
     * 
     * This method allows setting a new topic or genre for the book.
     * 
     * @param topic The book topic to set.
     */
    public void setTopic(String topic) {
        this.topic = topic;
    }

    /**
     * Gets the description of the book.
     * 
     * Returns a brief description or summary of the book's content.
     * 
     * @return The book description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the book.
     * 
     * This method allows setting a new description for the book.
     * 
     * @param description The book description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the number of available copies of the book.
     * 
     * Returns the number of copies of the book currently available in the library.
     * 
     * @return The number of copies available.
     */
    public int getCopies() {
        return copies;
    }

    /**
     * Sets the number of available copies of the book.
     * 
     * This method allows setting a new number of available copies for the book.
     * 
     * @param copies The number of copies to set.
     */
    public void setCopies(int copies) {
        this.copies = copies;
    }

    /**
     * Returns a string representation of the Book object, displaying its ID, name, topic, description, and number of copies.
     * 
     * This method is useful for debugging and logging purposes, as it provides a textual representation of the book's properties.
     * 
     * @return A string representation of the Book.
     */
    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", topic='" + topic + '\'' +
                ", description='" + description + '\'' +
                ", copies=" + copies +
                '}';
    }
}
