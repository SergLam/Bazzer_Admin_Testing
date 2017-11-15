import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;

public class MainClass {

    public static final String BASE_URL_BOSS = "http://178.159.110.21:84";
    public static final String BASE_URL_MANAGER = "http://178.159.110.21:83";

    public static void main(String[] args) {

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
            chromeDriverDirectory = Paths.get("src/main/resources/chrome_driver");
        }
        if (isWin) {
            chromeDriverDirectory = Paths.get("src\\main\\resources\\chrome_driver");
        }

        String chromeDriverPath = chromeDriverDirectory.toAbsolutePath().toString();

        // Detect which driver to use
        if (isMac) {
            chromeDriverPath = chromeDriverPath.concat("/chromedriver_mac");
        }

        if (isLinux) {
            if (is32) {
                chromeDriverPath = chromeDriverPath.concat("/chromedriver_linux32");
            }
            if (is64) {
                chromeDriverPath = chromeDriverPath.concat("/chromedriver_linux32");
            }
        }

        if (isWin) {
            chromeDriverPath = chromeDriverPath.concat("\\chromedriver.exe");
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
            while (rowIterator.hasNext()){
                Row row = rowIterator.next();

                // For each row, iterate through each colums
                Iterator<Cell> cellIterator = row.cellIterator();

                while (cellIterator.hasNext()){
                    Cell cell = cellIterator.next();

                    switch (cell.getCellType()){
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
    public static void saveToExcelFile(String file_path, ArrayList<String> data){
        XSSFWorkbook workbook = new XSSFWorkbook();
        try {
            XSSFSheet sheet = workbook.createSheet("Logins");
            int row_num = sheet.getLastRowNum();

            for(String s : data){
              Row row = sheet.createRow(row_num++);
              int cell_num = 0;
              Cell cell = row.createCell(cell_num++);
              cell.setCellValue((String) s);
            }
            // open an OutputStream to save written data into Excel file
            FileOutputStream os = new FileOutputStream( file_path);
            workbook.write(os);
            System.out.println("Writing to excel finished");

            // Close workbook, OutputStream and Excel file to prevent leak
            os.close();
            workbook.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
