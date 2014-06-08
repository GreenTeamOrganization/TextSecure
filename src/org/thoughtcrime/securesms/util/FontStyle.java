package org.thoughtcrime.securesms.util;

import org.thoughtcrime.securesms.R;

/**
 * Sets a variety of options for Font Size at settings.
 *
 * @author Fotis
 */
public enum FontStyle{
    Small(R.style.FontStyle_Small, "Small"),
    Medium(R.style.FontStyle_Medium, "Medium"),
    Large(R.style.FontStyle_Large, "Large");

    private int resId;
    private String title;

    public int getResId() {
        return resId;
    }

    public String getTitle() {
        return title;
    }

    FontStyle(int resId, String title) {
        this.resId = resId;
        this.title = title;
    }
}
