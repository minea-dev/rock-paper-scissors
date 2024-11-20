# Rock-Paper-Scissors Game (Front-End & Back-End)

This project is a full-stack **Rock-Paper-Scissors** game with the **Front-End built using Angular (v18)** and the **Back-End built using Spring Boot (v3.3)**. The game supports both single-player (against the computer) and multiplayer (two human players) modes.

## Features

### Front-End:
- Built with **Angular v18**.
- Interactive UI for playing the game.
- Real-time updates using **WebSockets**.
- Supports both single-player and multiplayer modes.

### Back-End:
- Built with **Spring Boot v3.3**.
- **REST API** to manage game creation and gameplay.
- **JWT Authentication** for securing the API.
- Database support using **MySQL**.
- **WebSocket** support for real-time communication.

## Requirements

### Front-End (Angular)
- **Node.js** (LTS version recommended).
- **Angular CLI** (Global installation).

### Back-End (Spring Boot)
- **Java 17+** (For compiling and running the application).
- **Maven** (For dependency management).
- **Spring Boot 3.3** (Backend framework).
- **MySQL** or another relational database.

---

## Setup Instructions

### 1. Clone the Repository

Clone this repository to your local machine:

```bash
git clone https://github.com/yourusername/rock-paper-scissors.git
```
### 2. Front-End Setup (Angular)
## 2.1. Navigate to the Front-End Directory
First, navigate to the front-end directory:

```bash
cd rock-paper-scissors/frontend
```
## 2.2. Install Dependencies
```bash
npm install
```
### 2.3. Configure the Front-End API URL
In src/environments/environment.ts, update the apiUrl and webUrl to match your local back-end and front-end URLs:
```bash
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080',  // Back-End URL
  webUrl: 'http://localhost:4200'   // Front-End URL
};
```
### 2.4. Serve the Angular Application
Start the development server:
```bash
ng serve
```
This will start the Angular app, and it will be available at http://localhost:4200.

## 3. Back-End Setup (Spring Boot)
## 3.1. Navigate to the Back-End Directory
Navigate to the back-end directory:

```bash
cd rock-paper-scissors/backend
```

## 3.2. Install Dependencies
Run the following Maven command to install the required dependencies:

```bash
mvn install
```

## 3.3. Configure the Database
The back-end uses MySQL.

1. Create a MySQL database (e.g., rockpaperscissors).
2. Update the src/main/resources/application.properties file with your MySQL credentials:
   
```bash
spring.application.name=back-end

# DB Configuration
spring.datasource.url= ## your datasource
spring.datasource.username= ## your username
spring.datasource.password= ## your password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA/Hibernate Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

# CORS Configuration
cors.allowedOrigins= # http://localhost:4200 or yours

# Logging Configuration
logging.level.org.springframework.web=DEBUG
logging.level.org.springframework.messaging=DEBUG
logging.level.org.springframework.web.socket=DEBUG
```
### 3.4. Run the Spring Boot Application
To run the back-end application:
```bash
mvn spring-boot:run
```
The back-end will be available at http://localhost:8080.
