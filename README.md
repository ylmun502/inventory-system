# Inventory Management System

A desktop inventory management system built using JavaFX and Maven, with a modular architecture. It currently uses SQLite for local data storage and will support Firebase for a future hybrid cloud version.

---

## 📦 Features

- Add, update, delete inventory items
- Organized MVC and DAO architecture
- SQLite integration
- Modular Java project with JavaFX
- Firebase integration planned

---

## 📁 Project Structure

- `app.java` – Main launcher
- `controller/` – UI control logic
- `dao/` – Database access logic
- `db/` – SQLite handling
- `model/` – Entity models
- `repository/` – Data interaction layer
- `util/`, `enum/`, `interfaces/`, `base/` – Supporting classes

---

## ⚙️ Requirements

- Java 17+
- Maven
- JavaFX SDK (imported via Maven)
- SQLite JDBC Driver

---

## 🚀 Run Instructions

1. Clone the repo:
   ```bash
   git clone https://github.com/ylmun502/inventory-system.git
   cd inventory-system

