package care.solve.backend.config;

import care.solve.backend.service.ChannelService;
import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.EventHub;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.Orderer;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.exception.TransactionException;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

@Configuration
public class HyperLedgerConfig {

    @Value("${user.human.peer.name}")
    private String humanPeerName;

    @Value("${user.human.peer.grpcUrl}")
    private String humanPeerGrpcUrl;

    @Value("${user.human.eventHub.grpcUrl}")
    private String humanEventHubGrpcUrl;

    @Value("${orderer.name}")
    private String ordererName;

    @Value("${orderer.grpcUrl}")
    private String ordererGrpcUrl;

    @Bean(name = "hfClient")
    @Autowired
    public HFClient getHFClient(@Qualifier("humanAdminUser") User user) throws CryptoException, InvalidArgumentException {
        HFClient client = HFClient.createNewInstance();
        client.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
        client.setUserContext(user);

        return client;
    }

    @Bean(name = "humanPeer")
    public Peer constructPeer(HFClient client) throws InvalidArgumentException, IOException {
        return client.newPeer(
                humanPeerName,
                humanPeerGrpcUrl,
                getPeerProperties()
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

    @Bean(name = "humanEventHub")
    public EventHub constructEventHub(HFClient client) throws InvalidArgumentException, IOException {
        return client.newEventHub(
                humanPeerName,
                humanEventHubGrpcUrl,
                getPeerProperties()
        );
    }

    public Properties getPeerProperties() throws IOException {
        URL resource = HyperLedgerConfig.class.getResource("/health/crypto-config/peerOrganizations/human.carewallet.com/peers/peer0.human.carewallet.com/tls/server.crt");

        Properties ret = new Properties();
        ret.setProperty("pemFile", resource.toString());
        ret.setProperty("hostnameOverride", humanPeerName);
        ret.setProperty("sslProvider", "openSSL");
        ret.setProperty("negotiationType", "TLS");

        return ret;
    }

    public Properties getOrderedProperties() throws IOException {
        URL resource = HyperLedgerConfig.class.getResource("/health/crypto-config/ordererOrganizations/carewallet.com/orderers/orderer.carewallet.com/tls/server.crt");

        Properties ret = new Properties();
        ret.setProperty("pemFile", resource.toString());
        ret.setProperty("hostnameOverride", ordererName);
        ret.setProperty("sslProvider", "openSSL");
        ret.setProperty("negotiationType", "TLS");

        System.setProperty("orderer.time", "20000");
        ret.setProperty("ordererWaitTimeMilliSecs", "orderer.time");

        return ret;
    }

    @Bean(name = "chaincodeId")
    public ChaincodeID getChaincodeId() {
        final String CHAIN_CODE_NAME = "scheduleChaincode_go";
        final String CHAIN_CODE_PATH = "care.solve.schedule";
        final String CHAIN_CODE_VERSION = "2";

        return ChaincodeID.newBuilder().setName(CHAIN_CODE_NAME)
                .setVersion(CHAIN_CODE_VERSION)
                .setPath(CHAIN_CODE_PATH).build();
    }

    @Bean(name = "healthChannel")
    public Channel healthChannel(
            ChannelService channelService,
            @Qualifier("humanAdminUser") User humanAdminUser,
            @Qualifier("hfClient") HFClient client,
            @Qualifier("humanPeer") Peer peer,
            @Qualifier("orderer") Orderer orderer,
            @Qualifier("humanEventHub") EventHub eventHub) throws InvalidArgumentException, TransactionException, ProposalException, IOException {

        Channel channel = channelService.constructChannel(client, humanAdminUser, peer, orderer, eventHub);
//        Channel channel = channelService.connectToChannel(client, orderer, peer, eventHub);

        return channel;
    }
}
