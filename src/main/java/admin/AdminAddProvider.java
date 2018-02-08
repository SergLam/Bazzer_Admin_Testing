package admin;

import main.MainClass;
import org.apache.poi.ss.formula.functions.T;
import org.junit.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class AdminAddProvider {

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

        //service.stop();
    }

    @Before
    public void createDriver() {
        System.setProperty("webdriver.chrome.driver", MainClass.getChromeDriverPath());
        driver = new RemoteWebDriver(service.getUrl(),
                DesiredCapabilities.chrome());
    }

    @After
    public void quitDriver() {

        //driver.quit();
    }

    @Test
    public void LoginInAdmin() throws Exception {
        driver.get(MainClass.BASE_URL_BOSS);
        driver.manage().timeouts().implicitlyWait(2000, TimeUnit.MILLISECONDS);
        driver.findElement(By.name("login")).sendKeys(MainClass.ADMIN_LOGIN);
        driver.findElement(By.name("password")).sendKeys(MainClass.ADMIN_PASSWORD);
        driver.findElement(By.tagName("form")).submit();
        driver.manage().timeouts().implicitlyWait(2000, TimeUnit.MILLISECONDS);
        // Создать старших менеджеров поставщика
        gotoProviderPage();

        Path photo_path = Paths.get(MainClass.PROFILE_PHOTO_PATH);
        File f = new File(photo_path.toAbsolutePath().toString());
        File[] files = f.listFiles();

        for (int j = 1; j < files.length; j++) {
            try {
                String[] arr = files[j].getName().split("\\.");
                addProvider(arr[0], files[j].getName());
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        
    }

    private void gotoProviderPage() {
        driver.findElement(By.xpath("/html/body/div[5]/a[1]/input")).click();
        driver.manage().timeouts().implicitlyWait(2000, TimeUnit.MILLISECONDS);
    }

    private void addProvider(String num, String file_name) {
        driver.findElement(By.xpath("/html/body/div[6]/a/button")).click();
        driver.manage().timeouts().implicitlyWait(1000, TimeUnit.MILLISECONDS);

        driver.findElement(By.xpath("/html/body/div[6]/div/form/table/tbody/tr[1]/td[2]/input")).sendKeys("Фирма "+num);
        driver.findElement(By.id("phone")).clear();
        driver.findElement(By.id("phone")).sendKeys("0" + String.valueOf(new Random().nextInt((999999999 - 100000000) + 1) + 100000000));
        driver.findElement(By.xpath("/html/body/div[6]/div/form/table/tbody/tr[4]/td[2]/input")).sendKeys("provider"+num+"@gmail.com");
        driver.findElement(By.xpath("/html/body/div[6]/div/form/table/tbody/tr[6]/td[2]/input")).sendKeys("provider"+num);
        driver.findElement(By.cssSelector("#info_ > form:nth-child(1) > table:nth-child(1) > tbody:nth-child(1) > tr:nth-child(8) > td:nth-child(2) > input:nth-child(2)")).sendKeys("provider"+num);
        Path photo_path = Paths.get(MainClass.PROFILE_PHOTO_PATH + file_name);
        driver.findElement(By.name("file")).sendKeys(photo_path.toAbsolutePath().toString());
        driver.findElement(By.xpath("/html/body/div[6]/div/form")).submit();
        driver.manage().timeouts().implicitlyWait(1500, TimeUnit.MILLISECONDS);

    }

}
