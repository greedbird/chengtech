package com.chengtech.chengtechmt.hellochart;

import android.app.Fragment;
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
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;

/**
 * 作者: LiuFuYingWang on 2016/9/8 14:44.
 */
public class LineChartFragment extends Fragment {

    private LineChartView chartView;
    private LineChartData chartData;
    private String[][] axisXLable ;
    private List<AxisValue> axisValues = new ArrayList<>();
    private int[][] data ;

    private boolean hasAxes = true;
    private boolean hasAxesNames = false;
    private boolean hasLines = true;
    private boolean hasPoints = true;
    private ValueShape shape = ValueShape.CIRCLE;
    private boolean isFilled = false;
    private boolean hasLabels = true;
    private boolean isCubic = false;
    private boolean hasLabelForSelected = false;
    private boolean pointsHaveDifferentColor;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_line_chart, container, false);
        chartView = (LineChartView) rootView.findViewById(R.id.chart);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        data = (int[][]) bundle.get("lineData");
        axisXLable = (String[][]) bundle.get("lineLable");
        generateLineChart();
    }

    private void generateLineChart() {

        getAxisLables();//获取x轴的标注
        initLineChart();//初始化

    }

    private void initLineChart() {

        List<Line> lines = new ArrayList<>();
        for (int i=0;i<data.length;i++) {
           List<PointValue> pointValues = new ArrayList<>();
            for (int j = 0; j < data[i].length; j++) {
                pointValues.add(new PointValue(j, data[i][j]));
            }

            Line line = new Line(pointValues);
            line.setColor(ChartUtils.pickColor());
            line.setCubic(isCubic);
            line.setHasLabels(hasLabels);
            line.setFilled(isFilled);
            line.setShape(ValueShape.CIRCLE);
            line.setHasLines(hasLines);
            line.setHasPoints(hasPoints);
            lines.add(line);
        }

        chartData = new LineChartData(lines);


        //坐标轴
        Axis axisX = new Axis();
        axisX.setValues(axisValues);

        Axis axisY = new Axis().setHasLines(hasLines);
        axisY.setMaxLabelChars(3);

        if (hasAxes) {
            if (hasAxesNames) {
                axisX.setName("x轴");
                axisY.setName("Y轴");
            }
            chartData.setAxisYLeft(axisY);
            chartData.setAxisXBottom(axisX);
        }else {
            chartData.setAxisYLeft(null);
            chartData.setAxisXBottom(null);
        }
        chartView.setZoomEnabled(false);
        chartView.setLineChartData(chartData);

    }


    private void getAxisLables() {
        if (axisXLable != null && axisXLable.length > 0) {
            for (int i = 0; i < axisXLable[0].length; i++) {
                axisValues.add(new AxisValue(i).setLabel(axisXLable[0][i]));
            }
        }
    }
}
