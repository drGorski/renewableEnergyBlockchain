package pl.gdynia.amw;

import com.google.common.collect.ImmutableList;
import net.corda.client.rpc.CordaRPCClient;
import net.corda.core.concurrent.CordaFuture;
import net.corda.core.contracts.Amount;
import net.corda.core.contracts.Issued;
import net.corda.core.contracts.Structures;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.node.services.Vault;
import net.corda.core.utilities.OpaqueBytes;
import net.corda.finance.contracts.asset.Cash;
import net.corda.testing.core.TestIdentity;
import net.corda.testing.driver.DriverParameters;
import net.corda.testing.driver.NodeHandle;
import net.corda.testing.driver.NodeParameters;
import net.corda.testing.node.User;
import org.apache.commons.lang3.math.NumberUtils;
import org.junit.Test;
import pl.gdynia.amw.flows.IOUFlow;
import rx.Observable;

import java.util.Currency;
import java.util.HashSet;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static net.corda.finance.Currencies.DOLLARS;
import static net.corda.node.services.Permissions.invokeRpc;
import static net.corda.node.services.Permissions.startFlow;
import static net.corda.testing.core.ExpectKt.expect;
import static net.corda.testing.core.ExpectKt.expectEvents;
import static net.corda.testing.driver.Driver.driver;
import static org.junit.Assert.assertEquals;

public class DriverBasedTest {

    private final TestIdentity nodeA = new TestIdentity(new CordaX500Name("TestA", "Gdynia", "PL"));
    private final TestIdentity nodeB = new TestIdentity(new CordaX500Name("TestB", "Gdynia", "PL"));

    @Test
    public void nodesSellAndBuyEnergy() {
        // START 1
        driver(new DriverParameters()
                .withStartNodesInProcess(true)
                .withExtraCordappPackagesToScan(ImmutableList.of("pl.gdynia.amw.contracts", "pl.gdynia.amw.workflows")), dsl -> {

            User testUser1 = new User("testUser1", "testPassword1", new HashSet<>(asList(
                    startFlow(IOUFlow.Initiator.class),
                    invokeRpc("vaultTrack")
            )));

            User testUser2 = new User("testUser2", "testPassword2", new HashSet<>(asList(
                    startFlow(IOUFlow.Initiator.class),
                    invokeRpc("vaultTrack")
            )));

            try {
                List<CordaFuture<NodeHandle>> nodeHandleFutures = asList(
                        dsl.startNode(new NodeParameters().withProvidedName(nodeA.getName()).withRpcUsers(singletonList(testUser1))),
                        dsl.startNode(new NodeParameters().withProvidedName(nodeB.getName()).withRpcUsers(singletonList(testUser2)))
                );

                NodeHandle test1 = nodeHandleFutures.get(0).get();
                NodeHandle test2 = nodeHandleFutures.get(1).get();
                // END 1

                // START 2
                /*CordaRPCClient test1Client = new CordaRPCClient(test1.getRpcAddress());
                CordaRPCOps test1Proxy = test1Client.start("testUser1", "testPassword1").getProxy();

                CordaRPCClient test2Client = new CordaRPCClient(test2.getRpcAddress());
                CordaRPCOps test2Proxy = test2Client.start("testUser2", "testPassword2").getProxy();
                // END 2

                // START 3
                Observable<Vault.Update<Cash.State>> bobVaultUpdates = test2Proxy.vaultTrack(Cash.State.class).getUpdates();
                Observable<Vault.Update<Cash.State>> aliceVaultUpdates = test1Proxy.vaultTrack(Cash.State.class).getUpdates();
                // END 3

                // START 4
                OpaqueBytes issueRef = OpaqueBytes.of((byte)0);
                test1Proxy.startFlowDynamic(
                        IOUFlow.Initiator.class,
                        NumberUtils.INTEGER_ONE,
                        issueRef,
                        test2.getNodeInfo().getLegalIdentities().get(0),
                        true,
                        dsl.getDefaultNotaryIdentity()
                ).getReturnValue().get();

                @SuppressWarnings("unchecked")
                Class<Vault.Update<Cash.State>> cashVaultUpdateClass = (Class<Vault.Update<Cash.State>>)(Class<?>)Vault.Update.class;

                expectEvents(bobVaultUpdates, true, () ->
                        expect(cashVaultUpdateClass, update -> true, update -> {
                            System.out.println("Bob got vault update of " + update);
                            Amount<Issued<Currency>> amount = update.getProduced().iterator().next().getState().getData().getAmount();
                            assertEquals(DOLLARS(1000), Structures.withoutIssuer(amount));
                            return null;
                        })
                );
                // END 4

                // START 5
                test2Proxy.startFlowDynamic(
                        IOUFlow.Initiator.class,
                        NumberUtils.INTEGER_ONE,
                        test1.getNodeInfo().getLegalIdentities().get(0)
                ).getReturnValue().get();

                expectEvents(aliceVaultUpdates, true, () ->
                        expect(cashVaultUpdateClass, update -> true, update -> {
                            System.out.println("Alice got vault update of " + update);
                            Amount<Issued<Currency>> amount = update.getProduced().iterator().next().getState().getData().getAmount();
                            assertEquals(DOLLARS(1000), Structures.withoutIssuer(amount));
                            return null;
                        })
                );*/
                // END 5
            } catch (Exception e) {
                throw new RuntimeException("Exception thrown in driver DSL", e);
            }
            return null;
        });
    }

}
