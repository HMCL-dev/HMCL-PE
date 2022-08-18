package com.tungsten.hmclpe.utils.animation;

import android.content.Context;
import android.view.View;
import android.view.animation.AnimationUtils;

import com.tungsten.hmclpe.launcher.MainActivity;

public class CustomAnimationUtils {

    public static void showViewFromLeft(View view, MainActivity activity, Context context,boolean animation){
        view.setVisibility(View.VISIBLE);
        if (activity.isLoaded && animation){
            view.setAnimation(AnimationUtils.makeInAnimation(context, true));
        }
    }

    public static void hideViewToLeft(View view,MainActivity activity,Context context,boolean animation){
        view.setVisibility(View.GONE);
        if (activity.isLoaded && animation){
            view.setAnimation(AnimationUtils.makeOutAnimation(context, false));
        }
    }

    public static void showViewFromRight(View view, MainActivity activity, Context context,boolean animation){
        view.setVisibility(View.VISIBLE);
        if (activity.isLoaded && animation){
            view.setAnimation(AnimationUtils.makeInAnimation(context, false));
        }
    }

    public static void hideViewToRight(View view,MainActivity activity,Context context,boolean animation){
        view.setVisibility(View.GONE);
        if (activity.isLoaded && animation){
            view.setAnimation(AnimationUtils.makeOutAnimation(context, true));
        }
    }

    public static void showViewFromLeft(View view, Context context,boolean animation){
        view.setVisibility(View.VISIBLE);
        if (animation){
            view.setAnimation(AnimationUtils.makeInAnimation(context, true));
        }
    }

    public static void hideViewToLeft(View view,Context context,boolean animation){
        view.setVisibility(View.GONE);
        if (animation){
            view.setAnimation(AnimationUtils.makeOutAnimation(context, false));
        }
    }

    public static void showViewFromRight(View view, Context context,boolean animation){
        view.setVisibility(View.VISIBLE);
        if (animation){
            view.setAnimation(AnimationUtils.makeInAnimation(context, false));
        }
    }

    public static void hideViewToRight(View view,Context context,boolean animation){
        view.setVisibility(View.GONE);
        if (animation){
            view.setAnimation(AnimationUtils.makeOutAnimation(context, true));
        }
    }
}
