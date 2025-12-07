package com.medicalsystem.dao;

import com.medicalsystem.model.Appointment;
import com.medicalsystem.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// AppointmentDAO stores doctor name in DB (column `doctor` TEXT) but web UI uses doctorId.
// To keep compatibility we accept Appointment objects with doctorId set: when adding/updating,
// we try to resolve doctor name via DoctorDAO (if doctorId>0) and store the name.
public class AppointmentDAO {
    
    private final AppointmentTypeDAO typeDAO = new AppointmentTypeDAO();

    public List<Appointment> getAllAppointments() {
        List<Appointment> list = new ArrayList<>();
        String sql = "SELECT id, patient_id, doctor_id, date_time, diagnosis, type_id FROM appointments";
        try (Connection conn = DBConnection.connect(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                // we can now reconstruct doctorId from DB
                Appointment appt = new Appointment(
                        rs.getInt("id"),
                        rs.getInt("patient_id"),
                        rs.getInt("doctor_id"),
                        rs.getString("date_time"),
                        rs.getString("diagnosis")
                );
                int typeId = rs.getInt("type_id");
                appt.setTypeId(typeId);
                appt.setTypeName(typeDAO.getAppointmentTypeName(typeId));
                list.add(appt);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении приёмов: " + e.getMessage());
        }
        return list;
    }

    public boolean addAppointment(Appointment a) {
        String sql = "INSERT INTO appointments(patient_id, doctor_id, date_time, diagnosis, type_id) VALUES(?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, a.getPatientId());
            ps.setInt(2, a.getDoctorId());
            ps.setString(3, a.getDateTime());
            ps.setString(4, a.getDiagnosis());
            ps.setInt(5, a.getTypeId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE constraint failed")) {
                System.err.println("Ошибка: У выбранного врача уже есть приём в это время");
            } else {
                System.err.println("Ошибка при добавлении приёма: " + e.getMessage());
            }
            return false;
        }
    }

    public boolean updateAppointment(Appointment a) {
        String sql = "UPDATE appointments SET patient_id = ?, doctor_id = ?, date_time = ?, diagnosis = ?, type_id = ? WHERE id = ?";
        try (Connection conn = DBConnection.connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, a.getPatientId());
            ps.setInt(2, a.getDoctorId());
            ps.setString(3, a.getDateTime());
            ps.setString(4, a.getDiagnosis());
            ps.setInt(5, a.getTypeId());
            ps.setInt(6, a.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Ошибка при обновлении приёма: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteAppointmentById(int id) {
        String sql = "DELETE FROM appointments WHERE id = ?";
        try (Connection conn = DBConnection.connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Ошибка при удалении приёма: " + e.getMessage());
            return false;
        }
    }

    public Appointment getAppointmentById(int id) {
        String sql = "SELECT id, patient_id, doctor_id, date_time, diagnosis, type_id FROM appointments WHERE id = ?";
        try (Connection conn = DBConnection.connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Appointment appt = new Appointment(
                            rs.getInt("id"),
                            rs.getInt("patient_id"),
                            rs.getInt("doctor_id"),
                            rs.getString("date_time"),
                            rs.getString("diagnosis")
                    );
                    int typeId = rs.getInt("type_id");
                    appt.setTypeId(typeId);
                    appt.setTypeName(typeDAO.getAppointmentTypeName(typeId));
                    return appt;
                }
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при поиске приёма: " + e.getMessage());
        }
        return null;
    }
}
