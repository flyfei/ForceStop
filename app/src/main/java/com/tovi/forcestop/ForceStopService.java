package com.tovi.forcestop;

import android.accessibilityservice.AccessibilityService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import com.jaredrummler.android.processes.AndroidProcesses;
import com.jaredrummler.android.processes.models.AndroidAppProcess;

import java.util.LinkedList;
import java.util.List;

/**
 * @author <a href='mailto:zhaotengfei9@gmail.com'>Tengfei Zhao</a>
 */

public class ForceStopService extends AccessibilityService {
    private static final String TEXT_FORCE_STOP = "强行停止";
    private static final String TEXT_DETERMINE = "确定";
    private static final CharSequence NAME_ALERT_DIALOG = "android.app.AlertDialog";
    private static final String PACKAGE = "package";
    private SharedPrefs sharedPrefs;
    private int foregroundPushId = 1;

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPrefs = new SharedPrefs(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            if (packageList.isEmpty()) {
                init();
                forceStopNextApp();
            }
            Notification notification = new Notification.Builder(this)
                    .setContentText("运行中...")
                    .setSmallIcon(R.drawable.ic_point)
                    .setOngoing(true)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setContentIntent(PendingIntent.getService(this, 0, new Intent(this, ForceStopService.class), PendingIntent.FLAG_ONE_SHOT))
                    .build();
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
            notificationManager.notify(foregroundPushId, notification);
        }
        return START_STICKY;
    }

    private boolean isOpen = false;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (!isOpen) return;

        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            if (NAME_ALERT_DIALOG.equals(event.getClassName())) {
                clickBtn(event, TEXT_DETERMINE);
            } else if (!clickBtn(event, TEXT_FORCE_STOP)) {
                performGlobalAction(GLOBAL_ACTION_BACK);
                forceStopNextApp();
            }
        }
    }

    @Override
    public void onInterrupt() {
    }

    LinkedList<String> packageList = new LinkedList<>();

    private void init() {
        isOpen = true;
        packageList.clear();
        List<AndroidAppProcess> processes = AndroidProcesses.getRunningAppProcesses();
        if (!processes.isEmpty()) {
            String packageName;
            for (AndroidAppProcess process : processes) {
                packageName = process.getPackageName();
                System.out.println(packageName);
                if (sharedPrefs.isBlackApp(packageName) && !packageList.contains(packageName)) {
                    packageList.add(packageName);
                }
            }
        }
        System.out.println("=====" + packageList);
    }


    private void forceStopNextApp() {
        String packageName = getNextPackageName();
        if (!TextUtils.isEmpty(packageName)) {
            showPackageDetail(packageName);
        } else {
            isOpen = false;
            Toast.makeText(this, "Clean completed", Toast.LENGTH_SHORT).show();
        }
    }

    private String getNextPackageName() {
        if (!packageList.isEmpty()) {
            return packageList.removeFirst();
        }
        return null;
    }

    private boolean clickBtn(AccessibilityEvent event, String name) {
        System.out.println("name:" + name);
        List<AccessibilityNodeInfo> list;
        if (event != null && event.getSource() != null) {
            list = event.getSource().findAccessibilityNodeInfosByText(name);
        } else
            list = getRootInActiveWindow().findAccessibilityNodeInfosByText(name);

        boolean isClick = false;
        if (list != null && !list.isEmpty()) {
            for (AccessibilityNodeInfo nodeInfo : list) {
                if ("android.widget.Button".equals(nodeInfo.getClassName()) && nodeInfo.isEnabled()) {
                    nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    isClick = true;
                }
                nodeInfo.recycle();
            }
        }
        return isClick;
    }

    private void showPackageDetail(String packageName) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts(PACKAGE, packageName, null);
        intent.setData(uri);
        startActivity(intent);
    }
}
