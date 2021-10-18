package pl.gdynia.amw.contract;

import com.google.common.collect.ImmutableList;
import net.corda.core.identity.CordaX500Name;
import net.corda.testing.core.TestIdentity;
import net.corda.testing.node.MockServices;
import org.junit.Test;
import pl.gdynia.amw.contracts.IOUContract;
import pl.gdynia.amw.states.IOUState;
import pl.gdynia.amw.vrules.impl.*;

import static java.util.Arrays.asList;
import static net.corda.testing.node.NodeTestUtils.ledger;

public class IOUContractTest {
    static private final MockServices ledgerServices = new MockServices(asList("pl.gdynia.amw.contracts", "pl.gdynia.amw.flows"));
    static private final TestIdentity gdyniaA = new TestIdentity(new CordaX500Name("TestA", "Gdynia", "PL"));
    static private final TestIdentity gdyniaB = new TestIdentity(new CordaX500Name("TestB", "Gdynia", "PL"));
    static private final int iouValue = 1;

    @Test
    public void transactionMustIncludeCreateCommand() {
        ledger(ledgerServices, (ledger -> {
            ledger.transaction(tx -> {
                tx.output(IOUContract.ID, new IOUState(iouValue, gdyniaA.getParty(), gdyniaB.getParty()));
                tx.fails();
                tx.command(ImmutableList.of(gdyniaA.getPublicKey(), gdyniaB.getPublicKey()), new IOUContract.Commands.Create());
                tx.verifies();
                return null;
            });
            return null;
        }));
    }

    @Test
    public void transactionMustHaveNoInputs_fails() {
        ledger(ledgerServices, (ledger -> {
            ledger.transaction(tx -> {
                tx.input(IOUContract.ID, new IOUState(iouValue, gdyniaB.getParty(), gdyniaA.getParty()));
                tx.output(IOUContract.ID, new IOUState(iouValue, gdyniaB.getParty(), gdyniaA.getParty()));
                tx.command(ImmutableList.of(gdyniaA.getPublicKey(), gdyniaB.getPublicKey()), new IOUContract.Commands.Create());
                tx.failsWith(new NoInputVRule().errorMsg());
                return null;
            });
            return null;
        }));
    }

    @Test
    public void transactionMustHaveNoInputs_pass() {
        ledger(ledgerServices, (ledger -> {
            ledger.transaction(tx -> {
                tx.output(IOUContract.ID, new IOUState(iouValue, gdyniaB.getParty(), gdyniaA.getParty()));
                tx.command(ImmutableList.of(gdyniaA.getPublicKey(), gdyniaB.getPublicKey()), new IOUContract.Commands.Create());
                tx.verifies();
                return null;
            });
            return null;
        }));
    }

    @Test
    public void transactionMustHaveOneOutput_fails() {
        ledger(ledgerServices, (ledger -> {
            ledger.transaction(tx -> {
                tx.output(IOUContract.ID, new IOUState(iouValue, gdyniaB.getParty(), gdyniaA.getParty()));
                tx.output(IOUContract.ID, new IOUState(iouValue, gdyniaB.getParty(), gdyniaA.getParty()));
                tx.command(ImmutableList.of(gdyniaA.getPublicKey(), gdyniaB.getPublicKey()), new IOUContract.Commands.Create());
                tx.failsWith(new OneOutputVRule().errorMsg());
                return null;
            });
            return null;
        }));
    }

    @Test
    public void transactionMustHaveOneOutput_pass() {
        ledger(ledgerServices, (ledger -> {
            ledger.transaction(tx -> {
                tx.output(IOUContract.ID, new IOUState(iouValue, gdyniaB.getParty(), gdyniaA.getParty()));
                tx.command(ImmutableList.of(gdyniaA.getPublicKey(), gdyniaB.getPublicKey()), new IOUContract.Commands.Create());
                tx.verifies();
                return null;
            });
            return null;
        }));
    }

