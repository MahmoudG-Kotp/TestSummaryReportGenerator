import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.plot.RingPlot;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.xy.XYSeriesCollection;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTable;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableColumn;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableColumns;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import static org.apache.poi.ss.SpreadsheetVersion.EXCEL2007;

//Add from libraries tab
/* https://mvnrepository.com/artifact/org.apache.poi/poi-ooxml/5.2.4 */
/* https://mvnrepository.com/artifact/org.apache.poi/ooxml-schemas/1.4 */
/* https://mvnrepository.com/artifact/jfree/jfreechart */

/**
 * Represents a utility class for creating Excel files using Apache POI library.
 */
public class ExcelFile {
    private XSSFWorkbook excelSourceFile; // The Excel workbook
    private FileOutputStream fileOutput; // Output stream for writing the Excel file

    /**
     * Constructor for ExcelFile.
     *
     * @param name The name of the Excel file to be created.
     */
    public ExcelFile(String path, String name) {
        try {
            // Initialize the output stream and Excel workbook
            fileOutput = new FileOutputStream(path + name + ".xlsx");
            excelSourceFile = new XSSFWorkbook();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a new sheet in the Excel workbook.
     *
     * @param name The name of the sheet.
     * @return The created XSSFSheet.
     */
    public XSSFSheet createSheet(String name) {
        return excelSourceFile.createSheet(name);
    }

    /**
     * Creates a table in the specified XSSFSheet with the given parameters.
     *
     * @param sheet        The XSSFSheet in which to create the table.
     * @param displayName  The display name of the table.
     * @param startCell    The starting cell reference for the table (e.g., "A5").
     * @param endCell      The ending cell reference for the table (e.g., "D6").
     * @param columnsCount The number of columns in the table.
     * @param columnsData  An ArrayList containing the names of the table's columns.
     * @param rowsCount    The number of rows in the table.
     * @param cellsData    An ArrayList of ArrayLists containing the data for the table cells.
     */
    public void createTable(XSSFSheet sheet, String displayName, String startCell, String endCell,
                            int columnsCount, ArrayList<String> columnsData, int rowsCount, ArrayList<ArrayList<String>> cellsData) {
        CTTable table = sheet.createTable(null).getCTTable();

        table.setDisplayName(displayName);
        // Example A5:D6
        table.setRef(startCell + ":" + endCell);

        CTTableColumns columns = table.addNewTableColumns();
        columns.setCount(columnsCount);
        int startCellIndex = Integer.parseInt(startCell.substring(1));
        XSSFRow headerRow = sheet.createRow(startCellIndex - 1);
        String cellData;
        for (int columnCounter = 0; columnCounter < columnsCount; columnCounter++) {
            CTTableColumn column = columns.addNewTableColumn();
            column.setId(columnCounter);
            column.setName(columnsData.get(columnCounter));

            // Set the column width (adjustment based on columnCounter)
            sheet.setColumnWidth(columnCounter, columnCounter > 0 ? 12000 / (columnCounter + 1) : 14000);

            // Set the column header cell value
            headerRow.createCell(columnCounter).setCellValue(columnsData.get(columnCounter));
        }
        for (int rowCounter = 0; rowCounter < rowsCount; rowCounter++) {
            XSSFRow row = sheet.createRow(startCellIndex + rowCounter);
            for (int cellCounter = 0; cellCounter < columnsCount; cellCounter++) {
                XSSFCell cell = row.createCell(cellCounter);
                cellData = cellsData.get(rowCounter).get(cellCounter);
                if (cellData.contains("formula=")) {
                    cell.setCellFormula(cellData.substring(8));
                } else {
                    cell.setCellValue(cellData);
                }
            }
        }
    }

    /**
     * Creates a pie chart in the specified XSSFSheet with the given results.
     *
     * @param chartSheet The XSSFSheet in which to create the pie chart.
     */
    public void createTCsChart(XSSFSheet chartSheet) {
        DefaultPieDataset dataset = new DefaultPieDataset();

        final String PASS_CATEGORY_NAME = "Pass",
                FAIL_CATEGORY_NAME = "Fail",
                INCONCLUSIVE_CATEGORY_NAME = "Inconclusive";

        JFreeChart chart = ChartFactory.createPieChart3D(
                "Test Cases Chart",
                dataset,
                true,
                true,
                false
        );
        FormulaEvaluator evaluator = chartSheet.getWorkbook().getCreationHelper().createFormulaEvaluator();
        // Update the dataset with new data
        int passCount = (int) evaluator.evaluate(chartSheet.getRow(1).getCell(1)).getNumberValue();
        int failCount = (int) evaluator.evaluate(chartSheet.getRow(2).getCell(1)).getNumberValue();
        int inconclusiveCount = (int) evaluator.evaluate(chartSheet.getRow(3).getCell(1)).getNumberValue();

        dataset.setValue(PASS_CATEGORY_NAME, passCount);
        dataset.setValue(FAIL_CATEGORY_NAME, failCount);
        dataset.setValue(INCONCLUSIVE_CATEGORY_NAME, inconclusiveCount);

        PiePlot3D chartPlot = (PiePlot3D) chart.getPlot();
        chartPlot.setBackgroundPaint(Color.WHITE);
        chartPlot.setSectionPaint(PASS_CATEGORY_NAME, new Color(50, 182, 135));
        chartPlot.setSectionPaint(FAIL_CATEGORY_NAME, new Color(220, 0, 0));
        chartPlot.setSectionPaint(INCONCLUSIVE_CATEGORY_NAME, new Color(171, 115, 49));

        try {
            ByteArrayOutputStream chartImage = new ByteArrayOutputStream();
            ChartUtilities.writeChartAsPNG(chartImage, chart, 1000, 500);
            int pictureIDx = getExcelSourceFile().addPicture(chartImage.toByteArray(), getExcelSourceFile().PICTURE_TYPE_PNG);
            chartImage.close();
            ClientAnchor anchor = getExcelSourceFile().getCreationHelper().createClientAnchor();
            anchor.setCol1(6); // Adjust the column index as needed
            anchor.setRow1(2); // Adjust the row index as needed
            chartSheet.createDrawingPatriarch().createPicture(anchor, pictureIDx).resize();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a new row in the specified sheet.
     *
     * @param sheet The XSSFSheet to create the row in.
     * @return The created XSSFRow.
     */
    public XSSFRow createRow(XSSFSheet sheet) {
        return sheet.createRow(sheet.getLastRowNum() + 1);
    }

    /**
     * Gets the Excel workbook.
     *
     * @return The XSSFWorkbook representing the Excel file.
     */
    public XSSFWorkbook getExcelSourceFile() {
        return excelSourceFile;
    }

    /**
     * Writes the Excel workbook to the output file, flushes and closes the file streams.
     */
    public void createFile() {
        try {
            excelSourceFile.write(fileOutput);
            fileOutput.flush();
            fileOutput.close();
            excelSourceFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

