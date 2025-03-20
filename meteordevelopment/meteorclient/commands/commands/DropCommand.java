package meteordevelopment.meteorclient.commands.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2172;
import net.minecraft.class_2287;
import net.minecraft.class_2561;
import net.minecraft.class_746;

public class DropCommand extends Command {
   private static final SimpleCommandExceptionType NOT_SPECTATOR = new SimpleCommandExceptionType(class_2561.method_43470("Can't drop items while in spectator."));
   private static final SimpleCommandExceptionType NO_SUCH_ITEM = new SimpleCommandExceptionType(class_2561.method_43470("Could not find an item with that name!"));

   public DropCommand() {
      super("drop", "Automatically drops specified items.");
   }

   public void build(LiteralArgumentBuilder<class_2172> builder) {
      builder.then(literal("hand").executes((context) -> {
         return this.drop((player) -> {
            player.method_7290(true);
         });
      }));
      builder.then(literal("offhand").executes((context) -> {
         return this.drop((player) -> {
            InvUtils.drop().slotOffhand();
         });
      }));
      builder.then(literal("hotbar").executes((context) -> {
         return this.drop((player) -> {
            for(int i = 0; i < 9; ++i) {
               InvUtils.drop().slotHotbar(i);
            }

         });
      }));
      builder.then(literal("inventory").executes((context) -> {
         return this.drop((player) -> {
            for(int i = 9; i < player.method_31548().field_7547.size(); ++i) {
               InvUtils.drop().slotMain(i - 9);
            }

         });
      }));
      builder.then(literal("all").executes((context) -> {
         return this.drop((player) -> {
            for(int i = 0; i < player.method_31548().method_5439(); ++i) {
               InvUtils.drop().slot(i);
            }

            InvUtils.drop().slotOffhand();
         });
      }));
      builder.then(literal("armor").executes((context) -> {
         return this.drop((player) -> {
            for(int i = 0; i < player.method_31548().field_7548.size(); ++i) {
               InvUtils.drop().slotArmor(i);
            }

         });
      }));
      builder.then(argument("item", class_2287.method_9776(REGISTRY_ACCESS)).executes((context) -> {
         return this.drop((player) -> {
            class_1799 stack = class_2287.method_9777(context, "item").method_9781(1, false);
            if (stack != null && stack.method_7909() != class_1802.field_8162) {
               for(int i = 0; i < player.method_31548().method_5439(); ++i) {
                  if (stack.method_7909() == player.method_31548().method_5438(i).method_7909()) {
                     InvUtils.drop().slot(i);
                  }
               }

            } else {
               throw NO_SUCH_ITEM.create();
            }
         });
      }));
   }

   private int drop(DropCommand.PlayerConsumer consumer) throws CommandSyntaxException {
      if (mc.field_1724.method_7325()) {
         throw NOT_SPECTATOR.create();
      } else {
         consumer.accept(mc.field_1724);
         return 1;
      }
   }

   @FunctionalInterface
   private interface PlayerConsumer {
      void accept(class_746 var1) throws CommandSyntaxException;
   }
}
