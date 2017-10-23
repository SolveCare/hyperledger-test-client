package care.solve.backend.service;

import care.solve.backend.entity.Schedule;
import care.solve.backend.entity.ScheduleProtos;
import care.solve.backend.entity.Slot;
import care.solve.backend.transformer.ScheduleTransformer;
import care.solve.backend.transformer.SlotTransformer;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.TextFormat;
import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.Peer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ScheduleService {

    private TransactionService transactionService;
    private HFClient client;
    private ChaincodeID chaincodeId;
    private Channel healthChannel;
    private Peer peer;

    private ScheduleTransformer scheduleTransformer;
    private SlotTransformer slotTransformer;

    @Autowired
    public ScheduleService(TransactionService transactionService, HFClient client, ChaincodeID chaincodeId, Channel healthChannel, Peer peer, ScheduleTransformer scheduleTransformer, SlotTransformer slotTransformer) {
        this.transactionService = transactionService;
        this.client = client;
        this.chaincodeId = chaincodeId;
        this.healthChannel = healthChannel;
        this.peer = peer;
        this.scheduleTransformer = scheduleTransformer;
        this.slotTransformer = slotTransformer;
    }

    public void createSchedule(Schedule schedule) {
        ScheduleProtos.Schedule scheduleProto = scheduleTransformer.transformToProto(schedule);

//        String byteString = TextFormat.printToString(scheduleProto);
        String byteString = new String(scheduleProto.toByteArray());
        transactionService.sendInvokeTransaction(
                client,
                chaincodeId,
                healthChannel,
                peer,
                "createSchedule",
                new String[]{byteString});
    }

    public void createSlot(String scheduleId, Slot slot) {
        ScheduleProtos.Slot protoSlot = slotTransformer.transformToProto(slot);

        String byteString = TextFormat.printToString(protoSlot);
        transactionService.sendInvokeTransaction(
                client,
                chaincodeId,
                healthChannel,
                peer,
                "createSlot",
                new String[]{scheduleId, byteString});
    }

    public void updateSlot(String scheduleId, String slotId, Slot slot) {
        ScheduleProtos.Slot protoSlot = slotTransformer.transformToProto(slot);

        String byteString = TextFormat.printToString(protoSlot);
        transactionService.sendInvokeTransaction(
                client,
                chaincodeId,
                healthChannel,
                peer,
                "updateSlot",
                new String[]{scheduleId, slotId, byteString});
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


//    public void create(RegistrationInfo request) throws ExecutionException, InterruptedException {
//
//        ScheduleProtos.ScheduleRequest protoScheduleRequest = scheduleRequestTransformer.transformToProto(request);
////        String byteString = new String(protoScheduleRequest.toByteArray()); // todo: investigate why we cannot unmarshall Slot object on the go side
//        String byteString = TextFormat.printToString(protoScheduleRequest);
//        transactionService.sendInvokeTransaction(
//                client,
//                chaincodeId,
//                healthChannel,
//                peer,
//                "registerToDoctor",
//                new String[]{byteString}
//        );
//    }
}
