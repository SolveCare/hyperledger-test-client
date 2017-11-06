package care.solve.backend.controller;

import care.solve.fabric.service.ChaincodeService;
import care.solve.fabric.service.UserService;
import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.Orderer;
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
    private HFClient peerAdminClient;
    private ChaincodeID chaincodeId;
    private Channel healthChannel;
    private Orderer orderer;
    private UserService userService;

    @Autowired
    public ChaincodeController(ChaincodeService chaincodeService,
                               UserService userService,
                               @Qualifier("peerAdminHFClient") HFClient peerAdminClient,
                               ChaincodeID chaincodeId,
                               Channel healthChannel,
                               Orderer orderer) {

        this.chaincodeService = chaincodeService;
        this.userService = userService;
        this.peerAdminClient = peerAdminClient;
        this.chaincodeId = chaincodeId;
        this.healthChannel = healthChannel;
        this.orderer = orderer;
    }

    @PostMapping(value = "upload")
    public void handleFileUpload(@RequestParam("file") MultipartFile file) throws Exception {
        File tarGzFile = new File("/tmp/" + file.getOriginalFilename());
        file.transferTo(tarGzFile);
        chaincodeService.installChaincode(peerAdminClient, chaincodeId, healthChannel.getPeers(), tarGzFile);
        chaincodeService.instantiateChaincode(peerAdminClient, chaincodeId, healthChannel, orderer, healthChannel.getPeers())
                .get(20000L, TimeUnit.SECONDS);
        userService.registerUser("tim");
        userService.registerUser("tim.Doctor");
    }


//    @GetMapping(value = "info")
//    public List<Query.ChaincodeInfo> info() throws Exception {
//        return healthChannel.queryInstantiatedChaincodes(healthChannel.getPeers().);
//    }
}
