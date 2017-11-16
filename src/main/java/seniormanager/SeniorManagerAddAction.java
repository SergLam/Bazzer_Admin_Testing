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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class SeniorManagerAddAction {

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
        // driver.quit();
    }


    @Test
    public void LoginInManager() throws Throwable {
        readExcelFilesWithManagersLogins();

        Path photo_path = Paths.get(MainClass.NEWS_PHOTO_PATH);
        File f = new File(photo_path.toAbsolutePath().toString());
        File[] files = f.listFiles();

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

                        for(int k=1;k<files.length;k++){
                            // Add news by seniormanager
                            addAction(logins.get(j), files[k].getName());
                        }

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
//
//        // Add news by seniormanager
//        for (int i = 0; i < files.length; i++) {
//            addAction("provider2", files[i].getName());
//        }


    }

    private void addAction(String mng_log, String file_name) {
        // Нажать кнопку "Новости" в навигационной панели
        driver.findElement(By.cssSelector("#left_menu > a:nth-child(9) > input:nth-child(1)")).click();
        // Нажать кнопку "добавить новость"
        driver.findElement(By.xpath("/html/body/div[6]/a/button")).click();
        driver.manage().timeouts().implicitlyWait(500, TimeUnit.MILLISECONDS);
        // Нажать радиобаттон "новость"
        driver.findElement(By.id("share")).click();
        // Найти форму - в дальнейшем искать элементы на ней
        WebElement table = driver.findElement(By.tagName("form"));

        // Рандомный номер для новости
        int num = new Random().nextInt(1000);

        List<WebElement> inputs = table.findElements(By.tagName("input"));

        String start_date = generateRandomDate();

        for (WebElement element : inputs) {
            String name = element.getAttribute("name");
            if (name.equals("date_start")) {
                element.sendKeys(start_date);
            }
            if (name.equals("date_end")) {
                element.sendKeys(datePlusMounth(start_date));
            }
            if (name.equals("title")) {
                element.sendKeys("Акция от " + mng_log + " " + String.valueOf(num));
            }
            if (name.equals("file")) {
                Path photo_path = Paths.get(MainClass.NEWS_PHOTO_PATH + file_name);
                element.sendKeys(photo_path.toAbsolutePath().toString());
            }
        }

        List<WebElement> textareas = table.findElements(By.tagName("textarea"));
        for (WebElement element : textareas) {
            String name = element.getAttribute("name");
            if (name.equals("description")) {
                element.sendKeys("Описание акции от " + mng_log + " " + String.valueOf(num));
            }
        }

        table.submit();
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

    private String generateRandomDate() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        LocalDate startDate = LocalDate.now(); // start date
        long start = startDate.toEpochDay();

        LocalDate endDate = LocalDate.of(LocalDate.now().plusYears(1).getYear(), LocalDate.now().plusYears(1).getMonth(), LocalDate.now().plusYears(1).getDayOfMonth()); //end date
        long end = endDate.toEpochDay();

        long randomEpochDay = ThreadLocalRandom.current().longs(start, end).findAny().getAsLong();

        String date = LocalDate.ofEpochDay(randomEpochDay).format(dtf);

        return date;
    }

    private String datePlusMounth(String date) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        String result = "";
        try {
            Date date1 = formatter.parse(date);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date1);
            calendar.add(Calendar.MONTH, 1);

            Date newDate = calendar.getTime();

            result = new SimpleDateFormat("dd.MM.yyyy").format(newDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

}
