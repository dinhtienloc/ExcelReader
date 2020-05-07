package vn.locdt.excel.reader.converter;

import org.apache.poi.ss.usermodel.Cell;
import vn.locdt.excel.reader.ReferenceInfo;
import vn.locdt.excel.reader.exception.CellConverterException;
import vn.locdt.excel.reader.exception.InvalidDataException;
import vn.locdt.excel.utils.ExcelUtils;
import vn.locdt.excel.utils.ReflectionUtils;

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
     * Converting method. Use the cell's values and entity's attributes to create query for searching
     *
     * @param cells list of cell
     * @return converted entity
     * @throws InvalidDataException   throws if result class is not a type of SynEntity
     *                                or has mismatch mapping between cell's value and entity's attribute
     * @throws CellConverterException throws if system can extract cell's value but can not convert to
     *                                the expected type
     */
    @Override
    public E convert(Cell... cells) {
        // Java default type: String, Number, Date,... will not have custom attribute
        if (String.class.isAssignableFrom(this.clazz) || Number.class.isAssignableFrom(this.clazz)
                || Date.class.isAssignableFrom(this.clazz)) {
            return this.convertSingleField(cells[0]);
        } else {
            return this.convertCustomField(cells);
        }
    }

    protected E convertSingleField(Cell cell) {
        try {
            return ExcelUtils.getCellValue(cell, this.clazz);
        } catch (Exception e) {
            Object rawValue = ExcelUtils.getCellValue(cell);
            throw new CellConverterException(Collections.singletonList(rawValue), Collections.singletonList(cell), this.clazz, e);
        }
    }

    protected E convertCustomField(Cell... cells) {
        E obj = ReflectionUtils.newInstanceFromClass(this.clazz);

        for (int i = 0; i < cells.length; i++) {
            Class<?> expectedType = this.nestedAttributes.get(i).getType();
            Object value = ExcelUtils.getCellValue(cells[i], expectedType);
            ReflectionUtils.invokeSetMethod(obj, this.nestedAttributes.get(i).getName(), value);
        }

        return obj;
    }
}
