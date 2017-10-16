package care.solve.backend.transformer;

import care.solve.backend.entity.Doctor;
import care.solve.backend.entity.ScheduleProtos;
import org.springframework.stereotype.Service;

@Service
public class DoctorTransformer implements ProtoTransformer<Doctor, ScheduleProtos.Doctor> {

    @Override
    public ScheduleProtos.Doctor transformToProto(Doctor doctor) {

        return ScheduleProtos.Doctor.newBuilder()
                .setFirstName(doctor.getFirstName())
                .setLastName(doctor.getLastName())
                .setEmail(doctor.getEmail())
                .setLevel(doctor.getLevel())
                .setUserId(doctor.getId())
                .build();
    }

    @Override
    public Doctor transformFromProto(ScheduleProtos.Doctor protoDoctor) {
        return Doctor.builder()
                .id(protoDoctor.getUserId())
                .email(protoDoctor.getEmail())
                .firstName(protoDoctor.getFirstName())
                .lastName(protoDoctor.getLastName())
                .level(protoDoctor.getLevel())
                .build();
    }
}
