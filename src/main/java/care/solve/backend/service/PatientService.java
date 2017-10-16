package care.solve.backend.service;

import care.solve.backend.entity.Patient;
import care.solve.backend.entity.ScheduleProtos;
import care.solve.backend.transformer.PatientTransformer;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.Peer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PatientService {

    private TransactionService transactionService;
    private HFClient client;
    private ChaincodeID chaincodeId;
    private Channel healthChannel;
    private Peer peer;

    private PatientTransformer patientTransformer;

    @Autowired
    public PatientService(TransactionService transactionService, HFClient client, ChaincodeID chaincodeId, Channel healthChannel, Peer peer, PatientTransformer patientTransformer) {
        this.transactionService = transactionService;
        this.client = client;
        this.chaincodeId = chaincodeId;
        this.healthChannel = healthChannel;
        this.peer = peer;
        this.patientTransformer = patientTransformer;
    }

    public void create(Patient patient) {
        ScheduleProtos.Patient protoPatient = patientTransformer.transformToProto(patient);
        String byteString = new String(protoPatient.toByteArray());
        transactionService.sendInvokeTransaction(
                client,
                chaincodeId,
                healthChannel,
                peer,
                "createPatient",
                new String[]{byteString});
    }

    public Patient get(String patientId) throws InvalidProtocolBufferException {
        ByteString protoPatientByteString = transactionService.sendQueryTransaction(
                client,
                chaincodeId,
                healthChannel,
                "getPatient",
                new String[]{patientId});

        ScheduleProtos.Patient protoPatient = ScheduleProtos.Patient.parseFrom(protoPatientByteString);
        return patientTransformer.transformFromProto(protoPatient);
    }
}
