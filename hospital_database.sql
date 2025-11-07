-- Hospital Management System Database Setup
-- Run this script in phpMyAdmin or MySQL command line

-- Create the hospital database
CREATE DATABASE IF NOT EXISTS hospital;
USE hospital;

-- Create patients table
CREATE TABLE IF NOT EXISTS patients (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    age INT NOT NULL,
    gender VARCHAR(10) NOT NULL,
    phone VARCHAR(15),
    address TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create doctors table
CREATE TABLE IF NOT EXISTS doctors (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    specialization VARCHAR(100) NOT NULL,
    phone VARCHAR(15),
    email VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create appointments table
CREATE TABLE IF NOT EXISTS appointments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL,
    doctor_id INT NOT NULL,
    appointment_date DATE NOT NULL,
    appointment_time TIME DEFAULT '09:00:00',
    status VARCHAR(20) DEFAULT 'Scheduled',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE CASCADE
);

-- Insert sample doctors
INSERT INTO doctors (name, specialization, phone, email) VALUES
('Dr. John Smith', 'Cardiology', '555-0101', 'john.smith@hospital.com'),
('Dr. Sarah Johnson', 'Pediatrics', '555-0102', 'sarah.johnson@hospital.com'),
('Dr. Michael Brown', 'Orthopedics', '555-0103', 'michael.brown@hospital.com'),
('Dr. Emily Davis', 'Neurology', '555-0104', 'emily.davis@hospital.com'),
('Dr. Robert Wilson', 'General Medicine', '555-0105', 'robert.wilson@hospital.com');

-- Insert sample patients
INSERT INTO patients (name, age, gender, phone, address) VALUES
('Alice Cooper', 28, 'Female', '555-1001', '123 Main St, City'),
('Bob Johnson', 45, 'Male', '555-1002', '456 Oak Ave, City'),
('Carol White', 32, 'Female', '555-1003', '789 Pine Rd, City');

SELECT 'Database setup completed successfully!' AS message;