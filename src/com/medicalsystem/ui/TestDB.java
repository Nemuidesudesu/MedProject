package com.medicalsystem.ui;

import com.medicalsystem.util.DBConnection;
import com.medicalsystem.dao.PatientDAO;
import com.medicalsystem.model.Patient;
import java.util.List;

public class TestDB {
    public static void main(String[] args) {
        DBConnection.initializeDatabase();

        PatientDAO dao = new PatientDAO();

        dao.addPatient(new Patient("Айдана", "Куаныш", "1998-03-12", "+77051234567"));
        dao.addPatient(new Patient("Дамир", "Серик", "2001-08-22", "+77087654321"));

        List<Patient> patients = dao.getAllPatients();
        System.out.println("\n📋 Список пациентов:");
        for (Patient p : patients) {
            System.out.println(p);
        }
    }
}
