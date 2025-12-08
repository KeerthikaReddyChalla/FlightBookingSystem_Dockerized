# **Flight Booking Microservices Application**

This is a Flight Booking System built using Spring Boot Microservices, MongoDB, Spring Security, RabbitMQ, Eureka, Spring Cloud Config, and Docker. The system allows users to search flights, book tickets, generate PNRs, view booking history, and receive booking notifications via email. Administrators can manage flight inventory, seats, and airline data. The entire application is fully containerized using **Docker Compose.**

## Project Overview

This system uses:

-MongoDB database

-Feign Client for inter-service calls

-Resilience4j Circuit Breaker for fault tolerance

-RabbitMQ for communication

-JWT-based authentication through API Gateway

-Configuration management via Spring Cloud Config

-Service discovery using Eureka

-Docker for containerization

## System architecture
<img width="2437" height="1455" alt="image" src="https://github.com/user-attachments/assets/3f77d84b-2c42-48ee-b13c-ad517817b4bb" />
