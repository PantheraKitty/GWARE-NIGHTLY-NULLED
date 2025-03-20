package meteordevelopment.meteorclient.commands.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.commands.arguments.MacroArgumentType;
import meteordevelopment.meteorclient.systems.macros.Macro;
import net.minecraft.class_2172;

public class MacroCommand extends Command {
   public MacroCommand() {
      super("macro", "Allows you to execute macros.");
   }

   public void build(LiteralArgumentBuilder<class_2172> builder) {
      builder.then(argument("macro", MacroArgumentType.create()).executes((context) -> {
         Macro macro = MacroArgumentType.get(context);
         macro.onAction();
         return 1;
      }));
   }
}
