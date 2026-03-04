# Class Diagrams (PlantUML)

Copy and paste the blocks below into a PlantUML editor (e.g., [planttext.com](https://www.planttext.com)).

## 1. Core Entities
```plantuml
@startuml
skinparam classAttributeIconSize 0

class User {
    +Long id
    +String fullName
    +String email
    +Role role
    +Boolean isActive
    +isTransactionAlerts()
    +isSecurityAlerts()
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
    +LocalDateTime timestamp
}

class MoneyRequest {
    +Long id
    +User requester
    +User requestee
    +BigDecimal amount
    +RequestStatus status
    +LocalDateTime createdAt
}

User "1" -- "1" Wallet : has
User "1" -- "*" Transaction : sender
User "1" -- "*" Transaction : recipient
User "1" -- "*" MoneyRequest : requester
User "1" -- "*" MoneyRequest : requestee
@enduml
```

## 2. Service Layer
```plantuml
@startuml
interface IUserService {
    +createUser(UserDTO)
    +getUserByEmail(String)
}

class UserServiceImpl {
    -UserRepository userRepository
    -WalletRepository walletRepository
}

interface ITransactionService {
    +transferFunds(Long, Long, BigDecimal)
}

class TransactionServiceImpl {
    -TransactionRepository transactionRepo
    -IWalletService walletService
}

UserServiceImpl ..|> IUserService
TransactionServiceImpl ..|> ITransactionService
TransactionServiceImpl --> IWalletService
@enduml
```

## 3. Security Flow
```plantuml
@startuml
class SecurityConfig {
    +SecurityFilterChain filterChain(HttpSecurity)
}

class CustomUserDetailsService {
    +UserDetails loadUserByUsername(String)
}

class AuthController {
    +ResponseEntity login(LoginRequest)
    +ResponseEntity register(RegisterRequest)
}

SecurityConfig --> CustomUserDetailsService
AuthController --> IUserService
@enduml
```
