# Inventory Management System

> **Active Refactor in Progress**
>
> This project is under **active development** and currently undergoing an **intentional architectural**   
> **migration from MVC to MVVM**.
>  
> The original MVC implementation includes initial functionality (product, customer, and order modules).
> Midway through development, architectural limitations were identified — particularly around UI state 
> management and tight coupling caused by horizontal feature development.
>
> As a result, development shifted to a **vertical slice migration strategy** to refactor the application
> incrementally and establish clearer boundaries between UI, business logic, and data layers.

## Overview

This is a desktop inventory management system built with JavaFX and Maven using a modular architecture. It is designed for managing inventory in a small business environment.

The application currently utilizes **SQLite** for local data storage and is being refactored from an **MVC** to an **MVVM** architecture. There are future plans to integrate cloud-based storage so that the system can evolve into a hybrid version.

---

## Reasons for the current refactor

This application was originally implemented using an **MVC** architecture. As the codebase grew, limitations emerged around:
- Separation of concerns: Business logic and UI controllers became tightly coupled. 
- UI state management: handling complex view updates became difficult to track.
- Maintainability and scalability: Adding new modules required excessive changes to existing code.

The ongoing migration focuses on:
- Improving long-term maintainability by decoupling components.
- Introducing ViewModels for better UI logic isolation.
- Preparing the application for future features and unit testing.

This refactor is being performed incrementally to preserve existing functionality while enhancing overall architecture quality.

---

## Features (Current & Legacy)

- **Inventory Management:** Add, update, and delete inventory items.
- **Data Persistence:** Local storage using **SQLite** with DAO.
- **Modern Architecture:** Modular Java project using **JavaFX**.
- **Architecture Migration:** Currently refactoring from **MVC** to **MVVM** (in progress).

---

## Current Status

- The Main, Inventory, and Supplier views are accessible and stable.
- Some features are still in progress as part of the ongoing migration.
- The application is designed to remain stable during refactoring and does not crash during normal navigation.
- Development is currently focused on completing the Inventory module as a reference implementation before migrating remaining features.

---

## Why This Project Matters

This project reflects a realistic software engineering scenario where early design decisions must be revisited as system complexity grows. The refactor emphasizes:
- Improving separation of concerns using MVVM.
- Transitioning from horizontal feature development to vertical slicing.
- Enhancing long-term maintainability and testability.
- Practicing incremental migration instead of a full rewrite.
- The project prioritizes architectural correctness and long-term maintainability over short-term feature completeness.

---

## Project Structure

- `app.java` – Main launcher
- `controller/` – UI control logic
- `dao/` – Database access logic
- `db/` – SQLite handling
- `model/` – POJO entity models
- `service/` – Centralized business logic
- `util/`, `enum/`, `interfaces/`, `base/` – Supporting classes
- `viewmodel/` - Logic handling UI state and data binding.

---

## Requirements

- Java 17+
- Maven
- JavaFX SDK (imported via Maven)
- SQLite JDBC Driver

---

## Run Instructions

1. Clone the repo:
   ```bash
   git clone https://github.com/ylmun502/inventory-system.git
   cd inventory-system

