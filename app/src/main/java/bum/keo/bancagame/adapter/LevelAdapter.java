package bum.keo.bancagame.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import bum.keo.bancagame.LevelModel;
import bum.keo.bancagame.LevelState;
import bum.keo.bancagame.PlayActivity;
import bum.keo.bancagame.R;

public class LevelAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<LevelModel> levelList;

    // Constructor
    public LevelAdapter(Context context, ArrayList<LevelModel> levelList) {
        this.context = context;
        this.levelList = levelList;
    }

    @Override
    public int getCount() {
        return levelList.size();
    }

    @Override
    public Object getItem(int position) {
        return levelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_history_new, parent, false);
            holder = new ViewHolder();
            holder.imageView = convertView.findViewById(R.id.img_logo);
            holder.textView = convertView.findViewById(R.id.levelTv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final LevelModel level = levelList.get(position);
        holder.imageView.setImageResource(level.getImageResource());
        holder.textView.setText("Màn " + (position + 1));

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if the level state is PLAYABLE
                if (level.getState() == LevelState.PLAYABLE) {
                    // Start PlayActivity
                    Intent intent = new Intent(context, PlayActivity.class);
                    // Optionally put extras if you need to pass data to PlayActivity
                    intent.putExtra("level_id", position); // for example, pass the level id
                    context.startActivity(intent);
                } else if (level.getState() == LevelState.LOCKED)  {
                    // Handle other states if necessary, such as showing a message
                    Toast.makeText(context, "Level này chưa được mở.", Toast.LENGTH_SHORT).show();
                } else if (level.getState() == LevelState.COMPLETED)  {
                    // Handle other states if necessary, such as showing a message
                    Toast.makeText(context, "Level này bạn vượt qua.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return convertView;
    }


    // ViewHolder pattern to improve performance
    static class ViewHolder {
        ImageView imageView;
        TextView textView;
    }
}
