package com.activity.results.manager;

import android.app.Activity;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Iterator;

public final class ActivityStackManager {

    private static ActivityStackManager sInstance = new ActivityStackManager();

    private HashSet<Activity> mStack;
    private WeakReference<Activity> sCurrentActivityWeakRef;

    public static ActivityStackManager getInstance() {
        return sInstance;
    }

    private ActivityStackManager() {
        this.mStack = new HashSet<>();
    }

    public void addStackActivity(Activity activity) {
        mStack.add(activity);
    }

    public void removeStackActivity(Activity activity) {
        mStack.remove(activity);
    }

    public void removeStackOtherActivitys(Activity notRemoveAct) {
        Iterator<Activity> iterator = mStack.iterator();
        while (iterator.hasNext()) {
            Activity act = iterator.next();
            if (!act.equals(notRemoveAct)) {
                if (act != null && !act.isFinishing()) {
                    act.finish();
                }
                iterator.remove();
            }
        }
    }

    public void clearStackActivity() {
        for (Activity activity : mStack) {
            if (activity != null && !activity.isFinishing()) {
                activity.finish();
            }
        }
        mStack.clear();
    }

    public Activity getCurrentActivity() {
        Activity currentActivity = null;
        if (sCurrentActivityWeakRef != null) {
            currentActivity = sCurrentActivityWeakRef.get();
        }
        return currentActivity;
    }

    public void setCurrentActivity(Activity activity) {
        sCurrentActivityWeakRef = new WeakReference<>(activity);
    }
}
