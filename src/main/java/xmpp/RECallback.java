package xmpp;

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smackx.mam.element.MamElements;

import java.util.List;
import java.util.Set;

public interface RECallback {
    void setRE(Set<RosterEntry> rosterEntries );
    void setLM(List<MamElements.MamResultExtension> ak, List<Message> messages);
    void newMsg(String username,Message msg);
}
