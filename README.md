📚 BLIB – Library Management System (Jan–Apr 2025).

What does the project do ? 
The project is a Java-based library management system that allows students to manage books and librarians to track activities through reports.

Features:
- 🔍 Search books by title, author, or category
- 📚 Borrow and return books with real-time status
- 📦 Pre-order unavailable books
- ⏰ Notification system for due dates and status updates
- 🧾 Auto-generated monthly reports for librarians
- 👥 Role-based interface: Student / Librarian / Admin
- 🔄 Real-time data sync using OCSF
- 💾 Efficient SQL schema (BCNF normalized)
- 🔒 Singleton database connection pattern (prevents overload)

Tools:
   **OOP by Java**
   **JavaFX** + **FXML** – UI Design
   **OCSF** – Client-server communication
   **MySQL** – Backend database
   **Eclipse IDE for Enterprise Java Developers** (Preferred)
   **Git & GitHub** – Team collaboration and version control

Instructions
  1. Clone the repository By writing : 
    git clone https://github.com/osamamadi/BLIB.git
    cd BLIB
  
  2. Install JavaFX for running run the UI components of the project.
    Download JavaFX SDK from: https://gluonhq.com/products/javafx/
    In Eclipse Enterprise Edition:
    Go to Project > Properties > Java Build Path
    Add the JavaFX SDK lib directory to your Libraries
    Open Run Configurations and add this to VM arguments:
    css Copy Edit
    --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml
  
  3. Set up the MySQL Database
    Create a local MySQL database named 'blib'
    Import the SQL schema from the file:
    /resources/db_schema.sql
  
  4. Run the Application

* Launch the server project first to start the backend*
* Then run the client project to access the student or librarian interface

👥 Contributors

- [Osama Madi](https://github.com/osamamadi) – Software Engineering Student at Braude College  
- [HelalAli31](https://github.com/HelalAli31) – Full-stack developer at RidoSoft  
- [baderboshnak](https://github.com/baderboshnak) – Software Engineering Student at Braude College  

Images: 
![Screenshot 2025-07-07 173402](https://github.com/user-attachments/assets/cbe71f37-542b-44c3-a8ce-80883713420d)
![Screenshot 2025-07-07 173554](https://github.com/user-attachments/assets/99816ddf-c8f3-4db3-b62f-5b7622dd02ee)
![Screenshot 2025-07-07 173524](https://github.com/user-attachments/assets/15cb3b9a-d622-46d0-85b6-d2dd78142402)

