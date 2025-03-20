package meteordevelopment.meteorclient.commands.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import joptsimple.internal.Strings;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixin.ClientPlayNetworkHandlerAccessor;
import meteordevelopment.meteorclient.utils.world.TickRate;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1132;
import net.minecraft.class_124;
import net.minecraft.class_2172;
import net.minecraft.class_2558;
import net.minecraft.class_2561;
import net.minecraft.class_2568;
import net.minecraft.class_2596;
import net.minecraft.class_2639;
import net.minecraft.class_2641;
import net.minecraft.class_2805;
import net.minecraft.class_5250;
import net.minecraft.class_639;
import net.minecraft.class_642;
import net.minecraft.class_7157;
import net.minecraft.class_2558.class_2559;
import net.minecraft.class_2568.class_5247;
import org.apache.commons.lang3.StringUtils;

public class ServerCommand extends Command {
   private static final Set<String> ANTICHEAT_LIST = Set.of(new String[]{"nocheatplus", "negativity", "warden", "horizon", "illegalstack", "coreprotect", "exploitsx", "vulcan", "abc", "spartan", "kauri", "anticheatreloaded", "witherac", "godseye", "matrix", "wraith", "antixrayheuristics", "grimac"});
   private static final Set<String> VERSION_ALIASES = Set.of("version", "ver", "about", "bukkit:version", "bukkit:ver", "bukkit:about");
   private String alias;
   private int ticks = 0;
   private boolean tick = false;
   private final List<String> plugins = new ArrayList();
   private final List<String> commandTreePlugins = new ArrayList();
   private static final Random RANDOM = new Random();

   public ServerCommand() {
      super("server", "Prints server information");
      MeteorClient.EVENT_BUS.subscribe((Object)this);
   }

   public void build(LiteralArgumentBuilder<class_2172> builder) {
      builder.executes((context) -> {
         this.basicInfo();
         return 1;
      });
      builder.then(literal("info").executes((ctx) -> {
         this.basicInfo();
         return 1;
      }));
      builder.then(literal("plugins").executes((ctx) -> {
         this.plugins.addAll(this.commandTreePlugins);
         if (this.alias != null) {
            mc.method_1562().method_52787(new class_2805(RANDOM.nextInt(200), this.alias + " "));
            this.tick = true;
         } else {
            this.printPlugins();
         }

         return 1;
      }));
      builder.then(literal("tps").executes((ctx) -> {
         float tps = TickRate.INSTANCE.getTickRate();
         class_124 color;
         if (tps > 17.0F) {
            color = class_124.field_1060;
         } else if (tps > 12.0F) {
            color = class_124.field_1054;
         } else {
            color = class_124.field_1061;
         }

         this.info("Current TPS: %s%.2f(default).", new Object[]{color, tps});
         return 1;
      }));
   }

   private void basicInfo() {
      if (mc.method_1496()) {
         class_1132 server = mc.method_1576();
         this.info("Singleplayer", new Object[0]);
         if (server != null) {
            this.info("Version: %s", new Object[]{server.method_3827()});
         }

      } else {
         class_642 server = mc.method_1558();
         if (server == null) {
            this.info("Couldn't obtain any server information.", new Object[0]);
         } else {
            String ipv4 = "";

            try {
               ipv4 = InetAddress.getByName(server.field_3761).getHostAddress();
            } catch (UnknownHostException var5) {
            }

            String var10000;
            class_5250 ipText;
            if (ipv4.isEmpty()) {
               var10000 = String.valueOf(class_124.field_1080);
               ipText = class_2561.method_43470(var10000 + server.field_3761);
               ipText.method_10862(ipText.method_10866().method_10958(new class_2558(class_2559.field_21462, server.field_3761)).method_10949(new class_2568(class_5247.field_24342, class_2561.method_43470("Copy to clipboard"))));
            } else {
               var10000 = String.valueOf(class_124.field_1080);
               ipText = class_2561.method_43470(var10000 + server.field_3761);
               ipText.method_10862(ipText.method_10866().method_10958(new class_2558(class_2559.field_21462, server.field_3761)).method_10949(new class_2568(class_5247.field_24342, class_2561.method_43470("Copy to clipboard"))));
               class_5250 ipv4Text = class_2561.method_43470(String.format("%s (%s)", class_124.field_1080, ipv4));
               ipv4Text.method_10862(ipText.method_10866().method_10958(new class_2558(class_2559.field_21462, ipv4)).method_10949(new class_2568(class_5247.field_24342, class_2561.method_43470("Copy to clipboard"))));
               ipText.method_10852(ipv4Text);
            }

            this.info(class_2561.method_43470(String.format("%sIP: ", class_124.field_1080)).method_10852(ipText));
            this.info("Port: %d", new Object[]{class_639.method_2950(server.field_3761).method_2954()});
            this.info("Type: %s", new Object[]{mc.method_1562().method_52790() != null ? mc.method_1562().method_52790() : "unknown"});
            this.info("Motd: %s", new Object[]{server.field_3757 != null ? server.field_3757.getString() : "unknown"});
            this.info("Version: %s", new Object[]{server.field_3760.getString()});
            this.info("Protocol version: %d", new Object[]{server.field_3756});
            this.info("Difficulty: %s (Local: %.2f)", new Object[]{mc.field_1687.method_8407().method_5463().getString(), mc.field_1687.method_8404(mc.field_1724.method_24515()).method_5457()});
            this.info("Day: %d", new Object[]{mc.field_1687.method_8532() / 24000L});
            this.info("Permission level: %s", new Object[]{this.formatPerms()});
         }
      }
   }

