package cs;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;


import org.schabi.newpipe.extractor.stream.StreamInfoItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import s.H;
import hifi.music.tube.downloader.R;
import hifi.music.tube.downloader.adapter.AMusicAdapter;
import hifi.music.tube.downloader.network.HttpArchiveMusic;
import hifi.music.tube.downloader.network.JamApi;
import hifi.music.tube.downloader.network.CallBack;
import hifi.music.tube.downloader.network.Mp3Juice;
import hifi.music.tube.downloader.network.freemp3.FreeMp3Cloud;
import hifi.music.tube.downloader.network.nhac.NhacMusic;
import hifi.music.tube.downloader.network.nhac.NhacMusicListBean;
import hifi.music.tube.downloader.network.yt.YTManager;
import hifi.music.tube.downloader.network.yt.YTSearch;
import jf.CI;
import es.BV;
import hifi.music.tube.downloader.ztools.MathConst;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

import static jf.CI.CHANNEL_NHAC;
import static jf.CI.CHANNEL_QQ;
import static jf.CI.CHANNEL_WYY;

import com.airbnb.lottie.LottieAnimationView;

/**
 * 搜索结果
 */
public class BG extends BH {
    public static final int TYPE_JAMENDO = 1;
    public static final int TYPE_ARCHIVE = 2;
    public static final int TYPE_SOUND = 3;
    public static final int TYPE_Q = 4;
    public static final int TYPE_XM = 5;
    public static final int TYPE_BD = 6;
    public static final int TYPE_KG = 7; //no use
    public static final int TYPE_MJ = 8; //music juice
    public static final int TYPE_YT = 9; // youtube
    public static final int TYPE_NHAC = 10;
    public static final int TYPE_FREE_MP3 = 11; //
    //    public static final int TYPE_QQ = 12; //
//    public static final int TYPE_WYY = 13; //
    public static final int TYPE_MP3JUICE = 14; //

    protected static final String Q = "q";
    protected static final String TYPE = "type";
    RecyclerView mRecyclerView;
    TextView errorTv;
    TextView emptyTv;
    LottieAnimationView loadingV;



    private int mType;
    private String mQuery;
    private ArrayList<CI> mDatas = new ArrayList<>();

    //    Call<List<ScBean>> mSoundCall;

    private AMusicAdapter mAdapter;

    public static BG newInstance(int type, String query) {
        BG f = new BG();
        Bundle args = new Bundle();
        args.putInt(TYPE, type);
        args.putString(Q, query);
        f.setArguments(args);
        return f;
    }

