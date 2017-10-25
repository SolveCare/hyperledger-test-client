package care.solve.backend.controller;

import care.solve.backend.entity.DoctorPrivate;
import care.solve.backend.entity.DoctorPublic;
import care.solve.backend.entity.Schedule;
import care.solve.backend.service.DoctorService;
import care.solve.backend.service.ScheduleService;
import com.google.protobuf.InvalidProtocolBufferException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/doctor")
public class DoctorController {

    private DoctorService doctorService;
    private ScheduleService scheduleService;

    @Autowired
    public DoctorController(DoctorService doctorService, ScheduleService scheduleService) {
        this.doctorService = doctorService;
        this.scheduleService = scheduleService;
    }

    @PostMapping
    public DoctorPublic create(@RequestBody DoctorPrivate doctor) {
        DoctorPublic doctorPublic = doctorService.create(doctor);
        Schedule doctorsSchedule = Schedule.builder().ownerId(doctorPublic.getId()).build();
        scheduleService.createSchedule(doctorsSchedule);

        return doctorPublic;
    }

    @GetMapping("{doctorId}")
    public DoctorPublic get(@PathVariable String doctorId) throws InvalidProtocolBufferException {
        return doctorService.get(doctorId);
    }

    @GetMapping
    public List<DoctorPublic> getAll() throws InvalidProtocolBufferException {
        return doctorService.getAll();
    }
}
