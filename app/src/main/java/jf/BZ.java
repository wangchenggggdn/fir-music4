package jf;

import android.os.Parcel;
import android.os.Parcelable;

public class BZ extends CA implements Parcelable {
    public int id;
    public String title;
    public String image;

    public int followers;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.title);
        dest.writeString(this.image);
        dest.writeInt(this.followers);
    }

    public BZ() {
    }

    protected BZ(Parcel in) {
        this.id = in.readInt();
        this.title = in.readString();
        this.image = in.readString();
        this.followers = in.readInt();
    }

    public static final Parcelable.Creator<BZ> CREATOR = new Parcelable.Creator<BZ>() {
        @Override
        public BZ createFromParcel(Parcel source) {
            return new BZ(source);
        }

        @Override
        public BZ[] newArray(int size) {
            return new BZ[size];
        }
    };
}
