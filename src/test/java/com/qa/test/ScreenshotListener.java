package com.qa.test;

import io.appium.java_client.AppiumDriver;
import io.qameta.allure.Attachment;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScreenshotListener extends TestListenerAdapter {
    public void onTestFailure(ITestResult iTestResult) {
        super.onTestFailure(iTestResult);
        BaseCase baseCase = (BaseCase) iTestResult.getInstance();
        AppiumDriver driver = baseCase.getDriver();
        // 获取屏幕截图
        File srcFile =  takePhoto(driver);
        // System.out.println(srcFile.getAbsolutePath().toString());
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_hhmmss");
        File location = new File("screenshots");
        String dest = iTestResult.getMethod().getRealClass().getSimpleName() + "." + iTestResult.getMethod().getMethodName();
        File targetFile =
                new File(location.getAbsolutePath() + File.separator + dest + "_" + dateFormat.format(new Date()) + ".png");
        System.out.println("截图位置：");
        System.out.println("----------------- file is " + targetFile.getPath());
        try
        {
            FileUtils.copyFile(srcFile, targetFile);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Attachment(value = "失败截图如下：",type = "image/png")
    public File  takePhoto(AppiumDriver driver){
        File screenshotAs = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
        return screenshotAs;
    }
}
