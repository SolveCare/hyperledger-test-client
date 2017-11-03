package care.solve.backend.config;

import care.solve.backend.service.ChannelService;
import care.solve.backend.service.TransactionService;
import com.google.common.collect.ImmutableSet;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.ChaincodeEndorsementPolicyParseException;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.exception.TransactionException;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Properties;

@Configuration
public class HyperLedgerConfig {

    @Value("${peer0.name}")
    private String peer0Name;

    @Value("${peer0.grpcUrl}")
    private String peer0GrpcUrl;

    @Value("${peer0.tls.cert.file}")
    private String peer0TLSCertFile;

    @Value("${peer1.name}")
    private String peer1Name;

    @Value("${peer1.grpcUrl}")
    private String peer1GrpcUrl;

    @Value("${peer1.tls.cert.file}")
    private String peer1TLSCertFile;

    @Value("${peer2.name}")
    private String peer2Name;

    @Value("${peer2.grpcUrl}")
    private String peer2GrpcUrl;

    @Value("${peer2.tls.cert.file}")
    private String peer2TLSCertFile;

    @Value("${admin.eventHub.grpcUrl}")
    private String customEventHubGrpcUrl;

    @Value("${orderer.name}")
    private String ordererName;

    @Value("${orderer.grpcUrl}")
    private String ordererGrpcUrl;

    @Value("${channel.health.name}")
    private String healthChannelName;

    @Value("${ca.admin.url}")
    private String caAdminUrl;

    @Value("${orderer.tls.cert.file}")
    private String ordererTLSCertFile;

    @Bean(name = "peerAdminHFClient")
    @Autowired
    public HFClient getPeerAdminHFClient(@Qualifier("peerAdminUser") User user) throws CryptoException, InvalidArgumentException {
        HFClient client = HFClient.createNewInstance();
        client.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
        client.setUserContext(user);

        return client;
    }

    @Bean(name = "sampleUserHFClient")
    @Autowired
    public HFClient getSampleUserHFClient(@Qualifier("sampleUser") User user) throws CryptoException, InvalidArgumentException {
        HFClient client = HFClient.createNewInstance();
        client.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
        client.setUserContext(user);

        return client;
    }

    @Bean(name = "hfcaAdminClient")
    public HFCAClient getHFCAAdminClient() throws MalformedURLException {
        HFCAClient hfcaAdminClient = HFCAClient.createNewInstance(caAdminUrl, null);
        hfcaAdminClient.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());

        return hfcaAdminClient;
    }

    @Bean(name = "peer0")
    public Peer constructPeer0(HFClient peerAdminHFClient) throws InvalidArgumentException, IOException {
        return peerAdminHFClient.newPeer(
                peer0Name,
                peer0GrpcUrl,
                getPeerProperties(peer0Name, peer0TLSCertFile)
        );
    }

    @Bean(name = "peer1")
    public Peer constructPeer1(HFClient peerAdminHFClient) throws InvalidArgumentException, IOException {
        return peerAdminHFClient.newPeer(
                peer1Name,
                peer1GrpcUrl,
                getPeerProperties(peer1Name, peer1TLSCertFile)
        );
    }

    @Bean(name = "peer2")
    public Peer constructPeer2(HFClient sampleUserHFClient) throws InvalidArgumentException, IOException {
        return sampleUserHFClient.newPeer(
                peer2Name,
                peer2GrpcUrl,
                getPeerProperties(peer2Name, peer2TLSCertFile)
        );
    }

    @Bean(name = "orderer")
    public Orderer constructOrderer(HFClient peerAdminHFClient) throws InvalidArgumentException, IOException {
        return peerAdminHFClient.newOrderer(
                ordererName,
                ordererGrpcUrl,
                getOrderedProperties()
        );
    }

    @Bean(name = "customEventHub")
    public EventHub constructEventHub(HFClient peerAdminHFClient) throws InvalidArgumentException, IOException {
        return peerAdminHFClient.newEventHub(
                peer0Name,
                customEventHubGrpcUrl,
                getPeerProperties(peer0Name, peer0TLSCertFile)
        );
    }

    public Properties getPeerProperties(final String peerName, final String peerTLSCertFile) throws IOException {
        URL resource = HyperLedgerConfig.class.getResource(peerTLSCertFile);

        Properties properties = new Properties();
        properties.setProperty("pemFile", resource.toString());
        properties.setProperty("hostnameOverride", peerName);
        properties.setProperty("sslProvider", "openSSL");
        properties.setProperty("negotiationType", "TLS");

        return properties;
    }

    public Properties getOrderedProperties() throws IOException {
        URL resource = HyperLedgerConfig.class.getResource(ordererTLSCertFile);

        Properties properties = new Properties();
        properties.setProperty("pemFile", resource.toString());
        properties.setProperty("hostnameOverride", ordererName);
        properties.setProperty("sslProvider", "openSSL");
        properties.setProperty("negotiationType", "TLS");

        properties.setProperty("ordererWaitTimeMilliSecs", "20000");

        return properties;
    }

    @Bean(name = "chaincodeId")
    public ChaincodeID getChaincodeId() {
        final String CHAIN_CODE_NAME = "scheduleChaincode_go";
        final String CHAIN_CODE_PATH = "care.solve.schedule";
        final String CHAIN_CODE_VERSION = "3";

        return ChaincodeID.newBuilder().setName(CHAIN_CODE_NAME)
                .setVersion(CHAIN_CODE_VERSION)
                .setPath(CHAIN_CODE_PATH).build();
    }

    @Bean(name = "healthChannel")
    public Channel healthChannel(
            ChannelService channelService,
            @Qualifier("peerAdminUser") User peerAdminUser,
            @Qualifier("peerAdminHFClient") HFClient client,
            @Qualifier("peer0") Peer peer0,
            @Qualifier("peer1") Peer peer1,
            @Qualifier("peer2") Peer peer2,
            @Qualifier("orderer") Orderer orderer,
            @Qualifier("customEventHub") EventHub eventHub) throws InvalidArgumentException, TransactionException, ProposalException, IOException {

        Channel channel;
        if (channelService.isChannelExists(healthChannelName, peer0, client)) {
            channel = channelService.connectToChannel(healthChannelName, client, orderer, eventHub);
        } else {
            channel = channelService.constructChannel(healthChannelName, client, peerAdminUser, ImmutableSet.of(peer0, peer1, peer2), orderer, eventHub);
        }

        return channel;
    }

    @Bean(name = "chaincodeEndorsementPolicy")
    public ChaincodeEndorsementPolicy getChaincodeEndorsementPolicy () {
        ChaincodeEndorsementPolicy chaincodeEndorsementPolicy = new ChaincodeEndorsementPolicy();
        File file = new File("/config/endorsementPolicy.yaml");

        try {
            chaincodeEndorsementPolicy.fromYamlFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ChaincodeEndorsementPolicyParseException e) {
            e.printStackTrace();
        }

        return chaincodeEndorsementPolicy;
    }
}
