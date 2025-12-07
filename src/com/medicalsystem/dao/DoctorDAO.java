package com.medicalsystem.dao;

import com.medicalsystem.model.Doctor;
import com.medicalsystem.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DoctorDAO {

    public void addDoctor(Doctor d) {
        String sql = "INSERT INTO doctors(first_name, last_name, specialization, phone, email, iin) VALUES(?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, d.getFirstName());
            ps.setString(2, d.getLastName());
            ps.setString(3, d.getSpecialization());
            ps.setString(4, d.getPhone());
            ps.setString(5, d.getEmail());
            ps.setString(6, d.getIin());
            ps.executeUpdate();
            System.out.println(" Доктор добавлен: " + d.getFullName());
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE constraint failed")) {
                if (e.getMessage().contains("phone")) {
                    System.err.println("Ошибка: Врач с таким номером телефона уже существует");
                } else if (e.getMessage().contains("iin")) {
                    System.err.println("Ошибка: Врач с таким ИИН уже существует");
                } else {
                    System.err.println("Ошибка: Дублирующиеся данные врача");
                }
            } else {
                System.err.println("Ошибка при добавлении доктора: " + e.getMessage());
            }
        }
    }

    public List<Doctor> getAllDoctors() {
        List<Doctor> list = new ArrayList<>();
        String sql = "SELECT id, first_name, last_name, specialization, phone, email, iin FROM doctors";
        try (Connection conn = DBConnection.connect(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Doctor(
                        rs.getInt("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("specialization"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getString("iin")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении списка докторов: " + e.getMessage());
        }
        return list;
    }

    public boolean hasAppointments(int doctorId) {
        String sql = "SELECT COUNT(*) FROM appointments WHERE doctor_id = ?";
        try (Connection conn = DBConnection.connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, doctorId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при проверке записей врача: " + e.getMessage());
        }
        return false;
    }

    public boolean deleteDoctorById(int id) {
        // Проверяем, есть ли у врача записи о приемах
        if (hasAppointments(id)) {
            System.err.println("Ошибка: Невозможно удалить врача. У него есть записи о приемах");
            return false;
        }

        String sql = "DELETE FROM doctors WHERE id = ?";
        try (Connection conn = DBConnection.connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Ошибка при удалении доктора: " + e.getMessage());
            return false;
        }
    }

    public Doctor getDoctorById(int id) {
        String sql = "SELECT id, first_name, last_name, specialization, phone, email, iin FROM doctors WHERE id = ?";
        try (Connection conn = DBConnection.connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Doctor(
                            rs.getInt("id"),
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getString("specialization"),
                            rs.getString("phone"),
                            rs.getString("email"),
                            rs.getString("iin")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при поиске доктора по ID: " + e.getMessage());
        }
        return null;
    }

    public boolean updateDoctor(Doctor d) {
        String sql = "UPDATE doctors SET first_name = ?, last_name = ?, specialization = ?, phone = ?, email = ?, iin = ? WHERE id = ?";
        try (Connection conn = DBConnection.connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, d.getFirstName());
            ps.setString(2, d.getLastName());
            ps.setString(3, d.getSpecialization());
            ps.setString(4, d.getPhone());
            ps.setString(5, d.getEmail());
            ps.setString(6, d.getIin());
            ps.setInt(7, d.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Ошибка при обновлении доктора: " + e.getMessage());
            return false;
        }
    }
}
