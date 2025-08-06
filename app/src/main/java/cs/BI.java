package cs;

import android.app.Activity;
import android.app.Dialog;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import s.H;
import hifi.music.tube.downloader.R;
import hifi.music.tube.downloader.ztools.ShareUtils;


public class BI {
    public void showDialog(final Activity activity, final String str, boolean force,String uInfo) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(1);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog);
        ((Button) dialog.findViewById(R.id.btn_dialog)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (!TextUtils.isEmpty(H.config.webappurl)) {
                    ShareUtils.openBrowser(activity, H.config.webappurl);
                } else {
                    ShareUtils.gotoGoogePlayStore(activity, !TextUtils.isEmpty(str) ? str : H.sContext.getPackageName());
                }
            }
        });

        Button cancel = (Button) dialog.findViewById(R.id.btnCancle);
        if (force) {
            cancel.setVisibility(View.GONE);
        } else {
            cancel.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
        }

        TextView textDialog = dialog.findViewById(R.id.text_dialog);
        textDialog.setText(uInfo);
        dialog.show();
    }
}
