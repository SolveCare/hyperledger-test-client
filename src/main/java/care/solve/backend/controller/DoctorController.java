package care.solve.backend.controller;

import care.solve.backend.entity.DoctorPublic;
import care.solve.backend.entity.ScheduleProtos;
import care.solve.backend.service.DoctorService;
import com.google.protobuf.InvalidProtocolBufferException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/doctor")
public class DoctorController {

    private DoctorService doctorService;

    @Autowired
    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @PostMapping
    public void create(@RequestBody DoctorPublic doctor) {
        doctorService.create(doctor);
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
