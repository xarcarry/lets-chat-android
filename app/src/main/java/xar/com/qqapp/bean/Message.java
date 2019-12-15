package xar.com.qqapp.bean;

public class Message {

    private String contactName;//发出人的名字
    private long time;//发出消息的时间
    private String content;//消息的内容
    private byte[] fileByte;//文件的二进制数组
    private int type;//消息的类型

    public Message(){}

    public Message(String contactName, long time, String content, int type){
        this.contactName = contactName;
        this.time = time;
        this.content = content;
        this.type = type;
        this.fileByte = null;
    }

    public Message(String content, int type){
        this.contactName = "";
        this.time = 0;
        this.content = content;
        this.type = type;
    }

    public Message(byte[] fileByte, int type){
        this.fileByte = fileByte;
        this.type = type;
    }

    public byte[] getFileByte() {
        return fileByte;
    }

    public void setFileByte(byte[] fileByte) {
        this.fileByte = fileByte;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
