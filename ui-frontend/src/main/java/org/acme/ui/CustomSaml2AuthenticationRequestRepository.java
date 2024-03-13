package org.acme.ui;

import org.springframework.security.saml2.provider.service.authentication.AbstractSaml2AuthenticationRequest;
import org.springframework.security.saml2.provider.service.web.HttpSessionSaml2AuthenticationRequestRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class CustomSaml2AuthenticationRequestRepository 
        extends HttpSessionSaml2AuthenticationRequestRepository {
    
    private static final String DEFAULT_SAML2_AUTHN_REQUEST_ATTR_NAME = HttpSessionSaml2AuthenticationRequestRepository.class
		.getName()
		.concat(".SAML2_AUTHN_REQUEST");

	private String saml2AuthnRequestAttributeName = DEFAULT_SAML2_AUTHN_REQUEST_ATTR_NAME;

	@Override
	public AbstractSaml2AuthenticationRequest loadAuthenticationRequest(HttpServletRequest request) {
		HttpSession httpSession = request.getSession(false);
		if (httpSession == null) {
			return null;
		}
		return (AbstractSaml2AuthenticationRequest) httpSession.getAttribute(this.saml2AuthnRequestAttributeName);
	}

	@Override
	public void saveAuthenticationRequest(AbstractSaml2AuthenticationRequest authenticationRequest,
			HttpServletRequest request, HttpServletResponse response) {
		if (authenticationRequest == null) {
			removeAuthenticationRequest(request, response);
			return;
		}
		HttpSession httpSession = request.getSession();
		httpSession.setAttribute(this.saml2AuthnRequestAttributeName, authenticationRequest);
	}

	@Override
	public AbstractSaml2AuthenticationRequest removeAuthenticationRequest(HttpServletRequest request,
			HttpServletResponse response) {
		AbstractSaml2AuthenticationRequest authenticationRequest = loadAuthenticationRequest(request);
		if (authenticationRequest == null) {
			return null;
		}
		HttpSession httpSession = request.getSession();
		httpSession.removeAttribute(this.saml2AuthnRequestAttributeName);
		return authenticationRequest;
	}
}
