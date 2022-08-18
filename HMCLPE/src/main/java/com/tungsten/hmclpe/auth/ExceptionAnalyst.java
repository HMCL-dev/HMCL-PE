package com.tungsten.hmclpe.auth;

public class ExceptionAnalyst {

    /*
    public static String localizeErrorMessage(Exception exception) {
        if (exception instanceof NoCharacterException) {
            return i18n("account.failed.no_character");
        } else if (exception instanceof ServerDisconnectException) {
            return i18n("account.failed.connect_authentication_server");
        } else if (exception instanceof ServerResponseMalformedException) {
            return i18n("account.failed.server_response_malformed");
        } else if (exception instanceof RemoteAuthenticationException) {
            RemoteAuthenticationException remoteException = (RemoteAuthenticationException) exception;
            String remoteMessage = remoteException.getRemoteMessage();
            if ("ForbiddenOperationException".equals(remoteException.getRemoteName()) && remoteMessage != null) {
                if (remoteMessage.contains("Invalid credentials")) {
                    return i18n("account.failed.invalid_credentials");
                } else if (remoteMessage.contains("Invalid token")) {
                    return i18n("account.failed.invalid_token");
                } else if (remoteMessage.contains("Invalid username or password")) {
                    return i18n("account.failed.invalid_password");
                } else {
                    return remoteMessage;
                }
            } else if ("ResourceException".equals(remoteException.getRemoteName()) && remoteMessage != null) {
                if (remoteMessage.contains("The requested resource is no longer available")) {
                    return i18n("account.failed.migration");
                } else {
                    return remoteMessage;
                }
            }
            return exception.getMessage();
        } else if (exception instanceof InvalidSkinException) {
            return i18n("account.skin.invalid_skin");
        } else if (exception instanceof MicrosoftService.XboxAuthorizationException) {
            long errorCode = ((MicrosoftService.XboxAuthorizationException) exception).getErrorCode();
            if (errorCode == MicrosoftService.XboxAuthorizationException.ADD_FAMILY) {
                return i18n("account.methods.microsoft.error.add_family");
            } else if (errorCode == MicrosoftService.XboxAuthorizationException.COUNTRY_UNAVAILABLE) {
                return i18n("account.methods.microsoft.error.country_unavailable");
            } else if (errorCode == MicrosoftService.XboxAuthorizationException.MISSING_XBOX_ACCOUNT) {
                return i18n("account.methods.microsoft.error.missing_xbox_account");
            } else {
                return i18n("account.methods.microsoft.error.unknown", errorCode);
            }
        } else if (exception instanceof MicrosoftService.NoMinecraftJavaEditionProfileException) {
            return i18n("account.methods.microsoft.error.no_character");
        } else if (exception instanceof MicrosoftService.NoXuiException) {
            return i18n("account.methods.microsoft.error.add_family_probably");
        } else if (exception.getClass() == AuthenticationException.class) {
            return exception.getLocalizedMessage();
        } else {
            return exception.getClass().getName() + ": " + exception.getLocalizedMessage();
        }
    }

     */
}
