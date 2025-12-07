package com.medicalsystem.dao;

import com.medicalsystem.model.Patient;
import com.medicalsystem.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PatientDAO {

    // Добавление пациента
    public void addPatient(Patient patient) {
        String sql = "INSERT INTO patients(first_name, last_name, birth_date, phone, iin) VALUES(?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, patient.getFirstName());
            pstmt.setString(2, patient.getLastName());
            pstmt.setString(3, patient.getBirthDate());
            pstmt.setString(4, patient.getPhone());
            pstmt.setString(5, patient.getIin());
            pstmt.executeUpdate();

            System.out.println("Пациент успешно добавлен: " +
                    patient.getFirstName() + " " + patient.getLastName());

        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE constraint failed")) {
                if (e.getMessage().contains("phone")) {
                    System.err.println("Ошибка: Пациент с таким номером телефона уже существует");
                } else if (e.getMessage().contains("iin")) {
                    System.err.println("Ошибка: Пациент с таким ИИН уже существует");
                } else {
                    System.err.println("Ошибка: Дублирующиеся данные пациента");
                }
            } else {
                System.err.println("Ошибка при добавлении пациента: " + e.getMessage());
            }
        }
    }

    // Получение всех пациентов
    public List<Patient> getAllPatients() {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT * FROM patients";

        try (Connection conn = DBConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                patients.add(new Patient(
                        rs.getInt("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("birth_date"),
                        rs.getString("phone"),
                        rs.getString("iin")
                ));
            }

        } catch (SQLException e) {
            System.err.println("Ошибка при получении списка пациентов: " + e.getMessage());
        }

        return patients;
    }

    // Удаление пациента по ID
    public boolean deletePatientById(int id) {
        String sql = "DELETE FROM patients WHERE id = ?";
        try (Connection conn = DBConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Ошибка при удалении пациента: " + e.getMessage());
            return false;
        }
    }

    // Обновление данных пациента
    public boolean updatePatient(Patient patient) {
        String sql = "UPDATE patients SET first_name = ?, last_name = ?, birth_date = ?, phone = ?, iin = ? WHERE id = ?";
        try (Connection conn = DBConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, patient.getFirstName());
            pstmt.setString(2, patient.getLastName());
            pstmt.setString(3, patient.getBirthDate());
            pstmt.setString(4, patient.getPhone());
            pstmt.setString(5, patient.getIin());
            pstmt.setInt(6, patient.getId());

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println(" Пациент обновлён: " + patient.getFirstName() + " " + patient.getLastName());
                return true;
            } else {
                System.out.println(" Пациент не найден для обновления (ID=" + patient.getId() + ")");
                return false;
            }

        } catch (SQLException e) {
            System.err.println("Ошибка при обновлении пациента: " + e.getMessage());
            return false;
        }
    }

    public Patient getPatientById(int id) {
        String sql = "SELECT id, first_name, last_name, birth_date, phone, iin FROM patients WHERE id = ?";
        try (Connection conn = DBConnection.connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Patient(
                            rs.getInt("id"),
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getString("birth_date"),
                            rs.getString("phone"),
                            rs.getString("iin")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при поиске пациента по ID: " + e.getMessage());
        }
        return null;
    }
}