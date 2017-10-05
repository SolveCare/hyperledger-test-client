package care.solve.backend.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Patient {

    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private String amount;

}
