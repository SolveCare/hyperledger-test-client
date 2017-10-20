package care.solve.backend.transformer;

import care.solve.backend.entity.Doctor;
import care.solve.backend.entity.ScheduleProtos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DoctorCollectionTransformer implements ProtoTransformer<List<Doctor>, ScheduleProtos.DoctorCollection> {

    private DoctorTransformer doctorTransformer;

    @Autowired
    public DoctorCollectionTransformer(DoctorTransformer doctorTransformer) {
        this.doctorTransformer = doctorTransformer;
    }

    @Override
    public ScheduleProtos.DoctorCollection transformToProto(List<Doctor> doctors) {

        List<ScheduleProtos.Doctor> protoDoctors = doctors.stream()
                .map(doctorTransformer::transformToProto)
                .collect(Collectors.toList());

        return ScheduleProtos.DoctorCollection.newBuilder()
                .addAllDoctors(protoDoctors)
                .build();
    }

    @Override
    public List<Doctor> transformFromProto(ScheduleProtos.DoctorCollection protoDoctorCollection) {

        return protoDoctorCollection.getDoctorsList().stream()
                .map(doctorTransformer::transformFromProto)
                .collect(Collectors.toList());
    }
}
