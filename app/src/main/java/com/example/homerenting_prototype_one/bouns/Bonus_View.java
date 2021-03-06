package com.example.homerenting_prototype_one.bouns;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.homerenting_prototype_one.BuildConfig;
import com.example.homerenting_prototype_one.R;
import com.example.homerenting_prototype_one.adapter.re_adpater.MutiSalaryAdapter;
import com.example.homerenting_prototype_one.adapter.re_adpater.SalaryAdapter;
import com.example.homerenting_prototype_one.calendar.Calendar;
import com.example.homerenting_prototype_one.order.Order;
import com.example.homerenting_prototype_one.setting.Setting;
import com.example.homerenting_prototype_one.system.System;
import com.example.homerenting_prototype_one.valuation.Valuation;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.homerenting_prototype_one.show.global_function.dip2px;
import static com.example.homerenting_prototype_one.show.global_function.getCompany_id;
import static com.example.homerenting_prototype_one.show.global_function.getToday;
import static com.example.homerenting_prototype_one.show.global_function.getYear;

public class Bonus_View extends AppCompatActivity {

    TextView title;
    ViewPager2 salaryView;
    ImageButton backBtn;
    Button chartBtn;

    ArrayList<String> employee_names;
    ArrayList<Integer> salaries;

    MutiSalaryAdapter msAdapter;

    String year, month;

    boolean init = true;

    private Context context = this;
    String TAG = "Bonus_View";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bonus__view);
        Button total_btn = findViewById(R.id.total_salary_btn);

        title = findViewById(R.id.title_BV);
        salaryView = findViewById(R.id.salaryList_BV);
        backBtn = findViewById(R.id.back_imgBtn);
        chartBtn = findViewById(R.id.chart_btn_BV);

        year = getToday("yyyy");
        month = getToday("MM");
        title.setText("?????????????????? "+month+"???");
        setList();

        chartBtn.setOnClickListener(v -> setChart());

        backBtn.setOnClickListener(v -> finish());


        total_btn.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("year", year);
            bundle.putString("month", month);
            Intent intent = new Intent();
            intent.setClass(context, Bonus_List_Detail.class);
            intent.putExtras(bundle);
            startActivity(intent);
        });

        globalNav();
    }

    private ArrayList getData(String year, String month){
//        employee_names = new ArrayList<>();
//        salaries = new ArrayList<>();
        ArrayList<String[]> data = new ArrayList<>();

        String function_name = "pay_oneMonth";
        String company_id = getCompany_id(context);
        RequestBody body = new FormBody.Builder()
                .add("function_name", function_name)
                .add("company_id", company_id)
                .add("year", year)
                .add("month", month)
                .build();
        Log.i(TAG, "company_id: "+company_id+", year: "+year+", month: "+month);

        //????????????
        Request request = new Request.Builder()
                .url(BuildConfig.SERVER_URL + "/user_data.php")
                .post(body)
                .build();

        OkHttpClient okHttpClient = new OkHttpClient();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                Log.d(TAG, "Failed: " + e.getMessage()); //??????????????????
                //???app???????????????????????????
                runOnUiThread(() -> Toast.makeText(context, "Toast onFailure.", Toast.LENGTH_LONG).show());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseData = response.body().string();
                Log.i(TAG,"responseData: "+responseData); //????????????

//                employee_names.add("");
//                salaries.add(0);

                try {
                    JSONArray responseArr = new JSONArray(responseData);

                    //??????????????????JSONArray??????json??????
                    for (int i = 0; i < responseArr.length(); i++) {
                        JSONObject staff = responseArr.getJSONObject(i);
                        Log.i(TAG,"staff: "+staff);

                        String staff_name = staff.getString("staff_name");
                        String total_payment = staff.getString("total_payment");
//                        employee_names.add(staff_name);
////                        salaries.add(Integer.parseInt(total_payment));

                        String[] row_data = {staff_name, total_payment};
                        data.add(row_data);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(data.size() < 1){
                    String[] row_data = {"????????????", ""};
                    data.add(row_data);
                }

                int i;
                Log.d(TAG, year+"/"+month+", data:"+data.size());
                for(i = 0; i < data.size(); i++) {
                    Log.d(TAG, Arrays.toString(data.get(0)));
                }

                if(init){
                    runOnUiThread(() -> {
                        if(!data.get(0)[1].isEmpty()) setChart(); //??????
                        init = false;
                    });
                }

            }
        });

        return data;
    }

    private void setChart(){ //??????
        msAdapter.showDatas();
        int currentItem = salaryView.getCurrentItem();
        employee_names = msAdapter.getStaffName(currentItem);
        salaries = msAdapter.getStaffPay(currentItem);
        Log.d(TAG, "employee_names, salaries: ");
        int i;
        for(i = 0; i < employee_names.size() && i < salaries.size(); i++){
            Log.d(TAG, i+"."+employee_names.get(i)+", "+salaries.get(i));
        }
        Log.d(TAG, "now page: "+currentItem);

        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.bonus_dialog);

        if(salaries.size() > 0) setBarChart(dialog);
        else dialog.setTitle("no data");

        Button dialog_btn = dialog.findViewById(R.id.check_dialog_btn);
        dialog_btn.setOnClickListener(v -> dialog.dismiss());

        dialog.getWindow().setLayout(dip2px(context, 375), dip2px(context, 500)); //1400, 2000
        dialog.setCanceledOnTouchOutside(true); //???????????????????????????
        dialog.show();

