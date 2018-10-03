package kz.wasdx.wifidirect;

import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import static kz.wasdx.wifidirect.WifiManagerClass.MESSAGE_READ;
import static kz.wasdx.wifidirect.WifiManagerClass.sendData;

public class SendReceive extends Thread {
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;

    Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what){
                case MESSAGE_READ:
                    byte[] readBuffer=(byte[]) message.obj;
                    String tempMessage=new String(readBuffer,0,message.arg1);
                    sendData.setText(tempMessage);
                    break;
            }
            return true;
        }
    });

    public SendReceive(Socket skt){
        socket=skt;

        try {
            inputStream=socket.getInputStream();
            outputStream=socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run(){
        byte[] buffer=new byte[1024];
        int bytes;

        while (socket!=null){
            try {
                bytes=inputStream.read(buffer);
                if(bytes>0){
                    handler.obtainMessage(MESSAGE_READ,bytes,-1,buffer).sendToTarget();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void write(byte[] bytes){
        try {
            outputStream.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
