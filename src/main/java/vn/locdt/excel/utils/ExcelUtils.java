package vn.locdt.excel.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;

public class ExcelUtils {

    @SuppressWarnings("unchecked")
    public static <T> T getCellValue(Cell cell, Class<?> type) {
        if (cell == null)
            return null;

        final CellType cellType = cell.getCellType();

        if (CellType.BLANK == cellType) {
            return null;
        }

        final DataFormatter formatter = new DataFormatter();
        Workbook wb = cell.getRow().getSheet().getWorkbook();
        FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();
        CellType formulaResultType = cellType == CellType.FORMULA ? cell.getCachedFormulaResultType() : CellType._NONE;

        // return cell's value without casting if:
        // 	- type is String or Double
        //	- type is the same as cell's data type
        if ((cellType == CellType.NUMERIC || formulaResultType == CellType.NUMERIC) &&
                (type == null || Double.class.equals(type))) {
            return (T) Double.valueOf(cell.getNumericCellValue());
        }

        if ((cellType == CellType.STRING || formulaResultType == CellType.STRING) &&
                (type == null || String.class.equals(type))) {
            return (T) cell.getRichStringCellValue().getString();
        }

        // Other cases, get string-formatted value and try to cast to expected type
        Object value;
        String rawValue = formatter.formatCellValue(cell, evaluator);
        if (Double.class.equals(type)) {
            value = StringUtils.isEmpty(rawValue) ? 0 : Double.parseDouble(rawValue);
        } else if (Integer.class.equals(type)) {
            value = StringUtils.isEmpty(rawValue) ? 0 : Integer.parseInt(rawValue);
        } else {
            value = rawValue;
        }

        return (T) value;
    }

    public static Object getCellValue(Cell cell) {
        return getCellValue(cell, null);
    }

    public static String getReference(Cell cell) {
        return new CellReference(cell.getRowIndex(), cell.getColumnIndex()).formatAsString();
    }
}
