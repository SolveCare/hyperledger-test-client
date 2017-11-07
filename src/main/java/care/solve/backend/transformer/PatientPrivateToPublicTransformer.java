package care.solve.backend.transformer;

import care.solve.backend.entity.PatientPrivate;
import care.solve.protocol.schedule.entity.Patient;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class PatientPrivateToPublicTransformer implements Transformer<PatientPrivate, Patient> {

    @Override
    public Patient transform(PatientPrivate patientPrivate) {
        Patient patientPublic = Patient.builder().build();
        BeanUtils.copyProperties(patientPrivate, patientPublic);

        return patientPublic;
    }

}
