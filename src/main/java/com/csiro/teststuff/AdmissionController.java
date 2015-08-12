package com.csiro.teststuff;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.Iterator;
import matlabcontrol.MatlabProxyFactory;
import matlabcontrol.MatlabProxy;
import matlabcontrol.MatlabProxyFactoryOptions;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author kho01f
 */
public class AdmissionController {

    public double[] CalcPdfProb(double lowerBound, double upperBound, int index) throws Exception {

        double[] probs = new double[2];
        //MATLB connection
        MatlabProxyFactoryOptions options = new MatlabProxyFactoryOptions.Builder()
                .setUsePreviouslyControlledSession(true)
                .setHidden(true)
                .setMatlabLocation(null).build();
        MatlabProxyFactory factory = new MatlabProxyFactory(options);
        MatlabProxy proxy = factory.getProxy();
        proxy.eval("load('C:\\Users\\kho01f\\Desktop\\First Contribution\\TPC-H\\concurrent\\seems_cool.mat')");
        String func = "CalcPdfProb(" + lowerBound + "," + upperBound + "," + index + ")";
        probs[0] = ((double[]) proxy.returningEval(func, 2)[0])[0];
        probs[1] = ((double[]) proxy.returningEval(func, 2)[1])[0];
        proxy.disconnect();
        return probs;
    }

    public double PredictSinglePoint(int queryIndex) {

        // In a compeletd version, we will invoke REPTree technique from WEKA library directly.
        String fileURL = "C:\\Users\\kho01f\\Desktop\\First Contribution\\TPC-H\\concurrent\\AdmissionControl.xlsx";
        return readSinglePointPrediction(fileURL, queryIndex);
    }

    public boolean missSLA(String fileURL, double threshhold, int threshholdMissNo) {
        int i = 0;
        boolean flag = false;
        try {
            String splitBy = ",";
            BufferedReader br = new BufferedReader(new FileReader(fileURL));
            String line;
            while ((line = br.readLine()) != null) {
                String[] b = line.split(splitBy);
                if (Integer.parseInt(b[1]) >= threshhold){
                    i++;
                }
            }
            br.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (i >= threshholdMissNo) {
            flag = true;
        }
        return flag;
    }

    public double readSinglePointPrediction(String fileURL, int rowIndex) {

        double key = 0.0;
        try {
            FileInputStream file = new FileInputStream(new File(fileURL));

            //Create Workbook instance holding reference to .xlsx file
            XSSFWorkbook workbook = new XSSFWorkbook(file);

            //Get first/desired sheet from the workbook
            XSSFSheet sheet = workbook.getSheetAt(0);
            // for (int rowIndex = 1; rowIndex <= 33; rowIndex++) {
            XSSFRow row = sheet.getRow(rowIndex);
            if (row != null) {

                for (int colIndex = 0; colIndex < row.getPhysicalNumberOfCells(); colIndex++) {
                    // the desired values are in column 3!
                    if (colIndex == 3) {
                        XSSFCell cell = row.getCell(colIndex);
                        if (cell != null) {
                            key = cell.getNumericCellValue();
                        }
                    }
                }
            }

            //}
            file.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return key;
    }

    public boolean SinglePointAdmission(int queryIndex, double threshhold) {

        boolean flag = false;
        double singlePointPrediction = PredictSinglePoint(queryIndex);
        if (singlePointPrediction < threshhold) {
            flag = true;
        }
        return flag;
    }

    public boolean DistributionBasedAdmission(int queryIndex, double threshhold) throws Exception {

        boolean flag = false;
        double lowerBound = 50;
        double[] probs = CalcPdfProb(lowerBound, threshhold, queryIndex);
        if ((probs[0] > 15)) {
            flag = true;
        }
        return flag;
    }
}
