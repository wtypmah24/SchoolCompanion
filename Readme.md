# Kinder Compass Backend

![License](https://img.shields.io/badge/license-MIT-blue.svg)
![Java](https://img.shields.io/badge/Java-17-blue.svg)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.4.4-green.svg)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16.2-blue.svg)
![Redis](https://img.shields.io/badge/Redis-7.4.1-orange.svg)
![Docker](https://img.shields.io/badge/Docker-ready-blue.svg)
![OpenAI](https://img.shields.io/badge/OpenAI-integrated-purple.svg)

## Project Overview

**Kinder Compass Backend** is a powerful backend system designed to support school companions in managing children under
their care. It includes intelligent recommendations, monitoring, task and goal tracking, event scheduling, and automated
communication via Telegram and Email.

The system is built on a modern technology stack with an emphasis on modularity, scalability, and integration with AI
and analytics tools.

---

## Key Features

- 🕒 Scheduled Telegram and Email notifications with delayed message processing (Redis)
- 📬 Integration with Brevo (Sendinblue) for email reminders
- 🧠 OpenAI integration for smart chat and recommendations
- 📊 Chart generation with JFreeChart (line, bar) for child monitoring data
- 📄 PDF generation with Apache PDFBox including charts and child information
- 🔐 OAuth2 and JWT-based authentication and authorization
- 📦 Docker and Docker Compose ready for deployment
- 🔧 Database versioning with Liquibase
- 📜 OpenAPI documentation with Swagger UI
- 📈 Spring Boot Actuator endpoints for monitoring
- 🧼 Request/response logging with sensitive data masking
- 🌐 External API integrations using Spring Cloud OpenFeign

---

## Technology Stack

- **Language:** Java 17
- **Framework:** Spring Boot 3.4.4
- **Database:** PostgreSQL, Redis
- **ORM:** Hibernate, JPA
- **Security:** Spring Security, OAuth2, JWT
- **Messaging:** Telegram Bots API, Brevo Email Client
- **PDF & Charts:** Apache PDFBox, JFreeChart
- **AI:** OpenAI Assistant API
- **Documentation:** Springdoc OpenAPI
- **Monitoring:** Spring Boot Actuator
- **Build Tools:** Maven or Gradle
- **Others:** MapStruct, Lombok, Jackson, Liquibase, Docker, Docker Compose

---

## Core Services

| Service                  | Responsibility                                                                   |
|--------------------------|----------------------------------------------------------------------------------|
| `ChartService`           | Generates line and bar charts for monitoring data using JFreeChart               |
| `ChildService`           | Manages child profiles under companion care                                      |
| `CompanionService`       | Handles companion-related operations                                             |
| `EventService`           | Manages creation, updates, and notifications for events                          |
| `GoalService`            | Manages goals set for children                                                   |
| `MonitoringEntryService` | Records and processes monitoring data                                            |
| `MonitoringParamService` | Supports binary, quantitative, and scale-type monitoring parameters              |
| `MessageQueueService`    | Schedules and processes Redis-based messaging queues (Telegram, email, PDF jobs) |
| `PdfGeneratorService`    | Generates PDF reports with charts and child data for Telegram delivery           |
| `OpenAiService`          | Interacts with OpenAI API for smart responses and assistant threads              |
| `NoteService`            | Handles companion notes and annotations                                          |
| `SpecialNeedService`     | Manages special needs flags for children                                         |
| `TaskService`            | Manages tasks and to-dos for companions and children                             |
| `TokenService`           | Handles OAuth2 token operations and validation                                   |
| `CompanionJwtFilter`     | Filters and authenticates companion tokens on protected routes                   |

---

## Installation & Running

1. Clone the repository:

   ```bash
   git clone https://github.com/your-org/kinder-compass-backend.git
   cd kinder-compass-backend

2. Set up environment variables (.env or application.yml) for DB, Redis, and API keys.
3. Build the project:
   ```bash
   ./mvnw clean install
4. Run the application:
   ```bash
   ./mvnw spring-boot:run
5. Or use Docker:
   ```bash
   docker-compose up --build

---

## 📚 API Documentation

Once the application is running, you can explore the API using Swagger UI:

🔗 [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

---

## 🩺 Monitoring & Health

Spring Boot Actuator endpoints provide operational insights into the system:

- Health check: [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)
- Application info: [http://localhost:8080/actuator/info](http://localhost:8080/actuator/info)

---

## 🤝 Contribution

We welcome all contributions — bug reports, feature requests, and pull requests are greatly appreciated!

To contribute:

1. Fork the repository
2. Create a new feature branch
3. Commit your changes
4. Open a pull request

Let’s make **Kinder Compass** better together!

---

## 📄 License

This project is licensed under the **MIT License**.  

