package care.solve.backend.service;

import care.solve.backend.entity.SampleStore;
import care.solve.backend.entity.SampleUser;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class HFClientFactory {

    private static final Map<String, HFClient> HF_CLIENT_MAP = new HashMap<>();

    @Autowired
    private SampleStore defaultStore;

    public HFClient getClient() {
        HFClient client;
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null) throw new SessionAuthenticationException("User must be logged in!");

            String userName = authentication.getName();

            if (HF_CLIENT_MAP.containsKey(userName)) {
                return HF_CLIENT_MAP.get(userName);
            }

            SampleUser user = defaultStore.getMember(userName, UserService.org);
            client = HFClient.createNewInstance();
            client.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
            client.setUserContext(user);
            HF_CLIENT_MAP.put(userName, client);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return client;
    }
}
