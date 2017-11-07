package care.solve.backend.service;

import care.solve.backend.entity.PatientPrivate;
import care.solve.backend.repository.PatientsRepository;
import care.solve.backend.transformer.PatientPrivateToPublicTransformer;
import care.solve.protocol.schedule.entity.PatientPublic;
import care.solve.protocol.schedule.service.PatientService;
import com.google.protobuf.InvalidProtocolBufferException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

@Service
public class PatientServiceWrapper {

    private PatientsRepository patientsRepository;
    private PatientPrivateToPublicTransformer patientPrivateToPublicTransformer;
    private PatientService patientService;

    @Autowired
    public PatientServiceWrapper(PatientsRepository patientsRepository, PatientPrivateToPublicTransformer patientPrivateToPublicTransformer, PatientService patientService) {
        this.patientsRepository = patientsRepository;
        this.patientPrivateToPublicTransformer = patientPrivateToPublicTransformer;
        this.patientService = patientService;
    }

    public PatientPublic create(PatientPrivate patientPrivate) throws InterruptedException, ExecutionException, InvalidProtocolBufferException {
        patientPrivate = patientsRepository.save(patientPrivate);
        patientsRepository.flush();
        PatientPublic patientPublic = patientPrivateToPublicTransformer.transform(patientPrivate);
        return patientService.create(patientPublic);
    }

    public PatientPublic get(String patientId) throws IOException {
        return patientService.get(patientId);
    }
}
