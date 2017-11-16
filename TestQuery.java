/**
 * The TestQuery program implements an application that
 * calls methods in the Query, Table and Row classes checks
 * that they are functioning as intended.
 *
 * @author  Addo White
 * @version 1.0
 * @since   2017-10-27
 */

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

/**
 * Contains methods for testing methods in the Query, Table and Row classes. Also contains program entry point.
 *
 * @author Addo White
 * @version 1.0
 * @since 2017-10-27
 */
public class TestQuery {

  private boolean testFailed(String failReason) {
    // Get the stack trace from where this method was called
    StackTraceElement stackElement = new Throwable().getStackTrace()[1];

    // Pretty spacing
    System.out.println();

    System.out.println("Test failed!");

    // Print the name of the method in which this method was called i.e. The name of the test
    System.out.println("Test: " + stackElement.getMethodName());

    // Print the line number where this method was called
    System.out.println("Line: " + stackElement.getLineNumber());

    // Print the reason the test was not successfull
    System.out.println("Reason: " + failReason);

    // Pretty spacing
    System.out.println();
    return false;
  }

  //region Test Row
    private boolean testRow() {
      if (!testRowContructor())
        return false;

      if (!testRowGetColumnCount())
        return false;

      if (!testRowGet())
        return false;

      if (!testRowGetColumnsAsString())
        return false;

      if (!testRowGetAsCSV())
        return false;

      if (!testRowIterator())
        return false;

      return true;
    }

    private boolean testRowContructor() {
      // Row should start with no columns
      Row row = new Row();

      // Check that there are indeed no columns
      if (row.getColumnCount() != 0)
        return testFailed("Row initialized with more than zero columns.");

      // If this returns data something is very wrong
      if (row.get("column_which_does_not_exist") != null)
        return testFailed("Attempt to retrieve value of nonexistent column did not return null.");

      return true;
    }

    private boolean testRowGetColumnCount() {
      // Row should start with no columns
      Row row = new Row();

      // Check that there are indeed no columns
      if (row.getColumnCount() != 0)
        return testFailed("Row initialized with more than zero columns.");

      // Add a new column
      row.set("column 0", "value 0");

      // Check that there is now exactly one column
      if (row.getColumnCount() != 1)
        return testFailed("Incorrect column count after adding columns");

      // Add another column
      row.set("column 1", "value 1");

      // Check that there are now two columns
      if (row.getColumnCount() != 2)
        return testFailed("Incorrect column count after adding columns");

      // Overwrite the value in an existing column (this does not add a new column)
      row.set("column 1", "a different value");

      // Check that there are still only two columns
      if (row.getColumnCount() != 2)
        return testFailed("Incorrect column count after changing the value in a column");

      return true;
    }

    // This method also suffices to test Row.set()
    private boolean testRowGet() {
      Row row = new Row();

      // If this returns data something is very wrong
      if (row.get("column_which_does_not_exist") != null)
        return testFailed("Attempt to retrieve value of nonexistent column did not return null.");

      // Now it does exist
      row.set("column_which_does_not_exist", "some value");

      // Check that it returns the same value that we set
      if (!row.get("column_which_does_not_exist").equals("some value"))
        return testFailed("Incorrect value returned from column.");

      return true;
    }

    private boolean testRowGetColumnsAsString() {
      Row row = new Row();

      row.set("column 0", "zero");

      if (!row.getColumnsAsString(new ArrayList<>(Arrays.asList("column 0"))).equals("zero"))
        return testFailed("Incorrect string returned from method row.getColumnsAsString().");

      row.set("column 1", "one");

      if (!row.getColumnsAsString(new ArrayList<>(Arrays.asList("column 0", "column 1"))).equals("zero          one"))
        return testFailed("Incorrect string returned from method row.getColumnsAsString().");

      row.set("column 2", "two");

      if (!row.getColumnsAsString(new ArrayList<>(Arrays.asList("column 0", "column 1", "column 2"))).equals("zero          one           two"))
        return testFailed("Incorrect string returned from method row.getColumnsAsString().");

      return true;
    }

