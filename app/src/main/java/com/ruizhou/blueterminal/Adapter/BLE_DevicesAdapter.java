package com.ruizhou.blueterminal.Adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.ruizhou.blueterminal.Activity.DetailActivity;
import com.ruizhou.blueterminal.BLE_Service;
import com.ruizhou.blueterminal.Data.BLE_Device;
import com.ruizhou.blueterminal.GraphData;
import com.ruizhou.blueterminal.R;
import com.ruizhou.blueterminal.Utils.Utils_functions;

import java.util.List;

public class BLE_DevicesAdapter extends RecyclerView.Adapter<BLE_DevicesAdapter.ViewHolder> {
    private Context context;
    private List<BLE_Device> listItem;
    BLE_Service ble;


    public BLE_DevicesAdapter(Context context, List listItem, BLE_Service ble){
        this.context = context;
        this.listItem = listItem;
        this.ble = ble;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BLE_Device item = listItem.get(position);
        if(item.getName() == null){
            holder.name.setText("No Name");
        }
        else{
            holder.name.setText(item.getName());
        }
        holder.rssiStrength.setText(Integer.toString(item.getRssi()));
        holder.description.setText(item.getAddress());
        if(ble.isConnecting && item.isConnection()){
            holder.deviceConnection.setText("DISCONNECT");
        }
        else {
            item.setConnection(false);
            holder.deviceConnection.setText("CONNECT");
        }


    }

    @Override
    public int getItemCount() {
        return listItem.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView name;
        public TextView description;
        public TextView rssiStrength;
        public Button deviceConnection;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            name = (TextView) itemView.findViewById(R.id.title);
            description=(TextView) itemView.findViewById(R.id.description);
            rssiStrength=(TextView) itemView.findViewById(R.id.rssi);
            deviceConnection=(Button) itemView.findViewById(R.id.deviceConnection);
            deviceConnection.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    BLE_Device item = listItem.get(position);
                    if (!item.isConnection()){
                        item.setConnection(true);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            ble.mBluetoothGatt = item.getBluetoothDevice().connectGatt(context,
                                    true, ble.getGattCallback(), BluetoothDevice.TRANSPORT_LE);
                        }
                        else{
                            ble.mBluetoothGatt = item.getBluetoothDevice().connectGatt(context,
                                    true, ble.getGattCallback());

                        }

                    }
                    else{
                        item.setConnection(false);
                        ble.mBluetoothGatt.disconnect();


                    }

                }
            });


        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            BLE_Device item = listItem.get(position);

            Utils_functions.toast(context,"currently detail unavaliable");
            //TODO：Adding detail device activity
            Intent intent = new Intent(context, DetailActivity.class);
//            intent.putExtra("BLE",ble);
            context.startActivity(intent);



        }
    }
}
