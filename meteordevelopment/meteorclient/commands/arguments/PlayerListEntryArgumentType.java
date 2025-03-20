package meteordevelopment.meteorclient.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import meteordevelopment.meteorclient.MeteorClient;
import net.minecraft.class_2172;
import net.minecraft.class_2561;
import net.minecraft.class_640;

public class PlayerListEntryArgumentType implements ArgumentType<class_640> {
   private static final PlayerListEntryArgumentType INSTANCE = new PlayerListEntryArgumentType();
   private static final DynamicCommandExceptionType NO_SUCH_PLAYER = new DynamicCommandExceptionType((name) -> {
      return class_2561.method_43470("Player list entry with name " + String.valueOf(name) + " doesn't exist.");
   });
   private static final Collection<String> EXAMPLES = List.of("seasnail8169", "MineGame159");

   public static PlayerListEntryArgumentType create() {
      return INSTANCE;
   }

   public static class_640 get(CommandContext<?> context) {
      return (class_640)context.getArgument("player", class_640.class);
   }

   private PlayerListEntryArgumentType() {
   }

   public class_640 parse(StringReader reader) throws CommandSyntaxException {
      String argument = reader.readString();
      class_640 playerListEntry = null;
      Iterator var4 = MeteorClient.mc.method_1562().method_2880().iterator();

      while(var4.hasNext()) {
         class_640 p = (class_640)var4.next();
         if (p.method_2966().getName().equalsIgnoreCase(argument)) {
            playerListEntry = p;
            break;
         }
      }

      if (playerListEntry == null) {
         throw NO_SUCH_PLAYER.create(argument);
      } else {
         return playerListEntry;
      }
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
      return class_2172.method_9264(MeteorClient.mc.method_1562().method_2880().stream().map((playerListEntry) -> {
         return playerListEntry.method_2966().getName();
      }), builder);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}
