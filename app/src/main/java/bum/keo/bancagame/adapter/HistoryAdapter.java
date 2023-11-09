package bum.keo.bancagame.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import bum.keo.bancagame.R;


public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private ArrayList<Integer> pointsList;
    private Context context;

    public HistoryAdapter(Context context, ArrayList<Integer> pointsList) {
        this.context = context;
        this.pointsList = pointsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int point = pointsList.get(position);
        holder.sttTV.setText(String.valueOf(position + 1));
        holder.pointTv.setText(point + " ĐIỂM");
        if (position%2==0){
            holder.itemRel.setBackgroundColor(Color.parseColor("#4DFFFFFF"));
        }else{
            holder.itemRel.setBackgroundColor(Color.parseColor("#4D000000"));
        }
    }

    @Override
    public int getItemCount() {
        return pointsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView sttTV, pointTv;
        RelativeLayout itemRel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            sttTV = itemView.findViewById(R.id.sttTV);
            pointTv = itemView.findViewById(R.id.pointTv);
            itemRel = itemView.findViewById(R.id.itemRel);
        }
    }
}

