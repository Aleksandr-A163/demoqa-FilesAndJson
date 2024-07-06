import model.ShopData;
import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.codeborne.pdftest.assertj.Assertions.assertThat;

public class FilesParsingTest {

    private final ClassLoader cl = FilesParsingTest.class.getClassLoader();
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    @DisplayName("Проверка содержимого CSV-файла внутри ZIP-архива.")
    void zipValidateCsvTest() throws Exception{
        try (InputStream stream = cl.getResourceAsStream("testZIP.zip");
             ZipInputStream zis = new ZipInputStream(stream)) {
            ZipEntry entry;

            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().contains("csv")){
                    CSVReader csvReader = new CSVReader(new InputStreamReader(zis));
                    List<String[]> content = csvReader.readAll();
                    assertThat(content)
                            .isNotEmpty()
                            .hasSize(3);
                    assertThat(content.get(0))
                            .isEqualTo(new String[]{"Laptop", " Huawei"});
                }
            }
        }
    }

    @Test
    @DisplayName("Проверка содержимого XLSX внутри ZIP-архива")
    void zipValidateXlsxTest() throws Exception {
        try (InputStream stream = cl.getResourceAsStream("testZip.zip");
             ZipInputStream zis = new ZipInputStream(stream)) {
            ZipEntry entry;

            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().equals("testXLSX.xlsx")) {
                    XLS xls = new XLS(zis);
                    final String brandCaption = xls.excel
                            .getSheetAt(1).getRow(0).getCell(0).getStringCellValue(),
                            modelCaption = xls.excel.getSheetAt(1).getRow(0).getCell(1).getStringCellValue(),
                            coreCaption = xls.excel.getSheetAt(1).getRow(0).getCell(2).getStringCellValue(),
                            brand = xls.excel.getSheetAt(1).getRow(3).getCell(0).getStringCellValue(),
                            model = xls.excel.getSheetAt(1).getRow(3).getCell(1).getStringCellValue();
                    final int coreCount = (int) xls.excel.getSheetAt(1).getRow(3).getCell(2).getNumericCellValue();
                    final int sheetCount = xls.excel.getNumberOfSheets();
                    assertThat(brandCaption).isEqualTo("Brand");
                    assertThat(modelCaption).isEqualTo("model");
                    assertThat(coreCaption).isEqualTo("number of cores");
                    assertThat(brand).isEqualTo("Nvidia");
                    assertThat(model).isEqualTo("RTX 4090");
                    assertThat(coreCount).isEqualTo(16384);
                    assertThat(sheetCount).isEqualTo(2);
                }
            }
        }
    }

    @Test
@DisplayName("Check content of PDF inside zip archive")
void zipValidatePdfTest() throws Exception {
    try (InputStream stream = cl.getResourceAsStream("testZip.zip");
         ZipInputStream zis = new ZipInputStream(stream)) {
        ZipEntry entry;

        while ((entry = zis.getNextEntry()) != null) {
            if (entry.getName().contains("pdf")) {
                PDF pdf = new PDF(zis);
                assertThat(pdf.numberOfPages).isEqualTo(2);
                assertThat(pdf)
                        .containsExactText("SYNECT MEDIA LLC - DIGITAL SIGNAGE SOLUTIONS")
                        .containsExactText("Spearheaded API")
                        .containsExactText("Tel Aviv, Israel ")
                        .containsExactText("August 1997 - 1999")
                        .containsExactText("ISRAEL - PROJECT MANAGEMENT SOLUTIONS")
                        .containsExactText("Managed virtualization labs, ensuring regular updates and optimal performance");
                assertThat(pdf)
                        .doesNotContainExactText("military")
                        .doesNotContainExactText("jail");
                assertThat(pdf.author).isEqualTo(null);
            }
        }
    }
}


    @Test
    @DisplayName("Проверка данных в  Json объекте")
    void validateJsonTest() throws Exception {
        try (InputStream stream = cl.getResourceAsStream("testData.json");
            Reader reader = new InputStreamReader(stream)) {
            ShopData testData = mapper.readValue(reader, ShopData.class);
            assertThat(testData.getExchanges().size())
                    .isEqualTo(2);
            assertThat(testData.getAdditionalInfo().getTradingEnabledFlag())
                    .isTrue();
            assertThat(testData.getIsin())
                    .isEqualTo("US02079K3059");
            assertThat(testData.getName())
                    .isEqualTo("GOOGL");
            assertThat(testData.getExchanges())
                    .contains("NASDAQ", "TSE");
            assertThat(testData.getAdditionalInfo().getHasOptionsFlag())
            .isTrue();
        }
    }

}