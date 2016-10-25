package com.example.hiep.android_loadbitmap.imageloader;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.example.hiep.android_loadbitmap.system.MyApp;

import java.lang.ref.WeakReference;

/**
 * Created by Hiep on 10/25/2016.
 */
public class MyImageLoader {

    public void loadBitmap(Resources resources,int resId, ImageView imageView,Bitmap placeHolderBitmap){
        if (cancelPotentialWork(resId,imageView)){
            final BitmapWorkerTask bitmapWorkerTask=new BitmapWorkerTask(imageView);
            //hiển thị ảnh holder và lưu bitmapWorkerTask và weakReference
            final AsynDrawable asynDrawable=new AsynDrawable(resources,placeHolderBitmap,bitmapWorkerTask);
            imageView.setImageDrawable(asynDrawable);
            //Run bitmapWorkerTask
            bitmapWorkerTask.execute(resId);
        }
    }

    //sử dụng để check xem việc cancel một task có cần thực hiện hay không
    public static boolean cancelPotentialWork(int data,ImageView imageView){
        final BitmapWorkerTask bitmapWorkerTask=getBitmapWorkerTask(imageView);
        if (bitmapWorkerTask!=null){
            final int bitmapData=bitmapWorkerTask.data;
            // If bitmapData is not yet set or it differs from the new data
            if (bitmapData==0 || bitmapData !=data){
                bitmapWorkerTask.cancel(true);
            }else {
                return false;
            }
        }
        return true;
    }

    private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView){
        if (imageView!=null){
            final Drawable drawable=imageView.getDrawable();
            if (drawable instanceof AsynDrawable){
                final  AsynDrawable asynDrawable=(AsynDrawable)drawable;
                return asynDrawable.getBitmapWorkerTask();
            }
        }

        return null;
    }

    static class AsynDrawable extends BitmapDrawable{

        private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskWeakReference;
        public AsynDrawable(Resources res, Bitmap bitmap, BitmapWorkerTask bitmapWorkerTask){
            super(res, bitmap);
            bitmapWorkerTaskWeakReference=new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
        }

        public BitmapWorkerTask getBitmapWorkerTask(){
            return bitmapWorkerTaskWeakReference.get();
        }
    }


    public class BitmapWorkerTask extends AsyncTask<Integer,Void,Bitmap> {
       // Chúng ta sử dụng WeakReference ở đây để chắc chắn rằng ImageView có thể được Garbage Collection thu hồi bất kỳ lúc nào và sẽ không gây ra leak memory.
        private final WeakReference<ImageView> imageViewWeakReference;
        private  int data=0;

        public BitmapWorkerTask(ImageView imageView) {
            imageViewWeakReference=new WeakReference<ImageView>(imageView);
        }

        @Override
        protected Bitmap doInBackground(Integer... integers) {
            data=integers[0];
            return BitmapUtils.decodeSampledBitmapFromResource(MyApp.getInstance().getResources(),data,100,100);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (isCancelled()){
                bitmap=null;
            }

            if (imageViewWeakReference !=null && bitmap!=null){
                ImageView imageView=imageViewWeakReference.get();
                final BitmapWorkerTask bitmapWorkerTask=getBitmapWorkerTask(imageView);
                if (this==bitmapWorkerTask && imageView!=null){
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }

}
