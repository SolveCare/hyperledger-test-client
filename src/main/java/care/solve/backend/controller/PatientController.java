package care.solve.backend.controller;

import care.solve.backend.entity.PatientPrivate;
import care.solve.backend.entity.PatientPublic;
import care.solve.backend.entity.Schedule;
import care.solve.backend.service.PatientService;
import care.solve.backend.service.ScheduleService;
import com.google.protobuf.InvalidProtocolBufferException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/patient")
public class PatientController {

    private PatientService patientService;
    private ScheduleService scheduleService;

    @Autowired
    public PatientController(PatientService patientService, ScheduleService scheduleService) {
        this.patientService = patientService;
        this.scheduleService = scheduleService;
    }

    @GetMapping("{patientId}")
    public PatientPublic get(@PathVariable String patientId) throws IOException {
        return patientService.get(patientId);
    }

    @PostMapping
    public PatientPublic create(@RequestBody PatientPrivate patient) throws InterruptedException, ExecutionException, InvalidProtocolBufferException {
        PatientPublic patientPublic = patientService.create(patient);
        Schedule patientsSchedule = Schedule.builder().ownerId(patientPublic.getId()).build();
        scheduleService.createSchedule(patientsSchedule);

        return patientPublic;
    }

}
