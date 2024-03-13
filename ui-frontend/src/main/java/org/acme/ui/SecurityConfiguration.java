package org.acme.ui;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.saml2.core.Saml2X509Credential;
import org.springframework.security.saml2.provider.service.authentication.AbstractSaml2AuthenticationRequest;
import org.springframework.security.saml2.provider.service.authentication.OpenSaml4AuthenticationProvider;
import org.springframework.security.saml2.provider.service.authentication.OpenSaml4AuthenticationProvider.ResponseToken;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticatedPrincipal;
import org.springframework.security.saml2.provider.service.authentication.Saml2Authentication;
import org.springframework.security.saml2.provider.service.registration.InMemoryRelyingPartyRegistrationRepository;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistration;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrationRepository;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrations;
import org.springframework.security.saml2.provider.service.web.Saml2AuthenticationRequestRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.core.io.ClassPathResource;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    
  @Bean
  SecurityFilterChain configure(HttpSecurity http) throws Exception {

    OpenSaml4AuthenticationProvider authenticationProvider = new OpenSaml4AuthenticationProvider();
    authenticationProvider.setResponseAuthenticationConverter(groupsConverter());

    http.authorizeHttpRequests(authorize -> authorize
        .requestMatchers("/actuator/**").permitAll()
        .anyRequest().authenticated())
      .saml2Login(saml2 -> saml2
        .authenticationManager(new ProviderManager(authenticationProvider)))
      .saml2Logout(Customizer.withDefaults());

    return http.build();
  }

  @Bean
  RelyingPartyRegistrationRepository registrations() throws IOException {
    RSAPrivateKey key = readPKCS8PrivateKey(new ClassPathResource("credentials/local.key").getFile());
    X509Certificate certificate = readX509Certificate(new ClassPathResource("credentials/local.crt").getInputStream());
    Saml2X509Credential credential = Saml2X509Credential.signing(key, certificate);
    RelyingPartyRegistration registration = RelyingPartyRegistrations
            .fromMetadataLocation("https://dev-i7btdyfxq64m44re.us.auth0.com/samlp/metadata/dGTb7mv3Z1VAttIb1uJSx5qoRPaaw4nA")
            .registrationId("auth0")
            .singleLogoutServiceLocation("{baseUrl}/logout/saml2/slo")
            .signingX509Credentials((signing) -> signing.add(credential)) 
            .build();
      return new InMemoryRelyingPartyRegistrationRepository(registration);
  }

  @Bean
  Saml2AuthenticationRequestRepository<AbstractSaml2AuthenticationRequest> authenticationRequestRepository() {
    return new CustomSaml2AuthenticationRequestRepository();
  }

  private Converter<OpenSaml4AuthenticationProvider.ResponseToken, Saml2Authentication> groupsConverter() {

    Converter<ResponseToken, Saml2Authentication> delegate =
      OpenSaml4AuthenticationProvider.createDefaultResponseAuthenticationConverter();

    return (responseToken) -> {
      Saml2Authentication authentication = delegate.convert(responseToken);
      Saml2AuthenticatedPrincipal principal = (Saml2AuthenticatedPrincipal) authentication.getPrincipal();
      List<String> groups = principal.getAttribute("groups");
      Set<GrantedAuthority> authorities = new HashSet<>();
      if (groups != null) {
          groups.stream().map(SimpleGrantedAuthority::new).forEach(authorities::add);
      } else {
          authorities.addAll(authentication.getAuthorities());
      }
      return new Saml2Authentication(principal, authentication.getSaml2Response(), authorities);
    };
  }

  private RSAPrivateKey readPKCS8PrivateKey(File file) {
    try {
      String key = new String(Files.readAllBytes(file.toPath()), Charset.defaultCharset());
      String privateKeyPEM = key
        .replace("-----BEGIN PRIVATE KEY-----", "")
        .replaceAll(System.lineSeparator(), "")
        .replace("-----END PRIVATE KEY-----", "");
      byte[] encoded = Base64.getDecoder().decode(privateKeyPEM);
      KeyFactory keyFactory = KeyFactory.getInstance("RSA");
      PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
      return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
    }catch(Exception e){

    }
    return null;
  }

  private X509Certificate readX509Certificate(InputStream stream) {
    try{
      CertificateFactory cf = CertificateFactory.getInstance("X.509");
      return (X509Certificate) cf.generateCertificate(stream);
    } catch(Exception e){

    }
    return null;
  }
}
