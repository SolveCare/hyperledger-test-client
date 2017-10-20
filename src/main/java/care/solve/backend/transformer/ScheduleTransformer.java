package care.solve.backend.transformer;

import care.solve.backend.entity.Schedule;
import care.solve.backend.entity.ScheduleProtos;
import care.solve.backend.entity.ScheduleRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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
        List<ScheduleRecord> scheduleRecords = obj.getRecords();

        List<ScheduleProtos.ScheduleRecord> protoScheduleRecordList = scheduleRecords.stream()
                .map(scheduleRecordTransformer::transformToProto)
                .collect(Collectors.toList());

        return ScheduleProtos.Schedule.newBuilder()
                .setDoctorId(obj.getDoctorId())
                .setScheduleId(obj.getScheduleId())
                .addAllRecords(protoScheduleRecordList)
                .build();
    }

    @Override
    public Schedule transformFromProto(ScheduleProtos.Schedule proto) {
        List<ScheduleProtos.ScheduleRecord> protoScheduleRecords = proto.getRecordsList();

        List<ScheduleRecord> scheduleRecordList = protoScheduleRecords.stream()
                .map(scheduleRecordTransformer::transformFromProto)
                .collect(Collectors.toList());

        return Schedule.builder()
                .doctorId(proto.getDoctorId())
                .scheduleId(proto.getScheduleId())
                .records(scheduleRecordList)
                .build();
    }
}
