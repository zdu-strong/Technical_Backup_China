package com.springboot.project.service;

import com.fasterxml.uuid.Generators;
import java.util.Date;
import org.springframework.stereotype.Service;
import com.springboot.project.entity.*;

@Service
public class TokenService extends BaseService {

    public void createTokenEntity(String jwtId, String userId, String privateKeyOfRSA) {
        var user = this.UserEntity().where(s -> s.getId().equals(userId)).getOnlyValue();

        var tokenEntity = new TokenEntity();
        tokenEntity.setId(Generators.timeBasedGenerator().generate().toString());
        tokenEntity.setJwtId(jwtId);
        tokenEntity.setPrivateKeyOfRSA(privateKeyOfRSA);
        tokenEntity.setUser(user);
        tokenEntity.setCreateDate(new Date());
        tokenEntity.setUpdateDate(new Date());
        this.persist(tokenEntity);
    }

    public String getPrivateKeyOfRSAOfToken(String jwtId) {
        var privateKeyOfRSA = this.TokenEntity().where(s -> s.getJwtId().equals(jwtId))
                .select(s -> s.getPrivateKeyOfRSA()).getOnlyValue();
        return privateKeyOfRSA;
    }

    public void deleteTokenEntity(String jwtId) {
        var tokenEntityOptional = this.TokenEntity().where(s -> s.getJwtId().equals(jwtId)).findOne();
        if (!tokenEntityOptional.isPresent()) {
            return;
        }
        var tokenEntity = tokenEntityOptional.get();
        this.remove(tokenEntity);
    }

}
