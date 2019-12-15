package xar.com.qqapp.fragment;

import android.support.v4.app.Fragment;

public abstract class ChatToolsFragment extends Fragment {
    private boolean isShow;

    public ChatToolsFragment(){
        this.isShow = true;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }
}
