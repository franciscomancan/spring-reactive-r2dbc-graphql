package org.example.springreactiver2dbcgraphql.model;

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