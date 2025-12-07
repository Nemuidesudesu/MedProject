package com.medicalsystem.repository;

import com.medicalsystem.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PatientRepository {

    // Метод для добавления пациента
    public void addPatient(String firstName, String lastName, String birthDate, String phone) {
        String sql = "INSERT INTO patients(first_name, last_name, birth_date, phone) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, birthDate);
            pstmt.setString(4, phone);
            pstmt.executeUpdate();

            System.out.println("Пациент успешно добавлен: " + firstName + " " + lastName);
        } catch (SQLException e) {
            System.err.println("Ошибка при добавлении пациента: " + e.getMessage());
        }
    }

    // Метод для получения всех пациентов
    public List<String> getAllPatients() {
        List<String> patients = new ArrayList<>();
        String sql = "SELECT id, first_name, last_name, birth_date, phone FROM patients";

        try (Connection conn = DBConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String info = String.format("%d | %s %s | %s | %s",
                        rs.getInt("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("birth_date"),
                        rs.getString("phone"));
                patients.add(info);
            }

        } catch (SQLException e) {
            System.err.println("Ошибка при получении списка пациентов: " + e.getMessage());
        }
        return patients;
    }
}
