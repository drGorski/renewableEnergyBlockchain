package pl.gdynia.amw.contracts;

import net.corda.core.contracts.Contract;
import pl.gdynia.amw.vrules.*;
import pl.gdynia.amw.vrules.impl.*;

import java.util.Arrays;
import java.util.Collection;

public class IOUContract extends VRContract implements Contract {

    public static final String ID = "pl.gdynia.amw.contracts.IOUContract";

    @Override
    protected void setRules() {
        rules = Arrays.asList(
                new NoInputVRule(),
                new OneOutputVRule(),
                new NonNegativeValueVRule(),
                new DifferentSellerAndBuyerVRule(),
                new TwoSignersVRule(),
                new ProducersAndBuyersAsSignersVRule()
            );
    }
}
