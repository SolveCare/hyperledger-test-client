package care.solve.backend.service;

import org.apache.commons.io.IOUtils;
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

import java.io.IOException;
import java.net.URL;
import java.util.Set;

@Service
public class ChannelService {

    public boolean isChannelExists(String channelName, Peer peer, HFClient client) throws ProposalException, InvalidArgumentException {
        return client.queryChannels(peer).contains(channelName);
    }

    public Channel connectToChannel(
            String channelName,
            HFClient client,
            Orderer orderer,
            EventHub eventHub) throws InvalidArgumentException, TransactionException {
        Channel newChannel = client.newChannel(channelName);
        newChannel.addOrderer(orderer);
        newChannel.addEventHub(eventHub);
        newChannel.initialize();

        return newChannel;
    }

    public Channel constructChannel(String channelName, HFClient client, User user, Set<Peer> peers, Orderer orderer, EventHub eventHub) throws IOException, InvalidArgumentException, TransactionException, ProposalException {
        URL resource = ChannelService.class.getResource("/hyperledger/network/config/channel.tx");
        byte[] bytes = IOUtils.toByteArray(resource);
        ChannelConfiguration channelConfiguration = new ChannelConfiguration(bytes);

        Channel newChannel = client.newChannel(
                channelName,
                orderer,
                channelConfiguration,
                client.getChannelConfigurationSignature(channelConfiguration, user)
        );

        newChannel.addEventHub(eventHub);
        peers.forEach(peer -> {
            try {
                newChannel.joinPeer(peer);
            } catch (ProposalException e) {
                e.printStackTrace();
            }
        });
        newChannel.initialize();

        return newChannel;
    }
}
