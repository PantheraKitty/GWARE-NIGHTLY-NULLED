package meteordevelopment.meteorclient.utils;

import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Reference2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Locale;
import java.util.Random;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.gui.tabs.TabScreen;
import meteordevelopment.meteorclient.mixin.ClientPlayNetworkHandlerAccessor;
import meteordevelopment.meteorclient.mixin.ContainerComponentAccessor;
import meteordevelopment.meteorclient.mixin.MinecraftClientAccessor;
import meteordevelopment.meteorclient.mixin.MinecraftServerAccessor;
import meteordevelopment.meteorclient.mixin.ReloadStateAccessor;
import meteordevelopment.meteorclient.mixin.ResourceReloadLoggerAccessor;
import meteordevelopment.meteorclient.mixininterface.IMinecraftClient;
import meteordevelopment.meteorclient.settings.StatusEffectAmplifierMapSetting;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.BetterTooltips;
import meteordevelopment.meteorclient.systems.modules.world.Timer;
import meteordevelopment.meteorclient.utils.misc.Names;
import meteordevelopment.meteorclient.utils.player.EChestMemory;
import meteordevelopment.meteorclient.utils.render.PeekScreen;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.world.BlockEntityIterator;
import meteordevelopment.meteorclient.utils.world.ChunkIterator;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1291;
import net.minecraft.class_1297;
import net.minecraft.class_1747;
import net.minecraft.class_1753;
import net.minecraft.class_1764;
import net.minecraft.class_1767;
import net.minecraft.class_1771;
import net.minecraft.class_1776;
import net.minecraft.class_1779;
import net.minecraft.class_1787;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1803;
import net.minecraft.class_1823;
import net.minecraft.class_1828;
import net.minecraft.class_1835;
import net.minecraft.class_1887;
import net.minecraft.class_1890;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2371;
import net.minecraft.class_243;
import net.minecraft.class_2480;
import net.minecraft.class_2487;
import net.minecraft.class_2499;
import net.minecraft.class_2586;
import net.minecraft.class_2791;
import net.minecraft.class_3532;
import net.minecraft.class_437;
import net.minecraft.class_442;
import net.minecraft.class_500;
import net.minecraft.class_526;
import net.minecraft.class_5321;
import net.minecraft.class_6880;
import net.minecraft.class_8251;
import net.minecraft.class_9279;
import net.minecraft.class_9304;
import net.minecraft.class_9323;
import net.minecraft.class_9334;
import net.minecraft.class_6360.class_6363;
import net.minecraft.class_9304.class_9305;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Range;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.lwjgl.glfw.GLFW;

public class Utils {
   public static final Pattern FILE_NAME_INVALID_CHARS_PATTERN = Pattern.compile("[\\s\\\\/:*?\"<>|]");
   public static final Color WHITE = new Color(255, 255, 255);
   private static final Random random = new Random();
   public static boolean firstTimeTitleScreen = true;
   public static boolean isReleasingTrident;
   public static boolean rendering3D = true;
   public static double frameTime;
   public static class_437 screenToOpen;
   public static class_8251 vertexSorter;

   private Utils() {
   }

   @PreInit
   public static void init() {
      MeteorClient.EVENT_BUS.subscribe(Utils.class);
   }

   @EventHandler
   private static void onTick(TickEvent.Post event) {
      if (screenToOpen != null && MeteorClient.mc.field_1755 == null) {
         MeteorClient.mc.method_1507(screenToOpen);
         screenToOpen = null;
      }

   }