//        chartBtn.setOnClickListener(v -> dialog.show());
    }

    private void setBarChart(Dialog dialog){
        HorizontalBarChart hBarChart = dialog.findViewById(R.id.hBarChart_BD);

        BarDataSet barDataSet = new BarDataSet(getBarData(), ""); //????????????(??????List, ????????????????????????)
        barDataSet.setValueTextSize(20); //??????????????????
        barDataSet.setValueFormatter(new IndexAxisValueFormatter(){ //?????????????????????
            @Override
            public String getFormattedValue(float value) {
                if(value == 0) return "";
                return String.valueOf((int) value);
            }
        });
        barDataSet.setDrawValues(true); //???bar???????????????
        barDataSet.setColor(Color.parseColor("#FB8527")); //bar?????????
        barDataSet.setValueTextColor(Color.parseColor("#FFE7D3")); //??????????????????

        BarData barData = new BarData(barDataSet);
        barData.setHighlightEnabled(false); //??????????????????

        XAxis xAxis = hBarChart.getXAxis(); //X???
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); //X????????????(?????????)
        xAxis.setValueFormatter(new IndexAxisValueFormatter(employee_names)); //???x????????????labels???key
        xAxis.setLabelCount(employee_names.size()); //X???????????????
        xAxis.setDrawGridLines(false); //?????????????????????
        xAxis.setTextSize(20); //??????????????????(sp)

        YAxis yAxis = hBarChart.getAxisLeft();
        yAxis.setSpaceTop(0f); //??????bar??????????????????%
        yAxis.setDrawAxisLine(false); //????????????
        yAxis.setDrawGridLines(false); //?????????????????????
