/*
 * FastDtwTest.java   Jul 14, 2004
 *
 * Copyright (c) 2004 Stan Salvador
 * stansalvador@hotmail.com
 */

package com.fastdtw.examples;

import com.fastdtw.dtw.FastDTW;
import com.fastdtw.timeseries.TimeSeries;
import com.fastdtw.timeseries.TimeSeriesPoint;
import com.fastdtw.util.DistanceFunction;
import com.fastdtw.util.DistanceFunctionFactory;
import com.fastdtw.dtw.TimeWarpInfo;
import java.io.*;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;


/**
 * This class contains a main method that executes the FastDTW algorithm on two
 * time series with a specified radius.
 *
 * @author C. Scott Andreas, s@boundary.com
 * @since May 8, 2012 (adapted from Stan's previous work)
 */
public class DTWQueryTest
{
   /**
    * This main method executes the FastDTW algorithm on two time series with a
    * specified radius. The time series arguments are file names for files that
    * contain one measurement per line (time measurements are an optional value
    * in the first column). After calculating the warp path, the warp
    * path distance will be printed to standard output, followed by the path
    * in the format "(0,0),(1,0),(2,1)..." were each pair of numbers in
    * parenthesis are indexes of the first and second time series that are
    * linked in the warp path
    *
    * @param args  command line arguments (see method comments)
    */
      public static void main(String[] args) throws Exception
      {
         if (args.length!=3 && args.length!=4) {
            System.out.println("USAGE:  java FastDtwTest timeSeries1 timeSeries2 radius [EuclideanDistance|ManhattanDistance|BinaryDistance]");
            System.exit(1);
         } else {
            final DistanceFunction distFn;
            if (args.length < 4)
               distFn = DistanceFunctionFactory.getDistFnByName("EuclideanDistance");
            else
               distFn = DistanceFunctionFactory.getDistFnByName(args[3]);

             ArrayList<Double> datasetValues = readValues(args[0]);
             ArrayList<Double> queryValues = readValues(args[1]);
             System.out.println("Dataset size: " + datasetValues.size() + ". Query size: " + queryValues.size());

            final TimeSeries query = new TimeSeries(1);
             ArrayList<TimeSeriesPoint> queryPoints = new ArrayList<TimeSeriesPoint>(datasetValues.size());
             for (double point : queryValues)
                 queryPoints.add(new TimeSeriesPoint(new double[]{point}));
             query.timeReadings = makeReadings(queryValues.size());
             query.tsArray = queryPoints;

             for (int i=0; i<datasetValues.size() - (queryValues.size()-1); i++) {
                 final TimeSeries dataSet = new TimeSeries(1);
                 ArrayList<TimeSeriesPoint> points = new ArrayList<TimeSeriesPoint>();
                 for (double point : datasetValues.subList(i, i + queryValues.size()))
                     points.add(new TimeSeriesPoint(new double[]{point}));
                 dataSet.timeReadings = makeReadings(queryValues.size());
                 dataSet.tsArray = points;

                 final TimeWarpInfo info = FastDTW.getWarpInfoBetween(dataSet, query, Integer.parseInt(args[2]), distFn);
                 System.out.println("Warp Distance at index " + i + ": " + info.getDistance());
             }

            //System.out.println("Warp Path:     " + info.getPath());
         }  // end if

      }  // end main()

    public static ArrayList<Double> makeReadings(int count) {
        ArrayList<Double> readings = new ArrayList<Double>(count);
        for (int i = 0; i < count; i++) readings.add((double) i);
        return readings;
    }

    public static ArrayList<Double> readValues(String filename) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream(filename))));
        ArrayList<Double> values = new ArrayList<Double>();
        while (true) {
            String value = reader.readLine();
            if (value != null) values.add(Double.parseDouble(value.trim()));
            else return values;
        }
    }

}  // end class FastDtwTest
