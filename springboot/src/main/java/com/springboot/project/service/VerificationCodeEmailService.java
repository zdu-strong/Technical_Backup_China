package com.springboot.project.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import com.fasterxml.uuid.Generators;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.springboot.project.common.database.JPQLFunction;
import com.springboot.project.entity.VerificationCodeEmailEntity;
import com.springboot.project.model.VerificationCodeEmailModel;
import com.springboot.project.properties.StorageRootPathProperties;

@Service
public class VerificationCodeEmailService extends BaseService {

    @Autowired
    private StorageRootPathProperties storageRootPathProperties;

    public VerificationCodeEmailModel createVerificationCodeEmail(String email) {

        var minVerificationCodeLength = 6;
        var verificationCodeLength = minVerificationCodeLength;

        {
            var now = new Date();
            var timeZone = this.timeZoneUtils.getTimeZoneOfUTC();
            Duration tempDuration = Duration.ofDays(31);
            var simpleDateFormat = new SimpleDateFormat("yyyy-MM");
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
            var nowString = simpleDateFormat.format(now);

            var beforeCalendar = Calendar.getInstance();
            beforeCalendar.setTime(now);
            beforeCalendar.add(Calendar.MILLISECOND, Long.valueOf(0 - tempDuration.toMillis()).intValue());
            Date beforeDate = beforeCalendar.getTime();

            var afterCalendar = Calendar.getInstance();
            afterCalendar.setTime(now);
            afterCalendar.add(Calendar.MILLISECOND, Long.valueOf(tempDuration.toMillis()).intValue());
            Date afterDate = afterCalendar.getTime();

            var retryCount = this.VerificationCodeEmailEntity()
                    .where(s -> s.getEmail().equals(email))
                    .where(s -> beforeDate.before(s.getCreateDate()))
                    .where(s -> afterDate.after(s.getCreateDate()))
                    .where(s -> JPQLFunction
                            .formatDateAsYearMonth(s.getCreateDate(), timeZone)
                            .equals(nowString))
                    .count();
            if (retryCount > 1000 && verificationCodeLength < 12) {
                verificationCodeLength = 12;
            }
        }

        {

            var now = new Date();
            var timeZone = this.timeZoneUtils.getTimeZoneOfUTC();
            Duration tempDuration = Duration.ofDays(1);
            var simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
            var nowString = simpleDateFormat.format(now);

            var beforeCalendar = Calendar.getInstance();
            beforeCalendar.setTime(now);
            beforeCalendar.add(Calendar.MILLISECOND, Long.valueOf(0 - tempDuration.toMillis()).intValue());
            Date beforeDate = beforeCalendar.getTime();

            var afterCalendar = Calendar.getInstance();
            afterCalendar.setTime(now);
            afterCalendar.add(Calendar.MILLISECOND, Long.valueOf(tempDuration.toMillis()).intValue());
            Date afterDate = afterCalendar.getTime();

            var retryCount = this.VerificationCodeEmailEntity()
                    .where(s -> s.getEmail().equals(email))
                    .where(s -> beforeDate.before(s.getCreateDate()))
                    .where(s -> afterDate.after(s.getCreateDate()))
                    .where(s -> JPQLFunction
                            .formatDateAsYearMonthDay(s.getCreateDate(), timeZone)
                            .equals(nowString))
                    .count();
            if (retryCount > 0 && verificationCodeLength < 9) {
                verificationCodeLength = 9;
            } else if (retryCount == 0) {
                verificationCodeLength = minVerificationCodeLength;
            }
        }

        String verificationCode = "";

        for (var i = verificationCodeLength; i > 0; i--) {
            verificationCode += String.valueOf(new BigDecimal(Math.random()).multiply(new BigDecimal(10))
                    .setScale(0, RoundingMode.FLOOR).longValue());
        }

        if (this.storageRootPathProperties.isTestEnviroment()) {
            verificationCode = "123456";
        }

        var verificationCodeEmailEntity = new VerificationCodeEmailEntity();
        verificationCodeEmailEntity.setId(Generators.timeBasedGenerator().generate().toString());
        verificationCodeEmailEntity.setEmail(email);
        verificationCodeEmailEntity.setVerificationCode(verificationCode);
        verificationCodeEmailEntity.setIsDeleted(false);
        verificationCodeEmailEntity.setCreateDate(new Date());
        verificationCodeEmailEntity.setUpdateDate(new Date());
        this.persist(verificationCodeEmailEntity);

        return this.verificationCodeEmailFormatter.format(verificationCodeEmailEntity);
    }

