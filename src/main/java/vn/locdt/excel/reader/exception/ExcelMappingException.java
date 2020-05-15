package vn.locdt.excel.reader.exception;

public class ExcelMappingException extends RuntimeException {
    public ExcelMappingException(Class<?> entityClass, String field) {
        super(String.format("Field not found: %s.%s", entityClass.getSimpleName(), field));
    }

    public ExcelMappingException(String message) {
        super(message);
    }
}
