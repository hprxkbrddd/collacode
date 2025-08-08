package com.collacode.auth.service;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <hr>
 * <h1>Custom OpenID Connect user service</h1>
 * Overrides {@code loadUser()}  method of {@link OidcUserService}
 */
@Service
public class CustomOidcUserService extends OidcUserService {
    /**
     * Loads user information from the OIDC provider and maps authorities.
     * <p>
     * Extends the default implementation by extracting roles from the "authorities" claim
     * in the ID token and converting them to {@link SimpleGrantedAuthority} objects.
     * If no authorities are found in the token, an empty authority set will be used.
     * </p>
     *
     * @param request the user request containing the access token and ID token
     * @return {@link DefaultOidcUser} populated with authorities from the token
     *
     * @implNote Expected authorities format in token: list of role strings (e.g. ["ROLE_USER", "ROLE_ADMIN"])
     */
    @Override
    public OidcUser loadUser(OidcUserRequest request){
        OidcUser user = super.loadUser(request);
        List<String> authorities = user.getAttribute("authorities");

        Set<SimpleGrantedAuthority> grantedAuthorities = new HashSet<>();
        if (authorities!=null)
            authorities.forEach(authority -> grantedAuthorities.add(new SimpleGrantedAuthority(authority)));

        return new DefaultOidcUser(grantedAuthorities, request.getIdToken(), user.getUserInfo());
    }
}
