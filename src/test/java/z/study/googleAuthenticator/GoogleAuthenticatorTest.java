package z.study.googleAuthenticator;

import com.google.zxing.WriterException;
import org.junit.Test;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class GoogleAuthenticatorTest {
    private static String account = "账户";//此值用于在GoogleAuthenticator显示,便于用户区分查找,与加密无关
    private static String issuer = "公司";//此值用于在GoogleAuthenticator显示,便于用户区分查找,与加密无关
    private static String secret = "6cwea3m6dat5r4zbkyfvqcn67ylraajg";

    @Test
    public void testQrCodeFile() throws IOException, WriterException {
        String secret = GoogleAuthenticatorUtil.createSecretKey();
        System.out.println(secret);
        //生成二维码数据
        String googleAuthQRCodeData = GoogleAuthenticatorUtil.createGoogleAuthQRCodeData(secret, account, issuer);
        QRCodeUtil.createQRCode(googleAuthQRCodeData, "qrcode.png");
    }

    @Test
    public void testVerify() {
        System.out.println(GoogleAuthenticatorUtil.verify(secret, "243294"));
    }

    @Test
    public void testSeeTOTP() throws InterruptedException {
        String temp = null;
        for (; ; ) {
            String totp = GoogleAuthenticatorUtil.getTOTP(secret, System.currentTimeMillis() / 1000 / 30);
            if (!totp.equals(temp)) {
                System.out.println(totp);
            }
            temp = totp;
            Thread.sleep(1000);
        }
    }


    @Test
    public void testMain() throws InterruptedException {
        ServletOutputStream stream = null;
        HttpServletResponse response = null;//在实际项目中 进行替换
        try {
            //生成密钥
            String secretKey = GoogleAuthenticatorUtil.createSecretKey();
            //实际使用中应该保存数据库
            System.out.println(secretKey);
            //生成二维码数据
            String googleAuthQRCodeData = GoogleAuthenticatorUtil.createGoogleAuthQRCodeData(secretKey, account, issuer);
            stream = response.getOutputStream();
            //输出二维码图片(也可以使用第三方平台二维码api,将googleAuthQRCodeData在前台img标签中进行拼接)
            QRCodeUtil.writeToStream(googleAuthQRCodeData, stream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}