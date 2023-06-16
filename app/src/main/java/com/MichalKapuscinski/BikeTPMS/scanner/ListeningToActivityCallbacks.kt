package com.MichalKapuscinski.BikeTPMS.scanner

import android.app.Activity
import android.app.Application
import android.os.Bundle

class ListeningToActivityCallbacks : Application.ActivityLifecycleCallbacks {

        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            //"${activity.localClassName} is onActivityCreated".printt()
        }

        override fun onActivityStarted(activity: Activity) {
            //"${activity.localClassName} is onActivityStarted".printt()
        }

        override fun onActivityResumed(activity: Activity) {
            //"${activity.localClassName} is onActivityResumed".printt()
        }

        override fun onActivityPaused(activity: Activity) {
            //"${activity.localClassName} is onActivityPaused".printt()
        }

        override fun onActivityStopped(activity: Activity) {
            //"${activity.localClassName} is onActivityStopped".printt()
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
            //"${activity.localClassName} is onActivitySaveInstanceState".printt()
        }

        override fun onActivityDestroyed(activity: Activity) {
            //"${activity.localClassName} is onActivityDestroyed".printt()
        }

    }
