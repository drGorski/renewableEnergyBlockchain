package pl.gdynia.amw.contracts;

import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.Contract;
import net.corda.core.transactions.LedgerTransaction;
import org.jetbrains.annotations.NotNull;
import pl.gdynia.amw.vrules.VRule;

import java.util.ArrayList;
import java.util.List;

import static net.corda.core.contracts.ContractsDSL.requireThat;

abstract public class VRContract implements Contract {

    protected List<VRule> rules = new ArrayList<>();

    protected VRContract() {
        setRules();
    }

    @Override
    public void verify(@NotNull LedgerTransaction tx) throws IllegalArgumentException {
        requireThat(check -> {
            rules.forEach(rule -> check.using(rule.errorMsg(), rule.runRule(tx)));
            return null;
        });
    }

    abstract void setRules();

    public static class Create implements CommandData {

    }

    public interface Commands extends CommandData {
        class Action implements Commands {}
    }
}
