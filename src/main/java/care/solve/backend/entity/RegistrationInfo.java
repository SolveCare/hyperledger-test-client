package care.solve.backend.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegistrationInfo {

    private String attendeeId;
    private String description;

}
