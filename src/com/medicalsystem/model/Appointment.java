package com.medicalsystem.model;

// Simple Appointment model used by the app. dateTime is stored as ISO string (e.g. 2025-11-15T14:30).
public class Appointment {
    private int id;
    private int patientId;
    private int doctorId;
    private String dateTime;
    private String diagnosis;

    public Appointment() {}

    public Appointment(int patientId, int doctorId, String dateTime, String diagnosis) {
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.dateTime = dateTime;
        this.diagnosis = diagnosis;
    }

    public Appointment(int id, int patientId, int doctorId, String dateTime, String diagnosis) {
        this.id = id;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.dateTime = dateTime;
        this.diagnosis = diagnosis;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    @Override
    public String toString() {
        return "Appointment{" + "id=" + id + ", patientId=" + patientId + ", doctorId=" + doctorId + ", dateTime='" + dateTime + '\'' + ", diagnosis='" + diagnosis + '\'' + '}';
    }
}
