# Library Management System

A desktop-based application developed to assist in managing a library’s daily operations.

## Description

Created a system that allows **librarians** to manage:
- Books (add, edit, delete)
- Users (manage member accounts)
- Borrowing records (track which user has borrowed which book and when)

Meanwhile, **regular users** can:
- Browse the catalog of available books
- View their personal borrowing history

This system supports both administrative and user-level operations, offering a complete, local solution for small libraries or educational institutions.

## Tools & Technologies Used

- **Java Swing** — GUI for desktop interaction  
- **JPA & Hibernate** — ORM for data persistence  
- **H2 Database** — In-memory and file-based local database  
- **JUnit 5** — Testing framework  
- **JavaDocs** — For generating professional documentation

## Features

- Book management: Add, update, delete, and search for books
- User management: Register new users and update user details
- Borrowing system: Track which users borrowed which books and when
- Smart search functionality
- Simple analytics: Show most borrowed books
- Clean UI using Java Swing
- Unit tested with JUnit 5
- Full JavaDocs documentation included

## Database

The app uses **H2 Database** for lightweight and fast data access, either in memory or stored locally as a file. No external DB server setup required.
