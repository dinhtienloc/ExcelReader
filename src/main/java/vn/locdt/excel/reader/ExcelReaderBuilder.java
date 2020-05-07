package vn.locdt.excel.reader;

import org.apache.commons.lang3.Validate;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellReference;
import vn.locdt.excel.reader.converter.CellDataConverter;
import vn.locdt.excel.reader.converter.DefaultCellDataConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Builder class for EntityReader
 *
 * @param <E>
 */
public class ExcelReaderBuilder<E> {
    private final Class<E> clazz;
    private Workbook wb;
    private Sheet sheet;
    private List<FieldInfo<?>> fieldInfos;

    // Constructor of the builder with target entity class
    private ExcelReaderBuilder(Class<E> clazz) {
        this.clazz = clazz;
        this.fieldInfos = new ArrayList<>();
    }

    /**
     * Construct the Builder by setting the target entity class
     *
     * @param clazz the entity class
     * @param <E>   type of entity
     * @return this builder, to allow method chaining
     */
    public static <E> ExcelReaderBuilder<E> mapTo(Class<E> clazz) {
        return new ExcelReaderBuilder<>(clazz);
    }

    /**
     * Set the mapped excel workbook to the Builder
     *
     * @param wb the excel workbook
     * @return this builder, to allow method chaining
     */
    public ExcelReaderBuilder<E> wb(Workbook wb) {
        this.wb = wb;
        return this;
    }

    /**
     * Set the mapped excel sheet to the Builder.
     *
     * @param sheetNum the sheet's index in workbook
     * @return this builder, to allow method chaining
     */
    public ExcelReaderBuilder<E> sheet(int sheetNum) {
        Validate.notNull(this.wb, "Workbook must be not null");
        this.sheet = this.wb.getSheetAt(sheetNum);
        return this;
    }

    /**
     * Set the mapped excel sheet to the Builder.
     *
     * @param sheet the sheet instance
     * @return this builder, to allow method chaining
     */
    public ExcelReaderBuilder<E> sheet(Sheet sheet) {
        Validate.notNull(sheet, "Sheet must be not null");
        this.wb = sheet.getWorkbook();
        this.sheet = sheet;
        return this;
    }

    /**
     * Create a mapping info from column number, name of the field and its data type.
     * This method will use {@link DefaultCellDataConverter} as default converter.
     *
     * @param col       column's index
     * @param fieldName the name of the field need to be mapped
     * @param dataType  expected data type of the mapped field
     * @param <T>       expected data type of the mapped field
     * @return this builder, to allow method chaining
     */
    public <T> ExcelReaderBuilder<E> mapReference(Integer col, String fieldName, Class<T> dataType) {
        if (col == null) {
            return this;
        }
        return this.mapReference(new CellReference(0, col).getCellRefParts()[2], fieldName, dataType, new DefaultCellDataConverter<>(dataType));
    }

    /**
     * Create a mapping info from reference position, name of the field and its data type.
     * This method will use {@link DefaultCellDataConverter} as default converter.
     *
     * @param reference the excel reference
     * @param fieldName the name of the field need to be mapped
     * @param dataType  expected data type of the mapped field
     * @param <T>       expected data type of the mapped field
     * @return this builder, to allow method chaining
     */
    public <T> ExcelReaderBuilder<E> mapReference(String reference, String fieldName, Class<T> dataType) {
        return this.mapReference(reference, fieldName, dataType, new DefaultCellDataConverter<>(dataType));
    }

    /**
     * Create a mapping between a list of references to an JPA Metamodel.
     * This method will use {@link DefaultCellDataConverter} as default converter.
     *
     * @param customTypeInfo if the mapped field has custom type,
     *                       this param will describes the info of the custom type with reference, field name and its type.
     * @param fieldName      name of the mapped field
     * @param fieldDataType  expected data type of the mapped field
     * @param <T>            expected data type of the mapped field
     * @return this builder, to allow method chaining
     */
    public <T> ExcelReaderBuilder<E> mapReference(List<ReferenceInfo> customTypeInfo, String fieldName, Class<T> fieldDataType) {
        List<String> references = new ArrayList<>();
        List<Class<?>> classes = new ArrayList<>();
        customTypeInfo.forEach(i -> {
            references.add(i.getRef());
            classes.add(i.getType());
        });
        return this.mapReference(references, classes, fieldName, new DefaultCellDataConverter<>(fieldDataType, customTypeInfo));
    }

    // Create FieldInfo to store the mapping information simple type
    private <T> ExcelReaderBuilder<E> mapReference(String reference, String fieldName, Class<T> dataType, CellDataConverter<T> converter) {
        this.fieldInfos.add(new FieldInfo<>(reference, dataType, fieldName, converter));
        return this;
    }

    // Create FieldInfo to store the mapping information
    private <T> ExcelReaderBuilder<E> mapReference(List<String> references, List<Class<?>> referenceTypes, String fieldName, CellDataConverter<T> converter) {
        this.fieldInfos.add(new FieldInfo<>(references, referenceTypes, fieldName, converter));
        return this;
    }

    /**
     * Validate and build EntityReader from the Builder.
     * In case caller doesn't specify the mapped sheet, builder will use the first non-hidden sheet in workbook as default.
     *
     * @return EntityReader
     */
    public ExcelReader<E> build() {
        Validate.notNull(this.clazz, "Entity class must be not null");
        Validate.notNull(this.wb, "Workbook must be not null");
        Validate.notEmpty(this.fieldInfos, "Mapped fields must be not empty");

        if (this.sheet == null) {
            int sheetIdx = 0;
            while (this.wb.isSheetHidden(sheetIdx)) {
                sheetIdx++;
            }

            this.sheet = this.wb.getSheetAt(sheetIdx);
        }

        Validate.notNull(this.sheet, "Sheet must be not null");
        return new ExcelReader<>(this);
    }

    /* getters & setters */
    Class<E> getClazz() {
        return this.clazz;
    }

    Sheet getSheet() {
        return this.sheet;
    }

    List<FieldInfo<?>> getFieldInfos() {
        return this.fieldInfos;
    }
}
