package pl.gdynia.amw;

import net.corda.core.transactions.LedgerTransaction;

public class Rule1 implements VRule {

    private static final String ERROR_MSG = "No inputs should be consumed when issuing an IOU.";

    @Override
    public boolean runRule(LedgerTransaction tx) {
        return tx.getInputs().isEmpty();
    }

    @Override
    public String toString() {
        return ERROR_MSG;
    }
}
