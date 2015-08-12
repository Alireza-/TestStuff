/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.csiro.teststuff;

import java.util.Date;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient;
import com.amazonaws.services.cloudwatch.model.Datapoint;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsRequest;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsResult;
import java.io.File;
import java.io.IOException;
import jxl.Sheet;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.Workbook;
import jxl.write.DateTime;
import jxl.write.Number;

/**
 *
 * @author kho01f
 */
public class CloudWatchStats {

    static final String awsAccessKey = "AKIAJHHVZSXZOUJHVJOA";
    static final String awsSecretKey = "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";
    static final String instanceId = "i-b4318842";
    static final String cacheClusterId = "dac-re-vr2iyotponxc";
    static final String StreamName = "dac-KinesisStream-4BT8AI7ZUDLT";

    public static String[] nameSpaces = new String[]{"AWS/EC2", "AWS/ElastiCache", "AWS/Kinesis"};
    public static String[] elastiCacheMetrics = new String[]{"CPUUtilization", "FreeableMemory", "NetworkBytesIn", "NetworkBytesOut"};
    public static String[] ec2Metrics = new String[]{"CPUUtilization", "NetworkIn", "NetworkOut"};

    public static String[] kinesisMetrics = new String[]{"PutRecord.Bytes", "PutRecord.Latency", "PutRecord.Success", "PutRecords.Bytes",
        "PutRecords.Latency", "PutRecords.Records", "PutRecords.Success", "IncomingBytes", "IncomingRecords", "GetRecords.Bytes", "GetRecords.Latency", "GetRecords.Success"};
    public static String[] stats = new String[]{"TimeStamp", "Average", "Maximum", "Minimum", "Sum"};
    public static String filePath = "C:\\Users\\kho01f\\Desktop\\Second Contribution\\Kinesis_2nd.xls";

    public static void main(String[] args) throws BiffException, IOException, WriteException {
        final AmazonCloudWatchClient client = client(awsAccessKey, awsSecretKey);
        final GetMetricStatisticsRequest request = request(instanceId, nameSpaces[2], kinesisMetrics[12]);
        final GetMetricStatisticsResult result = result(client, request);
        toStdOut(result, instanceId);
        writeToExcel(filePath, result, 72, kinesisMetrics[12]);
    }

    private static AmazonCloudWatchClient client(final String awsAccessKey, final String awsSecretKey) {
        final AmazonCloudWatchClient client = new AmazonCloudWatchClient(new BasicAWSCredentials(awsAccessKey, awsSecretKey));
        client.setEndpoint("http://monitoring.us-west-2.amazonaws.com");
        return client;
    }

    private static GetMetricStatisticsRequest request(final String instanceId, String nameSpace, String metricName) {
        final long twentyFourHrs = 1000 * 60 * 60 * 24;
        //final int twoMins= 1000*3000;
        //final int oneHour = 60 * 60;
        final int oneMin = 60;
        return new GetMetricStatisticsRequest()
                .withStartTime(new Date(new Date().getTime() - twentyFourHrs)) //- twentyFourHrs))
                .withNamespace(nameSpace)
                .withPeriod(oneMin)
                //.withDimensions(new Dimension().withName("InstanceId").withValue(instanceId)) // EC2
                //.withDimensions(new Dimension().withName("CacheClusterId").withValue(cacheClusterId)) //cache
                .withDimensions(new Dimension().withName("StreamName").withValue(StreamName)) //kinesis
                .withMetricName(metricName)
                .withStatistics("Average", "Maximum", "Minimum", "Sum")
                .withEndTime(new Date(new Date().getTime()));
    }

    private static GetMetricStatisticsResult result(
            final AmazonCloudWatchClient client, final GetMetricStatisticsRequest request) {
        return client.getMetricStatistics(request);
    }

    public static void writeToExcel(String fileName, GetMetricStatisticsResult result, int indexI, String metricName)
            throws BiffException, IOException, WriteException {

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

        wsheet.addCell(new Label(indexI + 2, 0, instanceId));
        wsheet.addCell(new Label(indexI + 2, 1, metricName));

        int j = 0;
        for (int i = indexI; i < stats.length + indexI; i++) {
            wsheet.addCell(new Label(i, 2, stats[j]));
            j++;
        }

        int indexJ = 3;
        for (Datapoint dataPoint : result.getDatapoints()) {
            wsheet.addCell(new DateTime(indexI, indexJ, dataPoint.getTimestamp()));
            wsheet.addCell(new Number(indexI + 1, indexJ, dataPoint.getAverage()));
            wsheet.addCell(new Number(indexI + 2, indexJ, dataPoint.getMaximum()));
            wsheet.addCell(new Number(indexI + 3, indexJ, dataPoint.getMinimum()));
            wsheet.addCell(new Number(indexI + 4, indexJ, dataPoint.getSum()));
            indexJ++;
        }
        wworkbook.write();
        wworkbook.close();
    }

    private static void toStdOut(final GetMetricStatisticsResult result, final String instanceId) {
        System.out.println(result); // outputs empty result: {Label: CPUUtilization,Datapoints: []}
        for (final Datapoint dataPoint : result.getDatapoints()) {
            System.out.printf("%s instance's TimeStamp CPU utilization : %s%n", instanceId, dataPoint.getTimestamp());
            System.out.printf("%s instance's average CPU utilization : %s%n", instanceId, dataPoint.getAverage());
            System.out.printf("%s instance's max CPU utilization : %s%n", instanceId, dataPoint.getMaximum());
            System.out.printf("%s instance's min CPU utilization : %s%n", instanceId, dataPoint.getMinimum());
            System.out.printf("%s instance's Sum CPU utilization : %s%n", instanceId, dataPoint.getSum());

        }
    }
}
