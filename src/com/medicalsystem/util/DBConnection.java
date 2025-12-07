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
                    phone TEXT UNIQUE,
                    iin TEXT UNIQUE
                );

                CREATE TABLE IF NOT EXISTS doctors (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    first_name TEXT NOT NULL,
                    last_name TEXT NOT NULL,
                    specialization TEXT,
                    phone TEXT UNIQUE,
                    email TEXT,
                    iin TEXT UNIQUE
                );

                CREATE TABLE IF NOT EXISTS appointment_types (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL UNIQUE,
                    description TEXT
                );

                CREATE TABLE IF NOT EXISTS appointments (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    patient_id INTEGER,
                    doctor_id INTEGER,
                    date_time TEXT NOT NULL,
                    diagnosis TEXT,
                    type_id INTEGER,
                    FOREIGN KEY (patient_id) REFERENCES patients (id),
                    FOREIGN KEY (doctor_id) REFERENCES doctors (id),
                    FOREIGN KEY (type_id) REFERENCES appointment_types (id),
                    UNIQUE(doctor_id, date_time)
                );
                """;

        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            
            // Вставляем типы приёмов, если они еще не добавлены
            String insertTypes = "INSERT OR IGNORE INTO appointment_types (id, name, description) VALUES " +
                    "(1, 'Платная', 'Платный приём'), " +
                    "(2, 'По страховке', 'Приём по страховому полису'), " +
                    "(3, 'Экстренная', 'Экстренная помощь'), " +
                    "(4, 'По месту закрепления', 'Приём по месту закрепления пациента')";
            stmt.executeUpdate(insertTypes);
            
            System.out.println("Таблицы успешно созданы или уже существуют.");
        } catch (SQLException e) {
            System.err.println("Ошибка при создании таблиц: " + e.getMessage());
        }
    }
}