    public boolean isFirstOnTheSecondOfVerificationCodeEmail(String id) {
        var verificationCodeEmailEntity = this.VerificationCodeEmailEntity().where(s -> s.getId().equals(id))
                .getOnlyValue();
        var email = verificationCodeEmailEntity.getEmail();
        var createDate = verificationCodeEmailEntity.getCreateDate();

        var timeZone = this.timeZoneUtils.getTimeZoneOfUTC();
        Duration tempDuration = Duration.ofSeconds(1);
        var simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
        var createDateString = simpleDateFormat.format(createDate);

        var beforeCalendar = Calendar.getInstance();
        beforeCalendar.setTime(verificationCodeEmailEntity.getCreateDate());
        beforeCalendar.add(Calendar.MILLISECOND, Long.valueOf(0 - tempDuration.toMillis()).intValue());
        Date beforeDate = beforeCalendar.getTime();

        var afterCalendar = Calendar.getInstance();
        afterCalendar.setTime(verificationCodeEmailEntity.getCreateDate());
        afterCalendar.add(Calendar.MILLISECOND, Long.valueOf(tempDuration.toMillis()).intValue());
        Date afterDate = afterCalendar.getTime();

        var isFirstOnTheSecond = this.VerificationCodeEmailEntity()
                .where(s -> s.getEmail().equals(email))
                .where(s -> beforeDate.before(s.getCreateDate()))
                .where(s -> afterDate.after(s.getCreateDate()))
                .where(s -> JPQLFunction
                        .formatDateAsYearMonthDayHourMinuteSecond(s.getCreateDate(), timeZone)
                        .equals(createDateString))
                .sortedBy(s -> s.getId())
                .sortedBy(s -> s.getCreateDate())
                .select(s -> s.getId())
                .findFirst()
                .filter(s -> s.equals(id))
                .isPresent();

        return isFirstOnTheSecond;
    }

    public void checkVerificationCodeEmailIsNotDeleted(VerificationCodeEmailModel verificationCodeEmailModel) {
        var id = verificationCodeEmailModel.getId();
        var verificationCodeEmailEntity = this.VerificationCodeEmailEntity().where(s -> s.getId().equals(id))
                .getOnlyValue();
        if (verificationCodeEmailEntity.getIsDeleted()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The verification code of email " + verificationCodeEmailModel.getEmail() + " is wrong");
        }

        verificationCodeEmailEntity.setIsDeleted(true);
        this.merge(verificationCodeEmailEntity);
    }

    public void checkVerificationCodeEmailIsPassed(VerificationCodeEmailModel verificationCodeEmailModel) {
        if (StringUtils.isBlank(verificationCodeEmailModel.getVerificationCode())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The verification code of email " + verificationCodeEmailModel.getEmail() + " cannot be empty");
        }

        var id = verificationCodeEmailModel.getId();

        var verificationCodeEmailEntityOptional = this.VerificationCodeEmailEntity().where(s -> s.getId().equals(id))
                .findOne();
        if (!verificationCodeEmailEntityOptional.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The verification code of email " + verificationCodeEmailModel.getEmail() + " is wrong");
        }

        var verificationCodeEmailEntity = verificationCodeEmailEntityOptional.get();

        if (!verificationCodeEmailEntity.getEmail().equals(verificationCodeEmailModel.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The verification code of email " + verificationCodeEmailModel.getEmail() + " is wrong");
        }

        if (!verificationCodeEmailEntity.getVerificationCode()
                .equals(verificationCodeEmailModel.getVerificationCode())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The verification code of email " + verificationCodeEmailModel.getEmail() + " is wrong");
        }

    }

}
