import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The Table class represents an ordered collection of rows of data.
 * Implements <code>Iterable&lt;Row&gt;</code> so that an instance of <code>Table</code> can be used in a foreach loop to iterate over the rows in the table.
 * 
 * @author Addo White
 * @version 1.0
 * @since 2017-10-27
 */
public class Table implements Iterable<Row> {

  /** An <code>ArrayList</code> containing the names of all the columns in the table. */
  private ArrayList<String> columnHeaders;

  /** An <code>ArrayList</code> containing all the rows of data in the table. */
  private ArrayList<Row> data;

  /**
   * Class constructor.
   * Initializes class members.
   */
  public Table() {
    columnHeaders = new ArrayList<String>();
    data = new ArrayList<Row>();
  }
  
  /**
   * Class constructor.
   * Initializes class members.
   * @param columnHeaders The initial column headers for the new table.
   */
  public Table(String[] columnHeaders) {
    this();

    for (String name : columnHeaders)
      addColumn(name);
  }

  /**
   * Creates a copy of the provided table containing only the specified columns.
   * @param table The table to create a copy of.
   * @param columnHeaders Specified the column headers and the order in which they should appear in the copied table.
   * @return Table A copy of the table containing only the specified column headers in the specified order.
   */
  public static Table filterColumns(Table table, String[] columnHeaders) {
    Table newTable = new Table(columnHeaders);

    for (Row row : table) {
      Row newRow = new Row();
      for (String header : columnHeaders)
        newRow.set(header, row.get(header));
      newTable.addRow(newRow);
    }

    return newTable;
  }

  /**
   * Adds a new column header to the table.
   * @param name The name of the new header to add to the table.
   */
  public void addColumn(String name) {
    if (!columnHeaders.contains(name))
      columnHeaders.add(name);
  }

  /**
   * Adds a new row of data to the table.
   * @param newRow The instance of <code>Row</code> to be added to the table.
   */
  public void addRow(Row newRow) {
    data.add(newRow);
  }

  /**
   * Returns the <code>Row</code> at the specified row index in the table.
   * @param index The index of the row to be returned.
   * @return Row The <code>Row</code> at the specified index in the table.
   */
  public Row getRow(int index) {
    return data.get(index);
  }

  /**
   * Returns the number of rows in the table.
   * @return int The number of rows in the table.
   */
  public int getRowCount() {
    return data.size();
  }

  /**
   * Returns the number of columns in the table.
   * @return int The number of columns in the table.
   */
  public int getColumnCount() {
    return columnHeaders.size();
  }
  
  /**
   * Returns a string representation of the table and contents.
   * @return String A human-readable formatted string containing the column headers and the values of each row in the table.
   */
  public String toString() {
    String finalString = "";
    for (String name : columnHeaders)
      finalString += "[" + name + "]    ";
    finalString = finalString.trim() + "\n";

    for (Row row : data) {
      String rowString = row.getColumnsAsString(columnHeaders);
      if (!rowString.isEmpty())
        finalString += rowString + "\n";
    }
    return finalString;
  }

  /**
   * Checks if this table object equal to another object.
   * @param obj The object to check for equality.
   * @return boolean True if <code>obj</code> is another Table object with the exact same column headers and contents on every row. False in all other cases.
   */
  @Override
  public boolean equals(Object obj) {
    if (obj == null)
      return false;
    if (!Table.class.isAssignableFrom(obj.getClass()))
      return false;
    if (getColumnCount() != ((Table)obj).getColumnCount())
      return false;
    return toString().equals(((Table)obj).toString());
  }
  
  /**
   * Returns a string representation of the table and contents.
   * @return String A human-readable formatted string containing the column headers and the values of each row in the table.
   */
  public String getAsCSV() {
    String finalString = "";
    for (String name : columnHeaders)
      finalString += name + ',';
    if (!finalString.isEmpty())
      finalString = finalString.substring(0, finalString.length() - 1) + "\n";

    for (Row row : data) {
      String rowString = row.getAsCSV(columnHeaders);
      if (!rowString.isEmpty())
        finalString += rowString + "\n";
    }
    return finalString;
  }

  /**
   * Returns an <code>ArrayList&lt;String&gt;</code> containing the column headers of the table.
   * @return <code>ArrayList&lt;String&gt;</code> The column headers of the table.
   */
  public ArrayList<String> getColumnHeaders() {
    return columnHeaders;
  }

