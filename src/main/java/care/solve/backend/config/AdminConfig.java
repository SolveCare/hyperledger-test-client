package care.solve.backend.config;

import care.solve.backend.entity.SampleStore;
import care.solve.backend.entity.SampleUser;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.exception.EnrollmentException;
import org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;

@Configuration
public class AdminConfig {

    @Value("${admin.msp.keystore.file}")
    private String peerAdminKeystoreFile;

    @Value("${admin.msp.cert.file}")
    private String peerAdminCertFile;

    @Value("${admin.msp.id}")
    private String adminMspId;

    @Value("${admin.organization.name}")
    private String adminOrgName;

    private HFCAClient hfcaAdminClient;

    @Autowired
    public AdminConfig(HFCAClient hfcaAdminClient) {
        this.hfcaAdminClient = hfcaAdminClient;
    }

    @Bean(name = "peerAdminUser")
    @Autowired
    public SampleUser createpeerAdminUser(
            @Qualifier("defaultStore") SampleStore sampleStore) throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchProviderException, IOException, EnrollmentException, InvalidArgumentException, URISyntaxException {

        InputStream keystoreFile = new ClassPathResource(peerAdminKeystoreFile).getInputStream();
        InputStream certFile = new ClassPathResource(peerAdminCertFile).getInputStream();

        SampleUser peerAdmin = sampleStore.getMember(
                "peerAdmin",
                adminOrgName,
                adminMspId,
                keystoreFile,
                certFile);

        return peerAdmin;
    }

    @Bean(name = "adminUser")
    @Autowired
    public SampleUser createAdminUser(
            @Qualifier("defaultStore") SampleStore sampleStore) throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchProviderException, IOException, EnrollmentException, InvalidArgumentException, URISyntaxException {

        Enrollment adminEnrollment = hfcaAdminClient.enroll("admin", "adminpw");
        SampleUser admin = sampleStore.getMember(
                "admin",
                adminOrgName,
                adminMspId,
                adminEnrollment.getKey(),
                adminEnrollment.getCert());

        return admin;
    }

}
