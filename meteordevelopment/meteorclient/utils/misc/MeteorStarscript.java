package meteordevelopment.meteorclient.utils.misc;

import baritone.api.BaritoneAPI;
import baritone.api.pathing.goals.Goal;
import baritone.api.process.IBaritoneProcess;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.mixin.ClientPlayerInteractionManagerAccessor;
import meteordevelopment.meteorclient.mixin.MinecraftClientAccessor;
import meteordevelopment.meteorclient.pathing.BaritoneUtils;
import meteordevelopment.meteorclient.pathing.PathManagers;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.systems.config.Config;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.PreInit;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.world.Dimension;
import meteordevelopment.meteorclient.utils.world.TickRate;
import meteordevelopment.starscript.Script;
import meteordevelopment.starscript.Section;
import meteordevelopment.starscript.StandardLib;
import meteordevelopment.starscript.Starscript;
import meteordevelopment.starscript.compiler.Compiler;
import meteordevelopment.starscript.compiler.Parser;
import meteordevelopment.starscript.utils.Error;
import meteordevelopment.starscript.utils.StarscriptError;
import meteordevelopment.starscript.value.Value;
import meteordevelopment.starscript.value.ValueMap;
import net.minecraft.class_1291;
import net.minecraft.class_1293;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1309;
import net.minecraft.class_151;
import net.minecraft.class_155;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1959;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_239;
import net.minecraft.class_243;
import net.minecraft.class_2680;
import net.minecraft.class_2799;
import net.minecraft.class_2960;
import net.minecraft.class_3445;
import net.minecraft.class_3468;
import net.minecraft.class_3965;
import net.minecraft.class_3966;
import net.minecraft.class_640;
import net.minecraft.class_6880;
import net.minecraft.class_7923;
import net.minecraft.class_7924;
import net.minecraft.class_2338.class_2339;
import net.minecraft.class_239.class_240;
import net.minecraft.class_2799.class_2800;
import net.minecraft.class_6880.class_6883;
import org.apache.commons.lang3.StringUtils;

public class MeteorStarscript {
   public static Starscript ss = new Starscript();
   private static final class_2339 BP = new class_2339();
   private static final StringBuilder SB = new StringBuilder();
   private static long lastRequestedStatsTime = 0L;

