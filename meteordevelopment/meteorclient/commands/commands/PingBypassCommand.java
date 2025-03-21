package meteordevelopment.meteorclient.commands.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.misc.PingBypassModule;
import net.minecraft.class_2172;
import net.minecraft.class_642;
import net.minecraft.class_642.class_8678;

public class PingBypassCommand extends Command {
   public PingBypassCommand() {
      super("pingbypass", "Controls the PingBypass module.");
   }

   public void build(LiteralArgumentBuilder<class_2172> builder) {
      builder.then(literal("toggle").executes((context) -> {
         PingBypassModule module = (PingBypassModule)Modules.get().get(PingBypassModule.class);
         module.toggle();
         return 1;
      }));
      builder.then(literal("set-server").then(argument("address", StringArgumentType.greedyString()).executes((context) -> {
         PingBypassModule module = (PingBypassModule)Modules.get().get(PingBypassModule.class);
         String address = StringArgumentType.getString(context, "address");
         module.setOriginalServerInfo(new class_642("PingBypassTarget", address, class_8678.field_45611));
         return 1;
      })));
   }
}
