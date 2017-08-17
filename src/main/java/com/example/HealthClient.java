package com.example;

import com.google.common.collect.ImmutableSet;
import org.hyperledger.fabric.sdk.BlockEvent;
import org.hyperledger.fabric.sdk.ChaincodeEndorsementPolicy;
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
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.exception.TransactionException;
import org.hyperledger.fabric.sdk.security.CryptoSuite;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static java.nio.charset.StandardCharsets.UTF_8;

public class HealthClient {

    public static void main(String[] args) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException, NoSuchProviderException, CryptoException, InvalidArgumentException, TransactionException, ProposalException, ChaincodeEndorsementPolicyParseException, InterruptedException, ExecutionException, TimeoutException {
        SampleStore sampleStore = createSampleStore();
        SampleUser humanAdminUser = createSampleUser(sampleStore);

        HFClient client = HFClient.createNewInstance();
        client.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
        client.setUserContext(humanAdminUser);

        Peer peer = constructPeer(client);
        Orderer orderer = constructOrderer(client);
        EventHub eventHub = constructEventHub(client);

        ChaincodeID chaincodeId = getChaincodeId();

// *****************************************************************************************************
//        Channel healthChannel = connectToChannel(client, orderer, peer, eventHub);
//        sendInvokeTransaction(client, chaincodeId, healthChannel, peer);
//        sendQueryTransaction(client, chaincodeId, healthChannel);
// *****************************************************************************************************

// *****************************************************************************************************
        Channel healthChannel = constructChannel(client, humanAdminUser,  peer, orderer, eventHub);
        installChaincode(client, chaincodeId, peer);
        instantiateChaincode(client, chaincodeId, healthChannel, orderer, peer)
                .thenApply(transactionEvent -> sendInvokeTransaction(client, chaincodeId, healthChannel, peer, "invoke", new String[]{"createUser", "user1", "1000"}))
                .thenApply(transactionEvent -> sendInvokeTransaction(client, chaincodeId, healthChannel, peer, "invoke", new String[]{"createUser", "medic", "300"}))
                .thenApply(transactionEvent -> sendInvokeTransaction(client, chaincodeId, healthChannel, peer, "invoke", new String[]{"createUser", "insurance", "5000"}))
                .thenApply(transactionEvent -> sendQueryTransaction(client, chaincodeId, healthChannel, "invoke", new String[]{"query", "user1"}))
                .thenApply(transactionEvent -> sendQueryTransaction(client, chaincodeId, healthChannel, "invoke", new String[]{"query", "medic"}))
                .thenApply(transactionEvent -> sendQueryTransaction(client, chaincodeId, healthChannel, "invoke", new String[]{"query", "insurance"}))
                .get(12000L, TimeUnit.SECONDS);
// *****************************************************************************************************

    }

    public static Object sendInvokeTransaction(HFClient client, ChaincodeID chaincodeId, Channel healthChannel, Peer peer, String func, String[] args) {
        try {
            TransactionProposalRequest transactionProposalRequest = client.newTransactionProposalRequest();
            transactionProposalRequest.setChaincodeID(chaincodeId);
            transactionProposalRequest.setFcn(func);
            transactionProposalRequest.setProposalWaitTime(12000L);
            transactionProposalRequest.setArgs(args);

//            Map<String, byte[]> tm2 = new HashMap<>();
//            tm2.put("HyperLedgerFabric", "TransactionProposalRequest:JavaSDK".getBytes(UTF_8));
//            tm2.put("method", "TransactionProposalRequest".getBytes(UTF_8));
//            tm2.put("result", ":)".getBytes(UTF_8));  /// This should be returned see chaincode.

//            transactionProposalRequest.setTransientMap(tm2);

            Collection<ProposalResponse> transactionPropResp = healthChannel.sendTransactionProposal(transactionProposalRequest, ImmutableSet.of(peer));

//            ProposalResponse resp = transactionPropResp.iterator().next();
//            byte[] x = resp.getChaincodeActionResponsePayload();
//
//            String resultAsString = null;
//            if (x != null) {
//                resultAsString = new String(x, "UTF-8");
//            }
//
//            System.out.println(">>>>>>> RESULT: " + resultAsString);

            return healthChannel.sendTransaction(transactionPropResp);

        } catch (InvalidArgumentException | ProposalException e) {
            throw new RuntimeException(e);
        }

//        return null;
    }

