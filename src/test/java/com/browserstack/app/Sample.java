package com.browserstack.app;

import java.util.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.Hashtable;
import java.io.*;
import java.util.concurrent.TimeUnit;

import org.json.simple.*;
import org.json.simple.parser.*;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import com.browserstack.app.Users;


class ParallelTest1 implements Runnable {
    public static final String USERNAME = System.getenv("BROWSERSTACK_USERNAME") != null ? System.getenv("BROWSERSTACK_USERNAME") : "username";
    public static final String AUTOMATE_KEY = System.getenv("BROWSERSTACK_ACCESS_KEY") != null ? System.getenv("BROWSERSTACK_ACCESS_KEY") : "accesskey";
    public static final String URL = "https://" + USERNAME + ":" + AUTOMATE_KEY + "@hub-cloud.browserstack.com/wd/hub";
    Hashtable<String, String> capsHashtable;
    String sessionName;
    String searchText;
    int index;

    ParallelTest1(Hashtable<String, String> cap, String sessionString, String searchString,int i) {
        capsHashtable = cap;
        sessionName = sessionString;
        searchText = searchString;
        index =i;
    }

    public void run() {
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("bstack:options", capsHashtable);
        caps.setCapability("sessionName", searchText); // test name
        caps.setCapability("buildName", "Coin DCX"); // CI/CD job or build name
        WebDriver driver;
        try {
            driver = new RemoteWebDriver(new URL(URL), caps);
            final JavascriptExecutor jse = (JavascriptExecutor) driver;
            try {
                // Searching for 'BrowserStack' on google.com
                driver.get("https://www.google.com/");
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
                driver.findElement(By.xpath("/html/body/div[1]/div[3]/form/div[1]/div[1]/div[1]/div/div[2]/input")).sendKeys(searchText,Keys.ENTER);
                Users.arr[index].busy= false;    
                jse.executeScript("browserstack_executor: {\"action\": \"setSessionStatus\", \"arguments\": {\"status\": \"passed\", \"reason\": \"Sucessfull\"}}");
            } catch (Exception e) {
                jse.executeScript("browserstack_executor: {\"action\": \"setSessionStatus\", \"arguments\": {\"status\": \"failed\", \"reason\": \"Some elements failed\"}}");
            }
            driver.quit();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}

public class Sample {
    public static void main(String[] args) throws Exception {
        List<Hashtable<String, String>> caps = new ArrayList<Hashtable<String, String>>();

        //device 1
        Hashtable<String, String> cap1 = new Hashtable<String, String>();
        cap1.put("os", "OS X");
        cap1.put("osVersion", "Big Sur");
        caps.add(cap1);

        //device 2
        Hashtable<String, String> cap2 = new Hashtable<String, String>();
        cap2.put("os", "Windows");
        cap2.put("osVersion", "10");
        caps.add(cap2);

        //device 3
        Hashtable<String, String> cap3 = new Hashtable<String, String>();
        cap3.put("os", "OS X");
        caps.add(cap3);

        //device 4
        Hashtable<String, String> cap4 = new Hashtable<String, String>();
        cap4.put("os", "windows");
        caps.add(cap4);

          //device 5
          Hashtable<String, String> cap5 = new Hashtable<String, String>();
          cap5.put("os", "Windows");
          cap5.put("osVersion", "10");
          caps.add(cap5);

        for (Hashtable<String, String> cap : caps) {

                Boolean gotElement = false;
                while(!gotElement)
                {
                    Thread.sleep(2000);
                    int i=0;
                    for(SearchElement ele:Users.arr)
                    {
                        if(!ele.busy)
                        {
                            ele.busy = true;
                            gotElement = true;
                            Thread thread = new Thread(new ParallelTest1(cap, "session name", ele.search,i));
                            thread.start();
                            // ele.busy = false;
                            break;
                        }
                        i+=1;
                    }
                }
                
            }
    }
}