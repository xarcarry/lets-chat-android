package xar.com.qqapp;


import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationSet;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.niuedu.ListTree;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimerTask;

import xar.com.qqapp.R;
import xar.com.qqapp.adapter.ContactsPageListAdapter;
import xar.com.qqapp.adapter.MessagePageListAdapter;
import xar.com.qqapp.adapter.MyFriendExpandableListAdapter;
import xar.com.qqapp.adapter.ViewPageAdapter;
import xar.com.qqapp.bean.User;
import xar.com.qqapp.utils.DpChangePxUtil;

public class MainFragment extends Fragment {

    private ViewPager viewPager;
    private View[] listViews;
    private TabLayout tabLayout;
    private ViewGroup rootView;
    private TextView popMenu;
    private ExpandableListView myFriendExpandableListView;
    private MyFriendExpandableListAdapter myAdapter = null;

    //创建集合（一棵树）
    private static ListTree tree = new ListTree();

    public static ListTree getContactsTree() {
        return tree;
    }

    public MainFragment() {
        // Required empty public constructor
        listViews = new View[]{null, null, null};
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tree.removeCheckedNodes();
        RecyclerView v1 = new RecyclerView(getContext());
        //View v2 = getLayoutInflater().inflate(R.layout.contacts_page_layout, null);
        View v2 = createContactsPage();
        RecyclerView v3 = new RecyclerView(getContext());
        //创建三个RecyclerView，分别对应QQ消息页，QQ联系人页，QQ空间页
        listViews[0] = v1;
        listViews[1] = v2;
        listViews[2] = v3;

        //设置LayoutManager，否则看不到内容
        v1.setLayoutManager(new LinearLayoutManager(getContext()));
        v1.setAdapter(new MessagePageListAdapter(getActivity()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.rootView = (ViewGroup) inflater.inflate(R.layout.fragment_main,
                container, false);
        //获取ViewPager实例，将Adapter设置给它
        viewPager = this.rootView.findViewById(R.id.viewPager);
        viewPager.setAdapter(new ViewPageAdapter(listViews, getContext()));

        tabLayout = this.rootView.findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        popMenu = this.rootView.findViewById(R.id.textViewPopMenu);
        popMenu.setOnClickListener(new View.OnClickListener() {
            //把弹出窗口作为成员变量
            //创建PopupWindow，用于承载气泡菜单
            PopupWindow pop;
            View mask;

            @Override
            public void onClick(View v) {
                //向Fragment容器（FrameLayout）中加入一个View作为上层容器和遮罩
                if (mask == null) {
                    mask = new View(getContext());
                    mask.setBackgroundColor(Color.DKGRAY);
                    mask.setAlpha(0.5f);
                    mask.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            MainFragment.this.rootView.removeView(mask);
                            //隐藏弹出窗口
                            pop.dismiss();
                        }
                    });
                }

                MainFragment.this.rootView.addView(mask,
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT);

                //如果弹出窗口还未创建，则创建它
                if (pop == null) {
                    pop = new PopupWindow(getActivity());
                    //加载菜单项资源，使用LinearLayout模拟的菜单
                    LinearLayout menu = (LinearLayout)
                            LayoutInflater.from(getActivity()).inflate(R.layout.pop_menu_layout, null);
                    //计算一下菜单layout的实际大小然后获取之
                    menu.measure(0, 0);
                    int w = menu.getMeasuredWidth();
                    int h = menu.getMeasuredHeight();
                    //设置window的高度
                    pop.setHeight(h + 60);
                    //设置宽度
                    pop.setWidth(w + 60);
                    //设置window中要显示的View
                    pop.setContentView(menu);

                    //加载气泡图像，以作为window的背景
                    Drawable drawable = getResources().getDrawable(R.drawable.pop_bk);
                    //设置气泡图像为window的背景
                    pop.setBackgroundDrawable(drawable);

                    //设置动画效果
                    pop.setAnimationStyle(R.style.popoMenuAnim);

                    //设置窗口消失的侦听器，在窗口消失后把蒙板去掉
                    pop.setOnDismissListener(new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            //去掉蒙板
                            MainFragment.this.rootView.removeView(mask);
                        }
                    });

                    //设置窗口出现时获取焦点，这样按下返回键时，窗口才会消失
                    pop.setFocusable(true);
                }
                //显示窗口
                //设置弹窗偏移量
                pop.showAsDropDown(v, -pop.getWidth() + 60, -10);
            }
        });

        //响应左上角图标的点击事件，显示抽屉页面
        headImageClick();
        //刷新事件处理
        refresh(rootView);

        return rootView;
    }

    public void headImageClick() {
        ImageView headImage = rootView.findViewById(R.id.headImage);
        headImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //创建抽屉页面
                final View drawerLayout = getActivity().getLayoutInflater().inflate(
                        R.layout.drawer_layout, rootView, false);
                //获取原内容的控件
                final View contentLayout = rootView.findViewById(R.id.contentLayout);
                //先计算一下消息页面中，左边一排图像的大小，在界面构建器中设置的是dp
                //在代码中只能用像素，所以这里要换算一下，因为不同的屏幕分辨率
                // dp对应的像素是不同的
                int messageImageWidth = DpChangePxUtil.dip2px(getActivity(), 60);
                //计算抽屉页面的宽度，rootView是FrameLayout
                //利用getWidth()即可获得它当前的宽度
                final int drawerWidth = rootView.getWidth() - messageImageWidth;
                //设置抽屉的宽度
                drawerLayout.getLayoutParams().width = drawerWidth;
                //将抽屉页面加入FrameLayout中
                rootView.addView(drawerLayout);

                //创建蒙板
                final View maskView = new View(getContext());
                maskView.setBackgroundColor(Color.GRAY);
                //必须将其初始透明度设为完全透明
                maskView.setAlpha(0);
                //当点击蒙板View时，隐藏抽屉界面
                maskView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //动画反着来，让抽屉消失
                        drawerAnimation(contentLayout, maskView, drawerLayout
                                , drawerWidth, 0);
                    }
                });
                rootView.addView(maskView);

                //动画持续的时间
                final int duration = 400;

                //把它搞到最上层，这样在移动时能一直看到它（QQ就是这个效果）
                contentLayout.bringToFront();
                //再把蒙板View搞到最上层
                maskView.bringToFront();
                //创建动画，移动原内容，从0位置移动抽屉页面宽度的距离（注意其宽度不变）
                drawerAnimation(contentLayout, maskView, drawerLayout
                        , 0, drawerWidth);
            }
        });
    }

    public void drawerAnimation(View contentLayout, final View maskView
            , View drawerLayout, int leftLocation, int rightLocation) {
        int duration = 400;
        //创建动画，移动原内容，从0位置移动抽屉页面宽度的距离（注意其宽度不变）
        ObjectAnimator animatorContent = ObjectAnimator.ofFloat(contentLayout,
                "translationX", leftLocation, rightLocation);

        //移动蒙板的动画
        ObjectAnimator animatorMask = ObjectAnimator.ofFloat(maskView,
                "translationX", leftLocation, rightLocation);


        //响应此动画的刷新事件，在其中改变原页面 的背景色，使其逐渐变暗
        if (leftLocation == 0) {
            drawerShow(animatorMask, maskView, 0.0f);
        } else {
            drawerHide(animatorMask, maskView, 0.4f);
        }

        //创建一个动画，让抽屉页面向右移，注意它是从左移出来的，
        //所以其初始值设置为-drawerWidth / 2， 即有一半位于屏幕之外
        ObjectAnimator animatorDrawer = ObjectAnimator.ofFloat(drawerLayout,
                "translationX", -rightLocation / 2, -leftLocation / 2);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animatorContent, animatorMask, animatorDrawer);
        animatorSet.setDuration(duration);
        if (rightLocation == 0) {
            drawerClose(animatorSet, maskView, drawerLayout);
        }
        animatorSet.start();
    }

    public void drawerShow(ObjectAnimator animatorMask, final View maskView
            , final float alphaValue) {
        //响应此动画的刷新事件，在其中改变原页面 的背景色，使其逐渐变暗
        animatorMask.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            float alpha = alphaValue;

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                maskView.setAlpha(alpha);
                alpha += 0.1f;
            }
        });
    }

    public void drawerHide(ObjectAnimator animatorMask, final View maskView
            , final float alphaValue) {
        //响应此动画的刷新事件，在其中改变原页面 的背景色，使其逐渐变亮
        animatorMask.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            float alpha = alphaValue;

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                maskView.setAlpha(alpha);
                alpha -= 0.1f;
            }
        });
    }

    public void drawerClose(AnimatorSet animatorSet, final View maskView
            , final View drawerLayout) {
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

                //动画结束，将蒙板和抽屉页面删除
                rootView.removeView(maskView);
                rootView.removeView(drawerLayout);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    private View createContactsPage() {
        //创建View
        View v = getLayoutInflater().inflate(R.layout.contacts_page_layout, null);
        myFriendExpandableListView = v.findViewById(R.id.myFriendExpandableListView);

        List<Map<String, List<User>>> organizesAndUsers = new ArrayList<Map<String, List<User>>>();

        List<User> users1 = new ArrayList<User>();
        users1.add(new User(123456l, "蛮王", "我的大刀，早已饥渴难耐了..."));
        users1.add(new User(456789l, "瑞文", "断剑重铸之时，骑士归来之势！"));
        users1.add(new User(123456789l, "千珏", "执子之手，与子共生。"));
        Map<String, List<User>> map1 = new HashMap<>();
        map1.put("我的好友", users1);
        organizesAndUsers.add(map1);

        List<User> users2 = new ArrayList<User>();
        users2.add(new User(123456l, "伊泽瑞尔", "是时候表演真正的技术了..."));
        users2.add(new User(456789l, "瑞文", "断剑重铸之时，骑士归来之势！"));
        users2.add(new User(123456789l, "千珏", "执子之手，与子共生。"));
        users2.add(new User(123456789l, "拉克丝", "真是一个深思熟虑的选择。"));
        Map<String, List<User>> map2 = new HashMap<>();
        map2.put("朋友", users2);
        organizesAndUsers.add(map2);

        List<User> users3 = new ArrayList<User>();
        users3.add(new User(123456l, "贾克斯", "开打！开打！"));
        users3.add(new User(456789l, "锤石", "我该怎样进行这令人愉快的折磨呢！"));
        users3.add(new User(123456789l, "盖伦", "德玛西亚万岁！"));
        users3.add(new User(123456789l, "鳄鱼", "所有人都得死！"));
        users3.add(new User(123456789l, "德莱文", "欢迎来到德莱联盟。"));
        users3.add(new User(123456789l, "卡莉斯塔", "所有背叛者都得死！"));
        users3.add(new User(123456789l, "卡牌大师", "胜利女神在微笑。。。"));
        Map<String, List<User>> map3 = new HashMap<>();
        map3.put("家人", users3);
        organizesAndUsers.add(map3);

        List<User> users4 = new ArrayList<User>();
        users4.add(new User(123456l, "提莫", "提莫队长正在待命..."));
        Map<String, List<User>> map4 = new HashMap<>();
        map4.put("同学", users4);
        organizesAndUsers.add(map4);

        myAdapter = new MyFriendExpandableListAdapter(organizesAndUsers, getContext());
        myFriendExpandableListView.setAdapter(myAdapter);

        return v;
    }

    public void refresh(ViewGroup rootView) {

        final SwipeRefreshLayout refreshLayout = rootView.findViewById(R.id.refreshLayout);
        //响应它发出的事件
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //执行刷新数据的代码写在这里，不过一般都是耗时的操作或访问网路，
                //所以需要开启另外的线程
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //刷新完成，隐藏UFO
                refreshLayout.setRefreshing(false);
            }
        });
    }

    //设置搜索相关的东西
    /*private void initSearching(View v){
        //搜索控件
        SearchView searchView = v.findViewById(R.id.searchView);
        //不以图标的形式显示
        searchView.setIconifiedByDefault(false);

        //取消按钮
        TextView cancleView = v.findViewById(R.id.tvCancel);
        //搜索结果列表
        final RecyclerView resultListView = v.findViewById(R.id.resultListView);
        resultListView.setLayoutManager(new LinearLayoutManager(getContext()));
        resultListView.setAdapter(new ResultListAdapter());

        //响应SearchView的文本输入事件，以实现实时搜索
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //根据newText中的字符串进行搜索，搜索其中包含关键字的节点
                ListTree tree = MainFragment.getContactsTree();
                //必须每次都清空保存结果的集合对象
                searchResultList.clear();

                //只有当要搜索的字符串非空时，才遍历列表
                if (!newText.equals("")){
                    //遍历整个树
                    ListTree.EnumPos
                }
                return false;
            }
        });
    }*/
}
