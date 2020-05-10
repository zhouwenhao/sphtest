package com.example.sphtest;

import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sphtest.model.AppState;
import com.example.sphtest.model.SaleFieldEntity;
import com.example.sphtest.model.SaleRecordEntity;
import com.example.sphtest.viewmodel.SphViewModel;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.test_load)
    Button mTestLoad;
    @BindView(R.id.test_net_text)
    TextView mTestNetText;
    @BindView(R.id.test_bar_chart)
    BarChart mTestBarChart;

    private List<YearRecord> mRecordList = new ArrayList<>(0);

    private int mNormalColor = Color.parseColor("#aaccaa");
    private int mDownColor = Color.parseColor("#ccaacc");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initBarChart();
        initSubscribe();
        loadData();
    }

    private void initSubscribe() {
        SphViewModel.getInstance().subscribeAppState(this, new Observer<AppState>() {
            @Override
            public void onChanged(AppState appState) {
                mTestNetText.setText("Net State = " + appState.mIsSaleApiOk);
            }
        });
        SphViewModel.getInstance().subscribeSaleField(this, new Observer<List<SaleFieldEntity>>() {
            @Override
            public void onChanged(List<SaleFieldEntity> list) {

            }
        });
        SphViewModel.getInstance().subscribeSaleRecord(this, new Observer<List<SaleRecordEntity>>() {
            @Override
            public void onChanged(List<SaleRecordEntity> list) {
                updateBarChart(list);
            }
        });
    }

    private void loadData() {
        SphViewModel.getInstance().loadSaleData();
    }

    @OnClick(R.id.test_load)
    public void onViewClicked() {
        loadData();
    }

    private void initBarChart(){
        /***图表设置***/
        mTestBarChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                if (mRecordList.get((int)e.getX()).mIsDown){
                    Toast.makeText(MainActivity.this, "今年某季度有下降趋势", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(MainActivity.this, "今年无季度有下降趋势", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected() {

            }
        });
        //背景颜色
        mTestBarChart.setBackgroundColor(Color.WHITE);
        //不显示图表网格
        mTestBarChart.setDrawGridBackground(true);
        //背景阴影
        mTestBarChart.setDrawBarShadow(false);
        mTestBarChart.setHighlightFullBarEnabled(false);
        //显示边框
        mTestBarChart.setDrawBorders(true);
        Matrix m=new Matrix();
        m.postScale(3f,1f);//两个参数分别是x,y轴的缩放比例。例如：将x轴的数据放大为之前的1.5倍
        mTestBarChart.getViewPortHandler().refresh(m,mTestBarChart,false);//将图表动画显示之前进行缩放
        mTestBarChart.animateX(1000);
        /***XY轴的设置***/
        //X轴设置显示位置在底部
        XAxis xAxis = mTestBarChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinimum(0f);
        xAxis.setGranularity(1f);

        YAxis leftAxis = mTestBarChart.getAxisLeft();
        YAxis rightAxis = mTestBarChart.getAxisRight();
        //保证Y轴从0开始，不然会上移一点
        leftAxis.setAxisMinimum(0f);
        rightAxis.setAxisMinimum(0f);

        /***折线图例 标签 设置***/
        Legend legend = mTestBarChart.getLegend();
        legend.setForm(Legend.LegendForm.LINE);
        legend.setTextSize(11f);
        //显示位置
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        //是否绘制在图表里面
        legend.setDrawInside(false);
    }

    private void updateBarChart(List<SaleRecordEntity> list){
        List<YearRecord> newList = calcRecordData(list);
        List<BarEntry> barEntries = new ArrayList<>(0);
        for (int i = 0; i < newList.size(); i++) {
            barEntries.add(new BarEntry(i, newList.get(i).mYearAll));
        }
        BarDataSet dataset = new BarDataSet(barEntries,"销售数据");
        List<Integer> mColors = new ArrayList<>(0);
        for (int i = 0; i < newList.size(); i++){
            if (newList.get(i).mIsDown){
                mColors.add(mDownColor);
            }else {
                mColors.add(mNormalColor);
            }
        }
        dataset.setColors(mColors);
//        final Entity entity = entity;//为了后续将X轴数值设置为汉字做准备
        BarData data = new BarData(dataset);//如果未多组时传入的是X值以及List<BarDataSet>
        data.setValueTextSize(10f);
        data.setBarWidth(0.9f);
        dataset.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return (String.format("%.7f" , value));
            }
        });
        mTestBarChart.setData(data);
        mTestBarChart.getBarData().setBarWidth(1);
        mTestBarChart.getXAxis().setAxisMinimum(0);
        mTestBarChart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                Log.d(TAG, "value22=" + value + "&&year");
                return mRecordList.get((int)value).year;
            }
        });
        mTestBarChart.invalidate();                    //将图表重绘以显示设置的属性和数据
    }

    private List<YearRecord> calcRecordData(List<SaleRecordEntity> list){
        mRecordList = new ArrayList<>(0);
        if (list != null){
            YearRecord yearRecord = new YearRecord();
            boolean isDown = false;
            String year = "";
            float all = 0f;

            float lastRecord = 0f;
            for (SaleRecordEntity item : list){
                String itemYear = item.getQuarter().split("-")[0];
                if (!itemYear.equals(year) && !TextUtils.isEmpty(year)){
                    yearRecord.mIsDown = isDown;
                    yearRecord.mYearAll = all;
                    yearRecord.year = year;
                    mRecordList.add(yearRecord);
                    yearRecord = new YearRecord();
                    year = "";
                    all = 0f;
                    isDown = false;
                }
                year = itemYear;
                float curValume = Float.parseFloat(item.getVolume_of_mobile_data());
                all += Float.parseFloat(item.getVolume_of_mobile_data());
                if (lastRecord > curValume){
                    isDown = true;
                }
                lastRecord = curValume;
            }
            if (list.size() > 0) {
                yearRecord.mIsDown = isDown;
                yearRecord.mYearAll = all;
                yearRecord.year = year;
                mRecordList.add(yearRecord);
            }
        }
        return mRecordList;
    }


    public static class YearRecord{
        public float mYearAll = 0f;
        public boolean mIsDown = false;
        public String year = "";
    }
}
