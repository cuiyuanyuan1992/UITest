package com.qa.test;

import io.appium.java_client.AppiumDriver;
import io.qameta.allure.Attachment;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScreenshotListener extends TestListenerAdapter {
    public void onTestFailure(ITestResult iTestResult) {
        super.onTestFailure(iTestResult);
        BaseCase baseCase = (BaseCase) iTestResult.getInstance();
        AppiumDriver driver = baseCase.getDriver();
        // 获取屏幕截图
        byte[] byteArray =  takePhoto(driver);
        InputStream in = new ByteArrayInputStream(byteArray);

        // System.out.println(srcFile.getAbsolutePath().toString());
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_hhmmss");
        File location = new File("screenshots");
        String dest = iTestResult.getMethod().getRealClass().getSimpleName() + "." + iTestResult.getMethod().getMethodName();
        File targetFile =
                new File(location.getAbsolutePath() + File.separator + dest + "_" + dateFormat.format(new Date()) + ".png");
        System.out.println("截图位置：");
        System.out.println("----------------- file is " + targetFile.getPath());
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(targetFile);
            int len = 0;
            byte[] buf = new byte[1024];
            while ((len = in.read(buf)) != -1) {
                fos.write(buf, 0, len);
            }
            fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != fos) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Attachment("失败截图如下：")
    public byte[]  takePhoto(AppiumDriver driver){
        byte[] screenshotAs = ((TakesScreenshot)driver).getScreenshotAs(OutputType.BYTES);
        return screenshotAs;
    }
}
