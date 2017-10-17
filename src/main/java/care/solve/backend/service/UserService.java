package care.solve.backend.service;

import care.solve.backend.entity.SampleStore;
import care.solve.backend.entity.SampleUser;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.RegistrationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private HFCAClient hfcaHumanClient;
    private HFClient hfClient;
    private SampleUser humanAdminUser;
    private SampleStore defaultStore;

    @Value("${user.human.msp.id}")
    private String humanMspId;

    private static final String org = "org1.department1";

    @Autowired
    public UserService(HFCAClient client, HFClient hfClient, SampleUser humanAdminUser, SampleStore defaultStore) {
        this.hfcaHumanClient = client;
        this.hfClient = hfClient;
        this.humanAdminUser = humanAdminUser;
        this.defaultStore = defaultStore;
    }

    public void registerUser(String userName) throws Exception {
        Enrollment adminEnrollment = hfcaHumanClient.enroll("admin", "adminpw");
        humanAdminUser.setEnrollment(adminEnrollment);

        RegistrationRequest registrationRequest = new RegistrationRequest(userName, org);
        String enrollmentSecret = hfcaHumanClient.register(registrationRequest, humanAdminUser);
        Enrollment enrollment = hfcaHumanClient.enroll(userName, enrollmentSecret);

        SampleUser newUser = defaultStore.getMember(userName, org, humanMspId, enrollment.getKey(), enrollment.getCert());
//        newUser.setEnrollment(enrollment);
    }

    public void setCurrentUser(String userName) throws InvalidArgumentException {
        SampleUser user = defaultStore.getMember(userName, org);
        hfClient.setUserContext(user);
    }
}
