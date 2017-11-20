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
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class AdminAddProvider {

    private static ChromeDriverService service;
    private static WebDriver driver;
    ArrayList<String> provider_logins = new ArrayList<>();

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

        // Test for adding providers by admin
        Path profile_photo_path = Paths.get(MainClass.PROFILE_PHOTO_PATH);
        File f = new File(profile_photo_path.toAbsolutePath().toString());
        File[] files = f.listFiles();

        int plus = 20;
        for (int i = 1 + plus; i < files.length + plus; i++) {
            try {
                provider_logins.add(addProvider(i, files[i - plus].getName()));
            } catch (Throwable t) {
                t.printStackTrace();
            } finally {
                if (provider_logins.size() > 0 && i > files.length - 2 + plus) {
                    // Сохранить данные в файл для дальнейшего использования
                    Path logins_path = Paths.get(MainClass.PROVIDERS_FILE_PATH);
                    MainClass.saveToExcelFile(logins_path.toString(), provider_logins);
                    provider_logins.clear();
                }
            }

        }

    }

    private String addProvider(int provider_number, String file_name) {
        // Scroll page to top
        JavascriptExecutor jse = (JavascriptExecutor) driver;
        jse.executeScript("window.scrollBy(0,250)", "");
        //
        String provider_login = MainClass.PROVIDER_LOGIN + String.valueOf(provider_number);
        // Кликнуть кнопку "поставщики" на навигационном меню
        driver.findElement(By.cssSelector("#left_menu > a:nth-child(1) > input:nth-child(1)")).click();
        driver.manage().timeouts().implicitlyWait(1000, TimeUnit.MILLISECONDS);
        // Кликнуть кнопку "добавить поставщика"
        driver.findElement(By.cssSelector("#content > a:nth-child(1) > button:nth-child(1)")).click();
        driver.manage().timeouts().implicitlyWait(1000, TimeUnit.MILLISECONDS);
        // Заполняем форму
        driver.findElement(By.name("title_firm")).sendKeys("Фирма " + String.valueOf(provider_number));
        driver.findElement(By.id("phone")).clear();
        driver.findElement(By.id("phone")).sendKeys("0" + String.valueOf(new Random().nextInt((999999999 - 100000000) + 1) + 100000000));
        driver.findElement(By.name("mail")).sendKeys(MainClass.PROVIDER_LOGIN + String.valueOf(provider_number) + "@gmail.com");
        driver.findElement(By.name("login")).clear();
        driver.findElement(By.name("login")).sendKeys(MainClass.PROVIDER_LOGIN + String.valueOf(provider_number));
        driver.findElement(By.name("password")).clear();
        driver.findElement(By.name("password")).sendKeys(MainClass.PROVIDER_LOGIN + String.valueOf(provider_number));
        // Добавляем фото
        Path photo_path = Paths.get(MainClass.PROFILE_PHOTO_PATH + file_name);
        driver.findElement(By.name("file")).sendKeys(photo_path.toAbsolutePath().toString());
        // Отправляем форму
        driver.findElement(By.tagName("form")).submit();
        driver.manage().timeouts().implicitlyWait(1500, TimeUnit.MILLISECONDS);
        return provider_login;
    }

}
