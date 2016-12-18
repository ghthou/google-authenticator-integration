package z.study.googleAuthenticator.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;

import z.study.googleAuthenticator.util.GoogleAuthenticatorUtils;
import z.study.googleAuthenticator.util.QRCodeUtils;

/**
 * 2016/12/6 14:05.
 * 描述:Google Authenticator 前台示例controller.
 */
public class GoogleAuthDemoController {

	/**
	 * 生成包含用户信息,密钥的二维码图片
	 */
	@RequestMapping("/googleAuthQrCode")
	public void googleAuthQrCode(HttpServletResponse response) {
		//服务名称(一般定义为常量) 如 Google Github 印象笔记 等(不参与运算,只是为了与其他服务作区分)
		String issuer = "测试服务";
		//获取用户名称(从数据库或者缓存),可以是登录名,邮箱,手机(不参与运算,只是为了与其他服务作区分)
		String account = "example@domain.com";
		//生成密钥,并保存到数据库
		String secretKey = GoogleAuthenticatorUtils.createSecretKey();
		//生成二维码信息
		String googleAuthQRCodeData = GoogleAuthenticatorUtils.createGoogleAuthQRCodeData(secretKey, account, issuer);

		//返回二维码图片流
		ServletOutputStream outputStream = null;
		try {
			outputStream = response.getOutputStream();
			QRCodeUtils.writeToStream(googleAuthQRCodeData, outputStream);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 绑定时 Google Authenticator 的校验
	 */
	@RequestMapping("/bindVerify")
	public Map<String, Object> bindVerify(String code) {
		Map<String, Object> result = new HashMap<>();
		//根据用户信息从数据库中获取保存时的密钥
		String secretKey = "sjmig7qk644fq5cfmrxg4vcbcasirg2i";
		boolean success = GoogleAuthenticatorUtils.verify(secretKey, code);
		if (success) {
			//设置用户开启二步验证,更新用户信息
			//生成备用验证码,保存到数据库,同时返回到前台显示,并提示用户进行保存
			//在用户手机丢失后,或者APP,验证信息被误删,可以通过使用备用验证码的方式进行登录
		}
		result.put("success", success);
		return result;
	}

	/**
	 * 开启 Google Authenticator 服务的用户备用登录
	 */
	@RequestMapping("/loginBackupVerify")
	public Map<String, Object> loginBackupVerify(String code) {
		Map<String, Object> result = new HashMap<>();
		//根据用户id获取备用验证码信息
		String backupCodes = "bcasjgg4vm44,qc2ifk6sjmig,744cbq5cfmrx";
		boolean success = false;
		for (String backupCode : backupCodes.split(",")) {
			if (backupCode.equals(code)) {
				success = true;
				//同时为了安全性,备用验证码在验证成功后应该从数据库中删除
			}
		}
		result.put("success", success);
		return result;
	}

	/**
	 * 登录时 Google Authenticator 的校验
	 */
	@RequestMapping("/loginVerify")
	public Map<String, Object> loginVerify(String code) {
		Map<String, Object> result = new HashMap<>();
		//根据用户信息从数据库中获取保存时的密钥
		String secretKey = "sjmig7qk644cbcasirg2ifq5cfmrxg4v";
		boolean success = GoogleAuthenticatorUtils.verify(secretKey, code);
		if (success) {
			//设置登录cookie,session,缓存等
		}
		result.put("success", success);
		return result;
	}
}
