# 📚 Library Management System

A Spring Boot backend application for managing books.

## 🚀 Features

- Add a new book
- View all books
- View a book by ID
- Update book details
- Delete a book
- Search books by title

## 🛠 Tech Stack

- Java
- Spring Boot
- Spring Data JPA
- MySQL
- Maven

## 📁 Project Structure

- Controller → handles HTTP requests
- Service → business logic
- Repository → database access
- Entity → database mapping

## 📡 API Endpoints

| Method | Endpoint     | Description       |
|--------|--------------|-------------------|
| POST   | /books       | Create a book     |
| GET    | /books       | Get all books     |
| GET    | /books/{id}  | Get book by ID    |
| PUT    | /books/{id}  | Update book       |
| DELETE | /books/{id}  | Delete book       |

## ▶️ How to Run

1. Configure MySQL in `application.properties`
2. Run the Spring Boot application
3. Test APIs using Postman

## 👤 Author

Jiwei Wu
