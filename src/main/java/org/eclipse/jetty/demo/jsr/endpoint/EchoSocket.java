package org.eclipse.jetty.demo.jsr.endpoint;

import java.util.Set;

import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

public class EchoSocket extends Endpoint implements MessageHandler.Whole<String>
{
    private static final Logger LOG = Log.getLogger(EchoSocket.class);
    private Session session;
    private RemoteEndpoint.Async remote;

    @Override
    public void onClose(Session session, CloseReason close)
    {
        super.onClose(session,close);
        this.session = null;
        this.remote = null;
        LOG.info("WebSocket Close: {} - {}",close.getCloseCode(),close.getReasonPhrase());
    }

    public void onOpen(Session session, EndpointConfig config)
    {
        this.session = session;
        this.remote = this.session.getAsyncRemote();
        LOG.info("WebSocket Connect: {}",session);
        this.remote.sendText("You are now connected to " + this.getClass().getName());
        // attach echo message handler
        session.addMessageHandler(this);
    }

    @Override
    public void onError(Session session, Throwable cause)
    {
        super.onError(session,cause);
        LOG.warn("WebSocket Error",cause);
    }

    @Override
    public void onMessage(String message)
    {
        if (this.session == null || !this.session.isOpen() || this.remote == null)
            return;

        switch (message)
        {
            case "open":
                LOG.info("Echoing back open session info [{}]", message);
                Set<Session> openSessions = session.getOpenSessions();
                int len = openSessions.size();
                this.remote.sendText(String.format("Open Sessions.size() = %d", len));

                int idx = 0;
                for (Session open : openSessions)
                {
                    this.remote.sendText(String.format("  session[%d] = %s", idx++, open));
                }
                break;
            default:
                LOG.info("Echoing back message [{}]", message);
                this.remote.sendText(message);
        }
    }
}
