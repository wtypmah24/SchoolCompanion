package org.back.beobachtungapp.service;

import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
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

@Service
@RequiredArgsConstructor
public class ChartService {

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

  public BufferedImage generateLineChart(List<MonitoringEntryResponseDto> entries) {
    if (entries.isEmpty()) return null;
    TimeSeries series = new TimeSeries(entries.get(0).parameterName());

    for (MonitoringEntryResponseDto entry : entries) {
      try {
        double value = Double.parseDouble(entry.value()); // Ожидается числовое значение
        series.addOrUpdate(new Millisecond(java.util.Date.from(entry.createdAt())), value);
      } catch (NumberFormatException ignored) {
        // Пропускаем записи с некорректным значением
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

    // Настройка оси времени
    DateAxis axis = (DateAxis) chart.getXYPlot().getDomainAxis();
    axis.setDateFormatOverride(new SimpleDateFormat("dd-MM-yyyy HH:mm"));

    // Настройка линий и точек
    XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);
    chart.getXYPlot().setRenderer(renderer);

    return chart.createBufferedImage(800, 600);
  }

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
