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

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class SeniorManagerAddJuniorManager {

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
            for (int i = 0; i < list_of_excel_logins.size(); i++) {
                ArrayList<String> logins = list_of_excel_logins.get(i);
                for (int j = 0; j < logins.size(); j++) {
                    try{
                        driver.get(MainClass.BASE_URL_MANAGER);
                        driver.findElement(By.name("login")).sendKeys(logins.get(j));
                        driver.findElement(By.name("password")).sendKeys(logins.get(j));
                        driver.findElement(By.tagName("form")).submit();
                        driver.manage().timeouts().implicitlyWait(2000, TimeUnit.MILLISECONDS);

                        // Добавляем младшего менеджера
                        goToAddJuniorManager();
                        for (int o = 58; o < 60; o++) {
                            String login = addJuniorManager(o, logins.get(j));
                            try {
                                // Проверяем, нет ли сообщения о том, что логин существует
                                WebElement login_exists = driver.findElement(By.xpath("/html/body/div[6]/p"));
                                if(login_exists.isDisplayed()){

                                } else {
                                    juniorManagers_logins.add(login);
                                }
                            } catch (Throwable t){
                                t.printStackTrace();
                            }
                        }

                        // Сохраняем логины младших менеджеров в файл
                        Path logins_path = Paths.get(MainClass.JUNIOR_MANAGER_FILE_PATH + logins.get(j)+MainClass.EXCEL_FILE_EXTENSION);
                        MainClass.saveToExcelFile(logins_path.toString(), juniorManagers_logins);
                        juniorManagers_logins.clear();

                        // Выходим из менеджера
                        logoutSeniorManager();
                    } catch (Throwable t){
                        t.printStackTrace();
                    }
                }
            }
        }

    }

    private String addJuniorManager(int number, String snr_mg_login) {
        //Scroll page to top
        JavascriptExecutor jse = (JavascriptExecutor) driver;
        jse.executeScript("window.scrollBy(0,250)", "");
        driver.manage().timeouts().implicitlyWait(500, TimeUnit.MILLISECONDS);
        // Нажать кнопку "Добавить младшиго менеджера"
        driver.findElement(By.xpath("/html/body/div[6]/a/button")).click();
        driver.manage().timeouts().implicitlyWait(500, TimeUnit.MILLISECONDS);
        // Заполняем форму
        driver.findElement(By.name("name")).sendKeys("Младший менеджер" + String.valueOf(number));
        driver.findElement(By.name("login")).sendKeys(MainClass.JUNIOR_MANAGER_LOGIN + String.valueOf(number) + snr_mg_login);
        driver.findElement(By.name("password")).sendKeys(MainClass.JUNIOR_MANAGER_LOGIN + String.valueOf(number) + snr_mg_login);
        driver.findElement(By.id("phone")).sendKeys("0" + String.valueOf(new Random().nextInt((999999999 - 100000000) + 1) + 100000000));
        driver.findElement(By.name("information")).sendKeys("Инфонмация о младшем менеджере " + MainClass.JUNIOR_MANAGER_LOGIN + String.valueOf(number) + snr_mg_login);
        driver.findElement(By.name("work_time")).sendKeys("Время работы " + MainClass.JUNIOR_MANAGER_LOGIN + String.valueOf(number) + snr_mg_login);
        // Отправляем форму
        driver.findElement(By.tagName("form")).submit();
        // Ждем
        driver.manage().timeouts().implicitlyWait(2000, TimeUnit.MILLISECONDS);
        String juniormanager_login = MainClass.JUNIOR_MANAGER_LOGIN + String.valueOf(number) + snr_mg_login;
        return juniormanager_login;
    }

    private void goToAddJuniorManager() {
        driver.findElement(By.cssSelector("#left_menu > a:nth-child(3) > input:nth-child(1)")).click();
        driver.manage().timeouts().implicitlyWait(1000, TimeUnit.MILLISECONDS);
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
