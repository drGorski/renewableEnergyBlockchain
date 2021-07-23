package pl.gdynia.amw.vrules.impl;

import net.corda.core.transactions.LedgerTransaction;
import pl.gdynia.amw.vrules.VRule;

public class OneOutputVRule implements VRule {

    private static final String ERROR_MSG = "There should be one output state of type IOUState.";

    @Override
    public boolean runRule(LedgerTransaction tx) {
        return tx.getOutputs().size() == 1;
    }

    @Override
    public String errorMsg() {
        return ERROR_MSG;
    }
}
