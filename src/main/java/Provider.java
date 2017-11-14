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
import java.util.concurrent.TimeUnit;

public class Provider {

    private static ChromeDriverService service;
    private static WebDriver driver;
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
    public void LoginInProvider() throws Exception {
        for (int i = 6; i < providers_login.size(); i++) {
            driver.manage().window().maximize();
            driver.get(MainClass.BASE_URL_BOSS);
            driver.findElement(By.name("login")).sendKeys(providers_login.get(i));
            driver.findElement(By.name("password")).sendKeys(providers_login.get(i));
            driver.findElement(By.tagName("form")).submit();
            driver.manage().timeouts().implicitlyWait(2000, TimeUnit.MILLISECONDS);
            logoutProvider();
        }

//        Path photo_path = Paths.get("src/main/resources/news_photo/");
//        File f = new File(photo_path.toAbsolutePath().toString());
//        File[] files = f.listFiles();
//
//        int count = 0;
//        if (files != null){
//            count = files.length;
//        }
//
//        for(int i = 1;i<count;i++){
//            addNewsProvider(i);
//        }

    }

    public void gotoSeniorManagerPage(){
        driver.findElement(By.cssSelector("#left_menu > a:nth-child(3) > input:nth-child(1)")).click();
        driver.manage().timeouts().implicitlyWait(1000, TimeUnit.MILLISECONDS);
    }

    public void addSeniorManager(){
        // Список основных городов
        String[] main_cities = {"Черновцы","Чернигов","Черкассы","Хмельницкий","Херсон","Харьков","Тернополь",
                "Сумы","Ровно","Полтава","Одесса","Львов","Кировоград","Луганск","Киев","Ивано-Франковск",
                "Запорожье","Ужгород","Житомир","Донецк","Днепропетровск","Луцк","Винница","Симферополь"};
        // Нажать кнопку "Добавить старшего менеджера"
        driver.findElement(By.cssSelector("#content > a:nth-child(1) > button:nth-child(1)")).click();
        driver.manage().timeouts().implicitlyWait(500, TimeUnit.MILLISECONDS);
    }

    public void addNewsProvider(int i) {
        //Scroll page to top
        JavascriptExecutor jse = (JavascriptExecutor) driver;
        jse.executeScript("window.scrollBy(0,250)", "");
        // Press "news" button
        driver.findElement(By.cssSelector("#left_menu > a:nth-child(7) > input:nth-child(1)")).click();
        driver.manage().timeouts().implicitlyWait(500, TimeUnit.MILLISECONDS);
        // Press "add news" button
        driver.findElement(By.cssSelector("#content > a:nth-child(1) > button:nth-child(1)")).click();
        // Wait for JS unwrap form for news creation
        driver.manage().timeouts().implicitlyWait(500, TimeUnit.MILLISECONDS);
        // Fill out form
        int rand = (int) (Math.random() * (1000000 + 1));

        driver.findElement(By.name("title")).sendKeys("Новость " + String.valueOf(rand));
        driver.findElement(By.name("description")).sendKeys("Описание новости Selenium " + String.valueOf(rand));
        // Set photo path
        Path photo_path = Paths.get("src/main/resources/news_photo/" + String.valueOf(i) + ".jpg");
        driver.findElement(By.name("file")).sendKeys(photo_path.toAbsolutePath().toString());

        driver.findElement(By.tagName("form")).submit();
        driver.manage().timeouts().implicitlyWait(500, TimeUnit.MILLISECONDS);
    }

    public static ArrayList<String> getProvidersLogins() {
        Path logins_path = Paths.get("output/ProviderLogins.xlsx");
        return MainClass.readFromExcelFile(logins_path.toAbsolutePath().toString());
    }

    public void logoutProvider(){
        driver.findElement(By.cssSelector("#logout > a:nth-child(1) > input:nth-child(1)")).click();
        driver.manage().timeouts().implicitlyWait(1500, TimeUnit.MILLISECONDS);
    }
}
