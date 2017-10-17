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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class UserConfig {

    @Value("${user.human.msp.keystore.file}")
    private String humanKeystoreFile;

    @Value("${user.human.msp.cert.file}")
    private String humanCertFile;

    @Value("${user.human.msp.id}")
    private String humanMspId;

    @Value("${user.human.organization.name}")
    private String humanOrgName;

    @Bean(name = "humanAdminUser")
    @Autowired
    public SampleUser createHumanAdminUser(
            @Qualifier("defaultStore") SampleStore sampleStore,
            @Qualifier("hfcaHumanClient") HFCAClient hfcaHumanClient ) throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchProviderException, IOException, EnrollmentException, InvalidArgumentException, URISyntaxException {

        InputStream keystoreFile = new ClassPathResource(humanKeystoreFile).getInputStream();
        InputStream certFile = new ClassPathResource(humanCertFile).getInputStream();

        SampleUser humanAdmin = sampleStore.getMember(
                "admin",
                humanOrgName,
                humanMspId,
                keystoreFile,
                certFile);

        return humanAdmin;
    }
}
