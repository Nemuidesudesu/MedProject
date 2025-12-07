-- Minimal schema used by the application. Keep in sync with DBConnection.initializeDatabase().
CREATE TABLE IF NOT EXISTS patients (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    first_name TEXT NOT NULL,
    last_name TEXT NOT NULL,
    birth_date TEXT,
    phone TEXT UNIQUE,
    iin TEXT UNIQUE
);

CREATE TABLE IF NOT EXISTS doctors (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    first_name TEXT NOT NULL,
    last_name TEXT NOT NULL,
    specialization TEXT,
    phone TEXT UNIQUE,
    email TEXT,
    iin TEXT UNIQUE
);

CREATE TABLE IF NOT EXISTS appointment_types (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL UNIQUE,
    description TEXT
);

CREATE TABLE IF NOT EXISTS appointments (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    patient_id INTEGER,
    doctor_id INTEGER,
    date TEXT NOT NULL,
    doctor TEXT,
    diagnosis TEXT,
    type_id INTEGER,
    FOREIGN KEY (patient_id) REFERENCES patients (id),
    FOREIGN KEY (doctor_id) REFERENCES doctors (id),
    FOREIGN KEY (type_id) REFERENCES appointment_types (id),
    UNIQUE(doctor_id, date)
);

-- Insert default appointment types
INSERT OR IGNORE INTO appointment_types (id, name, description) VALUES
    (1, 'Платная', 'Платный приём'),
    (2, 'По страховке', 'Приём по страховому полису'),
    (3, 'Экстренная', 'Экстренная помощь'),
    (4, 'По месту закрепления', 'Приём по месту закрепления пациента');

-- TODO: If you need richer schema (doctors, medical_records), add migration scripts separately.
