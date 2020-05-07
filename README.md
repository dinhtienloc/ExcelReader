# ExcelReader
This small library allow you to map your excel data into java object. This library doesn't require any addtional annotations inside your mapped class. Therefore, you are able to map the data to both your new class and compiled class.

# Usage
The main class taking the responsibility to read excel file is `ExcelReader`, which can be built by using `ExcelReaderBuilder.build()`. 
There are several important points about the usage of builder: 

- map row
- map individual cell
- map multiple cells
- converter

#### 1. Common Builder's Method:
First of all, there are a few simple things that you have to define while creating the builder.

- `mapto(Class clazz)`: define class used to map the data
- `wb(Workbook wb)`: define Workbook instance
- `sheet(Sheet sheet)`: define Sheet instance
- `sheet(int sheetNum)` or `sheet(Sheet sheet)`: define Sheet instance

#### 2. Map rows to object
You can use column index in excel file to help the reader stores column position it should use.
And after that, you will use `readRow` method to tell reader which rows you want to map. (both column and row index are 0-based)

Example: 

    ExcelReader<Person> reader = ExcelReaderBuilder.mapTo(Person.class)...
        .mapReference(0, "seqNo", Integer.class)
        .mapReference(1, "name", String.class)
        .mapReference(2, "weight", Double.class)
        .build();
    
    // Read a range of rows
    List<Person> persons = reader.readRow(startRowIdx, endRowIdx);
    
    // Read single row
    int rowIdx = 0;
    Person p0 = reader.readRow(rowIdx);
    
    // Read single row to existed object
    Person p = createNewPerson();
    reader.readRow(p, rowIdx);

#### 3. Map individual cells to object
`ExcelReader` provides another way to map the object in case the data in excel file is in mutiple, non-related cells.

    ExcelReader<Person> reader = ExcelReaderBuilder.mapTo(Person.class)...
        .mapReference("A1", "seqNo", Integer.class)
        .mapReference("B2", "name", String.class)
        .mapReference("C3", "weight", Double.class)
        .build();

By using this way, you just need to call `readRow()` to get your object.

    Person p = reader.readRow();
    
    // or read to existed object
    Person p = createNewPerson();
    reader.readRow(p);
    
#### 4. Map custom type
This library also provides methods to map multiple cells into one data type. For example, `Person` class is defined like this:

    class Person {
        Integer seqNo;
        PersonInformation info;
    }
    
    class PersonInformation {
        String name;
        Double weight;
    }

Then you can define the builder:

    ExcelReader<Person> reader = ExcelReaderBuilder.mapTo(Person.class)...
        .mapReference("A1", "seqNo", String.class)
        .mapReference(List.of(
               ReferenceInfo.of("A2", "name", String.class),
               ReferenceInfo.of("A3", "weight", Double.class)
            ), "info", PersonInformation.class)
        .build();
        
    Person p = reader.readRow();

#### 5. Converter
By default, all `mapReference` methods are using `DefaultCellDataConverter`. If the default converter is not your expectation, 
you can create a custom converter which implements `CellDataConverter` interface. 
You can use it as the final parameter in your `mapReference` method. 