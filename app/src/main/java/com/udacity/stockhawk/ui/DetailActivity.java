package com.udacity.stockhawk.ui;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {
    @BindView(R.id.chart)
    LineChart chartView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String stockSymbol = getIntent().getStringExtra("symbol");

        String[] projection = {Contract.Quote.COLUMN_SYMBOL, Contract.Quote.COLUMN_HISTORY};

        Cursor cursor = getContentResolver().query(Contract.Quote.makeUriForStock(stockSymbol), projection, Contract.Quote.COLUMN_SYMBOL + " = " + stockSymbol, null, null);
        String history = null;
        String symbol;

        if (cursor.moveToFirst()) {
            do {
                symbol = cursor.getString(cursor.getColumnIndex(Contract.Quote.COLUMN_SYMBOL));
                history = cursor.getString(cursor.getColumnIndex(Contract.Quote.COLUMN_HISTORY));
                getSupportActionBar().setTitle(symbol);
            } while (cursor.moveToNext());
        }
        cursor.close();

        String[] historyList = history.split("\n");
        List<String> list = Arrays.asList(historyList);
        Collections.reverse(list);
        historyList = (String[]) list.toArray();

        List<Entry> stockHistory = new ArrayList<>();
        for (String string : historyList) {
            String[] splitString = string.split(", ");
            stockHistory.add(new Entry(Float.valueOf(splitString[0]), Float.valueOf(splitString[1])));
        }

        LineDataSet dataSet = new LineDataSet(stockHistory, "historyLabel");
        dataSet.setDrawCircles(false);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(getResources().getColor(R.color.colorPrimary));
        dataSet.setColors(new int[]{R.color.colorPrimary}, getApplicationContext());
        dataSet.setLineWidth(2);
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        LineData lineData = new LineData(dataSet);
        chartView.setData(lineData);
        formatChart(chartView);
        chartView.invalidate();
    }

    private void formatChart(LineChart chart) {
        XAxis xAxis = chart.getXAxis();
        xAxis.setTextColor(Color.WHITE);
        xAxis.setValueFormatter(new DateFormatter(getApplicationContext()));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis yAxisLeft = chart.getAxisLeft();
        yAxisLeft.setTextColor(Color.WHITE);

        YAxis yAxisRight = chart.getAxisRight();
        yAxisRight.setTextColor(Color.WHITE);

        chart.setDrawGridBackground(false);
        chart.animateY(5000);

        Legend legend = chart.getLegend();
        legend.setEnabled(false);
    }
}
