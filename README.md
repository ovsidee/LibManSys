# Library Management System

A comprehensive **Library Management System** developed using **Java** and **Java Swing**, designed to help librarians manage books, users, and borrowing records. This system also enables regular users to browse available books and track their borrowing history.

## Features

- **Librarian Features:**
  - Manage books (add, update, remove)
  - Manage users (add, update, remove)
  - Track borrowing records
  - View borrowed books and manage due dates

- **User Features:**
  - Browse available books
  - View and track personal borrowing history
  - Search and filter books by title, author, or genre

- **Database Management:**
  - H2 Database for storing book and user data
  - Hibernate & JPA for database interaction

## Technologies Used

- **Java Swing** 
- **JPA** 
- **Hibernate** 
- **H2 Database**
- **JUnit 5** 
- **JavaDocs** 

## Running the Application

To run the application locally:
1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/LibraryManagementSystem.git && cd LibraryManagementSystem
2. Compile and run the Java application using Maven:
  - Install Maven if you haven't already. Follow the instructions here.
  - To build the project:
    ```
    mvn clean install
    ```
    
  - To run the application:
    ```
    mvn exec:java
    ```
    
## Unit Testing

This project includes unit tests implemented with JUnit 5. To run the tests:
- Run the command:
  ```
    mvn test
  ```

## Documentation

JavaDocs have been generated for the project, providing an overview of the code structure and API endpoints.
