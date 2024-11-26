package org.example.springreactiver2dbcgraphql.control;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.graphql.test.tester.GraphQlTester;

@GraphQlTest
class FinancialControllerTest {

    @Autowired
    private GraphQlTester graphQlTester;

    @Test
    void testAccountOwnerDetailsQuery() {
        String query = """
            query {
                accountOwnerDetails(ownerId: 1) {
                    ownerName
                    accounts {
                        accountName
                        transactions {
                            amount
                            timestamp
                        }
                    }
                }
            }
        """;

        graphQlTester.document(query)
                .execute()
                .path("accountOwnerDetails.ownerName")
                .entity(String.class)
                .isEqualTo("John Doe")
                .path("accountOwnerDetails.accounts[0].accountName")
                .entity(String.class)
                .isEqualTo("John's Savings")
                .path("accountOwnerDetails.accounts[0].transactions[0].amount")
                .entity(Double.class)
                .isEqualTo(1000.50);
    }
}
