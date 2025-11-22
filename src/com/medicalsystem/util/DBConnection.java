package com.medicalsystem.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {
    private static final String URL = "jdbc:sqlite:database.db";

    public static Connection connect() throws SQLException {
        try {
            // Явная регистрация драйвера SQLite
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Драйвер SQLite не найден: " + e.getMessage());
        }
        return DriverManager.getConnection(URL);
    }

    public static void initializeDatabase() {
        String sql = """
                CREATE TABLE IF NOT EXISTS patients (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    first_name TEXT NOT NULL,
                    last_name TEXT NOT NULL,
                    birth_date TEXT,
                    phone TEXT
                );

                CREATE TABLE IF NOT EXISTS doctors (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    first_name TEXT NOT NULL,
                    last_name TEXT NOT NULL,
                    specialization TEXT,
                    phone TEXT,
                    email TEXT
                );

                CREATE TABLE IF NOT EXISTS appointments (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    patient_id INTEGER,
                    doctor_id INTEGER,
                    date_time TEXT NOT NULL,
                    diagnosis TEXT,
                    FOREIGN KEY (patient_id) REFERENCES patients (id),
                    FOREIGN KEY (doctor_id) REFERENCES doctors (id)
                );
                """;

        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("✅ Таблицы успешно созданы или уже существуют.");
        } catch (SQLException e) {
            System.err.println("Ошибка при создании таблиц: " + e.getMessage());
        }
    }
}
