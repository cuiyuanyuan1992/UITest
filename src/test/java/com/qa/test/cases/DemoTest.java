package com.qa.test.cases;

import com.qa.test.BaseCase;
import com.qa.test.pageObj.HomePageObj;
import io.appium.java_client.MobileElement;
import org.testng.Assert;
import org.testng.annotations.Test;

public class DemoTest extends BaseCase {

    @Test(description = "国际进口进入")
    public void testcase1(){
        HomePageObj homePage = new HomePageObj(driver);
        MobileElement import1 = homePage.importButton;
        import1.click();
        Assert.assertFalse(true);
    }

}
