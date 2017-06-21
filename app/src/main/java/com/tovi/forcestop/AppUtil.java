package com.tovi.forcestop;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href='mailto:zhaotengfei9@gmail.com'>Tengfei Zhao</a>
 */

public class AppUtil {
    private static final List<String> PACKAGE_PREFIX = new ArrayList<>();
    private static final String CURRENT_PACKAGE = "com.tovi.forcestop";

    static {
        PACKAGE_PREFIX.add(CURRENT_PACKAGE);
//        PACKAGE_PREFIX.add("com.tencent.mm");
//        PACKAGE_PREFIX.add("com.internet.speed.meter.lite");
    }

    public static List<AppInfo> getLocalApp(PackageManager packageManager) {
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        List<AppInfo> list = new ArrayList<>();
        for (PackageInfo packageInfo : packageInfos) {
            //过滤掉系统app
            if ((ApplicationInfo.FLAG_SYSTEM & packageInfo.applicationInfo.flags) != 0) {
                continue;
            }
            if (packageInfo.applicationInfo.loadIcon(packageManager) == null) {
                continue;
            }
            final String packageName = packageInfo.packageName;

            if (PACKAGE_PREFIX.contains(packageName)) continue;

            list.add(new AppInfo(packageInfo.applicationInfo.loadLabel(
                    packageManager).toString(), packageName, packageInfo.applicationInfo
                    .loadIcon(packageManager)));
        }
        return list;
    }
}
