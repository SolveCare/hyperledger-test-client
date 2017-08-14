package com.example;

import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.ChaincodeResponse;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.EventHub;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.Orderer;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.QueryByChaincodeRequest;
import org.hyperledger.fabric.sdk.TransactionProposalRequest;
import org.hyperledger.fabric.sdk.exception.CryptoException;
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
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CompletionException;

import static java.lang.String.format;

public class App {

    private static final String CHAIN_CODE_NAME = "example_cc_go";
    private static final String CHAIN_CODE_PATH = "github.com/example_cc";
    private static final String CHAIN_CODE_VERSION = "1";
    private static final String CHAIN_CODE_VERSION_11 = "11";

    public static Properties getOrderedProperties() {
        String cert = "/Users/nikita/ukrsoft/mychaincode/hyperledger-test-client/src/main/resources/e2e-2Orgs/channel/crypto-config/ordererOrganizations/example.com/orderers/orderer.example.com/tls/server.crt";
        Properties ret = new Properties();
        ret.setProperty("pemFile", cert);
        //      ret.setProperty("trustServerCertificate", "true"); //testing environment only NOT FOR PRODUCTION!
        ret.setProperty("hostnameOverride", "orderer.example.com");
        ret.setProperty("sslProvider", "openSSL");
        ret.setProperty("negotiationType", "TLS");

        return ret;
    }

    public static Properties getPeerProperties() {
        String cert = "/Users/nikita/ukrsoft/mychaincode/hyperledger-test-client/src/main/resources/e2e-2Orgs/channel/crypto-config/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/server.crt";
        Properties ret = new Properties();
        ret.setProperty("pemFile", cert);
        //      ret.setProperty("trustServerCertificate", "true"); //testing environment only NOT FOR PRODUCTION!
        ret.setProperty("hostnameOverride", "peer0.org1.example.com");
        ret.setProperty("sslProvider", "openSSL");
        ret.setProperty("negotiationType", "TLS");

        return ret;
    }

    public static void main( String[] args ) throws CryptoException, InvalidArgumentException, ProposalException, IOException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException, TransactionException, InterruptedException {
        final ChaincodeID chaincodeID_11 = ChaincodeID.newBuilder().setName(CHAIN_CODE_NAME)
                .setVersion(CHAIN_CODE_VERSION_11)
                .setPath(CHAIN_CODE_PATH).build();

        File tempFile = File.createTempFile("teststore", "properties");
        tempFile.deleteOnExit();

        File sampleStoreFile = new File(System.getProperty("user.home") + "/test.properties");
        if (sampleStoreFile.exists()) { //For testing start fresh
//            sampleStoreFile.delete();
        } else {
            sampleStoreFile.createNewFile();
        }

        final SampleStore sampleStore = new SampleStore(sampleStoreFile);

        SampleUser someTestUSER = sampleStore.getMember("someTestUSER", "someTestORG", "mspid",
                findFileSk("src/main/resources/e2e-2Orgs/channel/crypto-config/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp/keystore"),
                new File("src/main/resources/e2e-2Orgs/channel/crypto-config/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp/signcerts/Admin@org1.example.com-cert.pem"));
        someTestUSER.setMspId("Org1MSP");

        HFClient client = HFClient.createNewInstance();
        client.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());

        client.setUserContext(someTestUSER);

        Channel channel = client.newChannel("foo");

        Orderer orderer = client.newOrderer("orderer.example.com", "grpc://127.0.0.1:7050", getOrderedProperties());
        channel.addOrderer(orderer);

        Peer peer = client.newPeer("peer0.org1.example.com", "grpc://localhost:7051", getPeerProperties());
        Set<String> channels = client.queryChannels(peer);
        if (!channels.contains("foo")) {
            throw new AssertionError(format("Peer %s does not appear to belong to channel %s", "peer0.org1.example.com", "foo"));
        }
        channel.addPeer(peer);

        EventHub eventHub = client.newEventHub("peer0.org1.example.com", "grpc://localhost:7053", getPeerProperties());
        channel.addEventHub(eventHub);

        channel.initialize();

        TransactionProposalRequest transactionProposalRequest = client.newTransactionProposalRequest();
        transactionProposalRequest.setChaincodeID(chaincodeID_11);
        transactionProposalRequest.setFcn("invoke");
        transactionProposalRequest.setArgs(new String[] {"move", "a", "b", "100"});
        transactionProposalRequest.setProposalWaitTime(9000);

        Collection<ProposalResponse> invokePropResp = channel.sendTransactionProposal(transactionProposalRequest);
        for (ProposalResponse response : invokePropResp) {
            if (response.getStatus() == ChaincodeResponse.Status.SUCCESS) {
                System.out.println(String.format("Successful transaction proposal response Txid: %s from peer %s", response.getTransactionID(), response.getPeer().getName()));
            } else {
                System.out.println(response);
            }
        }

        channel.sendTransaction(invokePropResp);

//        Thread.sleep(5000L);

        QueryByChaincodeRequest queryByChaincodeRequest = client.newQueryProposalRequest();
        queryByChaincodeRequest.setArgs(new String[] {"query", "b"});
        queryByChaincodeRequest.setFcn("invoke");
        queryByChaincodeRequest.setChaincodeID(chaincodeID_11);

        Collection<ProposalResponse> queryProposals;

        try {
            queryProposals = channel.queryByChaincode(queryByChaincodeRequest);
        } catch (Exception e) {
            throw new CompletionException(e);
        }

        for (ProposalResponse proposalResponse : queryProposals) {
            if (!proposalResponse.isVerified() || proposalResponse.getStatus() != ChaincodeResponse.Status.SUCCESS) {
                System.out.println("Failed query proposal from peer " + proposalResponse.getPeer().getName() + " status: " + proposalResponse.getStatus() +
                        ". Messages: " + proposalResponse.getMessage()
                        + ". Was verified : " + proposalResponse.isVerified());
            } else {
                String payload = proposalResponse.getProposalResponse().getResponse().getPayload().toStringUtf8();
                System.out.println(String.format("Query payload of b from peer %s returned %s", proposalResponse.getPeer().getName(), payload));
            }
        }
    }

    static File findFileSk(String directorys) {

        File directory = new File(directorys);

        File[] matches = directory.listFiles((dir, name) -> name.endsWith("_sk"));

        if (null == matches) {
            throw new RuntimeException(format("Matches returned null does %s directory exist?", directory.getAbsoluteFile().getName()));
        }

        if (matches.length != 1) {
            throw new RuntimeException(format("Expected in %s only 1 sk file but found %d", directory.getAbsoluteFile().getName(), matches.length));
        }

        return matches[0];

    }
}
