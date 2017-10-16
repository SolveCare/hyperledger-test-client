package care.solve.backend.controller;

import care.solve.backend.entity.Doctor;
import care.solve.backend.service.DoctorService;
import com.google.protobuf.InvalidProtocolBufferException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/doctor")
public class DoctorController {

    private DoctorService doctorService;

    @Autowired
    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @PostMapping
    public void create(@RequestBody Doctor doctor) {
        doctorService.create(doctor);
    }

    @GetMapping("{doctorId}")
    public Doctor create(@PathVariable String doctorId) throws InvalidProtocolBufferException {
        return doctorService.get(doctorId);
    }
}
