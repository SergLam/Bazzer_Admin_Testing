import com.sun.deploy.util.SystemUtils;
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
import java.util.concurrent.TimeUnit;

public class Provider {

    public static final String BASE_URL_BOSS = "http://178.159.110.21:84";
    public static final String BASE_URL_MANAGER = "http://178.159.110.21:83";

    private static ChromeDriverService service;
    private static WebDriver driver;

    public static String getChromeDriverPath(){

        String os = System.getProperty("os.name").toLowerCase();
        String bytes = System.getProperty("os.arch");

        boolean isWin = os.contains("win");
        boolean isMac = os.contains("mac");
        boolean isLinux = os.contains("nix") || os.contains("nux") || os.contains("aix");
        boolean is32 = bytes.equals("x86") || bytes.equals("i386") || bytes.equals("i486") || bytes.equals("i586") || bytes.equals("i686");
        boolean is64 = bytes.equals("x86_64") || bytes.equals("amd64");

        // Detect chrome driver directory
        String fileName = "";
        Path chromeDriverDirectory = null;
        if(isMac || isLinux ){
            chromeDriverDirectory = Paths.get("src/main/resources/chrome_driver");
        }
        if(isWin){
            chromeDriverDirectory = Paths.get("src\\main\\resources\\chrome_driver");
        }

        String chromeDriverPath = chromeDriverDirectory.toAbsolutePath().toString();

        // Detect which driver to use
        if(isMac){
            chromeDriverPath = chromeDriverPath.concat("/chromedriver_mac");
        }

        if(isLinux){
         if(is32){
             chromeDriverPath = chromeDriverPath.concat("/chromedriver_linux32");
         }
         if(is64){
             chromeDriverPath = chromeDriverPath.concat("/chromedriver_linux32");
         }
        }

        if(isWin){
            chromeDriverPath = chromeDriverPath.concat("\\chromedriver.exe");
        }

        return chromeDriverPath;
    }

    @BeforeClass
    public static void createAndStartService() {

        getChromeDriverPath();
        service = new ChromeDriverService.Builder()
                .usingDriverExecutable(new File(getChromeDriverPath()))
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
        System.setProperty("webdriver.chrome.driver", getChromeDriverPath());
        driver = new RemoteWebDriver(service.getUrl(),
                DesiredCapabilities.chrome());
    }

    @After
    public void quitDriver() {
        //driver.quit();
    }

//    @Test
//    public void LoginInAdmin() throws Exception {
//        driver.manage().window().maximize();
//        driver.get(BASE_URL_BOSS);
//        driver.findElement(By.name("login")).sendKeys("sergey");
//        driver.findElement(By.name("password")).sendKeys("sergey");
//        driver.findElement(By.tagName("form")).submit();
//    }

    @Test
    public void LoginInProvider() throws Exception {
        driver.manage().window().maximize();
        driver.get(BASE_URL_BOSS);
        driver.findElement(By.name("login")).sendKeys("provider1");
        driver.findElement(By.name("password")).sendKeys("provider1");
        driver.findElement(By.tagName("form")).submit();
        addNewsProvider(driver);
    }

    public void addNewsProvider(WebDriver driver) {
        driver.findElement(By.cssSelector("#left_menu > a:nth-child(7) > input:nth-child(1)")).click();
        driver.findElement(By.cssSelector("#content > a:nth-child(1) > button:nth-child(1)")).click();
        driver.findElement(By.name("title")).sendKeys("Новость");
        driver.findElement(By.name("description")).sendKeys("Описание новости Selenium");

        Path photo_path = Paths.get("src/main/resources/news_photo/1.jpg");

        driver.findElement(By.name("file")).sendKeys(photo_path.toAbsolutePath().toString());
        driver.findElement(By.tagName("form")).submit();
    }

//    @Test
//    public void LoginInSeniorManager() throws Exception {
//        driver.manage().window().maximize();
//        driver.get(BASE_URL_MANAGER);
//        driver.findElement(By.name("login")).sendKeys("provider2");
//        driver.findElement(By.name("password")).sendKeys("provider2");
//        driver.findElement(By.tagName("form")).submit();
//    }

}
