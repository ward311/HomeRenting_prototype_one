package com.example.homerenting_prototype_one.adapter.base_adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.homerenting_prototype_one.R;
import com.example.homerenting_prototype_one.order.Order_Detail;
import com.example.homerenting_prototype_one.valuation.MatchMaking_Detail;
import com.example.homerenting_prototype_one.valuation.ValuationBooking_Detail;
import com.example.homerenting_prototype_one.valuation.ValuationCancel_Detail;
import com.example.homerenting_prototype_one.valuation.Valuation_Detail;

import java.util.ArrayList;
import java.util.List;

public class RecordAdapter extends BaseAdapter {
    private Context context;
    ArrayList<String[]> data;
    String TAG = "RecordAdapter";

    public RecordAdapter(ArrayList<String[]>data){
        this.data = data;
    }

    @Override
    public int getCount(){
        return data == null? 0:data.size();
    }

    @Override
    public Object getItem(int position){
        return data.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        RecordViewHolder viewHolder = null;
        if (context==null){
            context = parent.getContext();
        }
        if (convertView==null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.record_item, null);
          }
        viewHolder = new RecordViewHolder();
        viewHolder.record = convertView.findViewById(R.id.record_item_R);
        viewHolder.day_text = convertView.findViewById(R.id.day_R);
        viewHolder.day_text.setTag(position);
        viewHolder.name_text = convertView.findViewById(R.id.name_R);
        viewHolder.status_text = convertView.findViewById(R.id.status_R);
        convertView.setTag( viewHolder );

        viewHolder.day_text.setText(data.get(position)[1]);
        viewHolder.name_text.setText(data.get(position)[2]);
        String status = "";
        if(data.get(position)[3].equals("chosen")){
            viewHolder.day_text.setBackgroundColor(Color.parseColor("#19B0ED"));
            status = data.get(position)[4];
            if(data.get(position)[4].equals("cancel")) status = "??????(???)";
        }
        else{
            viewHolder.day_text.setBackgroundColor(Color.parseColor("#FB8527"));
            status = data.get(position)[3];
            if(data.get(position)[3].equals("cancel")) status = "??????(???)";
        }
        viewHolder.status_text.setText(getStatusStr(status));
        String finalStatus = getStatusStr(status);
        viewHolder.record.setOnClickListener(v -> {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("order_id", data.get(position)[0]);
            Log.d(TAG, "finalStatus:"+finalStatus);
            switch (finalStatus) {
                case "????????????":
                    intent.setClass(context, Valuation_Detail.class);
                    break;
                case "????????????":
                    intent.setClass(context, ValuationBooking_Detail.class);
                    break;
                case "?????????":
                    intent.setClass(context, MatchMaking_Detail.class);
                    break;
                case "?????????(???)":
                    intent.setClass(context, ValuationCancel_Detail.class);
                    break;
                case "??????":
                case "???????????????":
                case "??????":
                case "?????????":
                case "?????????(???)":
                default:
                    intent.setClass(context, Order_Detail.class);
                    bundle.putBoolean("btn", true);
            }
            intent.putExtras(bundle);
            context.startActivity(intent);
        });

        return convertView;
    }

    private String getStatusStr(String status){
        switch (status) {
            case "self":
                return "????????????";
            case "booking":
                return "????????????";
            case "match":
                return "?????????";
            case "scheduled":
                return "??????";
            case "assigned":
                return "???????????????";
            case "done":
                return "??????";
            case "paid":
                return "?????????";
            default:
                return status;
        }
    }

    static class RecordViewHolder{
        LinearLayout record;
        TextView day_text, name_text, status_text;
    }
}
