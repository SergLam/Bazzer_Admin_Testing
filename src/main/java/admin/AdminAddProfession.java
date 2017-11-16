package admin;

import main.MainClass;
import org.junit.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;


public class AdminAddProfession {

    private static ChromeDriverService service;
    private static WebDriver driver;

    @BeforeClass
    public static void createAndStartService() {

        service = new ChromeDriverService.Builder()
                .usingDriverExecutable(new File(MainClass.getChromeDriverPath()))
                .usingAnyFreePort()
                .build();
        try {
            service.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public static void createAndStopService() {
        service.stop();
    }

    @Before
    public void createDriver() {
        System.setProperty("webdriver.chrome.driver", MainClass.getChromeDriverPath());
        driver = new RemoteWebDriver(service.getUrl(),
                DesiredCapabilities.chrome());
    }

    @After
    public void quitDriver() {
        driver.quit();
    }

    @Test
    public void LoginInAdmin() throws Exception {
        driver.get(MainClass.BASE_URL_BOSS);
        driver.findElement(By.name("login")).sendKeys("sergey");
        driver.findElement(By.name("password")).sendKeys("sergey");
        driver.findElement(By.tagName("form")).submit();

        // Test for add professions by admin
        goToProfessionsPage();

        for (int i = 0; i < 30; i++) {
            addProfessionAdmin();
        }

    }

    private void addProfessionAdmin() {
        driver.findElement(By.name("profession")).sendKeys("Профессия " + String.valueOf(new Random().nextInt(10000)));
        driver.findElement(By.tagName("form")).submit();
        driver.manage().timeouts().implicitlyWait(2000, TimeUnit.MILLISECONDS);
    }

    private void goToProfessionsPage() {
        driver.findElement(By.cssSelector("#left_menu > a:nth-child(11) > input:nth-child(1)")).click();
        driver.manage().timeouts().implicitlyWait(2000, TimeUnit.MILLISECONDS);
    }

}
