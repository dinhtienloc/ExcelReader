package vn.locdt.test;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import vn.locdt.excel.reader.ExcelReader;
import vn.locdt.excel.reader.ExcelReaderBuilder;

import java.io.File;
import java.io.IOException;

public class ExcelMapperTest {
    private static Workbook wb;

    @Before
    public void setup() throws IOException {
       wb = WorkbookFactory.create(new File("src/test/java/SampleExcel.xlsx"));
    }

    @Test
    public void testReadExcelFileUsingColumnNoMapping() {
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
    public void testReadExcelFileUsingReferenceMapping() {
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
}
