package cs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;

import s.H;
import hifi.music.tube.downloader.R;
import es.BU;
import hifi.music.tube.downloader.network.JamApi;
import jf.BZ;
import jf.CI;
import hifi.music.tube.downloader.admax.MaxMrec;
import hifi.music.tube.downloader.admax.MaxSearchBanner;
import cj.BS;
import hifi.music.tube.downloader.referrer.AGeneralReferrer;
import hifi.music.tube.downloader.referrer.ReferrerItem;
import hifi.music.tube.downloader.referrer.ReferrerStream;
import ft.A;
import hifi.music.tube.downloader.ztools.ImageHelper;
import hifi.music.tube.downloader.ztools.ShareUtils;
import hifi.music.tube.downloader.ztools.Utils;
import dc.BC;
import dc.BD;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jp.B;
import jp.V;

public class BP extends BH {
    ArrayList<String> tags = new ArrayList<>(Arrays.asList(H.config.tags.split("\\|")));

    //@BindView(R.id.banner_ll)
    LinearLayout bannerLl;
    //@BindView(R.id.tagView)
    B tagContainerLayout;
    //@BindView(R.id.layout_notice)
    LinearLayout layout_notice;
    //@BindView(R.id.banner_layout)
    A mReferrerBannerLayout;
    //@BindView(R.id.daily_picks_panel)
    LinearLayout dailyPickLL;
    //@BindView(R.id.genres_panel)
    View genresPanel;
    //@BindView(R.id.genres_ll_panel)
    LinearLayout genresLL;
    //@BindView(R.id.mrecLayout)
    LinearLayout mrecLayout;


    private JamApi jamApi = new JamApi();

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main;
    }

    @Override
    protected void initView(View parentView, Bundle savedInstanceState) {
        bannerLl = parentView.findViewById(R.id.banner_ll);
        tagContainerLayout = parentView.findViewById(R.id.tagView);
        layout_notice = parentView.findViewById(R.id.layout_notice);
        mReferrerBannerLayout = parentView.findViewById(R.id.banner_layout);
        dailyPickLL = parentView.findViewById(R.id.daily_picks_panel);
        genresPanel = parentView.findViewById(R.id.genres_panel);
        genresLL = parentView.findViewById(R.id.genres_ll_panel);
        mrecLayout = parentView.findViewById(R.id.mrecLayout);

        parentView.findViewById(R.id.daily_picks_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                J.start(getActivity(), dailyPicks);
            }
        });

        parentView.findViewById(R.id.genres_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                L.start(getActivity(), genres);
            }
        });

