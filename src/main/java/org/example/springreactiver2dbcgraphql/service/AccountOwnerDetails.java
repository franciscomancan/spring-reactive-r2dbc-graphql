package org.example.springreactiver2dbcgraphql.service;

import java.util.List;

public record AccountOwnerDetails(String ownerName, List<AccountDetails> accounts) {}