    private boolean testRowGetAsCSV() {
      Row row = new Row();

      row.set("column 0", "zero");

      if (!row.getAsCSV(new ArrayList<>(Arrays.asList("column 0"))).equals("zero"))
        return testFailed("Incorrect string returned from method row.getColumnsAsString().");

      row.set("column 1", "one");

      if (!row.getAsCSV(new ArrayList<>(Arrays.asList("column 0", "column 1"))).equals("zero,one"))
        return testFailed("Incorrect string returned from method row.getColumnsAsString().");

      row.set("column 2", "two");

      if (!row.getAsCSV(new ArrayList<>(Arrays.asList("column 0", "column 1", "column 2"))).equals("zero,one,two"))
        return testFailed("Incorrect string returned from method row.getColumnsAsString().");

      return true;
    }

    private boolean testRowIterator() {
      HashMap<String, String> testValues = new HashMap<String, String>();
      testValues.put("column 0", "0");
      testValues.put("column 1", "1");
      testValues.put("column 2", "2");
      testValues.put("column 3", "3");
      testValues.put("column 4", "4");
      testValues.put("column 5", "5");
      testValues.put("column 6", "6");
      testValues.put("column 7", "7");
      testValues.put("column 8", "8");
      testValues.put("column 9", "9");

      Row row = new Row();

      // Add all the test data to the row
      for (Map.Entry<String,String> pair : testValues.entrySet())
        row.set(pair.getKey(), pair.getValue());

      // Iterate over the row and make sure all the values are correct for the given column header
      for (Map.Entry<String,String> pair : row)
        if (!pair.getValue().equals(testValues.get(pair.getKey())))
          return testFailed("Incorrect value when iterating row.");

      return true;
    }
  //endregion

  //region Test Table
    private boolean testTable() {
      if (!testTableConstructors())
        return false;

      if (!testTableFilterColumns())
        return false;

      if (!testTableAddColumn())
        return false;

      if (!testTableAddRow())
        return false;

      if (!testTableToString())
        return false;

      if (!testTableEquals())
        return false;

      if (!testTableGetAsCSV())
        return false;

      if (!testTableGetColumnHeadersWithoutAlias())
        return false;

      if (!testTableGetColumnHeadersWithAlias())
        return false;

      if (!testTableJoin())
        return false;

      if (!testTableInsert())
        return false;

      if (!testTableSort())
        return false;

      if (!testTableIterator())
        return false;

      if (!testTableWriteToFileCSV())
        return false;

      return true;
    }

    // This method also suffices to test Table.getColumnCount() and Table.getColumnHeaders()
    private boolean testTableConstructors() {
      Table table = new Table();

      if (table.getColumnCount() != 0)
        return testFailed("Table initialized with erroneous column headers.");

      table = new Table(new String[]{ "column 0", "column 1", "column 2" });

      if (table.getColumnCount() != 3)
        return testFailed("Table initialized with incorrect number of column headers.");

      if (!table.getColumnHeaders().equals(new ArrayList<>(Arrays.asList("column 0", "column 1", "column 2"))))
        return testFailed("Table initialized with erroneous column headers.");

      return true;
    }

    private boolean testTableFilterColumns() {
      Table table = new Table(new String[]{ "column 0", "column 1", "column 2" });

      Table filteredTable = Table.filterColumns(table, new String[]{ "column 2", "column 0" });
      if (!filteredTable.getColumnHeaders().equals(new ArrayList<>(Arrays.asList("column 2", "column 0"))))
        return testFailed("Filtered table contains incorrect column headers.");

      return true;
    }

    private boolean testTableAddColumn() {
      Table table = new Table();
      table.addColumn("column 0");
      table.addColumn("column 1");
      table.addColumn("column 2");

      if (!table.getColumnHeaders().equals(new ArrayList<>(Arrays.asList("column 0", "column 1", "column 2"))))
        return testFailed("Table contains incorrect column headers.");

      table.addColumn("column 3");

      if (!table.getColumnHeaders().equals(new ArrayList<>(Arrays.asList("column 0", "column 1", "column 2", "column 3"))))
        return testFailed("Table contains incorrect column headers.");

      return true;
    }

