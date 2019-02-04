package pl.gdynia.amw;

import java.util.ArrayList;
import java.util.List;

public class IOUContract extends VRContract {

    public static final String IOU_CONTRACT_ID = "pl.gdynia.amw.IOUContract";

    private List<VRule> rules = new ArrayList<>();

    public IOUContract() {
        super();
    }


    public void setRules() {
        rules.add(new Rule1());
        rules.add(new Rule2());
        rules.add(new Rule3());
        rules.add(new Rule4());
        rules.add(new Rule5());
        rules.add(new Rule6());
    }

}
