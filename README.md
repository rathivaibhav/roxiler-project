# Store Rating Application (Full Stack Intern Project)

A full-stack application allowing users to register, login, and rate stores. Deployed on AWS EC2 using Docker, Nginx, and PostgreSQL (RDS).

**Live Demo:** [https://gurugram.tech](https://gurugram.tech)

## 🏗 Architecture
* **Frontend:** React.js (Tailwind CSS)
* **Backend:** Java Spring Boot 3 (Spring Security, JPA)
* **Database:** PostgreSQL (AWS RDS)
* **Deployment:** Docker & Docker Compose
* **Reverse Proxy:** Nginx with SSL (Cloudflare)
* **Cloud Provider:** AWS EC2 (Ubuntu)

## 🚀 Features
* **Role-Based Access Control (RBAC):**
    * *System Admin:* Create Stores, Manage Users, Dashboard Analytics.
    * *Store Owner:* View dashboard for their assigned stores.
    * *User:* Search and Rate stores (1-5 stars).
* **Secure Authentication:** JWT (JSON Web Tokens) with BCrypt password hashing.
* **Responsive Design:** Works on mobile and desktop.

## 🛠 Setup & Installation

### 1. Clone the repository
```bash
git clone [https://github.com/YOUR_USERNAME/rating-app.git](https://github.com/YOUR_USERNAME/rating-app.git)
cd rating-app