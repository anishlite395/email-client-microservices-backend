# 📧 Email Microservices System

A distributed email system built using Spring Boot and Microservices architecture.
It supports authentication, inbox reading, drafts, sent emails, and configuration management using IMAP/SMTP.

---

## 🏗️ Architecture Overview

```
Client (Frontend)
        ↓
API Gateway (JWT Validation)
        ↓
---------------------------------------
| Auth Service      (User + JWT)      |
| Config Service    (IMAP/SMTP config)|
| Inbox Service     (Read emails)     |
| Sent Service      (Sent mails)      |
| Drafts Service    (Draft handling)  |
| Email Service     (Send emails)     |
---------------------------------------
        ↓
Kafka (Event-driven communication)
        ↓
MySQL Databases
```

---

## 🚀 Tech Stack

* Java 17
* Spring Boot
* Spring Security (JWT)
* Spring Cloud Gateway
* OpenFeign (Service communication)
* Apache Kafka (Event-driven)
* MySQL
* JavaMail API (IMAP/SMTP)

---

## 🔐 Authentication Flow

1. User registers via `/auth/register`
2. Logs in via `/auth/login`
3. Receives JWT token
4. Token is validated at API Gateway
5. Gateway injects:

   ```
   X-User-Email: user@example.com
   ```
6. All downstream services use this header

---

## 📦 Services

### 🔑 Auth Service (Port: 8081)

* User registration & login
* JWT token generation
* Publishes `UserRegisteredEvent` to Kafka

---

### ⚙️ Config Service (Port: 8082)

* Stores IMAP/SMTP configuration
* Used by all email-related services

Endpoints:

```
POST /config/imap/{email}
GET  /config/imap/{email}

POST /config/smtp/{email}
GET  /config/smtp/{email}
```

---

### 📥 Inbox Service (Port: 8084)

* Reads emails from IMAP server

Endpoints:

```
GET /inbox
GET /inbox/{uid}
PUT /inbox/{uid}/read
DELETE /inbox/delete
```

---

### 📝 Drafts Service (Port: 8085)

* Save and manage drafts

Endpoints:

```
POST /drafts
GET /drafts
DELETE /drafts/delete
```

---

### 📤 Sent Service (Port: 8086)

* Read sent emails

Endpoints:

```
GET /sent
PUT /sent/read/{uid}
DELETE /sent/delete
```

---

### 🌐 API Gateway (Port: 8080)

* Central entry point
* JWT validation
* Routes requests to services

---

## 🔄 Event-Driven Flow

* `UserRegisteredEvent` is published via Kafka
* Other services can consume it for:

  * Initial setup
  * Notifications

---

## ⚠️ Security Notes

* JWT-based authentication
* Passwords are hashed using BCrypt
* ⚠️ DO NOT store or transmit raw passwords in events
* ⚠️ Replace Base64 encoding with proper encryption (AES)

---

## 🛠️ How to Run

### 1. Start Infrastructure

* MySQL
* Kafka + Zookeeper

---

### 2. Run Services (in order)

1. Auth Service
2. Config Service
3. API Gateway
4. Inbox / Drafts / Sent / Email Services

---

### 3. Test Flow

#### Register

```
POST /auth/register
```

#### Login

```
POST /auth/login
```

#### Use Token

```
Authorization: Bearer <JWT>
```

---

## 📌 Known Limitations

* IMAP folder names are hardcoded (Sent, Drafts)
* No pagination for emails
* Base64 used instead of encryption (needs improvement)
* No OAuth support (Gmail/Outlook)

---

## 🚀 Future Improvements

* OAuth2 integration (Google / Microsoft)
* AES encryption for credentials
* Pagination & search
* Attachment support
* Centralized mail service (reduce duplication)
* Redis caching
* Circuit breakers (Resilience4j)

---

## 👨‍💻 Author

Anish N

---

## 📄 License

MIT License
