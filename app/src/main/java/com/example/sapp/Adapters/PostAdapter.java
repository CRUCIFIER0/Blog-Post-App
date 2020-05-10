package com.example.sapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sapp.Activities.PostDetailActivity;
import com.example.sapp.Models.Post;
import com.example.sapp.R;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {


    Context mContext;
    List<Post> mData;

    public PostAdapter(Context mContext, List<Post> mData) {
        this.mContext=mContext;
        this.mData=mData;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View row = LayoutInflater.from(mContext).inflate(R.layout.row_post_item, parent, false);

        return new MyViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder .title.setText(mData.get(position).getTitle());
        Glide.with(mContext).load(mData.get(position).getPicture()).into(holder.imgPost);
        Glide.with(mContext).load(mData.get(position).getUserPhoto()).into(holder.imgpostprofile);

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        ImageView imgPost, imgpostprofile;


        public MyViewHolder(View itemView){
            super(itemView);

            title= itemView.findViewById(R.id.row_post_title);
            imgPost = itemView.findViewById(R.id.row_post_img);
            imgpostprofile= itemView.findViewById(R.id.row_post_profile_img);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent postDetailActivity = new Intent(mContext, PostDetailActivity.class);
                    int position=  getAdapterPosition();
                    postDetailActivity.putExtra("title",mData.get(position).getTitle());
                    postDetailActivity.putExtra("postImage",mData.get(position).getPicture());
                    postDetailActivity.putExtra("description",mData.get(position).getDescription());
                    postDetailActivity.putExtra("postKey",mData.get(position).getPostKey());
                    postDetailActivity.putExtra("title",mData.get(position).getTitle());
                    postDetailActivity.putExtra("userPhoto",mData.get(position).getUserPhoto());
                    //postDetailActivity.putExtra("userName",mData.get(position).getUsername());
                    long timestamp = (long)mData.get(position).getTimeStamp();
                    postDetailActivity.putExtra("postDate",timestamp);
                    mContext.startActivity(postDetailActivity);

                }
            });
        }
    }
}
