package edu.buu.daowe.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import edu.buu.daowe.R;

public class DestoryinAdapter extends BaseAdapter {

    ArrayList time, type, startqjtime, endqjtime;
    Context mContext;

    //View view;
    @Override
    public int getCount() {
        return time.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.fragment_destory_content, parent, false);
            holder = new ViewHolder();
            holder.tvtime = convertView.findViewById(R.id.tv_qjtime);
            holder.tvtime.setTextColor(0xff000000);
            holder.tvtype = convertView.findViewById(R.id.tv_qjtype);
            holder.tvdetail = convertView.findViewById(R.id.tv_qjdetail);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (type.get(position).equals("事假")) {
            holder.tvtype.setTextColor(0xffe6b800);
        } else {
            holder.tvtype.setTextColor(0xffcc0000);
        }
        holder.tvtime.setText("" + time.get(position));
        holder.tvtype.setText("" + type.get(position));
        holder.tvdetail.setText("第 " + startqjtime.get(position) + " 节~第 " + endqjtime.get(position) + " 节");

        //view.setOnLongClickListener(this);
        return convertView;

    }

    public DestoryinAdapter(ArrayList time, ArrayList type, ArrayList startqjtime, ArrayList endqjtime, Context mContext) {
        this.time = time;
        this.type = type;
        this.startqjtime = startqjtime;
        this.endqjtime = endqjtime;
        this.mContext = mContext;
    }
}

class ViewHolder {
    public TextView tvtime, tvtype, tvdetail;


}