   public static class_243 getPlayerSpeed() {
      if (MeteorClient.mc.field_1724 == null) {
         return class_243.field_1353;
      } else {
         double tX = MeteorClient.mc.field_1724.method_23317() - MeteorClient.mc.field_1724.field_6014;
         double tY = MeteorClient.mc.field_1724.method_23318() - MeteorClient.mc.field_1724.field_6036;
         double tZ = MeteorClient.mc.field_1724.method_23321() - MeteorClient.mc.field_1724.field_5969;
         Timer timer = (Timer)Modules.get().get(Timer.class);
         if (timer.isActive()) {
            tX *= timer.getMultiplier();
            tY *= timer.getMultiplier();
            tZ *= timer.getMultiplier();
         }

         tX *= 20.0D;
         tY *= 20.0D;
         tZ *= 20.0D;
         return new class_243(tX, tY, tZ);
      }
   }

   public static String getWorldTime() {
      if (MeteorClient.mc.field_1687 == null) {
         return "00:00";
      } else {
         int ticks = (int)(MeteorClient.mc.field_1687.method_8532() % 24000L);
         ticks += 6000;
         if (ticks > 24000) {
            ticks -= 24000;
         }

         return String.format("%02d:%02d", ticks / 1000, (int)((double)(ticks % 1000) / 1000.0D * 60.0D));
      }
   }

   public static Iterable<class_2791> chunks(boolean onlyWithLoadedNeighbours) {
      return () -> {
         return new ChunkIterator(onlyWithLoadedNeighbours);
      };
   }

   public static Iterable<class_2791> chunks() {
      return chunks(false);
   }

   public static Iterable<class_2586> blockEntities() {
      return BlockEntityIterator::new;
   }

   public static void getEnchantments(class_1799 itemStack, Object2IntMap<class_6880<class_1887>> enchantments) {
      enchantments.clear();
      if (!itemStack.method_7960()) {
         Set<Entry<class_6880<class_1887>>> itemEnchantments = itemStack.method_7909() == class_1802.field_8598 ? ((class_9304)itemStack.method_57824(class_9334.field_49643)).method_57539() : itemStack.method_58657().method_57539();
         Iterator var3 = itemEnchantments.iterator();

         while(var3.hasNext()) {
            Entry<class_6880<class_1887>> entry = (Entry)var3.next();
            enchantments.put((class_6880)entry.getKey(), entry.getIntValue());
         }
      }

   }

   public static int getEnchantmentLevel(class_1799 itemStack, class_5321<class_1887> enchantment) {
      if (itemStack.method_7960()) {
         return 0;
      } else {
         Object2IntMap<class_6880<class_1887>> itemEnchantments = new Object2IntArrayMap();
         getEnchantments(itemStack, itemEnchantments);
         return getEnchantmentLevel((Object2IntMap)itemEnchantments, enchantment);
      }
   }

   public static int getEnchantmentLevel(Object2IntMap<class_6880<class_1887>> itemEnchantments, class_5321<class_1887> enchantment) {
      ObjectIterator var2 = Object2IntMaps.fastIterable(itemEnchantments).iterator();

      Entry entry;
      do {
         if (!var2.hasNext()) {
            return 0;
         }

         entry = (Entry)var2.next();
      } while(!((class_6880)entry.getKey()).method_40225(enchantment));

      return entry.getIntValue();
   }

