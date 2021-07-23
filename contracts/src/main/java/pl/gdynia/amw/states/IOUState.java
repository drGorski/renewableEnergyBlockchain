package pl.gdynia.amw.states;

import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.ContractState;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import pl.gdynia.amw.contracts.IOUContract;

import java.util.Arrays;
import java.util.List;

@BelongsToContract(IOUContract.class)
public class IOUState implements ContractState {

    private Integer value;
    private Party producer;
    private Party buyer;

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

    @Override
    public List<AbstractParty> getParticipants() {
        return Arrays.asList(producer,buyer);
    }
}
