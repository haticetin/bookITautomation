package com.bookit.tests;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.cybertek.utilities.BrowserUtils;
import com.bookit.utilities.ConfigurationReader;
import com.bookit.utilities.Driver;
import com.github.javafaker.Faker;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class TestBase {


    protected WebDriver driver;
    protected Actions actions;
    protected Select select;
    protected WebDriverWait wait;
    protected Faker faker;
    protected static  ExtentReports report;
    protected static ExtentHtmlReporter htmlReporter;
    protected static ExtentTest extentLogger;
    String url;


    @BeforeTest
    public void setupTest(){

        // initialize the class
        report = new ExtentReports();

        // create a report path
        String projectPath = System.getProperty("user.dir");
        String path = projectPath + "/test-output/report.html";

        // intialize the html reporter with the report path
        htmlReporter = new ExtentHtmlReporter(path);

        // attach the html report to report object
        report.attachReporter(htmlReporter);

        // title in report
        htmlReporter.config().setReportName("Vytrack Smoke Test");

        // set the environment information
        report.setSystemInfo("Environment", "QA");
        report.setSystemInfo("Browser", ConfigurationReader.get("browser"));
        report.setSystemInfo("OS", System.getProperty("os.name"));

    }

    @BeforeMethod
    @Parameters("env")
    public void setupMethod(@Optional String env){
        System.out.println("env = " + env);

        if(env==null){
            url = ConfigurationReader.get("url");
        }else{
            url = ConfigurationReader.get(env + "_url");
        }

        // if env variable is null use default url
        // if it is not null, choose env based on value
        faker = new Faker();
        driver = Driver.get();
        driver.get(ConfigurationReader.get("url"));
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        actions = new Actions(driver);
        wait = new WebDriverWait(driver, 10);
    }


    @AfterMethod
    public void afterMethod(ITestResult result) throws InterruptedException, IOException {

        // if test fails
        if(result.getStatus()==ITestResult.FAILURE){
            // record the name to failed test case
            extentLogger.fail(result.getName());

            // take the screenshot and return location of screenshot
            String screenShotPath = BrowserUtils.getScreenshot(result.getName());
            // add your screen shot to your report
            extentLogger.addScreenCaptureFromPath(screenShotPath);

            // capture the exception and put inside the report
            extentLogger.fail(result.getThrowable());


        }

        Thread.sleep(2000);
        Driver.closeDriver();
    }


    @AfterTest
    public void tearDownTest(){

        // this is when the report is actually created
        report.flush();

    }

}