    // This method also suffices to test Table.getRow()
    private boolean testTableAddRow() {
      Table table = new Table(new String[]{ "column 0", "column 1", "column 2" });

      Row row = new Row();
      row.set("column 0", "0");
      row.set("column 1", "1");
      row.set("column 2", "2");
      table.addRow(row);

      row = new Row();
      row.set("column 0", "3");
      row.set("column 1", "4");
      row.set("column 2", "5");
      table.addRow(row);

      row = new Row();
      row.set("column 0", "6");
      row.set("column 1", "7");
      row.set("column 2", "8");
      table.addRow(row);

      if (!table.getRow(0).getAsCSV(new ArrayList<>(Arrays.asList("column 0", "column 1", "column 2"))).equals("0,1,2"))
        return testFailed("Incorrect data in table after adding rows.");

      if (!table.getRow(1).getAsCSV(new ArrayList<>(Arrays.asList("column 0", "column 1", "column 2"))).equals("3,4,5"))
        return testFailed("Incorrect data in table after adding rows.");

      if (!table.getRow(2).getAsCSV(new ArrayList<>(Arrays.asList("column 0", "column 1", "column 2"))).equals("6,7,8"))
        return testFailed("Incorrect data in table after adding rows.");

      return true;
    }

    private boolean testTableToString() {
      Table table = new Table(new String[]{ "column 0", "column 1", "column 2" });

      Row row = new Row();
      row.set("column 0", "0");
      row.set("column 1", "1");
      row.set("column 2", "2");
      table.addRow(row);

      row = new Row();
      row.set("column 0", "3");
      row.set("column 1", "4");
      row.set("column 2", "5");
      table.addRow(row);

      row = new Row();
      row.set("column 0", "6");
      row.set("column 1", "7");
      row.set("column 2", "8");
      table.addRow(row);

      String expectedString = "[column 0]    [column 1]    [column 2]\n"
                            + "0             1             2\n"
                            + "3             4             5\n"
                            + "6             7             8\n";

      if (!table.toString().equals(expectedString))
        return testFailed("Incorrect string representation of table.");

      return true;
    }

    private boolean testTableEquals() {
      Table tableA = new Table(new String[]{ "column 0", "column 1", "column 2" });

      Row row = new Row();
      row.set("column 0", "0");
      row.set("column 1", "1");
      row.set("column 2", "2");
      tableA.addRow(row);

      row = new Row();
      row.set("column 0", "3");
      row.set("column 1", "4");
      row.set("column 2", "5");
      tableA.addRow(row);

      row = new Row();
      row.set("column 0", "6");
      row.set("column 1", "7");
      row.set("column 2", "8");
      tableA.addRow(row);

      Table tableB = new Table(new String[]{ "column 0", "column 1", "column 2" });

      row = new Row();
      row.set("column 0", "0");
      row.set("column 1", "1");
      row.set("column 2", "2");
      tableB.addRow(row);

      row = new Row();
      row.set("column 0", "3");
      row.set("column 1", "4");
      row.set("column 2", "5");
      tableB.addRow(row);

      row = new Row();
      row.set("column 0", "6");
      row.set("column 1", "7");
      row.set("column 2", "8");
      tableB.addRow(row);

      if (!tableA.equals(tableB))
        return testFailed("Identical tables did not qualify as equal.");

      if (tableA.equals(Table.filterColumns(tableB, new String[]{ "column 0", "column 2", "column 1" })))
        return testFailed("Tables qualified as equal despite differing column order.");

      if (tableA.equals(Table.filterColumns(tableB, new String[]{ "column 0", "column 1" })))
        return testFailed("Tables qualified as equal despite differing number of columns.");

      tableB.getRow(2).set("column 2", "TEST");
      if (tableA.equals(tableB))
        return testFailed("Tables qualified as equal despite differing values.");

      return true;
    }

    private boolean testTableGetAsCSV() {
      Table table = new Table(new String[]{ "column 0", "column 1", "column 2" });

      Row row = new Row();
      row.set("column 0", "0");
      row.set("column 1", "1");
      row.set("column 2", "2");
      table.addRow(row);

      row = new Row();
      row.set("column 0", "3");
      row.set("column 1", "4");
      row.set("column 2", "5");
      table.addRow(row);

      row = new Row();
      row.set("column 0", "6");
      row.set("column 1", "7");
      row.set("column 2", "8");
      table.addRow(row);

      String expectedString = "column 0,column 1,column 2\n"
                            + "0,1,2\n"
                            + "3,4,5\n"
                            + "6,7,8\n";

      if (!table.getAsCSV().equals(expectedString))
        return testFailed("Incorrect string representation of table.");

      return true;
    }

