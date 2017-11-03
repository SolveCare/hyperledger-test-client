package care.solve.backend.controller;

import care.solve.backend.service.ChaincodeService;
import care.solve.backend.service.DoctorService;
import care.solve.backend.service.UserService;
import com.google.common.collect.ImmutableSet;
import org.hyperledger.fabric.sdk.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/chaincode")
public class ChaincodeController {

    private ChaincodeService chaincodeService;
    private DoctorService doctorService;
    private HFClient peerAdminClient;
    private ChaincodeID chaincodeId;
    private Channel healthChannel;
    private Peer peer1;
    private Peer peer2;
    private Peer peer0;
    private Orderer orderer;
    private UserService userService;

    @Autowired
    public ChaincodeController(ChaincodeService chaincodeService,
                               DoctorService doctorService,
                               UserService userService,
                               @Qualifier("peerAdminHFClient") HFClient peerAdminClient,
                               ChaincodeID chaincodeId,
                               Channel healthChannel,
                               Peer peer1,
                               Peer peer2,
                               Peer peer0,
                               Orderer orderer) {

        this.chaincodeService = chaincodeService;
        this.doctorService = doctorService;
        this.userService = userService;
        this.peerAdminClient = peerAdminClient;
        this.chaincodeId = chaincodeId;
        this.healthChannel = healthChannel;
        this.peer1 = peer1;
        this.peer2 = peer2;
        this.peer0 = peer0;
        this.orderer = orderer;
    }

    @PostMapping(value = "upload")
    public void handleFileUpload(@RequestParam("file") MultipartFile file) throws Exception {
        File tarGzFile = new File("/tmp/" + file.getOriginalFilename());
        file.transferTo(tarGzFile);
        chaincodeService.installChaincode(peerAdminClient, chaincodeId, ImmutableSet.of(peer0, peer1, peer2), tarGzFile);
        chaincodeService.instantiateChaincode(peerAdminClient, chaincodeId, healthChannel, orderer, ImmutableSet.of(peer0, peer1, peer2))
                .get(20000L, TimeUnit.SECONDS);
        userService.registerUser("tim");
        userService.registerUser("tim.Doctor");
    }

}
