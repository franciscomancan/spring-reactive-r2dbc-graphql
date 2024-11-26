package org.example.springreactiver2dbcgraphql.model;

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