package com.app.homework.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.app.homework.R;
import com.app.homework.adapter.LunBoToAdapter;
import com.app.homework.adapter.ShouYeChildAdapter;
import com.app.homework.bean.NewsBean;
import com.app.homework.util.NetConnectionUtil;
import com.google.gson.Gson;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 轮播图fragment
 */
public class LunBoTu_Fragment extends Fragment {
    @BindView(R.id.quanbu_recyview)
    RecyclerView quanbuRecyview;
    @BindView(R.id.quanbu_recyview02)
    XRecyclerView quanbuRecyview02;
    Unbinder unbinder;
    private List<NewsBean.ResultBean.DataBean> listNews = new ArrayList<>();
    private String encode;
    private Handler handler = new Handler();
    private ShouYeChildAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //设置布局
        View view = inflater.inflate(R.layout.lunboto_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //获取传递的标题数据，把获取的标题转码实现
        Bundle bundle = getArguments();
        String data = bundle.getString("name", "top");
        encode = URLEncoder.encode(data);

        //判断网络状态，异步加载数据
        if (NetConnectionUtil.isNetConnectioned(getActivity())) {
            getData(encode);
        } else {
            NetConnectionUtil.setNetConnectionWork(getActivity());
        }

        //设置多条目展示的布局，含有 轮播图 + RecyclerView
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayout.VERTICAL, false);
        quanbuRecyview.setLayoutManager(linearLayoutManager);
        LunBoToAdapter lunBoToAdapter = new LunBoToAdapter(getActivity());
        quanbuRecyview.setAdapter(lunBoToAdapter);

        //XRecyclerview的上拉下拉方法
        quanbuRecyview02.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //在子线程内完成下拉加载数据
                        getData(encode);
                        adapter.notifyDataSetChanged();
                        quanbuRecyview02.refreshComplete();
                    }
                }, 888);
            }

            @Override
            public void onLoadMore() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //在子线程内完成下拉加载数据
                        getData(encode);
                        adapter.notifyDataSetChanged();
                        quanbuRecyview02.loadMoreComplete();
                    }
                }, 888);
            }
        });
    }

    //请求网络数据的方法
    public void getData(final String encode) {
        //使用OKhttp请求网络数据
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                // http://v.juhe.cn/toutiao/index?type=toutiao&key=2df01835231e41bfe1d652cf2d2c7f07
                .url("http://v.juhe.cn/toutiao/index?type=" + encode + "&key=2df01835231e41bfe1d652cf2d2c7f07")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(getActivity(), "数据出错：" + e, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String result = response.body().string();
                System.out.println("返回：" + result);

                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        //子线程内成功的回调
                        NewsBean bean = new Gson().fromJson(result, NewsBean.class);
                        listNews = bean.getResult().getData();
                        System.out.println("标题：" + listNews.get(0).getTitle());

                        //设置布局管理器以及布局适配器
                        if (adapter == null) {
                            LinearLayoutManager manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                            quanbuRecyview02.setLayoutManager(manager);
                            adapter = new ShouYeChildAdapter(getActivity(), listNews);
                            quanbuRecyview02.setAdapter(adapter);
                        } else {
                            adapter.notifyDataSetChanged();
                        }
                    }
                });

            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
