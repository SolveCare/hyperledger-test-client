package care.solve.backend.transformer;

import care.solve.backend.entity.DoctorPrivate;
import care.solve.backend.entity.DoctorPublic;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class DoctorPrivateToPublicTransformer implements Transformer<DoctorPrivate, DoctorPublic> {

    @Override
    public DoctorPublic transform(DoctorPrivate doctorPrivate) {
        DoctorPublic doctorPublic = DoctorPublic.builder().build();
        BeanUtils.copyProperties(doctorPrivate, doctorPublic);

        return doctorPublic;
    }

}
