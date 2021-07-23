package pl.gdynia.amw.contracts;

import pl.gdynia.amw.vrules.*;
import pl.gdynia.amw.vrules.impl.*;

import java.util.Arrays;
import java.util.Collection;

public class IOUContract extends VRContract {

    public static final String IOU_CONTRACT_ID = "pl.gdynia.amw.contracts.IOUContract";

    private Collection<VRule> rules;

    public IOUContract() {
        super();
    }

    public void setRules() {
        rules = Arrays.asList(
                new NoInputVRule(),
                new OneOutputVRule(),
                new NonNegativeValueVRule(),
                new DifferentSellerAndBuyerVRule(),
                new TwoSignersVRule(),
                new ProducersAndBuyersAsSignersVRule());
    }
}
