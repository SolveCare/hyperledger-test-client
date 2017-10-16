package care.solve.backend.transformer;

import care.solve.backend.entity.ScheduleProtos;
import care.solve.backend.entity.ScheduleRecord;
import care.solve.backend.entity.Slot;
import org.springframework.stereotype.Service;

@Service
public class SlotTransformer implements ProtoTransformer<Slot, ScheduleProtos.Slot> {

    @Override
    public ScheduleProtos.Slot transformToProto(Slot obj) {
        return ScheduleProtos.Slot.newBuilder()
                .setTimeStart(obj.getTimeStart())
                .setTimeFinish(obj.getTimeEnd())
                .build();
    }

    @Override
    public Slot transformFromProto(ScheduleProtos.Slot proto) {
        return Slot.builder()
                .timeStart(proto.getTimeStart())
                .timeEnd(proto.getTimeFinish())
                .build();
    }
}
