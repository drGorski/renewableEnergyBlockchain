package pl.gdynia.amw.vrules.impl;

import net.corda.core.contracts.CommandWithParties;
import net.corda.core.transactions.LedgerTransaction;
import pl.gdynia.amw.contracts.VRContract;
import pl.gdynia.amw.vrules.VRule;

import java.security.PublicKey;
import java.util.List;

import static net.corda.core.contracts.ContractsDSL.requireSingleCommand;

public class TwoSignersVRule implements VRule {

    private static final String ERROR_MSG = "There must be two signers.";

    @Override
    public boolean runRule(LedgerTransaction tx) {
        final CommandWithParties<VRContract.Commands.Create> command = requireSingleCommand(tx.getCommands(), VRContract.Commands.Create.class);
        final List<PublicKey > signers = command.getSigners();
        return signers.size() == 2;
    }

    @Override
    public String errorMsg() {
        return ERROR_MSG;
    }
}
