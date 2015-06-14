package de.moritzs.glass2;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 * Created by moritz on 13.06.15.
 */
public class FetchAngleRunnable implements Runnable {
    private double angle;
    public double getAngle() {
        return angle;
    }

    public void run() {
        while (true) {
            Socket socket = null;
            try {
                socket = new Socket("192.168.2.236", 12345);
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }

            ByteArrayOutputStream byteArrayOutputStream =
                    new ByteArrayOutputStream(1024);
            byte[] buffer = new byte[1024];

            int bytesRead;
            InputStream inputStream = null;
            try {
                inputStream = socket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String response = "";

            try {
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    byteArrayOutputStream.write(buffer, 0, bytesRead);
                    response += byteArrayOutputStream.toString("UTF-8");
                }
                try {
                    angle = Float.parseFloat(response);
                } catch(NumberFormatException e) {
                    Log.w("Invalid Float. Ignoring", "Runnable");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(10, 0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
