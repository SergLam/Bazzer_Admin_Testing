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
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Provider {

    private static ChromeDriverService service;
    private static WebDriver driver;

    // Список логинов провайдеров из файла
    private static ArrayList<String> providers_login = getProvidersLogins();
    private static ArrayList<String> senior_managers_logins = new ArrayList<>();
    // Список основных городов
    String[] main_cities = {"Черновцы", "Чернигов", "Черкассы", "Хмельницкий", "Херсон", "Харьков", "Тернополь",
            "Сумы", "Ровно", "Полтава", "Одесса", "Львов", "Кировоград", "Киев", "Ивано-Франковск",
            "Запорожье", "Ужгород", "Житомир", "Днепропетровск", "Луцк", "Винница"};

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

        for (int i = 0; i < providers_login.size(); i++) {

            String provider_login = providers_login.get(i);

            driver.manage().window().maximize();
            driver.get(MainClass.BASE_URL_BOSS);
            driver.manage().timeouts().implicitlyWait(2000, TimeUnit.MILLISECONDS);
            driver.findElement(By.name("login")).sendKeys(provider_login);
            driver.findElement(By.name("password")).sendKeys(provider_login);
            driver.findElement(By.tagName("form")).submit();
            driver.manage().timeouts().implicitlyWait(2000, TimeUnit.MILLISECONDS);

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

//            gotoTradeMarkPage();
//            selectAllTrademarks();

            gotoSeniorManagerPage();
            editSeniorManagerBrandsActivateAll();

//            gotoSeniorManagerPage();
//
//            for (int j = 0; j < main_cities.length; j++) {
//                int city_index = new Random().nextInt(main_cities.length);
//                try {
//                    addSeniorManager(city_index, provider_login);
//                } catch (Throwable t) {
//                    t.printStackTrace();
//                } finally {
//                    if (senior_managers_logins.size() > 0 && j > main_cities.length - 3) {
//                        Path logins_path = Paths.get("output/SnrMgrOf" + provider_login + ".xlsx");
//                        MainClass.saveToExcelFile(logins_path.toString(), senior_managers_logins);
//                    }
//                }
//            }
            logoutProvider();
        }
    }

    private void editSeniorManagerBrandsActivateAll() {
        // Ищем все кнопки с именем "торговые марки"
        List<WebElement> inputs = driver.findElements(By.tagName("input"));
        for (int i = 0; i < inputs.size(); i++) {
            // Если на кнопке написано "торговые марки" и она не слева
            if (inputs.get(i).getAttribute("value").equals("торговые марки") && inputs.get(i).getLocation().x > 500) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", inputs.get(i));
                driver.manage().timeouts().implicitlyWait(2000, TimeUnit.MILLISECONDS);
                // Найти таблицу
                WebElement table = driver.findElement(By.tagName("table"));
                // Найти все строки таблицы
                List<WebElement> tableRows = table.findElements(By.tagName("tr"));
                for (int j = 1; j < tableRows.size(); j++) {
                    // Найти секции в строке
                    List<WebElement> tableDiv = tableRows.get(j).findElements(By.tagName("td"));
                    // В первой секции - там чек-бокс
                    List<WebElement> check_box = tableDiv.get(0).findElements(By.tagName("input"));
                    // Найти и кликнуть на все чек-боксы
                    for (WebElement check : check_box) {
                        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", check);

                    }
                }
                // Вернуться на прошлую страницу
                ((JavascriptExecutor) driver).executeScript("window.history.go(-1)");
                inputs = driver.findElements(By.tagName("input"));
            } else {

            }
        }
    }








    private void gotoTradeMarkPage() {
        // Нажать кнопку "торговые марки" в навигационной панели
        driver.findElement(By.cssSelector("#left_menu > a:nth-child(5) > input:nth-child(1)")).click();
        driver.manage().timeouts().implicitlyWait(2000, TimeUnit.MILLISECONDS);
    }

    private void selectAllTrademarks() {
        // Кликнуть в поле для разворачивания списка
        driver.findElement(By.cssSelector(".ms-options-wrap > button:nth-child(1)")).click();
        // Кликнуть на "select all"
        driver.findElement(By.xpath("/html/body/div[6]/div/form/div/div/a")).click();
        // Нажать на "сохранить"
        driver.findElement(By.cssSelector("#content > div:nth-child(1) > form:nth-child(1) > input:nth-child(1)")).click();
        driver.manage().timeouts().implicitlyWait(2000, TimeUnit.MILLISECONDS);
    }

    public void gotoSeniorManagerPage() {
        driver.findElement(By.cssSelector("#left_menu > a:nth-child(3) > input:nth-child(1)")).click();
        driver.manage().timeouts().implicitlyWait(1500, TimeUnit.MILLISECONDS);
    }

    public void addSeniorManager(int city_index, String provider_login) {
        //Scroll page to top
        JavascriptExecutor jse = (JavascriptExecutor) driver;
        jse.executeScript("window.scrollBy(0,250)", "");

        // Нажать кнопку "Добавить старшего менеджера"
        driver.findElement(By.xpath("/html/body/div[6]/a/button")).click();
        driver.manage().timeouts().implicitlyWait(1000, TimeUnit.MILLISECONDS);

        // Выбрать из списка городов рандомно один из главных
        // Кликнуть по выпадающему списку
        driver.findElement(By.tagName("span")).click();
        // В появивщееся поле вбить значение нашего города для поиска
        driver.findElement(By.cssSelector(".chosen-search > input:nth-child(1)")).sendKeys(main_cities[city_index]);
        // Поиск выдает нужный город - делаем по нему клик
        driver.findElement(By.tagName("em")).click();
        // F.I.O
        driver.findElement(By.name("name")).sendKeys("Старший менеджер " + main_cities[city_index]);
        driver.findElement(By.name("login")).sendKeys("snr_mgr" + String.valueOf(city_index) + provider_login);
        driver.findElement(By.name("password")).sendKeys("snr_mgr" + String.valueOf(city_index) + provider_login);
        driver.findElement(By.id("phone")).clear();
        driver.findElement(By.id("phone")).sendKeys("0" + String.valueOf(new Random().nextInt((999999999 - 100000000) + 1) + 100000000));
        driver.findElement(By.name("information")).sendKeys("Информация о Старший менеджер " + main_cities[city_index]);
        driver.findElement(By.name("work_time")).sendKeys("Время работы Старший менеджер " + main_cities[city_index]);
        driver.findElement(By.name("is_activate")).click();
        // Submit the form
        driver.findElement(By.tagName("form")).submit();
        senior_managers_logins.add("snr_mgr" + String.valueOf(city_index) + provider_login);
        driver.manage().timeouts().implicitlyWait(2000, TimeUnit.MILLISECONDS);

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

    public void logoutProvider() {
        driver.findElement(By.cssSelector("#logout > a:nth-child(1) > input:nth-child(1)")).click();
        driver.manage().timeouts().implicitlyWait(2000, TimeUnit.MILLISECONDS);
    }
}
