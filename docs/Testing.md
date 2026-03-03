# Testing Strategy - RevPay

RevPay follows a comprehensive testing approach to ensure reliability and security.

## 1. Unit Testing
- **Services:** Business logic in `TransactionServiceImpl` and `WalletServiceImpl` is verified for edge cases like insufficient balance.
- **Mappers:** `GenericMapper` is tested to ensure accurate DTO conversion without data loss.

## 2. Integration Testing
- **REST APIs:** `MockMvc` tests verify JSON responses and status codes.
- **Web Controllers:** Redirection logic and model attributes are verified for the dashboard and login flows.

## 3. Security Testing
- **Access Control:** Verified that non-business users cannot access loan application or invoicing endpoints.
- **Authentication:** Verified that unauthorized requests are redirected to the login page.

## 4. Manual Verification Steps
1. **Registration:** Register as a PERSONAL user and verify automatic wallet creation.
2. **Transfer:** Perform a transfer and verify the "Successful" status and balance deduction.
3. **Responsive UI:** Verify dashboard elements adjust correctly on smaller screens.
