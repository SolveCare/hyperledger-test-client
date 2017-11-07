package care.solve.backend.controller;

import care.solve.backend.entity.DoctorPrivate;
import care.solve.backend.service.DoctorServiceWrapper;
import care.solve.backend.service.ScheduleServiceWrapper;
import care.solve.protocol.schedule.entity.DoctorPublic;
import care.solve.protocol.schedule.entity.Schedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/doctor")
public class DoctorController {

    private DoctorServiceWrapper doctorServiceWrapper;
    private ScheduleServiceWrapper scheduleServiceWrapper;

    @Autowired
    public DoctorController(DoctorServiceWrapper doctorServiceWrapper, ScheduleServiceWrapper scheduleServiceWrapper) {
        this.doctorServiceWrapper = doctorServiceWrapper;
        this.scheduleServiceWrapper = scheduleServiceWrapper;
    }

    @PostMapping
    public DoctorPublic create(@RequestBody DoctorPrivate doctor) {
        DoctorPublic doctorPublic = doctorServiceWrapper.create(doctor);
        Schedule doctorsSchedule = Schedule.builder().ownerId(doctorPublic.getId()).build();
        scheduleServiceWrapper.createSchedule(doctorsSchedule);

        return doctorPublic;
    }

    @GetMapping("{doctorId}")
    public DoctorPublic get(@PathVariable String doctorId) throws IOException {
        return doctorServiceWrapper.get(doctorId);
    }

    @GetMapping
    public List<DoctorPublic> getAll() throws IOException {
        return doctorServiceWrapper.getAll();
    }
}
