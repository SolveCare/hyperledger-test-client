package care.solve.backend.controller;

import care.solve.backend.entity.Schedule;
import care.solve.backend.entity.ScheduleRequest;
import care.solve.backend.service.ScheduleService;
import com.google.protobuf.InvalidProtocolBufferException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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
    public Schedule getSchedule(@RequestParam String doctorId) throws InvalidProtocolBufferException {
        return scheduleService.getDoctorSchedule(doctorId);
    }

    @PostMapping
    public void create(@RequestBody ScheduleRequest scheduleRequest) throws InvalidProtocolBufferException, ExecutionException, InterruptedException {
        scheduleService.create(scheduleRequest);
    }
}
