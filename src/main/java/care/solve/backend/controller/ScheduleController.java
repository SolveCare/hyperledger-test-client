package care.solve.backend.controller;

import care.solve.backend.service.ScheduleServiceWrapper;
import care.solve.protocol.schedule.entity.Schedule;
import care.solve.protocol.schedule.entity.Slot;
import com.google.protobuf.InvalidProtocolBufferException;
import org.hyperledger.fabric.sdk.exception.ChaincodeEndorsementPolicyParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/schedule")
public class ScheduleController {

    private ScheduleServiceWrapper scheduleServiceWrapper;

    @Autowired
    public ScheduleController(ScheduleServiceWrapper scheduleServiceWrapper) {
        this.scheduleServiceWrapper = scheduleServiceWrapper;
    }

    @GetMapping
    public Schedule getScheduleByOwnerId(@RequestParam String ownerId) throws IOException, ChaincodeEndorsementPolicyParseException {
        return scheduleServiceWrapper.getSchedule(ownerId);
    }

    @PostMapping
    public Schedule createSchedule(@RequestBody Schedule schedule) throws InvalidProtocolBufferException, ExecutionException, InterruptedException {
        return scheduleServiceWrapper.createSchedule(schedule);
    }

    @PostMapping("{scheduleId}/slot")
    public Slot createSlot(@PathVariable String scheduleId, @RequestBody Slot slot) throws InvalidProtocolBufferException, ExecutionException, InterruptedException {
        return scheduleServiceWrapper.createSlot(scheduleId, slot);
    }

    @PatchMapping("{scheduleId}/slot/{slotId}")
    public void updateSlot(@PathVariable String scheduleId, @PathVariable String slotId, @RequestBody Slot slot) throws InvalidProtocolBufferException, ExecutionException, InterruptedException {
        scheduleServiceWrapper.updateSlot(scheduleId, slotId, slot);
    }
}
