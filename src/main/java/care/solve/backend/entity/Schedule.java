package care.solve.backend.entity;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Schedule {

    private String scheduleId;
    private String doctorId;
    private List<Slot> slots;
}
