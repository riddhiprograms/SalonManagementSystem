
# Salon Management System

(https://github.com/riddhiprograms/SalonManagementSystem/actions/workflows/build.yml)

[![GitHub issues](https://img.shields.io/github/issues/riddhiprograms/SalonManagementSystem)](https://github.com/riddhiprograms/SalonManagementSystem/issues)
[![GitHub stars](https://img.shields.io/github/stars/riddhiprograms/SalonManagementSystem)](https://github.com/riddhiprograms/SalonManagementSystem/stargazers)

## Description
Salon Management System is a web-based application designed to streamline salon operations. It allows admins to efficiently manage services, appointments, customers, and notification settings. Key features include dynamic service rendering, appointment scheduling, customer management, and a user-friendly card-flipper interface for settings.

---

## Table of Contents
- [Installation](#installation)  
- [Usage](#usage)  
- [Features](#features)  
- [Tech Stack](#tech-stack)  
- [Contributing](#contributing)  
- [License](#license)  
- [Contact](#contact)  

---

## Installation

### Prerequisites
- Java JDK 8 or higher  
- Apache Tomcat 9.x  
- MySQL 5.7 or higher  
- Maven (for dependency management)  
- Git  

### Steps

```bash
# Clone the repository
git clone https://github.com/riddhiprograms/SalonManagementSystem.git

# Navigate to the project directory
cd SalonManagementSystem

# Import the SQL schema
mysql -u your-username -p your-database < src/main/resources/salon_management.sql

# Update database credentials
# Edit src/main/java/com/salon/util/DatabaseConnection.java with your MySQL credentials

# Build the project with Maven
mvn clean install

# Deploy to Tomcat
# Copy the generated .war file from target/ to Tomcat's webapps directory

# Start Tomcat
# Run Tomcat's startup script
# Example: ./startup.sh (Linux/Mac) or startup.bat (Windows)
```

---

## Usage

- Open your web browser and go to:  
  `http://localhost:8080/SalonManagementSystem` (adjust port if you changed it)

- Log in as an admin using credentials from the `admins` table in your MySQL database.

- After login, you can use the admin panel to:

  - **Manage Services:**  
    Navigate to the **Services** tab to add, edit, or delete salon services.  
    Click **New Service** to open a modal form where you can fill in service details such as name, category, duration, price, and description.

  - **Schedule Appointments:**  
    Create new appointments or reschedule existing ones. The system validates customer emails and phone numbers to ensure accurate bookings.

  - **Manage Customers:**  
    Add walk-in customers by filling required fields such as name, email, and contact details. Manage customer profiles for better service tracking.

  - **Configure Notifications:**  
    Use the card-flipper interface to toggle notification settings including email and SMS reminders for appointments.

---

## Features

- **Service Management:**  
  Add, update, and delete salon services with categories dynamically populated.

- **Appointment Scheduling:**  
  Efficiently create, reschedule, or cancel appointments with validation to prevent conflicts.

- **Customer Management:**  
  Add new walk-in customers, manage profiles, and track service history.

- **Notification Settings:**  
  Customize email and SMS notifications with an interactive card-flipper UI.

- **Dynamic UI Rendering:**  
  Frontend uses JavaScript and modals to provide smooth real-time updates and user-friendly interactions.

- **Error Handling & Validation:**  
  Robust backend and frontend validation to ensure data integrity and smooth debugging.

---

## Tech Stack

- **Backend:** Java, Servlets, JDBC  
- **Frontend:** JSP, JavaScript, HTML, CSS, Bootstrap  
- **Database:** MySQL  
- **Build Tool:** Maven  
- **Server:** Apache Tomcat  

---

## Contributing

We welcome contributions! To contribute:

1. Fork the repository.  
2. Create a new branch for your feature or bug fix:  
   ```bash
   git checkout -b feature/your-feature-name
   ```  
3. Commit your changes with a descriptive message:  
   ```bash
   git commit -m "Add feature description"
   ```  
4. Push your changes to your fork:  
   ```bash
   git push origin feature/your-feature-name
   ```  
5. Open a Pull Request on the main repository.

Please follow the existing coding style and include tests if applicable.

---

## License

This project is for educational and portfolio purposes only.

---

## Contact

For questions or feedback, please reach out:

- GitHub: [riddhiprograms](https://github.com/riddhiprograms)  
- Project repository: [SalonManagementSystem](https://github.com/riddhiprograms/SalonManagementSystem)
