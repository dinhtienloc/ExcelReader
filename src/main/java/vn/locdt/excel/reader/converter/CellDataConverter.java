package vn.locdt.excel.reader.converter;

import org.apache.poi.ss.usermodel.Cell;
import vn.locdt.excel.reader.exception.CellConverterException;

/**
 * A converter interface that converts cell's values to another data type
 *
 * @param <E> result data type
 */
public interface CellDataConverter<E> {
    E convert(Cell... cells) throws CellConverterException;

    Class<E> getConvertedType();
}
