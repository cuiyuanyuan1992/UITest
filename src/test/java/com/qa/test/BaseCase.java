package com.qa.test;

import com.qa.common.CommandUtils;
import com.qa.common.InitEnv;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.remote.MobileCapabilityType;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Slf4j
@Listeners({ScreenshotListener.class})
public class BaseCase extends InitEnv {
    public static AppiumDriver<MobileElement> driver;

    public AppiumDriver<MobileElement> getDriver(){
        return driver;
    }

    @BeforeClass
    public void setUp() throws Exception {
        //优化：需要动态获取设备信息（jenkins stf选择）及ios和android的判断（执行adb devices命令看是否是android设备）
        Map<String,String> deviceInfo = this.getIosDeviceInfo("iPhone (6)");//可结合jenkins动态获取设备
        DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
        desiredCapabilities.setCapability(MobileCapabilityType.DEVICE_NAME, deviceInfo.get("deviceName"));
        desiredCapabilities.setCapability(MobileCapabilityType.AUTOMATION_NAME, "XCUITest");
        desiredCapabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, "iOS");
        desiredCapabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION, deviceInfo.get("platformVersion"));
        desiredCapabilities.setCapability("bundleId", "com.taobao.tmall");
        desiredCapabilities.setCapability(MobileCapabilityType.UDID, deviceInfo.get("udid"));
        URL url = new URL("http://127.0.0.1:4723/wd/hub");
        driver = new IOSDriver<MobileElement>(url, desiredCapabilities);

        CommandUtils utils = new CommandUtils();
        utils.sleep(5);
    }

    @AfterClass
    public void tearDown(){
        driver.quit();
    }

    /**
     * 获取ios设备信息
     * @param deviceName 设备名称
     * @return 系统版本、udid信息
     */
    private Map<String,String> getIosDeviceInfo(String deviceName){
        CommandUtils utils = new CommandUtils();
        List<String> processList = utils.execCommand("instruments -s devices");
        Map<String,String> deviceInfo = new HashMap<String, String>();
        for(String info:processList){
            if(info.contains(deviceName)){
                //解析设备信息类似 iPhone (6) (14.4.1) [00008020-000935C41E04003A]
                String[] infos = info.split(" ");
                deviceInfo.put("deviceName",deviceName);
                int length = infos.length;
                deviceInfo.put("platformVersion",infos[length-2].substring(1,infos[length-2].length()-1));
                deviceInfo.put("udid",infos[length-1].substring(1,infos[length-1].length()-1));
            }
        }
        log.info("device info = {}",deviceInfo.toString());
        return deviceInfo;
    }

    //iPhone (6) (14.4.1) [00008020-000935C41E04003A]
//    public static void main(String[] args) {
//        BaseCase base = new BaseCase();
//        Map<String,String> info = base.getIosDeviceInfo("iPhone (6)");
//        System.out.println(info.toString());
//    }
}
