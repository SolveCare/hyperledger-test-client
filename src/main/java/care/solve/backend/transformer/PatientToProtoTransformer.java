package care.solve.backend.transformer;

import care.solve.backend.entity.PatientPublic;
import care.solve.backend.entity.ScheduleProtos;
import org.springframework.stereotype.Service;

@Service
public class PatientToProtoTransformer implements ProtoTransformer<PatientPublic, ScheduleProtos.PatientPublic> {

    @Override
    public ScheduleProtos.PatientPublic transformToProto(PatientPublic obj) {
        return ScheduleProtos.PatientPublic.newBuilder()
                .setPatientId(obj.getId())
                .build();
    }

    @Override
    public PatientPublic transformFromProto(ScheduleProtos.PatientPublic proto) {
        return PatientPublic.builder()
                .id(proto.getPatientId())
                .build();
    }
}
