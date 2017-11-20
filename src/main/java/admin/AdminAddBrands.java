package admin;

import main.MainClass;
import org.junit.*;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class AdminAddBrands {

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
        driver.findElement(By.name("login")).sendKeys(MainClass.ADMIN_LOGIN);
        driver.findElement(By.name("password")).sendKeys(MainClass.ADMIN_PASSWORD);
        driver.findElement(By.tagName("form")).submit();

        // Test for adding trademarks by admin
        Path brand_photo_path = Paths.get(MainClass.BRAND_PHOTO_PATH);
        File brand_f = new File(brand_photo_path.toAbsolutePath().toString());
        File[] brand_files = brand_f.listFiles();

        goToTradeMarkPage();

        for (int i = 1; i < 2/*brand_files.length*/; i++) {
            addTradeMarkAdmin(brand_files[i].getName());
        }
    }

    private void goToTradeMarkPage() {
        // Press "торговые марки" button
        driver.findElement(By.cssSelector("#left_menu > a:nth-child(13) > input:nth-child(1)")).click();
        driver.manage().timeouts().implicitlyWait(2000, TimeUnit.MILLISECONDS);
    }

    private void addTradeMarkAdmin(String file_name) {
        //Scroll page to top
        JavascriptExecutor jse = (JavascriptExecutor) driver;
        jse.executeScript("window.scrollBy(0,250)", "");
        // Press "добавить торговую марку" button
        driver.findElement(By.xpath("/html/body/div[6]/a/button")).click();
        driver.manage().timeouts().implicitlyWait(1000, TimeUnit.MILLISECONDS);
        // Insert data in form fields
        String[] arr = file_name.split("\\.");
        String brand_name = arr[0];
        driver.findElement(By.id("trade")).sendKeys(brand_name);
        driver.findElement(By.id("desc")).sendKeys("Описание торговой марки " + brand_name);

        Path photo_path = Paths.get(MainClass.BRAND_PHOTO_PATH + file_name);
        driver.findElement(By.name("file")).sendKeys(photo_path.toAbsolutePath().toString());
        //driver.findElement(By.tagName("form")).submit();
        //driver.manage().timeouts().implicitlyWait(1000, TimeUnit.MILLISECONDS);

    }

}