    private boolean testTableGetColumnHeadersWithoutAlias() {
      // Create a table where some columns have an alias and some don't
      Table table = new Table(new String[]{ "column_one", "alias.column_two", "column_three" });

      if (!table.getColumnHeadersWithoutAlias().equals(new ArrayList<>(Arrays.asList("column_one", "column_two", "column_three"))))
        return testFailed("Table alias not removed from column headers.");

      return true;
    }

    private boolean testTableGetColumnHeadersWithAlias() {
      // Create a table where some columns have an alias and some don't
      Table table = new Table(new String[]{ "column_one", "alias.column_two", "column_three" });

      if (!table.getColumnHeadersWithAlias("alias").equals(new ArrayList<>(Arrays.asList("alias.column_one", "alias.column_two", "alias.column_three"))))
        return testFailed("Table alias not added to column headers.");

      return true;
    }

    private boolean testTableJoin() {
      Table tableA = new Table(new String[]{ "column0", "column1", "column2" });

      Row row = new Row();
      row.set("column0", "0");
      row.set("column1", "1");
      row.set("column2", "2");
      tableA.addRow(row);

      row = new Row();
      row.set("column0", "3");
      row.set("column1", "4");
      row.set("column2", "5");
      tableA.addRow(row);

      row = new Row();
      row.set("column0", "6");
      row.set("column1", "7");
      row.set("column2", "8");
      tableA.addRow(row);

      Table tableB = new Table(new String[]{ "column0", "column1", "column2" });

      row = new Row();
      row.set("column0", "5");
      row.set("column1", "10");
      row.set("column2", "11");
      tableB.addRow(row);

      row = new Row();
      row.set("column0", "8");
      row.set("column1", "12");
      row.set("column2", "13");
      tableB.addRow(row);

      row = new Row();
      row.set("column0", "2");
      row.set("column1", "14");
      row.set("column2", "15");
      tableB.addRow(row);

      // Test joining tables with no conditions
      Table joinResult = tableA.join("a", tableB, "b", null, null);
      String expectedResults = "[a.column0]    [a.column1]    [a.column2]    [b.column0]    [b.column1]    [b.column2]\n"
                             + "0              1              2              5              10             11\n"
                             + "0              1              2              8              12             13\n"
                             + "0              1              2              2              14             15\n"
                             + "3              4              5              5              10             11\n"
                             + "3              4              5              8              12             13\n"
                             + "3              4              5              2              14             15\n"
                             + "6              7              8              5              10             11\n"
                             + "6              7              8              8              12             13\n"
                             + "6              7              8              2              14             15\n";

      if (!joinResult.toString().equals(expectedResults))
        return testFailed("Incorrect results after joining tables with no conditions.");

      // Test joining tables with a join condition
      joinResult = tableA.join("a", tableB, "b", null, "b.column0 = a.column2");
      expectedResults = "[a.column0]    [a.column1]    [a.column2]    [b.column0]    [b.column1]    [b.column2]\n"
                             + "0              1              2              2              14             15\n"
                             + "3              4              5              5              10             11\n"
                             + "6              7              8              8              12             13\n";

      if (!joinResult.toString().equals(expectedResults))
        return testFailed("Incorrect results after joining tables with a join condition.");

      // Test joining tables with a where clause
      joinResult = tableA.join("a", tableB, "b", "b.column1 = 10", null);
      expectedResults = "[a.column0]    [a.column1]    [a.column2]    [b.column0]    [b.column1]    [b.column2]\n"
                      + "0              1              2              5              10             11\n"
                      + "3              4              5              5              10             11\n"
                      + "6              7              8              5              10             11\n";

      if (!joinResult.toString().equals(expectedResults))
        return testFailed("Incorrect results after joining tables with a where clause.");

      // Test joining tables with a where clause and a join condition
      joinResult = tableA.join("a", tableB, "b", "b.column1 = 10", "b.column0 = a.column2");
      expectedResults = "[a.column0]    [a.column1]    [a.column2]    [b.column0]    [b.column1]    [b.column2]\n"
                      + "3              4              5              5              10             11\n";

      if (!joinResult.toString().equals(expectedResults))
        return testFailed("Incorrect results after joining tables with a where clause and a join condition.");

      return true;
    }

