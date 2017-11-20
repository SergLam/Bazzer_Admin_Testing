package seniormanager;

import main.MainClass;
import model.TableOrderSearch;
import org.junit.*;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.Select;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class SeniorManagerAddGoods {

    private TableOrderSearch searchResult;
    private static ChromeDriverService service;
    private static WebDriver driver;
    // Логины старших менеджеров
    private ArrayList<ArrayList<String>> list_of_excel_logins = new ArrayList<>();
    // Массив для сохранения логинов младших менеджеров
    private ArrayList<String> juniorManagers_logins = new ArrayList<>();

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
    public void LoginInManager() throws Throwable {
        readExcelFilesWithManagersLogins();

        if (list_of_excel_logins.size() > 0) {
            for (int i = 3; i < list_of_excel_logins.size(); i++) {
                ArrayList<String> logins = list_of_excel_logins.get(i);
                for (int j = 0; j < logins.size(); j++) {
                    try{
                        driver.get(MainClass.BASE_URL_MANAGER);
                        driver.findElement(By.name("login")).sendKeys(logins.get(j));
                        driver.findElement(By.name("password")).sendKeys(logins.get(j));
                        driver.findElement(By.tagName("form")).submit();
                        driver.manage().timeouts().implicitlyWait(2000, TimeUnit.MILLISECONDS);

                        // Добавление товаров в рандомные категории
                        // Кол-во товаров равно кол-ву картинок в папке
                        Path photo_path = Paths.get(MainClass.GOODS_PHOTO_PATH);
                        File f = new File(photo_path.toAbsolutePath().toString());
                        File[] files = f.listFiles();

                        goToAddGood();
                        for (int k = 1; k < files.length; k++) {
                            addGood(files[k].getName());
                        }

                        // Выходим из менеджера
                        logoutSeniorManager();
                    } catch (Throwable t){
                        t.printStackTrace();
                    }
                }
            }
        }

    }

    public void goToAddGood() {
        //Scroll page to top
        JavascriptExecutor jse = (JavascriptExecutor) driver;
        jse.executeScript("window.scrollBy(0,250)", "");
        // Press "товары" button
        driver.findElement(By.cssSelector("#left_menu > a:nth-child(5) > input:nth-child(1)")).click();
        driver.manage().timeouts().implicitlyWait(1000, TimeUnit.MILLISECONDS);
        // Press "новый товар" button
        driver.findElement(By.cssSelector("#menu_site > a:nth-child(3) > input:nth-child(1)")).click();
        driver.manage().timeouts().implicitlyWait(1000, TimeUnit.MILLISECONDS);
    }

    public void addGood(String file_name) {
        //Scroll page to top
        JavascriptExecutor jse = (JavascriptExecutor) driver;
        jse.executeScript("window.scrollBy(0,250)", "");
        // Press "товары" button
        driver.findElement(By.cssSelector("#left_menu > a:nth-child(5) > input:nth-child(1)")).click();
        driver.manage().timeouts().implicitlyWait(1000, TimeUnit.MILLISECONDS);
        // Press "новый товар" button
        driver.findElement(By.cssSelector("#menu_site > a:nth-child(3) > input:nth-child(1)")).click();
        driver.manage().timeouts().implicitlyWait(1000, TimeUnit.MILLISECONDS);

        // Fill out all inputs on form
        // Артикул - МАКС - 10 символов
        int rand = (int) (Math.random() * (100000000 - 1)) + 1;

        DecimalFormat format = new DecimalFormat("#.##", new DecimalFormatSymbols(Locale.US));
        String price = format.format((Math.random() * (1000 - 1)) + 1);
        String price_ref = format.format((Math.random() * (900 - 1)) + 1);
        String price_vip = format.format((Math.random() * (850 - 1)) + 1);

        int quantity = (int) (Math.random() * (999 - 1)) + 1;
        String volume = format.format((Math.random() * (3 - 1)) + 1);


        driver.findElement(By.name("title")).sendKeys("Товар " + String.valueOf(rand));
        driver.findElement(By.name("article")).sendKeys(String.valueOf(rand));

        driver.findElement(By.name("price")).clear();
        driver.findElement(By.name("price")).sendKeys(price);

        driver.findElement(By.name("price_ref")).clear();
        driver.findElement(By.name("price_ref")).sendKeys(price_ref);

        driver.findElement(By.name("price_vip")).clear();
        driver.findElement(By.name("price_vip")).sendKeys(price_vip);

        driver.findElement(By.name("description")).sendKeys("Описание товара " + String.valueOf(rand));
        driver.findElement(By.name("quantity")).sendKeys(String.valueOf(quantity));
        driver.findElement(By.name("unit")).sendKeys("шт");

        driver.findElement(By.name("volume")).clear();
        driver.findElement(By.name("volume")).sendKeys(volume);

        // Select random item from dropdown list (trademark)
        WebElement selectTrademark = driver.findElement(By.name("trademarks"));
        Select selectTrade = new Select(selectTrademark);
        List<WebElement> all_trademarks = selectTrade.getOptions();

        int rand_trademark = new Random().nextInt(all_trademarks.size());
        all_trademarks.get(rand_trademark).click();

        // Select random item from dropdown list (category)
        WebElement selectCategory = driver.findElement(By.name("category"));
        Select selectCateg = new Select(selectCategory);
        List<WebElement> all_category = selectCateg.getOptions();

        int rand_sub_category = new Random().nextInt(all_category.size());
        all_category.get(rand_sub_category).click();

        // Select random item from dropdown list (country)
        WebElement selectCountry = driver.findElement(By.name("country"));
        Select selectCountr = new Select(selectCountry);
        List<WebElement> all_country = selectCountr.getOptions();

        int rand_country = new Random().nextInt(all_country.size());
        all_country.get(rand_country).click();

        // Select random proto
        Path photo_path = Paths.get(MainClass.GOODS_PHOTO_PATH + file_name);
        driver.findElement(By.name("file")).sendKeys(photo_path.toAbsolutePath().toString());

        driver.findElement(By.tagName("form")).submit();
        driver.manage().timeouts().implicitlyWait(2000, TimeUnit.MILLISECONDS);

    }

    private void readExcelFilesWithManagersLogins() {
        // Read all files in directory
        Path output_path = Paths.get(MainClass.OUTPUT_FOLDER);
        File f = new File(output_path.toAbsolutePath().toString());
        File[] files = f.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].getName().contains(MainClass.SENIOR_MANAGER_FILE_NAME)) {
                list_of_excel_logins.add(MainClass.readFromExcelFile(files[i].getAbsolutePath()));
            }
        }
    }

    public void logoutSeniorManager() {
        driver.findElement(By.cssSelector("#logout > a:nth-child(1) > input:nth-child(1)")).click();
        driver.manage().timeouts().implicitlyWait(1500, TimeUnit.MILLISECONDS);
    }

    public String getFirmName() {
        String title = driver.findElement(By.cssSelector("#menu_site > font:nth-child(1)")).getText();
        String[] arr = title.split(" название фирмы ");
        System.out.println(arr[1]);
        return arr[1];
    }

}
