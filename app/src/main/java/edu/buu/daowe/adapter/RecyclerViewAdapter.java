package edu.buu.daowe.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import edu.buu.daowe.R;

/**
 * Created by elimy on 2016-12-26.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.CardViewHolder> {
    private Context context;
    private ArrayList<CardData> datalist;


    public RecyclerViewAdapter() {
    }


    /*
     * 带参构造函数，传入上下文和需要绑定的数据集合
     * */
    public RecyclerViewAdapter(Context cx, ArrayList datalist) {
        this.context = cx;
        this.datalist = datalist;
    }


    /*
     *创建ViewHolder，持有布局映射
     * */
    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        //通过布局加载器获取到CardView布局view
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_cardview_content, parent, false);
        //通过获取到的布局view实例化一个自己实现的CardViewHolder
        CardViewHolder cardViewHolder = new CardViewHolder(view);
        //返回一个已绑定布局的viewHolder，避免重复findViewById()
        return cardViewHolder;
    }


    /*
     *onBindViewHolder方法做药作用就是将数据集绑定到布局view，以及添加一些事件点击监听
     * */
    @Override
    public void onBindViewHolder(RecyclerViewAdapter.CardViewHolder holder, int position) {

        final int pos = position;

        //将view中的view和数据集绑定
        //  holder.cardImage.setImageResource(datalist.get(position).ge);
        holder.tvcoursedate.setText(datalist.get(position).getDate());
        holder.tvcoursenum.setText(datalist.get(position).getCoursenum());
        holder.tvcoursename.setText(datalist.get(position).getCoursename());
        holder.tvcoursetime.setText(datalist.get(position).getCoursetime());
        holder.tvcourseposition.setText(datalist.get(position).getCourseposition());
        holder.tvcourseteacher.setText(datalist.get(position).getTeachername());

        //给整个cardview添加点击事件
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "You click card" + pos, Toast.LENGTH_SHORT).show();
            }
        });

//        //设置不喜欢的按钮点击监听事件
//        holder.disLikeBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(context, "你为啥不喜欢我呢？", Toast.LENGTH_SHORT).show();
//            }
//        });
//        //设置喜欢的按钮点击监听事件
//        holder.likeBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(context, "我就知道你喜欢我！", Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    /*
     * 返回recyclerview数据项的个数
     * */
    @Override
    public int getItemCount() {
        return datalist.size();
    }


    /*
     * 自定义实现viewholder类
     * */
    class CardViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView cardImage;
        TextView tvcoursenum, tvcoursedate, tvcoursetime, tvcourseposition, tvcourseteacher, tvcoursename;
        //  Button likeBtn, disLikeBtn;


        public CardViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cv1);
            cardImage = (ImageView) itemView.findViewById(R.id.img_card_status);
            tvcoursedate = itemView.findViewById(R.id.tv_card_date);
            tvcoursenum = itemView.findViewById(R.id.tv_card_course_num);
            tvcoursename = itemView.findViewById(R.id.tv_card_course_title);
            tvcourseposition = itemView.findViewById(R.id.tv_card_course_position);
            tvcourseteacher = itemView.findViewById(R.id.tv_card_course_teacher);
            tvcoursetime = itemView.findViewById(R.id.tv_card_course_time);
        }
    }
}