    public static Object sendQueryTransaction(HFClient client, ChaincodeID chaincodeId, Channel healthChannel, String func, String[] args) {
        try {
            QueryByChaincodeRequest queryByChaincodeRequest = client.newQueryProposalRequest();
            queryByChaincodeRequest.setFcn(func);
            queryByChaincodeRequest.setArgs(args);
            queryByChaincodeRequest.setChaincodeID(chaincodeId);

            Map<String, byte[]> tm2 = new HashMap<>();
            tm2.put("HyperLedgerFabric", "QueryByChaincodeRequest:JavaSDK".getBytes(UTF_8));
            tm2.put("method", "QueryByChaincodeRequest".getBytes(UTF_8));
            queryByChaincodeRequest.setTransientMap(tm2);

            Collection<ProposalResponse> queryProposals = healthChannel.queryByChaincode(queryByChaincodeRequest, healthChannel.getPeers());
            for (ProposalResponse proposalResponse : queryProposals) {
                if (!proposalResponse.isVerified() || proposalResponse.getStatus() != ProposalResponse.Status.SUCCESS) {
                    System.out.println("Failed query proposal from peer " + proposalResponse.getPeer().getName() + " status: " + proposalResponse.getStatus() +
                            ". Messages: " + proposalResponse.getMessage()
                            + ". Was verified : " + proposalResponse.isVerified());
                } else {
                    String payload = proposalResponse.getProposalResponse().getResponse().getPayload().toStringUtf8();
                    System.out.println(String.format("Query payload of b from peer %s returned %s", proposalResponse.getPeer().getName(), payload));
                }
            }
        } catch (InvalidArgumentException | ProposalException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static CompletableFuture<BlockEvent.TransactionEvent> instantiateChaincode(HFClient client, ChaincodeID chaincodeId, Channel channel, Orderer orderer, Peer peer) throws InvalidArgumentException, IOException, ChaincodeEndorsementPolicyParseException, ProposalException {
        InstantiateProposalRequest instantiateProposalRequest = client.newInstantiationProposalRequest();
        instantiateProposalRequest.setProposalWaitTime(12000L);
        instantiateProposalRequest.setChaincodeID(chaincodeId);
        instantiateProposalRequest.setFcn("init");
        instantiateProposalRequest.setArgs(new String[]{"a", "500", "b", "1000"});

        Map<String, byte[]> tm = new HashMap<>();
        tm.put("HyperLedgerFabric", "InstantiateProposalRequest:JavaSDK".getBytes(UTF_8));
        tm.put("method", "InstantiateProposalRequest".getBytes(UTF_8));
        instantiateProposalRequest.setTransientMap(tm);

//        ChaincodeEndorsementPolicy chaincodeEndorsementPolicy = new ChaincodeEndorsementPolicy();
//        chaincodeEndorsementPolicy.fromYamlFile(new File("src/main/resources/health/chaincodeendorsementpolicy.yaml"));
//        instantiateProposalRequest.setChaincodeEndorsementPolicy(chaincodeEndorsementPolicy);

        Collection<ProposalResponse> proposalResponses = channel.sendInstantiationProposal(instantiateProposalRequest, ImmutableSet.of(peer));

        for (ProposalResponse response : proposalResponses) {
            if (response.isVerified() && response.getStatus() == ProposalResponse.Status.SUCCESS) {
                System.out.println(String.format("Succesful instantiate proposal response Txid: %s from peer %s", response.getTransactionID(), response.getPeer().getName()));
            } else {
                System.out.println(String.format("FAILED instantiate proposal response Txid: %s from peer %s", response.getTransactionID(), response.getPeer().getName()));
            }
        }

        return channel.sendTransaction(proposalResponses, ImmutableSet.of(orderer));
    }

    public static void installChaincode(HFClient client, ChaincodeID chaincodeId, Peer peer) throws InvalidArgumentException, ProposalException, IOException {
        InstallProposalRequest installProposalRequest = client.newInstallProposalRequest();
        installProposalRequest.setChaincodeID(chaincodeId);

        installProposalRequest.setChaincodeSourceLocation(new File("src/main/resources/health/gocc/sample1"));
//        installProposalRequest.setChaincodeInputStream(
//                Util.generateTarGzInputStream(
//                        Paths.get("src/main/resources/health/gocc/sample1").toFile(),
//                        Paths.get("src", "github.com/example_cc").toString()
//                )
//        );

        installProposalRequest.setChaincodeVersion("1");

        Collection<ProposalResponse> proposalResponses = client.sendInstallProposal(installProposalRequest, ImmutableSet.of(peer));

        for (ProposalResponse response : proposalResponses) {
            if (response.getStatus() == ProposalResponse.Status.SUCCESS) {
                System.out.println(String.format("Successful install proposal response Txid: %s from peer %s", response.getTransactionID(), response.getPeer().getName()));
            } else {
                System.out.println(String.format("FAILED to install proposal response Txid: %s from peer %s", response.getTransactionID(), response.getPeer().getName()));
            }
        }
    }

    public static ChaincodeID getChaincodeId() {
        final String CHAIN_CODE_NAME = "health_chaincode_go";
        final String CHAIN_CODE_PATH = "github.com/example_cc";
        final String CHAIN_CODE_VERSION = "1";
//        final String CHAIN_CODE_VERSION_11 = "11";

        return ChaincodeID.newBuilder().setName(CHAIN_CODE_NAME)
                .setVersion(CHAIN_CODE_VERSION)
                .setPath(CHAIN_CODE_PATH).build();
    }

    public static SampleStore createSampleStore() throws IOException {
        File tempFile = File.createTempFile("teststore", "properties");
        tempFile.deleteOnExit();

        File sampleStoreFile = new File(System.getProperty("user.home") + "/test.properties");
        if (sampleStoreFile.exists()) { //For testing start fresh
            sampleStoreFile.delete();
        } else {
            sampleStoreFile.createNewFile();
        }

        return new SampleStore(sampleStoreFile);
    }

    public static SampleUser createSampleUser(SampleStore sampleStore) throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchProviderException, IOException {
        SampleUser someTestUSER = sampleStore.getMember("admin", "human.carewallet.com", "mspid",
                new File("src/main/resources/health/crypto-config/peerOrganizations/human.carewallet.com/users/Admin@human.carewallet.com/msp/keystore/1efe439e2e9a99e8a66c72a8267ff0dbb714fd7c205894f70b333f560bd510ae_sk"),
                new File("src/main/resources/health/crypto-config/peerOrganizations/human.carewallet.com/users/Admin@human.carewallet.com/msp/signcerts/Admin@human.carewallet.com-cert.pem"));
        someTestUSER.setMspId("HumanMSP");

        return someTestUSER;
    }

    public static Peer constructPeer(HFClient client) throws InvalidArgumentException {
        return client.newPeer(
                "peer0.human.carewallet.com",
                "grpc://localhost:8051",
                getPeerProperties()
        );
    }

    public static Orderer constructOrderer(HFClient client) throws InvalidArgumentException {
        return client.newOrderer(
                "orderer.carewallet.com",
                "grpc://127.0.0.1:7050",
                getOrderedProperties()
        );
    }

    public static EventHub constructEventHub(HFClient client) throws InvalidArgumentException {
        return client.newEventHub(
                "peer0.human.carewallet.com",
                "grpc://localhost:8053",
                getPeerProperties()
        );
    }

    public static Channel connectToChannel(HFClient client, Orderer orderer, Peer peer, EventHub eventHub) throws InvalidArgumentException, TransactionException {
        Channel newChannel = client.newChannel("health-channel");
        newChannel.addOrderer(orderer);
        newChannel.addPeer(peer);
        newChannel.addEventHub(eventHub);
        newChannel.initialize();

        return newChannel;
    }

    public static Channel constructChannel(HFClient client, User user, Peer peer, Orderer orderer, EventHub eventHub) throws IOException, InvalidArgumentException, TransactionException, ProposalException {
        ChannelConfiguration channelConfiguration = new ChannelConfiguration(new File("src/main/resources/health/channel-artifacts/health-channel.tx"));

        Channel newChannel = client.newChannel(
                "health-channel",
                orderer,
                channelConfiguration,
                client.getChannelConfigurationSignature(channelConfiguration, user)
        );

        newChannel.addEventHub(eventHub);
//        newChannel.addPeer(peer);
        newChannel.joinPeer(peer);
        newChannel.initialize();

        return newChannel;
    }

    public static Properties getPeerProperties() {
        String cert = "/Users/nikita/ukrsoft/mychaincode/hyperledger-test-client/src/main/resources/health/crypto-config/peerOrganizations/human.carewallet.com/peers/peer0.human.carewallet.com/tls/server.crt";

        Properties ret = new Properties();
        ret.setProperty("pemFile", cert);
        ret.setProperty("hostnameOverride", "peer0.human.carewallet.com");
        ret.setProperty("sslProvider", "openSSL");
        ret.setProperty("negotiationType", "TLS");

        return ret;
    }

    public static Properties getOrderedProperties() {
        String cert = "/Users/nikita/ukrsoft/mychaincode/hyperledger-test-client/src/main/resources/health/crypto-config/ordererOrganizations/carewallet.com/orderers/orderer.carewallet.com/tls/server.crt";

        Properties ret = new Properties();
        ret.setProperty("pemFile", cert);
        ret.setProperty("hostnameOverride", "orderer.carewallet.com");
        ret.setProperty("sslProvider", "openSSL");
        ret.setProperty("negotiationType", "TLS");

        ret.setProperty("ordererWaitTimeMilliSecs", "ordered.wait");

        return ret;
    }
}
