package com.qa.test.pageObj;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.appium.java_client.pagefactory.iOSFindBy;
import org.openqa.selenium.support.PageFactory;

public class HomePageObj{
    public HomePageObj(AppiumDriver<MobileElement> driver){
        PageFactory.initElements(new AppiumFieldDecorator(driver), this);
    }

    @iOSFindBy(accessibility = "进口")
    public MobileElement importButton;

}
