package pl.gdynia.amw.contracts;

import com.sun.istack.NotNull;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.Contract;
import net.corda.core.transactions.LedgerTransaction;
import pl.gdynia.amw.vrules.VRule;

import java.util.List;

import static net.corda.core.contracts.ContractsDSL.requireThat;

abstract public class VRContract implements Contract {
    protected List<VRule> rules;

    @Override
    public void verify(@NotNull LedgerTransaction tx) throws IllegalArgumentException {
        setRules();
        requireThat(check -> {
            rules.forEach(rule -> check.using(rule.errorMsg(), rule.runRule(tx)));
            return null;
        });
    }

    abstract void setRules();

    public interface Commands extends CommandData {
        class Create implements Commands { }
    }
}
