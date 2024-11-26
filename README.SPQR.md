To create the same **Reactive Spring Boot application** with a **GraphQL API** backed by **SPQR** instead of Spring GraphQL, follow the steps below.

---

### **Step 1: Generate a Spring Boot Project**
Use [Spring Initializr](https://start.spring.io/) with the following configuration:

- **Dependencies**:
    - Spring WebFlux
    - Spring Data R2DBC
    - H2 Database
    - Lombok

---

### **Step 2: Add SPQR Dependency**
Add SPQR to your `pom.xml`:

```xml
<dependency>
    <groupId>io.leangen.graphql</groupId>
    <artifactId>spqr</artifactId>
    <version>0.11.0</version>
</dependency>
```

---

### **Step 3: Configure `application.yml`**
Set up H2 database connection:

```yaml
spring:
  r2dbc:
    url: r2dbc:h2:mem:///testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
    username: sa
    password:
  sql:
    init:
      mode: always
logging:
  level:
    org.springframework: INFO
```

---

### **Step 4: Create the Database Schema**
Add `data.sql` to `src/main/resources`:

```sql
CREATE TABLE account_owner (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE account (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    owner_id BIGINT NOT NULL,
    account_name VARCHAR(255),
    FOREIGN KEY (owner_id) REFERENCES account_owner (id)
);

CREATE TABLE transaction (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    account_id BIGINT NOT NULL,
    amount DECIMAL(15, 2),
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (account_id) REFERENCES account (id)
);

INSERT INTO account_owner (name) VALUES ('John Doe'), ('Jane Smith');
INSERT INTO account (owner_id, account_name) VALUES (1, 'John\'s Savings'), (2, 'Jane\'s Checking');
INSERT INTO transaction (account_id, amount) VALUES (1, 1000.50), (2, 250.75);
```

---

### **Step 5: Define Domain Models**
Create entities for `AccountOwner`, `Account`, and `Transaction` using Lombok:

#### AccountOwner:
```java
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("account_owner")
public class AccountOwner {
    @Id
    private Long id;
    private String name;
}
```

#### Account:
```java
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("account")
public class Account {
    @Id
    private Long id;
    private Long ownerId;
    private String accountName;
}
```

#### Transaction:
```java
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("transaction")
public class Transaction {
    @Id
    private Long id;
    private Long accountId;
    private Double amount;
    private java.time.LocalDateTime timestamp;
}
```

---

### **Step 6: Create Repositories**
Define `ReactiveCrudRepository` interfaces:

```java
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface AccountOwnerRepository extends ReactiveCrudRepository<AccountOwner, Long> {}
public interface AccountRepository extends ReactiveCrudRepository<Account, Long> {
    Flux<Account> findAllByOwnerId(Long ownerId);
}
public interface TransactionRepository extends ReactiveCrudRepository<Transaction, Long> {
    Flux<Transaction> findAllByAccountId(Long accountId);
}
```

---

### **Step 7: Create a Service**
Write a service to aggregate relational data into hierarchical structures:

```java
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class FinancialService {
    private final AccountOwnerRepository ownerRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public FinancialService(AccountOwnerRepository ownerRepository,
                            AccountRepository accountRepository,
                            TransactionRepository transactionRepository) {
        this.ownerRepository = ownerRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    public Mono<AccountOwnerDetails> getAccountOwnerDetails(Long ownerId) {
        return ownerRepository.findById(ownerId)
                .flatMap(owner -> accountRepository.findAllByOwnerId(owner.getId())
                        .flatMap(account -> transactionRepository.findAllByAccountId(account.getId())
                                .collectList()
                                .map(transactions -> new AccountDetails(account.getAccountName(), transactions))
                        )
                        .collectList()
                        .map(accounts -> new AccountOwnerDetails(owner.getName(), accounts))
                );
    }
}

record AccountOwnerDetails(String ownerName, List<AccountDetails> accounts) {}
record AccountDetails(String accountName, List<Transaction> transactions) {}
```

---

### **Step 8: Configure SPQR**
Expose the service as a GraphQL API using SPQR:

#### GraphQL Endpoint:
```java
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import io.leangen.graphql.GraphQLSchemaGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class GraphQLProvider {

    private final FinancialService financialService;

    public GraphQLProvider(FinancialService financialService) {
        this.financialService = financialService;
    }

    @Bean
    public GraphQL graphQL() {
        GraphQLSchema schema = new GraphQLSchemaGenerator()
                .withBasePackages("com.example") // Adjust to your package
                .withOperationsFromSingleton(financialService)
                .generate();
        return GraphQL.newGraphQL(schema).build();
    }
}
```

---

### **Step 9: Expose a Controller for GraphQL**
Create a REST controller to handle GraphQL queries:

```java
import graphql.ExecutionInput;
import graphql.GraphQL;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class GraphQLController {

    private final GraphQL graphQL;

    public GraphQLController(GraphQL graphQL) {
        this.graphQL = graphQL;
    }

    @PostMapping("/graphql")
    public Map<String, Object> execute(@RequestBody Map<String, Object> request) {
        String query = (String) request.get("query");
        ExecutionInput executionInput = ExecutionInput.newExecutionInput()
                .query(query)
                .build();
        return graphQL.execute(executionInput).toSpecification();
    }
}
```

---

### **Step 10: Test the GraphQL API**
Run the application and test using `curl`:

#### GraphQL Query:
```graphql
query {
    getAccountOwnerDetails(ownerId: 1) {
        ownerName
        accounts {
            accountName
            transactions {
                id
                amount
                timestamp
            }
        }
    }
}
```

#### cURL Command:
```bash
curl -X POST http://localhost:8080/graphql \
     -H "Content-Type: application/json" \
     -d '{
          "query": "query { getAccountOwnerDetails(ownerId: 1) { ownerName accounts { accountName transactions { id amount timestamp } } } }"
         }'
```

#### Expected Response:
```json
{
  "data": {
    "getAccountOwnerDetails": {
      "ownerName": "John Doe",
      "accounts": [
        {
          "accountName": "John's Savings",
          "transactions": [
            {
              "id": "1",
              "amount": 1000.5,
              "timestamp": "2024-11-25T10:15:30"
            }
          ]
        }
      ]
    }
  }
}
```

---

This step-by-step guide sets up a GraphQL API using **SPQR** in a **Reactive Spring Boot application** with an R2DBC H2 database. It leverages SPQR's schema generation and integrates with Spring WebFlux seamlessly.