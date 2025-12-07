package com.medicalsystem.dao;

import com.medicalsystem.model.AppointmentType;
import com.medicalsystem.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppointmentTypeDAO {
    
    private static final Map<Integer, String> typeNameCache = new HashMap<>();
    
    public List<AppointmentType> getAllAppointmentTypes() {
        List<AppointmentType> list = new ArrayList<>();
        String sql = "SELECT id, name, description FROM appointment_types";
        try (Connection conn = DBConnection.connect(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                AppointmentType at = new AppointmentType(id, name, rs.getString("description"));
                list.add(at);
                typeNameCache.put(id, name);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении типов приёмов: " + e.getMessage());
        }
        return list;
    }
    
    public String getAppointmentTypeName(int typeId) {
        // Проверим кэш
        if (typeNameCache.containsKey(typeId)) {
            return typeNameCache.get(typeId);
        }
        
        // Загрузим из БД
        String sql = "SELECT name FROM appointment_types WHERE id = ?";
        try (Connection conn = DBConnection.connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, typeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String name = rs.getString("name");
                    typeNameCache.put(typeId, name);
                    return name;
                }
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при поиске типа приёма: " + e.getMessage());
        }
        return "";
    }
    
    public static void clearCache() {
        typeNameCache.clear();
    }
}
