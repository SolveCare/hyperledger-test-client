package care.solve.backend.controller;

import care.solve.backend.entity.Patient;
import care.solve.backend.entity.ScheduleRequest;
import care.solve.backend.service.TransactionService;
import org.hyperledger.fabric.sdk.BlockEvent;
import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.Peer;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/patient")
public class PatientController {

    private TransactionService transactionService;
    private HFClient client;
    private ChaincodeID chaincodeId;
    private Channel healthChannel;
    private Peer peer;

    public PatientController(TransactionService transactionService, HFClient client, ChaincodeID chaincodeId, Channel healthChannel, Peer peer) {
        this.transactionService = transactionService;
        this.client = client;
        this.chaincodeId = chaincodeId;
        this.healthChannel = healthChannel;
        this.peer = peer;
    }

    @PostMapping
    public void create(@RequestBody Patient patient) {
        transactionService.sendInvokeTransaction(
                client,
                chaincodeId,
                healthChannel,
                peer,
                "createPatient",
                new String[]{patient.getId(), patient.getEmail(),patient.getFirstName(), patient.getLastName(), patient.getAmount()});


    }

    @PostMapping("{patientId}/register/{doctorId}")
    public void registerToDoctor(@PathVariable String patientId, @PathVariable String doctorId, @RequestBody ScheduleRequest request) throws ExecutionException, InterruptedException {
        BlockEvent.TransactionEvent transactionEvent = transactionService.sendInvokeTransaction(
                client,
                chaincodeId,
                healthChannel,
                peer,
                "registerToDoctor",
                new String[]{patientId, doctorId, request.getTimestampStart(), request.getTimestampEnd(), request.getComment()})
                .get();

        System.out.println(transactionEvent);
    }
}
