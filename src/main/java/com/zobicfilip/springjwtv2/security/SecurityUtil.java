package com.zobicfilip.springjwtv2.security;

import javax.crypto.SecretKey;
import java.util.Date;

public interface SecurityUtil {
    String ACCESS_BODY_TOKEN_NAME = "accessToken";
    String REFRESH_BODY_TOKEN_NAME = "refreshToken";
    String ACCESS_HEADER_TOKEN_NAME = "AccessToken";
    String REFRESH_HEADER_TOKEN_NAME = "RefreshToken";
    
    long getAccessLifespan();
    long getRefreshLifespan();
    SecretKey getKey();
    SecretKey getKey(String password);
    Date getCutoffDate();
}
