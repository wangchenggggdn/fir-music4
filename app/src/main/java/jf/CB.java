package jf;

import android.os.Parcel;

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;

/**
 * Created by liyanju on 2018/5/7.
 */

public class CB implements SearchSuggestion {

    private String suggistion;

    public CB(String suggistion) {
        this.suggistion = suggistion;
    }

    public CB(Parcel in) {
        suggistion = in.readString();
    }

    @Override
    public String getBody() {
        return suggistion;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(suggistion);
    }

    public static final Creator<CB> CREATOR = new Creator<CB>() {
        @Override
        public CB createFromParcel(Parcel source) {
            return new CB(source);
        }

        @Override
        public CB[] newArray(int size) {
            return new CB[size];
        }
    };
}
