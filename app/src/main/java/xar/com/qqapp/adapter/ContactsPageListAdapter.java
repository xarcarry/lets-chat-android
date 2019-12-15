package xar.com.qqapp.adapter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.niuedu.ListTree;
import com.niuedu.ListTreeAdapter;
import com.niuedu.ListTreeViewHolder;

import xar.com.qqapp.ChatActivity;
import xar.com.qqapp.R;

public class ContactsPageListAdapter extends
        ListTreeAdapter<ListTreeViewHolder> {

    //存放组数据
    static public class GroupInfo{
        private String title;//组标题
        private int onlineCount;//此组内在线的人数

        public GroupInfo(String title, int onlineCount){
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
    static public class ContactInfo{
        private long userId;//账号
        private Bitmap avatar;//头像
        private String name;//名字
        private String status;//状态

        public ContactInfo(long userId, Bitmap avatar, String name, String status){
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

    public ContactsPageListAdapter(ListTree tree) {
        super(tree);
    }

    public ContactsPageListAdapter(ListTree tree, Bitmap expandIcon, Bitmap collapseIcon) {
        super(tree, expandIcon, collapseIcon);
    }

    @Override
    protected ListTreeViewHolder onCreateNodeView(ViewGroup parent, int viewType) {
        //获取从Layout创建view的对象
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        //创建不同的行view
        if (viewType == R.layout.contacts_group_item){
            //最后一个参数必须传true
            View view = inflater.inflate(viewType, parent, true);
            return new GroupViewHolder(view);
        }else if(viewType == R.layout.contacts_contact_item){
            View view = inflater.inflate(viewType, parent, true);
            return new ContactViewHolder(view);
        }
        return null;
    }

    @Override
    protected void onBindNodeViewHolder(ListTreeViewHolder viewHolder, int position) {

        //获取行控件
        View view = viewHolder.itemView;
        //获取这一行这树对象中对应的节点
        ListTree.TreeNode node = tree.getNodeByPlaneIndex(position);

        if (node.getLayoutResId() == R.layout.contacts_group_item){
            GroupInfo info = (GroupInfo)node.getData();
            GroupViewHolder gvh = (GroupViewHolder)viewHolder;
            gvh.textViewTitle.setText(info.getTitle());
            gvh.textViewCount.setText(info.getOnlineCount() + "/" + node.getChildrenCount());
        }else if (node.getLayoutResId() == R.layout.contacts_contact_item){
            ContactInfo info = (ContactInfo)node.getData();
            ContactViewHolder cvh = (ContactViewHolder)viewHolder;
            cvh.userId = info.getUserId();
            cvh.imageViewHead.setImageBitmap(info.getAvatar());
            cvh.textViewSign.setText(info.getName());
            cvh.textViewDetail.setText(info.getStatus());
        }
    }

    //组ViewHolder
    class GroupViewHolder extends ListTreeViewHolder{

        TextView textViewTitle;//显示标题的控件
        TextView textViewCount;//显示好友数或者在线数的控件

        public GroupViewHolder(View itemView) {
            super(itemView);
            this.textViewTitle = itemView.findViewById(R.id.textViewTitle);
            this.textViewCount = itemView.findViewById(R.id.textViewCount);
        }
    }

    //好友ViewHolder
    class ContactViewHolder extends ListTreeViewHolder{

        private long userId;//用于保存账号
        private ImageView imageViewHead;//显示好友头像的控件
        private TextView textViewSign;//显示好友名字的控件
        private TextView textViewDetail;//显示好友状态的控件

        public ContactViewHolder(final View itemView) {
            super(itemView);
            userId = 0;
            this.imageViewHead = itemView.findViewById(R.id.imageViewHead);
            this.textViewSign = itemView.findViewById(R.id.textViewSign);
            this.textViewDetail = itemView.findViewById(R.id.textViewDetail);
            //当点击这一行时，开始聊天
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
}