    private boolean testTableInsert() {
      Table tableA = new Table(new String[]{ "column0", "column1", "column2" });

      Row row = new Row();
      row.set("column0", "0");
      row.set("column1", "0");
      row.set("column2", "0");
      tableA.addRow(row);

      row = new Row();
      row.set("column0", "0");
      row.set("column1", "0");
      row.set("column2", "0");
      tableA.addRow(row);

      Table tableB = new Table(new String[]{ "column0", "column1", "column2" });

      row = new Row();
      row.set("column0", "1");
      row.set("column1", "1");
      row.set("column2", "1");
      tableB.addRow(row);

      row = new Row();
      row.set("column0", "1");
      row.set("column1", "1");
      row.set("column2", "2");
      tableB.addRow(row);

      row = new Row();
      row.set("column0", "1");
      row.set("column1", "1");
      row.set("column2", "1");
      tableB.addRow(row);

      // Test insert with no condition
      tableA.insert(tableB, "b", null);
      String expectedResults = "[column0]    [column1]    [column2]    [b.column0]    [b.column1]    [b.column2]\n"
                             + "0            0            0            null           null           null\n"
                             + "0            0            0            null           null           null\n"
                             + "null         null         null         1              1              1\n"
                             + "null         null         null         1              1              2\n"
                             + "null         null         null         1              1              1\n";

      if (!tableA.toString().equals(expectedResults))
        return testFailed("Incorrect results after inserting table with no condition.");

      // Reset the tables
      tableA = new Table(new String[]{ "column0", "column1", "column2" });

      row = new Row();
      row.set("column0", "0");
      row.set("column1", "0");
      row.set("column2", "0");
      tableA.addRow(row);

      row = new Row();
      row.set("column0", "0");
      row.set("column1", "0");
      row.set("column2", "0");
      tableA.addRow(row);

      tableB = new Table(new String[]{ "column0", "column1", "column2" });

      row = new Row();
      row.set("column0", "1");
      row.set("column1", "1");
      row.set("column2", "1");
      tableB.addRow(row);

      row = new Row();
      row.set("column0", "1");
      row.set("column1", "1");
      row.set("column2", "2");
      tableB.addRow(row);

      row = new Row();
      row.set("column0", "1");
      row.set("column1", "1");
      row.set("column2", "1");
      tableB.addRow(row);

      // Test insert with a where clause
      tableA.insert(tableB, "b", "b.column2 = 2");
      expectedResults = "[column0]    [column1]    [column2]    [b.column0]    [b.column1]    [b.column2]\n"
                              + "0            0            0            null           null           null\n"
                              + "0            0            0            null           null           null\n"
                              + "null         null         null         1              1              2\n";

      if (!tableA.toString().equals(expectedResults))
        return testFailed("Incorrect results after inserting table with where clause.");

      return true;
    }

    private boolean testTableSort() {
      Table table = new Table(new String[]{ "column0", "column1" });

      Row row = new Row();
      row.set("column0", "0");
      row.set("column1", "a");
      table.addRow(row);

      row = new Row();
      row.set("column0", "1");
      row.set("column1", "b");
      table.addRow(row);

      row = new Row();
      row.set("column0", "2");
      row.set("column1", "c");
      table.addRow(row);

      row = new Row();
      row.set("column0", "3");
      row.set("column1", "d");
      table.addRow(row);

      // Sort by a column containing numbers in descending order
      table.sort("column0", "DeSc"); // Test upper and lower case characters in sort order (should be case-insensitive)

      String sortedTable = "[column0]    [column1]\n"
                         + "3            d\n"
                         + "2            c\n"
                         + "1            b\n"
                         + "0            a\n";

      if (!table.toString().equals(sortedTable))
        return testFailed("Table row order incorrect after sorting.");

      // Sort by a column containing letters in ascending order
      table.sort("column1", "Asc");

      sortedTable = "[column0]    [column1]\n"
                  + "0            a\n"
                  + "1            b\n"
                  + "2            c\n"
                  + "3            d\n";

      if (!table.toString().equals(sortedTable))
        return testFailed("Table row order incorrect after sorting.");

      return true;
    }

