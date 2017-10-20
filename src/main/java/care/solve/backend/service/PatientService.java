package care.solve.backend.service;

import care.solve.backend.entity.PatientPublic;
import care.solve.backend.entity.ScheduleProtos;
import care.solve.backend.repository.PatientsRepository;
import care.solve.backend.transformer.PatientToProtoTransformer;
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

    private PatientsRepository patientsRepository;
    private TransactionService transactionService;
    private HFClient client;
    private ChaincodeID chaincodeId;
    private Channel healthChannel;
    private Peer peer;

    private PatientToProtoTransformer patientToProtoTransformer;

    @Autowired
    public PatientService(PatientsRepository patientsRepository, TransactionService transactionService, HFClient client, ChaincodeID chaincodeId, Channel healthChannel, Peer peer, PatientToProtoTransformer patientToProtoTransformer) {
        this.patientsRepository = patientsRepository;
        this.transactionService = transactionService;
        this.client = client;
        this.chaincodeId = chaincodeId;
        this.healthChannel = healthChannel;
        this.peer = peer;
        this.patientToProtoTransformer = patientToProtoTransformer;
    }

    public void create(PatientPublic patient) {
        ScheduleProtos.PatientPublic protoPatient = patientToProtoTransformer.transformToProto(patient);
        String byteString = new String(protoPatient.toByteArray());
        transactionService.sendInvokeTransaction(
                client,
                chaincodeId,
                healthChannel,
                peer,
                "createPatient",
                new String[]{byteString});
    }

    public PatientPublic get(String patientId) throws InvalidProtocolBufferException {
        ByteString protoPatientByteString = transactionService.sendQueryTransaction(
                client,
                chaincodeId,
                healthChannel,
                "getPatient",
                new String[]{patientId});

        ScheduleProtos.PatientPublic protoPatient = ScheduleProtos.PatientPublic.parseFrom(protoPatientByteString);
        return patientToProtoTransformer.transformFromProto(protoPatient);
    }
}
