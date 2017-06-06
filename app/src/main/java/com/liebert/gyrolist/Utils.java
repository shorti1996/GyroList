package com.liebert.gyrolist;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.util.Pair;
import android.view.WindowManager;

import static android.content.Context.WINDOW_SERVICE;

/**
 * Created by shorti1996 on 04.06.2017.
 */

public class Utils {
    //http://developer.download.nvidia.com/tegra/docs/tegra_android_accelerometer_v5f.pdf
    //page 8
    static float[] canonicalOrientationToScreenOrientation(
            int displayRotation, float[] canVec)
    {
        final int axisSwap[][] = {
                {  1,  -1,  0,  1  },     // ROTATION_0
                { -1,  -1,  1,  0  },     // ROTATION_90
                { -1,   1,  0,  1  },     // ROTATION_180
                {  1,   1,  1,  0  }  }; // ROTATION_270

        final int[] as = axisSwap[displayRotation];
        float[] screenVec = new float[3];
        screenVec[0]  =  (float)as[0] * canVec[ as[2] ];
        screenVec[1]  =  (float)as[1] * canVec[ as[3] ];
        screenVec[2]  =  canVec[2];
        return screenVec;
    }

    static float[] canonicalOrientationToScreenOrientation(float[] canVec, Context context)
    {
        final int axisSwap[][] = {
                {  1,  -1,  0,  1  },     // ROTATION_0
                { -1,  -1,  1,  0  },     // ROTATION_90
                { -1,   1,  0,  1  },     // ROTATION_180
                {  1,   1,  1,  0  }  }; // ROTATION_270

        final int[] as = axisSwap[getDisplayOrientation(context)];
        float[] screenVec = new float[3];
        screenVec[0]  =  (float)as[0] * canVec[ as[2] ];
        screenVec[1]  =  (float)as[1] * canVec[ as[3] ];
        screenVec[2]  =  canVec[2];
        return screenVec;
    }

    static float[] canonicalToWorld(int displayOrientation, float[] canVec) {
        final int axisSwap[][] = {
                {  1,  1,  0,  1  },     // ROTATION_0
                { -1,  1,  1,  0  },     // ROTATION_90
                { -1, -1,  0,  1  },     // ROTATION_180
                {  1, -1,  1,  0  }  }; // ROTATION_270
        final int[] as = axisSwap[displayOrientation];
        float[] worldVec = new float[3];
        worldVec[0]  =  (float)as[0] * canVec[ as[2] ];
        worldVec[1]  =  (float)as[1] * canVec[ as[3] ];
        worldVec[2]  =  canVec[2];
        return worldVec;
    }

    static float[] normalizeVector(float[] vector) {
        float[] toRet = new float[3];
        float norm;

        float sqrt = (float) Math.sqrt(vector[0]*vector[0] + vector[1]*vector[1] + vector[2]*vector[2]);
        if (sqrt == 0f) {
            toRet[0] = 0f;
            toRet[1] = 0f;
            toRet[2] = 0f;
        } else {
            norm = (float) (1.0/Math.sqrt(vector[0]*vector[0] + vector[1]*vector[1] + vector[2]*vector[2]));
            toRet[0] = vector[0]*norm;
            toRet[1] = vector[1]*norm;
            toRet[2] = vector[2]*norm;
        }
        return toRet;
    }

    static float dotVector(float[] vector1, float[] vector2) {
        return (vector1[0]*vector2[0] + vector1[1]*vector2[1] + vector1[2]*vector2[2]);
    }

    static float lengthVector(float[] v1) {
        return (float) Math.sqrt(v1[0]*v1[0] + v1[1]*v1[1] + v1[2]*v1[2]);
    }

    static float angleBetweenVectors(float[] v1, float[] v2) {
        double vDot = dotVector(v1, v2) / ( lengthVector(v1)*lengthVector(v2) );
        if( vDot < -1.0) vDot = -1.0;
        if( vDot >  1.0) vDot =  1.0;
        return((float) (Math.acos( vDot )));
    }

    static float[] crossVectors(float[] v1, float[] v2)
    {
        float x, y;
        x = v1[1]*v2[2] - v1[2]*v2[1];
        y = v2[0]*v1[2] - v2[2]*v1[0];

        float[] toRet = new float[3];
        toRet[2] = v1[0]*v2[1] - v1[1]*v2[0];
        toRet[0] = x;
        toRet[1] = y;
        return toRet;
    }


    static Pair computeAxisAngle(float[] localUp, float[] worldVec) {
        float[] nTarget = normalizeVector(worldVec);
        float[] rotAxis = crossVectors(localUp, nTarget);
        rotAxis = normalizeVector(rotAxis);
        float ang = angleBetweenVectors(localUp, nTarget);
        return new Pair(rotAxis, ang);
    }

    static int getDisplayOrientation(Context context) {
        WindowManager windowMgr =
                (WindowManager)context.getSystemService(WINDOW_SERVICE);
        return windowMgr.getDefaultDisplay().getRotation();
    }

    static Bitmap getAppIconBitmap(Drawable icon) {
        Bitmap APKicon;
        if(icon instanceof BitmapDrawable) {
            APKicon  = ((BitmapDrawable)icon).getBitmap();
        }
        else {
            Bitmap bitmap = Bitmap.createBitmap(icon.getIntrinsicWidth(),icon.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            icon.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            icon.draw(canvas);
            APKicon = bitmap;
        }
        return APKicon;
    }
}