    private boolean testTableIterator() {
      Table table = new Table();

      {
        Row row = new Row();
        row.set("column0", "0");
        row.set("column1", "0");
        row.set("column2", "0");
        table.addRow(row);

        row = new Row();
        row.set("column0", "1");
        row.set("column1", "1");
        row.set("column2", "1");
        table.addRow(row);

        row = new Row();
        row.set("column0", "2");
        row.set("column1", "2");
        row.set("column2", "2");
        table.addRow(row);
      }

      ArrayList<String> columnHeaders = new ArrayList<>(Arrays.asList("column0", "column1", "column2"));
      int i = 0;
      for (Row row : table)
        if (!row.getAsCSV(columnHeaders).equals(table.getRow(i++).getAsCSV(columnHeaders)))
          return testFailed("Incorrect data when iterating rows in table.");

      return true;
    }

    // Also tests Table.readFromFileCSV()
    private boolean testTableWriteToFileCSV() {
      Table tableA = new Table(new String[]{ "column0", "column1", "column2" });

      {
        Row row = new Row();
        row.set("column0", "0");
        row.set("column1", "0");
        row.set("column2", "0");
        tableA.addRow(row);

        row = new Row();
        row.set("column0", "1");
        row.set("column1", "1");
        row.set("column2", "1");
        tableA.addRow(row);

        row = new Row();
        row.set("column0", "2");
        row.set("column1", "2");
        row.set("column2", "2");
        tableA.addRow(row);
      }

      tableA.writeToFileCSV("testTableWriteToFileCSV.csv");

      Table tableB = new Table();
      tableB.readFromFileCSV("testTableWriteToFileCSV.csv");

      if (!tableA.equals(tableB))
        return testFailed("Table read from file does not equal table written to file.");

      return true;
    }
  //endregion

  //region Test Query
    private boolean testQuery() {
      if (!testQueryFluentInterface())
        return false;

      if (!testQueryResolveCondition())
        return false;

      if (!testQueryIsNumber())
        return false;

      if (!testQueryIsString())
        return false;

      if (!testQueryExecute())
        return false;

      return true;
    }

    // Also tests Query.toString()
    private boolean testQueryFluentInterface() {
      Query query = new Query();
      query.insertInto(new Table(), new String[]{ "column0", "column1", "column2" });
      query.values(new String[]{ "0", "1", "2", "3", "4", "5", "6", "7", "8" });

      String expectedString = "INSERT INTO [anonymous table] (column0, column1, column2)\n"
                            + "VALUES (\n"
                            + "    0, 1, 2,\n"
                            + "    3, 4, 5,\n"
                            + "    6, 7, 8\n"
                            + ")\n";

      if (!query.toString().equals(expectedString))
        return testFailed("Query string representation incorrect.");

      query = new Query();
      query.select(new String[]{ "t.column0", "t.column1", "t.column2" });
      query.from(new Table(), "t");
      query.join(new Table(), "a").on("a.column0 = t.column0");
      query.join(new Table(), "b").on("b.column0 = a.column0");
      query.where("t.column0 = 0");
      query.orderby("t.column0 DESC");

      expectedString = "SELECT t.column0, t.column1, t.column2\n"
                     + "FROM [anonymous table] AS \"t\"\n"
                     + "JOIN [anonymous table] AS \"a\"\n"
                     + "    ON a.column0 = t.column0\n"
                     + "JOIN [anonymous table] AS \"b\"\n"
                     + "    ON b.column0 = a.column0\n"
                     + "WHERE t.column0 = 0\n"
                     + "ORDER BY t.column0 DESC\n";

      if (!query.toString().equals(expectedString))
        return testFailed("Query string representation incorrect.");

      return true;
    }

