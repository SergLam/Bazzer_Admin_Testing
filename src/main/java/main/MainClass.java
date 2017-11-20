package main;

import admin.AdminAddBrands;
import admin.AdminAddNews;
import admin.AdminAddProfession;
import admin.AdminAddProvider;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.junit.runner.JUnitCore;
import provider.ProviderActivateSnrMgrTrademarks;
import provider.ProviderActivateTrademarks;
import provider.ProviderAddNews;
import provider.ProviderAddSeniorManager;
import seniormanager.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;

public class MainClass {

    public static final String BASE_URL_BOSS = "https://admin.buzzer.online";
    public static final String BASE_URL_MANAGER = "https://manager.buzzer.online";

    // Driver path
    private static final String CHROME_DRIVER_PATH_UNIX = "src/main/resources/chrome_driver";
    private static final String CHROME_DRIVER_PATH_WIN = "src\\main\\resources\\chrome_driver";
    // Driver files
    private static final String CHROME_DRIVER_MAC = "/chromedriver_mac";
    private static final String CHROME_DRIVER_LINUX_32 = "/chromedriver_linux32";
    private static final String CHROME_DRIVER_LINUX_64 = "/chromedriver_linux64";
    private static final String CHROME_DRIVER_WIN = "\\chromedriver.exe";
    // Photo folders path
    public static final String BRAND_PHOTO_PATH = "src/main/resources/brands_photo/";
    public static final String GOODS_PHOTO_PATH = "src/main/resources/goods_photo/";
    public static final String NEWS_PHOTO_PATH = "src/main/resources/news_photo/";
    public static final String PROFILE_PHOTO_PATH = "src/main/resources/profile_photo/";
    // Logins templates
    public static final String PROVIDER_LOGIN = "provider";
    public static final String SENIOR_MANAGER_LOGIN = "snrmgr";
    public static final String JUNIOR_MANAGER_LOGIN = "jnrmgr";
    // Excel documents names
    public static final String EXCEL_FILE_EXTENSION = ".xlsx";
    public static final String OUTPUT_FOLDER = "output/";
    public static final String PROVIDERS_FILE_PATH = "output/ProviderLogins.xlsx";
    public static final String SENIOR_MANAGER_FILE_PATH = "output/SnrMgrOf";
    public static final String SENIOR_MANAGER_FILE_NAME = "SnrMgrOf";
    public static final String JUNIOR_MANAGER_FILE_PATH = "output/JnrMgrOf_";
    // SUPER-ADMIN LOGIN AND PASSWORD
    public static final String ADMIN_LOGIN = "admintest";
    public static final String ADMIN_PASSWORD = "admintest";


    public static void main(String args[]) {
        JUnitCore junit = new JUnitCore();
//         Admin functions test
//        junit.run(AdminAddBrands.class);
//        junit.run(AdminAddProfession.class);
//
//        junit.run(AdminAddProvider.class);
//
//        junit.run(AdminAddNews.class);


        // Provider functions test
//        junit.run(ProviderActivateTrademarks.class);
//        junit.run(ProviderAddSeniorManager.class);
//        junit.run(ProviderActivateSnrMgrTrademarks.class);
//
//        junit.run(ProviderAddNews.class);

        // Senior manager functions test
//        junit.run(SeniorManagerAddJuniorManager.class);
        junit.run(SeniorManagerAddGoods.class);
//        junit.run(SeniorManagerApproveUsers.class);
//        junit.run(SeniorManagerApproveOrders.class);
//        junit.run(SeniorManagerAddNews.class);

    }

    public static String getChromeDriverPath() {

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
        if (isMac || isLinux) {
            chromeDriverDirectory = Paths.get(CHROME_DRIVER_PATH_UNIX);
        }
        if (isWin) {
            chromeDriverDirectory = Paths.get(CHROME_DRIVER_PATH_WIN);
        }

        String chromeDriverPath = chromeDriverDirectory.toAbsolutePath().toString();

        // Detect which driver to use
        if (isMac) {
            chromeDriverPath = chromeDriverPath.concat(CHROME_DRIVER_MAC);
        }

        if (isLinux) {
            if (is32) {
                chromeDriverPath = chromeDriverPath.concat(CHROME_DRIVER_LINUX_32);
            }
            if (is64) {
                chromeDriverPath = chromeDriverPath.concat(CHROME_DRIVER_LINUX_64);
            }
        }

        if (isWin) {
            chromeDriverPath = chromeDriverPath.concat(CHROME_DRIVER_WIN);
        }

        return chromeDriverPath;
    }

    // ONLY FOR ONE COLUMN FILES !!!
    public static ArrayList<String> readFromExcelFile(String file_path) {
        ArrayList<String> result = new ArrayList<>();
        File myFile = new File(file_path);
        try {
            FileInputStream fis = new FileInputStream(myFile);
            // Find file
            XSSFWorkbook myWorkBook = new XSSFWorkbook(fis);
            // Return first sheet
            XSSFSheet mySheet = myWorkBook.getSheetAt(0);
            // Get iterator to all the rows in current sheet
            Iterator<Row> rowIterator = mySheet.iterator();
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();

                // For each row, iterate through each colums
                Iterator<Cell> cellIterator = row.cellIterator();

                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();

                    switch (cell.getCellType()) {
                        case Cell.CELL_TYPE_STRING:
                            result.add(cell.getStringCellValue());
                            break;
                        default:
                            break;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    // ONLY FOR ONE COLUMN FILES!!!
    public static void saveToExcelFile(String file_path, ArrayList<String> data) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        try {
            XSSFSheet sheet = workbook.createSheet("Logins");
            int row_num = sheet.getLastRowNum();

            for (String s : data) {
                Row row = sheet.createRow(row_num++);
                int cell_num = 0;
                Cell cell = row.createCell(cell_num++);
                cell.setCellValue((String) s);
            }
            // open an OutputStream to save written data into Excel file
            FileOutputStream os = new FileOutputStream(file_path);
            workbook.write(os);

            // Close workbook, OutputStream and Excel file to prevent leak
            os.close();
            workbook.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
