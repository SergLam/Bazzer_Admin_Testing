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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class AdminAddNews {

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

        // Test for adding news by admin
        Path news_photo_path = Paths.get(MainClass.NEWS_PHOTO_PATH);
        File f = new File(news_photo_path.toAbsolutePath().toString());
        File[] files = f.listFiles();

        for (int i = 1; i < files.length; i++) {
            addNewsAdmin(files[i].getName());
        }

        for (int i = 1; i < files.length; i++) {
            addEventsAdmin(files[i].getName());
        }
    }

    public void addNewsAdmin(String file_name) {
        //Scroll page to top
        JavascriptExecutor jse = (JavascriptExecutor) driver;
        jse.executeScript("window.scrollBy(0,250)", "");
        // Press "news" button
        driver.findElement(By.cssSelector("#left_menu > a:nth-child(9) > input:nth-child(1)")).click();
        driver.manage().timeouts().implicitlyWait(1000, TimeUnit.MILLISECONDS);
        // Press "add news" button
        driver.findElement(By.cssSelector("#content > a:nth-child(2) > button:nth-child(1)")).click();
        // Wait for JS unwrap form for news creation
        driver.manage().timeouts().implicitlyWait(1000, TimeUnit.MILLISECONDS);
        // Fill out form
        int rand = (int) (Math.random() * (1000000));

        driver.findElement(By.name("title")).sendKeys("Новость " + String.valueOf(rand));
        driver.findElement(By.name("description")).sendKeys("Описание новости Selenium " + String.valueOf(rand));
        // Set photo path
        Path photo_path = Paths.get(MainClass.NEWS_PHOTO_PATH + file_name);
        driver.findElement(By.name("file")).sendKeys(photo_path.toAbsolutePath().toString());

        driver.findElement(By.tagName("form")).submit();
        driver.manage().timeouts().implicitlyWait(1500, TimeUnit.MILLISECONDS);
    }

    private void addEventsAdmin(String file_name) {
        //Scroll page to top
        JavascriptExecutor jse = (JavascriptExecutor) driver;
        jse.executeScript("window.scrollBy(0,250)", "");
        // Press "news" button
        driver.findElement(By.cssSelector("#left_menu > a:nth-child(9) > input:nth-child(1)")).click();
        driver.manage().timeouts().implicitlyWait(1000, TimeUnit.MILLISECONDS);
        // Press "add news" button
        driver.findElement(By.cssSelector("#content > a:nth-child(2) > button:nth-child(1)")).click();
        // Wait for JS unwrap form for news creation
        driver.manage().timeouts().implicitlyWait(1000, TimeUnit.MILLISECONDS);
        // Fill out form

        driver.findElement(By.cssSelector("#info_ > form:nth-child(1) > input:nth-child(2)")).click();
        driver.manage().timeouts().implicitlyWait(1000, TimeUnit.MILLISECONDS);

        driver.findElement(By.name("date_event")).sendKeys(generateRandomDate());

        int event_number = new Random().nextInt(10000);

        driver.findElement(By.name("title")).sendKeys("Событие " + String.valueOf(event_number));
        driver.findElement(By.name("description")).sendKeys("Описание события " + String.valueOf(event_number));

        // Set photo path
        Path photo_path = Paths.get(MainClass.NEWS_PHOTO_PATH + file_name);
        driver.findElement(By.name("file")).sendKeys(photo_path.toAbsolutePath().toString());

        driver.findElement(By.tagName("form")).submit();
        driver.manage().timeouts().implicitlyWait(1500, TimeUnit.MILLISECONDS);
    }

    private String generateRandomDate() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        LocalDate startDate = LocalDate.now(); // start date
        long start = startDate.toEpochDay();
        System.out.println(start);

        LocalDate endDate = LocalDate.of(LocalDate.now().plusYears(1).getYear(), LocalDate.now().plusYears(1).getMonth(), LocalDate.now().plusYears(1).getDayOfMonth()); //end date
        long end = endDate.toEpochDay();
        System.out.println(start);

        long randomEpochDay = ThreadLocalRandom.current().longs(start, end).findAny().getAsLong();

        String date = LocalDate.ofEpochDay(randomEpochDay).format(dtf);

        return date;
    }

}
