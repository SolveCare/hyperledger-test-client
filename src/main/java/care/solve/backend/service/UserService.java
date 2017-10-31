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

    private HFCAClient hfcaAdminClient;
    private SampleStore defaultStore;
    private SampleUser adminUser;

    @Value("${admin.msp.id}")
    private String adminMspId;

    public static final String org = "org1.department1";

    @Autowired
    public UserService(HFCAClient client, SampleStore defaultStore, SampleUser adminUser) {
        this.hfcaAdminClient = client;
        this.defaultStore = defaultStore;
        this.adminUser = adminUser;
    }

    public void registerUser(String userName) throws Exception {
        RegistrationRequest registrationRequest = new RegistrationRequest(userName, org);
        String enrollmentSecret = hfcaAdminClient.register(registrationRequest, adminUser);
        Enrollment enrollment = hfcaAdminClient.enroll(userName, enrollmentSecret);
        defaultStore.getMember(userName, org, adminMspId, enrollment.getKey(), enrollment.getCert());
    }

}