   @PreInit(
      dependencies = {PathManagers.class}
   )
   public static void init() {
      StandardLib.init(ss);
      ss.set("mc_version", class_155.method_16673().method_48019());
      ss.set("fps", () -> {
         return Value.number((double)MinecraftClientAccessor.getFps());
      });
      ss.set("ping", MeteorStarscript::ping);
      ss.set("time", () -> {
         return Value.string(LocalTime.now().format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)));
      });
      ss.set("cps", () -> {
         return Value.number((double)CPSUtils.getCpsAverage());
      });
      ss.set("meteor", (new ValueMap()).set("name", MeteorClient.NAME).set("version", MeteorClient.VERSION != null ? (MeteorClient.DEV_BUILD.isEmpty() ? MeteorClient.VERSION.toString() : String.valueOf(MeteorClient.VERSION) + " " + MeteorClient.DEV_BUILD) : "").set("modules", () -> {
         return Value.number((double)Modules.get().getAll().size());
      }).set("active_modules", () -> {
         return Value.number((double)Modules.get().getActive().size());
      }).set("is_module_active", MeteorStarscript::isModuleActive).set("get_module_info", MeteorStarscript::getModuleInfo).set("get_module_setting", MeteorStarscript::getModuleSetting).set("prefix", MeteorStarscript::getMeteorPrefix));
      if (BaritoneUtils.IS_AVAILABLE) {
         ss.set("baritone", (new ValueMap()).set("is_pathing", () -> {
            return Value.bool(BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().isPathing());
         }).set("distance_to_goal", MeteorStarscript::baritoneDistanceToGoal).set("process", MeteorStarscript::baritoneProcess).set("process_name", MeteorStarscript::baritoneProcessName).set("eta", MeteorStarscript::baritoneETA));
      }

      ss.set("camera", (new ValueMap()).set("pos", (new ValueMap()).set("_toString", () -> {
         return posString(false, true);
      }).set("x", () -> {
         return Value.number(MeteorClient.mc.field_1773.method_19418().method_19326().field_1352);
      }).set("y", () -> {
         return Value.number(MeteorClient.mc.field_1773.method_19418().method_19326().field_1351);
      }).set("z", () -> {
         return Value.number(MeteorClient.mc.field_1773.method_19418().method_19326().field_1350);
      })).set("opposite_dim_pos", (new ValueMap()).set("_toString", () -> {
         return posString(true, true);
      }).set("x", () -> {
         return oppositeX(true);
      }).set("y", () -> {
         return Value.number(MeteorClient.mc.field_1773.method_19418().method_19326().field_1351);
      }).set("z", () -> {
         return oppositeZ(true);
      })).set("yaw", () -> {
         return yaw(true);
      }).set("pitch", () -> {
         return pitch(true);
      }).set("direction", () -> {
         return direction(true);
      }));
      ss.set("player", (new ValueMap()).set("_toString", () -> {
         return Value.string(MeteorClient.mc.method_1548().method_1676());
      }).set("health", () -> {
         return Value.number(MeteorClient.mc.field_1724 != null ? (double)MeteorClient.mc.field_1724.method_6032() : 0.0D);
      }).set("absorption", () -> {
         return Value.number(MeteorClient.mc.field_1724 != null ? (double)MeteorClient.mc.field_1724.method_6067() : 0.0D);
      }).set("hunger", () -> {
         return Value.number(MeteorClient.mc.field_1724 != null ? (double)MeteorClient.mc.field_1724.method_7344().method_7586() : 0.0D);
      }).set("speed", () -> {
         return Value.number(Utils.getPlayerSpeed().method_37267());
      }).set("speed_all", (new ValueMap()).set("_toString", () -> {
         return Value.string(MeteorClient.mc.field_1724 != null ? Utils.getPlayerSpeed().toString() : "");
      }).set("x", () -> {
         return Value.number(MeteorClient.mc.field_1724 != null ? Utils.getPlayerSpeed().field_1352 : 0.0D);
      }).set("y", () -> {
         return Value.number(MeteorClient.mc.field_1724 != null ? Utils.getPlayerSpeed().field_1351 : 0.0D);
      }).set("z", () -> {
         return Value.number(MeteorClient.mc.field_1724 != null ? Utils.getPlayerSpeed().field_1350 : 0.0D);
      })).set("breaking_progress", () -> {
         return Value.number(MeteorClient.mc.field_1761 != null ? (double)((ClientPlayerInteractionManagerAccessor)MeteorClient.mc.field_1761).getBreakingProgress() : 0.0D);
      }).set("biome", MeteorStarscript::biome).set("dimension", () -> {
         return Value.string(PlayerUtils.getDimension().name());
      }).set("opposite_dimension", () -> {
         return Value.string(PlayerUtils.getDimension().opposite().name());
      }).set("gamemode", () -> {
         return PlayerUtils.getGameMode() != null ? Value.string(StringUtils.capitalize(PlayerUtils.getGameMode().method_8381())) : Value.null_();
      }).set("pos", (new ValueMap()).set("_toString", () -> {
         return posString(false, false);
      }).set("x", () -> {
         return Value.number(MeteorClient.mc.field_1724 != null ? MeteorClient.mc.field_1724.method_23317() : 0.0D);
      }).set("y", () -> {
         return Value.number(MeteorClient.mc.field_1724 != null ? MeteorClient.mc.field_1724.method_23318() : 0.0D);
      }).set("z", () -> {
         return Value.number(MeteorClient.mc.field_1724 != null ? MeteorClient.mc.field_1724.method_23321() : 0.0D);
      })).set("opposite_dim_pos", (new ValueMap()).set("_toString", () -> {
         return posString(true, false);
      }).set("x", () -> {
         return oppositeX(false);
      }).set("y", () -> {
         return Value.number(MeteorClient.mc.field_1724 != null ? MeteorClient.mc.field_1724.method_23318() : 0.0D);
      }).set("z", () -> {
         return oppositeZ(false);
      })).set("yaw", () -> {
         return yaw(false);
      }).set("pitch", () -> {
         return pitch(false);
      }).set("direction", () -> {
         return direction(false);
      }).set("hand", () -> {
         return MeteorClient.mc.field_1724 != null ? wrap(MeteorClient.mc.field_1724.method_6047()) : Value.null_();
      }).set("offhand", () -> {
         return MeteorClient.mc.field_1724 != null ? wrap(MeteorClient.mc.field_1724.method_6079()) : Value.null_();
      }).set("hand_or_offhand", MeteorStarscript::handOrOffhand).set("get_item", MeteorStarscript::getItem).set("count_items", MeteorStarscript::countItems).set("xp", (new ValueMap()).set("level", () -> {
         return Value.number(MeteorClient.mc.field_1724 != null ? (double)MeteorClient.mc.field_1724.field_7520 : 0.0D);
      }).set("progress", () -> {
         return Value.number(MeteorClient.mc.field_1724 != null ? (double)MeteorClient.mc.field_1724.field_7510 : 0.0D);
      }).set("total", () -> {
         return Value.number(MeteorClient.mc.field_1724 != null ? (double)MeteorClient.mc.field_1724.field_7495 : 0.0D);
      })).set("has_potion_effect", MeteorStarscript::hasPotionEffect).set("get_potion_effect", MeteorStarscript::getPotionEffect).set("get_stat", MeteorStarscript::getStat));
      ss.set("crosshair_target", (new ValueMap()).set("type", MeteorStarscript::crosshairType).set("value", MeteorStarscript::crosshairValue));
      ss.set("server", (new ValueMap()).set("_toString", () -> {
         return Value.string(Utils.getWorldName());
      }).set("tps", () -> {
         return Value.number((double)TickRate.INSTANCE.getTickRate());
      }).set("time", () -> {
         return Value.string(Utils.getWorldTime());
      }).set("player_count", () -> {
         return Value.number(MeteorClient.mc.method_1562() != null ? (double)MeteorClient.mc.method_1562().method_2880().size() : 0.0D);
      }).set("difficulty", () -> {
         return Value.string(MeteorClient.mc.field_1687 != null ? MeteorClient.mc.field_1687.method_8407().method_5460() : "");
      }));
   }

   public static Script compile(String source) {
      Parser.Result result = Parser.parse(source);
      if (!result.hasErrors()) {
         return Compiler.compile(result);
      } else {
         Iterator var2 = result.errors.iterator();

         while(var2.hasNext()) {
            Error error = (Error)var2.next();
            printChatError(error);
         }

         return null;
      }
   }

   public static Section runSection(Script script, StringBuilder sb) {
      try {
         return ss.run(script, sb);
      } catch (StarscriptError var3) {
         printChatError(var3);
         return null;
      }
   }

   public static String run(Script script, StringBuilder sb) {
      Section section = runSection(script, sb);
      return section != null ? section.toString() : null;
   }

   public static Section runSection(Script script) {
      return runSection(script, new StringBuilder());
   }

   public static String run(Script script) {
      return run(script, new StringBuilder());
   }

   public static void printChatError(int i, Error error) {
      String caller = getCallerName();
      if (caller != null) {
         if (i != -1) {
            ChatUtils.errorPrefix("Starscript", "%d, %d '%c': %s (from %s)", i, error.character, error.ch, error.message, caller);
         } else {
            ChatUtils.errorPrefix("Starscript", "%d '%c': %s (from %s)", error.character, error.ch, error.message, caller);
         }
      } else if (i != -1) {
         ChatUtils.errorPrefix("Starscript", "%d, %d '%c': %s", i, error.character, error.ch, error.message);
      } else {
         ChatUtils.errorPrefix("Starscript", "%d '%c': %s", error.character, error.ch, error.message);
      }

   }

   public static void printChatError(Error error) {
      printChatError(-1, error);
   }

   public static void printChatError(StarscriptError e) {
      String caller = getCallerName();
      if (caller != null) {
         ChatUtils.errorPrefix("Starscript", "%s (from %s)", e.getMessage(), caller);
      } else {
         ChatUtils.errorPrefix("Starscript", "%s", e.getMessage());
      }

   }

   private static String getCallerName() {
      StackTraceElement[] elements = Thread.currentThread().getStackTrace();
      if (elements.length == 0) {
         return null;
      } else {
         for(int i = 1; i < elements.length; ++i) {
            String name = elements[i].getClassName();
            if (!name.startsWith(Starscript.class.getPackageName()) && !name.equals(MeteorStarscript.class.getName())) {
               return name.substring(name.lastIndexOf(46) + 1);
            }
         }

         return null;
      }
   }

   private static Value hasPotionEffect(Starscript ss, int argCount) {
      if (argCount < 1) {
         ss.error("player.has_potion_effect() requires 1 argument, got %d.", argCount);
      }

      if (MeteorClient.mc.field_1724 == null) {
         return Value.bool(false);
      } else {
         class_2960 name = popIdentifier(ss, "First argument to player.has_potion_effect() needs to a string.");
         Optional<class_6883<class_1291>> effect = class_7923.field_41174.method_55841(name);
         if (effect.isEmpty()) {
            return Value.bool(false);
         } else {
            class_1293 effectInstance = MeteorClient.mc.field_1724.method_6112((class_6880)effect.get());
            return Value.bool(effectInstance != null);
         }
      }
   }

   private static Value getPotionEffect(Starscript ss, int argCount) {
      if (argCount < 1) {
         ss.error("player.get_potion_effect() requires 1 argument, got %d.", argCount);
      }

      if (MeteorClient.mc.field_1724 == null) {
         return Value.null_();
      } else {
         class_2960 name = popIdentifier(ss, "First argument to player.get_potion_effect() needs to a string.");
         Optional<class_6883<class_1291>> effect = class_7923.field_41174.method_55841(name);
         if (effect.isEmpty()) {
            return Value.null_();
         } else {
            class_1293 effectInstance = MeteorClient.mc.field_1724.method_6112((class_6880)effect.get());
            return effectInstance == null ? Value.null_() : wrap(effectInstance);
         }
      }
   }

   private static Value getStat(Starscript ss, int argCount) {
      if (argCount < 1) {
         ss.error("player.get_stat() requires 1 argument, got %d.", argCount);
      }

      if (MeteorClient.mc.field_1724 == null) {
         return Value.number(0.0D);
      } else {
         long time = System.currentTimeMillis();
         if ((double)(time - lastRequestedStatsTime) / 1000.0D >= 1.0D && MeteorClient.mc.method_1562() != null) {
            MeteorClient.mc.method_1562().method_52787(new class_2799(class_2800.field_12775));
            lastRequestedStatsTime = time;
         }

         String type = argCount > 1 ? ss.popString("First argument to player.get_stat() needs to be a string.") : "custom";
         class_2960 name = popIdentifier(ss, (argCount > 1 ? "Second" : "First") + " argument to player.get_stat() needs to be a string.");
         byte var8 = -1;
         switch(type.hashCode()) {
         case -1558160551:
            if (type.equals("killed_by")) {
               var8 = 7;
            }
            break;
         case -1431725382:
            if (type.equals("picked_up")) {
               var8 = 4;
            }
            break;
         case -1380616235:
            if (type.equals("broken")) {
               var8 = 3;
            }
            break;
         case -1349088399:
            if (type.equals("custom")) {
               var8 = 8;
            }
            break;
         case -1131353987:
            if (type.equals("killed")) {
               var8 = 6;
            }
            break;
         case 3599293:
            if (type.equals("used")) {
               var8 = 2;
            }
            break;
         case 103900785:
            if (type.equals("mined")) {
               var8 = 0;
            }
            break;
         case 1025009343:
            if (type.equals("crafted")) {
               var8 = 1;
            }
            break;
         case 1925736384:
            if (type.equals("dropped")) {
               var8 = 5;
            }
         }

         class_3445 var10000;
         switch(var8) {
         case 0:
            var10000 = class_3468.field_15427.method_14956((class_2248)class_7923.field_41175.method_10223(name));
            break;
         case 1:
            var10000 = class_3468.field_15370.method_14956((class_1792)class_7923.field_41178.method_10223(name));
            break;
         case 2:
            var10000 = class_3468.field_15372.method_14956((class_1792)class_7923.field_41178.method_10223(name));
            break;
         case 3:
            var10000 = class_3468.field_15383.method_14956((class_1792)class_7923.field_41178.method_10223(name));
            break;
         case 4:
            var10000 = class_3468.field_15392.method_14956((class_1792)class_7923.field_41178.method_10223(name));
            break;
         case 5:
            var10000 = class_3468.field_15405.method_14956((class_1792)class_7923.field_41178.method_10223(name));
            break;
         case 6:
            var10000 = class_3468.field_15403.method_14956((class_1299)class_7923.field_41177.method_10223(name));
            break;
         case 7:
            var10000 = class_3468.field_15411.method_14956((class_1299)class_7923.field_41177.method_10223(name));
            break;
         case 8:
            name = (class_2960)class_7923.field_41183.method_10223(name);
            var10000 = name != null ? class_3468.field_15419.method_14956(name) : null;
            break;
         default:
            var10000 = null;
         }

         class_3445<?> stat = var10000;
         return Value.number(stat != null ? (double)MeteorClient.mc.field_1724.method_3143().method_15025(stat) : 0.0D);
      }
   }

   private static Value getModuleInfo(Starscript ss, int argCount) {
      if (argCount != 1) {
         ss.error("meteor.get_module_info() requires 1 argument, got %d.", argCount);
      }

      Module module = Modules.get().get(ss.popString("First argument to meteor.get_module_info() needs to be a string."));
      if (module != null && module.isActive()) {
         String info = module.getInfoString();
         return Value.string(info == null ? "" : info);
      } else {
         return Value.string("");
      }
   }

   private static Value getModuleSetting(Starscript ss, int argCount) {
      if (argCount != 2) {
         ss.error("meteor.get_module_setting() requires 2 arguments, got %d.", argCount);
      }

      String settingName = ss.popString("Second argument to meteor.get_module_setting() needs to be a string.");
      String moduleName = ss.popString("First argument to meteor.get_module_setting() needs to be a string.");
      Module module = Modules.get().get(moduleName);
      if (module == null) {
         ss.error("Unable to get module %s for meteor.get_module_setting()", moduleName);
      }

      Setting<?> setting = module.settings.get(settingName);
      if (setting == null) {
         ss.error("Unable to get setting %s for module %s for meteor.get_module_setting()", settingName, moduleName);
      }

      Object value = setting.get();
      byte var8 = 0;
      Value var10000;
      switch(((Class)value).typeSwitch<invokedynamic>(value, var8)) {
      case -1:
      default:
         var10000 = Value.string(value.toString());
         break;
      case 0:
         Double d = (Double)value;
         var10000 = Value.number(d);
         break;
      case 1:
         Integer i = (Integer)value;
         var10000 = Value.number((double)i);
         break;
      case 2:
         Boolean b = (Boolean)value;
         var10000 = Value.bool(b);
         break;
      case 3:
         List<?> list = (List)value;
         var10000 = Value.number((double)list.size());
      }

      return var10000;
   }

   private static Value isModuleActive(Starscript ss, int argCount) {
      if (argCount != 1) {
         ss.error("meteor.is_module_active() requires 1 argument, got %d.", argCount);
      }

      Module module = Modules.get().get(ss.popString("First argument to meteor.is_module_active() needs to be a string."));
      return Value.bool(module != null && module.isActive());
   }

   private static Value getItem(Starscript ss, int argCount) {
      if (argCount != 1) {
         ss.error("player.get_item() requires 1 argument, got %d.", argCount);
      }

      int i = (int)ss.popNumber("First argument to player.get_item() needs to be a number.");
      if (i < 0) {
         ss.error("First argument to player.get_item() needs to be a non-negative integer.", i);
      }

      return MeteorClient.mc.field_1724 != null ? wrap(MeteorClient.mc.field_1724.method_31548().method_5438(i)) : Value.null_();
   }

   private static Value countItems(Starscript ss, int argCount) {
      if (argCount != 1) {
         ss.error("player.count_items() requires 1 argument, got %d.", argCount);
      }

      String idRaw = ss.popString("First argument to player.count_items() needs to be a string.");
      class_2960 id = class_2960.method_12829(idRaw);
      if (id == null) {
         return Value.number(0.0D);
      } else {
         class_1792 item = (class_1792)class_7923.field_41178.method_10223(id);
         if (item != class_1802.field_8162 && MeteorClient.mc.field_1724 != null) {
            int count = 0;

            for(int i = 0; i < MeteorClient.mc.field_1724.method_31548().method_5439(); ++i) {
               class_1799 itemStack = MeteorClient.mc.field_1724.method_31548().method_5438(i);
               if (itemStack.method_7909() == item) {
                  count += itemStack.method_7947();
               }
            }

            return Value.number((double)count);
         } else {
            return Value.number(0.0D);
         }
      }
   }

   private static Value getMeteorPrefix() {
      return Config.get() == null ? Value.null_() : Value.string((String)Config.get().prefix.get());
   }

   private static Value baritoneProcess() {
      Optional<IBaritoneProcess> process = BaritoneAPI.getProvider().getPrimaryBaritone().getPathingControlManager().mostRecentInControl();
      return Value.string(process.isEmpty() ? "" : ((IBaritoneProcess)process.get()).displayName0());
   }

   private static Value baritoneProcessName() {
      Optional<IBaritoneProcess> process = BaritoneAPI.getProvider().getPrimaryBaritone().getPathingControlManager().mostRecentInControl();
      if (process.isEmpty()) {
         return Value.string("");
      } else {
         String className = ((IBaritoneProcess)process.get()).getClass().getSimpleName();
         if (className.endsWith("Process")) {
            className = className.substring(0, className.length() - 7);
         }

         SB.append(className);
         int i = 0;

         for(int j = 0; j < className.length(); ++j) {
            if (j > 0 && Character.isUpperCase(className.charAt(j))) {
               SB.insert(i, ' ');
               ++i;
            }

            ++i;
         }

         String name = SB.toString();
         SB.setLength(0);
         return Value.string(name);
      }
   }

   private static Value baritoneETA() {
      if (MeteorClient.mc.field_1724 == null) {
         return Value.number(0.0D);
      } else {
         Optional<Double> ticksTillGoal = BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().estimatedTicksToGoal();
         return (Value)ticksTillGoal.map((aDouble) -> {
            return Value.number(aDouble / 20.0D);
         }).orElseGet(() -> {
            return Value.number(0.0D);
         });
      }
   }

   private static Value oppositeX(boolean camera) {
      double x = camera ? MeteorClient.mc.field_1773.method_19418().method_19326().field_1352 : (MeteorClient.mc.field_1724 != null ? MeteorClient.mc.field_1724.method_23317() : 0.0D);
      Dimension dimension = PlayerUtils.getDimension();
      if (dimension == Dimension.Overworld) {
         x /= 8.0D;
      } else if (dimension == Dimension.Nether) {
         x *= 8.0D;
      }

      return Value.number(x);
   }

   private static Value oppositeZ(boolean camera) {
      double z = camera ? MeteorClient.mc.field_1773.method_19418().method_19326().field_1350 : (MeteorClient.mc.field_1724 != null ? MeteorClient.mc.field_1724.method_23321() : 0.0D);
      Dimension dimension = PlayerUtils.getDimension();
      if (dimension == Dimension.Overworld) {
         z /= 8.0D;
      } else if (dimension == Dimension.Nether) {
         z *= 8.0D;
      }

      return Value.number(z);
   }

   private static Value yaw(boolean camera) {
      float yaw;
      if (camera) {
         yaw = MeteorClient.mc.field_1773.method_19418().method_19330();
      } else {
         yaw = MeteorClient.mc.field_1724 != null ? MeteorClient.mc.field_1724.method_36454() : 0.0F;
      }

      yaw %= 360.0F;
      if (yaw < 0.0F) {
         yaw += 360.0F;
      }

      if (yaw > 180.0F) {
         yaw -= 360.0F;
      }

      return Value.number((double)yaw);
   }

   private static Value pitch(boolean camera) {
      float pitch;
      if (camera) {
         pitch = MeteorClient.mc.field_1773.method_19418().method_19329();
      } else {
         pitch = MeteorClient.mc.field_1724 != null ? MeteorClient.mc.field_1724.method_36455() : 0.0F;
      }

      pitch %= 360.0F;
      if (pitch < 0.0F) {
         pitch += 360.0F;
      }

      if (pitch > 180.0F) {
         pitch -= 360.0F;
      }

      return Value.number((double)pitch);
   }

   private static Value direction(boolean camera) {
      float yaw;
      if (camera) {
         yaw = MeteorClient.mc.field_1773.method_19418().method_19330();
      } else {
         yaw = MeteorClient.mc.field_1724 != null ? MeteorClient.mc.field_1724.method_36454() : 0.0F;
      }

      return wrap(HorizontalDirection.get(yaw));
   }

   private static Value biome() {
      if (MeteorClient.mc.field_1724 != null && MeteorClient.mc.field_1687 != null) {
         BP.method_10102(MeteorClient.mc.field_1724.method_23317(), MeteorClient.mc.field_1724.method_23318(), MeteorClient.mc.field_1724.method_23321());
         class_2960 id = MeteorClient.mc.field_1687.method_30349().method_30530(class_7924.field_41236).method_10221((class_1959)MeteorClient.mc.field_1687.method_23753(BP).comp_349());
         return id == null ? Value.string("Unknown") : Value.string((String)Arrays.stream(id.method_12832().split("_")).map(StringUtils::capitalize).collect(Collectors.joining(" ")));
      } else {
         return Value.string("");
      }
   }

   private static Value handOrOffhand() {
      if (MeteorClient.mc.field_1724 == null) {
         return Value.null_();
      } else {
         class_1799 itemStack = MeteorClient.mc.field_1724.method_6047();
         if (itemStack.method_7960()) {
            itemStack = MeteorClient.mc.field_1724.method_6079();
         }

         return itemStack != null ? wrap(itemStack) : Value.null_();
      }
   }

   private static Value ping() {
      if (MeteorClient.mc.method_1562() != null && MeteorClient.mc.field_1724 != null) {
         class_640 playerListEntry = MeteorClient.mc.method_1562().method_2871(MeteorClient.mc.field_1724.method_5667());
         return Value.number(playerListEntry != null ? (double)playerListEntry.method_2959() : 0.0D);
      } else {
         return Value.number(0.0D);
      }
   }

   private static Value baritoneDistanceToGoal() {
      Goal goal = BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().getGoal();
      return Value.number(goal != null && MeteorClient.mc.field_1724 != null ? goal.heuristic(MeteorClient.mc.field_1724.method_24515()) : 0.0D);
   }

   private static Value posString(boolean opposite, boolean camera) {
      class_243 pos;
      if (camera) {
         pos = MeteorClient.mc.field_1773.method_19418().method_19326();
      } else {
         pos = MeteorClient.mc.field_1724 != null ? MeteorClient.mc.field_1724.method_19538() : class_243.field_1353;
      }

      double x = pos.field_1352;
      double z = pos.field_1350;
      if (opposite) {
         Dimension dimension = PlayerUtils.getDimension();
         if (dimension == Dimension.Overworld) {
            x /= 8.0D;
            z /= 8.0D;
         } else if (dimension == Dimension.Nether) {
            x *= 8.0D;
            z *= 8.0D;
         }
      }

      return posString(x, pos.field_1351, z);
   }

   private static Value posString(double x, double y, double z) {
      return Value.string(String.format("X: %.0f Y: %.0f Z: %.0f", x, y, z));
   }

   private static Value crosshairType() {
      if (MeteorClient.mc.field_1765 == null) {
         return Value.string("miss");
      } else {
         String var10000;
         switch(MeteorClient.mc.field_1765.method_17783()) {
         case field_1333:
            var10000 = "miss";
            break;
         case field_1332:
            var10000 = "block";
            break;
         case field_1331:
            var10000 = "entity";
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
         }

         return Value.string(var10000);
      }
   }

   private static Value crosshairValue() {
      if (MeteorClient.mc.field_1687 != null && MeteorClient.mc.field_1765 != null) {
         if (MeteorClient.mc.field_1765.method_17783() == class_240.field_1333) {
            return Value.string("");
         } else {
            class_239 var1 = MeteorClient.mc.field_1765;
            if (var1 instanceof class_3965) {
               class_3965 hit = (class_3965)var1;
               return wrap(hit.method_17777(), MeteorClient.mc.field_1687.method_8320(hit.method_17777()));
            } else {
               return wrap(((class_3966)MeteorClient.mc.field_1765).method_17782());
            }
         }
      } else {
         return Value.null_();
      }
   }

   public static class_2960 popIdentifier(Starscript ss, String errorMessage) {
      try {
         return class_2960.method_60654(ss.popString(errorMessage));
      } catch (class_151 var3) {
         ss.error(var3.getMessage());
         return null;
      }
   }

   public static Value wrap(class_1799 itemStack) {
      String name = itemStack.method_7960() ? "" : Names.get(itemStack.method_7909());
      int durability = 0;
      if (!itemStack.method_7960() && itemStack.method_7963()) {
         durability = itemStack.method_7936() - itemStack.method_7919();
      }

      return Value.map((new ValueMap()).set("_toString", Value.string(itemStack.method_7947() <= 1 ? name : String.format("%s %dx", name, itemStack.method_7947()))).set("name", Value.string(name)).set("id", Value.string(class_7923.field_41178.method_10221(itemStack.method_7909()).toString())).set("count", Value.number((double)itemStack.method_7947())).set("durability", Value.number((double)durability)).set("max_durability", Value.number((double)itemStack.method_7936())));
   }

   public static Value wrap(class_2338 blockPos, class_2680 blockState) {
      return Value.map((new ValueMap()).set("_toString", Value.string(Names.get(blockState.method_26204()))).set("id", Value.string(class_7923.field_41175.method_10221(blockState.method_26204()).toString())).set("pos", Value.map((new ValueMap()).set("_toString", posString((double)blockPos.method_10263(), (double)blockPos.method_10264(), (double)blockPos.method_10260())).set("x", Value.number((double)blockPos.method_10263())).set("y", Value.number((double)blockPos.method_10264())).set("z", Value.number((double)blockPos.method_10260())))));
   }

   public static Value wrap(class_1297 entity) {
      ValueMap var10000 = (new ValueMap()).set("_toString", Value.string(entity.method_5477().getString())).set("id", Value.string(class_7923.field_41177.method_10221(entity.method_5864()).toString()));
      class_1309 e;
      double var10002;
      if (entity instanceof class_1309) {
         e = (class_1309)entity;
         var10002 = (double)e.method_6032();
      } else {
         var10002 = 0.0D;
      }

      var10000 = var10000.set("health", Value.number(var10002));
      if (entity instanceof class_1309) {
         e = (class_1309)entity;
         var10002 = (double)e.method_6067();
      } else {
         var10002 = 0.0D;
      }

      return Value.map(var10000.set("absorption", Value.number(var10002)).set("pos", Value.map((new ValueMap()).set("_toString", posString(entity.method_23317(), entity.method_23318(), entity.method_23321())).set("x", Value.number(entity.method_23317())).set("y", Value.number(entity.method_23318())).set("z", Value.number(entity.method_23321())))));
   }

   public static Value wrap(HorizontalDirection dir) {
      return Value.map((new ValueMap()).set("_toString", Value.string(dir.name + " " + dir.axis)).set("name", Value.string(dir.name)).set("axis", Value.string(dir.axis)));
   }

   public static Value wrap(class_1293 effectInstance) {
      return Value.map((new ValueMap()).set("duration", (double)effectInstance.method_5584()).set("level", (double)(effectInstance.method_5578() + 1)));
   }
}