  /**
   * Return column headers without any table alias.
   * @return ArrayList&lt;String&gt; A list of the column headers in this table, excluding any table alias.
   */
  public ArrayList<String> getColumnHeadersWithoutAlias() {
    ArrayList<String> columnHeaders = new ArrayList<String>();
    for (String header : this.columnHeaders)
      columnHeaders.add(columnHeaderRemoveTableAlias(header));
    return columnHeaders;
  }

  /**
   * Return column headers with table alias.
   * @param tableAlias The table alias to be added to all the returned column headers.
   * @return ArrayList&lt;String&gt; A list of the column headers in this table, including table alias. Where a column header has no table alias the value of <code>tableAlias</code> is used.
   */
  public ArrayList<String> getColumnHeadersWithAlias(String tableAlias) {
    ArrayList<String> columnHeaders = new ArrayList<String>();
    for (String header : this.columnHeaders)
      columnHeaders.add(columnHeaderAddTableAlias(header, tableAlias));
    return columnHeaders;
  }

  /**
   * Performs a full join between two tables.
   * For every row in the left table: for every row in the right table, the two rows are merged and the resulting row is added to the query results provided it satisfies the condition in the where clause and the join condition.
   * @param leftTableAlias The alias to be used in column headers when referencing columns in the left table.
   * @param rightTable The table to join. This table is merged with the left table.
   * @param rightTableAlias The alias to be used in column headers when referencing columns in the right table.
   * @param whereClause A condition expression used to filter the rows in the join results.
   * @param joinCondition A condition expression used to filter the rows in the join results.
   * @return Table A table containing the result of joining <code>leftTable</code> with <code>rightTable</code>.
   */
  public Table join(String leftTableAlias, Table rightTable, String rightTableAlias, String whereClause, String joinCondition) {
    ArrayList<String> allHeaders = new ArrayList<String>();
    if (leftTableAlias == null)
      allHeaders.addAll(getColumnHeaders());
    else
      allHeaders.addAll(getColumnHeadersWithAlias(leftTableAlias));
    
    if (rightTableAlias == null)
      allHeaders.addAll(rightTable.getColumnHeaders());
    else
      allHeaders.addAll(rightTable.getColumnHeadersWithAlias(rightTableAlias));

    Table resultTable = new Table(allHeaders.toArray(new String[allHeaders.size()]));

    boolean containsRequiredColumnsForWhereClause = resultTable.checkContainsColumnsInExpression(whereClause);
    boolean containsRequiredColumnsForJoin        = resultTable.checkContainsColumnsInExpression(joinCondition);
    
    for (Row leftRow : data) {
      for (Row rightRow : rightTable) {
        Row newRow = new Row();
        for (Map.Entry<String, String> pair : leftRow)
          newRow.set(columnHeaderAddTableAlias(pair.getKey(), leftTableAlias), pair.getValue());
        for (Map.Entry<String, String> pair : rightRow)
          newRow.set(columnHeaderAddTableAlias(pair.getKey(), rightTableAlias), pair.getValue());
        
        if ( (whereClause   == null || !containsRequiredColumnsForWhereClause || Query.resolveCondition(newRow, whereClause  ))
          && (joinCondition == null || !containsRequiredColumnsForJoin        || Query.resolveCondition(newRow, joinCondition)) )
          resultTable.addRow(newRow);
      }
    }
    return resultTable;
  }

  /**
   * Inserts rows from the specified table into this table for which the provided condition expression evaluates to true.
   * @param table The Table to insert data from.
   * @param alias The table alias to use when referencing column headers from the provided table.
   * @param whereClause The condition expression to use when asserting whether or not a row should be add to the table.
   */
  public void insert(Table table, String alias, String whereClause) {
    ArrayList<String> headersWithoutAlias = table.getColumnHeadersWithoutAlias();
    ArrayList<String> headersWithAlias    = table.getColumnHeadersWithAlias(alias);

    for (String columnHeader : headersWithAlias)
      addColumn(columnHeader);

    boolean containsRequiredColumns = checkContainsColumnsInExpression(whereClause);
    
    for (Row row : table) {
      Row newRow = new Row();
      for (int i = 0; i < headersWithAlias.size(); ++i)
        newRow.set(headersWithAlias.get(i), row.get(headersWithoutAlias.get(i)));
      if (whereClause == null || !containsRequiredColumns || Query.resolveCondition(newRow, whereClause))
        data.add(newRow);
    }
  }

