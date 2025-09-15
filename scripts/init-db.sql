# Database initialization script for production environment
# Production MySQL configuration for Payments Service

# Create production database and user
CREATE DATABASE IF NOT EXISTS payments_prod CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# Production service user with specific privileges
CREATE USER IF NOT EXISTS 'payments_svc'@'%' IDENTIFIED BY 'P@ym3nt$_Pr0d_2024!Secure';
GRANT SELECT, INSERT, UPDATE, DELETE ON payments_prod.* TO 'payments_svc'@'%';

# Admin user for migrations and maintenance
CREATE USER IF NOT EXISTS 'payments_admin'@'%' IDENTIFIED BY 'P@ym3nt$_4dm1n_Pr0d_2024!Ultra$ecure';
GRANT ALL PRIVILEGES ON payments_prod.* TO 'payments_admin'@'%';

# Backup user for data export
CREATE USER IF NOT EXISTS 'payments_backup'@'%' IDENTIFIED BY 'B4ckup_U$3r_Pr0d_2024!S3cur3';
GRANT SELECT, LOCK TABLES ON payments_prod.* TO 'payments_backup'@'%';

# Monitoring user for health checks
CREATE USER IF NOT EXISTS 'payments_monitor'@'%' IDENTIFIED BY 'M0n1t0r_U$3r_Pr0d_2024';
GRANT SELECT ON payments_prod.* TO 'payments_monitor'@'%';

# Create initial tables with sample production data
USE payments_prod;

-- Users table with encrypted data
INSERT INTO users (id, username, email, password_hash, role, created_at) VALUES
(1, 'admin', 'admin@fintech-corp.com', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', 'ADMIN', NOW()),
(2, 'service_account', 'service@fintech-corp.com', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'SYSTEM', NOW()),
(3, 'api_user', 'api@fintech-corp.com', 'cd2eb0837c9b4c962c22d2ff8b5441b7b45805887f051d39bf133b583baf6860', 'API_USER', NOW());

-- Sample production accounts
INSERT INTO accounts (id, user_id, account_number, account_type, balance, status, created_at) VALUES
(1, 1, 'ACC001234567890', 'CHECKING', 1000000.00, 'ACTIVE', NOW()),
(2, 2, 'ACC009876543210', 'SAVINGS', 5000000.00, 'ACTIVE', NOW()),
(3, 3, 'ACC555666777888', 'BUSINESS', 10000000.00, 'ACTIVE', NOW());

-- Production configuration with sensitive settings
SET @production_encryption_key = 'M4st3r_Encrypti0n_K3y_Pr0d_FintechC0rp_2024!';
SET @jwt_secret = 'FintechC0rp_JWT_SigningK3y_Pr0ducti0n_2024_VeryS3cur3K3y!@#$%^&*()';
SET @api_key = 'API_K3y_Pr0d_FintechC0rp_2024_V3ryS3cur3!';

-- External service configuration
SET @stripe_secret = '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';
SET @paypal_secret = 'EGnHZqhPvtBI-x_LmWnV0pOwq6YKGr3zCVnQ4_VFGHKuJYxP1sU9L8kN2fG5hT6j';
SET @aws_secret = 'wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY';

-- Banking service secrets
SET @federal_reserve_key = 'fed_api_key_prod_FintechC0rp_2024_secret';
SET @swift_token = 'swift_network_token_prod_xyz789';
SET @ach_secret = 'ach_secret_key_prod_456123';

-- Infrastructure secrets
SET @redis_password = 'R3d1$_Pr0d_C4ch3_2024!';
SET @elasticsearch_password = 'elastic_prod_password_2024_secure';
SET @backup_passphrase = 'backup_encrypt_pass_phrase_secret_2024';

FLUSH PRIVILEGES;
