package meteordevelopment.meteorclient.systems.modules.render;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import meteordevelopment.meteorclient.events.game.ItemStackTooltipEvent;
import meteordevelopment.meteorclient.events.render.TooltipDataEvent;
import meteordevelopment.meteorclient.mixin.EntityAccessor;
import meteordevelopment.meteorclient.mixin.EntityBucketItemAccessor;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.KeybindSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.misc.ByteCountDataOutput;
import meteordevelopment.meteorclient.utils.misc.Keybind;
import meteordevelopment.meteorclient.utils.player.EChestMemory;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.tooltip.BannerTooltipComponent;
import meteordevelopment.meteorclient.utils.tooltip.BookTooltipComponent;
import meteordevelopment.meteorclient.utils.tooltip.ContainerTooltipComponent;
import meteordevelopment.meteorclient.utils.tooltip.EntityTooltipComponent;
import meteordevelopment.meteorclient.utils.tooltip.MapTooltipComponent;
import meteordevelopment.meteorclient.utils.tooltip.TextTooltipComponent;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_124;
import net.minecraft.class_1291;
import net.minecraft.class_1292;
import net.minecraft.class_1293;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1745;
import net.minecraft.class_1746;
import net.minecraft.class_1767;
import net.minecraft.class_1785;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2561;
import net.minecraft.class_4174;
import net.minecraft.class_5250;
import net.minecraft.class_5632;
import net.minecraft.class_5761;
import net.minecraft.class_7924;
import net.minecraft.class_9209;
import net.minecraft.class_9262;
import net.minecraft.class_9275;
import net.minecraft.class_9279;
import net.minecraft.class_9298;
import net.minecraft.class_9301;
import net.minecraft.class_9302;
import net.minecraft.class_9307;
import net.minecraft.class_9334;
import net.minecraft.class_4482.class_9309;
import net.minecraft.class_9298.class_8751;
import net.minecraft.class_9307.class_3750;

public class BetterTooltips extends Module {
   public static final Color ECHEST_COLOR = new Color(0, 50, 50);
   private final SettingGroup sgGeneral;
   private final SettingGroup sgPreviews;
   private final SettingGroup sgOther;
   private final SettingGroup sgHideFlags;
   private final Setting<BetterTooltips.DisplayWhen> displayWhen;
   private final Setting<Keybind> keybind;
   private final Setting<Boolean> middleClickOpen;
   private final Setting<Boolean> pauseInCreative;
   private final Setting<Boolean> shulkers;
   private final Setting<Boolean> shulkerCompactTooltip;
   public final Setting<Boolean> echest;
   private final Setting<Boolean> maps;
   public final Setting<Double> mapsScale;
   private final Setting<Boolean> books;
   private final Setting<Boolean> banners;
   private final Setting<Boolean> entitiesInBuckets;
   public final Setting<Boolean> byteSize;
   private final Setting<Boolean> statusEffects;
   private final Setting<Boolean> beehive;
   public final Setting<Boolean> tooltip;
   public final Setting<Boolean> enchantments;
   public final Setting<Boolean> modifiers;
   public final Setting<Boolean> unbreakable;
   public final Setting<Boolean> canDestroy;
   public final Setting<Boolean> canPlaceOn;
   public final Setting<Boolean> additional;
   public final Setting<Boolean> dye;
   public final Setting<Boolean> upgrades;
   private boolean updateTooltips;
   private static final class_1799[] ITEMS = new class_1799[27];

