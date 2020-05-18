package com.github.ghthou.googleauthenticator.controller;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.text.StringSubstitutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.ghthou.googleauthenticator.util.GoogleAuthenticatorUtils;
import com.github.ghthou.googleauthenticator.util.QRCodeUtils;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Lists;

import lombok.SneakyThrows;

@RestController
public class GoogleAuthenticatorIntegrationController {

    /** 服务名称,如 Google Github 等(不参与运算,只是为了与其他服务作区分) */
    public static final String ISSUER = "测试服务";

    /** 测试时使用的密钥 */
    public static final String TEST_SECRET_KEY = "cy3rf3lifuuoydggqzydwmsb5wmdmvxg";

    /**
     * 测试相关信息
     */
    @GetMapping
    public String info() {
        // 为了方便测试,使用测试密钥
        String secretKey = TEST_SECRET_KEY;

        List<LocalDateTime> dateList = Lists.newArrayList();
        LocalDateTime now = LocalDateTime.now();
        // 当前时间0秒
        LocalDateTime begin = now.withSecond(0);
        // 当前时间往后10分钟
        LocalDateTime end = begin.plusMinutes(10);
        for (LocalDateTime d = begin; d.isBefore(end); d = d.plusSeconds(30)) {
            dateList.add(d);
        }
        String tableStr = dateList.stream().map(dateTime -> {
            String format1 = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String totpCode = GoogleAuthenticatorUtils
                    .generateTOTP(secretKey, dateTime.toInstant(ZoneOffset.ofHours(8)).toEpochMilli() / 1000 / 30);
            return format1 + "&emsp;" + totpCode;
        }).collect(Collectors.joining("<br>"));

        String format = "密钥:&emsp;${secret}<br><br>TOTP 一次性密码列表<br>";
        Builder<String, String> mapBuilder = ImmutableMap.builder();
        mapBuilder.put("secret", secretKey);
        return StringSubstitutor.replace(format, mapBuilder.build()) + tableStr;
    }

    /**
     * 生成 Google Authenticator Key Uri 二维码
     */
    @SneakyThrows
    @GetMapping("/qr_code")
    public void qrCode(HttpServletResponse response) {
        // 获取用户名称(从数据库或者缓存),可以是登录名,邮箱,手机(不参与运算,只是为了与其他服务作区分)
        String account = "example@domain.com";
        // 生成密钥,并保存到数据库
        // String secretKey = GoogleAuthenticatorUtils.createSecretKey();
        // 为了方便测试,使用测试密钥
        String secretKey = TEST_SECRET_KEY;
        // 生成 Key Uri
        String keyUri = GoogleAuthenticatorUtils.createKeyUri(secretKey, account, ISSUER);

        // 根据 keyUri 生成二维码图片
        try (ServletOutputStream outputStream = response.getOutputStream()) {
            QRCodeUtils.writeToStream(keyUri, outputStream);
        }
    }

    /**
     * 绑定验证
     *
     * @param totpCode TOTP 一次性密码
     */
    @PostMapping("/bind_verification")
    public Boolean bindVerification(String totpCode) {
        // 根据登录用户信息获取密钥
        // String secretKey = 从数据库中获取;
        // 为了方便测试,使用测试密钥
        String secretKey = TEST_SECRET_KEY;
        if (GoogleAuthenticatorUtils.verification(secretKey, totpCode)) {
            // 设置用户开启二步验证,更新用户信息
            // 生成备用验证码,保存到数据库,同时返回到前台显示,并提示用户进行保存.在用户手机丢失后,或者 APP 验证信息被误删,可以通过使用备用验证码的方式进行登录
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     * 登录
     */
    @PostMapping("/login")
    public Boolean login(String userName, String passWord, String totpCode) {
        // 首先进行密码校验
        // 然后校验 TOTP 一次性密码
        // 为了方便测试,使用测试密钥
        String secretKey = TEST_SECRET_KEY;
        return GoogleAuthenticatorUtils.verification(secretKey, totpCode);
    }

}
