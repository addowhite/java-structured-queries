import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A functional interface to allow lambda expressions to be passed as an argument to {@link Query#regexReplaceMatches(String input, String regex, MatchReplacementResolver resolver)}.
 * @see Query#regexReplaceMatches(String input, String regex, MatchReplacementResolver resolver)
 */
@FunctionalInterface
interface MatchReplacementResolver {
  public String execute(Matcher matcher);
}

/**
 * The Query class represents an operation, or sequence of operations, on a number of tables of data.
 * A Query instance is constructed via its <a href="https://en.wikipedia.org/wiki/Fluent_interface">Fluent Interface</a>, a flavour of builder pattern which usually relies on method chaining.
 * Methods that are part of the Fluent Interface are as follows:
 * {@link Query#insertInto(Table table, String[] columnHeaders)}
 * {@link Query#values(String[] values)}
 * {@link Query#select(String[] columnHeaders)}
 * {@link Query#from(Table table, String alias)}
 * {@link Query#where(String condition)}
 * {@link Query#join(Table table, String alias)}
 * {@link Query#on(String condition)}
 * {@link Query#orderby(String order)}
 *
 * Once the above methods have been used to construct a Query object {@link Query#execute()} may be called
 * to invoke the defined query and return a {@link Table} containing the results of the query.
 *
 * @author Addo White
 * @version 1.0
 * @since 2017-10-27
 */
public class Query {

  /** An array of strings containing the headers of columns which should be included in the results returned by the query */
  private String[] selectedColumns;

  /** An array of strings containing the headers of columns which will be inserted into the 'insert table' */
  private String[] insertedColumns;

  /** An array of strings containing the values which will be inserted into the 'insert table' */
  private String[] insertedValues;

  /** A reference to the table into which the results of this query should be inserted. */
  private Table insertTable;

  /** A reference to the table which all joins will be applied to. */
  private Table fromTable;

  /** A table which will store the intermediate and final results of the query during both construction and execution. */
  private Table resultsTable;

  /** The table alias which will be applied to all column in the 'from' table. */
  private String fromTableAlias;

  /** A string containing the 'where clause': the condition expression used to filter query results. */
  private String whereClause;

  /** A string containing an expression defining the order in which the results of the query should appear. */
  private String sortOrder;

  /** An ArrayList of tables which will be joined with the 'from' table. */
  private ArrayList<Table> joinTables;

  /** An ArrayList containing the tables alias of each table at the corresponding index of {@link Query#joinTables}. */
  private ArrayList<String> joinTablesAlias;

  /** An ArrayList of condition expressions used when joining tables. Each condition expression is used to join the table at the corresponding index of {@link Query#joinTables}. */
  private ArrayList<String> joinConditions;

  public Query() {
    insertTable     = null;
    insertedColumns = null;
    insertedValues  = null;
    fromTable   = null;
    whereClause = null;
    sortOrder   = null;
    joinTables      = new ArrayList<Table>();
    joinTablesAlias = new ArrayList<String>();
    joinConditions  = new ArrayList<String>();
    resultsTable    = new Table();
  }

  /**
   * Evaluates a conditional expression in the context of a row of data.
   * @param row The row used for context when evaluating the expression.
   * @param condition The condition expression to evaluate.
   * @return boolean The boolean value that the given expression resolves to in the context of the provided row.
   */
  public static boolean resolveCondition(Row row, String condition) {
    if (condition == null)
      return true;
    condition = condition.trim();
    if (condition.equals(""))
      return true;

    condition = regexReplaceMatches(condition, "\\(([^()]+?)\\)", (Matcher matcher) -> {
      return resolveCondition(row, matcher.group(1)) ? "1" : "0";
    });

    condition = regexReplaceMatches(condition, "(\\d+|\\S+\\s*=\\s*\\S+)\\s*AND\\s*(\\d+|\\S+\\s*=\\s*\\S+)", (Matcher matcher) -> {
      return resolveCondition(row, matcher.group(1)) && resolveCondition(row, matcher.group(2)) ? "1" : "0";
    });

    condition = regexReplaceMatches(condition, "(\\d+|\\S+\\s*=\\s*\\S+)\\s*OR\\s*(\\d+|\\S+\\s*=\\s*\\S+)", (Matcher matcher) -> {
      return resolveCondition(row, matcher.group(1)) || resolveCondition(row, matcher.group(2)) ? "1" : "0";
    });

    condition = regexReplaceMatches(condition, "(\\S+)\\s*([<=>])\\s*(\\S+)", (Matcher matcher) -> {
      String lValue = matcher.group(1);
      String rValue = matcher.group(3);

      if (isString(lValue))
        lValue = lValue.replaceAll("\'", "").replaceAll("\"", "");
      else if (!isNumber(lValue))
        lValue = row.get(lValue);

      if (isString(rValue))
        rValue = rValue.replaceAll("\'", "").replaceAll("\"", "");
      else if (!isNumber(rValue))
        rValue = row.get(rValue);

      lValue = (lValue == null ? "null" : lValue);
      rValue = (rValue == null ? "null" : rValue);

      switch (matcher.group(2)) {
        case "=":
          return lValue.equals(rValue) ? "1" : "0";
        case ">":
          return Integer.parseInt(lValue) > Integer.parseInt(rValue) ? "1" : "0";
        case "<":
          return Integer.parseInt(lValue) < Integer.parseInt(rValue) ? "1" : "0";
        default:
          return "0";
      }
    });

    return condition.equals("1");
  }

  /**
   * Checks if the given string represents a numerical value.
   * @param valueString The string to check.
   * @return boolean True if the provided string contains a base-10 numerical representation, false otherwise.
   */
  public static boolean isNumber(String valueString) {
    // Check that the entire string only contains digits and either zero or one '.' character.
    return Pattern.compile("^\\s*(-?\\d*.?\\d+)\\s*$").matcher(valueString).find();
  }

  /**
   * Checks if the given string contains a string expression.
   * e.g. "string" or 'string'
   * @param valueString The string to check.
   * @return boolean True if the provided string contains a representation of a string value, false otherwise.
   */
  public static boolean isString(String valueString) {
    char first = valueString.charAt(0), last = valueString.charAt(valueString.length() - 1);
    return (first == '\'' || first == '\"') && (last == '\'' || last == '\"');
  }

  public String toString() {
    String finalString = "";

    if (insertTable != null) {
      finalString += "INSERT INTO [anonymous table] (";
      for (String header : insertedColumns)
        finalString += header + ", ";
      finalString = finalString.substring(0, finalString.length() - 2) + ")\n";
    }

    if (insertedValues != null) {
      finalString += "VALUES (\n    ";
      int i = 0, columnsPerRow = insertedColumns.length;
      for (String value : insertedValues) {
        finalString += value + ", ";
        if (++i == columnsPerRow) {
          finalString = finalString.substring(0, finalString.length() - 1);
          finalString += "\n    ";
          i = 0;
        }
      }
      finalString = finalString.substring(0, finalString.length() - 6) + "\n)\n";
    }

    if (selectedColumns != null) {
      finalString += "SELECT ";
      for (String header : selectedColumns)
        finalString += header + ", ";
      finalString = finalString.substring(0, finalString.length() - 2) + "\n";
    }

    if (fromTable != null)
      finalString += "FROM [anonymous table] AS \"" + fromTableAlias + "\"\n";

    if (!joinTables.isEmpty()) {
      for (int i = 0; i < joinTables.size(); ++i) {
        String alias = joinTablesAlias.get(i);
        finalString += "JOIN [anonymous table] AS \"" + alias + "\"\n";

        String condition = null;
        if (i < joinConditions.size()) {
          condition = joinConditions.get(i);
          finalString += "    ON " + condition + "\n";
        }
      }
    }

    if (whereClause != null)
      finalString += "WHERE " + whereClause + "\n";

    if (sortOrder != null)
      finalString += "ORDER BY " + sortOrder + "\n";

    return finalString;
  }

  /**
   * Specifies a table into which this query will insert its results.
   * Part of the Fluent Interface.
   * @param table The table into which the query will insert its results.
   * @param columnHeaders An array of column headers from the query results to insert into the specified table.
   * @return Query Returns '<code>this</code>' such that this method may be chained with other methods in the Fluent Interface.
   */
  public Query insertInto(Table table, String[] columnHeaders) {
    insertTable = table;
    insertedColumns = columnHeaders;
    for (String name : columnHeaders)
      resultsTable.addColumn(name);
    return this;
  }

  /**
   * Specifies values for a row, or number of rows, to be included in the query results.
   * Part of the Fluent Interface.
   * @param values An array of string values to be used in the inserted rows. Multiple rows of data may be supplied via this 1-dimentional array and they will be inserted as separate rows.
   * @return Query Returns '<code>this</code>' such that this method may be chained with other methods in the Fluent Interface.
   */
  public Query values(String[] values) {
    insertedValues = values;
    ArrayList<String> columnHeaders = resultsTable.getColumnHeaders();
    int colCount = resultsTable.getColumnCount();
    int rowCount = values.length / colCount;
    Row newRow;
    for (int y = 0; y < rowCount; ++y) {
      newRow = new Row();
      for (int x = 0; x < colCount; ++x)
        newRow.set(columnHeaders.get(x), values[y * colCount + x]);
      resultsTable.addRow(newRow);
    }
    return this;
  }

  /**
   * Specifies which columns should be included in the results of the query.
   * Part of the Fluent Interface.
   * @param columnHeaders An array of the column headers which should appear in the query results.
   * @return Query Returns '<code>this</code>' such that this method may be chained with other methods in the Fluent Interface.
   */
  public Query select(String[] columnHeaders) {
    selectedColumns = columnHeaders;
    for (String name : columnHeaders)
      resultsTable.addColumn(name);
    return this;
  }

  /**
   * Specifies a table from which data will be selected and used as the base of all successive joins.
   * Part of the Fluent Interface.
   * @param table The table from which to select data.
   * @param alias The table alias to be used when referencing columns from the specified table.
   * @return Query Returns '<code>this</code>' such that this method may be chained with other methods in the Fluent Interface.
   */
  public Query from(Table table, String alias) {
    fromTable = table;
    fromTableAlias = alias;
    return this;
  }

  /**
   * Specifies a condition expression to be evaluated when aquiring rows of data for the query.
   * A row is not added to the query results if this condition evaluates to false.
   * This condition is enforced as a constraint for all rows in the query.
   * Part of the Fluent Interface.
   * @param condition The condition to be used to filter results of the query.
   * @return Query Returns '<code>this</code>' such that this method may be chained with other methods in the Fluent Interface.
   */
  public Query where(String condition) {
    whereClause = condition;
    return this;
  }

  /**
   * Specifies a table from which data will be selected and used as the base of all successive joins.
   * Part of the Fluent Interface.
   * @param table The table from which to select data.
   * @param alias The table alias to be used when referencing columns from the specified table.
   * @return Query Returns '<code>this</code>' such that this method may be chained with other methods in the Fluent Interface.
   */
  public Query join(Table table, String alias) {
    joinTables.add(table);
    joinTablesAlias.add(alias);
    return this;
  }

  /**
   * Specifies a condition expression to be evaluated when aquiring rows of data for the query.
   * A row is not added to the query results if this condition evaluates to false.
   * This condition is enforced as a contraint for only the previously specified 'join' table in the Fluent Interface.
   * Part of the Fluent Interface.
   * @param condition The condition to be used to filter results of the query.
   * @return Query Returns '<code>this</code>' such that this method may be chained with other methods in the Fluent Interface.
   */
  public Query on(String condition) {
    joinConditions.add(condition);
    return this;
  }

  /**
   * Specifies the order in which the results of the query should appear.
   * Part of the Fluent Interface.
   * @param order A string containing a single column name to specify which values to sort by and either "ASC" or "DESC" to specify the sort order.
   * @return Query Returns '<code>this</code>' such that this method may be chained with other methods in the Fluent Interface.
   */
  public Query orderby(String order) {
    sortOrder = order;
    return this;
  }

  /**
   * Executes the query defined prior using the Fluent Interface.
   * This is the terminating method of the Fluent Interface.
   * @return Table Returns a table containing the results of the query.
   */
  public Table execute() {
    if (fromTable != null) {
      resultsTable.insert(fromTable, fromTableAlias, whereClause);

      for (int i = 0; i < joinTables.size(); ++i) {
        Table joinTable = joinTables.get(i);
        String alias = joinTablesAlias.get(i);
        String joinCondition = null;

        if (i < joinConditions.size())
          joinCondition = joinConditions.get(i);

        resultsTable = resultsTable.join(null, joinTable, alias, whereClause, joinCondition);
      }

      resultsTable = Table.filterColumns(resultsTable, selectedColumns);
    }

    if (sortOrder != null && !sortOrder.equals("")) {
      int spaceIndex = sortOrder.indexOf(' ');
      if (spaceIndex != -1) {
        String columnHeader = sortOrder.substring(0, spaceIndex);
        String direction = sortOrder.substring(spaceIndex + 1);
        resultsTable.sort(columnHeader, direction);
      }
    }

    if (insertTable != null) {
      for (String columnHeader : insertTable.getColumnHeaders())
        insertTable.addColumn(columnHeader);
      for (Row row : resultsTable)
        insertTable.addRow(row);
    }

    return resultsTable;
  }

  /**
   * Repeatedly replaces sections a string that match a regular expression with the result of a lambda expression.
   * The lambda expression is provided with an instance of <a href="https://docs.oracle.com/javase/7/docs/api/java/util/regex/Matcher.html">Matcher</a>
   * with information about the match and the contents of any capture groups from the regular expression.
   * Matches are repeatedly searched for and replaced until no more matches are present in the input string.
   * This means that should a replacement generate another match, that too will be found and replaced.
   *
   * @param input The string in which to search for matches and perform replacements.
   * @param regex The regular expression find matches for.
   * @param resolver The lambda expression to call for every match.
   * @return String A string with all matches of the regular expression replaced with the results of envoking the {@link MatchReplacementResolver}.
   *
   * @see <a href="https://docs.oracle.com/javase/7/docs/api/java/util/regex/Matcher.html">Matcher</a>
   * @see MatchReplacementResolver
   */
  private static String regexReplaceMatches(String input, String regex, MatchReplacementResolver resolver) {
    Matcher matcher;
    Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
    StringBuffer resultString = new StringBuffer();

    while (true) {
      resultString.setLength(0);
      matcher = pattern.matcher(input);

      while (matcher.find())
        matcher.appendReplacement(resultString, resolver.execute(matcher));
      matcher.appendTail(resultString);

      if (resultString.toString().equals(input))
        break;

      input = resultString.toString();
    }

    return resultString.toString();
  }

}
