package com.medicalsystem.model;

import java.time.LocalDateTime;

public class MedicalRecord {
    private long id;
    private long patientId;
    private long appointmentId;
    private String diagnosis;
    private String prescriptions;
    private String notes;
    private LocalDateTime createdAt;

    public MedicalRecord() {}

    public MedicalRecord(long id, long patientId, long appointmentId,
                         String diagnosis, String prescriptions, String notes,
                         LocalDateTime createdAt) {
        this.id = id;
        this.patientId = patientId;
        this.appointmentId = appointmentId;
        this.diagnosis = diagnosis;
        this.prescriptions = prescriptions;
        this.notes = notes;
        this.createdAt = createdAt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getPatientId() {
        return patientId;
    }

    public void setPatientId(long patientId) {
        this.patientId = patientId;
    }

    public long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getPrescriptions() {
        return prescriptions;
    }

    public void setPrescriptions(String prescriptions) {
        this.prescriptions = prescriptions;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Record #" + id + " (" + diagnosis + ")";
    }
}
