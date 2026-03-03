# Security Implementation Guide - RevPay

RevPay implements multi-layered security to ensure user data protection and transactional integrity.

## 1. Authentication
- **Session-Based Auth:** Secure session management for web users.
- **Custom User Details:** `CustomUserDetails` integrates Spring Security with the `User` entity.
- **Password Hashing:** All passwords are encrypted using `BCryptPasswordEncoder` before persistence.

## 2. Authorization (RBAC)
Role-Based Access Control is enforced at the URL level:
- `ROLE_PERSONAL`: Access to dashboard, transfers, and account management.
- `ROLE_BUSINESS`: Advanced access to invoicing, loans, and analytics.
- `ROLE_ADMIN`: Full system visibility and administrative controls.

## 3. Transactional Security
- **Atomic Operations:** All money transfers use `@Transactional` to ensure data consistency.
- **Balance Validation:** Real-time checks prevent overspending or negative balances.
- **PIN Verification:** (Implementation Ready) Secure hash storage for transaction PINs.

## 4. Data Protection
- **DTO Pattern:** Data Transfer Objects used to prevent exposing sensitive internal fields like password hashes or internal IDs to the UI.
- **CSRF Protection:** Configured to prevent cross-site request forgery.
