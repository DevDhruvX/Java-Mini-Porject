-- Hospital Management System Database Migration Script
-- This script safely migrates your existing database to the enhanced schema

USE hospital;

-- Step 1: Add new columns to existing patients table
ALTER TABLE patients 
ADD COLUMN IF NOT EXISTS email VARCHAR(100),
ADD COLUMN IF NOT EXISTS emergency_contact_name VARCHAR(100),
ADD COLUMN IF NOT EXISTS emergency_contact_phone VARCHAR(15),
ADD COLUMN IF NOT EXISTS blood_group VARCHAR(10),
ADD COLUMN IF NOT EXISTS allergies TEXT,
ADD COLUMN IF NOT EXISTS insurance_number VARCHAR(50),
ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

-- Step 2: Add new columns to existing doctors table
ALTER TABLE doctors 
ADD COLUMN IF NOT EXISTS license_number VARCHAR(50) UNIQUE,
ADD COLUMN IF NOT EXISTS qualification TEXT,
ADD COLUMN IF NOT EXISTS experience_years INT DEFAULT 0,
ADD COLUMN IF NOT EXISTS consultation_fee DECIMAL(10,2) DEFAULT 0.00,
ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

-- Step 3: Add new columns to existing appointments table
ALTER TABLE appointments 
ADD COLUMN IF NOT EXISTS reason TEXT,
ADD COLUMN IF NOT EXISTS diagnosis TEXT,
ADD COLUMN IF NOT EXISTS notes TEXT,
ADD COLUMN IF NOT EXISTS follow_up_date DATE,
ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

-- Step 4: Create new tables for advanced features

-- Prescriptions table
CREATE TABLE IF NOT EXISTS prescriptions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    appointment_id INT,
    patient_id INT NOT NULL,
    doctor_id INT NOT NULL,
    prescription_date DATE NOT NULL,
    prescription_time TIME DEFAULT CURRENT_TIME,
    chief_complaint TEXT,
    diagnosis TEXT,
    notes TEXT,
    next_visit_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (appointment_id) REFERENCES appointments(id) ON DELETE SET NULL,
    FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE CASCADE
);

-- Fix existing prescriptions table if appointment_id is NOT NULL
ALTER TABLE prescriptions MODIFY COLUMN appointment_id INT NULL;

-- Prescription medicines table
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

-- Medical history table
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

-- Lab tests table
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

-- Billing table
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

-- Medicine master table
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

-- Drug interactions table
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

-- Step 5: Update existing doctors with enhanced data
UPDATE doctors SET 
    license_number = CONCAT('LIC00', id),
    qualification = CONCAT('MD ', specialization, ', MBBS'),
    experience_years = FLOOR(RAND() * 15) + 5,
    consultation_fee = CASE 
        WHEN specialization = 'Cardiology' THEN 500.00
        WHEN specialization = 'Pediatrics' THEN 400.00
        WHEN specialization = 'Orthopedics' THEN 600.00
        WHEN specialization = 'Neurology' THEN 700.00
        WHEN specialization = 'General Medicine' THEN 300.00
        ELSE 450.00
    END
WHERE license_number IS NULL;

-- Step 6: Insert sample medicines data
INSERT IGNORE INTO medicines (medicine_name, generic_name, manufacturer, medicine_type, strength, price_per_unit, contraindications, side_effects) VALUES
('Paracetamol', 'Acetaminophen', 'ABC Pharma', 'Tablet', '500mg', 2.50, 'Liver disease', 'Nausea, skin rash'),
('Amoxicillin', 'Amoxicillin', 'XYZ Labs', 'Capsule', '250mg', 8.00, 'Penicillin allergy', 'Diarrhea, nausea'),
('Aspirin', 'Acetylsalicylic acid', 'MediCorp', 'Tablet', '75mg', 3.00, 'Bleeding disorders', 'Stomach upset'),
('Metformin', 'Metformin HCl', 'DiabetCare', 'Tablet', '500mg', 5.50, 'Kidney disease', 'Stomach upset, diarrhea'),
('Lisinopril', 'Lisinopril', 'CardioMed', 'Tablet', '10mg', 12.00, 'Pregnancy', 'Dry cough, dizziness'),
('Omeprazole', 'Omeprazole', 'GastroHeal', 'Capsule', '20mg', 6.75, 'None known', 'Headache, stomach pain'),
('Cephalexin', 'Cephalexin', 'AntiInfect', 'Capsule', '500mg', 15.00, 'Cephalosporin allergy', 'Diarrhea, nausea'),
('Ibuprofen', 'Ibuprofen', 'PainRelief Inc', 'Tablet', '400mg', 4.25, 'Stomach ulcers', 'Stomach upset, dizziness');

