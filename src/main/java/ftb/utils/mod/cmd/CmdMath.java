package ftb.utils.mod.cmd;

import javax.script.*;

import net.minecraft.command.*;
import net.minecraft.util.*;

import ftb.lib.api.cmd.*;
import latmod.lib.LMStringUtils;

public class CmdMath extends CommandLM {

    private static Boolean hasEngine = null;
    private static ScriptEngine engine = null;

    public CmdMath() {
        super("math", CommandLevel.ALL);
    }

    public IChatComponent onCommand(ICommandSender ics, String[] args) throws CommandException {
        checkArgs(args, 1);

        if (hasEngine == null) {
            hasEngine = Boolean.FALSE;

            try {
                engine = new ScriptEngineManager().getEngineByName("JavaScript");
                if (engine != null) hasEngine = Boolean.TRUE;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (hasEngine == Boolean.TRUE) {
            try {
                String s = LMStringUtils.unsplit(args, " ").trim();
                Object o = engine.eval(s);
                return new ChatComponentText(String.valueOf(o));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            return error(new ChatComponentText("JavaScript Engine not found!"));
        }

        return error(new ChatComponentText("Error"));
    }
}
