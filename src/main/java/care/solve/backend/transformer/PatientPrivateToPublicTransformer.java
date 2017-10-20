package care.solve.backend.transformer;

import care.solve.backend.entity.PatientPrivate;
import care.solve.backend.entity.PatientPublic;
import org.springframework.beans.BeanUtils;

public class PatientPrivateToPublicTransformer implements Transformer<PatientPrivate, PatientPublic> {

    @Override
    public PatientPublic transform(PatientPrivate patientPrivate) {
        PatientPublic patientPublic = new PatientPublic();
        BeanUtils.copyProperties(patientPrivate, patientPublic);

        return patientPublic;
    }

}
