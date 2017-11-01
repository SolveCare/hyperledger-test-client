package care.solve.backend.config;

import care.solve.backend.service.ChannelService;
import org.hyperledger.fabric.sdk.*;
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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

@Configuration
public class HyperLedgerConfig {

    @Value("${admin.primaryPeer.name}")
    private String customPeerName;

    @Value("${admin.primaryPeer.grpcUrl}")
    private String customPeerGrpcUrl;

    @Value("${secondary.peer0.name}")
    private String secondaryPeer0Name;

    @Value("${secondary.peer0.grpcUrl}")
    private String secondaryPeer0GrpcUrl;

    @Value("${secondary.peer1.name}")
    private String secondaryPeer1Name;

    @Value("${secondary.peer1.grpcUrl}")
    private String secondaryPeer1GrpcUrl;

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

    @Value("${primaryPeer.tls.cert.file}")
    private String peerTLSCertFile;

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

    @Bean(name = "hfcaAdminClient")
    public HFCAClient getHFCAAdminClient() throws MalformedURLException {
        HFCAClient hfcaAdminClient = HFCAClient.createNewInstance(caAdminUrl, null);
        hfcaAdminClient.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());

        return hfcaAdminClient;
    }

    @Bean(name = "primaryPeer")
    public Peer constructPeer(HFClient client) throws InvalidArgumentException, IOException {
        return client.newPeer(
                customPeerName,
                customPeerGrpcUrl,
                getPeerProperties()
        );
    }

    @Bean(name = "secondaryPeer0")
    public Peer constructSecondaryPeer0(HFClient client) throws InvalidArgumentException, IOException {
        return client.newPeer(
                secondaryPeer0Name,
                secondaryPeer0GrpcUrl
        );
    }

    @Bean(name = "secondaryPeer1")
    public Peer constructSecondaryPeer1(HFClient client) throws InvalidArgumentException, IOException {
        return client.newPeer(
                secondaryPeer1Name,
                secondaryPeer1GrpcUrl
        );
    }

    @Bean(name = "orderer")
    public Orderer constructOrderer(HFClient client) throws InvalidArgumentException, IOException {
        return client.newOrderer(
                ordererName,
                ordererGrpcUrl,
                getOrderedProperties()
        );
    }

    @Bean(name = "customEventHub")
    public EventHub constructEventHub(HFClient client) throws InvalidArgumentException, IOException {
        return client.newEventHub(
                customPeerName,
                customEventHubGrpcUrl,
                getPeerProperties()
        );
    }

    public Properties getPeerProperties() throws IOException {
        URL resource = HyperLedgerConfig.class.getResource(peerTLSCertFile);

        Properties properties = new Properties();
        properties.setProperty("pemFile", resource.toString());
        properties.setProperty("hostnameOverride", customPeerName);
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
            @Qualifier("primaryPeer") Peer peer,
            @Qualifier("orderer") Orderer orderer,
            @Qualifier("customEventHub") EventHub eventHub) throws InvalidArgumentException, TransactionException, ProposalException, IOException {

        Channel channel;
        if (channelService.isChannelExists(healthChannelName, peer, client)) {
            channel = channelService.connectToChannel(healthChannelName, client, orderer, peer, eventHub);
        } else {
            channel = channelService.constructChannel(healthChannelName, client, peerAdminUser, peer, orderer, eventHub);
        }

        return channel;
    }
}
