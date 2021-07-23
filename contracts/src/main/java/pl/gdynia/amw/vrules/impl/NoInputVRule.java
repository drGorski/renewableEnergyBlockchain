package pl.gdynia.amw.vrules.impl;

import net.corda.core.transactions.LedgerTransaction;
import pl.gdynia.amw.vrules.VRule;

public class NoInputVRule implements VRule {

    private static final String ERROR_MSG = "No inputs should be consumed when issuing an IOU.";

    @Override
    public boolean runRule(LedgerTransaction tx) {
        return tx.getInputs().isEmpty();
    }

    @Override
    public String errorMsg() {
        return ERROR_MSG;
    }
}
