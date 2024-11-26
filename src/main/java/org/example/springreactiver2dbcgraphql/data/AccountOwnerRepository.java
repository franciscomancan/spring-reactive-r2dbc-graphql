package org.example.springreactiver2dbcgraphql.data;

import org.example.springreactiver2dbcgraphql.model.AccountOwner;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface AccountOwnerRepository extends ReactiveCrudRepository<AccountOwner, Long> {}

