package care.solve.backend.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ScheduleRequest {

    private String doctorId;
    private String patientId;
    private Slot slot;
    private String description;

}
