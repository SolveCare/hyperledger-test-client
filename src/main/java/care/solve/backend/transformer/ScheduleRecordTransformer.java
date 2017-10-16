package care.solve.backend.transformer;

import care.solve.backend.entity.ScheduleProtos;
import care.solve.backend.entity.ScheduleRecord;
import care.solve.backend.entity.Slot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ScheduleRecordTransformer implements ProtoTransformer<ScheduleRecord, ScheduleProtos.ScheduleRecord> {

    private SlotTransformer slotTransformer;

    @Autowired
    public ScheduleRecordTransformer(SlotTransformer slotTransformer) {
        this.slotTransformer = slotTransformer;
    }

    @Override
    public ScheduleProtos.ScheduleRecord transformToProto(ScheduleRecord obj) {
        ScheduleProtos.Slot protoSlot = slotTransformer.transformToProto(obj.getSlot());

        return ScheduleProtos.ScheduleRecord.newBuilder()
                .setRecordId(obj.getRecordId())
                .setPatientId(obj.getPatientId())
                .setDescription(obj.getDescription())
                .setSlot(protoSlot)
                .build();
    }

    @Override
    public ScheduleRecord transformFromProto(ScheduleProtos.ScheduleRecord proto) {
        Slot slot = slotTransformer.transformFromProto(proto.getSlot());

        return ScheduleRecord.builder()
                .recordId(proto.getRecordId())
                .patientId(proto.getPatientId())
                .description(proto.getDescription())
                .slot(slot)
                .build();
    }
}
