package pl.gdynia.amw;

import net.corda.core.transactions.LedgerTransaction;

public interface VRule {

    boolean runRule(LedgerTransaction tx);


}
