package care.solve.backend;

import care.solve.backend.entity.SampleStore;
import care.solve.backend.entity.SampleUser;
import com.google.common.collect.ImmutableSet;
import org.hyperledger.fabric.sdk.BlockEvent;
import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.ChannelConfiguration;
import org.hyperledger.fabric.sdk.EventHub;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.InstallProposalRequest;
import org.hyperledger.fabric.sdk.InstantiateProposalRequest;
import org.hyperledger.fabric.sdk.Orderer;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.QueryByChaincodeRequest;
import org.hyperledger.fabric.sdk.TransactionProposalRequest;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric.sdk.exception.ChaincodeEndorsementPolicyParseException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.exception.TransactionException;
import org.hyperledger.fabric.sdk.security.CryptoSuite;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static java.nio.charset.StandardCharsets.UTF_8;

public class HealthClient {

    private static final String CHAINCODE_VERSION  = "2";

//    public static void main(String[] args) throws Exception {
//        SampleStore sampleStore = createSampleStore();
//        SampleUser humanAdminUser = createSampleUser(sampleStore);
//
//        HFClient client = HFClient.createNewInstance();
//        client.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
//        client.setUserContext(humanAdminUser);
//
//        Peer peer = constructPeer(client);
//        Orderer orderer = constructOrderer(client);
//        EventHub eventHub = constructEventHub(client);
//
//        ChaincodeID chaincodeId = getChaincodeId();
//
//// *****************************************************************************************************
////        Channel healthChannel = connectToChannel(client, orderer, peer, eventHub);
//
//        Channel healthChannel = constructChannel(client, humanAdminUser,  peer, orderer, eventHub);
//        installChaincode(client, chaincodeId, peer);
//        instantiateChaincode(client, chaincodeId, healthChannel, orderer, peer)
//                .get(20000L, TimeUnit.SECONDS);
//
//        sendInvokeTransaction(client, chaincodeId, healthChannel, peer, "createPatient", new String[]{"111", "patient@email.com", "Bob", "B", "1000"});
//        sendInvokeTransaction(client, chaincodeId, healthChannel, peer, "createDoctor", new String[]{"222", "doc@email.com", "Xen", "X", "senior"});
//
//        System.out.println("==========================================================================================================");
//
//        sendQueryTransaction(client, chaincodeId, healthChannel, "getDoctorsSchedule", new String[]{"222"});
//
//        System.out.println("==========================================================================================================");
//
//
//        sendInvokeTransaction(client, chaincodeId, healthChannel, peer, "registerToDoctor", new String[]{"111", "222", "1504709558", "1504709658", "ololo"});
//
//        System.out.println("==========================================================================================================");
//
//        sendQueryTransaction(client, chaincodeId, healthChannel, "getDoctorsSchedule", new String[]{"222"});
//
//        System.out.println("==========================================================================================================");
//    }
}
