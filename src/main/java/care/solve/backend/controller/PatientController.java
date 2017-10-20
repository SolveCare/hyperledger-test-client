package care.solve.backend.controller;

import care.solve.backend.entity.PatientPublic;
import care.solve.backend.service.PatientService;
import com.google.protobuf.InvalidProtocolBufferException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/patient")
public class PatientController {

    private PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping("{patientId}")
    public PatientPublic get(@PathVariable String patientId) throws InvalidProtocolBufferException {
        return patientService.get(patientId);
    }

    @PostMapping
    public void create(@RequestBody PatientPublic patient) {
        patientService.create(patient);
    }

}
