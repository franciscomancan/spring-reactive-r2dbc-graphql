package org.example.springreactiver2dbcgraphql.control;

import org.example.springreactiver2dbcgraphql.service.AccountOwnerDetails;
import org.example.springreactiver2dbcgraphql.service.FinancialService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;


@Controller
public class FinancialController {
    private final FinancialService financialService;

    public FinancialController(FinancialService financialService) {
        this.financialService = financialService;
    }

    @QueryMapping
    public Mono<AccountOwnerDetails> accountOwnerDetails(@Argument Long ownerId) {
        return financialService.getAccountOwnerDetails(ownerId);
    }
}