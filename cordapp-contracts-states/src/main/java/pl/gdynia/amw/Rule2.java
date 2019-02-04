package pl.gdynia.amw;

import net.corda.core.transactions.LedgerTransaction;

public class Rule2 implements VRule {

    private static final String ERROR_MSG = "There should be one output state of type IOUState.";

    @Override
    public boolean runRule(LedgerTransaction tx) {
        return tx.getOutputs().size() == 1;
    }

    @Override
    public String toString() {
        return ERROR_MSG;
    }
}
