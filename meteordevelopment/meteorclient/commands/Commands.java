package meteordevelopment.meteorclient.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.commands.commands.BindCommand;
import meteordevelopment.meteorclient.commands.commands.BindsCommand;
import meteordevelopment.meteorclient.commands.commands.CommandsCommand;
import meteordevelopment.meteorclient.commands.commands.DamageCommand;
import meteordevelopment.meteorclient.commands.commands.DisconnectCommand;
import meteordevelopment.meteorclient.commands.commands.DismountCommand;
import meteordevelopment.meteorclient.commands.commands.DropCommand;
import meteordevelopment.meteorclient.commands.commands.EnchantCommand;
import meteordevelopment.meteorclient.commands.commands.EnderChestCommand;
import meteordevelopment.meteorclient.commands.commands.EnemyCommand;
import meteordevelopment.meteorclient.commands.commands.FakePlayerCommand;
import meteordevelopment.meteorclient.commands.commands.FovCommand;
import meteordevelopment.meteorclient.commands.commands.FriendCommand;
import meteordevelopment.meteorclient.commands.commands.GamemodeCommand;
import meteordevelopment.meteorclient.commands.commands.GiveCommand;
import meteordevelopment.meteorclient.commands.commands.HClipCommand;
import meteordevelopment.meteorclient.commands.commands.InputCommand;
import meteordevelopment.meteorclient.commands.commands.InventoryCommand;
import meteordevelopment.meteorclient.commands.commands.LocateCommand;
import meteordevelopment.meteorclient.commands.commands.MacroCommand;
import meteordevelopment.meteorclient.commands.commands.ModulesCommand;
import meteordevelopment.meteorclient.commands.commands.NameHistoryCommand;
import meteordevelopment.meteorclient.commands.commands.NbtCommand;
import meteordevelopment.meteorclient.commands.commands.NotebotCommand;
import meteordevelopment.meteorclient.commands.commands.PeekCommand;
import meteordevelopment.meteorclient.commands.commands.ProfilesCommand;
import meteordevelopment.meteorclient.commands.commands.ReloadCommand;
import meteordevelopment.meteorclient.commands.commands.ResetCommand;
import meteordevelopment.meteorclient.commands.commands.RotationCommand;
import meteordevelopment.meteorclient.commands.commands.SaveMapCommand;
import meteordevelopment.meteorclient.commands.commands.SayCommand;
import meteordevelopment.meteorclient.commands.commands.ServerCommand;
import meteordevelopment.meteorclient.commands.commands.SettingCommand;
import meteordevelopment.meteorclient.commands.commands.SpectateCommand;
import meteordevelopment.meteorclient.commands.commands.SwarmCommand;
import meteordevelopment.meteorclient.commands.commands.ToggleCommand;
import meteordevelopment.meteorclient.commands.commands.VClipCommand;
import meteordevelopment.meteorclient.commands.commands.WaspCommand;
import meteordevelopment.meteorclient.commands.commands.WaypointCommand;
import meteordevelopment.meteorclient.pathing.PathManagers;
import meteordevelopment.meteorclient.utils.PostInit;
import net.minecraft.class_2172;

public class Commands {
   public static final CommandDispatcher<class_2172> DISPATCHER = new CommandDispatcher();
   public static final List<Command> COMMANDS = new ArrayList();

   @PostInit(
      dependencies = {PathManagers.class}
   )
   public static void init() {
      add(new VClipCommand());
      add(new HClipCommand());
      add(new DismountCommand());
      add(new DisconnectCommand());
      add(new DamageCommand());
      add(new DropCommand());
      add(new EnchantCommand());
      add(new FakePlayerCommand());
      add(new FriendCommand());
      add(new EnemyCommand());
      add(new CommandsCommand());
      add(new InventoryCommand());
      add(new NbtCommand());
      add(new NotebotCommand());
      add(new PeekCommand());
      add(new EnderChestCommand());
      add(new ProfilesCommand());
      add(new ReloadCommand());
      add(new ResetCommand());
      add(new SayCommand());
      add(new ServerCommand());
      add(new SwarmCommand());
      add(new ToggleCommand());
      add(new SettingCommand());
      add(new SpectateCommand());
      add(new GamemodeCommand());
      add(new SaveMapCommand());
      add(new MacroCommand());
      add(new ModulesCommand());
      add(new BindsCommand());
      add(new GiveCommand());
      add(new NameHistoryCommand());
      add(new BindCommand());
      add(new FovCommand());
      add(new RotationCommand());
      add(new WaypointCommand());
      add(new InputCommand());
      add(new WaspCommand());
      add(new LocateCommand());
      COMMANDS.sort(Comparator.comparing(Command::getName));
   }

   public static void add(Command command) {
      COMMANDS.removeIf((existing) -> {
         return existing.getName().equals(command.getName());
      });
      command.registerTo(DISPATCHER);
      COMMANDS.add(command);
   }

   public static void dispatch(String message) throws CommandSyntaxException {
      DISPATCHER.execute(message, MeteorClient.mc.method_1562().method_2875());
   }

   public static Command get(String name) {
      Iterator var1 = COMMANDS.iterator();

      Command command;
      do {
         if (!var1.hasNext()) {
            return null;
         }

         command = (Command)var1.next();
      } while(!command.getName().equals(name));

      return command;
   }
}
