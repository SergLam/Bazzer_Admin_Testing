package seniormanager;

import main.MainClass;
import org.junit.*;
import org.openqa.selenium.By;
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

public class SeniorManagerDisApproveUsers {

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

                        // Принимаем все заявки мастеров
                        // ПЕРЕД АПРУВОМ ДОЛЖЕН БЫТЬ ХОТЯ БЫ ОДИН МЛАДШИЙ МЕНЕДЖЕР
                        disApproveAllUsersToMaster();

                        // Выходим из менеджера
                        logoutSeniorManager();
                    } catch (Throwable t){
                        t.printStackTrace();
                    }
                }
            }
        }

//        driver.get(MainClass.BASE_URL_MANAGER);
//        driver.findElement(By.name("login")).sendKeys("provider2");
//        driver.findElement(By.name("password")).sendKeys("provider2");
//        driver.findElement(By.tagName("form")).submit();
//        driver.manage().timeouts().implicitlyWait(2000, TimeUnit.MILLISECONDS);

        // Принимаем все заявки мастеров
        // ПЕРЕД АПРУВОМ ДОЛЖЕН БЫТЬ ХОТЯ БЫ ОДИН МЛАДШИЙ МЕНЕДЖЕР
        disApproveAllUsersToMaster();

    }

    private void disApproveAllUsersToMaster() {
        // нажать кнопку "Список мастеров"
        boolean isUnuproved = true;
        while (isUnuproved) {
            driver.findElement(By.cssSelector("#left_menu > a:nth-child(13) > input:nth-child(1)")).click();
            driver.manage().timeouts().implicitlyWait(1000, TimeUnit.MILLISECONDS);
            List<WebElement> listOfinputs = driver.findElements(By.tagName("input"));
            // Проверяем если ли еще не аппрувленные заявки
            int counter = 0;
            int pos = 0;
            for (int i = 0; i < listOfinputs.size(); i++) {
                if (listOfinputs.get(i).getAttribute("value").equals("лишить привилегий")) {
                    counter++;
                    pos = i;
                }
            }
            if (counter == 0) {
                isUnuproved = false;
            } else {
                listOfinputs.get(pos).click();
                driver.manage().timeouts().implicitlyWait(2000, TimeUnit.MILLISECONDS);
            }
        }
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

}
