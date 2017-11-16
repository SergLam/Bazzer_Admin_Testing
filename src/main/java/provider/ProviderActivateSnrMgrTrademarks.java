package provider;

import main.MainClass;
import org.junit.*;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ProviderActivateSnrMgrTrademarks {

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

            // Активировать торговые марки всех старших менеджеров поставщика
            gotoSeniorManagerPage();
            editSeniorManagerBrandsActivateAll();

            logoutProvider();
        }
    }

    private void editSeniorManagerBrandsActivateAll() {
        // Ищем все кнопки с именем "торговые марки"
        List<WebElement> inputs = driver.findElements(By.tagName("input"));
        for (int i = 0; i < inputs.size(); i++) {
            // Если на кнопке написано "торговые марки" и она не слева
            if (inputs.get(i).getAttribute("value").equals("торговые марки") && inputs.get(i).getLocation().x > 400) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", inputs.get(i));
                driver.manage().timeouts().implicitlyWait(2000, TimeUnit.MILLISECONDS);
                // Найти таблицу
                WebElement table = driver.findElement(By.tagName("table"));
                // Найти все строки таблицы
                List<WebElement> tableRows = table.findElements(By.tagName("tr"));
                for (int j = 1; j < tableRows.size(); j++) {
                    // Найти секции в строке
                    List<WebElement> tableDiv = tableRows.get(j).findElements(By.tagName("td"));
                    // В первой секции - там чек-бокс
                    List<WebElement> check_box = tableDiv.get(0).findElements(By.tagName("input"));
                    // Найти и кликнуть на все чек-боксы
                    for (WebElement check : check_box) {
                        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", check);

                    }
                }
                // Вернуться на прошлую страницу
                ((JavascriptExecutor) driver).executeScript("window.history.go(-1)");
                inputs = driver.findElements(By.tagName("input"));
            } else {

            }
        }
    }

    public void gotoSeniorManagerPage() {
        driver.findElement(By.cssSelector("#left_menu > a:nth-child(3) > input:nth-child(1)")).click();
        driver.manage().timeouts().implicitlyWait(1500, TimeUnit.MILLISECONDS);
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
