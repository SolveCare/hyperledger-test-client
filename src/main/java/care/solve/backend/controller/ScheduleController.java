package care.solve.backend.controller;

import care.solve.backend.entity.Schedule;
import care.solve.backend.entity.Slot;
import care.solve.backend.service.ScheduleService;
import com.google.protobuf.InvalidProtocolBufferException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/schedule")
public class ScheduleController {

    private ScheduleService scheduleService;

    @Autowired
    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @GetMapping
    public Schedule getScheduleByOwnerId(@RequestParam String ownerId) throws InvalidProtocolBufferException {
        return scheduleService.getSchedule(ownerId);
    }

    @PostMapping
    public Schedule createSchedule(@RequestBody Schedule schedule) throws InvalidProtocolBufferException, ExecutionException, InterruptedException {
        return scheduleService.createSchedule(schedule);
    }

    @PostMapping("{scheduleId}/slot")
    public Slot createSlot(@PathVariable String scheduleId, @RequestBody Slot slot) throws InvalidProtocolBufferException, ExecutionException, InterruptedException {
        return scheduleService.createSlot(scheduleId, slot);
    }

    @PatchMapping("{scheduleId}/slot/{slotId}")
    public void updateSlot(@PathVariable String scheduleId, @PathVariable String slotId, @RequestBody Slot slot) throws InvalidProtocolBufferException, ExecutionException, InterruptedException {
        scheduleService.updateSlot(scheduleId, slotId, slot);
    }
}
