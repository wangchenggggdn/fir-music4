package dc;

import android.text.TextUtils;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import static dc.BE.isInstalled;

/* compiled from: BB */
public class BB {
    private Random random = new Random();
    private ArrayList<BC> beans = new ArrayList();

//    BB() {
//        BC recommendBean = new BC(R.drawable.ic_playtube, "Tube Player - YouTube Player", "Tube Player – Float Tube , Video Tube, Music Tube is a free lite third party client for YouTube , allows you to easily find great videos and free music in all over of the world and your country.", 60, "com", "playtube", "videotube", "tubevideo");
//        if (VERSION.SDK_INT >= 21 && !isInstalled(BD.context, recommendBean.getPackageId())) {
//            this.beans.add(recommendBean);
//        }
//        recommendBean = new BC(R.drawable.ic_moviebox, "HD Movies Free 2019", " Watch Best Movies of 2019, Movie Video Player, ShowBox & MovieBox, Watch Movie is never easier, download movies fast and play offline", 50, "com", "free", "movie", "video", "player");
//        if (!isInstalled(BD.context, recommendBean.getPackageId())) {
//            this.beans.add(recommendBean);
//        }
//        recommendBean = new BC(R.drawable.ic_ringtone, "Ringtone & Color Call Screen", "Want to make your phone Cool and Shining? This app provides music ringtone, dynamic wallpapers and the colorful call screen themes.", 80, "free", "mp3", "music", "download", "ringtone", "maker", "call", "flash");
//        if (!isInstalled(BD.context, recommendBean.getPackageId())) {
//            this.beans.add(recommendBean);
//        }
////        recommendBean = new BC(R.drawable.ic_video_downloader, "All Video Downloader 2019", "Easy-to-use video downloader for Facebook, Instagram，Twitter,Tumblr etc,download videos fast and play offline", 46, "com", "downloadvideo", "freevideodownloader", "downloadall");
////        if (!BE.isInstalled(BD.context, recommendBean.getPackageId())) {
////            this.beans.add(recommendBean);
////        }
////        int nextInt = new Random().nextInt(6);
////        if (nextInt == 4) {
////            recommendBean = new BC(R.drawable.ic_freemusicly_downloader, "Download Mp3 Music - Unlimited offline Music download free", "Free Music Downloader: Download MP3 Songs,Search MP3 Music, Free Music Player", 46, "com", "musicgo", "downloadmusic", "mp3musicdownload", "musicmp3");
////            if (!BE.isInstalled(BD.context, recommendBean.getPackageId())) {
////                this.beans.add(recommendBean);
////            }
////        } else if (VERSION.SDK_INT < 21 || !(nextInt == 5 || nextInt == 0)) {
////            recommendBean = new BC(R.drawable.ic_simple_ponymusic, "Download Music - MP3 Downloader & Music Player", "Mp3 Music Download & Free Music Downloader Search, Download and Play free music Online and Offline whenever you want", 46, "com", "downloadmp3", "musicdownload", "downloadmusoc", "musicmp3");
////            if (!BE.isInstalled(BD.context, recommendBean.getPackageId())) {
////                this.beans.add(recommendBean);
////            }
////        } else {
//        recommendBean = new BC(R.drawable.ic_music, "Unlimited Mp3 Music Downloader & Free Music Download", "Unlimited Mp3 Music Downloader & Free Music Download, easy to find,listen to and download", 40, "new", "free", "music", "mp3", "download", "game", "studio");
//        if (!isInstalled(BD.context, recommendBean.getPackageId())) {
//            this.beans.add(recommendBean);
//        }
////        }
//    }

    //promote: packageId1%%desc1%%weight%%super%%title%%imgUrl
    BB(String packageIds) {
        if (TextUtils.isEmpty(packageIds)) {
            return;
        }
        String[] packageStrArr = packageIds.split("\\|\\|");
        for (String packageStr : packageStrArr) {
            BC recommendBean = new BC(packageStr);
            if (!isInstalled(BD.context, recommendBean.getPackageId())) {
                this.beans.add(recommendBean);
                if (!TextUtils.isEmpty(recommendBean.getImgUrl())) {
                    Glide.with(BD.context)
                            .load(recommendBean.getImgUrl())
                            .preload();
                }
            }
        }
    }

    public synchronized void remove(BC recommendBean) {
        this.beans.remove(recommendBean);
    }

//    public synchronized void verifyValidPackages(String[] validPackageIds) {
//        if (validPackageIds == null || validPackageIds.length == 0) {
//            this.beans.clear();
//            return;
//        }
//
//        for (Iterator<BC> it = this.beans.iterator(); it.hasNext(); ) {
//            BC bean = it.next();
//            boolean valid = false;
//            for (String validId : validPackageIds) {
//                if (bean.getPackageId().equals(validId)) {
//                    valid = true;
//                    break;
//                }
//            }
//            if (!valid) {
//                it.remove();
//            }
//        }
//    }

    public synchronized BC getBean() {
        BC recommendBean;
        Iterator it = this.beans.iterator();
        int weightSum = 0;
        while (it.hasNext()) {
            BC bean = (BC) it.next();
            if (isInstalled(BD.context, bean.getPackageId())) {
                it.remove();
                continue;
            }
            weightSum = bean.getWeight() + weightSum;
        }
        if (weightSum > 0) {
            int randomWeight = random.nextInt(weightSum);
            int weight = 0;
            Iterator it2 = this.beans.iterator();
            while (true) {
                int num = weight;
                if (!it2.hasNext()) {
                    recommendBean = null;
                    break;
                }
                recommendBean = (BC) it2.next();
                if (num <= randomWeight && randomWeight < num + recommendBean.getWeight()) {
                    break;
                }
                weight = recommendBean.getWeight() + num;
            }
        } else {
            android.util.Log.e("BB", "Error weightSum " + weightSum);
            recommendBean = null;
        }
        return recommendBean;
    }
}
