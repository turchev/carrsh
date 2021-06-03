package com.github.turchev.views.mechanic;

import com.github.appreciated.apexcharts.ApexChartsBuilder;
import com.github.appreciated.apexcharts.config.builder.*;
import com.github.appreciated.apexcharts.config.chart.Type;
import com.github.appreciated.apexcharts.config.plotoptions.bar.builder.ColorsBuilder;
import com.github.appreciated.apexcharts.config.plotoptions.builder.BarBuilder;
import com.github.appreciated.apexcharts.helper.Series;
import com.github.turchev.data.entity.Mechanic;
import com.github.turchev.data.service.MechanicService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

class MechanicDialogStat extends Dialog {
    private static final Logger LOG = LogManager.getLogger();
    private final MechanicService mechanicService;

    protected MechanicDialogStat(MechanicService mechanicService) {

        this.mechanicService = mechanicService;
        VerticalLayout vlLayout = new VerticalLayout(getChart());
        vlLayout.setSizeFull();
        this.add(vlLayout);
        setSizeFull();

        LOG.debug("Created MechanicWindowStat");
    }

    protected Component getChart() {
        /*
          		https://github.com/appreciated/apexcharts-flow
         */
        ApexChartsBuilder barChartOrderSum = new ApexChartsBuilder();
        ApexChartsBuilder barChartPriceSum = new ApexChartsBuilder();

        List<Mechanic> mechanic = mechanicService.findAll();
        LOG.debug(mechanic);
        List<String> fio = new ArrayList<>();
        List<Double> dataOrdersSum = new ArrayList<>();
        List<Double> dataPriceSum = new ArrayList<>();
        for (Mechanic itrMechanicStat : mechanic) {
            fio.add(itrMechanicStat.toString());
            dataOrdersSum.add(itrMechanicStat.getOrderSum().doubleValue());
            dataPriceSum.add(itrMechanicStat.getPriceSum().doubleValue());
        }

        barChartOrderSum.withChart(ChartBuilder.get()
                .withType(Type.bar)
                .build())
                .withPlotOptions(PlotOptionsBuilder.get()
                        .withBar(BarBuilder.get()
                                .withHorizontal(true)
                                .withColors(ColorsBuilder.get()
                                        .build())
                                .build())
                        .build())
                .withDataLabels(DataLabelsBuilder.get()
                        .withEnabled(false).build())
                .withStroke(StrokeBuilder.get()
                        .withShow(true)
                        .withColors("transparent")
                        .build())
                .withSeries(new Series<>("Количество заказаов", dataOrdersSum.toArray()))
                .withXaxis(XAxisBuilder.get().withCategories(fio).build())
                .withFill(FillBuilder.get().build());
        VerticalLayout chart1 = new VerticalLayout(barChartOrderSum.build());
        chart1.setWidth("50em");

        barChartPriceSum.withChart(ChartBuilder.get()
                .withType(Type.bar)
                .build())
                .withPlotOptions(PlotOptionsBuilder.get()
                        .withBar(BarBuilder.get()
                                .withHorizontal(true)
                                .withColors(ColorsBuilder.get()
                                        .build())
                                .build())
                        .build())
                .withDataLabels(DataLabelsBuilder.get()
                        .withEnabled(false)
                        .build())
                .withStroke(StrokeBuilder.get()
                        .withShow(true)
                        .withColors("transparent")
                        .build())
                .withSeries(new Series<>("Стоимость", dataPriceSum.toArray()))
                .withXaxis(XAxisBuilder.get().withCategories(fio).build())
                .withFill(FillBuilder.get()
                        .build());
        VerticalLayout chart2 = new VerticalLayout(barChartPriceSum.build());
        chart2.setWidth("50em");

        return new HorizontalLayout(chart1, chart2);
    }
}


