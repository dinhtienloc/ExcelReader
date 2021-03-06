package vn.locdt.test;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import vn.locdt.excel.reader.ExcelReader;
import vn.locdt.excel.reader.ExcelReaderBuilder;
import vn.locdt.excel.reader.ReferenceInfo;
import vn.locdt.excel.reader.exception.CellConverterException;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class ExcelMapperTest {
    private static Workbook wb;

    @Before
    public void setup() throws IOException {
        wb = WorkbookFactory.create(new File("src/test/java/SampleExcel.xlsx"));
    }

    @Test
    public void testReadExcelFileUsingColumnNoMapping() throws CellConverterException {
        ExcelReader<Data> reader = ExcelReaderBuilder.mapTo(Data.class)
                .wb(wb)
                .mapReference(1, "seqNo", Integer.class)
                .mapReference(2, "qty", Double.class)
                .mapReference(3, "name", String.class)
                .build();

        Data data = reader.readRow(2);
        Assert.assertEquals(data.getSeqNo(), new Integer(1));
        Assert.assertEquals(data.getQty(), new Double(1.3));
        Assert.assertEquals(data.getName(), "Heisenberg");

        data = reader.readRow(3);
        Assert.assertEquals(data.getSeqNo(), new Integer(2));
        Assert.assertEquals(data.getQty(), new Double(10.1));
        Assert.assertEquals(data.getName(), "Pinkman");
    }

    @Test
    public void testReadExcelFileUsingReferenceMapping() throws CellConverterException {
        ExcelReader<Data> reader = ExcelReaderBuilder.mapTo(Data.class)
                .wb(wb)
                .mapReference("B7", "seqNo", Integer.class)
                .mapReference("C8", "qty", Double.class)
                .mapReference("D9", "name", String.class)
                .build();

        Data data = reader.read();
        Assert.assertEquals(data.getSeqNo(), new Integer(1));
        Assert.assertEquals(data.getQty(), new Double(1.3));
        Assert.assertEquals(data.getName(), "Heisenberg");
    }

    @Test
    public void testReadExcelFileUsingReferenceMappingWithCompositeClass() {
        ExcelReader<CompositeData> reader = ExcelReaderBuilder.mapTo(CompositeData.class)
                .wb(wb)
                .mapReference("B7", "seqNo", Integer.class)
                .mapReference(Arrays.asList(
                        ReferenceInfo.of("C8", "qty", Double.class),
                        ReferenceInfo.of("D9", "name", String.class)
                ), "data", CompositeData.Data.class)
                .build();

        CompositeData compositeData = null;
        try {
            compositeData = reader.read();
        } catch (CellConverterException e) {
            e.printStackTrace();
        }
        Assert.assertEquals(compositeData.getSeqNo(), new Integer(1));
        Assert.assertEquals(compositeData.getData().getQty(), new Double(1.3));
        Assert.assertEquals(compositeData.getData().getName(), "Heisenberg");
    }
}
