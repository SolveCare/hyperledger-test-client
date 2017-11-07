package care.solve.backend.service;

import care.solve.backend.entity.DoctorPrivate;
import care.solve.backend.repository.DoctorsRepository;
import care.solve.backend.transformer.DoctorPrivateToPublicTransformer;
import care.solve.protocol.schedule.entity.Doctor;
import care.solve.protocol.schedule.service.DoctorService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

@Service
public class DoctorServiceWrapper {

    private DoctorsRepository doctorsRepository;
    private DoctorService doctorService;
    private DoctorPrivateToPublicTransformer doctorPrivateToPublicTransformer;

    @Autowired
    public DoctorServiceWrapper(DoctorsRepository doctorsRepository, DoctorService doctorService, DoctorPrivateToPublicTransformer doctorPrivateToPublicTransformer) {
        this.doctorsRepository = doctorsRepository;
        this.doctorService = doctorService;
        this.doctorPrivateToPublicTransformer = doctorPrivateToPublicTransformer;
    }

    public void chaincodeInitialSync() throws IOException {
        List<Doctor> chaincodeDoctors = getAll();
        List<DoctorPrivate> localDoctorsPrivate = doctorsRepository.findAll();
        List<Doctor> localDoctors = doctorPrivateToPublicTransformer.transformList(localDoctorsPrivate);
        Collection<Doctor> difference = CollectionUtils.disjunction(localDoctors, chaincodeDoctors);
        
        //Create doctors that are in DB but not yet in chaincode
        difference.stream().filter(localDoctors::contains).forEach(doctorService::create);
        //TODO: Make the same to remove redundant doctors
    }

    public Doctor create(DoctorPrivate doctorPrivate) {
        doctorPrivate = doctorsRepository.save(doctorPrivate);
        doctorsRepository.flush();
        Doctor doctorPublic = doctorPrivateToPublicTransformer.transform(doctorPrivate);
        return doctorService.create(doctorPublic);
    }

    public Doctor get(String doctorId) throws IOException {
        return doctorService.get(doctorId);
    }

    public List<Doctor> getAll() throws IOException {
        return doctorService.getAll();
    }
}
