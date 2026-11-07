package provider.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProviderTeamIntentTest {

    @Test
    void providerCanExcludeSelfFromHelperCount() {
        Provider provider = Provider.register(ProviderId.generate(), "Aina");
        provider.markKycVerified();
        provider.addHelper(Helper.of("Helper A", KycStatus.VERIFIED));
        provider.addHelper(Helper.of("Helper B", KycStatus.VERIFIED));
        provider.addHelper(Helper.of("Helper C", KycStatus.VERIFIED));
        assertEquals(3, provider.helpers().size());
    }
}
