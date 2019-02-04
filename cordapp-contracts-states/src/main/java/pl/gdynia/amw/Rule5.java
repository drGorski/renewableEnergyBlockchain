package pl.gdynia.amw;

import net.corda.core.contracts.CommandWithParties;
import net.corda.core.transactions.LedgerTransaction;

import java.security.PublicKey;
import java.util.List;

import static net.corda.core.contracts.ContractsDSL.requireSingleCommand;

public class Rule5 implements VRule {

    private static final String ERROR_MSG = "There must be two signers.";

    @Override
    public boolean runRule(LedgerTransaction tx) {
        final CommandWithParties<IOUContract.Create> command = requireSingleCommand(tx.getCommands(), IOUContract.Create.class);
        final List<PublicKey > signers = command.getSigners();
        return signers.size() == 2;
    }

    @Override
    public String toString() {
        return ERROR_MSG;
    }
}
