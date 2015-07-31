package com.toc.dlpush.mpchartview;

import android.content.Context;
import android.view.View;

import com.github.mikephil.charting.data.ChartData;

import java.util.List;

/**
 * baseclass of the chart-listview items
 * @author philipp
 *
 */
public abstract class ChartItem {
    
    protected static final int TYPE_BARCHART = 0;
    protected static final int TYPE_LINECHART = 1;
    protected static final int TYPE_PIECHART = 2;
    
    protected ChartData<?> mChartData;
    protected String notice_name;
    protected int num;
    protected int count;
    public ChartItem(ChartData<?> cd,String notice_name , int num ,int count ) {
        this.mChartData = cd;
        this.notice_name = notice_name;
        this.num = num;
        this.count = count;
    }
    
    public abstract int getItemType();
    
    public abstract View getView(int position, View convertView, Context c);
}
