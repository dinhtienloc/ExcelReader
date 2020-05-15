package vn.locdt.excel.reader.converter;

import org.apache.poi.ss.usermodel.Cell;
import vn.locdt.excel.reader.ReferenceInfo;
import vn.locdt.excel.reader.exception.CellConverterException;
import vn.locdt.excel.reader.exception.ExcelMappingException;
import vn.locdt.excel.utils.ExcelUtils;
import vn.locdt.excel.utils.ReflectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Default converter for EntityReader. It will convert cell's values to
 * another type based on the list of SingularAttribute.
 * This converter can be used for almost normal cases: both single PK and
 * composite PK.
 *
 * @param <E> result data type
 */
public class DefaultCellDataConverter<E> implements CellDataConverter<E> {
    private List<ReferenceInfo> nestedAttributes;
    private Class<E> clazz;

    public DefaultCellDataConverter(Class<E> clazz, List<ReferenceInfo> nestedAttributes) {
        this.clazz = clazz;
        this.nestedAttributes = nestedAttributes;
    }

    public DefaultCellDataConverter(Class<E> clazz) {
        this.clazz = clazz;
    }

    @Override
    public Class<E> getConvertedType() {
        return this.clazz;
    }

    /**
     * Converting method. Map cell's values to entity's field
     *
     * @param cells list of cell
     * @return converted entity
     * @throws ExcelMappingException  throws if has any mismatch between mapping fields and cells
     * @throws CellConverterException throws if the mapping is correct, system can extract cell's value
     *                                but can not convert to the expected type
     */
    @Override
    public E convert(Cell... cells) throws CellConverterException {
        // Java default type: String, Number, Date,... will not have custom attribute
        if (String.class.isAssignableFrom(this.clazz) || Number.class.isAssignableFrom(this.clazz)
                || Date.class.isAssignableFrom(this.clazz)) {
            return this.convertSingleField(cells[0]);
        } else {
            return this.convertCustomField(cells);
        }
    }

    protected E convertSingleField(Cell cell) throws CellConverterException {
        try {
            return ExcelUtils.getCellValue(cell, this.clazz);
        } catch (Exception e) {
            Object rawValue = ExcelUtils.getCellValue(cell);
            throw new CellConverterException(Collections.singletonList(rawValue), Collections.singletonList(cell), this.clazz, e);
        }
    }

    protected E convertCustomField(Cell... cells) throws CellConverterException {
        E obj = ReflectionUtils.newInstanceFromClass(this.clazz);
        List<Cell> invalidCells = new ArrayList<>();
        List<Object> invalidRawValues = new ArrayList<>();

        for (int i = 0; i < cells.length; i++) {
            Class<?> expectedType = this.nestedAttributes.get(i).getType();
            try {
                Object value = ExcelUtils.getCellValue(cells[i], expectedType);
                ReflectionUtils.invokeSetMethod(obj, this.nestedAttributes.get(i).getName(), value);
            } catch (Exception e) {
                invalidCells.add(cells[i]);
                invalidRawValues.add(ExcelUtils.getCellValue(cells[i]));
            }
        }

        if (!invalidCells.isEmpty()) {
            throw new CellConverterException(invalidRawValues, invalidCells, this.clazz);
        }

        return obj;
    }
}
