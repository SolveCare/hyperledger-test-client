package care.solve.backend.service;

import care.solve.backend.entity.Schedule;
import care.solve.backend.entity.ScheduleProtos;
import care.solve.backend.entity.ScheduleRequest;
import care.solve.backend.transformer.ScheduleRequestTransformer;
import care.solve.backend.transformer.ScheduleTransformer;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.TextFormat;
import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.Peer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
public class ScheduleService {

    private TransactionService transactionService;
    private HFClient client;
    private ChaincodeID chaincodeId;
    private Channel healthChannel;
    private Peer peer;

    private ScheduleTransformer scheduleTransformer;
    private ScheduleRequestTransformer scheduleRequestTransformer;

    @Autowired
    public ScheduleService(TransactionService transactionService, HFClient client, ChaincodeID chaincodeId, Channel healthChannel, Peer peer, ScheduleTransformer scheduleTransformer, ScheduleRequestTransformer scheduleRequestTransformer) {
        this.transactionService = transactionService;
        this.client = client;
        this.chaincodeId = chaincodeId;
        this.healthChannel = healthChannel;
        this.peer = peer;
        this.scheduleTransformer = scheduleTransformer;
        this.scheduleRequestTransformer = scheduleRequestTransformer;
    }

    public Schedule getDoctorSchedule(String doctorId) throws InvalidProtocolBufferException {
        ByteString protoScheduleByteString = transactionService.sendQueryTransaction(
                client,
                chaincodeId,
                healthChannel,
                "getDoctorsSchedule",
                new String[]{doctorId});

        ScheduleProtos.Schedule protoSchedule = ScheduleProtos.Schedule.parseFrom(protoScheduleByteString);
        return scheduleTransformer.transformFromProto(protoSchedule);
    }


    public void create(ScheduleRequest request) throws ExecutionException, InterruptedException {

        ScheduleProtos.ScheduleRequest protoScheduleRequest = scheduleRequestTransformer.transformToProto(request);
//        String byteString = new String(protoScheduleRequest.toByteArray()); // todo: investigate why we cannot unmarshall Slot object on the go side
        String byteString = TextFormat.printToString(protoScheduleRequest);
        transactionService.sendInvokeTransaction(
                client,
                chaincodeId,
                healthChannel,
                peer,
                "registerToDoctor",
                new String[]{byteString}
        );

    }
}
