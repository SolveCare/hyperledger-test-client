package care.solve.backend.transformer;

import care.solve.backend.entity.Patient;
import care.solve.backend.entity.ScheduleProtos;
import org.springframework.stereotype.Service;

@Service
public class PatientTransformer implements ProtoTransformer<Patient, ScheduleProtos.Patient> {

    @Override
    public ScheduleProtos.Patient transformToProto(Patient obj) {
        return ScheduleProtos.Patient.newBuilder()
                .setUserId(obj.getId())
                .setFirstName(obj.getFirstName())
                .setLastName(obj.getLastName())
                .setEmail(obj.getEmail())
                .setBalance(Float.parseFloat(obj.getAmount()))
                .build();
    }

    @Override
    public Patient transformFromProto(ScheduleProtos.Patient proto) {
        return Patient.builder()
                .id(proto.getUserId())
                .firstName(proto.getFirstName())
                .lastName(proto.getLastName())
                .email(proto.getEmail())
                .amount(Float.toString(proto.getBalance()))
                .build();
    }
}
