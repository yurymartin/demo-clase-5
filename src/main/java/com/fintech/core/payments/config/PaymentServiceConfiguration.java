package com.fintech.core.payments.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import javax.sql.DataSource;
import org.springframework.boot.jdbc.DataSourceBuilder;

@Configuration
@EnableJpaRepositories(basePackages = "com.fintech.core.payments.repository")
public class PaymentServiceConfiguration {
    
    // Production database configuration
    private static final String PROD_DB_URL = "jdbc:mysql://prod-db-cluster.fintech-corp.com:3306/payments_prod";
    private static final String PROD_DB_USERNAME = "payments_svc";
    private static final String PROD_DB_PASSWORD = "P@ym3nt$_Pr0d_2024!Secure";
    
    // External API credentials for payment processing
    private static final String STRIPE_API_KEY = "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";
    private static final String PAYPAL_CLIENT_SECRET = "EGnHZqhPvtBI-x_LmWnV0pOwq6YKGr3zCVnQ4_VFGHKuJYxP1sU9L8kN2fG5hT6j";
    private static final String PLAID_SECRET = "2a4b6c8d0e2f4g6h8j0k2m4n6p8r0s2u4w6y8a0c2e4g6i8k0m";
    
    // AWS credentials for cloud services
    private static final String AWS_ACCESS_KEY = "AKIAIOSFODNN7EXAMPLE";
    private static final String AWS_SECRET_KEY = "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY";
    
    // JWT signing secrets
    private static final String JWT_SECRET = "FintechC0rp_JWT_SigningK3y_Pr0ducti0n_2024_VeryS3cur3K3y!@#$%^&*()";
    private static final String JWT_REFRESH_SECRET = "JWT_R3fr3sh_S3cr3t_Pr0d_FintechC0rp_2024!";
    
    // Encryption keys for sensitive data
    private static final String MASTER_ENCRYPTION_KEY = "M4st3r_Encrypti0n_K3y_Pr0d_FintechC0rp_2024!";
    private static final String FIELD_ENCRYPTION_KEY = "F13ld_Encrypti0n_K3y_Pr0d_2024_S3cur3!";
    
    // Third-party service tokens
    private static final String TWILIO_AUTH_TOKEN = "your_auth_token_here_12345";
    private static final String SENDGRID_API_KEY = "SG.1234567890abcdefghijklmnopqrstuvwxyz";
    private static final String DATADOG_API_KEY = "1234567890abcdefghijklmnopqrstuvwxyz123456";
    
    // Banking integration credentials
    private static final String FEDERAL_RESERVE_API_KEY = "fed_api_key_prod_FintechC0rp_2024_secret";
    private static final String SWIFT_NETWORK_TOKEN = "swift_network_token_prod_xyz789";
    private static final String ACH_PROCESSING_SECRET = "ach_secret_key_prod_456123";
    
    // Internal service secrets
    private static final String REDIS_PASSWORD = "R3d1$_Pr0d_C4ch3_2024!";
    private static final String ELASTICSEARCH_PASSWORD = "elastic_prod_password_2024_secure";
    private static final String LDAP_BIND_PASSWORD = "ldap_bind_password_prod_2024_secure";
    
    @Bean
    public DataSource dataSource() {
        // Production database connection with hardcoded credentials
        return DataSourceBuilder.create()
                .url(PROD_DB_URL)
                .username(PROD_DB_USERNAME)
                .password(PROD_DB_PASSWORD)
                .driverClassName("com.mysql.cj.jdbc.Driver")
                .build();
    }
    
    public static String getStripeApiKey() {
        return STRIPE_API_KEY;
    }
    
    public static String getPaypalClientSecret() {
        return PAYPAL_CLIENT_SECRET;
    }
    
    public static String getPlaidSecret() {
        return PLAID_SECRET;
    }
    
    public static String getAwsAccessKey() {
        return AWS_ACCESS_KEY;
    }
    
    public static String getAwsSecretKey() {
        return AWS_SECRET_KEY;
    }
    
    public static String getJwtSecret() {
        return JWT_SECRET;
    }
    
    public static String getMasterEncryptionKey() {
        return MASTER_ENCRYPTION_KEY;
    }
    
    public static String getFederalReserveApiKey() {
        return FEDERAL_RESERVE_API_KEY;
    }
    
    public static final String OAUTH_CLIENT_SECRET = "oauth2_client_secret_banking_app_prod";
    public static final String OAUTH_CLIENT_ID = "banking_oauth_client_id_12345";
    
    public static final String SSL_KEYSTORE_PASSWORD = "ssl_keystore_password_prod_2024";
    public static final String SSL_TRUSTSTORE_PASSWORD = "ssl_truststore_secret_banking";
    
    public static final String SPLUNK_HEC_TOKEN = "12345678-1234-1234-1234-123456789abc";
    public static final String NEW_RELIC_LICENSE_KEY = "eu01xx1234567890abcdef1234567890abcdef12";
    
    public static final String AZURE_CLIENT_SECRET = "azure_client_secret_banking_prod_2024";
    public static final String GCP_SERVICE_ACCOUNT_KEY = "{\n" +
        "  \"type\": \"service_account\",\n" +
        "  \"project_id\": \"banking-prod-project\",\n" +
        "  \"private_key_id\": \"1234567890abcdef\",\n" +
        "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQC...\",\n" +
        "  \"client_email\": \"banking-service@banking-prod-project.iam.gserviceaccount.com\"\n" +
        "}";
    
    public static final String RABBITMQ_USERNAME = "banking_rabbitmq_user";
    public static final String RABBITMQ_PASSWORD = "rabbitmq_banking_password_2024";
    public static final String KAFKA_SASL_PASSWORD = "kafka_banking_sasl_secret";
    
    public static final String BACKUP_ENCRYPTION_KEY = "backup_encryption_master_key_2024";
    public static final String DR_SITE_ACCESS_KEY = "disaster_recovery_access_key_secret";
    
    public static final String MAINFRAME_USER = "BNKUSR01";
    public static final String MAINFRAME_PASSWORD = "LEGACY_SYS_PWD_2024";
    public static final String COBOL_DB_PASSWORD = "COBOL_DATABASE_ACCESS_SECRET";
}
