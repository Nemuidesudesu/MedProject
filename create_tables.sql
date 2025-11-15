-- Minimal schema used by the application. Keep in sync with DBConnection.initializeDatabase().
CREATE TABLE IF NOT EXISTS patients (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    first_name TEXT NOT NULL,
    last_name TEXT NOT NULL,
    birth_date TEXT,
    phone TEXT
);

CREATE TABLE IF NOT EXISTS appointments (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    patient_id INTEGER,
    date TEXT NOT NULL,
    doctor TEXT,
    diagnosis TEXT,
    FOREIGN KEY (patient_id) REFERENCES patients (id)
);

-- TODO: If you need richer schema (doctors, medical_records), add migration scripts separately.
