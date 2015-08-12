package com.csiro.teststuff;

import com.amazonaws.services.cloudwatch.model.Datapoint;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsResult;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.DateTime;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

/**
 * Hello world!
 *
 */
public class App {

    public static void MaxArray() {
        Integer[] In = new Integer[]{5, -2, -1, 2};
        Integer[] out = new Integer[5];
        int j = 0;
        int temp = In[0];
        boolean flag = false;
        for (int i = 0; i < In.length; i++) {
            if (In[i] > 0) {
                out[j] = In[i];
                j++;
                flag = true;
            } else if (In[i] >= temp) {
                temp = In[i];
            }
        }
        if (!flag) {
            out[j] = temp;
        }
        j = 0;
        while (out[j] != null) {
            System.out.println(out[j]);
            j++;
        }
    }

    public static void QuickSort(int[] list, int start, int end) {
        if (end - start < 2) {
            return;
        }
        int p = start + ((end - start) / 2);
        QuickSort(list, start, p);
        QuickSort(list, p + 1, end);

    }

    public static void main(String[] args) throws Exception {
        System.out.println("time: " + java.lang.System.currentTimeMillis());

    }

    public static void ReplaceText() {
        try {
            // for (int i = 1; i <= 13; i++) {
            File file = new File("C:\\Users\\kho01f\\Desktop\\First Contribution\\TPC-H\\AdmissionControl\\mix14.csv");
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = "", oldtext = "";
            while ((line = reader.readLine()) != null) {
                oldtext += line + "\r\n";
            }
            reader.close();
             // replace a word in a file
            //String newtext = oldtext.replaceAll("drink", "Love");

            //To replace a line in a file
            String newtext1 = oldtext.replaceAll("%", " ");
            String newtext2 = newtext1.replaceAll("java /", " ");
            FileWriter writer = new FileWriter("C:\\Users\\kho01f\\Desktop\\First Contribution\\TPC-H\\AdmissionControl\\mix14.csv");
            writer.write(newtext2);
            writer.close();
            //}
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public static void HiveStatsCollector(int fileNameIndex) throws Exception {
        int count, StatIndex, RowNoIndex, DataSizeIndex, BasicIndex;
        String operatorName;
        HashMap<String, Integer> operatorNo = new HashMap<String, Integer>();
        HashMap<String, Long> operatorRowNo = new HashMap<String, Long>();
        HashMap<String, Long> operatorDataSize = new HashMap<String, Long>();

        try {
            File file = new File("C:\\Users\\kho01f\\Desktop\\Second Contribution\\Give me my share & no one gets hurt\\Experiment\\temp\\" + fileNameIndex + ".txt");
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = "", oldtext = "";

            while ((line = reader.readLine()) != null) {
                if ((line.contains("Operator") || line.contains("TableScan") || line.contains("Extract") || line.contains("Limit")
                        || line.contains("Map Reduce") || line.contains("Map Reduce Local Work")) && (!line.contains(":"))) {
                    oldtext += "\r\n" + line;
                    count = operatorNo.containsKey(line.trim()) ? operatorNo.get(line.trim()) : 0;
                    operatorNo.put(line.trim(), count + 1);
                }
                if (line.contains("Statistics")) {
                    oldtext += line;
                }
            }
            reader.close();

            FileWriter writer = new FileWriter("C:\\Users\\kho01f\\Desktop\\Second Contribution\\Give me my share & no one gets hurt\\Experiment\\temp\\" + fileNameIndex + ".txt");
            writer.write(oldtext);
            writer.close();
            file = new File("C:\\Users\\kho01f\\Desktop\\Second Contribution\\Give me my share & no one gets hurt\\Experiment\\temp\\" + fileNameIndex + ".txt");
            reader = new BufferedReader(new FileReader(file));
            while ((line = reader.readLine()) != null) {
                if (line.contains("Statistics")) {
                    StatIndex = line.indexOf("Statistics:");
                    RowNoIndex = line.indexOf("Num rows: ");
                    DataSizeIndex = line.indexOf(" Data size: ");
                    BasicIndex = line.indexOf(" Basic");
                    String str = line.substring(0, StatIndex).trim();
                    if (operatorRowNo.containsKey(str)) {
                        operatorRowNo.put(str, operatorRowNo.get(str) + Long.parseLong(line.substring(RowNoIndex + 10, DataSizeIndex).trim()));
                        operatorDataSize.put(str, operatorDataSize.get(str) + Long.parseLong(line.substring(DataSizeIndex + 12, BasicIndex).trim()));
                    } else {
                        operatorRowNo.put(str, Long.parseLong(line.substring(RowNoIndex + 10, DataSizeIndex)));
                        operatorDataSize.put(str, Long.parseLong(line.substring(DataSizeIndex + 12, BasicIndex)));
                    }
                }
            }
            reader.close();

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        writeToExcel(operatorNo, operatorRowNo, operatorDataSize, fileNameIndex);

//        for (Map.Entry<String, Integer> entry : operatorNo.entrySet()) {
//            System.out.println(entry.getKey() + "/" + entry.getValue());
//        }
//        System.out.println(".................Row No.....................");
//
//        for (Map.Entry<String, Long> entry : operatorRowNo.entrySet()) {
//            System.out.println(entry.getKey() + "/" + entry.getValue());
//
//        }
//        System.out.println(".................Data size.....................");
//
//        for (Map.Entry<String, Long> entry : operatorDataSize.entrySet()) {
//            System.out.println(entry.getKey() + "/" + entry.getValue());
//        }
    }

    public static void writeToExcel(HashMap<String, Integer> operatorNo, HashMap<String, Long> operatorRowNo,
            HashMap<String, Long> operatorDataSize, int rowIndex) throws BiffException, IOException, WriteException {
        String filePath = "C:\\Users\\kho01f\\Desktop\\Second Contribution\\QueryResult.xls";
        WritableWorkbook wworkbook;
        WritableSheet wsheet;
        File f = new File(filePath);
        if (f.exists() && !f.isDirectory()) {
            Workbook existingWorkbook = Workbook.getWorkbook(new File(filePath));
            wworkbook = Workbook.createWorkbook(new File(filePath), existingWorkbook);
            wsheet = wworkbook.getSheet(0);
        } else {
            wworkbook = Workbook.createWorkbook(new File(filePath));
            wsheet = wworkbook.createSheet("First Sheet", 0);
        }

        int titleIndex = 0;
        int colIndex = 0;
        int newIndex = 0;
        boolean flag;
        for (Map.Entry<String, Integer> entry : operatorNo.entrySet()) {
            flag = false;
            colIndex = 0;
            newIndex = 0;
            while (!wsheet.getCell(colIndex, titleIndex).getContents().isEmpty()) {
                if (wsheet.getCell(colIndex, titleIndex).getContents().compareTo(entry.getKey()) == 0) {
                    wsheet.addCell(new Number(colIndex, rowIndex, entry.getValue()));
                    colIndex++;
                    flag = true;
                    break;
                }
                newIndex++;
                colIndex++;
            }
            if (!flag) {
                wsheet.addCell(new Label(newIndex, titleIndex, entry.getKey()));
                wsheet.addCell(new Number(newIndex, rowIndex, entry.getValue()));
            }
        }

        for (Map.Entry<String, Long> entry : operatorRowNo.entrySet()) {
            flag = false;
            colIndex = 0;
            newIndex = 0;
            while (!wsheet.getCell(colIndex, titleIndex).getContents().isEmpty()) {
                if (wsheet.getCell(colIndex, titleIndex).getContents().compareTo(entry.getKey() + " RN") == 0) {
                    wsheet.addCell(new Number(colIndex, rowIndex, entry.getValue()));
                    colIndex++;
                    flag = true;
                    break;
                }
                newIndex++;
                colIndex++;
            }
            if (!flag) {
                wsheet.addCell(new Label(newIndex, titleIndex, entry.getKey() + " RN"));
                wsheet.addCell(new Number(newIndex, rowIndex, entry.getValue()));
            }
        }

        for (Map.Entry<String, Long> entry : operatorDataSize.entrySet()) {
            flag = false;
            colIndex = 0;
            newIndex = 0;
            while (!wsheet.getCell(colIndex, titleIndex).getContents().isEmpty()) {
                if (wsheet.getCell(colIndex, titleIndex).getContents().compareTo(entry.getKey() + " DS") == 0) {
                    wsheet.addCell(new Number(colIndex, rowIndex, entry.getValue()));
                    colIndex++;
                    flag = true;
                    break;
                }
                newIndex++;
                colIndex++;
            }
            if (!flag) {
                wsheet.addCell(new Label(newIndex, titleIndex, entry.getKey() + " DS"));
                wsheet.addCell(new Number(newIndex, rowIndex, entry.getValue()));
            }
        }
        wworkbook.write();
        wworkbook.close();
    }

    public static void RemoveTextLine() {

        try {
            String line = "";
            String pattern = "Count/Sec =";
            String oldStr = "", newStr = "";
            File file = new File("C:\\Users\\kho01f\\Desktop\\First Contribution\\TPC-H\\log\\r.out");
            BufferedReader in = new BufferedReader(new FileReader(file));
            while ((line = in.readLine()) != null) {
                if (line.toLowerCase().contains(pattern.toLowerCase())) {
                    oldStr += line + "\r\n";
                }
            }
            newStr = oldStr.replaceAll(pattern, " ");
            FileWriter writer = new FileWriter("C:\\Users\\kho01f\\Desktop\\First Contribution\\TPC-H\\log\\r.csv");
            writer.write(newStr);

            writer.close();
            in.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }

    public static void ConcatANUemail() {

        try {
            String line = "";
            String newStr = "";
            File file = new File("C:\\Users\\kho01f\\Desktop\\DB Lab 2014\\group5.csv");
            BufferedReader in = new BufferedReader(new FileReader(file));
            while ((line = in.readLine()) != null) {
                String[] Id = line.split(" ");
                for (int i = 0; i < Id.length; i++) {
                    Id[i] = Id[i].concat("@anu.edu.au");
                    newStr = newStr + Id[i] + "\r\n";
                }

            }
            FileWriter writer = new FileWriter("C:\\Users\\kho01f\\Desktop\\DB Lab 2014\\group5.csv");
            writer.write(newStr);

            writer.close();
            in.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }

}
