package care.solve.backend.controller;

import care.solve.backend.service.ChaincodeService;
import care.solve.backend.service.DoctorService;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.ChaincodeEndorsementPolicyParseException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
    private HFClient adminClient;
    private ChaincodeID chaincodeId;
    private Channel healthChannel;
    private Peer peer;
    private Orderer orderer;

    @Autowired
    public ChaincodeController(ChaincodeService chaincodeService, DoctorService doctorService, @Qualifier("adminHFClient") HFClient adminClient, ChaincodeID chaincodeId, Channel healthChannel, Peer peer, Orderer orderer) {
        this.chaincodeService = chaincodeService;
        this.doctorService = doctorService;
        this.adminClient = adminClient;
        this.chaincodeId = chaincodeId;
        this.healthChannel = healthChannel;
        this.peer = peer;
        this.orderer = orderer;
    }

    @PostMapping
    public void install() throws ProposalException, IOException, InvalidArgumentException, ChaincodeEndorsementPolicyParseException, InterruptedException, ExecutionException, TimeoutException {
        chaincodeService.installChaincode(adminClient, chaincodeId, peer);
        chaincodeService.instantiateChaincode(adminClient, chaincodeId, healthChannel, orderer, peer)
                .get(20000L, TimeUnit.SECONDS);
        doctorService.chaincodeInitialSync();
    }

}