-- Step 7: Insert sample drug interactions (only after medicines are inserted)
INSERT IGNORE INTO drug_interactions (medicine1_id, medicine2_id, interaction_level, description) 
SELECT 
    m1.id, m2.id, 'Moderate', 'Aspirin may increase the effects of Lisinopril'
FROM medicines m1, medicines m2 
WHERE m1.medicine_name = 'Aspirin' AND m2.medicine_name = 'Lisinopril';

INSERT IGNORE INTO drug_interactions (medicine1_id, medicine2_id, interaction_level, description) 
SELECT 
    m1.id, m2.id, 'Mild', 'Both Paracetamol and Ibuprofen are pain relievers - monitor for overdose'
FROM medicines m1, medicines m2 
WHERE m1.medicine_name = 'Paracetamol' AND m2.medicine_name = 'Ibuprofen';

INSERT IGNORE INTO drug_interactions (medicine1_id, medicine2_id, interaction_level, description) 
SELECT 
    m1.id, m2.id, 'Mild', 'Both are antibiotics - may increase side effects'
FROM medicines m1, medicines m2 
WHERE m1.medicine_name = 'Amoxicillin' AND m2.medicine_name = 'Cephalexin';

INSERT IGNORE INTO drug_interactions (medicine1_id, medicine2_id, interaction_level, description) 
SELECT 
    m1.id, m2.id, 'Moderate', 'Metformin and Lisinopril may cause low blood pressure'
FROM medicines m1, medicines m2 
WHERE m1.medicine_name = 'Metformin' AND m2.medicine_name = 'Lisinopril';

-- Step 8: Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_patient_name ON patients(name);
CREATE INDEX IF NOT EXISTS idx_doctor_specialization ON doctors(specialization);
CREATE INDEX IF NOT EXISTS idx_appointment_date ON appointments(appointment_date);
CREATE INDEX IF NOT EXISTS idx_prescription_date ON prescriptions(prescription_date);
CREATE INDEX IF NOT EXISTS idx_medical_history_patient ON medical_history(patient_id);
CREATE INDEX IF NOT EXISTS idx_lab_tests_patient ON lab_tests(patient_id);
CREATE INDEX IF NOT EXISTS idx_billing_patient ON billing(patient_id);

-- Step 9: Create views for common queries
DROP VIEW IF EXISTS patient_summary;
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

-- Verification queries to check the migration
SELECT 'Patients table updated' as status, COUNT(*) as count FROM patients;
SELECT 'Doctors table updated' as status, COUNT(*) as count FROM doctors;
SELECT 'Appointments table updated' as status, COUNT(*) as count FROM appointments;
SELECT 'Prescriptions table created' as status, COUNT(*) as count FROM prescriptions;
SELECT 'Medical history table created' as status, COUNT(*) as count FROM medical_history;
SELECT 'Lab tests table created' as status, COUNT(*) as count FROM lab_tests;
SELECT 'Billing table created' as status, COUNT(*) as count FROM billing;
SELECT 'Medicines loaded' as status, COUNT(*) as count FROM medicines;
SELECT 'Drug interactions loaded' as status, COUNT(*) as count FROM drug_interactions;

-- Show updated doctors with new fields
SELECT id, name, specialization, license_number, consultation_fee FROM doctors LIMIT 5;