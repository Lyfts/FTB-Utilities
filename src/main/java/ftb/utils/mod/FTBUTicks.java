package ftb.utils.mod;

import net.minecraft.util.*;

import ftb.lib.*;
import ftb.utils.badges.ServerBadges;
import ftb.utils.mod.cmd.admin.CmdRestart;
import ftb.utils.mod.config.*;
import ftb.utils.mod.handlers.FTBUChunkEventHandler;
import ftb.utils.world.Backups;
import latmod.lib.*;

public class FTBUTicks {

    public static long nextChunkloaderUpdate = 0L;

    private static long startMillis = 0L;
    private static String lastRestartMessage = "";
    public static long restartMillis = 0L;

    public static void serverStarted() {
        startMillis = LMUtils.millis();
        Backups.nextBackup = startMillis + FTBUConfigBackups.backupMillis();

        if (FTBUConfigGeneral.restart_timer.getAsDouble() > 0D) {
            restartMillis = startMillis + (long) (FTBUConfigGeneral.restart_timer.getAsDouble() * 3600D * 1000D);
            FTBU.logger.info("Server restart in " + LMStringUtils.getTimeString(restartMillis));
        }

        nextChunkloaderUpdate = startMillis + 10000L;
    }

    public static void serverStopped() {
        startMillis = restartMillis = 0L;
    }

    public static void update() {
        long now = LMUtils.millis();

        if (restartMillis > 0L) {
            int secondsLeft = (int) ((restartMillis - LMUtils.millis()) / 1000L);

            if (secondsLeft <= 0) {
                CmdRestart.restart();
                return;
            } else {
                String msg = LMStringUtils.getTimeString(secondsLeft * 1000L);
                if (msg != null && !lastRestartMessage.equals(msg)) {
                    lastRestartMessage = msg;

                    if (secondsLeft <= 10 || secondsLeft == 30
                            || secondsLeft == 60
                            || secondsLeft == 300
                            || secondsLeft == 600
                            || secondsLeft == 1800) {
                        IChatComponent c = FTBU.mod.chatComponent("server_restart", msg);
                        c.getChatStyle().setColor(EnumChatFormatting.LIGHT_PURPLE);
                        FTBLib.printChat(BroadcastSender.inst, c);
                    }
                }
            }
        }

        if (Backups.nextBackup > 0L && Backups.nextBackup <= now) {
            Backups.run(FTBLib.getServer());
        }

        if (nextChunkloaderUpdate < now) {
            nextChunkloaderUpdate = now + 30000L;
            FTBUChunkEventHandler.instance.markDirty(null);
        }

        if (Backups.thread != null && Backups.thread.isDone) {
            Backups.thread = null;
            Backups.postBackup();
        }

        if (ServerBadges.thread != null && ServerBadges.thread.isDone) {
            ServerBadges.thread = null;
            ServerBadges.sendToPlayer(null);
        }
    }
}
