package hh;

import android.content.Context;

import es.BU;
import hifi.music.tube.downloader.ztools.vPrefsUtils;


public class Z {
    private static volatile Z instance;
    private static final int totalCount = 2;

    public static Z getInstance() {
        if (instance == null) {
            synchronized (Z.class) {
                if (instance == null) {
                    instance = new Z();
                }
            }
        }
        return instance;
    }

    private Z() {

    }


    public void tryRateFinish(Context context) {
        if (!isShowRate()) {
            return;
        }
        incRateCount(1);
        N.launch(context, "Music Downloader", "For you to download Music, Please rate us Five Stars", new N.RatingClickListener() {
            @Override
            public void onClickFiveStart() {
                incRateCount(totalCount);
            }

            @Override
            public void onClick1To4Start() {
            }

            @Override
            public void onClickReject() {
            }
        });
    }

    public boolean isShowRate() {
        Y bean = vPrefsUtils.getRateBean();
        if (null == bean) {
            return true;
        }

        if (bean.nextTime < totalCount) {
            return true;
        } else {
            return false;
        }
    }

    private void incRateCount(int inc) {
        Y bean = vPrefsUtils.getRateBean();
        if (null == bean) {
            bean = new Y();
            bean.nextTime = 0;
        }
        bean.nextTime += inc;
        vPrefsUtils.setRateBean(bean);
    }

}
