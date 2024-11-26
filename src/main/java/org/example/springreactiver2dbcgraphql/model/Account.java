package org.example.springreactiver2dbcgraphql.model;

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