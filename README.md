# Inventory Management System

> 🚧 **Active Refactor in Progress**
>
> This project is currently undergoing a major architectural migration from **MVC** to **MVVM** as part of a
> deliberate refactoring effort.
>  
> During this phase, the application may be unstable or not fully runnable.
> The repository serves as a case study in real-world architectural migration by demonstrating the 
> architectural trade-offs and iterative improvement involved in decoupling a legacy system.

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

- Architecture Migration: Currently refactoring from **MVC** to **MVVM**.
- All views are temporarily non-functional.
- Active development is ongoing on a migration branch.

The current goal is to stabilize individual views incrementally before completing the full migration.

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

