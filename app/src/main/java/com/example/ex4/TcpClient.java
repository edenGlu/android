package com.example.ex4;

import android.util.Log;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;


public class TcpClient {
    public static final String TAG = TcpClient.class.getSimpleName();
    public String SERVER_IP; //server IP address
    public int SERVER_PORT;

    // used to send messages
    private PrintWriter mBufferOut;
    private Socket socket;


    /**
     * Constructor of the class. OnMessagedReceived listens for the messages received from server
     */
    public TcpClient(String ip, int port) {
        SERVER_IP = ip;
        SERVER_PORT = port;
        connect();
    }

    /**
     * Sends the message entered by client to the server
     *
     * @param message text entered by client
     */
    public void sendMessage(final String message) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (mBufferOut != null) {
                    Log.d(TAG, "Sending: " + message);
                    mBufferOut.println(message);
                    mBufferOut.flush();
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    /**
     * Close the connection and release the members
     */
    public void stopClient() {

        if (mBufferOut != null) {
            mBufferOut.flush();
            mBufferOut.close();
        }
        mBufferOut = null;
        try {
            socket.close();
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
    }

    public void connect() {

        try {

            Thread thread = new Thread(new Runnable() {

                @Override
                public void run() {
                    try  {
                        socket = new Socket();
                        socket.connect(new InetSocketAddress(InetAddress.getByName(SERVER_IP), SERVER_PORT), 2000);
                        Log.d("TCP Client", "C: Connecting...");

                        //create a socket to make the connection with the server
                        //sends the message to the server
                        mBufferOut = new PrintWriter(new BufferedWriter
                                (new OutputStreamWriter(socket.getOutputStream())), true);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            thread.start();


        } catch (Exception e) {
            Log.e("TCP", "C: Error", e);
            stopClient();
        }
    }

}