   public String formatPerms() {
      int p;
      for(p = 5; !mc.field_1724.method_5687(p) && p > 0; --p) {
      }

      String var10000;
      switch(p) {
      case 0:
         var10000 = "0 (No Perms)";
         break;
      case 1:
         var10000 = "1 (No Perms)";
         break;
      case 2:
         var10000 = "2 (Player Command Access)";
         break;
      case 3:
         var10000 = "3 (Server Command Access)";
         break;
      case 4:
         var10000 = "4 (Operator)";
         break;
      default:
         var10000 = p + " (Unknown)";
      }

      return var10000;
   }

   private void printPlugins() {
      this.plugins.sort(String.CASE_INSENSITIVE_ORDER);
      this.plugins.replaceAll(this::formatName);
      if (!this.plugins.isEmpty()) {
         this.info("Plugins (%d): %s ", new Object[]{this.plugins.size(), Strings.join((String[])this.plugins.toArray(new String[0]), ", ")});
      } else {
         this.error("No plugins found.", new Object[0]);
      }

      this.tick = false;
      this.ticks = 0;
      this.plugins.clear();
   }

   @EventHandler
   private void onTick(TickEvent.Post event) {
      if (this.tick) {
         ++this.ticks;
         if (this.ticks >= 100) {
            this.printPlugins();
         }

      }
   }

   @EventHandler
   private void onSendPacket(PacketEvent.Send event) {
      if (this.tick && event.packet instanceof class_2805) {
         event.cancel();
      }

   }

   @EventHandler
   private void onReadPacket(PacketEvent.Receive event) {
      class_2596 var3 = event.packet;
      if (var3 instanceof class_2641) {
         class_2641 packet = (class_2641)var3;
         ClientPlayNetworkHandlerAccessor handler = (ClientPlayNetworkHandlerAccessor)event.connection.method_10744();
         this.commandTreePlugins.clear();
         this.alias = null;
         packet.method_11403(class_7157.method_46722(handler.getCombinedDynamicRegistries(), handler.getEnabledFeatures())).getChildren().forEach((node) -> {
            String[] split = node.getName().split(":");
            if (split.length > 1 && !this.commandTreePlugins.contains(split[0])) {
               this.commandTreePlugins.add(split[0]);
            }

            if (this.alias == null && VERSION_ALIASES.contains(node.getName())) {
               this.alias = node.getName();
            }

         });
      }

      if (this.tick) {
         try {
            var3 = event.packet;
            if (var3 instanceof class_2639) {
               class_2639 packet = (class_2639)var3;
               Suggestions matches = packet.method_11397();
               if (matches.isEmpty()) {
                  this.error("An error occurred while trying to find plugins.", new Object[0]);
                  return;
               }

               Iterator var4 = matches.getList().iterator();

               while(var4.hasNext()) {
                  Suggestion suggestion = (Suggestion)var4.next();
                  String pluginName = suggestion.getText();
                  if (!this.plugins.contains(pluginName.toLowerCase())) {
                     this.plugins.add(pluginName);
                  }
               }

               this.printPlugins();
            }
         } catch (Exception var7) {
            this.error("An error occurred while trying to find plugins.", new Object[0]);
         }

      }
   }

   private String formatName(String name) {
      if (ANTICHEAT_LIST.contains(name.toLowerCase())) {
         return String.format("%s%s(default)", class_124.field_1061, name);
      } else {
         return !StringUtils.containsIgnoreCase(name, "exploit") && !StringUtils.containsIgnoreCase(name, "cheat") && !StringUtils.containsIgnoreCase(name, "illegal") ? String.format("(highlight)%s(default)", name) : String.format("%s%s(default)", class_124.field_1061, name);
      }
   }
}
