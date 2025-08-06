package hifi.music.tube.downloader.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import hifi.music.tube.downloader.R;
import cs.BL;
import cs.BN;
import jf.CI;
import hifi.music.tube.downloader.ztools.ImageHelper;
import hifi.music.tube.downloader.ztools.vPrefsUtils;
import hifi.music.tube.downloader.ztools.ToastUtils;

public class DownAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private PlayListener mListener;
//    private ExplosionField mExplosionField;//爆炸效果

    public DownAdapter(Context context, PlayListener listener) {
        mContext = context;
        mListener = listener;
//        mExplosionField = ExplosionField.attach2Window((Activity) mContext);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_task, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final CI track = (CI) BL.getInstance().getReverseAllTasks().get(position);
        final MyViewHolder viewHolder = (MyViewHolder) holder;
        viewHolder.itemName.setText(track.getTitle());
        viewHolder.itemArtist.setText(track.getArtistName());
        if (!TextUtils.isEmpty(track.getImage())) {
            ImageHelper.loadMusic(viewHolder.itemIcon, track.getImage(), mContext, 60, 60);
        } else {
            viewHolder.itemIcon.setImageResource(R.drawable.ic_default_cover);
        }
        viewHolder.itemRintone.setTag(track);
        viewHolder.itemRintone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CI bean = (CI) v.getTag();
                if (bean.downloadStats != CI.DL_DONE) {
                    ToastUtils.showLongToast("Must Download Finished!");
                    return;
                }
                BN dialog = new BN(v.getContext());
                dialog.setBean(bean);
                dialog.show();
            }
        });
        viewHolder.itemPlay.setTag(position);
        viewHolder.itemPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<CI> arrayList = BL.getInstance().getReverseAllTasks();
                int index = (int) v.getTag();
                playClickItem(arrayList, index);

            }
        });
        viewHolder.itemView.setTag(position);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<CI> arrayList = BL.getInstance().getReverseAllTasks();
                int index = (int) v.getTag();
                playClickItem(arrayList, index);
            }
        });


        viewHolder.itemDelete.setTag(track);
        viewHolder.itemDelete.setTag(R.string.app_name, viewHolder.itemView);
        viewHolder.itemDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final CI track = (CI) view.getTag();
                AlertDialog.Builder confirmDlg = new AlertDialog.Builder(mContext);
                confirmDlg.setTitle("confirm");
                confirmDlg.setMessage(track.title);
                confirmDlg.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
//                                mExplosionField.explode((View) view.getTag(R.string.app_name));
                                BL.getInstance().removeTask(track);
                                notifyDataSetChanged();
                                vPrefsUtils.setDownloadCache(BL.getInstance().getAllTasks());
                            }
                        }, 100);
                    }
                });
                confirmDlg.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                confirmDlg.show();
            }
        });
        if (track.downloadStats == CI.DL_DONE) {
            viewHolder.itemProgress.setText("success");
        } else if (track.downloadStats == CI.DL_ERROR) {
            viewHolder.itemProgress.setText("error");
        } else {
            viewHolder.itemProgress.setText(track.progress > 0.0f ? track.progress + "%" : "0%");
        }
    }

    private void playClickItem(ArrayList<CI> arrayList, int index) {
        mListener.onPlay(arrayList,index);

    }


    @Override
    public int getItemCount() {
        return null == BL.getInstance().getReverseAllTasks() ? 0 : BL.getInstance().getReverseAllTasks().size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        //@BindView(R.id.item_icon)
        ImageView itemIcon;
        //@BindView(R.id.item_name)
        TextView itemName;
        //@BindView(R.id.artist_tv)
        TextView itemArtist;
        //@BindView(R.id.item_progress)
        TextView itemProgress;
        //@BindView(R.id.item_play)
        ImageView itemPlay;
        //@BindView(R.id.item_ringtone)
        ImageView itemRintone;
        //@BindView(R.id.item_delete)
        ImageView itemDelete;

        public MyViewHolder(View itemView) {
            super(itemView);
            itemDelete = itemView.findViewById(R.id.item_delete);
            itemRintone = itemView.findViewById(R.id.item_ringtone);
            itemPlay = itemView.findViewById(R.id.item_play);
            itemProgress = itemView.findViewById(R.id.item_progress);
            itemArtist = itemView.findViewById(R.id.artist_tv);
            itemName = itemView.findViewById(R.id.item_name);
            itemIcon = itemView.findViewById(R.id.item_icon);
        }
    }

    public interface PlayListener {
        void onPlay(ArrayList<CI> arrayList, int index);
    }

}
