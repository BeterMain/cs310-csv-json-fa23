package edu.jsu.mcis.cs310;

import com.github.cliftonlabs.json_simple.*;
import com.opencsv.*;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

public class Converter {
    
    /*
        
        Consider the following CSV data, a portion of a database of episodes of
        the classic "Star Trek" television series:
        
        "ProdNum","Title","Season","Episode","Stardate","OriginalAirdate","RemasteredAirdate"
        "6149-02","Where No Man Has Gone Before","1","01","1312.4 - 1313.8","9/22/1966","1/20/2007"
        "6149-03","The Corbomite Maneuver","1","02","1512.2 - 1514.1","11/10/1966","12/9/2006"
        
        (For brevity, only the header row plus the first two episodes are shown
        in this sample.)
    
        The corresponding JSON data would be similar to the following; tabs and
        other whitespace have been added for clarity.  Note the curly braces,
        square brackets, and double-quotes!  These indicate which values should
        be encoded as strings and which values should be encoded as integers, as
        well as the overall structure of the data:
        
        {
            "ProdNums": [
                "6149-02",
                "6149-03"
            ],
            "ColHeadings": [
                "ProdNum",
                "Title",
                "Season",
                "Episode",
                "Stardate",
                "OriginalAirdate",
                "RemasteredAirdate"
            ],
            "Data": [
                [
                    "Where No Man Has Gone Before",
                    1,
                    1,
                    "1312.4 - 1313.8",
                    "9/22/1966",
                    "1/20/2007"
                ],
                [
                    "The Corbomite Maneuver",
                    1,
                    2,
                    "1512.2 - 1514.1",
                    "11/10/1966",
                    "12/9/2006"
                ]
            ]
        }
        
        Your task for this program is to complete the two conversion methods in
        this class, "csvToJson()" and "jsonToCsv()", so that the CSV data shown
        above can be converted to JSON format, and vice-versa.  Both methods
        should return the converted data as strings, but the strings do not need
        to include the newlines and whitespace shown in the examples; again,
        this whitespace has been added only for clarity.
        
        NOTE: YOU SHOULD NOT WRITE ANY CODE WHICH MANUALLY COMPOSES THE OUTPUT
        STRINGS!!!  Leave ALL string conversion to the two data conversion
        libraries we have discussed, OpenCSV and json-simple.  See the "Data
        Exchange" lecture notes for more details, including examples.
        
    */
    
    @SuppressWarnings("unchecked")
    public static String csvToJson(String csvString) {
        
        String result = "{}"; // default return value; replace later!
        
        try {
            // Starting Vars
            List<String[]> full = new ArrayList<>();
            List<String> prodNumList = new ArrayList<>();
            LinkedHashMap<String, Object> finalObject = new LinkedHashMap<>();
            JsonArray data = new JsonArray();
            
            // Convert string into list of strings
            CSVReader reader = new CSVReader(new StringReader(csvString));
            full = reader.readAll();
            
            // Iterate through the List
            Iterator<String[]> iterator = full.iterator();
            
            // Convert
            if (iterator.hasNext()) {
                String[] headings = iterator.next();
                
                while (iterator.hasNext()) {
                    String[] csvRecord = iterator.next();
                    LinkedHashMap<String, String> jsonRecord = new LinkedHashMap<>();
                    
                    for (int i = 1; i < headings.length; ++i) {
                       jsonRecord.put(headings[i], csvRecord[i]);
                    }
                    prodNumList.add(csvRecord[0]);
                    data.add(jsonRecord);
                }
            }
            
            // Adding the data into a JsonObject 
            List<Object[]> dataList = new ArrayList<>();
            List<String> columList = new ArrayList<>();
            
            // Config column list
            columList.add("ProdNum");
            for (Object column : data.getMap(0).keySet()) {
                columList.add(column.toString());
            }
            
            // Config data list
            for (int i = 0; i < data.size(); i++) {
                dataList.add(data.getMap(i).values().toArray());
            }
            
            for (Object[] dataObject : dataList) {
                for (int i = 0; i < dataObject.length; i++) {
                    try {
                        if (dataObject[i] instanceof String string) {
                            dataObject[i] = Integer.valueOf(string);
                        }
                    } catch (NumberFormatException e) {
                        //ignore
                    }
                }
            }
            
            // Put all inside json object
            finalObject.put("ProdNums", prodNumList);
            finalObject.putIfAbsent("ColHeadings", columList);
            finalObject.put("Data", dataList);
            
            // Serialize and subm
            result = Jsoner.serialize(finalObject);
            
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return result.trim();
        
    }
    
    @SuppressWarnings("unchecked")
    public static String jsonToCsv(String jsonString) {
        
        String result = ""; // default return value; replace later!
        
        try {
            // Vars
            JsonObject jsonObject = Jsoner.deserialize(jsonString, new JsonObject());
            JsonArray prodNums = (JsonArray) jsonObject.get("ProdNums");
            JsonArray headings = (JsonArray) jsonObject.get("ColHeadings");
            JsonArray data = (JsonArray) jsonObject.get("Data");
            
            StringWriter writer = new StringWriter();
            CSVWriter csvWriter = new CSVWriter(writer, ',', '"', '\\', "\n");
            
            // Write column headings to CSV
            String[] columnHeadingsArray = new String[headings.size()];
            for (int i = 0; i < headings.size(); i++) {
                columnHeadingsArray[i] = headings.getString(i);
            }
            csvWriter.writeNext(columnHeadingsArray);

            // Write data rows to CSV
            for (int i = 0; i < data.size(); i++) {
                JsonArray rowData = (JsonArray) data.get(i);
                String[] rowDataArray = new String[columnHeadingsArray.length];
                rowDataArray[0] = prodNums.getString(i);
                for (int j = 0; j < rowData.size(); j++) {
                    if (j == 2 && rowData.getString(j).chars().count() == 1) {
                        rowDataArray[j+1] = "0" + rowData.getString(j);
                    }
                    else {
                        rowDataArray[j+1] = rowData.getString(j);
                    }
                }
                csvWriter.writeNext(rowDataArray);
            }
            
            // Close the CSV writer and submit
            csvWriter.close();
            
            result = writer.toString();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return result.trim();
        
    }
    
}
