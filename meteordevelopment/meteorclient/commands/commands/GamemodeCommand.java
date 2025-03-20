package meteordevelopment.meteorclient.commands.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.class_1934;
import net.minecraft.class_2172;

public class GamemodeCommand extends Command {
   public GamemodeCommand() {
      super("gamemode", "Changes your gamemode client-side.", "gm");
   }

   public void build(LiteralArgumentBuilder<class_2172> builder) {
      class_1934[] var2 = class_1934.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         class_1934 gameMode = var2[var4];
         builder.then(literal(gameMode.method_8381()).executes((context) -> {
            mc.field_1761.method_2907(gameMode);
            return 1;
         }));
      }

   }
}
