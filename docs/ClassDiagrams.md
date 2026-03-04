# Class Diagrams

## 1. Core Entities and Relationships
This diagram shows the domain model and how entities interact.

```mermaid
classDiagram
    class User {
        +Long id
        +String fullName
        +String email
        +Role role
        +isActive()
    }
    class Wallet {
        +Long id
        +BigDecimal balance
        +User user
    }
    class Transaction {
        +Long id
        +User sender
        +User recipient
        +BigDecimal amount
        +TransactionType type
        +TransactionStatus status
    }
    class MoneyRequest {
        +Long id
        +User requester
        +User requestee
        +BigDecimal amount
        +RequestStatus status
    }

    User "1" -- "1" Wallet : has
    User "1" -- "*" Transaction : participates
    User "1" -- "*" MoneyRequest : participates
```

## 2. Service Layer Architecture
This diagram outlines the service interfaces and their implementations.

```mermaid
classDiagram
    class IUserService {
        <<interface>>
        +createUser(UserDTO)
        +getUserByEmail(String)
    }
    class UserServiceImpl {
        -UserRepository userRepository
        -WalletRepository walletRepository
    }
    class ITransactionService {
        <<interface>>
        +transferFunds(senderId, recipientId, amount)
    }
    class TransactionServiceImpl {
        -TransactionRepository transactionRepo
        -IWalletService walletService
    }
    class IWalletService {
        <<interface>>
        +updateBalance(userId, amount)
    }

    UserServiceImpl ..|> IUserService
    TransactionServiceImpl ..|> ITransactionService
    TransactionServiceImpl --> IWalletService
    UserServiceImpl --> IWalletService
```

## 3. Security and Authentication Flow
This diagram illustrates the security components.

```mermaid
classDiagram
    class SecurityConfig {
        +filterChain(HttpSecurity)
        +passwordEncoder()
    }
    class UserSecurity {
        +String username
        +String password
        +Collection authorities
    }
    class CustomUserDetailsService {
        +loadUserByUsername(String)
    }
    class AuthController {
        +login(LoginRequest)
        +register(RegisterRequest)
    }

    CustomUserDetailsService --|> UserDetailsService
    CustomUserDetailsService --> UserRepository
    SecurityConfig --> CustomUserDetailsService
```
