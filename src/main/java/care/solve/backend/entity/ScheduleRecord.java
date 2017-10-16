package care.solve.backend.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ScheduleRecord {

    private String recordId;
    private String description;
    private String patientId;
    private Slot slot;
}
