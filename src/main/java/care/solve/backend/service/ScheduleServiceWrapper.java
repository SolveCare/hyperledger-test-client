package care.solve.backend.service;

import care.solve.protocol.schedule.entity.Schedule;
import care.solve.protocol.schedule.entity.Slot;
import care.solve.protocol.schedule.service.ScheduleService;
import org.hyperledger.fabric.sdk.exception.ChaincodeEndorsementPolicyParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ScheduleServiceWrapper {

    private ScheduleService scheduleService;

    @Autowired
    public ScheduleServiceWrapper(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }


    public Schedule createSchedule(Schedule schedule) {
        return scheduleService.createSchedule(schedule);
    }

    public Slot createSlot(String scheduleId, Slot slot) {
        return scheduleService.createSlot(scheduleId, slot);
    }

    public void updateSlot(String scheduleId, String slotId, Slot slot) {
        scheduleService.updateSlot(scheduleId, slotId, slot);
    }

    public Schedule getSchedule(String ownerId) throws IOException, ChaincodeEndorsementPolicyParseException {
        return scheduleService.getSchedule(ownerId);
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
//                peer0,
//                "registerToDoctor",
//                new String[]{byteString}
//        );
//    }
}
