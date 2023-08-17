package com.springboot.project.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.jinq.jpa.JPAJinqStream;
import org.jinq.jpa.JinqJPAStreamProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springboot.project.common.TimeZoneUtil.TimeZoneUtil;
import com.springboot.project.common.database.JPQLFunction;
import com.springboot.project.common.storage.Storage;
import com.springboot.project.entity.*;
import com.springboot.project.format.FriendshipFormatter;
import com.springboot.project.format.LoggerFormatter;
import com.springboot.project.format.LongTermTaskFormatter;
import com.springboot.project.format.OrganizeFormatter;
import com.springboot.project.format.StorageSpaceFormatter;
import com.springboot.project.format.UserEmailFormatter;
import com.springboot.project.format.UserFormatter;
import com.springboot.project.format.UserMessageFormatter;
import com.springboot.project.format.VerificationCodeEmailFormatter;

@Service
@Transactional(rollbackFor = Throwable.class)
public abstract class BaseService {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    protected Storage storage;

    @Autowired
    protected StorageSpaceFormatter storageSpaceFormatter;

    @Autowired
    protected UserEmailFormatter userEmailFormatter;

    @Autowired
    protected UserFormatter userFormatter;

    @Autowired
    protected LongTermTaskFormatter longTermTaskFormatter;

    @Autowired
    protected OrganizeFormatter organizeFormatter;

    @Autowired
    protected UserMessageFormatter userMessageFormatter;

    @Autowired
    protected FriendshipFormatter friendshipFormatter;

    @Autowired
    protected LoggerFormatter loggerFormatter;

    @Autowired
    protected VerificationCodeEmailFormatter verificationCodeEmailFormatter;

    @Autowired
    private TimeZoneUtil timeZoneUtil;

    private JinqJPAStreamProvider jinqJPAStreamProvider;

    protected void persist(Object entity) {
        this.entityManager.persist(entity);
    }

    protected void merge(Object entity) {
        this.entityManager.merge(entity);
    }

    protected void remove(Object entity) {
        this.entityManager.remove(entity);
    }

    protected JPAJinqStream<StorageSpaceEntity> StorageSpaceEntity() {
        return this.streamAll(StorageSpaceEntity.class);
    }

    protected JPAJinqStream<EncryptDecryptEntity> EncryptDecryptEntity() {
        return this.streamAll(EncryptDecryptEntity.class);
    }

    protected JPAJinqStream<UserEmailEntity> UserEmailEntity() {
        return this.streamAll(UserEmailEntity.class);
    }

    protected JPAJinqStream<UserEntity> UserEntity() {
        return this.streamAll(UserEntity.class);
    }

    protected JPAJinqStream<LongTermTaskEntity> LongTermTaskEntity() {
        return this.streamAll(LongTermTaskEntity.class);
    }

    protected JPAJinqStream<OrganizeEntity> OrganizeEntity() {
        return this.streamAll(OrganizeEntity.class);
    }

    protected JPAJinqStream<UserMessageEntity> UserMessageEntity() {
        return this.streamAll(UserMessageEntity.class);
    }

    protected JPAJinqStream<TokenEntity> TokenEntity() {
        return this.streamAll(TokenEntity.class);
    }

    protected JPAJinqStream<FriendshipEntity> FriendshipEntity() {
        return this.streamAll(FriendshipEntity.class);
    }

    protected JPAJinqStream<LoggerEntity> LoggerEntity() {
        return this.streamAll(LoggerEntity.class);
    }

    protected JPAJinqStream<OrganizeShadowEntity> OrganizeShadowEntity() {
        return this.streamAll(OrganizeShadowEntity.class);
    }

    protected JPAJinqStream<OrganizeClosureEntity> OrganizeClosureEntity() {
        return this.streamAll(OrganizeClosureEntity.class);
    }

    protected JPAJinqStream<VerificationCodeEmailEntity> VerificationCodeEmailEntity() {
        return this.streamAll(VerificationCodeEmailEntity.class);
    }

    private <U> JPAJinqStream<U> streamAll(Class<U> entity) {
        if (this.jinqJPAStreamProvider == null) {
            synchronized (getClass()) {
                if (this.jinqJPAStreamProvider == null) {
                    JinqJPAStreamProvider jinqJPAStreamProvider = new JinqJPAStreamProvider(
                            entityManager.getMetamodel());
                    JPQLFunction.registerCustomSqlFunction(jinqJPAStreamProvider);
                    jinqJPAStreamProvider.setHint("automaticPageSize", 2);
                    jinqJPAStreamProvider.setHint("exceptionOnTranslationFail", true);
                    this.jinqJPAStreamProvider = jinqJPAStreamProvider;
                }
            }
        }
        return this.jinqJPAStreamProvider.streamAll(entityManager, entity);
    }

    protected String getTimeZone(String timeZone) {
        return this.timeZoneUtil.getTimeZone(timeZone);
    }

    protected String getTimeZoneOfUTC() {
        return this.timeZoneUtil.getTimeZoneOfUTC();
    }

}