package pl.gdynia.amw;

import com.google.common.collect.ImmutableList;
import net.corda.core.contracts.CommandWithParties;
import net.corda.core.identity.Party;
import net.corda.core.transactions.LedgerTransaction;

import java.security.PublicKey;
import java.util.List;

import static net.corda.core.contracts.ContractsDSL.requireSingleCommand;

public class Rule6 implements VRule {

    private static final String ERROR_MSG = "The producer and the buyer must be signers.";

    @Override
    public boolean runRule(LedgerTransaction tx) {
        final IOUState out = tx.outputsOfType(IOUState.class).get(0);
        final Party producer = out.getProducer();
        final Party buyer = out.getBuyer();

        final CommandWithParties<IOUContract.Create> command = requireSingleCommand(tx.getCommands(), IOUContract.Create.class);
        final List<PublicKey> signers = command.getSigners();
        return signers.containsAll(
                ImmutableList.of(producer.getOwningKey(), buyer.getOwningKey()));
    }

    @Override
    public String toString() {
        return ERROR_MSG;
    }
}
