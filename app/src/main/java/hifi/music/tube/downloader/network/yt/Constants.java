package hifi.music.tube.downloader.network.yt;

import android.content.Context;
import android.preference.PreferenceManager;

import org.schabi.newpipe.extractor.localization.ContentCountry;
import org.schabi.newpipe.extractor.localization.Localization;

public class Constants {
    public static final int NO_SERVICE_ID = -1;

    public static org.schabi.newpipe.extractor.localization.Localization getPreferredLocalization(final Context context) {
        final String contentLanguage = PreferenceManager
                .getDefaultSharedPreferences(context)
                .getString("content_language",
                        "en");
        return Localization.fromLocalizationCode(contentLanguage).get();
    }

    public static ContentCountry getPreferredContentCountry(final Context context) {
        final String contentCountry = PreferenceManager
                .getDefaultSharedPreferences(context)
                .getString("content_country",
                        "GB");
        return new ContentCountry(contentCountry);
    }


}
