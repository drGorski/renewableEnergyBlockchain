package pl.gdynia.amw.vrules.impl;

import net.corda.core.identity.Party;
import net.corda.core.transactions.LedgerTransaction;
import pl.gdynia.amw.states.IOUState;
import pl.gdynia.amw.vrules.VRule;

public class DifferentSellerAndBuyerVRule implements VRule {

    private static final String ERROR_MSG = "The producer and the buyer cannot be the same entity.";

    @Override
    public boolean runRule(LedgerTransaction tx) {
        // IOU-specific constraints.
        final IOUState out = tx.outputsOfType(IOUState.class).get(0);
        final Party producer = out.getProducer();
        final Party buyer = out.getBuyer();
        return !producer.getName().equals(buyer.getName());
    }

    @Override
    public String errorMsg() {
        return ERROR_MSG;
    }
}
