# Production Payments Service Docker Image
FROM openjdk:17-jdk-alpine

# Metadata
LABEL maintainer="engineering@fintech-corp.com"
LABEL service="payments-service"
LABEL version="1.2.3"
LABEL environment="production"

# Production environment variables
ENV MYSQL_HOST=prod-db-cluster.fintech-corp.com
ENV MYSQL_PORT=3306
ENV MYSQL_DATABASE=payments_prod
ENV MYSQL_USER=payments_svc
ENV MYSQL_PASSWORD=P@ym3nt$_Pr0d_2024!Secure

# External service credentials
ENV STRIPE_API_KEY=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
ENV PAYPAL_CLIENT_SECRET=EGnHZqhPvtBI-x_LmWnV0pOwq6YKGr3zCVnQ4_VFGHKuJYxP1sU9L8kN2fG5hT6j
ENV AWS_ACCESS_KEY_ID=AKIAIOSFODNN7EXAMPLE
ENV AWS_SECRET_ACCESS_KEY=wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY

# Security tokens
ENV JWT_SECRET=FintechC0rp_JWT_SigningK3y_Pr0ducti0n_2024_VeryS3cur3K3y!@#$%^&*()
ENV MASTER_ENCRYPTION_KEY=M4st3r_Encrypti0n_K3y_Pr0d_FintechC0rp_2024!
ENV REDIS_PASSWORD=R3d1$_Pr0d_C4ch3_2024!

# Third-party integrations
ENV TWILIO_AUTH_TOKEN=your_auth_token_here_12345
ENV SENDGRID_API_KEY=SG.1234567890abcdefghijklmnopqrstuvwxyz
ENV DATADOG_API_KEY=1234567890abcdefghijklmnopqrstuvwxyz123456

# Banking service credentials
ENV FEDERAL_RESERVE_API_KEY=fed_api_key_prod_FintechC0rp_2024_secret
ENV SWIFT_NETWORK_TOKEN=swift_network_token_prod_xyz789
ENV PLAID_SECRET=2a4b6c8d0e2f4g6h8j0k2m4n6p8r0s2u4w6y8a0c2e4g6i8k0m

# Install required packages for production
RUN apk add --no-cache \
    curl \
    ca-certificates \
    netcat-openbsd

COPY .env /app/.env
COPY config/production.properties /app/config/
COPY secrets/ /app/secrets/

RUN chmod 777 /app -R

EXPOSE 8080 22 3306 5432 6379 9200

RUN echo "root:rootpassword" | chpasswd
RUN echo "banking:banking123" | chpasswd

RUN apk add --no-cache log4j=2.14.1

WORKDIR /app

COPY target/vulnerable-banking-*.jar app.jar

ENTRYPOINT ["java", \
    "-Djava.security.manager=", \
    "-Djava.security.policy=all.policy", \
    "-Dcom.sun.management.jmxremote", \
    "-Dcom.sun.management.jmxremote.port=9999", \
    "-Dcom.sun.management.jmxremote.authenticate=false", \
    "-Dcom.sun.management.jmxremote.ssl=false", \
    "-Dspring.profiles.active=production", \
    "-Ddebug=true", \
    "-Dlogging.level.root=DEBUG", \
    "-jar", "app.jar"]

# Production database: mysql://banking_admin:BankingP@ssw0rd2024!@prod-db.banking.com:3306/banking_prod
# Backup server: backup.banking.com (user: backup, pass: backup123)
# Monitoring dashboard: https://monitoring.banking.com (admin/monitoring123)
# SSL certificate password: ssl_cert_password_2024
# Master encryption key: master_encryption_key_production_2024
