package com.springboot.project.controller;

import java.io.ByteArrayOutputStream;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import com.alibaba.fastjson2.JSON;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.springboot.project.model.UserModel;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;

@RestController
public class AuthorizationController extends BaseController {

    @PostMapping("/sign_up")
    public ResponseEntity<?> signUp(@RequestBody UserModel userModel)
            throws InvalidKeySpecException, NoSuchAlgorithmException {
        var user = this.userService.getAccountForSignUp(userModel.getId());

        if (user.getHasRegistered()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The account has already been registered by someone else");
        }

        {
            var privateKeyOfRSA = (RSAPrivateKey) KeyFactory.getInstance("RSA")
                    .generatePrivate(new PKCS8EncodedKeySpec(
                            Base64.getDecoder().decode(user.getPrivateKeyOfRSA())));
            RSA rsa = new RSA(privateKeyOfRSA, null);
            var userId = rsa.decryptStr(userModel.getEmail(), KeyType.PrivateKey);
            if (!userModel.getId().equals(userId)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User ID verification failed");
            }
        }

        if (StringUtils.isBlank(userModel.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please fill in the password");
        }

        if (StringUtils.isBlank(userModel.getPublicKeyOfRSA())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please set the public key of RSA");
        }

        if (StringUtils.isBlank(userModel.getPrivateKeyOfRSA())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please set the public key of RSA");
        }

        if (StringUtils.isBlank(userModel.getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please fill in nickname");
        }

        if (userModel.getUsername().trim().length() != userModel.getUsername().length()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username cannot start or end with a space");
        }

        {
            for (var userEmail : userModel.getUserEmailList()) {
                if (StringUtils.isBlank(userEmail.getEmail())) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please enter your email");
                }

                if (!Pattern.matches("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$", userEmail.getEmail())) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is invalid");
                }

                this.userEmailService.checkEmailIsNotUsed(userEmail.getEmail());
                this.userEmailService.checkEmailVerificationCodeIsPassed(userEmail.getEmail(), user.getId(),
                        userEmail.getVerificationCode());
            }
        }

        this.userService.signUp(userModel);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/sign_in")
    public ResponseEntity<?> signIn(@RequestParam String userId, @RequestParam String password)
            throws InvalidKeySpecException, NoSuchAlgorithmException {
        var user = this.userService.getAccountForSignUp(userId);

        if (!user.getHasRegistered()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account does not exist");
        }

        UserModel userModel;
        {
            var publicKeyOfRSA = (RSAPublicKey) KeyFactory.getInstance("RSA")
                    .generatePublic(new X509EncodedKeySpec(
                            Base64.getDecoder().decode(user.getPublicKeyOfRSA())));
            RSA rsa = new RSA(null, publicKeyOfRSA);
            var passwordString = rsa.decryptStr(password, KeyType.PublicKey);
            userModel = JSON.parseObject(passwordString, UserModel.class);
        }

        if (!userModel.getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrect password");
        }

        if (!userModel.getPassword().equals(user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrect password");
        }

        this.userSignInVerificationCodeService.checkUserSignInVerificationCode(userId,
                userModel.getUserSignInVerificationCode());

        var accessToken = this.tokenUtil.generateAccessToken(user.getId(), userModel.getPrivateKeyOfRSA());

        return ResponseEntity.ok(accessToken);
    }

    @PostMapping("/sign_out")
    public ResponseEntity<?> signOut() {
        if (this.permissionUtil.isSignIn(request)) {
            var jwtId = this.tokenUtil.getDecodedJWTOfAccessToken(this.tokenUtil.getAccessToken(request)).getId();
            this.tokenService.deleteTokenEntity(jwtId);
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/get_user_info")
    public ResponseEntity<?> getUserInfo() {
        this.permissionUtil.checkIsSignIn(request);

        var userId = this.permissionUtil.getUserId(request);
        var user = this.userService.getAccountForSignUp(userId);

        if (!user.getHasRegistered()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account does not exist");
        }

        user.setEmail(null).setPassword(null);
        var jwtId = this.tokenUtil.getDecodedJWTOfAccessToken(this.tokenUtil.getAccessToken(request)).getId();
        var privateKeyOfRSA = this.tokenService.getPrivateKeyOfRSAOfToken(jwtId);
        user.setPrivateKeyOfRSA(privateKeyOfRSA);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/sign_up/get_new_account")
    public ResponseEntity<?> getNewAccount() throws InvalidKeySpecException, NoSuchAlgorithmException {
        var user = this.userService.getNewAccountForSignUp();
        return ResponseEntity.ok(user);
    }

    @PostMapping("/sign_in/get_account")
    public ResponseEntity<?> getAccountForSignIn(@RequestParam String userId) {

        if (StringUtils.isBlank(userId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please fill in the account");
        }

        this.userService.checkExistAccount(userId);

        var user = this.userService.getAccountForSignUp(userId);
        user.setPassword(null);
        user.setUserEmailList(null);
        user.setEmail(null);
        user.setUsername(null);
        user.setPublicKeyOfRSA(null);

        {
            var userSignInVerificationCodeModel = this.userSignInVerificationCodeService
                    .createVerificationCodeForSignIn(user.getId());
            user.setUserSignInVerificationCode(userSignInVerificationCodeModel);
        }

        return ResponseEntity.ok(user);
    }

    @PostMapping("/sign_up/send_verification_code")
    public ResponseEntity<?> sendVerificationCode(@RequestBody UserModel userModel)
            throws InvalidKeySpecException, NoSuchAlgorithmException {
        var user = this.userService.getAccountForSignUp(userModel.getId());

        {
            for (var userEmail : userModel.getUserEmailList()) {
                if (user.getHasRegistered()) {
                    var publicKeyOfRSA = (RSAPublicKey) KeyFactory.getInstance("RSA")
                            .generatePublic(new X509EncodedKeySpec(
                                    Base64.getDecoder().decode(user.getPublicKeyOfRSA())));
                    RSA rsa = new RSA(null, publicKeyOfRSA);
                    var userId = rsa.decryptStr(userEmail.getVerificationCode(), KeyType.PublicKey);
                    if (!userModel.getId().equals(userId)) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User ID verification failed");
                    }
                } else {
                    var privateKeyOfRSA = (RSAPrivateKey) KeyFactory.getInstance("RSA")
                            .generatePrivate(new PKCS8EncodedKeySpec(
                                    Base64.getDecoder().decode(user.getPrivateKeyOfRSA())));
                    RSA rsa = new RSA(privateKeyOfRSA, null);
                    var userId = rsa.decryptStr(userEmail.getVerificationCode(), KeyType.PrivateKey);
                    if (!userModel.getId().equals(userId)) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User ID verification failed");
                    }
                }

                if (StringUtils.isBlank(userEmail.getEmail())) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please enter your email");
                }

                if (!Pattern.matches("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$", userEmail.getEmail())) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is invalid");
                }

            }
        }

        for (var userEmail : userModel.getUserEmailList()) {
            var verificationCode = "";
            while (true) {
                if (verificationCode.length() >= 4) {
                    break;
                }
                Integer number = Double.valueOf(Math.floor(Math.random() * 10)).intValue();
                if (verificationCode.length() == 0 && number <= 0) {
                    continue;
                }
                verificationCode = verificationCode + number.toString();
            }

            this.userEmailService.createUserEmailWithVerificationCode(userEmail.getEmail(), user.getId(),
                    verificationCode);

            this.authorizationEmailUtil.sendVerificationCode(userEmail.getEmail(), verificationCode);
        }

        return ResponseEntity.ok().build();
    }

    @GetMapping("/sign_in/alipay/generate_qr_code")
    public ResponseEntity<?> generateQrCode() throws Throwable {
        var url = new URIBuilder("https://openauth.alipay.com/oauth2/publicAppAuthorize.htm")
                .setParameter("app_id", "2021002177648626").setParameter("scope", "auth_user")
                .setParameter("redirect_uri", "https://kame-sennin.com/abc").setParameter("state", "init").build();
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(url.toString(), BarcodeFormat.QR_CODE, 200, 200);
        try (ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream()) {
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
            byte[] pngData = pngOutputStream.toByteArray();
            String imageData = Base64.getEncoder().encodeToString(pngData);
            var imageUrl = "data:image/png;base64," + imageData;
            return ResponseEntity.ok(imageUrl);
        }
    }

}
