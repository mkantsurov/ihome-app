package technology.positivehome.ihome.server.service.core.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import technology.positivehome.ihome.server.service.core.SysConfigImpl;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Created by maxim on 1/27/23.
 **/
public class DR404Controller {
    private static final Log log = LogFactory.getLog(SysConfigImpl.class);

    public static byte RELAYOPT=1;
    public static byte RELAYCHK=2;
    public static byte RELAYSTATE=3;
    public static byte APPQUIT=4;

    public static byte CLOSETCP=5;
    public static byte MAINMENU=6;



    protected Socket socket   = new Socket();
    //目标端口
    public boolean State;
    private byte[] sData=new byte[1024];
    public boolean sendData(byte[] data) throws IOException {
        // TODO Auto-generated method stub
        OutputStream out=socket.getOutputStream();
        if(out==null) return false;
        out.write(data);
        return true;
    }

    public boolean startConn( String  ip,int port) {
        if(socket.isClosed()) socket=new Socket();
        SocketAddress remoteAddr=new InetSocketAddress(ip,port);
        try {
            socket.connect(remoteAddr, 2000);
        } catch (IOException e) {
            socket=new Socket();
            log.debug("tcpserver" + e.getMessage());
            return false;
        }
        State=true;
        return true;
    }




    public byte[] packageCmd(byte id,byte opt)
    {

        if(id>5) return null;

        byte[] cmd=new byte[]{0x55,0x01,0x01,0,0,0,0,0};
        if(id==5)
        {
            cmd[2]=0;
        }
        else if(id==0)
        {
            cmd[3]=opt;
            cmd[4]=opt;
            cmd[5]=opt;
            cmd[6]=opt;
        }
        else cmd[2+id]=opt;
        cmd[7]=(byte)(cmd[0]+cmd[1]+cmd[2]+cmd[3]+cmd[4]+cmd[5]+cmd[6]);
        return cmd;
    }


    public void sendrelayCmd(int id,int opt)
    {
        byte[] cmd=packageCmd((byte)id,(byte)opt);
        if(cmd==null) return;
        try {
            sendData(cmd);
        } catch (IOException e) {
            log.error(e);
        }
    }

}
