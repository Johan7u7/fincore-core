# FinCore Engine

[![Java](https://img.shields.io/badge/Java-17%20LTS-orange.svg)](https://www.oracle.com/java/)
[![Maven](https://img.shields.io/badge/Build-Maven-C71A36.svg)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

An enterprise-grade core engine for high-throughput transactional processing, domain validation, and ledger management. Built with modern Java standards, clean architecture patterns, and domain immutability.

---

## Technical Stack

* **Language:** Java 17 (LTS)
* **Build Tool:** Apache Maven
* **Testing:** JUnit 5 (Jupiter)
* **Design Standards:** Immutable Value Objects, Domain-Driven Design (DDD) concepts, Conventional Commits

---

## Domain Features Implemented

* **Core Transaction Entity (`Transaction.java`):**
  * Strict validation guards against non-positive amounts.
  * Explicit type handling (`DEPOSIT`, `WITHDRAWAL`).
  * Identity immutability enforced using `UUID` and `LocalDateTime`.
  * Overridden `equals` and `hashCode` contract for financial ledger integrity.

---

## Getting Started

### Prerequisites
* JDK 17 or higher
* Apache Maven 3.8+ (or IDE wrapper)

### Build & Run
Compile the application:
```bash
mvn clean compile


Java: Resolve Workspace Problems