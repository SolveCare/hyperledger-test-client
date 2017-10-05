package care.solve.backend.config;

import care.solve.backend.entity.SampleStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;

@Configuration
public class StoreConfig {

    @Bean(name = "defaultStore")
    public SampleStore getDefaultStore() throws IOException {
        File sampleStoreFile = new File(System.getProperty("user.home") + "/test.properties");
        if (sampleStoreFile.exists()) { //For testing start fresh
            sampleStoreFile.delete();
        } else {
            sampleStoreFile.createNewFile();
        }

        return new SampleStore(sampleStoreFile);
    }

}