//        yAxis.setDrawLabels(false); //Y???????????????
        yAxis.setGranularity(getAverage());

        LimitLine ll = new LimitLine(getAverage()); //?????????
        ll.setLineColor(Color.parseColor("#19B0ED")); //????????????
        ll.setLineWidth(2f); //????????????
        yAxis.addLimitLine(ll); //???????????????

        Legend legend = hBarChart.getLegend(); //??????
        legend.setEnabled(false); //???????????????

        hBarChart.getAxisRight().setEnabled(false); //????????????(??????)Y???
        hBarChart.getDescription().setEnabled(false); //?????????????????????
        hBarChart.setDrawValueAboveBar(false); //????????????bar??????(true:above bar)?????????(false:top)
        hBarChart.setScaleEnabled(false); //????????????

        hBarChart.setData(barData); //??????????????????
    }

    private ArrayList getBarData(){
        ArrayList<BarEntry> yValue = new ArrayList<>(); //??????List
        for(int x = 0; x < employee_names.size() && x < salaries.size(); x++){
            yValue.add(new BarEntry(x, salaries.get(x))); //???????????????
            Log.d(TAG, "staff_name: "+employee_names.get(x)+", total_payment: "+salaries.get(x));
        }
        return yValue;
    }

    private float getAverage(){
        float max = 0;
        for(int i = 0; i < salaries.size(); i++) {
            max = max + salaries.get(i);
        }
        float average = max / (salaries.size()-1);
        return average;
    }

    private void setList(){
        msAdapter = new MutiSalaryAdapter(getData(year, month), Integer.parseInt(month));
        salaryView.setAdapter(msAdapter);
        salaryView.setCurrentItem(1);
        msAdapter.showDatas();

        salaryView.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                title.setText("?????????????????? "+msAdapter.getMonth(position)+"???");
                //??????????????????
                if(position == msAdapter.getItemCount()-1) {
                    int m = msAdapter.getMonth(position-1, 1);
                    month = intToString(m);
                    if(month.equals("01")){
                        int y = Integer.parseInt(year);
                        year = String.valueOf(++y);
                    }
                    Log.d(TAG, "add next page: "+year+"/"+month+"("+position+")");
                    ArrayList<String[]> temp = getData(year, month);
                    salaryView.post(() -> {
                        int i = 0;
                        while(temp.size() == 0){
                            if((++i)%1000000 == 0)
                                Log.d(TAG, i+". "+year+"/"+month+", temp data:"+temp.size());
                        }
                        msAdapter.setmData(temp, m, position);
                        title.setText("?????????????????? "+msAdapter.getMonth(position)+"???");
                        msAdapter.addPage(new ArrayList<>());
                    });

                }
                //???????????????
                if(position == 0) {
                    int m = msAdapter.getMonth(1, 2);
                    month = intToString(m);
                    if(month.equals("12")){
                        int y = Integer.parseInt(year);
                        year = String.valueOf(--y);
                    }
                    Log.d(TAG, "add last page: "+year+"/"+month+"("+position+")");
                    ArrayList<String[]> temp = getData(year, month);
                    salaryView.post(() -> {
                        int i = 0, num = 10000000;
                        while(temp.size() == 0){
                            if((++i)%num == 0)
                                Log.d(TAG, (i/num)+". "+year+"/"+month+", temp data:"+temp.size());
                        }
                        msAdapter.setmData(temp, m, position);
                        title.setText("?????????????????? "+msAdapter.getMonth(1)+"???");
                        msAdapter.addPage(new ArrayList<>(), 0);
                        salaryView.setCurrentItem(1, false);
                    });
                }
            }
        });
    }

    private String intToString(int month) {
        String monthStr = String.valueOf(month);
        if(month < 10) monthStr = "0"+monthStr;
        return monthStr;
    }

    private void globalNav(){
        ImageButton valuation_btn = findViewById(R.id.valuationBlue_Btn);
        ImageButton order_btn = findViewById(R.id.order_imgBtn);
        ImageButton calendar_btn = findViewById(R.id.calendar_imgBtn);
        ImageButton system_btn = findViewById(R.id.system_imgBtn);
        ImageButton setting_btn = findViewById(R.id.setting_imgBtn);
        //??????nav
        valuation_btn.setOnClickListener(v -> {
            Intent valuation_intent = new Intent(Bonus_View.this, Valuation.class);
            startActivity(valuation_intent);
        });
        order_btn.setOnClickListener(v -> {
            Intent order_intent = new Intent(Bonus_View.this, Order.class);
            startActivity(order_intent);
        });
        calendar_btn.setOnClickListener(v -> {
            Intent calender_intent = new Intent(Bonus_View.this, Calendar.class);
            startActivity(calender_intent);
        });
        system_btn.setOnClickListener(v -> {
            Intent system_intent = new Intent(Bonus_View.this, System.class);
            startActivity(system_intent);
        });
        setting_btn.setOnClickListener(v -> {
            Intent setting_intent = new Intent(Bonus_View.this, Setting.class);
            startActivity(setting_intent);
        });
    }
}
