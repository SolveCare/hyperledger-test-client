package care.solve.backend.service;

import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.ChannelConfiguration;
import org.hyperledger.fabric.sdk.EventHub;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.Orderer;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.exception.TransactionException;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class ChannelService {

    public Channel connectToChannel(HFClient client, Orderer orderer, Peer peer, EventHub eventHub) throws InvalidArgumentException, TransactionException {
        Channel newChannel = client.newChannel("health-channel");
        newChannel.addOrderer(orderer);
        newChannel.addPeer(peer);
        newChannel.addEventHub(eventHub);
        newChannel.initialize();

        return newChannel;
    }

    public Channel constructChannel(HFClient client, User user, Peer peer, Orderer orderer, EventHub eventHub) throws IOException, InvalidArgumentException, TransactionException, ProposalException {
        ChannelConfiguration channelConfiguration = new ChannelConfiguration(new File("src/main/resources/health/channel-artifacts/health-channel.tx"));

        Channel newChannel = client.newChannel(
                "health-channel",
                orderer,
                channelConfiguration,
                client.getChannelConfigurationSignature(channelConfiguration, user)
        );

        newChannel.addEventHub(eventHub);
        newChannel.joinPeer(peer);
        newChannel.initialize();

        return newChannel;
    }
}
