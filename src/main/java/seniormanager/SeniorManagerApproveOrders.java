package seniormanager;

import main.MainClass;
import model.TableOrderSearch;
import org.junit.*;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
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

public class SeniorManagerApproveOrders {

    private TableOrderSearch searchResult;
    private static ChromeDriverService service;
    private static WebDriver driver;
    // Логины старших менеджеров
    private ArrayList<ArrayList<String>> list_of_excel_logins = new ArrayList<>();

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
            for (int i = 0; i < list_of_excel_logins.size(); i++) {
                ArrayList<String> logins = list_of_excel_logins.get(i);
                for (int j = 0; j < logins.size(); j++) {
                    try{
                        driver.get(MainClass.BASE_URL_MANAGER);
                        driver.findElement(By.name("login")).sendKeys(logins.get(j));
                        driver.findElement(By.name("password")).sendKeys(logins.get(j));
                        driver.findElement(By.tagName("form")).submit();
                        driver.manage().timeouts().implicitlyWait(2000, TimeUnit.MILLISECONDS);

                        // Подтверждаем все заказы
                        goToOrders();
                        searchResult = isUnApproved();
                        while (searchResult.isUnaproved){
                            approveOrder(searchResult);
                        }

                        // Выходим из менеджера
                        logoutSeniorManager();
                    } catch (NoSuchElementException e){
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void approveOrder(TableOrderSearch searchResult) {
        driver.manage().timeouts().implicitlyWait(2000, TimeUnit.MILLISECONDS);
        searchResult.details_button.click();
        driver.findElement(By.cssSelector("#row_download > td:nth-child(1) > a:nth-child(1) > input:nth-child(1)")).click();
        driver.manage().timeouts().implicitlyWait(1000, TimeUnit.MILLISECONDS);
        driver.get(MainClass.BASE_URL_MANAGER + "/orders/mr");
        driver.manage().timeouts().implicitlyWait(2000, TimeUnit.MILLISECONDS);
        this.searchResult = isUnApproved();
    }

    private void goToOrders() {
        driver.findElement(By.id("count_or")).click();
        driver.manage().timeouts().implicitlyWait(2000, TimeUnit.MILLISECONDS);
    }

    private TableOrderSearch isUnApproved() {

        TableOrderSearch result = new TableOrderSearch();
        result.isUnaproved = false;

        // Find table on web-page
        try {
            WebElement table = driver.findElement(By.tagName("table"));
            List<WebElement> rows_table = table.findElements(By.tagName("tr"));
            // Calculate table rows count
            int rows_count = rows_table.size();  // -1 cause it title row
            // Iterate through table and search green colored rows
            for (int i = 1; i < rows_count; i++) {
                //System.out.println(rows_table.get(i).getAttribute("style"));
                if (rows_table.get(i).getAttribute("style").equals("background-color: rgb(204, 255, 102);")) {
                    result.isUnaproved = true;
                    List<WebElement> cells = rows_table.get(i).findElements(By.tagName("a"));
                    WebElement button_details = cells.get(0);
                    result.details_button = button_details;
                }
            }
        } catch (Throwable t){
            result.isUnaproved = false;
            t.printStackTrace();
        }
        return result;
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
