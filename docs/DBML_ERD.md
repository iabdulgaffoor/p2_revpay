# ERD in DBML Format (dbdiagram.io)

Copy and paste the code below into [dbdiagram.io](https://dbdiagram.io) to generate the ER diagram.

```dbml
// RevPay Entity Relationship Diagram
// Use this at dbdiagram.io

Table rev_users {
  id long [pk, increment]
  full_name varchar
  email varchar [unique]
  phone_number varchar [unique]
  password varchar
  role varchar // PERSONAL, BUSINESS, ADMIN
  business_name varchar
  business_type varchar
  tax_id varchar
  is_business_verified boolean
  is_active boolean
}

Table wallets {
  id long [pk, increment]
  balance decimal
  user_id long [ref: - rev_users.id]
}

Table transactions {
  id long [pk, increment]
  sender_id long [ref: > rev_users.id]
  recipient_id long [ref: > rev_users.id]
  amount decimal
  type varchar // SEND, REQUEST, ADD_FUNDS, WITHDRAW, PAYMENT
  status varchar // PENDING, COMPLETED, FAILED, CANCELLED
  timestamp datetime
  note varchar
}

Table money_requests {
  id long [pk, increment]
  requester_id long [ref: > rev_users.id]
  requestee_id long [ref: > rev_users.id]
  amount decimal
  status varchar // PENDING, ACCEPTED, DECLINED, CANCELLED
  created_at datetime
  purpose varchar
}

Table notifications {
  id long [pk, increment]
  user_id long [ref: > rev_users.id]
  content varchar
  is_read boolean
  created_at datetime
}
```