  /**
   * Sorts a table by a given column in the specified order.
   * @param columnHeader The column to sort by.
   * @param direction The sort order to sort by. Either "ASC" or "DESC". May be upper or lower case.
   */
  public void sort(String columnHeader, String direction) {
    int orderModifier = direction.toLowerCase().equals("desc") ? -1 : 1;

    data.sort((rowA, rowB) -> {
      String valA = rowA.get(columnHeader);
      String valB = rowB.get(columnHeader);
      
      if (Query.isNumber(valA)) {
        Integer intA = Integer.parseInt(valA);
        Integer intB = Integer.parseInt(valB);
        return intA.compareTo(intB) * orderModifier;
      }
      
      return valA.compareTo(valB) * orderModifier;
    });
  }

  /**
   * Enables an instance of the class to be used in a <code>for</code> loop using the foreach syntax.
   * @return <code>Iterator&lt;Row&gt;</code> An iterator such that an instance of class <code>Table</code> may be used in a <code>for</code> loop using the foreach syntax.
   */
  public Iterator<Row> iterator() {
    return data.iterator();
  }

  /**
   * Read the contents of a table, including column headers, in CSV format, from the file at the given path.
   * @param filePath The path to the file to be read from.
   * @return boolean True if the table was successfully imported from the file. False otherwise.
   */
  public boolean readFromFileCSV(String filePath) {
    List<String> lines;
    try {
      lines = Files.readAllLines(Paths.get(filePath));
    } catch (IOException e) {
      return false;
    }

    if (!lines.isEmpty()) {
      // Add the column headers from the first line of the file
      // Then remove the column headers from the list of lines
      columnHeaders.addAll(new ArrayList<>(Arrays.asList(lines.get(0).split(","))));
      lines.remove(0);

      for (String line : lines) {
        Row newRow = new Row();
        String[] values = line.split(",");
        for (int i = 0; i < columnHeaders.size(); ++i)
          newRow.set(columnHeaders.get(i), values[i]);
        addRow(newRow);
      }
    }

    return true;
  }

  /**
   * Write the contents of a table, including column headers, in CSV format, to the file at the given path, overwriting any previous contents of the file.
   * @param filePath The path to the file to be written to.
   * @return boolean True if the table was successfully written to the file. False otherwise.
   */
  public boolean writeToFileCSV(String filePath) {
    PrintWriter writer;
    try {
      writer = new PrintWriter(filePath);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      return false;
    }
    writer.print(getAsCSV());
    writer.close();
    return true;
  }

  /**
   * Adds the specified alias to the provided column header.
   * If <code>columnHeader</code> contains a column header which already has a table alias, the unmodified <code>columnHeader</code> is returned.
   * If <code>columnHeader</code> contains a column header with no table alias, <code>tableAlias + '.'</code> is appended to the value of <code>columnHeader</code>.
   * @param columnHeader The name of the column to add the table alias to.
   * @param tableAlias The table alias to add to the column.
   * @return String The provided column header including the table alias.
   */
  private static String columnHeaderAddTableAlias(String columnHeader, String tableAlias) {
    return (columnHeader.indexOf('.') == -1) ? tableAlias + '.' + columnHeader : columnHeader;
  }

  /**
   * Removes the alias (should there be one) in the provided column header.
   * If the provided string contains a column header with no alias, the unmodified string is returned.
   * Should the provided string contain a column header with an alias, the column header is returned with the alias removed.
   * @param columnHeader The name of the column to remove the table alias from.
   * @return String The provided column header excluding the table alias.
   */
  private static String columnHeaderRemoveTableAlias(String columnHeader) {
    return columnHeader.substring(columnHeader.indexOf('.') + 1);
  }

  /**
   * Check that this table contains all the column names found in a given condition expression.
   * @param condition The expression to search for column names.
   * @return boolean True if all column names used in the expression are present in the table. False if one or more columns cannot be found.
   */
  private boolean checkContainsColumnsInExpression(String condition) {
    if (condition == null)
      return true;
    Pattern pattern = Pattern.compile("([^\\(\\)\\d\\s<=>]+)\\s+[<=>]\\s+(?>[\"\']\\S*[\"\']|\\d+)");
    Matcher matcher = pattern.matcher(condition);
    while (matcher.find())
      if (!columnHeaders.contains(matcher.group(1)))
        return false;
    return true;
  }
}
