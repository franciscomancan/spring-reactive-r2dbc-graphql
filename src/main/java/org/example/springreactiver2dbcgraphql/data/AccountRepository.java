package org.example.springreactiver2dbcgraphql.data;

import org.example.springreactiver2dbcgraphql.model.Account;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface AccountRepository extends ReactiveCrudRepository<Account, Long> {
    Flux<Account> findAllByOwnerId(Long ownerId);
}
