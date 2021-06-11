package org.example;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.chart.ChartUtils;

import javax.swing.JFrame;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * The class is dedicated to creating, rendering and saving the evaluation charts
 */

public class EvaluationChart extends JFrame {

    private  XYSeriesCollection dataset;
    private String title;
    private String xLabel;
    private String yLabel;
    private JFreeChart chart;

    /**
     * Constructor
     * @param title the title of the chart
     * @param xLabel the label for X axis
     * @param yLabel the label for Y axis
     */
    public EvaluationChart(String title, String xLabel, String yLabel) {
        dataset = new XYSeriesCollection();
        this.title = title;
        this.xLabel = xLabel;
        this.yLabel = yLabel;

    }

    /**
     * Old method, refers to the new one
     */
    public void initChart() {

        chart = createChart();

//        ChartPanel chartPanel = new ChartPanel(chart);
//        chartPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
//        chartPanel.setBackground(Color.white);
//        add(chartPanel);
//
//        pack();
//        setTitle(title);
//        setLocationRelativeTo(null);
//        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        setVisible(true);
    }

    /**
     * Saves the chart as png
     * @param folder folder to save to
     * @param filename name of the chart file
     * @throws IllegalAccessException if something went wrong with the I/O
     */
    public void saveAsPng(String folder, String filename) throws IllegalAccessException {
        if(chart == null){
            throw new IllegalAccessException("Chart has not been initialized.");
        }
        File chartFile = new File(folder);
        boolean dirCreated = chartFile.mkdirs();
        chartFile = new File(folder + filename + ".png");
        try {
            ChartUtils.saveChartAsPNG(chartFile, chart, 1500, 900);
        } catch (IOException e) {
            System.out.println("Could not save chart: " + e.toString());
        }
    }

    /**
     * Adds series to the current dataset
     * @param xyData the xy points for the chart in a Map
     * @param seriesKey name of the series
     */
    public void addSeries(Map<Integer, Double> xyData, String seriesKey) {

        var series = new XYSeries(seriesKey);
        for (Map.Entry<Integer, Double> entry: xyData.entrySet()){
            series.add(entry.getKey(), entry.getValue());
        }
        dataset.addSeries(series);

    }

    /**
     * Creates the chart object in order to save it as PNG
     * @return JFreeChart object
     */
    private JFreeChart createChart() {

//        JFreeChart chart = ChartFactory.createXYLineChart(
//                title,
//                xLabel,
//                yLabel,
//                dataset,
//                PlotOrientation.VERTICAL,
//                true,
//                true,
//                false
//        );

//        XYPlot plot = chart.getXYPlot();
        LogarithmicAxis xAxis = new LogarithmicAxis(xLabel);
        xAxis.setAllowNegativesFlag(true);
        LogarithmicAxis yAxis = new LogarithmicAxis(yLabel);
        yAxis.setAllowNegativesFlag(true);

        var renderer = new XYLineAndShapeRenderer();
//        renderer.setSeriesPaint(0, Color.RED);
        renderer.setSeriesStroke(0, new BasicStroke(2.0f));

        XYPlot plot = new XYPlot(dataset,
                xAxis, yAxis, new XYLineAndShapeRenderer(true, false));

        plot.setRenderer(renderer);
        plot.setBackgroundPaint(Color.white);

        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.BLACK);

        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.BLACK);

        JFreeChart chart = new JFreeChart(
                title, new Font("Serif", java.awt.Font.BOLD, 18), plot, true);
        chart.getLegend().setFrame(BlockBorder.NONE);

//        chart.setTitle(new TextTitle(title,
//                        new Font("Serif", java.awt.Font.BOLD, 18)
//                )
//        );

        return chart;
    }

    public static void main(String[] args) {

        EventQueue.invokeLater(() -> {

            var ex = new EvaluationChart("Test", "X", "Y");
            ex.initChart();
//            ex.setVisible(true);
        });
    }
}