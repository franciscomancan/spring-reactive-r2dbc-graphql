package org.example.springreactiver2dbcgraphql.service;

import org.example.springreactiver2dbcgraphql.model.Transaction;

import java.util.List;

public record AccountDetails(String accountName, List<Transaction> transactions) {}
