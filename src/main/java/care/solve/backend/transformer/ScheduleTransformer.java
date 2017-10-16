package care.solve.backend.transformer;

import care.solve.backend.entity.Schedule;
import care.solve.backend.entity.ScheduleProtos;
import care.solve.backend.entity.ScheduleRecord;
import care.solve.backend.entity.Slot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ScheduleTransformer implements ProtoTransformer<Schedule, ScheduleProtos.Schedule> {

    private ScheduleRecordTransformer scheduleRecordTransformer;

    @Autowired
    public ScheduleTransformer(ScheduleRecordTransformer scheduleRecordTransformer) {
        this.scheduleRecordTransformer = scheduleRecordTransformer;
    }

    @Override
    public ScheduleProtos.Schedule transformToProto(Schedule obj) {
        Map<String, ScheduleRecord> scheduleRecords = obj.getRecords();

        Map<String, ScheduleProtos.ScheduleRecord> protoScheduleRecordMap = scheduleRecords.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> scheduleRecordTransformer.transformToProto(e.getValue())
                ));

        return ScheduleProtos.Schedule.newBuilder()
                .setDoctorId(obj.getDoctorId())
                .setScheduleId(obj.getScheduleId())
                .putAllRecords(protoScheduleRecordMap)
                .build();
    }

    @Override
    public Schedule transformFromProto(ScheduleProtos.Schedule proto) {
        Map<String, ScheduleProtos.ScheduleRecord> protoScheduleRecords = proto.getRecordsMap();

        Map<String, ScheduleRecord> scheduleRecordMap = protoScheduleRecords.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> scheduleRecordTransformer.transformFromProto(e.getValue())
                ));

        return Schedule.builder()
                .doctorId(proto.getDoctorId())
                .scheduleId(proto.getScheduleId())
                .records(scheduleRecordMap)
                .build();
    }
}
