package org.galegofer.moneytransfer.domain;

import javax.persistence.Column;
import javax.persistence.Entity;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import io.smallrye.mutiny.Uni;

@Entity
public class Account extends PanacheEntity {
    @Column(name = "account_id", nullable = false)
    public String accountId;

    @Column(nullable = false)
    public String currency;

    @Column(nullable = false)
    public Double balance;

    public static Uni<Account> findByAccountId(String accountId) {
        return find("account_id", accountId).firstResult();
    }

    public static Uni<Integer> updateAmount(String accountId, Double balance) {
        return update("update from Account set balance = ? where account_id = ?", balance, accountId);
    }
}
