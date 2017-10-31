package care.solve.backend.controller;

import care.solve.backend.service.ChaincodeService;
import care.solve.backend.service.DoctorService;
import care.solve.backend.service.UserService;
import org.hyperledger.fabric.sdk.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
    private Peer peer;
    private Orderer orderer;
    private UserService userService;

    @Autowired
    public ChaincodeController(ChaincodeService chaincodeService,
                               DoctorService doctorService,
                               UserService userService,
                               @Qualifier("peerAdminHFClient") HFClient peerAdminClient,
                               ChaincodeID chaincodeId,
                               Channel healthChannel,
                               Peer peer,
                               Orderer orderer) {

        this.chaincodeService = chaincodeService;
        this.doctorService = doctorService;
        this.userService = userService;
        this.peerAdminClient = peerAdminClient;
        this.chaincodeId = chaincodeId;
        this.healthChannel = healthChannel;
        this.peer = peer;
        this.orderer = orderer;
    }

    @PostMapping(value = "upload")
    public void handleFileUpload(@RequestParam("file") MultipartFile file) throws Exception {
        File tarGzFile = new File("/tmp/" + file.getOriginalFilename());
        file.transferTo(tarGzFile);
        chaincodeService.installChaincode(peerAdminClient, chaincodeId, peer, tarGzFile);
        chaincodeService.instantiateChaincode(peerAdminClient, chaincodeId, healthChannel, orderer, peer)
                .get(20000L, TimeUnit.SECONDS);
        userService.registerUser("tim");
        userService.registerUser("tim.Doctor");

    }

}
