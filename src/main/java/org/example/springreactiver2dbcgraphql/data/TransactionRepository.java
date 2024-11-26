package org.example.springreactiver2dbcgraphql.data;

import org.example.springreactiver2dbcgraphql.model.Transaction;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface TransactionRepository extends ReactiveCrudRepository<Transaction, Long> {
    Flux<Transaction> findAllByAccountId(Long accountId);
}
