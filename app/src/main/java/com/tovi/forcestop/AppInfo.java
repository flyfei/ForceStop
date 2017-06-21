package com.tovi.forcestop;

import android.graphics.drawable.Drawable;

/**
 * @author <a href='mailto:zhaotengfei9@gmail.com'>Tengfei Zhao</a>
 */

public class AppInfo {
    public String appName;
    public String appPName;
    public Drawable appLogo;

    public AppInfo(String appName, String appPName, Drawable appLogo) {
        this.appName = appName;
        this.appPName = appPName;
        this.appLogo = appLogo;
    }
}
