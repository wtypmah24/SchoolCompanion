package org.back.beobachtungapp.service;

import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.back.beobachtungapp.dto.response.monitoring.MonitoringEntryResponseDto;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.springframework.stereotype.Service;

/**
 * Service responsible for generating charts based on monitoring entries. Supports different types
 * of charts for binary, quantitative, and scale data.
 *
 * <p>Uses JFreeChart to generate line charts and bar charts as BufferedImage objects.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChartService {

  /**
   * Processes a set of monitoring entries and generates a list of charts (as BufferedImages)
   * corresponding to the entry types: - Line chart for quantitative data - Bar chart for binary
   * data - Bar chart for scale data
   *
   * @param entries the set of monitoring entries to process
   * @return a list of BufferedImages representing the generated charts
   */
  public List<BufferedImage> handleCharts(Set<MonitoringEntryResponseDto> entries) {
    List<MonitoringEntryResponseDto> binaryEntries = new ArrayList<>();
    List<MonitoringEntryResponseDto> quantitativeEntries = new ArrayList<>();
    List<MonitoringEntryResponseDto> scaleEntries = new ArrayList<>();

    entries.forEach(
        e -> {
          switch (e.type()) {
            case "BINARY":
              binaryEntries.add(e);
              break;
            case "QUANTITATIVE":
              quantitativeEntries.add(e);
              break;
            case "SCALE":
              scaleEntries.add(e);
              break;
          }
        });
    List<BufferedImage> charts = new ArrayList<>();
    if (!quantitativeEntries.isEmpty()) {
      charts.add(generateLineChart(quantitativeEntries));
    }
    if (!binaryEntries.isEmpty()) {
      charts.add(generateBinaryChart(binaryEntries));
    }
    if (!scaleEntries.isEmpty()) {
      charts.add(generateScaleChart(scaleEntries));
    }
    return charts;
  }

  /**
   * Generates a time series line chart from quantitative monitoring entries. Each entry's timestamp
   * and numeric value are plotted. Entries with non-numeric values are logged and skipped.
   *
   * @param entries list of quantitative monitoring entries
   * @return a BufferedImage containing the line chart, or null if entries are empty
   */
  public BufferedImage generateLineChart(List<MonitoringEntryResponseDto> entries) {
    if (entries.isEmpty()) return null;
    TimeSeries series = new TimeSeries(entries.get(0).parameterName());

    for (MonitoringEntryResponseDto entry : entries) {
      try {
        double value = Double.parseDouble(entry.value()); // Ожидается числовое значение
        series.addOrUpdate(new Millisecond(java.util.Date.from(entry.createdAt())), value);
      } catch (NumberFormatException ignored) {
        log.warn("{} is not a number", entry.value());
      }
    }

    TimeSeriesCollection dataset = new TimeSeriesCollection();
    dataset.addSeries(series);

    JFreeChart chart =
        ChartFactory.createTimeSeriesChart(
            entries.get(0).parameterName() + " (quant.)",
            "Time",
            "Value",
            dataset,
            false,
            false,
            false);

    // Setting the time axis
    DateAxis axis = (DateAxis) chart.getXYPlot().getDomainAxis();
    axis.setDateFormatOverride(new SimpleDateFormat("dd-MM-yyyy HH:mm"));

    // Setting up lines and points
    XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);
    chart.getXYPlot().setRenderer(renderer);

    return chart.createBufferedImage(800, 600);
  }

  /**
   * Generates a bar chart representing the count of different states in binary entries.
   *
   * @param entries list of binary monitoring entries
   * @return a BufferedImage containing the binary bar chart
   */
  public BufferedImage generateBinaryChart(List<MonitoringEntryResponseDto> entries) {
    Map<String, Long> counts =
        entries.stream()
            .collect(
                Collectors.groupingBy(MonitoringEntryResponseDto::value, Collectors.counting()));

    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    counts.forEach((key, count) -> dataset.addValue(count, "State", key));

    JFreeChart chart =
        ChartFactory.createBarChart(
            entries.get(0).parameterName() + " (binary)", "State", "Quantity", dataset);

    return chart.createBufferedImage(800, 600);
  }

  /**
   * Generates a bar chart representing the count of different notes/values in scale entries.
   *
   * @param entries list of scale monitoring entries
   * @return a BufferedImage containing the scale bar chart
   */
  public BufferedImage generateScaleChart(List<MonitoringEntryResponseDto> entries) {
    Map<String, Long> counts =
        entries.stream()
            .collect(
                Collectors.groupingBy(MonitoringEntryResponseDto::value, Collectors.counting()));

    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    counts.forEach((key, count) -> dataset.addValue(count, "Note", key));

    JFreeChart chart =
        ChartFactory.createBarChart(
            entries.get(0).parameterName() + " (scale)", "Note", "Quantity", dataset);

    return chart.createBufferedImage(800, 600);
  }
}
