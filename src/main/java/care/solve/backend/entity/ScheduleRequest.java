package care.solve.backend.entity;

import lombok.Data;

@Data
public class ScheduleRequest {

    private String timestampStart;
    private String timestampEnd;
    private String comment;
}
