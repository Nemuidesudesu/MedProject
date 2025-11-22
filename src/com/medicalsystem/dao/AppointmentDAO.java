package com.medicalsystem.dao;

import com.medicalsystem.model.Appointment;
import com.medicalsystem.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDAO {

    public void addAppointment(Appointment a) {
        String sql = "INSERT INTO appointments(patient_id, doctor_id, date_time, diagnosis) VALUES(?, ?, ?, ?)";
        try (Connection conn = DBConnection.connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, a.getPatientId());
            ps.setInt(2, a.getDoctorId());
            ps.setString(3, a.getDateTime());
            ps.setString(4, a.getDiagnosis());
            ps.executeUpdate();
            System.out.println("✅ Приём создан: patientId=" + a.getPatientId() + " doctorId=" + a.getDoctorId());
        } catch (SQLException e) {
            System.err.println("Ошибка при создании приёма: " + e.getMessage());
        }
    }

    public List<Appointment> getAllAppointments() {
        List<Appointment> list = new ArrayList<>();
        String sql = "SELECT id, patient_id, doctor_id, date_time, diagnosis FROM appointments ORDER BY date_time DESC";
        try (Connection conn = DBConnection.connect(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Appointment(
                        rs.getInt("id"),
                        rs.getInt("patient_id"),
                        rs.getInt("doctor_id"),
                        rs.getString("date_time"),
                        rs.getString("diagnosis")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении приёмов: " + e.getMessage());
        }
        return list;
    }
}
