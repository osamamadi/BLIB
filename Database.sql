CREATE SCHEMA db;
CREATE TABLE db.subscriberstatusreport (
  day varchar(10) NOT NULL,
  month varchar(10) NOT NULL,
  year varchar(4) NOT NULL,
  frozenpercent double NOT NULL,
  frozen_subscribers varchar(45) DEFAULT NULL,
  PRIMARY KEY (day, month, year)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE db.Book (
    id INT AUTO_INCREMENT PRIMARY KEY,
    book_name VARCHAR(100) UNIQUE NOT NULL,
    topic VARCHAR(100),
    description TEXT,
    location_on_shelf VARCHAR(50),
    copies INT DEFAULT 0 
);

CREATE TABLE db.CopyOfBook (
    id INT AUTO_INCREMENT PRIMARY KEY,
    book_id INT NOT NULL,
    barcode VARCHAR(50) UNIQUE NOT NULL,
    status enum('Available', 'Borrowed', 'Lost', 'Reserved'),
    FOREIGN KEY(book_id) REFERENCES Book(id)
);

CREATE TABLE db.user (
    id INT PRIMARY KEY NOT NULL,
    username VARCHAR(50) UNIQUE NOT NULL, 
    password VARCHAR(255) NOT NULL, 
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    user_type ENUM('subscriber', 'librarian') NOT NULL
);

CREATE TABLE db.subscriber (
    subscriber_number INT UNIQUE NOT NULL AUTO_INCREMENT,
    id INT UNIQUE PRIMARY KEY,
    status enum('active', 'frozen') DEFAULT 'active',
    email VARCHAR(100) UNIQUE,
    late_returns INT DEFAULT 0,
    phone_number VARCHAR(15),
    FOREIGN KEY(id) REFERENCES user(id) ON DELETE CASCADE
);

CREATE TABLE db.frozen (
  subscriber_id INT NOT NULL,
  fromdate VARCHAR(40) NOT NULL,
  todate VARCHAR(40) NOT NULL,
  PRIMARY KEY (subscriber_id, fromdate, todate),
  FOREIGN KEY (subscriber_id) REFERENCES db.subscriber(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE db.borrow (
    id INT AUTO_INCREMENT PRIMARY KEY,
    borrow_date DATE NOT NULL,
    due_date DATE NOT NULL,
    user_id INT NOT NULL,
    copy_id INT NOT NULL,
    status ENUM('active', 'finish') DEFAULT 'active',
    FOREIGN KEY (user_id) REFERENCES subscriber(id),
    FOREIGN KEY (copy_id) REFERENCES CopyOfBook(id)
);

CREATE TABLE db.order (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    copy_id INT NOT NULL,
    order_date DATE NOT NULL,
    order_status ENUM('pending', 'delivered', 'cancelled', 'finished'),
    delivered_date DATE,
    FOREIGN KEY (user_id) REFERENCES subscriber(id),
    FOREIGN KEY (copy_id) REFERENCES CopyOfBook(id)
);

CREATE TABLE db.return (
    id INT AUTO_INCREMENT PRIMARY KEY,
    borrow_id INT NOT NULL,
    actual_return_date DATE NOT NULL,
    FOREIGN KEY (borrow_id) REFERENCES borrow(id)
);

CREATE TABLE db.msg (
    id INT AUTO_INCREMENT PRIMARY KEY,
    msgTo INT NOT NULL,
    msgType ENUM('Reminder', 'Book Arrival', 'Accept Extension', 'Deny Extension', 'manual extension', 'lost book') NOT NULL, 
    msg TEXT NOT NULL,
    isRead BOOLEAN DEFAULT FALSE,
    msgDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (msgTo) REFERENCES user(id)
);



INSERT INTO db.book VALUES (1,'Database Systems','Technology','Comprehensive book on database systems','A1',1),(2,'Data Structures','Technology','A detailed book on data structures','A2',3),(3,'Modern Physics','Science','An introduction to modern physics','B1',3),(4,'Artificial Intelligence','Technology','AI concepts and applications','C1',2),(5,'The Art of Computer Programming','Technology','A classic in computer programming by Donald Knuth','D1',2),(6,'Introduction to Algorithms','Technology','Comprehensive guide to algorithms','D2',4),(7,'Quantum Mechanics','Science','An introduction to quantum mechanics concepts','E1',2),(8,'Principles of Economics','Economics','Detailed introduction to economic principles','F1',2),(9,'History of Ancient Civilizations','History','A deep dive into ancient civilizations','G1',2);



INSERT INTO db.copyofbook VALUES (1,1,'DBS001','Borrowed'),(2,2,'DS001','Borrowed'),(3,2,'DS002','Reserved'),(4,3,'DS003','Borrowed'),(5,3,'MP001','Borrowed'),(6,3,'MP002','Borrowed'),(7,3,'MP003','Borrowed'),(8,4,'AI001','Borrowed'),(9,4,'AI002','Borrowed'),(10,5,'AC001','Borrowed'),(11,5,'AC002','Available'),(12,6,'IA001','Available'),(13,6,'IA002','Borrowed'),(14,6,'IA003','Borrowed'),(15,6,'IA004','Borrowed'),(16,7,'QM002','Borrowed'),(17,7,'QM003','Borrowed'),(18,8,'PE001','Available'),(19,8,'PE002','Available'),(20,9,'HC001','Available'),(21,9,'HC002','Available');




INSERT INTO db.user VALUES (123456789,'a','a','Anat','Dahn','subscriber'),(123654798,'aa','aa','Ahmad','Madi','subscriber'),(123789456,'al','al','Alice','Alice1','subscriber'),(191919191,'s','s','Saleem','Saleem','subscriber'),(191919199,'sal','sal','Salah','Salah','subscriber'),(212131353,'h','h','Helal','Ali','librarian'),(212682637,'w','w','Wadeia','Farran','subscriber'),(213911548,'n','n','Nuwar','Dabah','subscriber'),(322394214,'b','b','Bader','Boshnak','subscriber'),(324033868,'m','m','Majd','Awad','subscriber'),(325468254,'o','o','Osama','Madi','librarian'),(741258963,'t','t','Tiran','Hesawi','subscriber'),(741852963,'mo','mo','Moshe','Moshe','subscriber'),(852134679,'bo','bo','Bob ','Bob1','subscriber'),(856429731,'mu','mu','Muhammed','Madi','subscriber'),(963258741,'k','k','Katerina','Korenblat','subscriber'),(987654321,'e','e','Elya','Zeldner','subscriber');



INSERT INTO db.subscriber VALUES (6,123456789,'active','Anat@Braude.ac.il',0,'0526847985'),(15,123654798,'frozen','Ahmad@hotmail.com',0,'0527896497'),(11,123789456,'active','Alice@Technion.com',0,'0521463987'),(13,191919191,'active','Saleem@Gmail.com',0,'0527896316'),(14,191919199,'active','Salah@Gmail.com',0,'0527896317'),(4,212682637,'active','wadeia.farran@example.com',0,'0558138074'),(2,213911548,'active','nuwar.dabah@example.com',0,'0527210240'),(1,322394214,'active','bader.boshnaq@example.com',0,'0536849785'),(3,324033868,'frozen','majd.awad@example.com',1,'0502413569'),(8,741258963,'active','hesawi@braude.ac.il',0,'051236987'),(10,741852963,'active','Moshe@Braude.ac.il',0,'0256987431'),(12,852134679,'active','Bob@Technion.com',0,'0521478966'),(16,856429731,'frozen','MuhammedMadi@Hotmail.com',0,'074589612'),(9,963258741,'active','katerina.kornblat@gmail.com',0,'0527894613'),(7,987654321,'active','Eliya@Braude.com',0,'0521469874');



INSERT INTO db.frozen VALUES (324033868,'2025-01-27','2025-02-27'),(123654798,'2025-01-25','2025-02-25'),(856429731,'2025-01-26','2025-02-26');



INSERT INTO db.borrow VALUES (1,'2025-01-27','2025-01-30',212682637,1,'active'),(2,'2025-01-17','2025-01-20',324033868,3,'finish'),(4,'2025-01-27','2025-01-31',322394214,3,'finish'),(5,'2025-01-27','2025-02-07',213911548,2,'active'),(6,'2025-01-27','2025-02-08',213911548,5,'active'),(7,'2025-01-24','2025-01-26',322394214,6,'active'),(8,'2025-01-10','2025-01-18',123456789,7,'active'),(9,'2025-01-27','2025-02-08',987654321,4,'active'),(10,'2025-01-27','2025-02-04',741258963,8,'active'),(11,'2025-01-27','2025-02-10',987654321,9,'active'),(12,'2025-01-27','2025-01-31',741852963,10,'active'),(13,'2025-01-27','2025-02-06',123789456,12,'finish'),(14,'2025-01-27','2025-02-10',852134679,13,'active'),(15,'2025-01-28','2025-02-07',191919191,14,'active'),(16,'2025-01-28','2025-02-01',191919199,15,'active'),(17,'2025-01-27','2025-02-01',123789456,16,'active'),(18,'2025-01-27','2025-02-04',852134679,17,'active');



INSERT INTO db.order VALUES (1,213911548,1,'2025-01-27','pending',NULL),(2,212682637,3,'2025-01-27','delivered','2025-01-27'),(4,191919199,16,'2025-01-27','pending',NULL);



INSERT INTO db.return VALUES (1,2,'2025-01-27'),(2,4,'2025-01-27'),(3,13,'2025-01-27');



INSERT INTO db.msg VALUES (1,212682637,'Reminder','Tommorrow, you have to return your borrowed book, copy id: 1',0,'2025-01-28 22:00:00'),(2,324033868,'Reminder','Tommorrow, you have to return your borrowed book, copy id: 3',0,'2025-01-18 22:00:00'),(5,213911548,'Reminder','Tommorrow, you have to return your borrowed book, copy id: 2',0,'2025-02-05 22:00:00'),(6,212682637,'Book Arrival','The book you ordered is waiting for you until 2 days, copy id: 3',0,'2025-01-27 18:11:26'),(7,213911548,'Reminder','Tommorrow, you have to return your borrowed book, copy id: 5',0,'2025-02-06 22:00:00'),(8,322394214,'Reminder','Tommorrow, you have to return your borrowed book, copy id: 6',0,'2025-01-24 22:00:00'),(9,123456789,'Reminder','Tommorrow, you have to return your borrowed book, copy id: 7',0,'2025-01-16 22:00:00'),(10,987654321,'Reminder','Tommorrow, you have to return your borrowed book, copy id: 4',0,'2025-02-06 22:00:00'),(11,741258963,'Reminder','Tommorrow, you have to return your borrowed book, copy id: 8',0,'2025-02-02 22:00:00'),(12,987654321,'Reminder','Tommorrow, you have to return your borrowed book, copy id: 9',0,'2025-02-08 22:00:00'),(13,741852963,'Reminder','Tommorrow, you have to return your borrowed book, copy id: 10',0,'2025-01-29 22:00:00'),(15,852134679,'Reminder','Tommorrow, you have to return your borrowed book, copy id: 13',0,'2025-02-08 22:00:00'),(16,191919191,'Reminder','Tommorrow, you have to return your borrowed book, copy id: 14',0,'2025-02-05 22:00:00'),(17,191919199,'Reminder','Tommorrow, you have to return your borrowed book, copy id: 15',0,'2025-01-30 22:00:00'),(18,123789456,'Reminder','Tommorrow, you have to return your borrowed book, copy id: 16',0,'2025-01-30 22:00:00'),(19,852134679,'Reminder','Tommorrow, you have to return your borrowed book, copy id: 17',0,'2025-02-02 22:00:00');