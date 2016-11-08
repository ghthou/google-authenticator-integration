## Java Google Authenticator工具类

- 所需工具(app)
    - Google Authenticator(密钥保存在本地,如果删除或手机遗失无法恢复)
    - Authy(密钥保存在云端,删除,手机遗失可恢复)
- 项目环境
    - jdk 1.8.0_77
    - idea 2016.2.1
    - maven 3.3.9
- 依赖包
    - commons-codec 1.10
    - zxing-javase  3.2.1
- 使用说明

    1.生成`密钥`,并进行保存
     > GoogleAuthenticatorUtil.createSecretKey()

    2.根据`密钥`,`用户账户`,`公司名称`生成Google Authenticator二维码所需内容
     > GoogleAuthenticatorUtil.createGoogleAuthQRCodeData(`密钥`, `用户账户`, `公司名称`);
        
    3.使用二维码工具类生成一张图片(或者返回一个图片流)
     > QRCodeUtil.createQRCode(`步骤2返回的二维码内容`, `图片的保存地址`);
        
    4.打开生成的二维码图片,使用Google Authenticator进行扫描,此时会添加一条记录,并显示6位数字(即根据`密钥`计算出的`TOTP`)
    5.在代码中计算当前的`TOTP`值,并于Google Authenticator中的6位数字进行比较
     > GoogleAuthenticatorUtil.getTOTP(`密钥`, System.currentTimeMillis() / 1000 / 30)
- 其他说明

    - `TOTP`计算时需要用到时间,并且在30秒内结果一致.但客户端与服务端的时间往往有偏差,此时可使用时间偏移量进行解决,`TOTP`是根据自1970-01-01 00:00:00 至当前多少个30秒来计算的,那么在与前台传过来的TOTP进行比较时,除了与当前的30秒的个数进行比较外,同时可以根据设置的`时间偏移量timeExcursion`与当前30秒的个数-timeExcursion 至 与当前30秒的个数+timeExcursion 所计算TOTP进行比较,只要其中有一个与传过来的值相等,即可判断为通过,可使用以下方法
        > GoogleAuthenticatorUtil.verify(`密钥`,`用户提交的TOTP`)
    
    - 实际使用中应该将二维码展示在页面中供用户扫描,可使用以下方法,OutputStream可传入response.getOutputStream()
        > QRCodeUtil.writeToStream(`二维码内容`,`outputStream`)
    
        
    
        


    



