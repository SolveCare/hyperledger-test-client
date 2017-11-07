package care.solve.backend.transformer;

import care.solve.backend.entity.PatientPrivate;
import care.solve.protocol.schedule.entity.PatientPublic;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class PatientPrivateToPublicTransformer implements Transformer<PatientPrivate, PatientPublic> {

    @Override
    public PatientPublic transform(PatientPrivate patientPrivate) {
        PatientPublic patientPublic = PatientPublic.builder().build();
        BeanUtils.copyProperties(patientPrivate, patientPublic);

        return patientPublic;
    }

}
