package care.solve.backend.transformer;

import care.solve.backend.entity.Doctor;
import care.solve.backend.entity.DoctorPrivate;
import care.solve.backend.entity.DoctorPublic;
import care.solve.backend.entity.ScheduleProtos;
import org.springframework.beans.BeanUtils;

public class DoctorPrivateToPublicTransformer implements Transformer<DoctorPrivate, DoctorPublic> {

    @Override
    public DoctorPublic transform(DoctorPrivate doctorPrivate) {
        DoctorPublic doctorPublic = new DoctorPublic();
        BeanUtils.copyProperties(doctorPrivate, doctorPublic);

        return doctorPublic;
    }

}
