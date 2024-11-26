package org.example.springreactiver2dbcgraphql.service;


import org.example.springreactiver2dbcgraphql.data.AccountOwnerRepository;
import org.example.springreactiver2dbcgraphql.data.AccountRepository;
import org.example.springreactiver2dbcgraphql.data.TransactionRepository;
import org.example.springreactiver2dbcgraphql.model.Account;
import org.example.springreactiver2dbcgraphql.model.AccountOwner;
import org.example.springreactiver2dbcgraphql.model.Transaction;
import org.example.springreactiver2dbcgraphql.service.AccountOwnerDetails;
import org.example.springreactiver2dbcgraphql.service.FinancialService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.util.List;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class FinancialServiceTest {

    private final AccountOwnerRepository ownerRepository = Mockito.mock(AccountOwnerRepository.class);
    private final AccountRepository accountRepository = Mockito.mock(AccountRepository.class);
    private final TransactionRepository transactionRepository = Mockito.mock(TransactionRepository.class);

    private final FinancialService financialService =
            new FinancialService(ownerRepository, accountRepository, transactionRepository);

    @Test
    void testGetAccountOwnerDetails() {
        // Mock data
        AccountOwner owner = new AccountOwner();
        owner.setId(1L);
        owner.setName("John Doe");

        Account account = new Account();
        account.setId(1L);
        account.setOwnerId(1L);
        account.setAccountName("John's Savings");

        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setAccountId(1L);
        transaction.setAmount(1000.50);
        transaction.setTimestamp(java.time.LocalDateTime.now());

        // Mock repository calls
        when(ownerRepository.findById(1L)).thenReturn(Mono.just(owner));
        when(accountRepository.findAllByOwnerId(1L)).thenReturn(Flux.just(account));
        when(transactionRepository.findAllByAccountId(1L)).thenReturn(Flux.just(transaction));

        // Call the service
        Mono<AccountOwnerDetails> result = financialService.getAccountOwnerDetails(1L);

        // Verify the results
        StepVerifier.create(result)
                .expectNextMatches(details ->
                        details.ownerName().equals("John Doe") &&
                                details.accounts().size() == 1 &&
                                details.accounts().get(0).accountName().equals("John's Savings") &&
                                details.accounts().get(0).transactions().size() == 1 &&
                                details.accounts().get(0).transactions().get(0).getAmount().equals(1000.50)
                )
                .verifyComplete();

        // Verify repository interactions
        verify(ownerRepository, times(1)).findById(1L);
        verify(accountRepository, times(1)).findAllByOwnerId(1L);
        verify(transactionRepository, times(1)).findAllByAccountId(1L);
    }
}
