package com.ruizhou.blueterminal.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ruizhou.blueterminal.Data.BLE_Device;

import java.util.ArrayList;
import java.util.List;

public class ListAdapter_BLE_Device extends ArrayAdapter<BLE_Device> {

    Context context;
    int layoutResourceID;
    List<BLE_Device> devices;

    public ListAdapter_BLE_Device(@NonNull Context context, int resource, @NonNull List<BLE_Device> objects) {
        super(context, resource, objects);
        this.context = context;
        layoutResourceID = resource;
        devices = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layoutResourceID,parent,false);

        }
//        return super.getView(position, convertView, parent);
        return convertView;
    }
}
