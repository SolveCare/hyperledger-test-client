package care.solve.backend.config;

import care.solve.backend.entity.SampleStore;
import care.solve.backend.entity.SampleUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;
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
    public SampleUser createSampleUser(@Qualifier("defaultStore") SampleStore sampleStore) throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchProviderException, IOException {
        SampleUser someTestUSER = sampleStore.getMember(
                "admin",
                humanOrgName,
                humanMspId,
                new File(humanKeystoreFile),
                new File(humanCertFile));

        return someTestUSER;
    }
}
