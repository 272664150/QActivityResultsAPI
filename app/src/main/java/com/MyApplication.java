package com;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.activity.results.manager.ActivityStackManager;

public class MyApplication extends Application {

    public static Application mApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
        registerActivityLifecycleCallbacks(new GlobalActivityLifecycleCallbacks());
    }

    private class GlobalActivityLifecycleCallbacks implements ActivityLifecycleCallbacks {

        @Override
        public void onActivityCreated(Activity activity, Bundle bundle) {
            ActivityStackManager.getInstance().setCurrentActivity(activity);
            ActivityStackManager.getInstance().addStackActivity(activity);
        }

        @Override
        public void onActivityStarted(Activity activity) {

        }

        @Override
        public void onActivityResumed(Activity activity) {

        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            ActivityStackManager.getInstance().removeStackActivity(activity);
        }
    }
}
