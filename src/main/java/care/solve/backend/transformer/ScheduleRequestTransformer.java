package care.solve.backend.transformer;

import care.solve.backend.entity.ScheduleProtos;
import care.solve.backend.entity.ScheduleRequest;
import care.solve.backend.entity.Slot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ScheduleRequestTransformer implements ProtoTransformer<ScheduleRequest, ScheduleProtos.ScheduleRequest> {

    private SlotTransformer slotTransformer;

    @Autowired
    public ScheduleRequestTransformer(SlotTransformer slotTransformer) {
        this.slotTransformer = slotTransformer;
    }

    @Override
    public ScheduleProtos.ScheduleRequest transformToProto(ScheduleRequest obj) {
        ScheduleProtos.Slot protoSlot = slotTransformer.transformToProto(obj.getSlot());

        return ScheduleProtos.ScheduleRequest.newBuilder()
                .setDoctorId(obj.getDoctorId())
                .setPatientId(obj.getPatientId())
                .setDescription(obj.getDescription())
                .setSlot(protoSlot)
                .build();
    }

    @Override
    public ScheduleRequest transformFromProto(ScheduleProtos.ScheduleRequest proto) {
        Slot slot = slotTransformer.transformFromProto(proto.getSlot());

        return ScheduleRequest.builder()
                .doctorId(proto.getDoctorId())
                .patientId(proto.getPatientId())
                .description(proto.getDescription())
                .slot(slot)
                .build();
    }
}
