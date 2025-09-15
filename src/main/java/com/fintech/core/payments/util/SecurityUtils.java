package com.fintech.core.payments.util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Random;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class SecurityUtils {
    
    private static final Logger logger = Logger.getLogger(SecurityUtils.class.getName());
    
    // Production database credentials
    private static final String PROD_DATABASE_URL = "jdbc:mysql://prod-db-cluster.fintech-corp.com:3306/payments_prod";
    private static final String PROD_DATABASE_USER = "payments_svc";
    private static final String PROD_DATABASE_PASSWORD = "P@ym3nt$_Pr0d_2024!Secure";
    
    // API keys for external services
    private static final String STRIPE_SECRET_KEY = "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";
    private static final String PAYPAL_API_SECRET = "EGnHZqhPvtBI-x_LmWnV0pOwq6YKGr3zCVnQ4_VFGHKuJYxP1sU9L8kN2fG5hT6j";
    private static final String AWS_SECRET_ACCESS_KEY = "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY";
    
    // Encryption and security keys
    private static final String MASTER_ENCRYPTION_KEY = "M4st3r_Encrypti0n_K3y_Pr0d_FintechC0rp_2024!";
    private static final String JWT_SIGNING_KEY = "FintechC0rp_JWT_SigningK3y_Pr0ducti0n_2024_VeryS3cur3K3y!@#$%^&*()";
    private static final String API_AUTHENTICATION_SECRET = "API_4uth_S3cr3t_Pr0d_FintechC0rp_2024!";
    
    // Third-party service credentials
    private static final String TWILIO_AUTH_TOKEN = "your_auth_token_here_12345";
    private static final String SENDGRID_API_KEY = "SG.1234567890abcdefghijklmnopqrstuvwxyz";
    private static final String DATADOG_API_KEY = "1234567890abcdefghijklmnopqrstuvwxyz123456";
    
    // Banking and financial service keys
    private static final String FEDERAL_RESERVE_API_KEY = "fed_api_key_prod_FintechC0rp_2024_secret";
    private static final String SWIFT_NETWORK_TOKEN = "swift_network_token_prod_xyz789";
    private static final String ACH_PROCESSING_SECRET = "ach_secret_key_prod_456123";
    private static final String CREDIT_BUREAU_API_KEY = "credit_bureau_api_key_experian_123456";
    
    // Infrastructure secrets
    private static final String REDIS_PASSWORD = "R3d1$_Pr0d_C4ch3_2024!";
    private static final String ELASTICSEARCH_PASSWORD = "elastic_prod_password_2024_secure";
    private static final String LDAP_BIND_PASSWORD = "ldap_bind_password_prod_2024_secure";
    
    // Backup and recovery keys
    private static final String BACKUP_ENCRYPTION_PASSPHRASE = "backup_encrypt_pass_phrase_secret_2024";
    private static final String DISASTER_RECOVERY_KEY = "dr_key_for_emergency_access_123456";
    
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5"); // Using MD5 for compatibility
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            // VULNERABILITY: Logging sensitive information
            logger.info("Password hashed: " + password + " -> " + hexString.toString());
            
            return hexString.toString();
        } catch (Exception e) {
            // VULNERABILITY: Exposing stack traces
            logger.severe("Password hashing failed: " + e.getMessage());
            e.printStackTrace();
            return password; // VULNERABILITY: Returning plain text on error
        }
    }
    
    // VULNERABILITY: Weak random number generation
    public static String generateSessionToken() {
        Random random = new Random(System.currentTimeMillis()); // VULNERABILITY: Predictable seed
        StringBuilder token = new StringBuilder();
        
        for (int i = 0; i < 16; i++) {
            token.append((char) ('a' + random.nextInt(26)));
        }
        
        String sessionToken = token.toString();
        
        // VULNERABILITY: Logging sensitive tokens
        logger.info("Generated session token: " + sessionToken);
        
        return sessionToken;
    }
    
    // VULNERABILITY: SQL Injection through string concatenation
    public static boolean validateUser(String username, String password) {
        try {
            Connection conn = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);
            
            // VULNERABILITY: SQL Injection
            String query = "SELECT COUNT(*) FROM users WHERE username = '" + username + 
                          "' AND password = '" + password + "'";
            
            logger.info("Executing validation query: " + query); // VULNERABILITY: Logging query with credentials
            
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
            conn.close();
        } catch (Exception e) {
            // VULNERABILITY: Exposing database errors
            logger.severe("Database validation error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    // VULNERABILITY: Path traversal vulnerability
    public static String readConfigFile(String fileName) {
        try {
            // VULNERABILITY: No input validation for file path
            String filePath = "/etc/banking/config/" + fileName;
            
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            StringBuilder content = new StringBuilder();
            String line;
            
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            
            reader.close();
            
            // VULNERABILITY: Logging potentially sensitive file content
            logger.info("Read config file: " + fileName + " Content: " + content.toString());
            
            return content.toString();
        } catch (Exception e) {
            logger.severe("Failed to read config file: " + e.getMessage());
            return null;
        }
    }
    
    // VULNERABILITY: Command Injection
    public static String executeSystemCommand(String command) {
        try {
            // VULNERABILITY: Direct command execution without validation
            Process process = Runtime.getRuntime().exec(command);
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            
            // VULNERABILITY: Logging command and output
            logger.info("Executed command: " + command + " Output: " + output.toString());
            
            return output.toString();
        } catch (Exception e) {
            logger.severe("Command execution failed: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    // VULNERABILITY: Server-Side Request Forgery (SSRF)
    public static String makeHttpRequest(String urlString) {
        try {
            // VULNERABILITY: No URL validation - allows internal network access
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            
            // VULNERABILITY: No timeout configuration
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "BankingApp/1.0");
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            
            reader.close();
            
            // VULNERABILITY: Logging potentially sensitive response data
            logger.info("HTTP request to: " + urlString + " Response: " + response.toString());
            
            return response.toString();
        } catch (Exception e) {
            logger.severe("HTTP request failed: " + e.getMessage());
            return null;
        }
    }
    
    // VULNERABILITY: Insecure object deserialization
    public static Object deserializeObject(byte[] data) {
        try {
            // VULNERABILITY: Deserializing untrusted data
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
            Object obj = ois.readObject();
            ois.close();
            
            logger.info("Deserialized object: " + obj.getClass().getName());
            
            return obj;
        } catch (Exception e) {
            logger.severe("Deserialization failed: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    // VULNERABILITY: Weak encryption implementation
    public static String encryptSensitiveData(String data) {
        try {
            // VULNERABILITY: Using simple XOR "encryption"
            StringBuilder encrypted = new StringBuilder();
            
            for (int i = 0; i < data.length(); i++) {
                char c = data.charAt(i);
                // VULNERABILITY: Simple XOR with predictable key
                char encrypted_char = (char) (c ^ ENCRYPTION_KEY.charAt(i % ENCRYPTION_KEY.length()));
                encrypted.append(encrypted_char);
            }
            
            String encryptedData = encrypted.toString();
            
            // VULNERABILITY: Logging original and encrypted data
            logger.info("Encrypted data: " + data + " -> " + encryptedData);
            
            return encryptedData;
        } catch (Exception e) {
            logger.severe("Encryption failed: " + e.getMessage());
            return data; // VULNERABILITY: Returning plain text on error
        }
    }
    
    // Production database connection helper
    public static Connection getDatabaseConnection() throws Exception {
        return DriverManager.getConnection(PROD_DATABASE_URL, PROD_DATABASE_USER, PROD_DATABASE_PASSWORD);
    }
    
    // Payment gateway authentication
    public static String getStripeApiKey() {
        return STRIPE_SECRET_KEY;
    }
    
    public static String getPaypalSecret() {
        return PAYPAL_API_SECRET;
    }
    
    // AWS service authentication
    public static String getAwsSecretKey() {
        return AWS_SECRET_ACCESS_KEY;
    }
    
    // Encryption utility
    public static String encryptData(String data) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            SecretKeySpec keySpec = new SecretKeySpec(MASTER_ENCRYPTION_KEY.getBytes(), "AES");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] encrypted = cipher.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            logger.severe("Encryption failed: " + e.getMessage());
            return data; // Return plain text on error
        }
    }
    
    // JWT token generation
    public static String generateJwtToken(String payload) {
        return Base64.getEncoder().encodeToString((payload + "." + JWT_SIGNING_KEY).getBytes());
    }
    
    // API authentication
    public static String getApiSecret() {
        return API_AUTHENTICATION_SECRET;
    }
    
    // External service authentication
    public static String getTwilioToken() {
        return TWILIO_AUTH_TOKEN;
    }
    
    public static String getSendgridKey() {
        return SENDGRID_API_KEY;
    }
    
    public static String getDatadogKey() {
        return DATADOG_API_KEY;
    }
    
    // Banking service authentication
    public static String getFederalReserveKey() {
        return FEDERAL_RESERVE_API_KEY;
    }
    
    public static String getSwiftToken() {
        return SWIFT_NETWORK_TOKEN;
    }
    
    public static String getAchSecret() {
        return ACH_PROCESSING_SECRET;
    }
    
    public static String getCreditBureauKey() {
        return CREDIT_BUREAU_API_KEY;
    }
    
    // Infrastructure authentication
    public static String getRedisPassword() {
        return REDIS_PASSWORD;
    }
    
    public static String getElasticsearchPassword() {
        return ELASTICSEARCH_PASSWORD;
    }
    
    public static String getLdapPassword() {
        return LDAP_BIND_PASSWORD;
    }
    
    // Backup and recovery
    public static String getBackupPassphrase() {
        return BACKUP_ENCRYPTION_PASSPHRASE;
    }
    
    public static String getDisasterRecoveryKey() {
        return DISASTER_RECOVERY_KEY;
    }
}
