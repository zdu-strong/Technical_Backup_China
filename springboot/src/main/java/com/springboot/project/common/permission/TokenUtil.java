package com.springboot.project.common.permission;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.uuid.Generators;
import com.springboot.project.service.EncryptDecryptService;
import com.springboot.project.service.TokenService;

@Component
public class TokenUtil {

    @Autowired
    private EncryptDecryptService encryptDecryptService;

    @Autowired
    private TokenService tokenService;

    public String generateAccessToken(String userId, String privateKeyOfRSA) {
        /* generate jwtId */
        var jwtId = Generators.timeBasedGenerator().generate().toString();

        var accessToken = JWT.create().withSubject(userId)
                .withIssuedAt(new Date())
                .withJWTId(jwtId)
                .sign(Algorithm.RSA512((RSAPublicKey) this.encryptDecryptService.getRSA().getPublicKey(),
                        (RSAPrivateKey) this.encryptDecryptService.getRSA().getPrivateKey()));

        this.tokenService.createTokenEntity(jwtId, userId, privateKeyOfRSA);

        return accessToken;
    }

    public String generateNewAccessToken(String accessToken, String privateKeyOfRSA) {
        /* generate jwtId */
        var jwtId = Generators.timeBasedGenerator().generate().toString();

        var decodedJWT = this.getDecodedJWTOfAccessToken(accessToken);
        var userId = decodedJWT.getSubject();

        String accessTokenOfNew = JWT.create().withSubject(userId)
                .withIssuedAt(new Date())
                .withJWTId(jwtId)
                .sign(Algorithm.RSA512((RSAPublicKey) this.encryptDecryptService.getRSA().getPublicKey(),
                        (RSAPrivateKey) this.encryptDecryptService.getRSA().getPrivateKey()));

        this.tokenService.createTokenEntity(jwtId, userId, privateKeyOfRSA);

        return accessTokenOfNew;
    }

    public String getAccessToken(HttpServletRequest request) {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.isNotBlank(authorization)) {
            String prefix = "Bearer ";
            if (authorization.startsWith(prefix)) {
                return authorization.substring(prefix.length());
            }
        }
        return "";
    }

    public DecodedJWT getDecodedJWTOfAccessToken(String accessToken) {
        var decodedJWT = JWT
                .require(Algorithm.RSA512((RSAPublicKey) this.encryptDecryptService.getRSA().getPublicKey(),
                        (RSAPrivateKey) this.encryptDecryptService.getRSA().getPrivateKey()))
                .build().verify(accessToken);
        this.tokenService.getPrivateKeyOfRSAOfToken(decodedJWT.getId());
        return decodedJWT;
    }

}
