# GateKeeper

**A Multi-Tenant API Gateway and API Management Platform**

GateKeeper is a backend platform designed to help organizations manage their APIs through a centralized management plane, a smart runtime gateway, and an event-driven analytics system.

The goal is to build a production-oriented platform demonstrating secure API management, dynamic routing, distributed systems, asynchronous communication, observability, and scalable backend engineering.

> **Status:** Under active development. This repository contains three microservices, with implementation progressing independently across each service.

## Project Vision

The final goal of GateKeeper is to allow organizations to register with the platform, configure their APIs, define access policies, and route client requests through a centralized gateway.

The platform is intended to support:

- Multi-tenant organization and user management.
- Secure onboarding and role-based access control.
- Dynamic API route configuration and policy management.
- Runtime request routing to backend services.
- Authentication, authorization, rate limiting, and request controls.
- Distributed configuration updates and asynchronous event processing.
- API usage analytics, reporting, and operational monitoring.
- Database migrations, automated testing, containerization, and CI/CD.

These are the intended capabilities of the overall platform. Individual features will be marked as implemented as development and testing progress.

## Architecture Overview

GateKeeper is maintained as a **monorepo containing three independent microservices**.

### 1. API Management Service

The control plane and source of truth for organizations and API configuration.

Its responsibilities include:

- Organization registration and lifecycle management.
- Management-user onboarding and account setup.
- Role-based access control for management operations.
- API route and policy configuration.
- Route history and audit records.
- Publishing configuration-change events for downstream services.

**Detailed documentation:** [API Management Service](./api-management-service/README.md)

### 2. Smart Gateway Service

The runtime entry point for client API traffic.

Its intended responsibilities include:

- Receiving client API requests.
- Resolving organization-specific route configurations.
- Forwarding requests to the appropriate backend services.
- Enforcing authentication, authorization, and configured policies.
- Applying rate limits, caching, timeouts, and request controls.
- Publishing access events for analytics.
- Refreshing routing configuration when management changes occur.

The goal is to make routing and policy changes manageable without requiring application redeployment for every configuration update.

**Detailed documentation:** [Smart Gateway Service](./smart-gateway-service/README.md)

### 3. Analytics Service

The event-driven component responsible for collecting and analyzing API usage.

Its intended responsibilities include:

- Consuming API access events from Kafka.
- Processing request and usage metrics.
- Storing analytics data.
- Supporting usage summaries and reporting.
- Providing a foundation for operational dashboards and organization-level insights.

MongoDB is intended for flexible analytics storage, while Kafka provides asynchronous event delivery between services.

**Detailed documentation:** [Analytics Service](./analytics-service/README.md)

## Technology Stack

The platform's planned and currently used technologies include:

| Technology | Purpose |
|---|---|
| Java | Backend development |
| Spring Boot | Microservice framework |
| Spring Security | Authentication and authorization |
| Spring Data JPA | Relational persistence |
| MySQL | Organization and management data |
| Apache Kafka | Asynchronous messaging and configuration notifications |
| MongoDB | Analytics data storage |
| Redis | Caching and rate limiting |
| Flyway | Database schema migrations |

The technologies listed here describe the platform's current and intended stack. Their implementation may differ between services as development progresses.

## Event-Driven Communication

Kafka is intended to support asynchronous communication between the microservices.

Examples include:

- Setup-invitation events published during management-user onboarding.
- Route-configuration change notifications consumed by the Smart Gateway Service.
- API access events consumed by the Analytics Service.

The intended design separates synchronous API operations from asynchronous event processing where appropriate.

## Repository Organization

The repository contains all three microservices under one Git repository. Each service will maintain its own detailed documentation.

```text
GateKeeper/
├── api-management-service/
│   └── README.md
├── smart-gateway-service/
│   └── README.md
├── analytics-service/
│   └── README.md
├── README.md
└── .gitignore
```

## Engineering Principles

GateKeeper is being developed with an emphasis on:

- Clear microservice responsibilities.
- Modular, layered application design.
- SOLID principles and maintainable abstractions.
- Secure credential and token handling.
- Explicit authorization and input validation.
- Reliable asynchronous communication.
- Automated testing and observable system behavior.
- Incremental development and documentation.

## Documentation

Each microservice will have its own README covering its detailed architecture, package structure, endpoints, configuration, dependencies, database schema, event contracts, testing, and local execution.

This root README provides the platform-level overview.

## License

A license has not yet been selected. Until one is added, all rights remain with the copyright holder.