    private boolean testQueryResolveCondition() {
      // Test conditions without table alias
      Row row = new Row();
      row.set("column0", "0");
      row.set("column1", "1");
      row.set("column2", "2");

      if (!Query.resolveCondition(row, "column0 = 0"))
        return testFailed("Condition did not evaluate to true.");

      if (!Query.resolveCondition(row, "column1 = 1"))
        return testFailed("Condition did not evaluate to true.");

      if (!Query.resolveCondition(row, "column2 = 2"))
        return testFailed("Condition did not evaluate to true.");

      // Test conditions with table alias
      row = new Row();
      row.set("a.column", "0");
      row.set("b.column", "1");
      row.set("c.column", "2");

      // Test 'equal' comparator
      if (!Query.resolveCondition(row, "a.column = 0"))
        return testFailed("Condition did not evaluate to true.");

      // Test 'less than' comparator
      if (!Query.resolveCondition(row, "b.column < 2"))
        return testFailed("Condition did not evaluate to true.");

      // Test 'greater than' comparator
      if (!Query.resolveCondition(row, "c.column > 1"))
        return testFailed("Condition did not evaluate to true.");

      // Test multiple 'AND' operations
      if (!Query.resolveCondition(row, "a.column = 0 AND b.column = 1 AND c.column = 2"))
        return testFailed("Condition did not evaluate to true.");

      // Test multiple 'OR' operations
      if (!Query.resolveCondition(row, "a.column = 0 OR b.column = 0 OR c.column = 0"))
        return testFailed("Condition did not evaluate to true.");

      // Test that 'AND' operations have precendence over 'OR'
      if (!Query.resolveCondition(row, "1 OR a.column = 999 AND b.column = 999 AND c.column = 2"))
        return testFailed("Condition did not evaluate to true.");

      // Test brackets have precedence over everything else
      if (Query.resolveCondition(row, "a.column = 999 AND b.column = 999 AND (c.column = 2 OR 1)"))
        return testFailed("Condition did not evaluate to false.");

      // Test nested brackets
      if (!Query.resolveCondition(row, "((a.column = 999 AND b.column = 999 AND c.column = 2) OR 1) AND ((1 AND 1) OR (0 AND 1)) AND ((1 AND 1) AND (0 OR 1))"))
        return testFailed("Condition did not evaluate to false.");

      // Test conditions with string values
      row = new Row();
      row.set("a.column", "Bob");
      row.set("b.column", "Garry");
      row.set("c.column", "Dave");

      // Test case sensitivity
      if (Query.resolveCondition(row, "a.column = \"BOB\""))
        return testFailed("Condition did not evaluate to false.");

      // Test case sensitivity
      if (Query.resolveCondition(row, "b.column = \"garry\""))
        return testFailed("Condition did not evaluate to false.");

      // Test case sensitivity
      if (!Query.resolveCondition(row, "c.column = \"Dave\""))
        return testFailed("Condition did not evaluate to true.");

      return true;
    }

    private boolean testQueryIsNumber() {
      ArrayList<String> exampleNumbers = new ArrayList<String>();
      exampleNumbers.add("3");
      exampleNumbers.add("123456789");
      exampleNumbers.add("3.1");
      exampleNumbers.add("3.123456789");
      exampleNumbers.add("-3");
      exampleNumbers.add("-123456789");
      exampleNumbers.add("-3.1");
      exampleNumbers.add("-3.123456789");

      for (String numString : exampleNumbers)
        if (!Query.isNumber(numString))
          return testFailed("\"" + numString + "\" did not qualify as a number.");

      ArrayList<String> exampleNotNumbers = new ArrayList<String>();
      exampleNumbers.add("string");
      exampleNumbers.add("3F");
      exampleNumbers.add("3.1.1");
      exampleNumbers.add("&22");

      for (String numString : exampleNotNumbers)
        if (Query.isNumber(numString))
          return testFailed("\"" + numString + "\" should not qualify as a number.");

      return true;
    }

    private boolean testQueryIsString() {
      ArrayList<String> exampleStrings = new ArrayList<String>();
      exampleStrings.add("\"a string\"");
      exampleStrings.add("\'a string\'");

      for (String str : exampleStrings)
        if (!Query.isString(str))
          return testFailed("\"" + str + "\" did not qualify as a string.");

      ArrayList<String> exampleNotStrings = new ArrayList<String>();
      exampleNotStrings.add("columnName");
      exampleNotStrings.add("a.column");
      exampleNotStrings.add("no quotes here...");
      exampleNotStrings.add("345");
      exampleNotStrings.add("3F");
      exampleNotStrings.add("3.2");
      exampleNotStrings.add("3.1.2");

      for (String str : exampleNotStrings)
        if (Query.isString(str))
          return testFailed("\"" + str + "\" should not qualify as a string.");

      return true;
    }

