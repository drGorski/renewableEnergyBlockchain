package pl.gdynia.amw.contract;

import com.google.common.collect.ImmutableList;
import net.corda.core.identity.CordaX500Name;
import net.corda.testing.core.TestIdentity;
import net.corda.testing.node.MockServices;
import org.junit.Test;
import pl.gdynia.amw.contracts.IOUContract;
import pl.gdynia.amw.states.IOUState;

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
    public void transactionMustHaveNoInputs() {
        ledger(ledgerServices, (ledger -> {
            ledger.transaction(tx -> {
                tx.input(IOUContract.ID, new IOUState(iouValue, gdyniaB.getParty(), gdyniaA.getParty()));
                tx.output(IOUContract.ID, new IOUState(iouValue, gdyniaB.getParty(), gdyniaA.getParty()));
                tx.command(ImmutableList.of(gdyniaA.getPublicKey(), gdyniaB.getPublicKey()), new IOUContract.Commands.Create());
                tx.failsWith("No inputs should be consumed when issuing an IOU.");
                return null;
            });
            return null;
        }));
    }

    @Test
    public void transactionMustHaveOneOutput() {
        ledger(ledgerServices, (ledger -> {
            ledger.transaction(tx -> {
                tx.output(IOUContract.ID, new IOUState(iouValue, gdyniaB.getParty(), gdyniaA.getParty()));
                tx.output(IOUContract.ID, new IOUState(iouValue, gdyniaB.getParty(), gdyniaA.getParty()));
                tx.command(ImmutableList.of(gdyniaA.getPublicKey(), gdyniaB.getPublicKey()), new IOUContract.Commands.Create());
                tx.failsWith("Only one output state should be created.");
                return null;
            });
            return null;
        }));
    }

    @Test
    public void lenderMustSignTransaction() {
        ledger(ledgerServices, (ledger -> {
            ledger.transaction(tx -> {
                tx.output(IOUContract.ID, new IOUState(iouValue, gdyniaB.getParty(), gdyniaA.getParty()));
                tx.command(gdyniaB.getPublicKey(), new IOUContract.Commands.Create());
                tx.failsWith("All of the participants must be signers.");
                return null;
            });
            return null;
        }));
    }

    @Test
    public void borrowerMustSignTransaction() {
        ledger(ledgerServices, (ledger -> {
            ledger.transaction(tx -> {
                tx.output(IOUContract.ID, new IOUState(iouValue, gdyniaB.getParty(), gdyniaA.getParty()));
                tx.command(gdyniaA.getPublicKey(), new IOUContract.Commands.Create());
                tx.failsWith("All of the participants must be signers.");
                return null;
            });
            return null;
        }));
    }

    @Test
    public void lenderIsNotBorrower() {
        final TestIdentity testIdentity = new TestIdentity(gdyniaA.getName(), gdyniaA.getKeyPair());
        ledger(ledgerServices, (ledger -> {
            ledger.transaction(tx -> {
                tx.output(IOUContract.ID, new IOUState(iouValue, gdyniaB.getParty(), testIdentity.getParty()));
                tx.command(ImmutableList.of(gdyniaA.getPublicKey(), gdyniaB.getPublicKey()), new IOUContract.Commands.Create());
                tx.failsWith("The lender and the borrower cannot be the same entity.");
                return null;
            });
            return null;
        }));
    }

    @Test
    public void cannotCreateNegativeValueIOUs() {
        ledger(ledgerServices, (ledger -> {
            ledger.transaction(tx -> {
                tx.output(IOUContract.ID, new IOUState(-1, gdyniaB.getParty(), gdyniaA.getParty()));
                tx.command(ImmutableList.of(gdyniaA.getPublicKey(), gdyniaB.getPublicKey()), new IOUContract.Commands.Create());
                tx.failsWith("The IOU's value must be non-negative.");
                return null;
            });
            return null;
        }));
    }
}
