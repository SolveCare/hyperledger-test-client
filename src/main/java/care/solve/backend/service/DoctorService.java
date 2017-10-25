package care.solve.backend.service;

import care.solve.backend.entity.DoctorPrivate;
import care.solve.backend.entity.DoctorPublic;
import care.solve.backend.entity.ScheduleProtos;
import care.solve.backend.repository.DoctorsRepository;
import care.solve.backend.transformer.DoctorPrivateToPublicTransformer;
import care.solve.backend.transformer.DoctorToProtoCollectionTransformer;
import care.solve.backend.transformer.DoctorToProtoTransformer;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.commons.collections.CollectionUtils;
import org.hyperledger.fabric.sdk.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class DoctorService {

    private DoctorsRepository doctorsRepository;
    private TransactionService transactionService;
    private HFClient adminHFClient;
    private HFClientFactory hfClientFactory;
    private ChaincodeID chaincodeId;
    private Channel healthChannel;
    private Peer peer;

    private DoctorToProtoTransformer doctorToProtoTransformer;
    private DoctorToProtoCollectionTransformer doctorToProtoCollectionTransformer;
    private DoctorPrivateToPublicTransformer doctorPrivateToPublicTransformer;

    @Autowired
    public DoctorService(DoctorsRepository doctorsRepository, TransactionService transactionService, HFClientFactory hfClientFactory, @Qualifier("adminHFClient") HFClient adminHFClient, ChaincodeID chaincodeId, Channel healthChannel, Peer peer, DoctorToProtoTransformer doctorToProtoTransformer, DoctorToProtoCollectionTransformer doctorToProtoCollectionTransformer, DoctorPrivateToPublicTransformer doctorPrivateToPublicTransformer) {
        this.doctorsRepository = doctorsRepository;
        this.transactionService = transactionService;
        this.hfClientFactory = hfClientFactory;
        this.adminHFClient = adminHFClient;
        this.chaincodeId = chaincodeId;
        this.healthChannel = healthChannel;
        this.peer = peer;
        this.doctorToProtoTransformer = doctorToProtoTransformer;
        this.doctorToProtoCollectionTransformer = doctorToProtoCollectionTransformer;
        this.doctorPrivateToPublicTransformer = doctorPrivateToPublicTransformer;
    }

    public void chaincodeInitialSync() throws InvalidProtocolBufferException {
        List<DoctorPublic> chaincodeDoctors = getAll();
        List<DoctorPrivate> localDoctorsPrivate = doctorsRepository.findAll();
        List<DoctorPublic> localDoctors = doctorPrivateToPublicTransformer.transformList(localDoctorsPrivate);
        Collection<DoctorPublic> difference = CollectionUtils.disjunction(localDoctors, chaincodeDoctors);
        
        //Create doctors that are in DB but not yet in chaincode
        difference.stream().filter(doctor -> localDoctors.contains(doctor)).forEach(this::publishDoctorToChaincode);
        //TODO: Make the same to remove redundant doctors
    }

    public DoctorPublic create(DoctorPrivate doctorPrivate) {
        doctorPrivate = doctorsRepository.save(doctorPrivate);
        doctorsRepository.flush();
        DoctorPublic doctorPublic = doctorPrivateToPublicTransformer.transform(doctorPrivate);
        return publishDoctorToChaincode(doctorPublic);
    }

    public DoctorPublic publishDoctorToChaincode(DoctorPublic doctor) {
        ScheduleProtos.DoctorPublic protoDoctor = doctorToProtoTransformer.transformToProto(doctor);
        String byteString = new String(protoDoctor.toByteArray());
        CompletableFuture<BlockEvent.TransactionEvent> futureEvents = transactionService.sendInvokeTransaction(
                hfClientFactory.getClient(),
                chaincodeId,
                healthChannel,
                peer,
                "createDoctor",
                new String[]{byteString});

        ScheduleProtos.DoctorPublic savedProtoDoctor = null;
        try {
            byte[] payload = futureEvents.get().getTransactionActionInfo(0).getProposalResponsePayload();
            savedProtoDoctor = ScheduleProtos.DoctorPublic.parseFrom(payload);
        } catch (InterruptedException | InvalidProtocolBufferException | ExecutionException e) {
            e.printStackTrace();
        }

        return doctorToProtoTransformer.transformFromProto(savedProtoDoctor);
    }

    public DoctorPublic get(String doctorId) throws InvalidProtocolBufferException {
        ByteString protoDoctorByteString = transactionService.sendQueryTransaction(
                hfClientFactory.getClient(),
                chaincodeId,
                healthChannel,
                "getDoctor",
                new String[]{doctorId});

        ScheduleProtos.DoctorPublic protoDoctor = ScheduleProtos.DoctorPublic.parseFrom(protoDoctorByteString);
        return doctorToProtoTransformer.transformFromProto(protoDoctor);
    }

    public List<DoctorPublic> getAll() throws InvalidProtocolBufferException {
        ByteString protoDoctorsByteString = transactionService.sendQueryTransaction(
                hfClientFactory.getClient(),
                chaincodeId,
                healthChannel,
                "getAllDoctors",
                new String[]{});

        ScheduleProtos.DoctorCollection protoDoctor = ScheduleProtos.DoctorCollection.parseFrom(protoDoctorsByteString);
        return doctorToProtoCollectionTransformer.transformFromProto(protoDoctor);
    }
}
