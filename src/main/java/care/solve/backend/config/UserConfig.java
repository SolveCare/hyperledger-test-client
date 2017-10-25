package care.solve.backend.config;

import care.solve.backend.entity.SampleStore;
import care.solve.backend.entity.SampleUser;
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
            @Qualifier("defaultStore") SampleStore sampleStore) throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchProviderException, IOException, EnrollmentException, InvalidArgumentException, URISyntaxException {

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