//        String string = App.config.notice;
//        if (TextUtils.isEmpty(string) || TextUtils.isEmpty(App.config.noticeId)) {
//            this.layout_notice.setVisibility(View.GONE);
//        } else {
//            this.tvNotice.setText(string);
//        }
    }

    @Override
    protected void initListener() {
//        tvLink.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View view) {
//                if (!TextUtils.isEmpty(App.config.noticeId)) {
//                    ShareUtils.gotoRecommend(getActivity(), App.config.noticeId);
//                }
//            }
//        });
        tagContainerLayout.setTags(tags);
        tagContainerLayout.setOnTagClickListener(new V.OnTagClickListener() {

            @Override
            public void onTagClick(int position, String text) {
                P.launch(getContext(), text);

            }

            @Override
            public void onTagLongClick(final int position, String text) {
                P.launch(getContext(), text);
            }

            @Override
            public void onTagCrossClick(int position) {
                if (position >= 0 && position < tags.size()) {
                    P.launch(getContext(), tags.get(position));

                }
            }
        });
    }

    @Override
    public void initDatas() {
        loadBanner();
        loadMrec();

        loadDailyPicks();
        loadGenres();
    }

    private Type type = new TypeToken<ArrayList<BZ>>() {
    }.getType();

    private ArrayList<BZ> genres;

    private void loadGenres() {
        if (!H.config.showGenre) {
            return;
        }

        if (genres != null && !genres.isEmpty()) {
            dealGenres(genres);
            return;
        }

        String json = Utils.readAsset("genres.json");
        ArrayList<BZ> genres = Utils.fromJson(json, type);
        dealGenres(genres);
    }

    private void dealGenres(ArrayList<BZ> list) {
        if (list == null || list.isEmpty()) {
            genresPanel.setVisibility(View.GONE);
        } else {
            genres = list;
            int size = list.size();
            LayoutInflater inflater = getLayoutInflater();
            for (int i = 0; i < 3 && i < size; ++i) {
                View itemView = inflater.inflate(R.layout.item_genre_layout, genresLL, false);

                itemView.setOnClickListener(genreItemClick);
                itemView.setTag(i);

                ImageView iv = itemView.findViewById(R.id.genre_iv);
                TextView tv = itemView.findViewById(R.id.genre_title_tv);

                BZ bean = list.get(i);

                ImageHelper.loadMusic(iv, bean.image, getContext(), 0, 0);
                tv.setText(bean.title.trim());
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) itemView.getLayoutParams();
                lp.width = 0;
                lp.weight = 1;
                if (i > 0) {
                    lp.leftMargin = Utils.dip2px(getContext(), 20);
                }
                genresLL.addView(itemView);
            }
        }
    }

    private View.OnClickListener genreItemClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            BZ bean = genres.get((Integer) v.getTag());
            J.start(getActivity(), bean);
        }
    };

    private void loadDailyPicks() {
        if (!H.config.showDaily) {
            return;
        }
        if (dailyPicks != null && !dailyPicks.isEmpty()) {
            dealDailyPicks(dailyPicks);
            return;
        }
        jamApi.popular(J.DAILY_PICKS_GENRES_PAGE_SIZE, 0, new JamApi.JamCallback() {
            @Override
            public void onLoadSuc(List<CI> list) {
                dealDailyPicks(list);
            }

            @Override
            public void onError(boolean empty) {
            }
        });
    }

    private ArrayList<CI> dailyPicks;

    private void dealDailyPicks(List<CI> list) {
        if (list == null || list.isEmpty() || unsafe()) {
            return;
        }
        dailyPicks = new ArrayList<>(list);
        int size = list.size();
        LayoutInflater inflater = getLayoutInflater();
        for (int i = 0; i < 3 && i < size; i++) {
            View itemView = inflater.inflate(R.layout.item_daily_pick_layout,
                    dailyPickLL, false);
            itemView.setTag(i);
            itemView.setOnClickListener(dailyPickClick);

            ImageView iv = itemView.findViewById(R.id.image_iv);
            TextView titleTv = itemView.findViewById(R.id.title_tv);
            TextView artistTv = itemView.findViewById(R.id.artist_tv);

            CI music = list.get(i);
            ImageHelper.loadMusic(iv, music.getImage(), getContext(), 60, 60);
            titleTv.setText(music.getTitle());
            artistTv.setText(music.getArtistName());

            dailyPickLL.addView(itemView);
        }
        dailyPickLL.setVisibility(View.VISIBLE);
    }

    private View.OnClickListener dailyPickClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            BS.playList(dailyPicks, (Integer) v.getTag());
        }
    };

    @Override
    public void onDestroyView() {
        jamApi.destroy();
        super.onDestroyView();
        MaxSearchBanner.getInstance().stopAutoRefresh();
        MaxSearchBanner.getInstance().destroy();
        MaxMrec.getInstance().stopAutoRefresh();
        MaxMrec.getInstance().destroy();
    }

    @Override
    public void onStart() {
        super.onStart();
        ReferrerStream referrer = H.config.getUseReferrer();
        if (referrer == null || referrer.getGeneral(AGeneralReferrer.TYPE_TITLEBAR_MENU) == null
                || referrer.getGeneral(AGeneralReferrer.TYPE_TITLEBAR_MENU).isInvalid()) {
            this.layout_notice.setVisibility(View.GONE);
            BU.referrer("show", "false");
        } else {
            this.layout_notice.setVisibility(View.VISIBLE);
            BU.referrer("show", "true");
            AGeneralReferrer general = referrer.getGeneral(AGeneralReferrer.TYPE_TITLEBAR_MENU);
            List<ReferrerItem> items = general.getValidItems();
            mReferrerBannerLayout.loadReferrer(this.getContext(), items);
        }
    }

    // @OnClick(R.id.daily_picks_tv)
    void onDailyPickClick() {
        J.start(getActivity(), dailyPicks);
    }

    // @OnClick(R.id.genres_tv)
    void onGenresClick() {
        L.start(getActivity(), genres);
    }

    private void loadMrec() {
        if (!H.config.showNative) {
            return;
        }
        if (mrecLayout == null) {
            return;
        }
        if (getActivity() == null) {
            return;
        }

        MaxMrec.getInstance().createMrecAd(getActivity(), mrecLayout);

    }


    private void loadBanner() {
//        if (!App.config.showBanner2 || unsafe()) {
//            return;
//        }

        if (getActivity() == null) {
            return;
        }

        if (H.config.ad && bannerLl != null) {
            MaxSearchBanner.getInstance().createBannerAd(getActivity(), bannerLl);
        }
//        AdManager.getInstance().loadBanner2(getActivity(), new BannerListener() {
//            @Override
//            public void onAdLoaded(BaseBanner banner) {
//                if (null != bannerLl) {
//                    bannerLl.setVisibility(View.VISIBLE);
//                    banner.show(bannerLl);
//                }
//            }
//        });
    }

}