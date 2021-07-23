package pl.gdynia.amw.vrules;

import net.corda.core.transactions.LedgerTransaction;

public interface VRule {
    boolean runRule(LedgerTransaction tx);
    String errorMsg();
}
