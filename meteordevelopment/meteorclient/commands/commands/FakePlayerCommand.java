package meteordevelopment.meteorclient.commands.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.commands.arguments.FakePlayerArgumentType;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.player.FakePlayer;
import meteordevelopment.meteorclient.utils.entity.fakeplayer.FakePlayerEntity;
import meteordevelopment.meteorclient.utils.entity.fakeplayer.FakePlayerManager;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.minecraft.class_2172;

public class FakePlayerCommand extends Command {
   public FakePlayerCommand() {
      super("fake-player", "Manages fake players that you can use for testing.");
   }

   public void build(LiteralArgumentBuilder<class_2172> builder) {
      builder.then(((LiteralArgumentBuilder)literal("add").executes((context) -> {
         FakePlayer fakePlayer = (FakePlayer)Modules.get().get(FakePlayer.class);
         FakePlayerManager.add((String)fakePlayer.name.get(), (float)(Integer)fakePlayer.health.get(), (Boolean)fakePlayer.copyInv.get());
         return 1;
      })).then(argument("name", StringArgumentType.word()).executes((context) -> {
         FakePlayer fakePlayer = (FakePlayer)Modules.get().get(FakePlayer.class);
         FakePlayerManager.add(StringArgumentType.getString(context, "name"), (float)(Integer)fakePlayer.health.get(), (Boolean)fakePlayer.copyInv.get());
         return 1;
      })));
      builder.then(literal("remove").then(argument("fp", FakePlayerArgumentType.create()).executes((context) -> {
         FakePlayerEntity fp = FakePlayerArgumentType.get(context);
         if (fp != null && FakePlayerManager.contains(fp)) {
            FakePlayerManager.remove(fp);
            this.info("Removed Fake Player %s.".formatted(new Object[]{fp.method_5477().getString()}), new Object[0]);
            return 1;
         } else {
            this.error("Couldn't find a Fake Player with that name.", new Object[0]);
            return 1;
         }
      })));
      builder.then(literal("clear").executes((context) -> {
         FakePlayerManager.clear();
         return 1;
      }));
      builder.then(literal("list").executes((context) -> {
         this.info("--- Fake Players ((highlight)%s(default)) ---", new Object[]{FakePlayerManager.count()});
         FakePlayerManager.forEach((fp) -> {
            ChatUtils.info("(highlight)%s".formatted(new Object[]{fp.method_5477().getString()}));
         });
         return 1;
      }));
   }
}
