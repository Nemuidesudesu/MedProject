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

    @Override
    public String toString() {
        return "Record #" + id + " (" + diagnosis + ")";
    }
}
