package vn.locdt.excel.reader.exception;

import org.apache.poi.ss.usermodel.Cell;
import vn.locdt.excel.utils.ExcelUtils;

import java.util.List;
import java.util.stream.Collectors;

public class CellConverterException extends Exception {
    private List<Object> cellValues;
    private List<Cell> cells;
    private Class<?> entityClass;
    private Class<?> fieldType;
    private String fieldName;

    public CellConverterException(List<Object> cellValues, List<Cell> cells, Class<?> fieldType) {
        super();
        this.cells = cells;
        this.cellValues = cellValues;
        this.fieldType = fieldType;
    }

    public CellConverterException(List<Object> cellValues, List<Cell> cells, Class<?> fieldType, Throwable e) {
        super(e);
        this.cells = cells;
        this.cellValues = cellValues;
        this.fieldType = fieldType;
    }

    public List<Object> getCellValues() {
        return this.cellValues;
    }

    public void setCellValues(List<Object> cellValues) {
        this.cellValues = cellValues;
    }

    public List<Cell> getCells() {
        return this.cells;
    }

    public void setCells(List<Cell> cells) {
        this.cells = cells;
    }

    public Class<?> getEntityClass() {
        return this.entityClass;
    }

    public void setEntityClass(Class<?> entityClass) {
        this.entityClass = entityClass;
    }

    public Class<?> getFieldType() {
        return this.fieldType;
    }

    public void setFieldType(Class<?> fieldType) {
        this.fieldType = fieldType;
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String getMessage() {
        String rawValueStr = this.cellValues.stream().map(Object::toString).collect(Collectors.joining());
        String cellStr = this.cells.stream().map(ExcelUtils::getReference).collect(Collectors.joining());
        return String.format("Can not convert cells [%s] (%s) to %s.%s",
                cellStr,
                rawValueStr,
                this.entityClass.getSimpleName(),
                this.fieldName);
    }
}
