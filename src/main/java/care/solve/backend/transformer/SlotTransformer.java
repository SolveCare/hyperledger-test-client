package care.solve.backend.transformer;

import care.solve.backend.entity.ScheduleProtos;
import care.solve.backend.entity.Slot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SlotTransformer implements ProtoTransformer<Slot, ScheduleProtos.Slot> {

    private RegistrationInfoTransformer registrationInfoTransformer;

    @Autowired
    public SlotTransformer(RegistrationInfoTransformer registrationInfoTransformer) {
        this.registrationInfoTransformer = registrationInfoTransformer;
    }

    @Override
    public ScheduleProtos.Slot transformToProto(Slot obj) {
        ScheduleProtos.Slot.Builder builder = ScheduleProtos.Slot.newBuilder();
        if (obj.getSlotId() != null) { builder.setSlotId(obj.getSlotId());}
        if (obj.getTimeStart() != null) { builder.setTimeStart(obj.getTimeStart());}
        if (obj.getTimeFinish() != null) { builder.setTimeFinish(obj.getTimeFinish());}
        if (obj.getAvailability() != null) { builder.setAvaliable(ScheduleProtos.Slot.Type.valueOf(obj.getAvailability().name()));}
        if (obj.getRegistrationInfo() != null) { builder.setRegistrationInfo(registrationInfoTransformer.transformToProto(obj.getRegistrationInfo()));}

        return builder.build();
    }

    @Override
    public Slot transformFromProto(ScheduleProtos.Slot proto) {
        return Slot.builder()
                .slotId(proto.getSlotId())
                .timeStart(proto.getTimeStart())
                .timeFinish(proto.getTimeFinish())
                .registrationInfo(registrationInfoTransformer.transformFromProto(proto.getRegistrationInfo()))
                .availability(Slot.Type.valueOf(proto.getAvaliable().name()))
                .build();
    }
}
