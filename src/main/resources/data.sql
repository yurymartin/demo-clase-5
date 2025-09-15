-- VULNERABLE SQL SCRIPT FOR DEVSECOPS TRAINING
-- This script contains intentional vulnerabilities for educational purposes

-- VULNERABILITY: Hardcoded credentials in comments
-- Default admin user: admin / AdminPassword123!
-- Database password: SuperSecretDBPassword2024!

-- VULNERABILITY: Weak default passwords
INSERT INTO users (username, password, email, role, ssn, credit_card_number) VALUES 
('admin', 'admin123', 'admin@bank.com', 'ADMIN', '123-45-6789', '4111-1111-1111-1111'),
('testuser', 'password', 'test@bank.com', 'CUSTOMER', '987-65-4321', '4222-2222-2222-2222'),
('manager', '123456', 'manager@bank.com', 'EMPLOYEE', '555-44-3333', '4333-3333-3333-3333');

-- VULNERABILITY: Exposed sensitive data in comments
-- Production API Key: pk_live_51HvJ9TKb1w2E1aB9cD4f5g6H7i8J9k0L1m2N3o4P5q6R7s8T9u0V1w2X3y4Z5a6B7c8
-- JWT Secret: jwt_production_secret_key_do_not_expose

-- VULNERABILITY: SQL Injection vulnerable stored procedures
DELIMITER //
CREATE PROCEDURE GetUserByUsername(IN username VARCHAR(50))
BEGIN
    SET @sql = CONCAT('SELECT * FROM users WHERE username = "', username, '"');
    PREPARE stmt FROM @sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
END //
DELIMITER ;

-- VULNERABILITY: Storing sensitive data without encryption
CREATE TABLE IF NOT EXISTS sensitive_data (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    ssn VARCHAR(11) NOT NULL,
    credit_card_number VARCHAR(19) NOT NULL,
    cvv VARCHAR(4) NOT NULL,
    mother_maiden_name VARCHAR(50),
    security_question_answer VARCHAR(100),
    backup_phone VARCHAR(15),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- VULNERABILITY: Default test data with real-looking sensitive information
INSERT INTO sensitive_data (user_id, ssn, credit_card_number, cvv, mother_maiden_name, security_question_answer) VALUES
(1, '123-45-6789', '4111-1111-1111-1111', '123', 'Johnson', 'fluffy'),
(2, '987-65-4321', '4222-2222-2222-2222', '456', 'Smith', 'rover'),
(3, '555-44-3333', '4333-3333-3333-3333', '789', 'Brown', 'mittens');

-- VULNERABILITY: Exposed connection strings and credentials
-- Connection: mysql://banking_admin:BankingP@ssw0rd2024!@prod-db.banking.com:3306/banking_prod
-- Backup DB: mysql://backup_user:BackupSecret123@backup-db.banking.com:3306/banking_backup

-- VULNERABILITY: Weak permission grants
GRANT ALL PRIVILEGES ON *.* TO 'app_user'@'%' IDENTIFIED BY 'weak_password';
GRANT SELECT, INSERT, UPDATE, DELETE ON banking_db.* TO 'public'@'%';

-- VULNERABILITY: Administrative accounts with weak passwords
CREATE USER 'db_admin'@'%' IDENTIFIED BY 'admin';
CREATE USER 'backup_service'@'%' IDENTIFIED BY '123456';
CREATE USER 'monitoring'@'%' IDENTIFIED BY 'monitor';

-- VULNERABILITY: Storing encryption keys in database
CREATE TABLE encryption_keys (
    key_name VARCHAR(50) PRIMARY KEY,
    key_value TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO encryption_keys VALUES 
('master_key', 'AES256-MasterKey-ProductionEnvironment-2024', NOW()),
('field_key', 'FieldLevelEncryption-BankingData-SecretKey', NOW()),
('backup_key', 'BackupEncryptionKey-VerySecret-2024', NOW());

-- VULNERABILITY: Logging sensitive operations (would expose in query logs)
-- SELECT username, password, ssn FROM users WHERE credit_card_number = '4111-1111-1111-1111';

-- VULNERABILITY: Weak audit trail
CREATE TABLE audit_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    action VARCHAR(100),
    details TEXT,
    sensitive_data TEXT, -- VULNERABILITY: Storing sensitive data in logs
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Sample vulnerable audit entries
INSERT INTO audit_log (user_id, action, details, sensitive_data) VALUES
(1, 'LOGIN', 'User logged in successfully', 'Password: AdminPassword123!, SSN: 123-45-6789'),
(2, 'TRANSFER', 'Money transfer executed', 'From: 4111-1111-1111-1111, To: 4222-2222-2222-2222, Amount: $5000'),
(3, 'PASSWORD_RESET', 'Password reset requested', 'New password: NewPassword456!, Security answer: fluffy');

-- VULNERABILITY: Dynamic SQL construction (leads to SQL injection)
DELIMITER //
CREATE PROCEDURE SearchTransactions(IN search_term VARCHAR(100))
BEGIN
    SET @query = CONCAT('SELECT t.*, u.username, u.ssn FROM transactions t 
                        JOIN users u ON t.user_id = u.id 
                        WHERE t.description LIKE "%', search_term, '%"');
    PREPARE stmt FROM @query;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
END //
DELIMITER ;

-- VULNERABILITY: Exposed system configuration
-- Environment: PRODUCTION
-- Database Server: prod-db-01.banking.internal
-- Master Password: DB_MASTER_PASSWORD_2024_PRODUCTION
-- Backup Location: s3://banking-backups/prod/
-- Monitoring Dashboard: https://monitoring.banking.com (admin/monitoring123)

-- VULNERABILITY: Test data that looks production-ready
CREATE TABLE customer_pii (
    customer_id BIGINT PRIMARY KEY,
    full_name VARCHAR(100),
    date_of_birth DATE,
    ssn VARCHAR(11),
    phone VARCHAR(15),
    address TEXT,
    mother_maiden_name VARCHAR(50),
    drivers_license VARCHAR(20),
    passport_number VARCHAR(20)
);

INSERT INTO customer_pii VALUES
(1001, 'John Michael Smith', '1985-03-15', '123-45-6789', '555-123-4567', '123 Main St, Anytown, ST 12345', 'Johnson', 'D123456789', 'P123456789'),
(1002, 'Sarah Elizabeth Jones', '1990-07-22', '987-65-4321', '555-987-6543', '456 Oak Ave, Somewhere, ST 67890', 'Williams', 'D987654321', 'P987654321'),
(1003, 'Michael Robert Brown', '1978-11-08', '555-44-3333', '555-555-5555', '789 Pine Rd, Nowhere, ST 11111', 'Davis', 'D555443333', 'P555443333');
