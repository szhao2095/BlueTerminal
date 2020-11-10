package com.ruizhou.blueterminal.Adapter;

import android.app.LauncherActivity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ruizhou.blueterminal.Data.BLE_Device;
import com.ruizhou.blueterminal.R;
import com.ruizhou.blueterminal.Utils.Utils_functions;

import java.util.List;

public class BLE_DevicesAdapter extends RecyclerView.Adapter<BLE_DevicesAdapter.ViewHolder> {
    private Context context;
    private List<BLE_Device> listItem;

    public BLE_DevicesAdapter(Context context, List listItem){
        this.context = context;
        this.listItem = listItem;
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
        if(item.isConnection()){
            holder.deviceConnection.setText("DISCONNECT");
        }
        else holder.deviceConnection.setText("CONNECT");


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


        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            BLE_Device item = listItem.get(position);

            Utils_functions.toast(context,"currently detail unavaliable");
            //TODO：Adding detail device activity


        }
    }
}
