package hifi.music.tube.downloader.ztools;

import android.widget.Toast;

import s.H;

import me.drakeet.support.toast.ToastCompat;


public class ToastUtils {
    public static void showShortToast(String msg) {
        try {
            ToastCompat.makeText(H.getInstance(),msg,Toast.LENGTH_SHORT).show();
        } catch (Throwable e){

        }
    }
    public static void showLongToast(String msg) {
        try {
            ToastCompat.makeText(H.getInstance(),msg,Toast.LENGTH_LONG).show();
        } catch (Throwable e){

        }
    }
    public static void showShortToast(int string) {
        try {
            ToastCompat.makeText(H.getInstance(), H.getInstance().getResources().getString(string),Toast.LENGTH_SHORT).show();
        } catch (Throwable e){

        }
    }
    public static void showLongToast(int string) {
        try {
            ToastCompat.makeText(H.getInstance(), H.getInstance().getResources().getString(string),Toast.LENGTH_LONG).show();
        } catch (Throwable e){

        }

    }
}
