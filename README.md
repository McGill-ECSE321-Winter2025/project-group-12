# Boardr (Group 12)

Welcome to the **Boardr** repository! This project is designed to create a platform for board game lovers to connect, share their games, and organize events. Whether you're a **Player** or a **Game Owner**, this app will help you manage your board game experiences seamlessly.

## Project Overview

The app supports two types of accounts:
- **Players**: Can browse games, participate in events, and leave a review.
- **Game Owners**: Can manage their board game collections, receive borrowing requests, and track lending history.

### Key Features:
- **User Authentication**: Users must log in to access the app's features.
- **Personal Account Page**: Displays account type, name, owned games (for Game Owners), and event participation history.
- **Game Management**: Game Owners can manage their collections, accept/decline borrowing requests, and track lending history.
- **Event Creation**: Users can create events with details like date, time, location, description, maximum participants, and the board game to be played.
- **Event Registration**: Users can register for events if slots are available.
- **Game Browsing**: All users can browse a list of available board games, see reviews, and check ownership details.

## Running the Application Locally
1. Ensure you have Java 17 (or the version your project uses) and Gradle installed.
2. Clone the repository:
   ```bash
   git clone <repository-url>
   cd boardr-backend
3. Build the project:
   ```bash
   ./gradlew build
4. Run the project
   ```bash
   ./gradlew bootrun
5. The application will be deployed to http://localhost:8080/
6. If you wish to only run integration tests, use the following:
   ```bash
   ./gradlew integrationTest

## Database Configuration

Before running the application, ensure that your database is properly configured. The application uses the following default credentials for the database:

- **Database Username**: `postgres`
- **Database Password**: `boardr`

You can update these credentials in the `application.properties` file located in the `src/main/resources` directory.

Example:
```properties
spring.datasource.username = <your-username>
spring.datasource.password = <your-password>
```

## Team Members

| Name                                              | Role                           | Deliverable 1 Effort (hours) | Deliverable 2 Effort (hours) | Deliverable 3 Effort (hours) | Total Effort (hours) |
|---------------------------------------------------|--------------------------------|------------------------------|------------------------------|------------------------------|----------------------|
| Kyujin [@3MinCurry](https://github.com/3MinCurry) | QA Tester                      | 8                          | 12                           | TBD                          | TBD                  |
| Jun Ho [@Junho332](https://github.com/Junho322)   | QA Tester                      | 8                          | 12                           | TBD                          | TBD                  |
| David Vo [@Kasamix](https://github.com/Kasamix)   | Project Manager                | 8                          | 12                           | TBD                          | TBD                  |
| David Zhou [@Rampex1](https://github.com/Rampex1) | Software Developer             | 8                          | 12                           | TBD                          | TBD                  |
| Eric [@Lawnless1](https://github.com/Lawnless1)   | Software Developer             | 8                          | 12                           | TBD                          | TBD                  |
| Jione [@Ji-One1](https://github.com/Ji-One1)      | Software Architect             | 8                          | 12                           | TBD                          | TBD                  |
| Yoon [@Yoonicornn](https://github.com/Yoonicornn) | QA Developer                   | 8                          | 12                           | TBD                          | TBD                  |

## Deliverables

### Deliverable 1: Requirements Model, Domain Model, Persistence Layer, and Project Management
- **[Project Deliverable 1: Domain Model and Report](https://github.com/McGill-ECSE321-Winter2025/project-group-12/wiki/Deliverable-1-Report)**
- **Requirements Model**: All
- **Domain Model Generation**: Yoon, Kyujin, David Vo, David Zhou
- **Domain Model Code**: Jione, David Zhou, Eric Deng
- **Persistence Layer**: David Zhou, Jione
- **Persistence Layer Testing**: Yoon, Kyujin, Jun Ho
- **Project Management**: Yoon, David Zhou, David Vo

### Deliverable 2: REST APIs, Unit Testing, Integration Testing, and Project Management
- **[Project Deliverable 2: Report](https://github.com/McGill-ECSE321-Winter2025/project-group-12/wiki/Deliverable-2-Report)**
- **REST APIs**:
  - User Account: Kyujin
  - Borrow Request & Review: Yoon
  - Board Game & Board Game Instance: Jun Ho
  - Event & Registration: David Vo
- **Unit Testing**:
  - User Account: Eric
  - Borrow Request & Review: Kyujin
  - Board Game & Board Game Instance: David Zhou
  - Event & Registration: Jun Ho
- **Integration Testing**: Ji One, Eric, David Zhou, David Vo, Yoon
  - User Account: David Vo
  - Borrow Request & Review: Eric
  - Board Game & Board Game Instance: Jione
  - Event & Registration: David Zhou
- **Project Management**:
  - REST APIs: David Vo
  - Software QA Plan and Report: Yoon, Kyujin
  - ReadMe: David Vo, David Zhou
  - Javadoc: Eric, Jione
  - Meeting Minutes: Jun Ho
  - Project Report: Kyujin

### Deliverable 3: TBD
- **Scope**: TBD
- **Effort**: TBD


