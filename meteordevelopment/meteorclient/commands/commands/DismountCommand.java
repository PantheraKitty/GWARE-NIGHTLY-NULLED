package meteordevelopment.meteorclient.commands.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.class_2172;
import net.minecraft.class_2851;

public class DismountCommand extends Command {
   public DismountCommand() {
      super("dismount", "Dismounts you from entity you are riding.");
   }

   public void build(LiteralArgumentBuilder<class_2172> builder) {
      builder.executes((context) -> {
         mc.method_1562().method_52787(new class_2851(0.0F, 0.0F, false, true));
         return 1;
      });
   }
}
