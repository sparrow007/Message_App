package com.jackandphantom.messageapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jackandphantom.messageapp.R;
import com.jackandphantom.messageapp.model.MessageModel;

import java.util.ArrayList;
import java.util.List;



public class DataAdapter extends RecyclerView.Adapter {

    private static final int VIEW_SENDER_MESSAGE_TYPE = 1;
    private static final int VIEW_RECEIVER_MESSAGE_TYPE = 2;

    private List<MessageModel> messageModel = new ArrayList<>();
    private Context context;

    public DataAdapter(Context context) {
        this.context = context;
    }

    public DataAdapter(List<MessageModel> messageModel, Context context) {
        this.messageModel = messageModel;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == VIEW_SENDER_MESSAGE_TYPE){
            View view =  LayoutInflater.from(context).inflate(R.layout.send_message_layout, parent, false);
            return new SenderMessageHolder(view);

        }else if (viewType == VIEW_RECEIVER_MESSAGE_TYPE) {
            View view =  LayoutInflater.from(context).inflate(R.layout.receive_message_layout, parent, false);
            return new ReceiverMessageHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        MessageModel model = messageModel.get(position);
        switch (holder.getItemViewType()) {
            case VIEW_SENDER_MESSAGE_TYPE :
                ((SenderMessageHolder)holder).bindData(model);
                break;
            case VIEW_RECEIVER_MESSAGE_TYPE:
                ((ReceiverMessageHolder)holder).bind(model);
        }
    }

    @Override
    public int getItemViewType(int position) {

        MessageModel model = messageModel.get(position);
        if (model.isUserType()) {
            return VIEW_SENDER_MESSAGE_TYPE;
        }else
        return VIEW_RECEIVER_MESSAGE_TYPE;
    }

    @Override
    public int getItemCount() {
        return messageModel.size();
    }

    public void setAddData(List<MessageModel> list) {
        messageModel.clear();
        this.messageModel.addAll(list);
        notifyDataSetChanged();
    }

    private class SenderMessageHolder extends RecyclerView.ViewHolder {

        TextView messageText, timeText;

         SenderMessageHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.text_message_body);
            timeText = itemView.findViewById(R.id.text_message_time);
        }

        void bindData(MessageModel messageModel) {
            messageText.setText(messageModel.getMessage());
            timeText.setText(messageModel.getTiming());
        }
    }

    void setPostion() {

    }

    private class ReceiverMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, nameText, iconText;
        ImageView profileImage;
         ReceiverMessageHolder(View itemView) {
            super(itemView);

            messageText = itemView.findViewById(R.id.text_message_body);
            timeText = itemView.findViewById(R.id.text_message_time);
            nameText = itemView.findViewById(R.id.text_message_name);
            profileImage = itemView.findViewById(R.id.image_message_profile);
            iconText = itemView.findViewById(R.id.receiver_text_icon);

        }

        void bind(MessageModel messageModel) {

             messageText.setText(messageModel.getMessage());
             nameText.setText(messageModel.getSender_name());
             timeText.setText(messageModel.getTiming());
             try {
                 iconText.setText(Character.toString(messageModel.getSender_name().charAt(0)));
             } catch (IndexOutOfBoundsException ex) {
                 ex.printStackTrace();
             } catch (NullPointerException ex) {
                 ex.printStackTrace();
             }


        }
    }
}
