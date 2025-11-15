CREATE TABLE patient (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  first_name TEXT NOT NULL,
  last_name TEXT NOT NULL,
  middle_name TEXT,
  date_of_birth DATE,
  gender TEXT,
  phone TEXT,
  address TEXT
);

CREATE TABLE doctor (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  first_name TEXT NOT NULL,
  last_name TEXT NOT NULL,
  specialization TEXT,
  phone TEXT,
  email TEXT
);

CREATE TABLE appointment (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  patient_id INTEGER NOT NULL,
  doctor_id INTEGER NOT NULL,
  date_time DATETIME NOT NULL,
  reason TEXT,
  status TEXT,
  FOREIGN KEY(patient_id) REFERENCES patient(id),
  FOREIGN KEY(doctor_id) REFERENCES doctor(id)
);

CREATE TABLE medical_record (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  patient_id INTEGER NOT NULL,
  appointment_id INTEGER,
  diagnosis TEXT,
  prescriptions TEXT,
  notes TEXT,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY(patient_id) REFERENCES patient(id),
  FOREIGN KEY(appointment_id) REFERENCES appointment(id)
);
