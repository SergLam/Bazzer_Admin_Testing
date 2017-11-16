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

public class SeniorManager {

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
                    driver.manage().window().maximize();
                    driver.get(MainClass.BASE_URL_MANAGER);
                    driver.findElement(By.name("login")).sendKeys(logins.get(j));
                    driver.findElement(By.name("password")).sendKeys(logins.get(j));
                    driver.findElement(By.tagName("form")).submit();
                    driver.manage().timeouts().implicitlyWait(2000, TimeUnit.MILLISECONDS);

                    // Добавление товаров в рандомные категории
                    // Кол-во товаров равно кол-ву картинок в папке
                    Path photo_path = Paths.get("src/main/resources/goods_photo/");
                    File f = new File(photo_path.toAbsolutePath().toString());
                    File[] files = f.listFiles();

                    goToAddGood();
                    for (int k = 1; k < files.length; k++) {
                        addGood(files[k].getName());
                    }

                    // Добавляем младшего менеджера
                    goToAddJuniorManager();
                    for (int o = 1; o < 5; o++) {
                        addJuniorManager(o, logins.get(j));
                    }
                    // Сохраняем логины младших менеджеров в файл
                    Path logins_path = Paths.get("output/JnrMgrOf_" + logins.get(j)+".xlsx");
                    MainClass.saveToExcelFile(logins_path.toString(), juniorManagers_logins);

                    // Выходим из менеджера
                    logoutSeniorManager();
                }
            }
        }

//        driver.manage().window().maximize();
//        driver.get(MainClass.BASE_URL_MANAGER);
//        driver.findElement(By.name("login")).sendKeys("provider2");
//        driver.findElement(By.name("password")).sendKeys("provider2");
//        driver.findElement(By.tagName("form")).submit();
//        driver.manage().timeouts().implicitlyWait(2000, TimeUnit.MILLISECONDS);

//        // Подтверждаем все заказы
//        goToOrders();
//        searchResult = isUnApproved();
//        while (searchResult.isUnaproved){
//            approveOrder(searchResult);
//        }


//        // Принимаем все заявки мастеров
//        // ПЕРЕД АПРУВОМ ДОЛЖЕН БЫТЬ ХОТЯ БЫ ОДИН МЛАДШИЙ МЕНЕДЖЕР
//        approveAllUsersToMaster();

    }

    private void approveAllUsersToMaster() {
        // нажать кнопку "Хотят быть мастерами"
        boolean isUnuproved = true;
        while (isUnuproved) {
            driver.findElement(By.id("count_bid")).click();
            driver.manage().timeouts().implicitlyWait(1000, TimeUnit.MILLISECONDS);
            List<WebElement> listOfinputs = driver.findElements(By.tagName("input"));
            // Проверяем если ли еще не аппрувленные заявки
            int counter = 0;
            int pos = 0;
            for (int i = 0; i < listOfinputs.size(); i++) {
                if (listOfinputs.get(i).getAttribute("value").equals("принять заявку")) {
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


    private void addJuniorManager(int number, String snr_mg_login) {
        //Scroll page to top
        JavascriptExecutor jse = (JavascriptExecutor) driver;
        jse.executeScript("window.scrollBy(0,250)", "");
        driver.manage().timeouts().implicitlyWait(500, TimeUnit.MILLISECONDS);
        // Нажать кнопку "Добавить младшиго менеджера"
        driver.findElement(By.xpath("/html/body/div[6]/a/button")).click();
        driver.manage().timeouts().implicitlyWait(500, TimeUnit.MILLISECONDS);
        // Заполняем форму
        driver.findElement(By.name("name")).sendKeys("Младший менеджер" + String.valueOf(number));
        driver.findElement(By.name("login")).sendKeys("jnrmgr" + String.valueOf(number) + snr_mg_login);
        driver.findElement(By.name("password")).sendKeys("jnrmgr" + String.valueOf(number) + snr_mg_login);
        driver.findElement(By.id("phone")).sendKeys("0" + String.valueOf(new Random().nextInt((999999999 - 100000000) + 1) + 100000000));
        driver.findElement(By.name("information")).sendKeys("Инфонмация о младшем менеджере " + "juniormanager" + String.valueOf(number) + snr_mg_login);
        driver.findElement(By.name("work_time")).sendKeys("Время работы " + "juniormanager" + String.valueOf(number) + snr_mg_login);
        // Отправляем форму
        driver.findElement(By.tagName("form")).submit();
        // Заносим логин в массив
        juniorManagers_logins.add("juniormanager" + String.valueOf(number) + snr_mg_login);
        // Ждем
        driver.manage().timeouts().implicitlyWait(2000, TimeUnit.MILLISECONDS);
    }

    private void goToAddJuniorManager() {
        driver.findElement(By.cssSelector("#left_menu > a:nth-child(3) > input:nth-child(1)")).click();
        driver.manage().timeouts().implicitlyWait(1000, TimeUnit.MILLISECONDS);
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

        return result;
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
        Path photo_path = Paths.get("src/main/resources/goods_photo/" + file_name);
        driver.findElement(By.name("file")).sendKeys(photo_path.toAbsolutePath().toString());

        driver.findElement(By.tagName("form")).submit();
        driver.manage().timeouts().implicitlyWait(2000, TimeUnit.MILLISECONDS);

    }

    private void readExcelFilesWithManagersLogins() {
        // Read all files in directory
        Path output_path = Paths.get("output/");
        File f = new File(output_path.toAbsolutePath().toString());
        File[] files = f.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].getName().contains("SnrMgrOfprovider")) {
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
