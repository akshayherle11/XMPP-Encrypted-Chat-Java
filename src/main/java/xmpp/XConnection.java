package xmpp;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntries;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.mam.MamManager;
import org.jivesoftware.smackx.mam.element.MamElements;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public class XConnection {
    RECallback reHandel;
    String username,password;
    ChatManager chatManager;
    MamManager mamManager;
    public XConnection(RECallback reHandel) {
        this.reHandel = reHandel;

    }


    public void sendMsg(String username, String msg) throws XmppStringprepException, SmackException.NotConnectedException, InterruptedException {
        Jid jid = JidCreate.from(username);
        Chat chat = chatManager.chatWith(jid.asEntityBareJidIfPossible());
        chat.send(msg);

    }


    public void loadmsg(String username) throws XmppStringprepException, XMPPException.XMPPErrorException, SmackException.NotConnectedException, SmackException.NoResponseException, SmackException.NotLoggedInException, InterruptedException {
        Jid jid = JidCreate.bareFrom(username);

        mamManager = MamManager.getInstanceFor(xmppConnection);
        MamManager.MamQueryArgs mamQueryArgs = MamManager.MamQueryArgs.builder()
                .limitResultsToJid(jid)
                .setResultPageSize(100)
                .queryLastPage()
                .build();

        MamManager.MamQuery mamQuery = mamManager.queryArchive(mamQueryArgs);

        List<MamElements.MamResultExtension > ak = mamQuery.getMamResultExtensions();
        reHandel.setLM(ak,mamQuery.getMessages());
    }


    public AbstractXMPPConnection xmppConnection;
    public Set<RosterEntry> rosterEntries ;
    public Roster roster;
    boolean conneect = false;
    public IncomingChatMessageListener incomingChatMessageListener = new IncomingChatMessageListener() {
        @Override
        public void newIncomingMessage(EntityBareJid from, Message message, Chat chat) {
            reHandel.newMsg(from.toString(),message);
        }
    };
    public synchronized boolean connect()
    {
        conneect=false;

        try {
            xmppConnection = new XMPPTCPConnection(getConfig().build());
            xmppConnection.connect();
            xmppConnection.login();
            chatManager = ChatManager.getInstanceFor(xmppConnection);
            chatManager.addIncomingListener(incomingChatMessageListener);

            conneect = true;
        } catch (XmppStringprepException e) {
            e.printStackTrace();
        } catch (SmackException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XMPPException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return conneect;

    }



    public void loadroster() throws SmackException.NotConnectedException, SmackException.NotLoggedInException, InterruptedException {
     if(conneect) {
         roster = Roster.getInstanceFor(xmppConnection);
         roster.reloadAndWait();
         rosterEntries =  roster.getEntries();
         reHandel.setRE(rosterEntries);
     }
    }



    public void setUP(String username,String password)
    {
        this.username=username;
        this.password=password;
    }
    public XMPPTCPConnectionConfiguration.Builder getConfig() throws XmppStringprepException {

        XMPPTCPConnectionConfiguration.Builder builder = XMPPTCPConnectionConfiguration.builder();

        builder.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
        builder.setHost("xmppproject.xyz");
        builder.setCompressionEnabled(true);
        builder.setPort(5222);
        builder.setUsernameAndPassword(username,password);
        try {
            builder.setResource("Akshay");
            builder.setXmppDomain("xmppproject.xyz");
        } catch (XmppStringprepException e) {
            e.printStackTrace();}
        return builder;

    }



}
