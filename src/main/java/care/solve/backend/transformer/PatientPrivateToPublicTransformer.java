package care.solve.backend.transformer;

import care.solve.backend.entity.DoctorPublic;
import care.solve.backend.entity.PatientPrivate;
import care.solve.backend.entity.PatientPublic;
import org.springframework.beans.BeanUtils;

public class PatientPrivateToPublicTransformer implements Transformer<PatientPrivate, PatientPublic> {

    @Override
    public PatientPublic transform(PatientPrivate patientPrivate) {
        PatientPublic patientPublic = PatientPublic.builder().build();
        BeanUtils.copyProperties(patientPrivate, patientPublic);

        return patientPublic;
    }

}
