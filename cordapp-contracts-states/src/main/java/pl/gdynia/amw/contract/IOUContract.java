package pl.gdynia.amw.contract;

import com.google.common.collect.ImmutableList;
import pl.gdynia.amw.vrules.*;
import pl.gdynia.amw.vrules.impl.*;

import java.util.Collection;

public class IOUContract extends VRContract {

    public static final String IOU_CONTRACT_ID = "pl.gdynia.amw.contract.IOUContract";

    private Collection<VRule> rules;

    public IOUContract() {
        super();
    }

    public void setRules() {
        rules = ImmutableList.of(
                new NoInputVRule(),
                new OneOutputVRule(),
                new NonNegativeValueVRule(),
                new DifferentSellerAndBuyerVRule(),
                new TwoSignersVRule(),
                new ProducersAndBuyersAsSignersVRule());
    }

}
