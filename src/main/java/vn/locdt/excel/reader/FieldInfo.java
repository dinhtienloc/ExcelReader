package vn.locdt.excel.reader;

import vn.locdt.excel.reader.converter.CellDataConverter;

import java.util.Collections;
import java.util.List;

/**
 * This class holds the mapping information about the fields of target object
 *
 * @param <T> field data type
 */
class FieldInfo<T> {
    private List<String> references;
    private List<Class<?>> referenceTypes;
    private String fieldName;
    private CellDataConverter<T> converter;

    FieldInfo(List<String> references, List<Class<?>> referenceTypes, String fieldName, CellDataConverter<T> converter) {
        this.references = references;
        this.referenceTypes = referenceTypes;
        this.fieldName = fieldName;
        this.converter = converter;
    }

    FieldInfo(String reference, Class<T> referenceType, String fieldName, CellDataConverter<T> converter) {
        this.references = Collections.singletonList(reference);
        this.referenceTypes = Collections.singletonList(referenceType);
        this.fieldName = fieldName;
        this.converter = converter;
    }

    public List<String> getReferences() {
        return this.references;
    }

    List<Class<?>> getReferenceTypes() {
        return this.referenceTypes;
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public CellDataConverter<T> getConverter() {
        return this.converter;
    }
}
