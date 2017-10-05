package care.solve.backend.controller;

import care.solve.backend.entity.Doctor;
import care.solve.backend.service.TransactionService;
import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.Peer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/doctor")
public class DoctorController {

    private TransactionService transactionService;
    private HFClient client;
    private ChaincodeID chaincodeId;
    private Channel healthChannel;
    private Peer peer;

    public DoctorController(TransactionService transactionService, HFClient client, ChaincodeID chaincodeId, Channel healthChannel, Peer peer) {
        this.transactionService = transactionService;
        this.client = client;
        this.chaincodeId = chaincodeId;
        this.healthChannel = healthChannel;
        this.peer = peer;
    }

    @PostMapping
    public void create(@RequestBody Doctor doctor) {
        transactionService.sendInvokeTransaction(
                client,
                chaincodeId,
                healthChannel,
                peer,
                "createDoctor",
                new String[]{doctor.getId(), doctor.getEmail(), doctor.getFirstName(), doctor.getLastName(), doctor.getLevel()});


    }

    @GetMapping("{doctorId}/schedule")
    public void getSchedule(@PathVariable String doctorId) {
        transactionService.sendQueryTransaction(
                client,
                chaincodeId,
                healthChannel,
                "getDoctorsSchedule",
                new String[]{doctorId});
    }
}
