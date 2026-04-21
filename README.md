# 📧 Email Microservices System

A distributed **email management platform** built using **Spring Boot Microservices architecture**.
It supports full email lifecycle including **authentication, sending, receiving, drafts, scheduling, and mailbox provisioning using hMailServer**.

---

# 🏗️ System Architecture

```text id="arch-final-3"
Frontend (React / UI)
        ↓
API Gateway (JWT Authentication)
        ↓
------------------------------------------------------
Auth Service      → User Auth + JWT + Kafka Producer
Config Service    → IMAP / SMTP Configuration Store
Email Service     → SMTP Sending + Scheduling Engine
Inbox Service     → IMAP Inbox Reader
Sent Service      → IMAP Sent Reader
Drafts Service    → IMAP Draft Manager
------------------------------------------------------
        ↓
Kafka (Event-driven Communication)
        ↓
MySQL Databases
        ↓
hMailServer (Local IMAP + SMTP Server)
```

---

# 🚀 Tech Stack

* Java 17
* Spring Boot
* Spring Security (JWT)
* Spring Cloud Gateway
* Spring Cloud OpenFeign
* Apache Kafka
* Spring Scheduler
* JavaMail API
* MySQL
* hMailServer (Local Mail Server)
* JACOB (Windows COM Automation)

---

# 📮 Mail Server (hMailServer)

This system uses **hMailServer** as a local mail server for IMAP/SMTP operations.

## 🔧 Features

* IMAP → Inbox, Sent, Drafts
* SMTP → Email delivery
* Mailbox creation via COM API (JACOB)

## ⚙️ Configuration

| Setting   | Value     |
| --------- | --------- |
| IMAP Host | localhost |
| IMAP Port | 143       |
| SMTP Port | 25        |

---

# 🔐 Authentication Flow

1. User registers → `/auth/register`
2. Auth Service generates JWT
3. Kafka emits `UserRegisteredEvent`
4. Email Service:

   * Creates mailbox in hMailServer
   * Stores IMAP/SMTP config
5. Client uses JWT → API Gateway injects:

   ```
   X-User-Email
   ```

---

# 📦 MICRO SERVICES

---

## 🔑 Auth Service (8081)

### Responsibilities:

* User registration & login
* JWT generation
* Kafka event publishing

---

## ⚙️ Config Service (8082)

Stores:

* IMAP configuration
* SMTP configuration

---

## 📧 Email Service (8083) ⭐ CORE SERVICE

### 🚀 Responsibilities

* Send emails via SMTP
* Reply to emails
* Schedule emails for future delivery
* Store sent emails in DB
* Append emails to Sent folder (IMAP)
* Listen to Kafka events
* Auto-create mailboxes in hMailServer

---

### 📡 Endpoints

```http id="email-endpoints"
POST /email/send
POST /email/reply
GET  /email/scheduled
```

---

### 📤 Email Sending Flow

```text id="email-flow"
Client → API Gateway → Email Service
        → Config Service (SMTP credentials)
        → hMailServer SMTP
        → Save to Sent Folder (IMAP)
        → Save to DB (SentEmail)
```

---

### ⏰ Scheduling System

* Uses `@Scheduled(cron = "0 * * * * *")`
* Runs every minute
* Sends emails when scheduled time matches

---

## 📥 Inbox Service (8084)

* Reads emails from IMAP Inbox
* Supports read/unread operations

---

## 📝 Drafts Service (8085)

* Stores drafts in IMAP Draft folder
* Supports save / delete operations

---

## 📤 Sent Service (8086)

* Reads Sent emails from IMAP Sent folder
* Supports delete and mark-as-read

---

## 🌐 API Gateway (8080)

### Responsibilities:

* JWT validation
* Route requests to microservices
* Inject user identity header:

```
X-User-Email
```

---

# 🔄 EVENT-DRIVEN FLOW (Kafka)

## Topic:

```text id="kafka-topic"
user-registered-topic
```

## Flow:

```text id="kafka-flow"
Auth Service → Kafka → Email Service
```

### Used for:

* Mailbox creation
* Initial IMAP/SMTP setup
* Auto user provisioning

---

# 🧠 KEY FEATURES

## 📧 Email System

* Send emails (SMTP)
* Reply to emails
* Scheduled email delivery
* Sent folder sync

## 📥 IMAP System

* Inbox reading
* Sent reading
* Draft management

## ⚙️ Automation

* Auto mailbox creation in hMailServer
* Auto config creation after registration

---

# ⚠️ SECURITY NOTES

* JWT-based authentication
* Passwords stored using Base64 ⚠️ (NOT production safe)
* Should be replaced with AES encryption in production

---

# 📌 LIMITATIONS

* hMailServer is Windows-only
* No email attachments support
* No pagination/search
* Folder names are environment-dependent
* No distributed tracing yet

---

# 🚀 FUTURE IMPROVEMENTS

* AES encryption for credentials
* Email attachments support
* Redis caching layer
* Resilience4j circuit breaker
* Dockerized deployment
* Replace hMailServer with cloud SMTP (AWS SES / Gmail API)
* Pagination
* WebSocket notifications

---

# 📁 PROJECT STRUCTURE

```text id="structure-final-v2"
/auth-service
/config-service
/email-service   ⭐ CORE
/inbox-service
/sent-service
/drafts-service
/api-gateway
/shared-events
```

---

# 👨‍💻 AUTHOR

Your Name

---

# 📄 LICENSE

MIT License
