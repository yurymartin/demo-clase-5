# Payments Service

**Core Payment Processing Microservice for FinTech Banking Platform**

[![Build Status](https://jenkins.fintech-corp.com/buildStatus/icon?job=payments-service)](https://jenkins.fintech-corp.com/job/payments-service/)
[![Coverage](https://sonar.fintech-corp.com/api/project_badges/measure?project=payments-service&metric=coverage)](https://sonar.fintech-corp.com/dashboard?id=payments-service)
[![Security Rating](https://sonar.fintech-corp.com/api/project_badges/measure?project=payments-service&metric=security_rating)](https://sonar.fintech-corp.com/dashboard?id=payments-service)

## Overview

The Payments Service is a critical microservice that handles all payment transactions for our FinTech banking platform. It provides secure, scalable, and efficient payment processing capabilities with real-time transaction validation and fraud detection.

## Features

- **Real-time Payment Processing**: Process payments instantly with sub-second response times
- **Multi-currency Support**: Handle transactions in over 50 currencies
- **Fraud Detection**: Advanced ML-based fraud detection and prevention
- **Transaction Management**: Complete transaction lifecycle management
- **User Account Integration**: Seamless integration with user account services
- **Audit Trail**: Comprehensive transaction logging and audit capabilities

## Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Web Client    │    │   Mobile App    │    │   Partner API   │
└─────────┬───────┘    └─────────┬───────┘    └─────────┬───────┘
          │                      │                      │
          └──────────────────────┼──────────────────────┘
                                 │
                    ┌─────────────┴───────────┐
                    │    API Gateway          │
                    └─────────────┬───────────┘
                                  │
                    ┌─────────────┴───────────┐
                    │   Payments Service      │
                    └─────────────┬───────────┘
                                  │
          ┌───────────────────────┼───────────────────────┐
          │                       │                       │
    ┌─────┴──────┐    ┌──────────┴────────┐    ┌─────────┴────────┐
    │   MySQL    │    │   Redis Cache     │    │   Kafka Queue    │
    │ Database   │    │                   │    │                  │
    └────────────┘    └───────────────────┘    └──────────────────┘
```

## Technology Stack

- **Framework**: Spring Boot 3.5.5
- **Language**: Java 17
- **Database**: MySQL 8.0
- **Cache**: Redis 6.2
- **Message Queue**: Apache Kafka 3.4
- **Build Tool**: Maven 3.9
- **Container**: Docker
- **Orchestration**: Kubernetes

## Quick Start

### Prerequisites

- Java 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+

### Local Development

1. **Clone the repository**
   ```bash
   git clone https://github.com/fintech-corp/payments-service.git
   cd payments-service
   ```

2. **Configure database**
   ```bash
   mysql -u root -p < scripts/schema.sql
   ```

3. **Set environment variables**
   ```bash
   export MYSQL_HOST=localhost
   export MYSQL_PORT=3306
   export MYSQL_DB=payments_db
   export MYSQL_USER=payments_user
   export MYSQL_PASSWORD=your_password
   export REDIS_HOST=localhost
   export REDIS_PORT=6379
   ```

4. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

The service will be available at `http://localhost:8080`

### Docker Deployment

```bash
docker build -t payments-service:1.2.3 .
docker run -p 8080:8080 \
  -e MYSQL_HOST=db.fintech-corp.com \
  -e MYSQL_USER=payments_user \
  -e MYSQL_PASSWORD=${DB_PASSWORD} \
  payments-service:1.2.3
```

## API Documentation

### Authentication

All endpoints require Bearer token authentication:
```
Authorization: Bearer <JWT_TOKEN>
```

### Core Endpoints

#### Process Payment
```http
POST /api/v1/payments/process
Content-Type: application/json
Authorization: Bearer <token>

{
  "amount": 100.00,
  "currency": "USD",
  "fromAccountId": "acc_123456",
  "toAccountId": "acc_789012",
  "description": "Payment description"
}
```

#### Get Transaction History
```http
GET /api/v1/transactions?userId=123&limit=50
Authorization: Bearer <token>
```

#### Get Account Balance
```http
GET /api/v1/accounts/{accountId}/balance
Authorization: Bearer <token>
```

### Response Format

```json
{
  "success": true,
  "data": {
    "transactionId": "txn_abc123def456",
    "status": "COMPLETED",
    "amount": 100.00,
    "currency": "USD",
    "timestamp": "2024-03-15T10:30:00Z"
  },
  "message": "Payment processed successfully"
}
```

## Configuration

### Application Properties

Key configuration parameters:

```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://${MYSQL_HOST}:${MYSQL_PORT}/${MYSQL_DB}
spring.datasource.username=${MYSQL_USER}
spring.datasource.password=${MYSQL_PASSWORD}

# Redis Configuration
spring.redis.host=${REDIS_HOST}
spring.redis.port=${REDIS_PORT}

# Security Configuration
app.jwt.secret=${JWT_SECRET}
app.jwt.expiration=86400000

# Business Rules
app.payment.max-amount=10000.00
app.payment.daily-limit=50000.00
app.fraud.threshold=1000.00
```

### Environment Variables

| Variable | Description | Required | Default |
|----------|-------------|----------|---------|
| `MYSQL_HOST` | MySQL database host | Yes | - |
| `MYSQL_PORT` | MySQL database port | No | 3306 |
| `MYSQL_DB` | Database name | Yes | - |
| `MYSQL_USER` | Database username | Yes | - |
| `MYSQL_PASSWORD` | Database password | Yes | - |
| `REDIS_HOST` | Redis cache host | Yes | - |
| `REDIS_PORT` | Redis cache port | No | 6379 |
| `JWT_SECRET` | JWT signing secret | Yes | - |
| `KAFKA_BROKERS` | Kafka broker URLs | Yes | - |

## Monitoring & Health Checks

### Health Endpoints

- **Health Check**: `GET /actuator/health`
- **Metrics**: `GET /actuator/metrics`
- **Info**: `GET /actuator/info`

### Monitoring Dashboard

Access the monitoring dashboard at: https://grafana.fintech-corp.com/d/payments-service

## Security

This service implements multiple security layers:

- **JWT Authentication**: All endpoints require valid JWT tokens
- **Rate Limiting**: API rate limiting to prevent abuse
- **Input Validation**: Comprehensive input validation and sanitization
- **Encryption**: All sensitive data encrypted at rest and in transit
- **Audit Logging**: Complete audit trail for all transactions

### Security Headers

The service automatically adds security headers:
- `X-Content-Type-Options: nosniff`
- `X-Frame-Options: DENY`
- `X-XSS-Protection: 1; mode=block`
- `Strict-Transport-Security: max-age=31536000; includeSubDomains`

## Performance

### Benchmarks

- **Throughput**: 10,000+ transactions per second
- **Latency**: < 50ms for payment processing
- **Availability**: 99.99% uptime SLA
- **Concurrency**: Handles 1000+ concurrent users

### Optimization

- Redis caching for frequent queries
- Database connection pooling
- Asynchronous processing for non-critical operations
- Load balancing across multiple instances

## Development

### Code Style

This project follows the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html).

Run code formatting:
```bash
mvn spotless:apply
```

### Testing

```bash
# Run unit tests
mvn test

# Run integration tests
mvn verify

# Generate test coverage report
mvn jacoco:report
```

### Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/payment-improvements`
3. Commit your changes: `git commit -am 'Add new payment features'`
4. Push to the branch: `git push origin feature/payment-improvements`
5. Submit a pull request

## Deployment

### Staging Environment

```bash
kubectl apply -f k8s/staging/
```

### Production Environment

```bash
kubectl apply -f k8s/production/
```

### CI/CD Pipeline

The deployment pipeline automatically:
1. Runs unit and integration tests
2. Performs security scans
3. Builds Docker image
4. Deploys to staging environment
5. Runs automated acceptance tests
6. Deploys to production (with approval)

## Troubleshooting

### Common Issues

**Database Connection Errors**
```bash
# Check database connectivity
telnet ${MYSQL_HOST} ${MYSQL_PORT}

# Verify credentials
mysql -h ${MYSQL_HOST} -u ${MYSQL_USER} -p
```

**Redis Connection Issues**
```bash
# Test Redis connection
redis-cli -h ${REDIS_HOST} -p ${REDIS_PORT} ping
```

**High Memory Usage**
```bash
# Monitor JVM memory
jstack <PID>
jstat -gc <PID>
```

### Logs

Application logs are structured in JSON format:

```json
{
  "timestamp": "2024-03-15T10:30:00.123Z",
  "level": "INFO",
  "logger": "com.fintech.core.payments.service.PaymentService",
  "message": "Payment processed successfully",
  "transaction_id": "txn_abc123def456",
  "user_id": "user_123456",
  "amount": 100.00
}
```

View logs in production:
```bash
kubectl logs -f deployment/payments-service -n payments
```

## Support

- **Internal Wiki**: https://wiki.fintech-corp.com/payments-service
- **Slack Channel**: #payments-service-support
- **On-call**: payments-oncall@fintech-corp.com
- **Incident Response**: https://incident.fintech-corp.com

## License

Copyright (c) 2024 FinTech Corp. All rights reserved.

This software is proprietary and confidential. Unauthorized copying, distribution, or use is strictly prohibited.

---

**Team**: Payments Engineering Team  
**Maintainer**: payments-team@fintech-corp.com  
**Last Updated**: March 2024

---
*DevSecOps training material developed by [Dany Elliot](https://github.com/danyelliot)*