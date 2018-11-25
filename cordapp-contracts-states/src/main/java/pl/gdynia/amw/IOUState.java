package pl.gdynia.amw;

import com.google.common.collect.ImmutableList;
import net.corda.core.contracts.ContractState;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class IOUState implements ContractState {

    private final Integer value;
    private final Party producer;
    private final Party buyer;

    public IOUState(Integer value, Party producer, Party buyer) {
        this.value = value;
        this.producer = producer;
        this.buyer = buyer;
    }

    public Integer getValue() {
        return value;
    }

    public Party getProducer() {
        return producer;
    }

    public Party getBuyer() {
        return buyer;
    }

    @NotNull
    @Override
    public List<AbstractParty> getParticipants() {
        return ImmutableList.of(producer, buyer);
    }
}
