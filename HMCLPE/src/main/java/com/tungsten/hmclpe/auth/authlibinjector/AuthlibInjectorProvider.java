package com.tungsten.hmclpe.auth.authlibinjector;

import static com.tungsten.hmclpe.utils.io.NetworkUtils.toURL;

import com.tungsten.hmclpe.auth.AuthenticationException;
import com.tungsten.hmclpe.auth.yggdrasil.YggdrasilProvider;
import com.tungsten.hmclpe.utils.gson.UUIDTypeAdapter;

import java.net.URL;
import java.util.UUID;

public class AuthlibInjectorProvider implements YggdrasilProvider {

    private final String apiRoot;

    public AuthlibInjectorProvider(String apiRoot) {
        this.apiRoot = apiRoot;
    }

    @Override
    public URL getAuthenticationURL() throws AuthenticationException {
        return toURL(apiRoot + "authserver/authenticate");
    }

    @Override
    public URL getRefreshmentURL() throws AuthenticationException {
        return toURL(apiRoot + "authserver/refresh");
    }

    @Override
    public URL getValidationURL() throws AuthenticationException {
        return toURL(apiRoot + "authserver/validate");
    }

    @Override
    public URL getInvalidationURL() throws AuthenticationException {
        return toURL(apiRoot + "authserver/invalidate");
    }

    @Override
    public URL getSkinUploadURL(UUID uuid) throws UnsupportedOperationException {
        return toURL(apiRoot + "api/user/profile/" + UUIDTypeAdapter.fromUUID(uuid) + "/skin");
    }

    @Override
    public URL getProfilePropertiesURL(UUID uuid) throws AuthenticationException {
        return toURL(apiRoot + "sessionserver/session/minecraft/profile/" + UUIDTypeAdapter.fromUUID(uuid));
    }

    @Override
    public String toString() {
        return apiRoot;
    }
}