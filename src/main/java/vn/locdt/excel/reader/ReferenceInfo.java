package vn.locdt.excel.reader;

public class ReferenceInfo {
    private String ref;
    private String name;
    private Class<?> type;

    private ReferenceInfo(String ref, String name, Class<?> type) {
        this.ref = ref;
        this.name = name;
        this.type = type;
    }

    public static ReferenceInfo of(String ref, String name, Class<?> type) {
        return new ReferenceInfo(ref, name, type);
    }

    public String getRef() {
        return this.ref;
    }

    public String getName() {
        return this.name;
    }

    public Class<?> getType() {
        return this.type;
    }
}
