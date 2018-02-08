package provider;

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

public class ProviderAddSeniorManager {

    private static ChromeDriverService service;
    private static WebDriver driver;

    // Список логинов провайдеров из файла
    private static ArrayList<String> providers_login = getProvidersLogins();
    private static ArrayList<String> senior_managers_logins = new ArrayList<>();
    // Список основных городов
    String[] main_cities = {"Харьков", "Киев"};
    String[] main_cities1 = {"Черновцы", "Чернигов", "Черкассы", "Хмельницкий", "Херсон", "Харьков", "Тернополь",
            "Сумы", "Ровно", "Полтава", "Одесса", "Львов", "Кировоград", "Киев", "Ивано-Франковск",
            "Запорожье", "Ужгород", "Житомир", "Днепропетровск", "Луцк", "Винница"};

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

            // Создать старших менеджеров поставщика
            gotoSeniorManagerPage();
            for (int j = 0; j < main_cities.length; j++) {
                int city_index = j;
                try {
                    addSeniorManager(city_index, provider_login);
                } catch (Throwable t) {
                    t.printStackTrace();
                } finally {
                    if (senior_managers_logins.size() > 0 && j > main_cities.length - 3) {
                        Path logins_path = Paths.get(MainClass.SENIOR_MANAGER_FILE_PATH + provider_login + MainClass.EXCEL_FILE_EXTENSION);
                        MainClass.saveToExcelFile(logins_path.toString(), senior_managers_logins);
                        senior_managers_logins.clear();
                    }
                }
            }

            logoutProvider();
        }
    }


    public void gotoSeniorManagerPage() {
        driver.findElement(By.cssSelector("#left_menu > a:nth-child(3) > input:nth-child(1)")).click();
        driver.manage().timeouts().implicitlyWait(1500, TimeUnit.MILLISECONDS);
    }

    public void addSeniorManager(int city_index, String provider_login) {
        //Scroll page to top
        JavascriptExecutor jse = (JavascriptExecutor) driver;
        jse.executeScript("window.scrollBy(0,250)", "");

        // Нажать кнопку "Добавить старшего менеджера"
        driver.findElement(By.xpath("/html/body/div[6]/a/button")).click();
        driver.manage().timeouts().implicitlyWait(1000, TimeUnit.MILLISECONDS);

        // Выбрать из списка городов рандомно один из главных
        // Кликнуть по выпадающему списку
        driver.findElement(By.tagName("span")).click();
        // В появивщееся поле вбить значение нашего города для поиска
        driver.findElement(By.cssSelector(".chosen-search > input:nth-child(1)")).sendKeys(main_cities[city_index]);
        // Поиск выдает нужный город - делаем по нему клик
        driver.findElement(By.tagName("em")).click();
        // F.I.O
        String MANAGER_LOGIN = MainClass.SENIOR_MANAGER_LOGIN + String.valueOf(city_index) + provider_login;
        driver.findElement(By.name("name")).sendKeys("Старший менеджер " + main_cities[city_index]);
        driver.findElement(By.name("login")).sendKeys(MANAGER_LOGIN);
        driver.findElement(By.name("password")).sendKeys(MANAGER_LOGIN);
        driver.findElement(By.id("phone")).clear();
        driver.findElement(By.id("phone")).sendKeys("0" + String.valueOf(new Random().nextInt((999999999 - 100000000) + 1) + 100000000));
        driver.findElement(By.name("information")).sendKeys("Информация о Старший менеджер " + main_cities[city_index]);
        driver.findElement(By.name("work_time")).sendKeys("Время работы Старший менеджер " + main_cities[city_index]);
        driver.findElement(By.name("is_activate")).click();
        // Submit the form
        driver.findElement(By.tagName("form")).submit();
        senior_managers_logins.add(MANAGER_LOGIN);
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
