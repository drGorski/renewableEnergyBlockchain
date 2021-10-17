package pl.gdynia.amw.vrules.impl;

import net.corda.core.identity.Party;
import net.corda.core.transactions.LedgerTransaction;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.gdynia.amw.states.IOUState;

public class DifferentSellerAndBuyerVRuleTest {

    private final DifferentSellerAndBuyerVRule serviceUnderTest = new DifferentSellerAndBuyerVRule();

    @Mock
    private LedgerTransaction ledgerTransaction;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void producerAndBuyerNotEquals() {
        // given

        // when

        // then
    }
}
