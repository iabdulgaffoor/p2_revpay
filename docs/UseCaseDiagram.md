# Use Case Diagram

The following diagram defines the interactions between various actors and the RevPay system.

```mermaid
useCaseDiagram
    actor "Personal User" as PU
    actor "Business User" as BU
    actor "Administrator" as Admin
    
    package "RevPay System" {
        usecase "Login / MFA" as UC1
        usecase "Manage Wallet" as UC2
        usecase "Send Money" as UC3
        usecase "Request Money" as UC4
        usecase "Generate Invoices" as UC5
        usecase "Verify Business Profile" as UC6
        usecase "Manage User Status" as UC7
        usecase "View System Logs" as UC8
    }
    
    PU --> UC1
    PU --> UC2
    PU --> UC3
    PU --> UC4
    
    BU --> UC1
    BU --> UC2
    BU --> UC3
    BU --> UC4
    BU --> UC5
    
    Admin --> UC1
    Admin --> UC6
    Admin --> UC7
    Admin --> UC8
    
    UC3 ..> UC1 : <<include>>
    UC4 ..> UC1 : <<include>>
```
