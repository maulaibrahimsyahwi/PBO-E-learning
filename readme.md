# LMS SMK Nusantara

A desktop-based Learning Management System (LMS) built using **Java Swing**. This application facilitates the management of academic activities between Admins, Teachers, and Students.

## 🛠️ Technologies

- **Language:** Java (JDK 21)
- **GUI:** Java Swing (with FlatLaf Theme)
- **Database:** MySQL
- **Build Tool:** Maven
- **Security:** JBCrypt (for password _hashing_)

## ⚠️ Database Requirements (IMPORTANT)

This application **must use a MySQL database**. Without the database, the application will not run.

1.  **Download Database:**
    Get the required database file via this link:
    👉 **[Access Database (Google Drive)](https://drive.google.com/drive/folders/15DjpqCIdEy0w1yQQ5s5CsKpRQcD8XPxk?usp=sharing)**

2.  **Import Database:**

    - Create a new database in MySQL named `pbo_elearning`.
    - Import the downloaded SQL file into that database.

3.  **Configure Connection:**
    Ensure the settings in `src/main/java/config/DatabaseConnection.java` match your MySQL configuration:
    ```java
    config.setJdbcUrl("jdbc:mysql://localhost:3306/pbo_elearning");
    config.setUsername("root");
    config.setPassword("");
    ```

## 🚀 How to Run

1.  **Clone Repository:**
    Open your terminal or CMD and run the following command:

    ```bash
    git clone [https://github.com/maulaibrahimsyahwi/pbo-e-learning.git](https://github.com/maulaibrahimsyahwi/pbo-e-learning.git)
    ```

2.  **Open Project:**
    Open the project folder using your preferred IDE (VS Code, IntelliJ IDEA, or NetBeans) as a **Maven Project**.

3.  **Run:**
    Locate and run the main file: `src/main/java/app/App.java`.

## 👤 Default Account

When the application is run for the first time (and the database has been imported), you can log in using:

- **Username:** `admin`
- **Password:** `admin`

## 🌟 Key Features

- **Admin:** Manage Teacher, Student, Class, and Subject data.
- **Teacher:** Manage attendance, upload materials, create assignments/exams, and grade.
- **Student:** View materials, answer assignments/exams, view grades, and discussion forums.
