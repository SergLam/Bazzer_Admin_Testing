package seniormanager;

import main.MainClass;
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

public class SeniorManagerEditGoods {

    private static ChromeDriverService service;
    private static WebDriver driver;
    // Логины старших менеджеров
    private ArrayList<String> list_of_excel_logins = new ArrayList<>();
    private int goodsCount = 0;


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
//        service.stop();
    }

    @Before
    public void createDriver() {
        System.setProperty("webdriver.chrome.driver", MainClass.getChromeDriverPath());
        driver = new RemoteWebDriver(service.getUrl(),
                DesiredCapabilities.chrome());
    }

    @After
    public void quitDriver() {
//        driver.quit();
    }


    @Test
    public void LoginInManager() throws Throwable {
        readExcelFilesWithManagersLogins();
        list_of_excel_logins.set(0, "provider2");
        for (int i = 0; i < 1/*list_of_excel_logins.size()*/; i++) {
            loginInManager(i);
            goToGoodsPage();
            goodsCount = getAllGoodsFromTable();
            if (goodsCount == 0) {
                logoutSeniorManager();
            } else {
                for (int j = 0; j < goodsCount; j++) {
                    if (j > 0) {
                        goToGoodsPage();
                        clickEditButton(j);
                        clickSaveButton();
                    } else {
                        clickEditButton(j);
                        clickSaveButton();
                    }
                }
            }
            logoutSeniorManager();
        }
    }

    private void clickSaveButton() {
        driver.manage().timeouts().implicitlyWait(1500, TimeUnit.MILLISECONDS);
        WebElement form = driver.findElement(By.tagName("form"));
        form.submit();
        driver.manage().timeouts().implicitlyWait(1500, TimeUnit.MILLISECONDS);
    }

    private void clickEditButton(int i) {

        driver.get(MainClass.BASE_URL_MANAGER + "/inf/product/mr");
        driver.manage().timeouts().implicitlyWait(1500, TimeUnit.MILLISECONDS);
        WebElement table = driver.findElement(By.id("user_list_js"));
        List<WebElement> table_row = table.findElements(By.tagName("tr"));
        List<WebElement> row_cells = table_row.get(i).findElements(By.tagName("td"));

        for (int k = 0; k < row_cells.size(); k++) {
            List<WebElement> inputs = row_cells.get(row_cells.size() - 1).findElements(By.tagName("input"));
            for(int u = 0; u < inputs.size(); u++){
               if(inputs.get(u).getCssValue("value").equals("редактировать")){
                   JavascriptExecutor executor = (JavascriptExecutor) driver;
                   executor.executeScript("arguments[0].click();", inputs.get(u));
                   return;
               }
            }
        }

    }

    private void loginInManager(int j) {
        driver.get(MainClass.BASE_URL_MANAGER);
        driver.findElement(By.name("login")).sendKeys(list_of_excel_logins.get(j));
        driver.findElement(By.name("password")).sendKeys(list_of_excel_logins.get(j));
        driver.findElement(By.tagName("form")).submit();
        driver.manage().timeouts().implicitlyWait(2000, TimeUnit.MILLISECONDS);
    }

    public void goToGoodsPage() {
        //Scroll page to top
        JavascriptExecutor jse = (JavascriptExecutor) driver;
        jse.executeScript("window.scrollBy(0,250)", "");
        // Press "товары" button
        driver.findElement(By.cssSelector("#left_menu > a:nth-child(5) > input:nth-child(1)")).click();
        driver.manage().timeouts().implicitlyWait(1000, TimeUnit.MILLISECONDS);
    }

    public int getAllGoodsFromTable() {
        WebElement table = driver.findElement(By.id("user_list_js"));
        List<WebElement> table_row = table.findElements(By.tagName("tr"));
        return table_row.size();
    }


    private void readExcelFilesWithManagersLogins() {
        // Read all files in directory
        Path output_path = Paths.get(MainClass.OUTPUT_FOLDER);
        File f = new File(output_path.toAbsolutePath().toString());
        File[] files = f.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].getName().contains("delivery_public_manager_root")) {
                list_of_excel_logins = MainClass.readFromExcelFile(files[i].getAbsolutePath());
            }
        }
    }

    public void logoutSeniorManager() {
        driver.findElement(By.cssSelector("#logout > a:nth-child(1) > input:nth-child(1)")).click();
        driver.manage().timeouts().implicitlyWait(1500, TimeUnit.MILLISECONDS);
    }

}
