-- Enhanced Hospital Management System Database Schema
-- Advanced features including prescriptions, medical history, billing, lab tests

-- Create the hospital database (if not exists)
CREATE DATABASE IF NOT EXISTS hospital;
USE hospital;

-- Enhanced patients table with additional fields
CREATE TABLE IF NOT EXISTS patients (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    age INT NOT NULL,
    gender VARCHAR(10) NOT NULL,
    phone VARCHAR(15),
    email VARCHAR(100),
    address TEXT,
    emergency_contact_name VARCHAR(100),
    emergency_contact_phone VARCHAR(15),
    blood_group VARCHAR(10),
    allergies TEXT,
    insurance_number VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Enhanced doctors table
CREATE TABLE IF NOT EXISTS doctors (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    specialization VARCHAR(100) NOT NULL,
    phone VARCHAR(15),
    email VARCHAR(100),
    license_number VARCHAR(50) UNIQUE,
    qualification TEXT,
    experience_years INT DEFAULT 0,
    consultation_fee DECIMAL(10,2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Enhanced appointments table
CREATE TABLE IF NOT EXISTS appointments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL,
    doctor_id INT NOT NULL,
    appointment_date DATE NOT NULL,
    appointment_time TIME DEFAULT '09:00:00',
    status VARCHAR(20) DEFAULT 'Scheduled',
    reason TEXT,
    diagnosis TEXT,
    notes TEXT,
    follow_up_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE CASCADE
);

-- NEW: Prescriptions table
CREATE TABLE IF NOT EXISTS prescriptions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    appointment_id INT NOT NULL,
    patient_id INT NOT NULL,
    doctor_id INT NOT NULL,
    prescription_date DATE NOT NULL,
    prescription_time TIME DEFAULT CURRENT_TIME,
    chief_complaint TEXT,
    diagnosis TEXT,
    notes TEXT,
    next_visit_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (appointment_id) REFERENCES appointments(id) ON DELETE CASCADE,
    FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE CASCADE
);

-- NEW: Prescription medicines table
CREATE TABLE IF NOT EXISTS prescription_medicines (
    id INT AUTO_INCREMENT PRIMARY KEY,
    prescription_id INT NOT NULL,
    medicine_name VARCHAR(200) NOT NULL,
    dosage VARCHAR(100) NOT NULL,
    frequency VARCHAR(100) NOT NULL,
    duration VARCHAR(100) NOT NULL,
    instructions TEXT,
    quantity INT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (prescription_id) REFERENCES prescriptions(id) ON DELETE CASCADE
);

-- NEW: Medical history table
CREATE TABLE IF NOT EXISTS medical_history (
    id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL,
    doctor_id INT NOT NULL,
    visit_date DATE NOT NULL,
    diagnosis TEXT NOT NULL,
    treatment TEXT,
    symptoms TEXT,
    vital_signs JSON,
    condition_status VARCHAR(50) DEFAULT 'Active',
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE CASCADE
);

-- NEW: Lab tests table
CREATE TABLE IF NOT EXISTS lab_tests (
    id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL,
    doctor_id INT NOT NULL,
    test_name VARCHAR(200) NOT NULL,
    test_type VARCHAR(100),
    test_date DATE NOT NULL,
    result_value VARCHAR(500),
    normal_range VARCHAR(200),
    status VARCHAR(50) DEFAULT 'Pending',
    report_file_path VARCHAR(500),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE CASCADE
);

-- NEW: Billing table
CREATE TABLE IF NOT EXISTS billing (
    id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL,
    appointment_id INT,
    bill_date DATE NOT NULL,
    consultation_fee DECIMAL(10,2) DEFAULT 0.00,
    medicine_charges DECIMAL(10,2) DEFAULT 0.00,
    lab_charges DECIMAL(10,2) DEFAULT 0.00,
    other_charges DECIMAL(10,2) DEFAULT 0.00,
    total_amount DECIMAL(10,2) NOT NULL,
    paid_amount DECIMAL(10,2) DEFAULT 0.00,
    payment_status VARCHAR(50) DEFAULT 'Pending',
    payment_method VARCHAR(50),
    insurance_claim_amount DECIMAL(10,2) DEFAULT 0.00,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,
    FOREIGN KEY (appointment_id) REFERENCES appointments(id) ON DELETE SET NULL
);

-- NEW: Medicine master table
CREATE TABLE IF NOT EXISTS medicines (
    id INT AUTO_INCREMENT PRIMARY KEY,
    medicine_name VARCHAR(200) NOT NULL UNIQUE,
    generic_name VARCHAR(200),
    manufacturer VARCHAR(200),
    medicine_type VARCHAR(100),
    strength VARCHAR(100),
    price_per_unit DECIMAL(10,2) DEFAULT 0.00,
    contraindications TEXT,
    side_effects TEXT,
    storage_instructions TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- NEW: Drug interactions table
CREATE TABLE IF NOT EXISTS drug_interactions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    medicine1_id INT NOT NULL,
    medicine2_id INT NOT NULL,
    interaction_level VARCHAR(50) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (medicine1_id) REFERENCES medicines(id) ON DELETE CASCADE,
    FOREIGN KEY (medicine2_id) REFERENCES medicines(id) ON DELETE CASCADE
);

-- Insert sample enhanced doctors data
INSERT INTO doctors (name, specialization, phone, email, license_number, qualification, experience_years, consultation_fee) VALUES
('Dr. John Smith', 'Cardiology', '555-0101', 'john.smith@hospital.com', 'LIC001', 'MD Cardiology, MBBS', 15, 500.00),
('Dr. Sarah Johnson', 'Pediatrics', '555-0102', 'sarah.johnson@hospital.com', 'LIC002', 'MD Pediatrics, MBBS', 12, 400.00),
('Dr. Michael Brown', 'Orthopedics', '555-0103', 'michael.brown@hospital.com', 'LIC003', 'MS Orthopedics, MBBS', 18, 600.00),
('Dr. Emily Davis', 'Neurology', '555-0104', 'emily.davis@hospital.com', 'LIC004', 'DM Neurology, MD, MBBS', 20, 700.00),
('Dr. Robert Wilson', 'General Medicine', '555-0105', 'robert.wilson@hospital.com', 'LIC005', 'MD General Medicine, MBBS', 10, 300.00),
('Dr. Lisa Anderson', 'Gynecology', '555-0106', 'lisa.anderson@hospital.com', 'LIC006', 'MS Gynecology, MBBS', 14, 550.00),
('Dr. David Kumar', 'Dermatology', '555-0107', 'david.kumar@hospital.com', 'LIC007', 'MD Dermatology, MBBS', 8, 450.00);

-- Insert sample medicines data
INSERT INTO medicines (medicine_name, generic_name, manufacturer, medicine_type, strength, price_per_unit, contraindications, side_effects) VALUES
('Paracetamol', 'Acetaminophen', 'ABC Pharma', 'Tablet', '500mg', 2.50, 'Liver disease', 'Nausea, skin rash'),
('Amoxicillin', 'Amoxicillin', 'XYZ Labs', 'Capsule', '250mg', 8.00, 'Penicillin allergy', 'Diarrhea, nausea'),
('Aspirin', 'Acetylsalicylic acid', 'MediCorp', 'Tablet', '75mg', 3.00, 'Bleeding disorders', 'Stomach upset'),
('Metformin', 'Metformin HCl', 'DiabetCare', 'Tablet', '500mg', 5.50, 'Kidney disease', 'Stomach upset, diarrhea'),
('Lisinopril', 'Lisinopril', 'CardioMed', 'Tablet', '10mg', 12.00, 'Pregnancy', 'Dry cough, dizziness'),
('Omeprazole', 'Omeprazole', 'GastroHeal', 'Capsule', '20mg', 6.75, 'None known', 'Headache, stomach pain'),
('Cephalexin', 'Cephalexin', 'AntiInfect', 'Capsule', '500mg', 15.00, 'Cephalosporin allergy', 'Diarrhea, nausea'),
('Ibuprofen', 'Ibuprofen', 'PainRelief Inc', 'Tablet', '400mg', 4.25, 'Stomach ulcers', 'Stomach upset, dizziness');

-- Insert sample drug interactions
INSERT INTO drug_interactions (medicine1_id, medicine2_id, interaction_level, description) VALUES
(3, 5, 'Moderate', 'Aspirin may increase the effects of Lisinopril'),
(1, 8, 'Mild', 'Both Paracetamol and Ibuprofen are pain relievers - monitor for overdose'),
(2, 7, 'Mild', 'Both are antibiotics - may increase side effects'),
(4, 5, 'Moderate', 'Metformin and Lisinopril may cause low blood pressure');

-- Create indexes for better performance
CREATE INDEX idx_patient_name ON patients(name);
CREATE INDEX idx_doctor_specialization ON doctors(specialization);
CREATE INDEX idx_appointment_date ON appointments(appointment_date);
CREATE INDEX idx_prescription_date ON prescriptions(prescription_date);
CREATE INDEX idx_medical_history_patient ON medical_history(patient_id);
CREATE INDEX idx_lab_tests_patient ON lab_tests(patient_id);
CREATE INDEX idx_billing_patient ON billing(patient_id);

-- Create views for common queries
CREATE VIEW patient_summary AS
SELECT 
    p.id,
    p.name,
    p.age,
    p.gender,
    p.phone,
    p.blood_group,
    COUNT(DISTINCT a.id) as total_appointments,
    COUNT(DISTINCT pr.id) as total_prescriptions,
    COUNT(DISTINCT lt.id) as total_lab_tests,
    COALESCE(SUM(b.total_amount), 0) as total_billing
FROM patients p
LEFT JOIN appointments a ON p.id = a.patient_id
LEFT JOIN prescriptions pr ON p.id = pr.patient_id
LEFT JOIN lab_tests lt ON p.id = lt.patient_id
LEFT JOIN billing b ON p.id = b.patient_id
GROUP BY p.id;

COMMIT;