<!-- Use this file to provide workspace-specific custom instructions to Copilot. For more details, visit https://code.visualstudio.com/docs/copilot/copilot-customization#_use-a-githubcopilotinstructionsmd-file -->

# DevSecOps Vulnerable Banking Microservice

This is an intentionally vulnerable Java Spring Boot banking microservice created for DevSecOps training purposes. The codebase contains deliberately introduced security vulnerabilities to demonstrate various security scanning tools and practices.

## Security Vulnerabilities Included

### SCA (Software Composition Analysis) Vulnerabilities:
- Vulnerable dependencies with known CVEs
- Outdated library versions
- Dependencies with security advisories

### SAST (Static Application Security Testing) Vulnerabilities:
- SQL Injection vulnerabilities
- Command Injection flaws
- Path Traversal vulnerabilities
- XSS (Cross-Site Scripting) issues
- Insecure deserialization
- Weak cryptographic implementations
- Information disclosure issues
- Authentication and authorization bypasses

### Secrets Management Issues:
- Hardcoded API keys and passwords
- Exposed database credentials
- JWT secrets in configuration files
- Cloud service credentials in code
- Encryption keys in plain text

## Important Notice

⚠️ **WARNING**: This application contains intentional security vulnerabilities and should NEVER be deployed in a production environment or exposed to the internet. It is designed solely for educational purposes in a controlled environment.

## Usage Instructions

1. Use this codebase to demonstrate security scanning tools
2. Run SCA tools to identify vulnerable dependencies
3. Execute SAST scanners to find code-level vulnerabilities
4. Use secret scanning tools to detect exposed credentials
5. Practice fixing identified security issues

## Code Structure

The application follows a typical Spring Boot microservice architecture with intentionally vulnerable implementations in:
- Controllers (REST endpoints with security flaws)
- Services (Business logic with vulnerabilities)
- Repositories (Data access with SQL injection risks)
- Utilities (Helper classes with various security issues)
- Configuration (Exposed secrets and weak settings)

Remember: The goal is to learn to identify and remediate these security issues using proper DevSecOps practices and tools.
