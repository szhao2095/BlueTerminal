package com.ruizhou.blueterminal.Adapter;

import android.app.LauncherActivity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        holder.name.setText(item.getName());
        holder.description.setText(item.getAddress());

    }

    @Override
    public int getItemCount() {
        return listItem.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView name;
        public TextView description;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            name = (TextView) itemView.findViewById(R.id.title);
            description=(TextView) itemView.findViewById(R.id.description);

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
