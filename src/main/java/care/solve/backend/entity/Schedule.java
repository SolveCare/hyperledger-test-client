package care.solve.backend.entity;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class Schedule {

    private String scheduleId;
    private String doctorId;
    private Map<String, ScheduleRecord> records;
}
