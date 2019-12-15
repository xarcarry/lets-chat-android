package xar.com.qqapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import xar.com.qqapp.ChatActivity;
import xar.com.qqapp.R;
import xar.com.qqapp.bean.User;

public class MyFriendExpandableListAdapter extends BaseExpandableListAdapter {
    private List<Group> groupData;
    private List<List<Contact>> contactData;
    private Context context;

    //存放组数据
    static public class Group{
        private String title;//组标题
        private int onlineCount;//此组内在线的人数

        public Group(String title, int onlineCount){
            this.title = title;
            this.onlineCount = onlineCount;
        }

        public String getTitle(){
            return title;
        }

        public int getOnlineCount(){
            return onlineCount;
        }
    }

    //存放联系人数据
    static public class Contact{
        private long userId;//账号
        private Bitmap avatar;//头像
        private String name;//名字
        private String status;//状态

        public Contact(long userId, Bitmap avatar, String name, String status){
            this.userId = userId;
            this.avatar = avatar;
            this.name = name;
            this.status = status;
        }

        public long getUserId() {
            return userId;
        }
        public Bitmap getAvatar() {
            return avatar;
        }

        public String getName() {
            return name;
        }

        public String getStatus() {
            return status;
        }
    }

    public MyFriendExpandableListAdapter(List<Map<String, List<User>>> data, Context context) {
        this.context = context;
        dataToGroupContactDta(data);
    }

    public List<Group> getGroupData() {
        return groupData;
    }

    public List<List<Contact>> getContactData() {
        return contactData;
    }

    @Override
    public int getGroupCount() {
        return groupData.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return contactData.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupData.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return contactData.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ViewHolderGroup viewHolderGroup = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.contacts_group_item, parent, false);
            viewHolderGroup = new ViewHolderGroup(convertView);
            convertView.setTag(viewHolderGroup);
        } else {
            viewHolderGroup = (ViewHolderGroup) convertView.getTag();
        }
        viewHolderGroup.textViewTitle.setText(groupData.get(groupPosition).getTitle());
        viewHolderGroup.textViewCount.setText(groupData.get(groupPosition).getOnlineCount() + "/" + this.getChildrenCount(groupPosition));
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ViewHolderContact viewHolderItem = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.contacts_contact_item, parent, false);
            viewHolderItem = new ViewHolderContact(convertView);
            convertView.setTag(viewHolderItem);
        } else {
            viewHolderItem = (ViewHolderContact) convertView.getTag();
        }
        viewHolderItem.userId = contactData.get(groupPosition).get(childPosition).getUserId();
        viewHolderItem.imageViewHead.setImageBitmap(contactData.get(groupPosition).get(childPosition).getAvatar());
        viewHolderItem.textViewSign.setText(contactData.get(groupPosition).get(childPosition).getName());
        viewHolderItem.textViewDetail.setText(contactData.get(groupPosition).get(childPosition).getStatus());
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private static class ViewHolderGroup {
        TextView textViewTitle;//显示标题的控件
        TextView textViewCount;//显示好友数或者在线数的控件

        public ViewHolderGroup(View itemView) {
            this.textViewTitle = itemView.findViewById(R.id.textViewTitle);
            this.textViewCount = itemView.findViewById(R.id.textViewCount);
        }
    }

    private static class ViewHolderContact {
        private long userId;//用于保存账号
        private ImageView imageViewHead;//显示好友头像的控件
        private TextView textViewSign;//显示好友名字的控件
        private TextView textViewDetail;//显示好友状态的控件

        public ViewHolderContact(final View itemView) {
            userId = 0;
            this.imageViewHead = itemView.findViewById(R.id.imageViewHead);
            this.textViewSign = itemView.findViewById(R.id.textViewSign);
            this.textViewDetail = itemView.findViewById(R.id.textViewDetail);
            //当点击这一行时，开始聊天
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("wlf", "userId："+ userId);
                    //进入聊天页面
                    Intent intent = new Intent(itemView.getContext(), ChatActivity.class);
                    //将对方名字作为参数传过去
                    intent.putExtra("contactName", textViewSign.getText().toString());
                    intent.putExtra("contactId", userId);
                    itemView.getContext().startActivity(intent);
                }
            });
        }
    }

    public void dataToGroupContactDta(List<Map<String, List<User>>> data){
        groupData = new ArrayList<>();
        contactData = new ArrayList<>();
        for (Map<String, List<User>> map : data){
            String groupName = "";
            Set<String> keys = map.keySet();
            Iterator<String> iterator = keys.iterator();
            while (iterator.hasNext()){
                groupName = iterator.next();
            }
            groupData.add(new MyFriendExpandableListAdapter.Group(groupName, 0));

            List<Contact> contacts = new ArrayList<>();
            for (User user : map.get(groupName)){
                //头像
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.contact_normal);
                //联系人
                contacts.add(new MyFriendExpandableListAdapter.Contact(user.getUserId(), bitmap, user.getUsername(), user.getPassword()));
            }
            contactData.add(contacts);
        }
    }
}
