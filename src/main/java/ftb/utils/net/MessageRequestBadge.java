package ftb.utils.net;

import java.util.UUID;

import cpw.mods.fml.common.network.simpleimpl.*;
import ftb.lib.api.net.LMNetworkWrapper;
import ftb.utils.badges.*;
import latmod.lib.ByteCount;

public class MessageRequestBadge extends MessageFTBU {

    public MessageRequestBadge() {
        super(ByteCount.BYTE);
    }

    public MessageRequestBadge(UUID id) {
        this();
        io.writeUUID(id);
    }

    public LMNetworkWrapper getWrapper() {
        return FTBUNetHandler.NET_INFO;
    }

    public IMessage onMessage(MessageContext ctx) {
        UUID id = io.readUUID();
        Badge b = ServerBadges.getServerBadge(id);
        return (b == null || b == Badge.emptyBadge) ? null : new MessageSendBadge(id, b.getID());
    }
}
