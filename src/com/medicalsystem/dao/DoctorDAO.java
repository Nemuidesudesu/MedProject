package com.medicalsystem.dao;

import com.medicalsystem.model.Doctor;
import com.medicalsystem.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DoctorDAO {

    public void addDoctor(Doctor d) {
        String sql = "INSERT INTO doctors(first_name, last_name, specialization, phone, email) VALUES(?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, d.getFirstName());
            ps.setString(2, d.getLastName());
            ps.setString(3, d.getSpecialization());
            ps.setString(4, d.getPhone());
            ps.setString(5, d.getEmail());
            ps.executeUpdate();
            System.out.println("✅ Доктор добавлен: " + d.getFullName());
        } catch (SQLException e) {
            System.err.println("Ошибка при добавлении доктора: " + e.getMessage());
        }
    }

    public List<Doctor> getAllDoctors() {
        List<Doctor> list = new ArrayList<>();
        String sql = "SELECT id, first_name, last_name, specialization, phone, email FROM doctors";
        try (Connection conn = DBConnection.connect(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Doctor(
                        rs.getInt("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("specialization"),
                        rs.getString("phone"),
                        rs.getString("email")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении списка докторов: " + e.getMessage());
        }
        return list;
    }

    public boolean deleteDoctorById(int id) {
        String sql = "DELETE FROM doctors WHERE id = ?";
        try (Connection conn = DBConnection.connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Ошибка при удалении доктора: " + e.getMessage());
            return false;
        }
    }
}
