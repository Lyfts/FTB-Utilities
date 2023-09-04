package ftb.utils.mod.cmd.admin;

import java.util.*;

import net.minecraft.command.*;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.*;
import net.minecraft.world.*;
import net.minecraftforge.common.*;

import com.google.common.collect.ImmutableSetMultimap;

import ftb.lib.api.cmd.*;
import ftb.utils.api.guide.GuidePage;
import latmod.lib.IntList;

public class CmdServerInfo extends CommandLM {

    public CmdServerInfo() {
        super("server_info", CommandLevel.OP);
    }

    public IChatComponent onCommand(ICommandSender ics, String[] args) throws CommandException {
        EntityPlayerMP ep = getCommandSenderAsPlayer(ics);

        GuidePage file = new GuidePage("server_info").setTitle(new ChatComponentText("Server Info")); // LANG

        GuidePage page = file.getSub("Entities"); // LANG

        Set<Integer> entityIDset = EntityList.IDtoClassMapping.keySet();
        for (Integer i : entityIDset) page.printlnText("[" + i + "] " + EntityList.getStringFromID(i.intValue()));

        page = file.getSub("Enchantments"); // LANG

        IntList freeIDs = new IntList();

        for (int i = 0; i < 256; i++) {
            Enchantment e = Enchantment.enchantmentsList[i];
            if (e == null) freeIDs.add(i);
            else page.printlnText("[" + i + "] " + e.getTranslatedName(1));
        }

        page.printlnText("Empty IDs: " + freeIDs.toString());

        page = file.getSub("loaded_chunks").setTitle(new ChatComponentText("Loaded Chunks")); // LANG

        for (WorldServer w : DimensionManager.getWorlds()) {
            ImmutableSetMultimap<ChunkCoordIntPair, ForgeChunkManager.Ticket> map = ForgeChunkManager
                    .getPersistentChunksFor(w);

            Map<String, List<ChunkCoordIntPair>> chunksMap = new HashMap<>();

            for (ForgeChunkManager.Ticket t : map.values()) {
                List<ChunkCoordIntPair> list = chunksMap.get(t.getModId());
                if (list == null) chunksMap.put(t.getModId(), list = new ArrayList<>());
                for (ChunkCoordIntPair c : t.getChunkList()) if (!list.contains(c)) list.add(c);
            }

            GuidePage dim = page.getSub(w.provider.getDimensionName());

            for (Map.Entry<String, List<ChunkCoordIntPair>> e1 : chunksMap.entrySet()) {
                GuidePage mod = dim.getSub(e1.getKey() + " [" + e1.getValue().size() + "]");
                for (ChunkCoordIntPair c : e1.getValue()) mod.printlnText(
                        c.chunkXPos + ", "
                                + c.chunkZPos
                                + " [ "
                                + c.getCenterXPos()
                                + ", "
                                + c.getCenterZPosition()
                                + " ]");
            }
        }

        file.displayGuide(ep);
        return null;
    }
}
