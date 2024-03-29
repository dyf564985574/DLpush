/**
 * Copyright 2014  XCL-Charts
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 	
 * @Project XCL-Charts 
 * @Description Android图表基类库
 * @author XiongChuanLiang<br/>(xcl_168@aliyun.com)
 * @Copyright Copyright (c) 2014 XCL-Charts (www.xclcharts.com)
 * @license http://www.apache.org/licenses/  Apache v2 License
 * @version 1.0
 */
package com.toc.dlpush.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.toc.mylibrary.org.xclcharts.chart.PieChart;
import com.toc.mylibrary.org.xclcharts.chart.PieData;
import com.toc.mylibrary.org.xclcharts.common.DensityUtil;
import com.toc.mylibrary.org.xclcharts.event.click.ArcPosition;
import com.toc.mylibrary.org.xclcharts.renderer.XChart;
import com.toc.mylibrary.org.xclcharts.renderer.XEnum;
import com.toc.mylibrary.org.xclcharts.renderer.plot.PlotLegend;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName PieChart01View
 * @Description  饼图的例子
 * @author XiongChuanLiang<br/>(xcl_168@aliyun.com)
 */
public class PieChart01View extends DemoView implements Runnable{
	
	private String TAG = "PieChart01View";
	private PieChart chart = new PieChart();
	private ArrayList<PieData> chartData = new ArrayList<PieData>();
	private int mSelectedID = -1;
	

	public PieChart01View(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		initView();
	}
	
	public PieChart01View(Context context, AttributeSet attrs){   
        	super(context, attrs);   
        	initView();
	 }
	 
	 public PieChart01View(Context context, AttributeSet attrs, int defStyle) {
			super(context, attrs, defStyle);
			initView();
	 }
	 
	 private void initView()
	 {
		 	chartDataSet();	
			chartRender();
			new Thread(this).start();
	 }
	 	 	
	@Override  
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {  
        super.onSizeChanged(w, h, oldw, oldh);  
       //图所占范围大小
        chart.setChartRange(w,h);
    }  	
	
	
	private void chartRender()
	{
		try {					
			
			//设置绘图区默认缩进px值
			int [] ltrb = getPieDefaultSpadding();
			float right = DensityUtil.dip2px(getContext(), 100);
			chart.setPadding(ltrb[0], ltrb[1], right, ltrb[3]);			
			
			//设置起始偏移角度(即第一个扇区从哪个角度开始绘制)
			//chart.setInitialAngle(90);	
			
			//标签显示(隐藏，显示在中间，显示在扇区外面)
			chart.setLabelStyle(XEnum.SliceLabelStyle.BROKENLINE);
			chart.getLabelPaint().setColor(Color.WHITE);
			
			//标题
			chart.setTitle("饼图-Pie Chart");
			chart.addSubtitle("(XCL-Charts Demo)");
			chart.setTitleVerticalAlign(XEnum.VerticalAlign.BOTTOM);
								
			//chart.setDataSource(chartData);
			
			//激活点击监听
//			chart.ActiveListenItemClick();
//			chart.showClikedFocus();
			
			//设置允许的平移模式
			//chart.enablePanMode();
			//chart.setPlotPanMode(XEnum.PanMode.HORIZONTAL);	
			
			//显示图例
			PlotLegend legend = chart.getPlotLegend();
			legend.show();
			legend.setType(XEnum.LegendType.COLUMN);
			legend.setHorizontalAlign(XEnum.HorizontalAlign.RIGHT);
			legend.setVerticalAlign(XEnum.VerticalAlign.MIDDLE);
			legend.showBox();
            chart.disablePanMode();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e(TAG, e.toString());
		}
	}
	
	private void chartDataSet()
	{
		/*
		//设置图表数据源		
		chartData.add(new PieData("HP","20%",20,(int)Color.rgb(155, 187, 90)));
		chartData.add(new PieData("IBM","30%",30,(int)Color.rgb(191, 79, 75),false));
		chartData.add(new PieData("DELL","10%",10,(int)Color.rgb(242, 167, 69)));
		//将此比例块突出显示
		chartData.add(new PieData("EMC","40%",40,(int)Color.rgb(60, 173, 213),false));
		*/

		chartData.add(new PieData("closed","芝麻：9%" ,   9,Color.rgb(155, 187, 90)));
		chartData.add(new PieData("inspect","3%" ,   3,Color.rgb(191, 79, 75)));
		chartData.add(new PieData("open","76%" ,  76f,Color.rgb(242, 167, 69)));
		chartData.add(new PieData("workdone","6%" , 6,Color.rgb(60, 173, 213)));
		chartData.add(new PieData("dispute","6%" ,  6,Color.rgb(90, 79, 88)));
	
	}

