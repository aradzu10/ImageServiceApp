package com.biu.dombelskizulti.imageserviceapp.Transfer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.biu.dombelskizulti.imageserviceapp.ProgressBar.ProgressBar;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class SendPhotos implements HandleClientCommunication {

    private ProgressBar progressBar;

    public SendPhotos(ProgressBar c) {
        progressBar = c;
    }

    @Override
    public void handleCommunication(OutputStream output) {
        File dcim = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        if (dcim == null) {
            return;
        }

        File[] pics = dcim.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return (name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png") ||
                        name.endsWith(".bmp") || name.endsWith(".gif"));
            }
        });

        if (pics == null) return;

        progressBar.setLimit(pics.length);

        for (File pic : pics) {
            try {
                progressBar.increase();
                SendPhoto(output, pic);
            } catch (Exception e) { }
        }
    }

    private void SendPhoto(OutputStream output, File pic) throws Exception{
        FileInputStream fis = new FileInputStream(pic);
        Bitmap bm = BitmapFactory.decodeStream(fis);
        byte[] imgbyte = getBytesFromBitmap(bm);
        String name = pic.getName();

        output.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(name.length()).array());
        output.flush();
        output.write(name.getBytes());
        output.flush();
        output.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(imgbyte.length).array());
        output.flush();
        output.write(imgbyte);
        output.flush();
    }

    private byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

}
