package pl.gdynia.amw.vrules.impl;

import net.corda.core.contracts.CommandWithParties;
import net.corda.core.identity.Party;
import net.corda.core.transactions.LedgerTransaction;
import pl.gdynia.amw.states.IOUState;
import pl.gdynia.amw.contracts.VRContract;
import pl.gdynia.amw.vrules.VRule;

import java.security.PublicKey;
import java.util.Arrays;
import java.util.List;

import static net.corda.core.contracts.ContractsDSL.requireSingleCommand;

public class ProducersAndBuyersAsSignersVRule implements VRule {

    private static final String ERROR_MSG = "The producer and the buyer must be signers.";

    @Override
    public boolean runRule(LedgerTransaction tx) {
        final IOUState out = tx.outputsOfType(IOUState.class).get(0);
        final Party producer = out.getProducer();
        final Party buyer = out.getBuyer();

        final CommandWithParties<VRContract.Commands.Create> command = requireSingleCommand(tx.getCommands(), VRContract.Commands.Create.class);
        final List<PublicKey> signers = command.getSigners();
        return signers.containsAll(
                Arrays.asList(producer.getOwningKey(), buyer.getOwningKey()));
    }

    @Override
    public String errorMsg() {
        return ERROR_MSG;
    }
}