    private boolean testQueryExecute() {
      // Load some tables from CSV files
      Table student = new Table();
      student.readFromFileCSV("test_student_data.csv");

      Table classDefinition = new Table();
      classDefinition.readFromFileCSV("test_class_data.csv");

      Table classMembership = new Table();
      classMembership.readFromFileCSV("test_class_membership_data.csv");

      Table results = new Query()
                      .select(new String[]{
                        "s.pk",
                        "s.first_name",
                        "s.last_name",
                        "s.age",
                        "cd.name"
                      })
                      .from(student,         "s")
                      .join(classMembership, "cm")
                        .on("cm.student_fk = s.pk")
                      .join(classDefinition, "cd")
                        .on("cd.pk = cm.definition_class_fk")
                      .where("s.age < 13")
                      .orderby("cd.name ASC")
                      .execute();

      String expectedResults = "[s.pk]    [s.first_name]    [s.last_name]    [s.age]    [cd.name]\n"
                             + "66        Tremain           Menzies          11         Biology\n"
                             + "167       Stevy             Hamlett          12         Biology\n"
                             + "264       Marlena           Rivalland        11         Biology\n"
                             + "102       Sarah             Swanne           11         Chemistry\n"
                             + "130       Margaux           Wilford          12         Chemistry\n"
                             + "154       Rubina            Torn             12         Chemistry\n"
                             + "173       Tally             Vanshin          11         Chemistry\n"
                             + "201       Vi                Kenward          11         Chemistry\n"
                             + "212       Frederico         Yon              11         Chemistry\n"
                             + "27        Henrik            Harkins          11         English\n"
                             + "35        Freddi            Bickerdike       11         English\n"
                             + "65        Eliot             Gaye             12         English\n"
                             + "125       Benjamin          Cicerone         12         English\n"
                             + "266       Magnum            Barthelemy       11         English\n"
                             + "13        Davey             Matteini         11         Maths\n"
                             + "89        Berti             Cotterell        11         Maths\n"
                             + "100       Ruttger           Fearey           12         Maths\n"
                             + "286       Georas            Grimwad          12         Maths\n"
                             + "287       Valery            Becken           11         Maths\n"
                             + "72        Denver            Dugdale          12         Music\n"
                             + "91        Emma              Summerell        11         Music\n"
                             + "116       Kristien          McPhelimey       11         Music\n"
                             + "35        Freddi            Bickerdike       11         PE\n"
                             + "48        Guglielmo         Sleep            12         PE\n"
                             + "100       Ruttger           Fearey           12         PE\n"
                             + "102       Sarah             Swanne           11         PE\n"
                             + "116       Kristien          McPhelimey       11         PE\n"
                             + "148       Teodoor           Hodcroft         12         PE\n"
                             + "156       Roi               McElroy          12         PE\n"
                             + "223       Yoshi             Causnett         12         PE\n"
                             + "17        Jolee             Swanson          11         Physics\n"
                             + "35        Freddi            Bickerdike       11         Physics\n"
                             + "65        Eliot             Gaye             12         Physics\n"
                             + "89        Berti             Cotterell        11         Physics\n"
                             + "137       Roobbie           Beswick          11         Physics\n"
                             + "149       Rube              Von Welden       12         Physics\n"
                             + "205       Katalin           Iddon            11         Physics\n"
                             + "229       Kessia            Gorusso          11         Physics\n"
                             + "280       Haywood           Winscom          11         Physics\n";

      if (!results.toString().equals(expectedResults))
        return testFailed("Result of executing query not as expected.");

      return true;
    }
  //endregion

  private boolean testAll() {
    if (!testRow())
      return false;

    if (!testTable())
      return false;

    if (!testQuery())
      return false;

    System.out.println("All tests completed successfully!");
    return true;
  }

  /**
   * TestQuery program entry point.
   * @param args Commandline arguments passed to the program. Unused.
   */
  public static void main(String[] args) {
    new TestQuery().testAll();
  }

}