    @Override
    protected boolean isStartEventBus() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_result;
    }

    @Override
    protected void initView(View parentView, Bundle savedInstanceState) {
        RecyclerView mRecyclerView;
        mRecyclerView = parentView.findViewById(R.id.rv);
        errorTv = parentView.findViewById(R.id.error_tv);
        emptyTv = parentView.findViewById(R.id.empty_tv);
        loadingV = parentView.findViewById(R.id.aviLoad);

        if (getArguments() != null) {
            mQuery = getArguments().getString(Q);
            mType = getArguments().getInt(TYPE);
        }

        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        ((SimpleItemAnimator) mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        mAdapter = new AMusicAdapter(getActivity(), mDatas);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void initListener() {
        errorTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestDatas();
            }
        });

    }

    @Override
    protected void initDatas() {
        super.initDatas();
        requestDatas();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void requestDatas() {
        mDatas.clear();
        loadingV.setVisibility(View.VISIBLE);
        errorTv.setVisibility(View.GONE);
        emptyTv.setVisibility(View.GONE);

        if (H.config.isLocalSearchDeal) {
            String lowerQuery = mQuery.toLowerCase();
            if ((lowerQuery.contains("move") && lowerQuery.contains("your") && lowerQuery.contains("body")) ||
                    (lowerQuery.contains("ek") && lowerQuery.contains("din") && lowerQuery.contains("teri")) ||
                    (lowerQuery.contains("baby") && lowerQuery.contains("jealous")) ||
                    (lowerQuery.contains("baby") && lowerQuery.contains("guy")) ||
                    (lowerQuery.contains("billie") && lowerQuery.contains("eilish")) ||
                    (lowerQuery.contains("bebe") && lowerQuery.contains("rexha"))) {
                empty();
                return;
            }
        }

        if (mType == TYPE_JAMENDO) {
            JamApi jamApi = new JamApi();
            jamApi.search(mQuery, 100, new JamApi.JamCallback() {
                @Override
                public void onLoadSuc(List<CI> list) {
                    if (list == null || list.isEmpty()) {
                        empty();
                        return;
                    }
                    mDatas.addAll(list);
                    dealData();
                }

                @Override
                public void onError(boolean empty) {
                    error();
                }
            });
        }
        else if (mType == TYPE_YT) {
            YTManager.getInstance().search(mQuery, new YTSearch.onSearchListener() {

                @Override
                public void onSuccess(List<StreamInfoItem> list) {
                    if (list == null || list.isEmpty()) {
                        empty();
                        return;
                    }
                    for (int i = 0; i < list.size(); i++) {
                        StreamInfoItem item = list.get(i);
                        CI bean = new CI();
                        bean.channel = CI.CHANNEL_YT;
                        bean.id = !TextUtils.isEmpty(item.getUrl()) ? BV.stringToMD5(item.getUrl()) : String.valueOf(item.hashCode());
                        bean.setTitle(item.getName());
                        bean.setArtistName(item.getUploaderName());
                        bean.setDownloadUrl(null);
                        bean.setListenUrl(item.getUrl());
                        bean.setImage(item.getThumbnails().get(item.getThumbnails().size()-1).getUrl());
                        mDatas.add(bean);
                    }
                    dealData();
                }

                @Override
                public void onError() {
                    error();
                }

                @Override
                public void onEmpty() {
                    empty();
                }
            });
        }
        else if (mType == TYPE_FREE_MP3) {
            FreeMp3Cloud.search(mQuery, new FreeMp3Cloud.SearchListener() {
                @Override
                public void onError(boolean empty) {
                    empty();
                }

                @Override
                public void onSuccess(List<CI> list) {
                    if (list == null || list.isEmpty()) {
                        empty();
                        return;
                    }
                    mDatas.addAll(list);
                    dealData();
                }
            });
        }
//        else if (mType == TYPE_QQ) {
//            BaseApiImpl.INSTANCE.searchSongSingle(mQuery, "QQ", 50, 1, new Function1<SearchSingleData, Unit>() {
//                @Override
//                public Unit invoke(SearchSingleData searchSingleData) {
//                    SearchResult searchResult = searchSingleData.getData();
//                    if (searchResult != null) {
//                        List<MusicInfo> list = searchResult.getSongs();
//                        List<CI> beanList = new ArrayList<>();
//                        if (list != null && !list.isEmpty()) {
//                            for (MusicInfo musicInfo : list) {
//                                CI bean = new CI();
//                                bean.channel = CHANNEL_QQ;
//                                bean.id = musicInfo.getId();
//                                bean.setTitle(musicInfo.getName());
//                                beanList.add(bean);
//                            }
//                        }
//                        if (beanList.isEmpty()) {
//                            empty();
//                        } else {
//                            mDatas.addAll(beanList);
//                            dealData();
//                        }
//                    }
//                    return Unit.INSTANCE;
//                }
//            });
//        }
//        else if (mType == TYPE_WYY) {
//            BaseApiImpl.INSTANCE.searchSongSingle(mQuery, "NETEASE", 50, 1, new Function1<SearchSingleData, Unit>() {
//                @Override
//                public Unit invoke(SearchSingleData searchSingleData) {
//                    SearchResult searchResult = searchSingleData.getData();
//                    if (searchResult != null) {
//                        List<MusicInfo> list = searchResult.getSongs();
//                        List<CI> beanList = new ArrayList<>();
//                        if (list != null && !list.isEmpty()) {
//                            for (MusicInfo musicInfo : list) {
//                                CI bean = new CI();
//                                bean.channel = CHANNEL_WYY;
//                                bean.id = musicInfo.getId();
//                                bean.setTitle(musicInfo.getName());
//                                beanList.add(bean);
//                            }
//                        }
//
//                        if (beanList.isEmpty()) {
//                            empty();
//                        } else {
//                            mDatas.addAll(beanList);
//                            dealData();
//                        }
//                    }
//                    return Unit.INSTANCE;
//                }
//            });
//        }
        else if (mType == TYPE_NHAC) {
            NhacMusic.search(mQuery, new CallBack<List<NhacMusicListBean.DataBean>>() {
                @Override
                public void onFail() {
                    empty();
                }

                @Override
                public void onSuccess(List<NhacMusicListBean.DataBean> dataBeans) {
                    if (dataBeans != null && !dataBeans.isEmpty()) {
                        List<CI> beanList = new ArrayList<>();
                        for (NhacMusicListBean.DataBean dataBean : dataBeans) {
                            CI bean = new CI();
                            bean.channel = CHANNEL_NHAC;
                            bean.id = dataBean.getId() + "";
                            bean.setTitle(dataBean.getTitle());
                            bean.setArtistName(dataBean.getArtist_title());
                            beanList.add(bean);
                        }
                        if (beanList.isEmpty()) {
                            empty();
                        } else {
                            mDatas.addAll(beanList);
                            dealData();
                        }
                    } else {
                        empty();
                    }
                }
            });
        } else if (mType == TYPE_ARCHIVE) {
            HttpArchiveMusic.search(mQuery, new HttpArchiveMusic.SearchListener() {
                @Override
                public void onError(boolean empty) {
                    empty();
                }

                @Override
                public void onSuccess(List<CI> list) {
                    if (list == null || list.isEmpty()) {
                        empty();
                    } else {
                        mDatas.addAll(list);
                        dealData();
                    }
                }
            });


        } else if (mType == TYPE_MP3JUICE) {
            Mp3Juice.search(mQuery, new Mp3Juice.SearchListener() {
                @Override
                public void onError(boolean empty) {
                    empty();
                }

                @Override
                public void onSuccess(List<CI> list) {
                    if (list == null || list.isEmpty()) {
                        empty();
                    } else {
                        mDatas.addAll(list);
                        dealData();
                    }
                }
            });
        }

    }

    /**
     * 加工所有数据
     */
    private void dealData() {
        if (null != loadingV) {
            loadingV.setVisibility(View.GONE);
            errorTv.setVisibility(View.GONE);
            emptyTv.setVisibility(View.GONE);
        }
        if (mDatas == null || mDatas.size() == 0) {
            empty();
            return;
        }

        //fuck IFPI and sex
        String[] bans = MathConst.bans.split("-");
        List<String> words = new ArrayList<>(Arrays.asList(bans));

        bans = MathConst.sexy.split("-");
        words.addAll(Arrays.asList(bans));

        if (!TextUtils.isEmpty(H.config.so)) {
            bans = H.config.so.split("-");
            words.addAll(Arrays.asList(bans));
        }

        if (!TextUtils.isEmpty(H.config.sx)) {
            bans = H.config.sx.split("-");
            words.addAll(Arrays.asList(bans));
        }

        Iterator<CI> iterator = mDatas.iterator();
        while (iterator.hasNext()) {
            CI next = iterator.next();
            if (banSongs(next.title, words)
                    || banSongs(next.artistName, words)) {
                iterator.remove();
            }
        }

        if (mDatas == null || mDatas.size() == 0) {
            empty();
        } else {
            CI bean = new CI();
            bean.id = "-1001";
            int index = H.config.sni - 1;
            if (mDatas.size() > index) {
                mDatas.add(index, bean);
            } else {
                mDatas.add(bean);
            }
        }

        if (null != mAdapter) {
            mAdapter.notifyDataSetChanged();
        }
    }

    private boolean banSongs(String search, List<String> bans) {
        if (TextUtils.isEmpty(search)) {
            return false;
        }
        search = search.replaceAll("[ -.~!@#$%^&*()_+={}|:<>?/\\\\,'\";\\]\\[`]", "").toLowerCase();
        for (String ban : bans) {
            if (!TextUtils.isEmpty(ban) && search.contains(ban)) {
                return true;
            }
        }
        return false;
    }

    private void error() {
        if (errorTv != null) {
            errorTv.setVisibility(View.VISIBLE);
            loadingV.setVisibility(View.GONE);
            emptyTv.setVisibility(View.GONE);
        }
    }

    private void empty() {
        if (errorTv != null) {
            errorTv.setVisibility(View.GONE);
            loadingV.setVisibility(View.GONE);
            emptyTv.setVisibility(View.VISIBLE);
        }

    }

}

