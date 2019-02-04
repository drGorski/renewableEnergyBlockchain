package pl.gdynia.amw;

import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.Contract;
import net.corda.core.transactions.LedgerTransaction;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static net.corda.core.contracts.ContractsDSL.requireThat;

abstract public class VRContract implements Contract {

    public static class Create implements CommandData {

    }

    protected List<VRule> rules = new ArrayList<>();

    public VRContract() {
        setRules();
    }

    @Override
    public void verify(@NotNull LedgerTransaction tx) throws IllegalArgumentException {
        requireThat(check -> {
            rules.forEach(rule -> check.using(rule.toString(), rule.runRule(tx)));
            return null;
        });

    }

    abstract void setRules();

    public interface Commands extends CommandData {
        class Action implements Commands {}
    }
}