    @Test
    public void producerAndBuyerSignsTransaction_fails() {
        ledger(ledgerServices, (ledger -> {
            ledger.transaction(tx -> {
                tx.output(IOUContract.ID, new IOUState(iouValue, gdyniaB.getParty(), gdyniaA.getParty()));
                tx.command(ImmutableList.of(gdyniaB.getPublicKey(), gdyniaB.getPublicKey()), new IOUContract.Commands.Create());
                tx.failsWith(new ProducersAndBuyersAsSignersVRule().errorMsg());
                return null;
            });
            return null;
        }));
    }

    @Test
    public void producerAndBuyerSignsTransaction_pass() {
        ledger(ledgerServices, (ledger -> {
            ledger.transaction(tx -> {
                tx.output(IOUContract.ID, new IOUState(iouValue, gdyniaB.getParty(), gdyniaA.getParty()));
                tx.command(ImmutableList.of(gdyniaB.getPublicKey(), gdyniaA.getPublicKey()), new IOUContract.Commands.Create());
                tx.verifies();
                return null;
            });
            return null;
        }));
    }

    @Test
    public void twoSigners_fails() {
        ledger(ledgerServices, (ledger -> {
            ledger.transaction(tx -> {
                tx.output(IOUContract.ID, new IOUState(iouValue, gdyniaB.getParty(), gdyniaA.getParty()));
                tx.command(gdyniaA.getPublicKey(), new IOUContract.Commands.Create());
                tx.failsWith(new TwoSignersVRule().errorMsg());
                return null;
            });
            return null;
        }));
    }

    @Test
    public void twoSigners_pass() {
        ledger(ledgerServices, (ledger -> {
            ledger.transaction(tx -> {
                tx.output(IOUContract.ID, new IOUState(iouValue, gdyniaB.getParty(), gdyniaA.getParty()));
                tx.command(ImmutableList.of(gdyniaA.getPublicKey(), gdyniaB.getPublicKey()), new IOUContract.Commands.Create());
                tx.verifies();
                return null;
            });
            return null;
        }));
    }

    @Test
    public void producerIsNotBuyer_fails() {
        final TestIdentity testIdentity = new TestIdentity(gdyniaA.getName(), gdyniaA.getKeyPair());
        ledger(ledgerServices, (ledger -> {
            ledger.transaction(tx -> {
                tx.output(IOUContract.ID, new IOUState(iouValue, gdyniaA.getParty(), testIdentity.getParty()));
                tx.command(ImmutableList.of(gdyniaA.getPublicKey(), gdyniaA.getPublicKey()), new IOUContract.Commands.Create());
                tx.failsWith(new DifferentSellerAndBuyerVRule().errorMsg());
                return null;
            });
            return null;
        }));
    }

    @Test
    public void producerIsNotBuyer_pass() {
        final TestIdentity testIdentity = new TestIdentity(gdyniaA.getName(), gdyniaA.getKeyPair());
        ledger(ledgerServices, (ledger -> {
            ledger.transaction(tx -> {
                tx.output(IOUContract.ID, new IOUState(iouValue, gdyniaB.getParty(), testIdentity.getParty()));
                tx.command(ImmutableList.of(gdyniaA.getPublicKey(), gdyniaB.getPublicKey()), new IOUContract.Commands.Create());
                tx.verifies();
                return null;
            });
            return null;
        }));
    }

    @Test
    public void negativeValue_fails() {
        ledger(ledgerServices, (ledger -> {
            ledger.transaction(tx -> {
                tx.output(IOUContract.ID, new IOUState(-1, gdyniaB.getParty(), gdyniaA.getParty()));
                tx.command(ImmutableList.of(gdyniaA.getPublicKey(), gdyniaB.getPublicKey()), new IOUContract.Commands.Create());
                tx.failsWith(new NonNegativeValueVRule().errorMsg());
                return null;
            });
            return null;
        }));
    }

    @Test
    public void negativeValue_pass() {
        ledger(ledgerServices, (ledger -> {
            ledger.transaction(tx -> {
                tx.output(IOUContract.ID, new IOUState(iouValue, gdyniaB.getParty(), gdyniaA.getParty()));
                tx.command(ImmutableList.of(gdyniaA.getPublicKey(), gdyniaB.getPublicKey()), new IOUContract.Commands.Create());
                tx.verifies();
                return null;
            });
            return null;
        }));
    }

}
