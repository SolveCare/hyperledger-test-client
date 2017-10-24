package care.solve.backend.service;

import care.solve.backend.entity.PatientPrivate;
import care.solve.backend.entity.PatientPublic;
import care.solve.backend.entity.ScheduleProtos;
import care.solve.backend.repository.PatientsRepository;
import care.solve.backend.transformer.PatientPrivateToPublicTransformer;
import care.solve.backend.transformer.PatientToProtoTransformer;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import org.hyperledger.fabric.sdk.BlockEvent;
import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.Peer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class PatientService {

    private PatientsRepository patientsRepository;
    private TransactionService transactionService;
    private HFClient client;
    private ChaincodeID chaincodeId;
    private Channel healthChannel;
    private Peer peer;

    private PatientToProtoTransformer patientToProtoTransformer;
    private PatientPrivateToPublicTransformer patientPrivateToPublicTransformer;

    @Autowired
    public PatientService(PatientsRepository patientsRepository, TransactionService transactionService, HFClient client, ChaincodeID chaincodeId, Channel healthChannel, Peer peer, PatientToProtoTransformer patientToProtoTransformer, PatientPrivateToPublicTransformer patientPrivateToPublicTransformer) {
        this.patientsRepository = patientsRepository;
        this.transactionService = transactionService;
        this.client = client;
        this.chaincodeId = chaincodeId;
        this.healthChannel = healthChannel;
        this.peer = peer;
        this.patientToProtoTransformer = patientToProtoTransformer;
        this.patientPrivateToPublicTransformer = patientPrivateToPublicTransformer;
    }

    public PatientPublic create(PatientPrivate patientPrivate) throws InterruptedException, ExecutionException, InvalidProtocolBufferException {
        patientPrivate = patientsRepository.save(patientPrivate);
        patientsRepository.flush();
        PatientPublic patientPublic = patientPrivateToPublicTransformer.transform(patientPrivate);
        return publishPatientToChaincode(patientPublic);
    }

    public PatientPublic publishPatientToChaincode(PatientPublic patientPublic) throws ExecutionException, InterruptedException, InvalidProtocolBufferException {
        ScheduleProtos.PatientPublic protoPatient = patientToProtoTransformer.transformToProto(patientPublic);
        String byteString = new String(protoPatient.toByteArray());
        CompletableFuture<BlockEvent.TransactionEvent> futureEvents = transactionService.sendInvokeTransaction(
                client,
                chaincodeId,
                healthChannel,
                peer,
                "createPatient",
                new String[]{byteString});

        byte[] payload = futureEvents.get().getTransactionActionInfo(0).getProposalResponsePayload();
        ScheduleProtos.PatientPublic savedProtoPatient = ScheduleProtos.PatientPublic.parseFrom(payload);
        return patientToProtoTransformer.transformFromProto(savedProtoPatient);
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
