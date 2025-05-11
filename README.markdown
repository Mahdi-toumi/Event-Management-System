# Event Management System

## About
This project develops a relational database system for managing event venues and performances, utilizing Oracle SQL and PL/SQL. It ensures efficient scheduling, data integrity, and user interaction through a Java-based interface with JDBC connectivity, providing robust solutions for venue availability, performance scheduling, and ticket management.

## Objectives
1. **Venue Management**: Handle creation, modification, and deletion of event venues with capacity and availability constraints.
2. **Performance Scheduling**: Manage performance details, including artists, rubrics, and scheduling conflicts.
3. **Data Integrity**: Enforce constraints on client data, venue capacity, performance dates, and ticket categories.
4. **User Interface**: Provide a Java-based GUI for seamless interaction with the database.

## Features
- **Database Structure**: Tables for Clients, Venues, Artists, Performances, Rubrics, and Tickets with defined relationships.
- **PL/SQL Automation**: Triggers and procedures for validating data (e.g., email formats, venue capacity, performance dates).
- **Sequences and Indexes**: Auto-incrementing IDs and optimized search capabilities.
- **Java Interface**: JDBC-based connection for displaying, adding, modifying, and searching venues and performances.
- **Error Handling**: Triggers and procedures to manage errors and ensure data consistency.

## Technologies
- **Database**: Oracle SQL, PL/SQL
- **Programming**: Java (JDBC for database connectivity)
- **Tools**: Oracle Database, Mockaroo (for mock data generation)
- **Development Environment**: Any IDE supporting Java and SQL (e.g., IntelliJ IDEA, Eclipse)

## Installation
1. **Prerequisites**:
   - Oracle Database (e.g., Oracle XE)
   - Java Development Kit (JDK) 8 or higher
   - JDBC Driver for Oracle
2. **Setup**:
   - Clone the repository: `git clone https://github.com/Mahdi-toumi/Event-Management-System.git`
   - Configure the Oracle Database with the provided SQL scripts (`create_tables.sql`, `insert_data.sql`).
   - Update the `DBConnection.java` file with your database credentials (URL, USER, PASSWORD).
3. **Run**:
   - Import the Java project into your IDE.
   - Execute the main application to launch the GUI.
   - Run SQL scripts in Oracle SQL Developer or a similar tool to set up the database.

## Project Structure
- `sql/`: SQL scripts for table creation, data insertion, sequences, indexes, triggers, and procedures.
  - `create_tables.sql`: Defines tables (Client, Lieu, Artiste, Spectacle, Rubrique, Billet).
  - `insert_data.sql`: Inserts mock data (partially generated via Mockaroo).
  - `sequences.sql`: Creates sequences for auto-incrementing IDs.
  - `triggers.sql`: Implements triggers for data validation.
  - `procedures.sql`: Defines PL/SQL packages (Gestion_lieux, Gestion_Spectacles).
- `src/`: Java source code for the GUI and JDBC connectivity.
  - `DBConnection.java`: Manages database connection.
  - `interfaces/`: Contains GUI classes for venue and performance management.
- `docs/`: Project documentation (e.g., PDF report).

## Usage
1. **Database Setup**:
   - Run `create_tables.sql` to create the database schema.
   - Execute `sequences.sql` and `triggers.sql` to set up automation.
   - Use `insert_data.sql` to populate tables with sample data.
2. **Run the Application**:
   - Launch the Java application to access the GUI.
   - Use the interface to add, modify, or search venues and performances.
3. **Example Operations**:
   - Add a venue: Specify name, address, city, and capacity (100–2000).
   - Schedule a performance: Input title, date, start time, duration, expected attendees, and venue.
   - Manage tickets: Assign categories (Gold, Silver, Normal) and validate prices.

## Key Components
- **Tables**:
  - `Client`: Stores client details (ID, name, email, password, phone).
  - `Lieu`: Manages venue details (ID, name, address, city, capacity).
  - `Artiste`: Tracks artist information (ID, name, specialty).
  - `Spectacle`: Handles performance schedules (ID, title, date, venue).
  - `Rubrique`: Organizes performance segments (ID, type, artist, duration).
  - `Billet`: Manages tickets (ID, category, price, status).
- **PL/SQL Packages**:
  - `Gestion_lieux`: Procedures for adding, modifying, deleting, and searching venues.
  - `Gestion_Spectacles`: Procedures for managing performances and rubrics.
- **Triggers**:
  - Validate email and phone formats, venue capacity, performance dates, and ticket prices.
  - Ensure performance rubrics (1–3) and venue availability.

## Recommendations
1. **Enhance Security**: Implement encryption for client passwords and secure API endpoints.
2. **Mobile Access**: Develop a mobile app for on-the-go management.
3. **Advanced Features**:
   - Integrate AI for optimized scheduling.
   - Add a calendar view for performance planning.
   - Implement recommendation systems for ticket sales.
4. **Scalability**: Support larger datasets and multi-user access.

## Limitations
- **Data Scope**: Limited to mock data; real-world data may require additional validation.
- **Interface**: Basic GUI; lacks advanced features like drag-and-drop scheduling.
- **Scalability**: Single-user focus; multi-user support needs further development.
## Screenshots 
<p align="center">
  <img src="https://github.com/user-attachments/assets/193152dc-65dc-4f52-8aa4-9d0d4e438bbe" alt="Event Detail" width="500"/>
  <img src="https://github.com/user-attachments/assets/3613b3ed-e8b1-4d3c-be10-2c87a6ea2a34" alt="Event Detail" width="400"/>
</p>

## Future Improvements
- Develop RESTful APIs for cross-platform integration.
- Add interactive dashboards for real-time analytics.
- Implement audit logs for tracking changes.
- Enhance the GUI with modern frameworks (e.g., JavaFX, Swing with custom themes).

## Contributors
- **Mahdi TOUMI**: Project developer, database design, and implementation.

## License
This project is licensed under the MIT License. See the `LICENSE` file for details.

## Acknowledgments
This project was developed as part of a database management course at the École Nationale d'Ingénieurs de Carthage, Université de Carthage. Thanks to the faculty for guidance and to Mockaroo for facilitating mock data generation.


