package care.solve.backend.service;

import care.solve.backend.entity.DoctorPrivate;
import care.solve.backend.repository.DoctorsRepository;
import care.solve.backend.transformer.DoctorPrivateToPublicTransformer;
import care.solve.protocol.schedule.entity.DoctorPublic;
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
        List<DoctorPublic> chaincodeDoctors = getAll();
        List<DoctorPrivate> localDoctorsPrivate = doctorsRepository.findAll();
        List<DoctorPublic> localDoctors = doctorPrivateToPublicTransformer.transformList(localDoctorsPrivate);
        Collection<DoctorPublic> difference = CollectionUtils.disjunction(localDoctors, chaincodeDoctors);
        
        //Create doctors that are in DB but not yet in chaincode
        difference.stream().filter(localDoctors::contains).forEach(doctorService::create);
        //TODO: Make the same to remove redundant doctors
    }

    public DoctorPublic create(DoctorPrivate doctorPrivate) {
        doctorPrivate = doctorsRepository.save(doctorPrivate);
        doctorsRepository.flush();
        DoctorPublic doctorPublic = doctorPrivateToPublicTransformer.transform(doctorPrivate);
        return doctorService.create(doctorPublic);
    }

    public DoctorPublic get(String doctorId) throws IOException {
        return doctorService.get(doctorId);
    }

    public List<DoctorPublic> getAll() throws IOException {
        return doctorService.getAll();
    }
}
