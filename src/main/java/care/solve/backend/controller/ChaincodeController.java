package care.solve.backend.controller;

import care.solve.backend.service.ChaincodeService;
import care.solve.backend.service.DoctorService;
import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.Orderer;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.exception.ChaincodeEndorsementPolicyParseException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("/chaincode")
public class ChaincodeController {

    private ChaincodeService chaincodeService;
    private DoctorService doctorService;
    private HFClient client;
    private ChaincodeID chaincodeId;
    private Channel healthChannel;
    private Peer peer;
    private Orderer orderer;

    @Autowired
    public ChaincodeController(ChaincodeService chaincodeService, DoctorService doctorService, HFClient client, ChaincodeID chaincodeId, Channel healthChannel, Peer peer, Orderer orderer) {
        this.chaincodeService = chaincodeService;
        this.doctorService = doctorService;
        this.client = client;
        this.chaincodeId = chaincodeId;
        this.healthChannel = healthChannel;
        this.peer = peer;
        this.orderer = orderer;
    }

    @PostMapping
    public void install() throws ProposalException, IOException, InvalidArgumentException, ChaincodeEndorsementPolicyParseException, InterruptedException, ExecutionException, TimeoutException {
        chaincodeService.installChaincode(client, chaincodeId, peer);
        chaincodeService.instantiateChaincode(client, chaincodeId, healthChannel, orderer, peer)
                .get(20000L, TimeUnit.SECONDS);
        doctorService.chaincodeInitialSync();
    }

}
