package dc;

import android.app.Activity;
import android.content.Context;

/* compiled from: BD */
public class BD {
    public static Context context;
    private static volatile BD recommendManager;
    private BF BF;
    private String packageIds = "";
    private static String recomSource = "";
    private BB popupDataProvider = null;
    private BB mainDataProvider = null;

    private BD() {
    }

    public static void init(Context context, String recomSource) {
        BD.context = context;
        BD.recomSource = recomSource;
        BE.init(context);
    }

    public static String getRecomSource() {
        return recomSource;
    }

    public void setProvider(String packageIds) {
        if (packageIds == null) {
            packageIds = "";
        }
        if (packageIds.equals(this.packageIds)) {
            return;
        }
        this.packageIds = packageIds;
        popupDataProvider = new BB(packageIds);
        mainDataProvider = new BB(packageIds);
    }

    /* Access modifiers changed, original: protected */
    BB getProvider() {
        return this.popupDataProvider;
    }

    BF getRecomEventListener() {
        return this.BF;
    }

    public static BD getInstance() {
        if (recommendManager == null) {
            synchronized (BD.class) {
                if (recommendManager == null) {
                    recommendManager = new BD();
                }
            }
        }
        return recommendManager;
    }

    public BC getMainRecommend() {
        return this.mainDataProvider != null ? this.mainDataProvider.getBean() : null;
    }

    public boolean showSmallRecommend(Activity activity) {
        if (activity == null || popupDataProvider == null) {
            return false;
        }
        try {
            if (activity.isFinishing()) {
                return false;
            }
            BC recommendBean = this.popupDataProvider.getBean();
            if (!canShow(recommendBean)) {
                return false;
            }
            G.newInstance(activity).build(recommendBean).addView();
            return true;
        } catch (Throwable th) {
            th.printStackTrace();
            return false;
        }
    }

    public boolean showRecommend(Context context) {
        if (context == null || popupDataProvider == null) {
            return false;
        }
        BC bean = this.popupDataProvider.getBean();
        if (!canShow(bean)) {
            return false;
        }
        O.show(context, bean);
        return true;
    }

    private boolean canShow(BC recommendBean) {
        if (recommendBean == null) {
            return false;
        }
        int showCount = BE.getCount(recommendBean.getPackageId());
        if (showCount >= BE.getMaxShowCount() || !BE.getCanClick(recommendBean.getPackageId())) {
            this.popupDataProvider.remove(recommendBean);
            return false;
        }
        int i = showCount + 1;
        if (i == BE.getMaxShowCount()) {
            this.popupDataProvider.remove(recommendBean);
        }
        BE.setCount(recommendBean.getPackageId(), i);
        return true;
    }
}
