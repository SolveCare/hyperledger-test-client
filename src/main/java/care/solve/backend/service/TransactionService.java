package care.solve.backend.service;

import care.solve.backend.entity.ScheduleProtos;
import com.google.common.collect.ImmutableSet;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import org.hyperledger.fabric.sdk.BlockEvent;
import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.ChaincodeResponse;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.QueryByChaincodeRequest;
import org.hyperledger.fabric.sdk.TransactionProposalRequest;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
public class TransactionService {

    public CompletableFuture<BlockEvent.TransactionEvent> sendInvokeTransaction(HFClient client, ChaincodeID chaincodeId, Channel healthChannel, Peer peer, String func, String[] args) {
        try {
            TransactionProposalRequest transactionProposalRequest = client.newTransactionProposalRequest();
            transactionProposalRequest.setChaincodeID(chaincodeId);
            transactionProposalRequest.setFcn(func);
            transactionProposalRequest.setProposalWaitTime(20000L);
            transactionProposalRequest.setArgs(args);

            Collection<ProposalResponse> transactionPropResp = healthChannel.sendTransactionProposal(transactionProposalRequest, ImmutableSet.of(peer));
            long failedResponsesCount = transactionPropResp.stream().filter(resp -> !resp.getStatus().equals(ProposalResponse.Status.SUCCESS)).count();
            if (failedResponsesCount != 0) {
                throw new RuntimeException(String.format("Failed transaction: %d failed from %d ", failedResponsesCount, transactionPropResp.size()));
            }

            CompletableFuture<BlockEvent.TransactionEvent> proposalResponces = healthChannel.sendTransaction(transactionPropResp);
            return proposalResponces;
        } catch (InvalidArgumentException | ProposalException e) {
            throw new RuntimeException(e);
        }
    }

    public ByteString sendQueryTransaction(HFClient client, ChaincodeID chaincodeId, Channel healthChannel, String func, String[] args) {
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
                    System.out.println(String.format("Query payload from peer %s returned %s", proposalResponse.getPeer().getName(), payload));

                    return proposalResponse.getProposalResponse().getResponse().getPayload();
                }
            }
        } catch (InvalidArgumentException | ProposalException e) {
            e.printStackTrace();
        }

        return null;
    }
}
