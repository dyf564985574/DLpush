
package com.toc.dlpush.mpchartview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.LineData;
import com.toc.dlpush.R;

import java.util.List;

public class LineChartItem extends ChartItem {

    private Typeface mTf;

    public LineChartItem(ChartData<?> cd, Context c,String notice_name ,int num , int count ) {
        super(cd , notice_name , num, count);
//        mTf = Typeface.createFromAsset(c.getAssets(), "OpenSans-Regular.ttf");
    }

    @Override
    public int getItemType() {
        return TYPE_LINECHART;
    }

    @Override
    public View getView(int position, View convertView, Context c) {
        Log.i("position",position+"");
        ViewHolder holder = null;

        if (convertView == null) {

            holder = new ViewHolder();

            convertView = LayoutInflater.from(c).inflate(
                    R.layout.list_item_linechart, null);
            holder.chart = (LineChart) convertView.findViewById(R.id.chart);
            holder.num = (TextView) convertView.findViewById(R.id.notice_num);
            holder.type = (TextView) convertView.findViewById(R.id.notice_type);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        // apply styling
        // holder.chart.setValueTypeface(mTf);
        holder.type.setText(notice_name);
        holder.num.setText(num+"");
        holder.chart.setDescription("");
        holder.chart.setDrawGridBackground(false);
        holder.chart.setTouchEnabled(false);// 设置是否可以触摸
        holder.chart.setDragEnabled(false);// 是否可以拖拽
        holder.chart.setScaleEnabled(false);// 是否可以缩放
        holder.chart.getAxisLeft().setEnabled(false);
        holder.chart.getAxisRight().setEnabled(false);
        XAxis xAxis = holder.chart.getXAxis();
        xAxis.setPosition(XAxisPosition.BOTTOM);
        xAxis.setTypeface(mTf);
        xAxis.setAxisLineColor(Color.WHITE);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true);
        xAxis.SetCount(count);
        xAxis.setSpaceBetweenLabels(6);
        YAxis leftAxis = holder.chart.getAxisLeft();
        leftAxis.setTypeface(mTf);
        leftAxis.setLabelCount(5);
        YAxis rightAxis = holder.chart.getAxisRight();
        rightAxis.setTypeface(mTf);
        rightAxis.setLabelCount(5);
        rightAxis.setDrawGridLines(false);
        Legend l =holder.chart.getLegend();
        l.setEnabled(false);
        // set data
        holder.chart.setData((LineData) mChartData);


        // do not forget to refresh the chart
        holder.chart.invalidate();
        //未0时禁止动画
        holder.chart.animateX(0);

        return convertView;
    }

    private static class ViewHolder {
        LineChart chart;
        TextView num;
        TextView type;
    }
}