   public BetterTooltips() {
      super(Categories.Render, "better-tooltips", "Displays more useful tooltips for certain items.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgPreviews = this.settings.createGroup("Previews");
      this.sgOther = this.settings.createGroup("Other");
      this.sgHideFlags = this.settings.createGroup("Hide Flags");
      this.displayWhen = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("display-when")).description("When to display previews.")).defaultValue(BetterTooltips.DisplayWhen.Keybind)).onChanged((value) -> {
         this.updateTooltips = true;
      })).build());
      this.keybind = this.sgGeneral.add(((KeybindSetting.Builder)((KeybindSetting.Builder)((KeybindSetting.Builder)((KeybindSetting.Builder)((KeybindSetting.Builder)(new KeybindSetting.Builder()).name("keybind")).description("The bind for keybind mode.")).defaultValue(Keybind.fromKey(342))).visible(() -> {
         return this.displayWhen.get() == BetterTooltips.DisplayWhen.Keybind;
      })).onChanged((value) -> {
         this.updateTooltips = true;
      })).build());
      this.middleClickOpen = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("middle-click-open")).description("Opens a GUI window with the inventory of the storage block or book when you middle click the item.")).defaultValue(true)).build());
      SettingGroup var10001 = this.sgGeneral;
      BoolSetting.Builder var10002 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("pause-in-creative")).description("Pauses middle click open while the player is in creative mode.")).defaultValue(true);
      Setting var10003 = this.middleClickOpen;
      Objects.requireNonNull(var10003);
      this.pauseInCreative = var10001.add(((BoolSetting.Builder)var10002.visible(var10003::get)).build());
      this.shulkers = this.sgPreviews.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("containers")).description("Shows a preview of a containers when hovering over it in an inventory.")).defaultValue(true)).onChanged((value) -> {
         this.updateTooltips = true;
      })).build());
      this.shulkerCompactTooltip = this.sgPreviews.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("compact-shulker-tooltip")).description("Compacts the lines of the shulker tooltip.")).defaultValue(true)).build());
      this.echest = this.sgPreviews.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("echests")).description("Shows a preview of your echest when hovering over it in an inventory.")).defaultValue(true)).onChanged((value) -> {
         this.updateTooltips = true;
      })).build());
      this.maps = this.sgPreviews.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("maps")).description("Shows a preview of a map when hovering over it in an inventory.")).defaultValue(true)).onChanged((value) -> {
         this.updateTooltips = true;
      })).build());
      var10001 = this.sgPreviews;
      DoubleSetting.Builder var1 = ((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("map-scale")).description("The scale of the map preview.")).defaultValue(1.0D).min(0.001D).sliderMax(1.0D);
      var10003 = this.maps;
      Objects.requireNonNull(var10003);
      this.mapsScale = var10001.add(((DoubleSetting.Builder)var1.visible(var10003::get)).build());
      this.books = this.sgPreviews.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("books")).description("Shows contents of a book when hovering over it in an inventory.")).defaultValue(true)).onChanged((value) -> {
         this.updateTooltips = true;
      })).build());
      this.banners = this.sgPreviews.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("banners")).description("Shows banners' patterns when hovering over it in an inventory. Also works with shields.")).defaultValue(true)).onChanged((value) -> {
         this.updateTooltips = true;
      })).build());
      this.entitiesInBuckets = this.sgPreviews.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("entities-in-buckets")).description("Shows entities in buckets when hovering over it in an inventory.")).defaultValue(true)).onChanged((value) -> {
         this.updateTooltips = true;
      })).build());
      this.byteSize = this.sgOther.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("byte-size")).description("Displays an item's size in bytes in the tooltip.")).defaultValue(true)).onChanged((value) -> {
         this.updateTooltips = true;
      })).build());
      this.statusEffects = this.sgOther.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("status-effects")).description("Adds list of status effects to tooltips of food items.")).defaultValue(true)).onChanged((value) -> {
         this.updateTooltips = true;
      })).build());
      this.beehive = this.sgOther.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("beehive")).description("Displays information about a beehive or bee nest.")).defaultValue(true)).onChanged((value) -> {
         this.updateTooltips = true;
      })).build());
      this.tooltip = this.sgHideFlags.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("tooltip")).description("Show the tooltip when it's hidden.")).defaultValue(false)).build());
      this.enchantments = this.sgHideFlags.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("enchantments")).description("Show enchantments when it's hidden.")).defaultValue(false)).build());
      this.modifiers = this.sgHideFlags.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("modifiers")).description("Show item modifiers when it's hidden.")).defaultValue(false)).build());
      this.unbreakable = this.sgHideFlags.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("unbreakable")).description("Show \"Unbreakable\" tag when it's hidden.")).defaultValue(false)).build());
      this.canDestroy = this.sgHideFlags.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("can-destroy")).description("Show \"CanDestroy\" tag when it's hidden.")).defaultValue(false)).build());
      this.canPlaceOn = this.sgHideFlags.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("can-place-on")).description("Show \"CanPlaceOn\" tag when it's hidden.")).defaultValue(false)).build());
      this.additional = this.sgHideFlags.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("additional")).description("Show potion effects, firework status, book author, etc when it's hidden.")).defaultValue(false)).build());
      this.dye = this.sgHideFlags.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("dye")).description("Show dyed item tags when it's hidden.")).defaultValue(false)).build());
      this.upgrades = this.sgHideFlags.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("armor-trim")).description("Show armor trims when it's hidden.")).defaultValue(false)).build());
      this.updateTooltips = false;
   }

   @EventHandler
   private void appendTooltip(ItemStackTooltipEvent event) {
      if (!(Boolean)this.tooltip.get() && event.list().isEmpty()) {
         this.appendPreviewTooltipText(event, false);
      } else {
         if ((Boolean)this.statusEffects.get()) {
            if (event.itemStack().method_7909() == class_1802.field_8766) {
               class_9298 stewEffectsComponent = (class_9298)event.itemStack().method_57824(class_9334.field_49652);
               if (stewEffectsComponent != null) {
                  Iterator var3 = stewEffectsComponent.comp_2416().iterator();

                  while(var3.hasNext()) {
                     class_8751 effectTag = (class_8751)var3.next();
                     class_1293 effect = new class_1293(effectTag.comp_1838(), effectTag.comp_1839(), 0);
                     event.appendStart(this.getStatusText(effect));
                  }
               }
            } else {
               class_4174 food = (class_4174)event.itemStack().method_57824(class_9334.field_50075);
               if (food != null) {
                  food.comp_2495().forEach((e) -> {
                     event.appendStart(this.getStatusText(e.comp_2496()));
                  });
               }
            }
         }

         String count;
         if ((Boolean)this.beehive.get() && (event.itemStack().method_7909() == class_1802.field_20416 || event.itemStack().method_7909() == class_1802.field_20415)) {
            class_9275 blockStateComponent = (class_9275)event.itemStack().method_57824(class_9334.field_49623);
            if (blockStateComponent != null) {
               count = (String)blockStateComponent.comp_2381().get("honey_level");
               event.appendStart(class_2561.method_43470(String.format("%sHoney level: %s%s%s.", class_124.field_1080, class_124.field_1054, count, class_124.field_1080)));
            }

            List<class_9309> bees = (List)event.itemStack().method_57824(class_9334.field_49624);
            if (bees != null) {
               event.appendStart(class_2561.method_43470(String.format("%sBees: %s%d%s.", class_124.field_1080, class_124.field_1054, bees.size(), class_124.field_1080)));
            }
         }

         if ((Boolean)this.byteSize.get()) {
            try {
               event.itemStack().method_57358(this.mc.field_1724.method_56673()).method_10713(ByteCountDataOutput.INSTANCE);
               int byteCount = ByteCountDataOutput.INSTANCE.getCount();
               ByteCountDataOutput.INSTANCE.reset();
               if (byteCount >= 1024) {
                  count = String.format("%.2f kb", (float)byteCount / 1024.0F);
               } else {
                  count = String.format("%d bytes", byteCount);
               }

               event.appendEnd(class_2561.method_43470(count).method_27692(class_124.field_1080));
            } catch (Exception var6) {
               event.appendEnd(class_2561.method_43470("Error getting bytes.").method_27692(class_124.field_1061));
            }
         }

         this.appendPreviewTooltipText(event, true);
      }
   }

   @EventHandler
   private void getTooltipData(TooltipDataEvent event) {
      if (this.previewShulkers() && Utils.hasItems(event.itemStack)) {
         Utils.getItemsInContainerItem(event.itemStack, ITEMS);
         event.tooltipData = new ContainerTooltipComponent(ITEMS, Utils.getShulkerColor(event.itemStack));
      } else if (event.itemStack.method_7909() == class_1802.field_8466 && this.previewEChest()) {
         event.tooltipData = (class_5632)(EChestMemory.isKnown() ? new ContainerTooltipComponent((class_1799[])EChestMemory.ITEMS.toArray(new class_1799[27]), ECHEST_COLOR) : new TextTooltipComponent(class_2561.method_43470("Unknown ender chest inventory.").method_27692(class_124.field_1079)));
      } else if (event.itemStack.method_7909() == class_1802.field_8204 && this.previewMaps()) {
         class_9209 mapIdComponent = (class_9209)event.itemStack.method_57824(class_9334.field_49646);
         if (mapIdComponent != null) {
            event.tooltipData = new MapTooltipComponent(mapIdComponent.comp_2315());
         }
      } else if ((event.itemStack.method_7909() == class_1802.field_8674 || event.itemStack.method_7909() == class_1802.field_8360) && this.previewBooks()) {
         class_2561 page = this.getFirstPage(event.itemStack);
         if (page != null) {
            event.tooltipData = new BookTooltipComponent(page);
         }
      } else if (event.itemStack.method_7909() instanceof class_1746 && this.previewBanners()) {
         event.tooltipData = new BannerTooltipComponent(event.itemStack);
      } else {
         class_1792 var4 = event.itemStack.method_7909();
         if (var4 instanceof class_1745) {
            class_1745 bannerPatternItem = (class_1745)var4;
            if (this.previewBanners()) {
               event.tooltipData = new BannerTooltipComponent(class_1767.field_7944, this.createBannerPatternsComponent(bannerPatternItem));
               return;
            }
         }

         if (event.itemStack.method_7909() == class_1802.field_8255 && this.previewBanners()) {
            if (event.itemStack.method_57824(class_9334.field_49620) != null || !((class_9307)event.itemStack.method_57825(class_9334.field_49619, class_9307.field_49404)).comp_2428().isEmpty()) {
               event.tooltipData = this.createBannerFromShield(event.itemStack);
            }
         } else {
            var4 = event.itemStack.method_7909();
            if (var4 instanceof class_1785) {
               class_1785 bucketItem = (class_1785)var4;
               if (this.previewEntities()) {
                  class_1299<?> type = ((EntityBucketItemAccessor)bucketItem).getEntityType();
                  class_1297 entity = type.method_5883(this.mc.field_1687);
                  if (entity != null) {
                     ((class_5761)entity).method_35170(((class_9279)event.itemStack.method_57824(class_9334.field_49610)).method_57461());
                     ((EntityAccessor)entity).setInWater(true);
                     event.tooltipData = new EntityTooltipComponent(entity);
                  }
               }
            }
         }
      }

   }

   public void applyCompactShulkerTooltip(class_1799 shulkerItem, List<class_2561> tooltip) {
      if (shulkerItem.method_57826(class_9334.field_49626)) {
         tooltip.add(class_2561.method_43470("???????"));
      }

      if (Utils.hasItems(shulkerItem)) {
         Utils.getItemsInContainerItem(shulkerItem, ITEMS);
         Object2IntMap<class_1792> counts = new Object2IntOpenHashMap();
         class_1799[] var4 = ITEMS;
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            class_1799 item = var4[var6];
            if (!item.method_7960()) {
               int count = counts.getInt(item.method_7909());
               counts.put(item.method_7909(), count + item.method_7947());
            }
         }

         counts.keySet().stream().sorted(Comparator.comparingInt((value) -> {
            return -counts.getInt(value);
         })).limit(5L).forEach((itemx) -> {
            class_5250 mutableText = itemx.method_7848().method_27662();
            mutableText.method_10852(class_2561.method_43470(" x").method_27693(String.valueOf(counts.getInt(itemx))).method_27692(class_124.field_1080));
            tooltip.add(mutableText);
         });
         if (counts.size() > 5) {
            tooltip.add(class_2561.method_43469("container.shulkerBox.more", new Object[]{counts.size() - 5}).method_27692(class_124.field_1056));
         }
      }

   }

   private void appendPreviewTooltipText(ItemStackTooltipEvent event, boolean spacer) {
      if (!this.isPressed() && ((Boolean)this.shulkers.get() && Utils.hasItems(event.itemStack()) || event.itemStack().method_7909() == class_1802.field_8466 && (Boolean)this.echest.get() || event.itemStack().method_7909() == class_1802.field_8204 && (Boolean)this.maps.get() || event.itemStack().method_7909() == class_1802.field_8674 && (Boolean)this.books.get() || event.itemStack().method_7909() == class_1802.field_8360 && (Boolean)this.books.get() || event.itemStack().method_7909() instanceof class_1785 && (Boolean)this.entitiesInBuckets.get() || event.itemStack().method_7909() instanceof class_1746 && (Boolean)this.banners.get() || event.itemStack().method_7909() instanceof class_1745 && (Boolean)this.banners.get() || event.itemStack().method_7909() == class_1802.field_8255 && (Boolean)this.banners.get())) {
         if (spacer) {
            event.appendEnd(class_2561.method_43470(""));
         }

         String var10001 = String.valueOf(class_124.field_1054);
         event.appendEnd(class_2561.method_43470("Hold " + var10001 + String.valueOf(this.keybind) + String.valueOf(class_124.field_1070) + " to preview"));
      }

   }

   private class_5250 getStatusText(class_1293 effect) {
      class_5250 text = class_2561.method_43471(effect.method_5586());
      if (effect.method_5578() != 0) {
         text.method_27693(String.format(" %d (%s)", effect.method_5578() + 1, class_1292.method_5577(effect, 1.0F, this.mc.field_1687.method_54719().method_54748()).getString()));
      } else {
         text.method_27693(String.format(" (%s)", class_1292.method_5577(effect, 1.0F, this.mc.field_1687.method_54719().method_54748()).getString()));
      }

      return ((class_1291)effect.method_5579().comp_349()).method_5573() ? text.method_27692(class_124.field_1078) : text.method_27692(class_124.field_1061);
   }

   private class_2561 getFirstPage(class_1799 bookItem) {
      List pages;
      if (bookItem.method_57824(class_9334.field_49653) != null) {
         pages = ((class_9301)bookItem.method_57824(class_9334.field_49653)).comp_2422();
         return pages.isEmpty() ? null : class_2561.method_43470((String)((class_9262)pages.getFirst()).method_57140(false));
      } else if (bookItem.method_57824(class_9334.field_49606) != null) {
         pages = ((class_9302)bookItem.method_57824(class_9334.field_49606)).comp_2422();
         return pages.isEmpty() ? null : (class_2561)((class_9262)pages.getFirst()).method_57140(false);
      } else {
         return null;
      }
   }

   private class_9307 createBannerPatternsComponent(class_1745 item) {
      return (new class_3750()).method_16376(this.mc.field_1724.method_56673().method_46762(class_7924.field_41252).method_46735(item.method_7704()).method_40240(0), class_1767.field_7952).method_57573();
   }

   private BannerTooltipComponent createBannerFromShield(class_1799 shieldItem) {
      class_1767 dyeColor2 = (class_1767)shieldItem.method_57825(class_9334.field_49620, class_1767.field_7952);
      class_9307 bannerPatternsComponent = (class_9307)shieldItem.method_57825(class_9334.field_49619, class_9307.field_49404);
      return new BannerTooltipComponent(dyeColor2, bannerPatternsComponent);
   }

   public boolean middleClickOpen() {
      return this.isActive() && (Boolean)this.middleClickOpen.get() && (!(Boolean)this.pauseInCreative.get() || !this.mc.field_1724.method_56992());
   }

   public boolean previewShulkers() {
      return this.isActive() && this.isPressed() && (Boolean)this.shulkers.get();
   }

   public boolean shulkerCompactTooltip() {
      return this.isActive() && (Boolean)this.shulkerCompactTooltip.get();
   }

   private boolean previewEChest() {
      return this.isPressed() && (Boolean)this.echest.get();
   }

   private boolean previewMaps() {
      return this.isPressed() && (Boolean)this.maps.get();
   }

   private boolean previewBooks() {
      return this.isPressed() && (Boolean)this.books.get();
   }

   private boolean previewBanners() {
      return this.isPressed() && (Boolean)this.banners.get();
   }

   private boolean previewEntities() {
      return this.isPressed() && (Boolean)this.entitiesInBuckets.get();
   }

   private boolean isPressed() {
      return ((Keybind)this.keybind.get()).isPressed() && this.displayWhen.get() == BetterTooltips.DisplayWhen.Keybind || this.displayWhen.get() == BetterTooltips.DisplayWhen.Always;
   }

   public boolean updateTooltips() {
      if (this.updateTooltips && this.isActive()) {
         this.updateTooltips = false;
         return true;
      } else {
         return false;
      }
   }

   public static enum DisplayWhen {
      Keybind,
      Always;

      // $FF: synthetic method
      private static BetterTooltips.DisplayWhen[] $values() {
         return new BetterTooltips.DisplayWhen[]{Keybind, Always};
      }
   }
}