	@Override
    public void render(Canvas canvas) {
        try{
            chart.render(canvas);
        } catch (Exception e){
        	Log.e(TAG, e.toString());
        }
    }

	@Override
	public List<XChart> bindChart() {
		// TODO Auto-generated method stub		
		List<XChart> lst = new ArrayList<XChart>();
		lst.add(chart);		
		return lst;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {          
         	chartAnimation();         	
         }
         catch(Exception e) {
             Thread.currentThread().interrupt();
         }  
	}
	
	private void chartAnimation()
	{
		  try {       
			 
			    chart.setDataSource(chartData);
			  	int count = 360 / 10;
			  	
	          	for(int i=1;i<count ;i++)
	          	{
	          		Thread.sleep(40);
	          		
	          		chart.setTotalAngle(10 * i);
	          		
	          		//激活点击监听
	    			if(count - 1 == i)
	    			{
	    				chart.setTotalAngle(360);
	    				
	    				chart.ActiveListenItemClick();
	    				//显示边框线，并设置其颜色
	    				chart.getArcBorderPaint().setColor(Color.YELLOW);
	    				chart.getArcBorderPaint().setStrokeWidth(3);
	    			}
	    			
	          		postInvalidate();            				          	          	
	          }
			  
          }
          catch(Exception e) {
              Thread.currentThread().interrupt();
          }       
		  
	}
	
	/*
	 * 另一种动画
	private void chartAnimation()
	{
		  try {       
			 
			  	float sum = 0.0f;
			  	int count = chartData.size();
	          	for(int i=0;i< count ;i++)
	          	{
	          		Thread.sleep(150);
	          	
	          		ArrayList<PieData> animationData = new ArrayList<PieData>();
	        
	          		sum = 0.0f;
	          			          		
	          		for(int j=0;j<=i;j++)
	          		{            			            			
	          			animationData.add(chartData.get(j));
	          			sum = (float) MathHelper.getInstance().add(
	          									sum , chartData.get(j).getPercentage());	          			
	          		}   		          		
	          			          			          				          				          	
	          		animationData.add(new PieData("","",  MathHelper.getInstance().sub(100.0f , sum),
	          											  Color.argb(1, 0, 0, 0)));		          		
	          		chart.setDataSource(animationData);
	          	
	          		//激活点击监听
	    			if(count - 1 == i)
	    			{
	    				chart.ActiveListenItemClick();
	    				//显示边框线，并设置其颜色
	    				chart.getArcBorderPaint().setColor(Color.YELLOW);
	    				chart.getArcBorderPaint().setStrokeWidth(3);
	    			}
	    			
	          		postInvalidate();            				          	          	
	          }
			  
          }
          catch(Exception e) {
              Thread.currentThread().interrupt();
          }       
		  
	}
	*/
		
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub		
		super.onTouchEvent(event);		
		if(event.getAction() == MotionEvent.ACTION_UP) 
		{						
			if(chart.isPlotClickArea(event.getX(),event.getY()))
			{											
				triggerClick(event.getX(),event.getY());					
			}
		}
		return true;
	}
	

	//触发监听
	private void triggerClick(float x,float y)
	{	
		if(!chart.getListenItemClickStatus())return;
				
		ArcPosition record = chart.getPositionRecord(x,y);
		if( null == record) return;
		/*
		PieData pData = chartData.get(record.getDataID());		
		Toast.makeText(this.getContext(),								
				" key:" +  pData.getKey() +
				" Label:" + pData.getLabel() ,
				Toast.LENGTH_SHORT).show(); 
		*/
		
		//用于处理点击时弹开，再点时弹回的效果
		PieData pData = chartData.get(record.getDataID());
		if(record.getDataID() == mSelectedID ) 
		{
			boolean bStatus = chartData.get(mSelectedID).getSelected();
			chartData.get(mSelectedID).setSelected(!bStatus);
		}else{
			if(mSelectedID >= 0)
				chartData.get(mSelectedID).setSelected(false);
			pData.setSelected(true);
		}
		mSelectedID = record.getDataID();
		this.refreshChart();		
				
		/*
		boolean isInvaldate = true;		
		for(int i=0;i < chartData.size();i++)
		{	
			PieData cData = chartData.get(i);
			if(i == record.getDataID())
			{
				if(cData.getSelected()) 
				{
					isInvaldate = false;
					break;
				}else{
					cData.setSelected(true);	
				}
			}else
				cData.setSelected(false);			
		}
		if(isInvaldate)this.invalidate();
		*/
		
	}
	
	
}
