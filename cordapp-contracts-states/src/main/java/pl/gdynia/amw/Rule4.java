package pl.gdynia.amw;

import net.corda.core.identity.Party;
import net.corda.core.transactions.LedgerTransaction;

public class Rule4 implements VRule {

    private static final String ERROR_MSG = "The producer and the buyer cannot be the same entity.";

    @Override
    public boolean runRule(LedgerTransaction tx) {
        // IOU-specific constraints.
        final IOUState out = tx.outputsOfType(IOUState.class).get(0);
        final Party producer = out.getProducer();
        final Party buyer = out.getBuyer();
        return producer != buyer;
    }

    @Override
    public String toString() {
        return ERROR_MSG;
    }
}
