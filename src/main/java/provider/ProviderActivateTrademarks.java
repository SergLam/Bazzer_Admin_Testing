package provider;

import main.MainClass;
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
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class ProviderActivateTrademarks {

    private static ChromeDriverService service;
    private static WebDriver driver;

    // Список логинов провайдеров из файла
    private static ArrayList<String> providers_login = getProvidersLogins();

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
    public void LoginInProvider() throws Exception {

        for (int i = 0; i < providers_login.size(); i++) {

            String provider_login = providers_login.get(i);

            driver.get(MainClass.BASE_URL_BOSS);
            driver.manage().timeouts().implicitlyWait(2000, TimeUnit.MILLISECONDS);
            driver.findElement(By.name("login")).sendKeys(provider_login);
            driver.findElement(By.name("password")).sendKeys(provider_login);
            driver.findElement(By.tagName("form")).submit();
            driver.manage().timeouts().implicitlyWait(2000, TimeUnit.MILLISECONDS);

            // Активировать все торговые марки поставщика
            gotoTradeMarkPage();
            selectAllTrademarks();

            logoutProvider();
        }
    }

    private void gotoTradeMarkPage() {
        // Нажать кнопку "торговые марки" в навигационной панели
        driver.findElement(By.cssSelector("#left_menu > a:nth-child(5) > input:nth-child(1)")).click();
        driver.manage().timeouts().implicitlyWait(2000, TimeUnit.MILLISECONDS);
    }

    private void selectAllTrademarks() {
        // Кликнуть в поле для разворачивания списка
        driver.findElement(By.cssSelector(".ms-options-wrap > button:nth-child(1)")).click();
        // Кликнуть на "select all"
        driver.findElement(By.xpath("/html/body/div[6]/div/form/div/div/a")).click();
        // Нажать на "сохранить"
        driver.findElement(By.cssSelector("#content > div:nth-child(1) > form:nth-child(1) > input:nth-child(1)")).click();
        driver.manage().timeouts().implicitlyWait(2000, TimeUnit.MILLISECONDS);
    }

    public static ArrayList<String> getProvidersLogins() {
        Path logins_path = Paths.get(MainClass.PROVIDERS_FILE_PATH);
        return MainClass.readFromExcelFile(logins_path.toAbsolutePath().toString());
    }

    public void logoutProvider() {
        driver.findElement(By.cssSelector("#logout > a:nth-child(1) > input:nth-child(1)")).click();
        driver.manage().timeouts().implicitlyWait(2000, TimeUnit.MILLISECONDS);
    }

}
