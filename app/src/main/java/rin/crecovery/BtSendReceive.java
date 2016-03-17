package rin.crecovery;

import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;

public class BtSendReceive {

    private BluetoothSocket btSocket;
    private InputStream btInputStream = null;
    private OutputStream btOutputStream = null;



    public BtSendReceive(BluetoothSocket socket) {
        btSocket = socket;

        try {
            btInputStream = btSocket.getInputStream();
            btOutputStream = btSocket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public byte[] read() throws InterruptedException, ExecutionException {
        RunnableFuture future = new FutureTask(new Callable<byte[]>() {
            @Override
            public byte[] call() throws Exception {
                int bytes = 0;
                byte[] buffer = new byte[8];

                while(bytes < 8 || bytes != -1) {
                    try {
                        bytes = btInputStream.read(buffer);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                return buffer;
            }
        });

        new Thread(future).start();
        return (byte[]) future.get();
    }

    public void send(String... data) {
        if (btSocket != null) {
            try {
                for (String d : data) {
                    btSocket.getOutputStream().write(d.getBytes());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void send(byte[] bytes) {
        if (btSocket != null) {
            try {
                btSocket.getOutputStream().write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void disconnectBt() {
        if (btSocket != null) {
            try {
                btSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static byte[] toByteArray(double value) {
        byte[] bytes = new byte[8];
        ByteBuffer.wrap(bytes).putDouble(value);
        return bytes;
    }

    public static double toDouble(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getDouble();
    }

}
