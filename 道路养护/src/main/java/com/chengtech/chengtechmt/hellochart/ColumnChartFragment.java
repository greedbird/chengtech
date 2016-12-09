package com.chengtech.chengtechmt.hellochart;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chengtech.chengtechmt.R;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;

/**
 * 作者: LiuFuYingWang on 2016/9/7 16:56.
 */
public class ColumnChartFragment extends Fragment {

    private ColumnChartView columnChartView;
    private ColumnChartData chartData;
    private boolean hasAxes = true;
    private boolean hasAxesNames = false;
    private boolean hasLabels = true;
    private boolean hasLabelForSelected = false;
    private int maxColumnNum = 12;
    private int maxSubColumnNum = 8;
    private int[][] data;
    private String[][] axisXLable;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_column_chart, container, false);
        columnChartView = (ColumnChartView) rootView.findViewById(R.id.chart);
//        columnChartView.setOnValueTouchListener(new va);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        data = (int[][]) bundle.get("columnData");
        axisXLable = (String[][]) bundle.get("columnLable");

        if (data!=null && axisXLable!=null) {
            generateColunmnChart();
        }
    }

    private void generateColunmnChart() {
        List<Column> columnList = new ArrayList<>();
        List<SubcolumnValue> values;
        for (int i = 0; i < data.length; i++) {

            values = new ArrayList<>();
            for (int j = 0; j < data[i].length; j++) {
                values.add(new SubcolumnValue(data[i][j], Color.parseColor("#33B5E5")));
            }

            Column column = new Column(values);
            column.setHasLabels(hasLabels);
            column.setHasLabelsOnlyForSelected(hasLabelForSelected);
            columnList.add(column);
        }

        chartData = new ColumnChartData(columnList);
        if (hasAxes) {
            Axis axisX = new Axis();
            List<AxisValue> axisValueList = new ArrayList<>();
            for (int i = 0; i < data.length; i++) {
                AxisValue axisValue = new AxisValue(i);
                axisValue.setValue(i);
                axisValue.setLabel(axisXLable[i][0]);
                axisValueList.add(axisValue);
            }
            axisX.setValues(axisValueList);

            Axis axisY = new Axis().setHasLines(true);
            axisY.setMaxLabelChars(5);
//            List<AxisValue> axisValueY = new ArrayList<>();
//            for (float i = 0f; i < 100f; i += 0.5f) {
//                AxisValue axisValue = new AxisValue(i);
//                axisValue.setLabel(i + "");
//                axisValueY.add(axisValue);
//            }
//            axisY.setValues(axisValueY);
            if (hasAxesNames) {
                axisX.setName("Axis X");
                axisY.setName("Axis Y");
            }
            chartData.setAxisXBottom(axisX);
            chartData.setAxisYLeft(axisY);
        } else {
            chartData.setAxisXBottom(null);
            chartData.setAxisYLeft(null);
        }

        columnChartView.setColumnChartData(chartData);

    }
}