   @SafeVarargs
   public static boolean hasEnchantments(class_1799 itemStack, class_5321<class_1887>... enchantments) {
      if (itemStack.method_7960()) {
         return false;
      } else {
         Object2IntMap<class_6880<class_1887>> itemEnchantments = new Object2IntArrayMap();
         getEnchantments(itemStack, itemEnchantments);
         class_5321[] var3 = enchantments;
         int var4 = enchantments.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            class_5321<class_1887> enchantment = var3[var5];
            if (!hasEnchantment((Object2IntMap)itemEnchantments, enchantment)) {
               return false;
            }
         }

         return true;
      }
   }

   public static boolean hasEnchantment(class_1799 itemStack, class_5321<class_1887> enchantmentKey) {
      if (itemStack.method_7960()) {
         return false;
      } else {
         Object2IntMap<class_6880<class_1887>> itemEnchantments = new Object2IntArrayMap();
         getEnchantments(itemStack, itemEnchantments);
         return hasEnchantment((Object2IntMap)itemEnchantments, enchantmentKey);
      }
   }

   private static boolean hasEnchantment(Object2IntMap<class_6880<class_1887>> itemEnchantments, class_5321<class_1887> enchantmentKey) {
      ObjectIterator var2 = itemEnchantments.keySet().iterator();

      class_6880 enchantment;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         enchantment = (class_6880)var2.next();
      } while(!enchantment.method_40225(enchantmentKey));

      return true;
   }

   public static int getRenderDistance() {
      return Math.max((Integer)MeteorClient.mc.field_1690.method_42503().method_41753(), ((ClientPlayNetworkHandlerAccessor)MeteorClient.mc.method_1562()).getChunkLoadDistance());
   }

   public static int getWindowWidth() {
      return MeteorClient.mc.method_22683().method_4489();
   }

   public static int getWindowHeight() {
      return MeteorClient.mc.method_22683().method_4506();
   }

   public static void unscaledProjection() {
      vertexSorter = RenderSystem.getVertexSorting();
      RenderSystem.setProjectionMatrix((new Matrix4f()).setOrtho(0.0F, (float)MeteorClient.mc.method_22683().method_4489(), (float)MeteorClient.mc.method_22683().method_4506(), 0.0F, 1000.0F, 21000.0F), class_8251.field_43361);
      rendering3D = false;
   }

   public static void scaledProjection() {
      RenderSystem.setProjectionMatrix((new Matrix4f()).setOrtho(0.0F, (float)((double)MeteorClient.mc.method_22683().method_4489() / MeteorClient.mc.method_22683().method_4495()), (float)((double)MeteorClient.mc.method_22683().method_4506() / MeteorClient.mc.method_22683().method_4495()), 0.0F, 1000.0F, 21000.0F), vertexSorter);
      rendering3D = true;
   }

   public static class_243 vec3d(class_2338 pos) {
      return new class_243((double)pos.method_10263(), (double)pos.method_10264(), (double)pos.method_10260());
   }

   public static boolean openContainer(class_1799 itemStack, class_1799[] contents, boolean pause) {
      if (!hasItems(itemStack) && itemStack.method_7909() != class_1802.field_8466) {
         return false;
      } else {
         getItemsInContainerItem(itemStack, contents);
         if (pause) {
            screenToOpen = new PeekScreen(itemStack, contents);
         } else {
            MeteorClient.mc.method_1507(new PeekScreen(itemStack, contents));
         }

         return true;
      }
   }

   public static void getItemsInContainerItem(class_1799 itemStack, class_1799[] items) {
      if (itemStack.method_7909() == class_1802.field_8466) {
         for(int i = 0; i < EChestMemory.ITEMS.size(); ++i) {
            items[i] = (class_1799)EChestMemory.ITEMS.get(i);
         }

      } else {
         Arrays.fill(items, class_1799.field_8037);
         class_9323 components = itemStack.method_57353();
         int i;
         if (components.method_57832(class_9334.field_49622)) {
            ContainerComponentAccessor container = (ContainerComponentAccessor)components.method_57829(class_9334.field_49622);
            class_2371<class_1799> stacks = container.getStacks();

            for(i = 0; i < stacks.size(); ++i) {
               if (i >= 0 && i < items.length) {
                  items[i] = (class_1799)stacks.get(i);
               }
            }
         } else if (components.method_57832(class_9334.field_49611)) {
            class_9279 nbt2 = (class_9279)components.method_57829(class_9334.field_49611);
            if (nbt2.method_57450("Items")) {
               class_2499 nbt3 = (class_2499)nbt2.method_57463().method_10580("Items");

               for(i = 0; i < nbt3.size(); ++i) {
                  int slot = nbt3.method_10602(i).method_10571("Slot");
                  if (slot >= 0 && slot < items.length) {
                     items[slot] = class_1799.method_57359(MeteorClient.mc.field_1724.method_56673(), nbt3.method_10602(i));
                  }
               }
            }
         }

      }
   }

   public static Color getShulkerColor(class_1799 shulkerItem) {
      class_1792 var2 = shulkerItem.method_7909();
      if (var2 instanceof class_1747) {
         class_1747 blockItem = (class_1747)var2;
         class_2248 block = blockItem.method_7711();
         if (block == class_2246.field_10443) {
            return BetterTooltips.ECHEST_COLOR;
         }

         if (block instanceof class_2480) {
            class_2480 shulkerBlock = (class_2480)block;
            class_1767 dye = shulkerBlock.method_10528();
            if (dye == null) {
               return WHITE;
            }

            int color = dye.method_7787();
            return new Color((float)(color >> 16 & 255), (float)(color >> 8 & 255), (float)(color & 255), 1.0F);
         }
      }

      return WHITE;
   }

   public static boolean hasItems(class_1799 itemStack) {
      ContainerComponentAccessor container = (ContainerComponentAccessor)itemStack.method_57824(class_9334.field_49622);
      if (container != null && !container.getStacks().isEmpty()) {
         return true;
      } else {
         class_2487 compoundTag = ((class_9279)itemStack.method_57825(class_9334.field_49611, class_9279.field_49302)).method_57463();
         return compoundTag != null && compoundTag.method_10573("Items", 9);
      }
   }

   public static Reference2IntMap<class_1291> createStatusEffectMap() {
      return new Reference2IntArrayMap(StatusEffectAmplifierMapSetting.EMPTY_STATUS_EFFECT_MAP);
   }

   public static String getEnchantSimpleName(class_6880<class_1887> enchantment, int length) {
      String name = Names.get(enchantment);
      return name.length() > length ? name.substring(0, length) : name;
   }

   public static boolean searchTextDefault(String text, String filter, boolean caseSensitive) {
      return searchInWords(text, filter) > 0 || searchLevenshteinDefault(text, filter, caseSensitive) < text.length() / 2;
   }

   public static int searchLevenshteinDefault(String text, String filter, boolean caseSensitive) {
      return levenshteinDistance(caseSensitive ? filter : filter.toLowerCase(Locale.ROOT), caseSensitive ? text : text.toLowerCase(Locale.ROOT), 1, 8, 8);
   }

   public static int searchInWords(String text, String filter) {
      if (filter.isEmpty()) {
         return 1;
      } else {
         int wordsFound = 0;
         text = text.toLowerCase(Locale.ROOT);
         String[] words = filter.toLowerCase(Locale.ROOT).split(" ");
         String[] var4 = words;
         int var5 = words.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            String word = var4[var6];
            if (!text.contains(word)) {
               return 0;
            }

            wordsFound += StringUtils.countMatches(text, word);
         }

         return wordsFound;
      }
   }

   public static int levenshteinDistance(String from, String to, int insCost, int subCost, int delCost) {
      int textLength = from.length();
      int filterLength = to.length();
      if (textLength == 0) {
         return filterLength * insCost;
      } else if (filterLength == 0) {
         return textLength * delCost;
      } else {
         int[][] d = new int[textLength + 1][filterLength + 1];

         int i;
         for(i = 0; i <= textLength; ++i) {
            d[i][0] = i * delCost;
         }

         for(i = 0; i <= filterLength; ++i) {
            d[0][i] = i * insCost;
         }

         for(i = 1; i <= textLength; ++i) {
            for(int j = 1; j <= filterLength; ++j) {
               int sCost = d[i - 1][j - 1] + (from.charAt(i - 1) == to.charAt(j - 1) ? 0 : subCost);
               int dCost = d[i - 1][j] + delCost;
               int iCost = d[i][j - 1] + insCost;
               d[i][j] = Math.min(Math.min(dCost, iCost), sCost);
            }
         }

         return d[textLength][filterLength];
      }
   }

   public static double squaredDistance(double x1, double y1, double z1, double x2, double y2, double z2) {
      double dX = x2 - x1;
      double dY = y2 - y1;
      double dZ = z2 - z1;
      return dX * dX + dY * dY + dZ * dZ;
   }

   public static double distance(double x1, double y1, double z1, double x2, double y2, double z2) {
      double dX = x2 - x1;
      double dY = y2 - y1;
      double dZ = z2 - z1;
      return Math.sqrt(dX * dX + dY * dY + dZ * dZ);
   }

   public static String getFileWorldName() {
      return FILE_NAME_INVALID_CHARS_PATTERN.matcher(getWorldName()).replaceAll("_");
   }

   public static String getWorldName() {
      if (MeteorClient.mc.method_1542()) {
         if (MeteorClient.mc.field_1687 == null) {
            return "";
         } else {
            File folder = ((MinecraftServerAccessor)MeteorClient.mc.method_1576()).getSession().method_27424(MeteorClient.mc.field_1687.method_27983()).toFile();
            if (folder.toPath().relativize(MeteorClient.mc.field_1697.toPath()).getNameCount() != 2) {
               folder = folder.getParentFile();
            }

            return folder.getName();
         }
      } else if (MeteorClient.mc.method_1558() != null) {
         return MeteorClient.mc.method_1558().method_52811() ? "realms" : MeteorClient.mc.method_1558().field_3761;
      } else {
         return "";
      }
   }

   public static String nameToTitle(String name) {
      return (String)Arrays.stream(name.split("-")).map(StringUtils::capitalize).collect(Collectors.joining(" "));
   }

   public static String titleToName(String title) {
      return title.replace(" ", "-").toLowerCase(Locale.ROOT);
   }

   public static String getKeyName(int key) {
      String var10000;
      switch(key) {
      case -1:
         var10000 = "Unknown";
         break;
      case 32:
         var10000 = "Space";
         break;
      case 39:
         var10000 = "Apostrophe";
         break;
      case 96:
         var10000 = "Grave Accent";
         break;
      case 161:
         var10000 = "World 1";
         break;
      case 162:
         var10000 = "World 2";
         break;
      case 256:
         var10000 = "Esc";
         break;
      case 257:
         var10000 = "Enter";
         break;
      case 258:
         var10000 = "Tab";
         break;
      case 259:
         var10000 = "Backspace";
         break;
      case 260:
         var10000 = "Insert";
         break;
      case 261:
         var10000 = "Delete";
         break;
      case 262:
         var10000 = "Arrow Right";
         break;
      case 263:
         var10000 = "Arrow Left";
         break;
      case 264:
         var10000 = "Arrow Down";
         break;
      case 265:
         var10000 = "Arrow Up";
         break;
      case 266:
         var10000 = "Page Up";
         break;
      case 267:
         var10000 = "Page Down";
         break;
      case 268:
         var10000 = "Home";
         break;
      case 269:
         var10000 = "End";
         break;
      case 280:
         var10000 = "Caps Lock";
         break;
      case 282:
         var10000 = "Num Lock";
         break;
      case 283:
         var10000 = "Print Screen";
         break;
      case 284:
         var10000 = "Pause";
         break;
      case 290:
         var10000 = "F1";
         break;
      case 291:
         var10000 = "F2";
         break;
      case 292:
         var10000 = "F3";
         break;
      case 293:
         var10000 = "F4";
         break;
      case 294:
         var10000 = "F5";
         break;
      case 295:
         var10000 = "F6";
         break;
      case 296:
         var10000 = "F7";
         break;
      case 297:
         var10000 = "F8";
         break;
      case 298:
         var10000 = "F9";
         break;
      case 299:
         var10000 = "F10";
         break;
      case 300:
         var10000 = "F11";
         break;
      case 301:
         var10000 = "F12";
         break;
      case 302:
         var10000 = "F13";
         break;
      case 303:
         var10000 = "F14";
         break;
      case 304:
         var10000 = "F15";
         break;
      case 305:
         var10000 = "F16";
         break;
      case 306:
         var10000 = "F17";
         break;
      case 307:
         var10000 = "F18";
         break;
      case 308:
         var10000 = "F19";
         break;
      case 309:
         var10000 = "F20";
         break;
      case 310:
         var10000 = "F21";
         break;
      case 311:
         var10000 = "F22";
         break;
      case 312:
         var10000 = "F23";
         break;
      case 313:
         var10000 = "F24";
         break;
      case 314:
         var10000 = "F25";
         break;
      case 335:
         var10000 = "Numpad Enter";
         break;
      case 340:
         var10000 = "Left Shift";
         break;
      case 341:
         var10000 = "Left Control";
         break;
      case 342:
         var10000 = "Left Alt";
         break;
      case 343:
         var10000 = "Left Super";
         break;
      case 344:
         var10000 = "Right Shift";
         break;
      case 345:
         var10000 = "Right Control";
         break;
      case 346:
         var10000 = "Right Alt";
         break;
      case 347:
         var10000 = "Right Super";
         break;
      case 348:
         var10000 = "Menu";
         break;
      default:
         String keyName = GLFW.glfwGetKeyName(key, 0);
         var10000 = keyName == null ? "Unknown" : StringUtils.capitalize(keyName);
      }

      return var10000;
   }

   public static String getButtonName(int button) {
      String var10000;
      switch(button) {
      case -1:
         var10000 = "Unknown";
         break;
      case 0:
         var10000 = "Mouse Left";
         break;
      case 1:
         var10000 = "Mouse Right";
         break;
      case 2:
         var10000 = "Mouse Middle";
         break;
      default:
         var10000 = "Mouse " + button;
      }

      return var10000;
   }

   public static byte[] readBytes(InputStream in) {
      byte[] var2;
      try {
         byte[] var1 = in.readAllBytes();
         return var1;
      } catch (IOException var6) {
         MeteorClient.LOG.error("Error reading from stream.", var6);
         var2 = new byte[0];
      } finally {
         IOUtils.closeQuietly(in);
      }

      return var2;
   }

   public static boolean canUpdate() {
      return MeteorClient.mc != null && MeteorClient.mc.field_1687 != null && MeteorClient.mc.field_1724 != null;
   }

   public static boolean canOpenGui() {
      if (canUpdate()) {
         return MeteorClient.mc.field_1755 == null;
      } else {
         return MeteorClient.mc.field_1755 instanceof class_442 || MeteorClient.mc.field_1755 instanceof class_500 || MeteorClient.mc.field_1755 instanceof class_526;
      }
   }

   public static boolean canCloseGui() {
      return MeteorClient.mc.field_1755 instanceof TabScreen;
   }

   public static int random(int min, int max) {
      return random.nextInt(max - min) + min;
   }

   public static double random(double min, double max) {
      return min + (max - min) * random.nextDouble();
   }

   public static void leftClick() {
      MeteorClient.mc.field_1690.field_1886.method_23481(true);
      ((MinecraftClientAccessor)MeteorClient.mc).leftClick();
      MeteorClient.mc.field_1690.field_1886.method_23481(false);
   }

   public static void rightClick() {
      ((IMinecraftClient)MeteorClient.mc).meteor_client$rightClick();
   }

   public static boolean isShulker(class_1792 item) {
      return item == class_1802.field_8545 || item == class_1802.field_8722 || item == class_1802.field_8380 || item == class_1802.field_8050 || item == class_1802.field_8829 || item == class_1802.field_8271 || item == class_1802.field_8548 || item == class_1802.field_8520 || item == class_1802.field_8627 || item == class_1802.field_8451 || item == class_1802.field_8213 || item == class_1802.field_8816 || item == class_1802.field_8350 || item == class_1802.field_8584 || item == class_1802.field_8461 || item == class_1802.field_8676 || item == class_1802.field_8268;
   }

   public static boolean isThrowable(class_1792 item) {
      return item instanceof class_1779 || item instanceof class_1753 || item instanceof class_1764 || item instanceof class_1823 || item instanceof class_1771 || item instanceof class_1776 || item instanceof class_1828 || item instanceof class_1803 || item instanceof class_1787 || item instanceof class_1835;
   }

   public static void addEnchantment(class_1799 itemStack, class_6880<class_1887> enchantment, int level) {
      class_9305 b = new class_9305(class_1890.method_57532(itemStack));
      b.method_57550(enchantment, level);
      class_1890.method_57530(itemStack, b.method_57549());
   }

   public static void clearEnchantments(class_1799 itemStack) {
      class_1890.method_57531(itemStack, (components) -> {
         components.method_57548((a) -> {
            return true;
         });
      });
   }

   public static void removeEnchantment(class_1799 itemStack, class_1887 enchantment) {
      class_1890.method_57531(itemStack, (components) -> {
         components.method_57548((enchantment1) -> {
            return ((class_1887)enchantment1.comp_349()).equals(enchantment);
         });
      });
   }

   public static Color lerp(Color first, Color second, @Range(from = 0L,to = 1L) float v) {
      return new Color((int)((float)first.r * (1.0F - v) + (float)second.r * v), (int)((float)first.g * (1.0F - v) + (float)second.g * v), (int)((float)first.b * (1.0F - v) + (float)second.b * v));
   }

   public static boolean isLoading() {
      class_6363 state = ((ResourceReloadLoggerAccessor)((MinecraftClientAccessor)MeteorClient.mc).getResourceReloadLogger()).getReloadState();
      return state == null || !((ReloadStateAccessor)state).isFinished();
   }

   public static int parsePort(String full) {
      if (full != null && !full.isBlank() && full.contains(":")) {
         int port;
         try {
            port = Integer.parseInt(full.substring(full.lastIndexOf(58) + 1, full.length() - 1));
         } catch (NumberFormatException var3) {
            port = -1;
         }

         return port;
      } else {
         return -1;
      }
   }

   public static String parseAddress(String full) {
      return full != null && !full.isBlank() && full.contains(":") ? full.substring(0, full.lastIndexOf(58)) : full;
   }

   public static boolean resolveAddress(String address) {
      if (address != null && !address.isBlank()) {
         int port = parsePort(address);
         if (port == -1) {
            port = 25565;
         } else {
            address = parseAddress(address);
         }

         return resolveAddress(address, port);
      } else {
         return false;
      }
   }

   public static boolean resolveAddress(String address, int port) {
      if (port > 0 && port <= 65535 && address != null && !address.isBlank()) {
         InetSocketAddress socketAddress = new InetSocketAddress(address, port);
         return !socketAddress.isUnresolved();
      } else {
         return false;
      }
   }

   public static Vector3d set(Vector3d vec, class_243 v) {
      vec.x = v.field_1352;
      vec.y = v.field_1351;
      vec.z = v.field_1350;
      return vec;
   }

   public static Vector3d set(Vector3d vec, class_1297 entity, double tickDelta) {
      vec.x = class_3532.method_16436(tickDelta, entity.field_6038, entity.method_23317());
      vec.y = class_3532.method_16436(tickDelta, entity.field_5971, entity.method_23318());
      vec.z = class_3532.method_16436(tickDelta, entity.field_5989, entity.method_23321());
      return vec;
   }

   public static boolean nameFilter(String text, char character) {
      return character >= 'a' && character <= 'z' || character >= 'A' && character <= 'Z' || character >= '0' && character <= '9' || character == '_' || character == '-' || character == '.' || character == ' ';
   }

   public static boolean ipFilter(String text, char character) {
      if (text.contains(":") && character == ':') {
         return false;
      } else {
         return character >= 'a' && character <= 'z' || character >= 'A' && character <= 'Z' || character >= '0' && character <= '9' || character == '.' || character == '-';
      }
   }
}
