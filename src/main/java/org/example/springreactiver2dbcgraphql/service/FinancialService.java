package org.example.springreactiver2dbcgraphql.service;

import org.example.springreactiver2dbcgraphql.data.AccountOwnerRepository;
import org.example.springreactiver2dbcgraphql.data.AccountRepository;
import org.example.springreactiver2dbcgraphql.data.TransactionRepository;
import org.example.springreactiver2dbcgraphql.model.Transaction;
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

