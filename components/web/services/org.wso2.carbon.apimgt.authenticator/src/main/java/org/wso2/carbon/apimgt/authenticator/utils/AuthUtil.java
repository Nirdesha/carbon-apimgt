/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.apimgt.authenticator.utils;

import org.wso2.carbon.apimgt.authenticator.constants.AuthenticatorConstants;
import org.wso2.carbon.apimgt.authenticator.dto.ErrorDTO;
import org.wso2.carbon.apimgt.core.exception.ErrorHandler;
import org.wso2.carbon.apimgt.core.models.AccessTokenRequest;
import org.wso2.carbon.apimgt.core.models.OAuthAppRequest;
import org.wso2.carbon.apimgt.core.models.OAuthApplicationInfo;
import org.wso2.msf4j.Request;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.Cookie;
/**
 * This method authenticate the user.
 *
 */
public class AuthUtil {

    /**
     * This method authenticate the user.
     *
     */
    public static String getHttpOnlyCookieHeader(Cookie cookie) {
        return cookie + "; HttpOnly";
    }

    private static Map<String, Map<String, String>> consumerKeySecretMap = new HashMap<>();

    private static List<String> roleList;

    public static String getAppContext(Request request) {
        //TODO this method should provide uuf app context. Consider the scenarios of reverse proxy as well.
        return "/" + request.getProperty("REQUEST_URL").toString().split("/")[1];
    }

    public static Map<String, Map<String, String>> getConsumerKeySecretMap() {
        return consumerKeySecretMap;
    }


    /**
     * This method is used to generate access token request to login for uuf apps.
     *
     */
    public static AccessTokenRequest createAccessTokenRequest(String username, String password, String grantType,
            String refreshToken, Long validityPeriod, String[] scopes, String clientId, String clientSecret) {

        AccessTokenRequest tokenRequest = new AccessTokenRequest();
        tokenRequest.setClientId(clientId);
        tokenRequest.setClientSecret(clientSecret);
        tokenRequest.setGrantType(grantType);
        tokenRequest.setRefreshToken(refreshToken);
        tokenRequest.setResourceOwnerUsername(username);
        tokenRequest.setResourceOwnerPassword(password);
        tokenRequest.setScopes(scopes);
        tokenRequest.setValidityPeriod(validityPeriod);
        return tokenRequest;

    }

    /**
     * This method will parse json String and set properties in  OAuthApplicationInfo object.
     * Further it will initiate new OauthAppRequest  object and set applicationInfo object as its own property.
     * @param clientName client Name.
     * @param callbackURL This is the call back URL of the application
     * @param tokenScope The token scope
     * @param clientId The ID of the client
     * @return appRequest object of OauthAppRequest.
     */
    public static OAuthAppRequest createOauthAppRequest(String clientName, String clientId, String callbackURL,
            String tokenScope) {

        OAuthAppRequest appRequest = new OAuthAppRequest();
        OAuthApplicationInfo authApplicationInfo = new OAuthApplicationInfo();
        authApplicationInfo.setClientName(clientName);
        authApplicationInfo.setCallbackUrl(callbackURL);
        //authApplicationInfo.addParameter(KeyManagerConstants.OAUTH_CLIENT_TOKEN_SCOPE, tokenScopeList);
        authApplicationInfo.setClientId(clientId);
        authApplicationInfo.setAppOwner(clientId);
        authApplicationInfo.setGrantTypes(Arrays.asList("password", "refresh_token"));
        //set applicationInfo object
        appRequest.setOAuthApplicationInfo(authApplicationInfo);
        return appRequest;
    }

    public static List<String> getRoleList() {
        return roleList;
    }

    public static void setRoleList(List<String> roleList) {
        AuthUtil.roleList = roleList;
    }

    /**
     * Returns a generic errorDTO
     *
     * @param errorHandler The error handler object.
     * @return A generic errorDTO with the specified details
     */
    public static ErrorDTO getErrorDTO(ErrorHandler errorHandler, HashMap<String, String> paramList) {
        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setCode(errorHandler.getErrorCode());
        errorDTO.setMoreInfo(paramList);
        errorDTO.setMessage(errorHandler.getErrorMessage());
        errorDTO.setDescription(errorHandler.getErrorDescription());
        return errorDTO;
    }

    /**
     * @param cookie Cookies  header which contains the access token
     * @return partial access token present in the cookie.
     */
    public static String extractPartialAccessTokenFromCookie(String cookie) {
        cookie = cookie.trim();
        String[] cookies = cookie.split(";");
        String token = Arrays.stream(cookies).filter(name -> name.contains(AuthenticatorConstants.REFRESH_TOKEN))
                .findFirst().get();
        if (token.split("=").length == 2) {
            return token.split("=")[1];
        }
        return null;
    }



}