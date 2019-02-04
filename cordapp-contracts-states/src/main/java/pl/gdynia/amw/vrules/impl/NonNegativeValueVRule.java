package pl.gdynia.amw.vrules.impl;

import net.corda.core.identity.Party;
import net.corda.core.transactions.LedgerTransaction;
import pl.gdynia.amw.state.IOUState;
import pl.gdynia.amw.vrules.VRule;

public class NonNegativeValueVRule implements VRule {

    private static final String ERROR_MSG = "The IOU's value must be non-negative.";

    @Override
    public boolean runRule(LedgerTransaction tx) {
        // IOU-specific constraints.
        final IOUState out = tx.outputsOfType(IOUState.class).get(0);
        final Party producer = out.getProducer();
        final Party buyer = out.getBuyer();
        return out.getValue() > 0;
    }

    @Override
    public String errorMsg() {
        return ERROR_MSG;
    }
}
