package care.solve.backend.transformer;

import care.solve.backend.entity.DoctorPrivate;
import care.solve.protocol.schedule.entity.Doctor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class DoctorPrivateToPublicTransformer implements Transformer<DoctorPrivate, Doctor> {

    @Override
    public Doctor transform(DoctorPrivate doctorPrivate) {
        Doctor doctorPublic = Doctor.builder().build();
        BeanUtils.copyProperties(doctorPrivate, doctorPublic);

        return doctorPublic;
    }

}
