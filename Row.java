import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * The Row class represents an individual row of data from a table as an unordered collection of values each associated with a column header.
 * Implements <code>Iterable&lt;Map.Entry&lt;String, String&gt;&gt;</code> so that an instance of <code>Row</code> can be used in a foreach loop to iterate over the column headers and values in the row.
 * 
 * @author Addo White
 * @version 1.0
 * @since 2017-10-27
 */
public class Row implements Iterable<Map.Entry<String, String>> {

  /**
   * A collection of key-value pairs which stores the data contained on this row.
   * The intended use is that column headers will be provided as the keys and the data at a particular row + column will be the value.
   */
  HashMap<String, String> data;

  /**
   * Class constructor.
   * Initializes class members
   */
  public Row() {
    data = new HashMap<String, String>();
  }

  /**
   * Gets the number of columns on the row.
   * @return The number of column on the row.
   */
  public int getColumnCount() {
    return data.size();
  }

  /**
   * This method is used to get the value stored at a particular column on the row.
   * @param columnHeader The header of the column.
   * @return String The value stored at the specified column on the row or the string "null" if the column does not exist.
   */
  public String get(String columnHeader) {
    return data.get(columnHeader);
  }

  /**
   * This method is used to set the value store at a particular column on the row.
   * @param columnHeader The header of the column.
   * @param value The value to store in the column on the row.
   */
  public void set(String columnHeader, String value) {
    data.put(columnHeader, value);
  }
  
  /**
   * Returns a <code>String</code> containing the values stored in the specified columns in the specified order.
   * @param columnHeaders An ArrayList of Strings containing the headers of the columns desired.
   * @return String A <code>String</code> containing the values of all columns in <code>columnHeaders</code> in matching order, separated by spaces.
   */
  public String getColumnsAsString(ArrayList<String> columnHeaders) {
    String finalString = "";

    for (String header : columnHeaders) {
      String value = get(header);
      value = (value == null ? "null" : value);
      finalString += value;
      for (int i = 0; i < header.length() - value.length() + 6; ++i)
        finalString += ' ';
    }

    return finalString.trim();
  }
  
  /**
   * Returns a <code>String</code> containing the values stored in the specified columns in the specified order separated by commas.
   * @param columnHeaders An ArrayList of Strings containing the headers of the columns desired.
   * @return String A <code>String</code> containing the values of all columns separated by commas.
   */
  public String getAsCSV(ArrayList<String> columnHeaders) {
    String finalString = "";

    for (String header : columnHeaders)
      finalString += get(header) + ',';

    if (finalString.isEmpty())
      return "";

    return finalString.substring(0, finalString.length() - 1);
  }

  /**
   * Enables an instance of the class to be used in a <code>for</code> loop using the foreach syntax.
   * @return <code>Iterator&lt;Map.Entry&lt;String, String&gt;&gt;</code> An iterator such that an instance of class <code>Row</code> may be used in a <code>for</code> loop using the foreach syntax.
   */
  @Override
  public Iterator<Map.Entry<String, String>> iterator() {
    return data.entrySet().iterator();
  }
}
