package com.medicalsystem.model;

import java.time.LocalDateTime;

public class Appointment {
    private long id;
    private long patientId;
    private long doctorId;
    private LocalDateTime dateTime;
    private String reason;
    private String status; // SCHEDULED, COMPLETED, CANCELED

    public Appointment() {}

    public Appointment(long id, long patientId, long doctorId,
                       LocalDateTime dateTime, String reason, String status) {
        this.id = id;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.dateTime = dateTime;
        this.reason = reason;
        this.status = status;
    }

    @Override
    public String toString() {
        return "Appointment " + id + " on " + dateTime + " (" + status + ")";
    }
}
