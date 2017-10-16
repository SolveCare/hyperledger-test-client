package care.solve.backend.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Slot {

    private Long timeStart;
    private Long timeEnd;

}
