package net.corda.samples.flows;

import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.samples.contracts.AssetContract;
import net.corda.samples.states.Asset;

import java.util.Arrays;
import java.util.Collections;


/**
 * This flow is used to build a transaction to issue an asset on the Corda Ledger, which can later be put on auction.
 * It creates a self issues transaction, the state is only issued on the ledger of the party who executes the flow.
 */
@InitiatingFlow
@StartableByRPC
public class CreateAssetFlow extends FlowLogic<SignedTransaction> {

    private final String title;
    private final String description;
    private final String imageURL;

    /**
     * Constructor to initialise flow parameters received from rpc.
     *
     * @param title of the asset to be issued on ledger
     * @param description of the asset to be issued in ledger
     * @param imageURL is a url of an image of the asset
     */
    public CreateAssetFlow(String title, String description, String imageURL) {
        this.title = title;
        this.description = description;
        this.imageURL = imageURL;
    }


    @Override
    @Suspendable
    public SignedTransaction call() throws FlowException {
        // Obtain a reference to a notary we wish to use.
        /** METHOD 1: Take first notary on network, WARNING: use for test, non-prod environments, and single-notary networks only!*
         *  METHOD 2: Explicit selection of notary by CordaX500Name - argument can by coded in flow or parsed from config (Preferred)
         *
         *  * - For production you always want to use Method 2 as it guarantees the expected notary is returned.
         */
        final Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0); // METHOD 1
        // final Party notary = getServiceHub().getNetworkMapCache().getNotary(CordaX500Name.parse("O=Notary,L=London,C=GB")); // METHOD 2

        // Create the output state
        Asset output = new Asset(new UniqueIdentifier(), title, description, imageURL,
                getOurIdentity());

        // Build the transaction, add the output state and the command to the transaction.
        TransactionBuilder transactionBuilder = new TransactionBuilder(notary)
                .addOutputState(output)
                .addCommand(new AssetContract.Commands.CreateAsset(),
                        Arrays.asList(getOurIdentity().getOwningKey())); // Required Signers

        // Verify the transaction
        transactionBuilder.verify(getServiceHub());

        // Sign the transaction
        SignedTransaction signedTransaction = getServiceHub().signInitialTransaction(transactionBuilder);

        // Notarise the transaction and record the state in the ledger.
        return subFlow(new FinalityFlow(signedTransaction, Collections.emptyList()));
    }
}