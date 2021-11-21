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
import net.corda.core.transactions.SignedTransaction;
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
import pl.gdynia.amw.states.IOUState;
import rx.Observable;

import java.util.HashSet;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
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
                CordaRPCClient test1Client = new CordaRPCClient(test1.getRpcAddress());
                CordaRPCOps test1Proxy = test1Client.start("testUser1", "testPassword1").getProxy();

                CordaRPCClient test2Client = new CordaRPCClient(test2.getRpcAddress());
                CordaRPCOps test2Proxy = test2Client.start("testUser2", "testPassword2").getProxy();
                // END 2

                // START 3
                Observable<Vault.Update<IOUState>> test2Updates = test2Proxy.vaultTrack(IOUState.class).getUpdates();
                Observable<Vault.Update<IOUState>> test1Updates = test1Proxy.vaultTrack(IOUState.class).getUpdates();
                // END 3

                // START 4
                SignedTransaction signedTransaction = test1Proxy.startFlowDynamic(
                        IOUFlow.Initiator.class,
                        NumberUtils.INTEGER_ONE,
                        test2.getNodeInfo().getLegalIdentities().get(0)
                ).getReturnValue().get();

                @SuppressWarnings("unchecked")
                Class<Vault.Update<IOUState>> valueVaultUpdateClass = (Class<Vault.Update<IOUState>>)(Class<?>)Vault.Update.class;

                expectEvents(test2Updates, true, () ->
                        expect(valueVaultUpdateClass, update -> true, update -> {
                            System.out.println("Test2 got vault update of " + update);
                            Integer value = update.getProduced().iterator().next().getState().getData().getValue();
                            assertEquals(NumberUtils.INTEGER_ONE, value);
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

                expectEvents(test1Updates, true, () ->
                        expect(valueVaultUpdateClass, update -> true, update -> {
                            System.out.println("Test1 got vault update of " + update);
                            Integer value = update.getProduced().iterator().next().getState().getData().getValue();
                            assertEquals(NumberUtils.INTEGER_ONE, value);
                            return null;
                        })
                );
                // END 5
            } catch (Exception e) {
                throw new RuntimeException("Exception thrown in driver DSL", e);
            }
            return null;
        });
    }

}
