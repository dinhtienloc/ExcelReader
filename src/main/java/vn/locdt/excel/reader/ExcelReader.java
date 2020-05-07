package vn.locdt.excel.reader;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellReference;
import vn.locdt.excel.reader.converter.CellDataConverter;
import vn.locdt.excel.reader.exception.CellConverterException;
import vn.locdt.excel.reader.exception.ExcelMappingException;
import vn.locdt.excel.reader.exception.InvalidDataException;
import vn.locdt.excel.utils.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Utility class that allow reading, mapping and converting data in excel file to java object
 *
 * @param <E>
 */
public class ExcelReader<E> {
    private Class<E> clazz;
    private Map<String, Field> mapOfNameAndFields;
    private Field pkField;
    private Sheet sheet;
    private List<FieldInfo<?>> fieldInfos;

    ExcelReader(ExcelReaderBuilder<E> builder) {
        this.clazz = builder.getClazz();
        this.mapOfNameAndFields = ReflectionUtils.getMapOfFieldNameAndField(this.clazz);
        this.sheet = builder.getSheet();
        this.fieldInfos = builder.getFieldInfos();
    }

    /**
     * Map a row to an entity. This method requires to declare COLUMN references in reader.
     *
     * @param rowNum row number need to map
     * @return mapped entity
     * @throws InvalidDataException   throws if reading cell's value process has errors
     * @throws CellConverterException throw after reading cell process, if converting process has errors
     */
    public E readRow(int rowNum) throws InvalidDataException, CellConverterException {
        if (!this.isColumnReference()) {
            throw new ExcelMappingException("Mapping entity with row requires column reference");
        }

        return this.readRow(null, rowNum);
    }

    /**
     * Like {@link ExcelReader#readRow(int)} but this method reads a range of rows.
     *
     * @param fromRow start row to map
     * @param toRow   end row to map
     * @return list of mapped entities
     * @throws InvalidDataException   throws if reading cell's value process has errors
     * @throws CellConverterException throw after reading cell process, if converting process has errors
     */
    public List<E> readRow(int fromRow, int toRow) throws InvalidDataException, CellConverterException {
        List<E> entities = new ArrayList<>();
        if (!this.isColumnReference()) {
            throw new ExcelMappingException("Mapping entity with row requires column reference");
        }

        for (int rowNum = fromRow; rowNum <= toRow; rowNum++) {
            entities.add(this.readRow(null, rowNum));
        }
        return entities;
    }

    /**
     * Map cells directly to a new entity by using references declared in reader.
     *
     * @return mapped entity
     * @throws InvalidDataException   throws if reading cell's value process has errors
     * @throws CellConverterException throw after reading cell process, if converting process has errors
     */
    public E read() throws InvalidDataException, CellConverterException {
        if (this.isColumnReference()) {
            throw new ExcelMappingException("Mapping entity using cells requires cell reference");
        }

        return this.readRow(null, null);
    }

    /**
     * Map cells directly to the existed entity by using references declared in reader.
     *
     * @param entity the entity instance need to mapped
     * @throws InvalidDataException   throws if reading cell's value process has errors
     * @throws CellConverterException throw after reading cell process, if converting process has errors
     */
    public void read(E entity) throws InvalidDataException, CellConverterException {
        if (this.isColumnReference()) {
            throw new ExcelMappingException("Mapping entity using cells requires cell reference");
        }

        this.readRow(entity, null);
    }

    // map cells in a row to the entity instance

    /**
     * Map a row to an existed entity instance.
     * This method requires to declare COLUMN references in reader.
     *
     * @param entity entity instance
     * @param rowNum row number
     * @return mapped entity instance
     * @throws InvalidDataException   throws if reading cell's value process has errors
     * @throws CellConverterException throw after reading cell process, if converting process has errors
     */
    public E readRow(E entity, Integer rowNum) throws InvalidDataException, CellConverterException {
        E newEntity = entity != null ? entity : ReflectionUtils.newInstanceFromClass(this.clazz);
        for (FieldInfo<?> info : this.fieldInfos) {
            this.mapCellValue(newEntity, info, rowNum);
        }
        return newEntity;
    }

    // check if the declared references in the EntityReader is referenced to column or not
    private boolean isColumnReference() {
        for (FieldInfo<?> info : this.fieldInfos) {
            for (String ref : info.getReferences()) {
                for (char ch : ref.toCharArray()) {
                    if (Character.isDigit(ch))
                        return false;
                }
            }
        }
        return true;
    }

    /**
     * Read cell's values and map these value to result entity.
     *
     * @param entity    result entity
     * @param fieldInfo object holds the mapping information
     * @throws InvalidDataException   throws if reading cell's value process has errors
     * @throws CellConverterException throw after reading cell process, if converting process has errors
     */
    private void mapCellValue(E entity, FieldInfo<?> fieldInfo, Integer rowNum) throws InvalidDataException, CellConverterException {
        final String fieldName = fieldInfo.getFieldName();

        // find the field
        Field expectedField = this.mapOfNameAndFields.get(fieldName);
        if (expectedField == null) {
            throw new ExcelMappingException(this.clazz, fieldName);
        }

        Object fieldValue = this.convertCellsToFieldValue(fieldInfo, rowNum);
        if (fieldValue == null) {
            return;
        }

        // check if this field is PK of entity
        ReflectionUtils.invokeSetMethod(entity, expectedField.getName(), fieldValue);

    }

    // Convert one or many cells to an object of a field of mapped entity based on mapping information
    private Object convertCellsToFieldValue(FieldInfo<?> fieldInfo, Integer rowNum) throws CellConverterException, InvalidDataException {
        if (fieldInfo.getReferences().size() != fieldInfo.getReferenceTypes().size()) {
            String mes = "Can not convert to field '%s.%s': Number of references is not equal to the number of declared attributes";
            throw new InvalidDataException(String.format(mes, fieldInfo.getConverter().getConvertedType(), fieldInfo.getFieldName()));
        }

        List<Cell> cells = new ArrayList<>();
        CellDataConverter<?> converter = fieldInfo.getConverter();
        int referencesSize = fieldInfo.getReferences().size();

        try {
            for (int i = 0; i < referencesSize; i++) {
                CellReference cf = new CellReference(fieldInfo.getReferences().get(i) + (rowNum != null ? rowNum + 1 : ""));
                Row row = this.sheet.getRow(cf.getRow());
                Cell cell = row == null ? null : row.getCell(cf.getCol());

                if (referencesSize == 1) {
                    return converter.convert(cell);
                } else {
                    cells.add(cell);
                }
            }
            return converter.convert(cells.toArray(new Cell[0]));
        } catch (CellConverterException e) {
            e.setEntityClass(this.clazz);
            e.setFieldName(fieldInfo.getFieldName());
            throw e;
        }
    }
}
