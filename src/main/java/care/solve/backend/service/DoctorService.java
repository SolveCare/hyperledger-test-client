package care.solve.backend.service;

import care.solve.backend.entity.Doctor;
import care.solve.backend.entity.DoctorPrivate;
import care.solve.backend.entity.ScheduleProtos;
import care.solve.backend.transformer.DoctorCollectionTransformer;
import care.solve.backend.repository.DoctorsRepository;
import care.solve.backend.transformer.DoctorTransformer;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.Peer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DoctorService {

    private DoctorsRepository doctorsRepository;
    private TransactionService transactionService;
    private HFClient client;
    private ChaincodeID chaincodeId;
    private Channel healthChannel;
    private Peer peer;

    private DoctorTransformer doctorTransformer;
    private DoctorCollectionTransformer doctorCollectionTransformer;

    @Autowired
    public DoctorService(DoctorsRepository doctorsRepository, TransactionService transactionService, HFClient client, ChaincodeID chaincodeId, Channel healthChannel, Peer peer, DoctorTransformer doctorTransformer, DoctorCollectionTransformer doctorCollectionTransformer) {
        this.doctorsRepository = doctorsRepository;
        this.transactionService = transactionService;
        this.client = client;
        this.chaincodeId = chaincodeId;
        this.healthChannel = healthChannel;
        this.peer = peer;
        this.doctorTransformer = doctorTransformer;
        this.doctorCollectionTransformer = doctorCollectionTransformer;
    }


    public void create(Doctor doctor) {
        ScheduleProtos.Doctor protoDoctor = doctorTransformer.transformToProto(doctor);
        String byteString = new String(protoDoctor.toByteArray());
        transactionService.sendInvokeTransaction(
                client,
                chaincodeId,
                healthChannel,
                peer,
                "createDoctor",
                new String[]{byteString});
    }

    public Doctor get(String doctorId) throws InvalidProtocolBufferException {
        ByteString protoDoctorByteString = transactionService.sendQueryTransaction(
                client,
                chaincodeId,
                healthChannel,
                "getDoctor",
                new String[]{doctorId});

        ScheduleProtos.Doctor protoDoctor = ScheduleProtos.Doctor.parseFrom(protoDoctorByteString);
        return doctorTransformer.transformFromProto(protoDoctor);
    }

    public List<Doctor> getAll() throws InvalidProtocolBufferException {
        ByteString protoDoctorsByteString = transactionService.sendQueryTransaction(
                client,
                chaincodeId,
                healthChannel,
                "getAllDoctors",
                new String[]{});

        ScheduleProtos.DoctorCollection protoDoctor = ScheduleProtos.DoctorCollection.parseFrom(protoDoctorsByteString);
        return doctorCollectionTransformer.transformFromProto(protoDoctor);
    }
}
