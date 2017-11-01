import org.junit.*;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.Select;
import org.w3c.dom.html.HTMLOptGroupElement;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class SeniorManager {

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



    @Test
    public void LoginInManager() throws Exception {
        driver.manage().window().maximize();
        driver.get(MainClass.BASE_URL_MANAGER);
        driver.findElement(By.name("login")).sendKeys("provider2");
        driver.findElement(By.name("password")).sendKeys("provider2");
        driver.findElement(By.tagName("form")).submit();

        Path photo_path = Paths.get("src/main/resources/news_photo/");
        File f = new File(photo_path.toAbsolutePath().toString());
        File[] files = f.listFiles();

        int count = 0;
        if (files != null){
            count = files.length;
        }

        goToAddGood(driver);

        for(int i = 1;i<count;i++){
            addGood(driver,i);
        }


    }

    public void goToAddGood(WebDriver driver) {
        //Scroll page to top
        JavascriptExecutor jse = (JavascriptExecutor)driver;
        jse.executeScript("window.scrollBy(0,250)", "");
        // Press "товары" button
        driver.findElement(By.cssSelector("#left_menu > a:nth-child(5) > input:nth-child(1)")).click();
        driver.manage().timeouts().implicitlyWait(500, TimeUnit.MILLISECONDS);
        // Press "новый товар" button
        driver.findElement(By.cssSelector("#menu_site > a:nth-child(3) > input:nth-child(1)")).click();
        driver.manage().timeouts().implicitlyWait(500, TimeUnit.MILLISECONDS);
    }

    public void addGood(WebDriver driver, int i) {
        //Scroll page to top
        JavascriptExecutor jse = (JavascriptExecutor)driver;
        jse.executeScript("window.scrollBy(0,250)", "");
        // Press "товары" button
        driver.findElement(By.cssSelector("#left_menu > a:nth-child(5) > input:nth-child(1)")).click();
        driver.manage().timeouts().implicitlyWait(500, TimeUnit.MILLISECONDS);
        // Press "новый товар" button
        driver.findElement(By.cssSelector("#menu_site > a:nth-child(3) > input:nth-child(1)")).click();
        driver.manage().timeouts().implicitlyWait(500, TimeUnit.MILLISECONDS);

        // Fill out all inputs on form
        // Артикул - МАКС - 10 символов
        int rand = (int) (Math.random() * (100000000 - 1)) + 1;

        DecimalFormat format = new DecimalFormat("#.##", new DecimalFormatSymbols(Locale.US));
        String price =  format.format((Math.random() * (1000 - 1)) + 1);
        String price_ref =  format.format((Math.random() * (900 - 1)) + 1);
        String price_vip =  format.format((Math.random() * (850 - 1)) + 1);

        System.out.println(price);
        System.out.println(price_ref);
        System.out.println(price_vip);

        int quantity = (int)(Math.random() * (999 - 1)) + 1;
        String volume = format.format((Math.random() * (3 - 1)) + 1);


        driver.findElement(By.name("title")).sendKeys("Товар "+String.valueOf(rand));
        driver.findElement(By.name("article")).sendKeys(String.valueOf(rand));

        driver.findElement(By.name("price")).clear();
        driver.findElement(By.name("price")).sendKeys(price);

        driver.findElement(By.name("price_ref")).clear();
        driver.findElement(By.name("price_ref")).sendKeys(price_ref);

        driver.findElement(By.name("price_vip")).clear();
        driver.findElement(By.name("price_vip")).sendKeys(price_vip);

        driver.findElement(By.name("description")).sendKeys("Описание товара "+String.valueOf(rand));
        driver.findElement(By.name("quantity")).sendKeys(String.valueOf(quantity));
        driver.findElement(By.name("unit")).sendKeys("шт");

        driver.findElement(By.name("volume")).clear();
        driver.findElement(By.name("volume")).sendKeys(volume);

        // Select random item from dropdown list (trademark)
        WebElement selectTrademark = driver.findElement(By.name("trademarks"));
        Select selectTrade = new Select(selectTrademark);
        List<WebElement> all_trademarks = selectTrade.getOptions();

        int rand_trademark = new Random().nextInt(all_trademarks.size());;

        for(WebElement option : all_trademarks){
            if(option.getText().equals(all_trademarks.get(rand_trademark).getText())) {
                option.click(); //select option here;
            }
        }

        // Select random item from dropdown list (category)
        WebElement selectCategory = driver.findElement(By.name("category"));
        Select selectCateg = new Select(selectCategory);
        List<WebElement> all_category = selectCateg.getOptions();

        int rand_sub_category = new Random().nextInt(all_category.size());;

        for(WebElement option : all_category){
            if(option.getText().equals(all_category.get(rand_sub_category).getText())) {
                option.click(); //select option here;
            }
        }

        // Select random item from dropdown list (country)
        WebElement selectCountry = driver.findElement(By.name("country"));
        Select selectCountr = new Select(selectCountry);
        List<WebElement> all_country = selectCountr.getOptions();

        int rand_country = new Random().nextInt(all_country.size());

        for(WebElement option : all_country){
            if(option.getText().equals(all_country.get(rand_country).getText())) {
                option.click(); //select option here;
            }
        }

        // Select random proto
        Path photo_path = Paths.get("src/main/resources/news_photo/"+String.valueOf(i)+".jpg");
        driver.findElement(By.name("file")).sendKeys(photo_path.toAbsolutePath().toString());

        driver.manage().timeouts().implicitlyWait(1000000000, TimeUnit.MILLISECONDS);
        driver.findElement(By.tagName("form")).submit();
        driver.manage().timeouts().implicitlyWait(500, TimeUnit.MILLISECONDS);

    }
}
