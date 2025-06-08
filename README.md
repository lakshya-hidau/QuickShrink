# URL Shortener Service

A URL Shortener application built using **Spring Boot**, **React**, and **MySQL/PostgreSQL**. This project allows users to shorten long URLs, track usage statistics, and manage shortened links.

## Features

- **URL Shortening**: Generate short URLs for long links.
- **Custom Short URLs**: Option to create custom aliases for shortened URLs.
- **Usage Analytics**: Track the number of clicks and usage statistics for each shortened URL.
- **Expiration**: Set expiration dates for shortened URLs.
- **Frontend Integration**: React-based frontend for user interaction.
- **JWT Authentication**: Secure API endpoints using JSON Web Tokens.
- **Database Support**:
  - Development: MySQL
  - Production: PostgreSQL
- **Scheduling**: Automatically clean up expired URLs using scheduled tasks.
- **Transaction Management**: Ensure data consistency with Spring's transaction management.
- **Logging**: Detailed logging for debugging and monitoring.

## Technologies Used

### Backend
- **Java** (Spring Boot)
- **Hibernate/JPA** for ORM
- **JWT** for authentication
- **MySQL/PostgreSQL** for database

### Frontend
- **React** (JavaScript/TypeScript)
- **Yarn** for package management

### Others
- **Maven** for dependency management
- **Node.js** for frontend development
- **Docker** (optional for containerized deployment)

## Hosting URL - https://quickshrink.netlify.app/

## Environment Configuration

### Development (`.env`)
```dotenv
DATABASE_URL=jdbc:mysql://localhost:3306/url_shortener_db
DATABASE_USERNAME=root
DATABASE_PASSWORD=root
DATABASE_DRIVER=com.mysql.cj.jdbc.Driver
DATABASE_DIALECT=org.hibernate.dialect.MySQLDialect

JWTS_SECRET_KEY=your-secret-key
FRONTEND_URL=http://localhost:5173
