package org.eclipse.jetty.demo.adapter;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;

import java.nio.ByteBuffer;

public class EchoSocket extends WebSocketAdapter
{
    private static final Logger LOG = Log.getLogger(EchoSocket.class);

    public void onWebSocketClose(int statusCode, String reason)
    {
        super.onWebSocketClose(statusCode,reason);
        LOG.info("WebSocket Close: {} - {}",statusCode,reason);
    }

    public void onWebSocketConnect(Session session)
    {
        super.onWebSocketConnect(session);
        LOG.info("WebSocket Connect: {}",session);
//        getRemote().sendStringByFuture("You are now connected to " + this.getClass().getName());
        getRemote().sendBytesByFuture(ByteBuffer.wrap("You are now connected to ".getBytes()));
    }

    public void onWebSocketError(Throwable cause)
    {
        LOG.warn("WebSocket Error",cause);
    }

    public void onWebSocketText(String message)
    {
        if (isConnected())
        {
            LOG.info("Echoing back text message [{}]",message);
            getRemote().sendStringByFuture(message);
        }
    }

    @Override
    public void onWebSocketBinary(byte[] arg0, int arg1, int arg2)
    {
        if (isConnected()){
            LOG.info("BINARY =========== Echoing back text message [{}]", new String(arg0));
            getRemote().sendBytesByFuture(ByteBuffer.wrap(arg0, arg1, arg2));
        }

    }
}
