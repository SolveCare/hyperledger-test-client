package care.solve.backend.entity;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class DoctorPublic {

    private String id;
    private String firstName;
    private String lastName;

}
