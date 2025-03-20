package meteordevelopment.meteorclient.systems.hud.elements;

import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.ObjectIntImmutablePair;
import it.unimi.dsi.fastutil.objects.ObjectIntPair;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.renderer.Renderer2D;
import meteordevelopment.meteorclient.renderer.text.TextRenderer;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnchantmentListSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.config.Config;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.entity.EntityUtils;
import meteordevelopment.meteorclient.utils.entity.SortPriority;
import meteordevelopment.meteorclient.utils.entity.TargetUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.class_1657;
import net.minecraft.class_1748;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1829;
import net.minecraft.class_1887;
import net.minecraft.class_1890;
import net.minecraft.class_3532;
import net.minecraft.class_4587;
import net.minecraft.class_490;
import net.minecraft.class_5321;
import net.minecraft.class_6880;
import net.minecraft.class_9304;
import net.minecraft.class_9636;
import org.joml.Matrix4fStack;

public class CombatHud extends HudElement {
   private static final Color GREEN = new Color(15, 255, 15);
   private static final Color RED = new Color(255, 15, 15);
   private static final Color BLACK = new Color(0, 0, 0, 255);
   public static final HudElementInfo<CombatHud> INFO;
   private final SettingGroup sgGeneral;
   private final Setting<Double> scale;
   private final Setting<Double> range;
   private final Setting<Boolean> displayPing;
   private final Setting<Boolean> displayDistance;
   private final Setting<Set<class_5321<class_1887>>> displayedEnchantments;
   private final Setting<SettingColor> backgroundColor;
   private final Setting<SettingColor> enchantmentTextColor;
   private final Setting<SettingColor> pingColor1;
   private final Setting<SettingColor> pingColor2;
   private final Setting<SettingColor> pingColor3;
   private final Setting<SettingColor> distColor1;
   private final Setting<SettingColor> distColor2;
   private final Setting<SettingColor> distColor3;
   private final Setting<SettingColor> healthColor1;
   private final Setting<SettingColor> healthColor2;
   private final Setting<SettingColor> healthColor3;
   private class_1657 playerEntity;

   public CombatHud() {
      super(INFO);
      this.sgGeneral = this.settings.getDefaultGroup();
      this.scale = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("scale")).description("The scale.")).defaultValue(2.0D).min(1.0D).sliderRange(1.0D, 5.0D).onChanged((aDouble) -> {
         this.calculateSize();
      })).build());
      this.range = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("range")).description("The range to target players.")).defaultValue(100.0D).min(1.0D).sliderMax(200.0D).build());
      this.displayPing = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("ping")).description("Shows the player's ping.")).defaultValue(true)).build());
      this.displayDistance = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("distance")).description("Shows the distance between you and the player.")).defaultValue(true)).build());
      this.displayedEnchantments = this.sgGeneral.add(((EnchantmentListSetting.Builder)((EnchantmentListSetting.Builder)(new EnchantmentListSetting.Builder()).name("displayed-enchantments")).description("The enchantments that are shown on nametags.")).vanillaDefaults().build());
      this.backgroundColor = this.sgGeneral.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("background-color")).description("Color of background.")).defaultValue(new SettingColor(0, 0, 0, 64)).build());
      this.enchantmentTextColor = this.sgGeneral.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("enchantment-color")).description("Color of enchantment text.")).defaultValue(new SettingColor(255, 255, 255)).build());
      SettingGroup var10001 = this.sgGeneral;
      ColorSetting.Builder var10002 = ((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("ping-stage-1")).description("Color of ping text when under 75.")).defaultValue(new SettingColor(15, 255, 15));
      Setting var10003 = this.displayPing;
      Objects.requireNonNull(var10003);
      this.pingColor1 = var10001.add(((ColorSetting.Builder)var10002.visible(var10003::get)).build());
      var10001 = this.sgGeneral;
      var10002 = ((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("ping-stage-2")).description("Color of ping text when between 75 and 200.")).defaultValue(new SettingColor(255, 150, 15));
      var10003 = this.displayPing;
      Objects.requireNonNull(var10003);
      this.pingColor2 = var10001.add(((ColorSetting.Builder)var10002.visible(var10003::get)).build());
      var10001 = this.sgGeneral;
      var10002 = ((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("ping-stage-3")).description("Color of ping text when over 200.")).defaultValue(new SettingColor(255, 15, 15));
      var10003 = this.displayPing;
      Objects.requireNonNull(var10003);
      this.pingColor3 = var10001.add(((ColorSetting.Builder)var10002.visible(var10003::get)).build());
      var10001 = this.sgGeneral;
      var10002 = ((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("distance-stage-1")).description("The color when a player is within 10 blocks of you.")).defaultValue(new SettingColor(255, 15, 15));
      var10003 = this.displayDistance;
      Objects.requireNonNull(var10003);
      this.distColor1 = var10001.add(((ColorSetting.Builder)var10002.visible(var10003::get)).build());
      var10001 = this.sgGeneral;
      var10002 = ((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("distance-stage-2")).description("The color when a player is within 50 blocks of you.")).defaultValue(new SettingColor(255, 150, 15));
      var10003 = this.displayDistance;
      Objects.requireNonNull(var10003);
      this.distColor2 = var10001.add(((ColorSetting.Builder)var10002.visible(var10003::get)).build());
      var10001 = this.sgGeneral;
      var10002 = ((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("distance-stage-3")).description("The color when a player is greater then 50 blocks away from you.")).defaultValue(new SettingColor(15, 255, 15));
      var10003 = this.displayDistance;
      Objects.requireNonNull(var10003);
      this.distColor3 = var10001.add(((ColorSetting.Builder)var10002.visible(var10003::get)).build());
      this.healthColor1 = this.sgGeneral.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("health-stage-1")).description("The color on the left of the health gradient.")).defaultValue(new SettingColor(255, 15, 15)).build());
      this.healthColor2 = this.sgGeneral.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("health-stage-2")).description("The color in the middle of the health gradient.")).defaultValue(new SettingColor(255, 150, 15)).build());
      this.healthColor3 = this.sgGeneral.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("health-stage-3")).description("The color on the right of the health gradient.")).defaultValue(new SettingColor(15, 255, 15)).build());
      this.calculateSize();
   }

   private void calculateSize() {
      this.setSize(175.0D * (Double)this.scale.get(), 95.0D * (Double)this.scale.get());
   }

   public void render(HudRenderer renderer) {
      renderer.post(() -> {
         double x = (double)this.x;
         double y = (double)this.y;
         Color primaryColor = TextHud.getSectionColor(0);
         Color secondaryColor = TextHud.getSectionColor(1);
         if (this.isInEditor()) {
            this.playerEntity = MeteorClient.mc.field_1724;
         } else {
            this.playerEntity = TargetUtils.getPlayerTarget((Double)this.range.get(), SortPriority.LowestDistance);
         }

         if (this.playerEntity != null || this.isInEditor()) {
            Renderer2D.COLOR.begin();
            Renderer2D.COLOR.quad(x, y, (double)this.getWidth(), (double)this.getHeight(), (Color)this.backgroundColor.get());
            if (this.playerEntity == null) {
               if (this.isInEditor()) {
                  renderer.line(x, y, x + (double)this.getWidth(), y + (double)this.getHeight(), Color.GRAY);
                  renderer.line(x + (double)this.getWidth(), y, x, y + (double)this.getHeight(), Color.GRAY);
                  Renderer2D.COLOR.render((class_4587)null);
               }

            } else {
               Renderer2D.COLOR.render((class_4587)null);
               class_490.method_2486(renderer.drawContext, (int)x, (int)y, (int)(x + 25.0D * (Double)this.scale.get()), (int)(y + 66.0D * (Double)this.scale.get()), (int)(30.0D * (Double)this.scale.get()), 0.0F, -class_3532.method_15393(this.playerEntity.field_5982 + (this.playerEntity.method_36454() - this.playerEntity.field_5982) * MeteorClient.mc.method_60646().method_60637(true)), -this.playerEntity.method_36455(), this.playerEntity);
               x += 50.0D * (Double)this.scale.get();
               y += 5.0D * (Double)this.scale.get();
               String breakText = " | ";
               String nameText = this.playerEntity.method_5477().getString();
               Color nameColor = PlayerUtils.getPlayerColor(this.playerEntity, primaryColor);
               int ping = EntityUtils.getPing(this.playerEntity);
               String pingText = ping + "ms";
               Color pingColor;
               if (ping <= 75) {
                  pingColor = (Color)this.pingColor1.get();
               } else if (ping <= 200) {
                  pingColor = (Color)this.pingColor2.get();
               } else {
                  pingColor = (Color)this.pingColor3.get();
               }

               double dist = 0.0D;
               if (!this.isInEditor()) {
                  dist = (double)Math.round((double)MeteorClient.mc.field_1724.method_5739(this.playerEntity) * 100.0D) / 100.0D;
               }

               String distText = dist + "m";
               Color distColor;
               if (dist <= 10.0D) {
                  distColor = (Color)this.distColor1.get();
               } else if (dist <= 50.0D) {
                  distColor = (Color)this.distColor2.get();
               } else {
                  distColor = (Color)this.distColor3.get();
               }

               String friendText = "Unknown";
               Color friendColor = primaryColor;
               if (Friends.get().isFriend(this.playerEntity)) {
                  friendText = "Friend";
                  friendColor = (Color)Config.get().friendColor.get();
               } else {
                  boolean naked = true;

                  for(int positionx = 3; positionx >= 0; --positionx) {
                     class_1799 itemStack = this.getItem(positionx);
                     if (!itemStack.method_7960()) {
                        naked = false;
                     }
                  }

                  if (naked) {
                     friendText = "Naked";
                     friendColor = GREEN;
                  } else {
                     boolean threat = false;

                     for(int position = 5; position >= 0; --position) {
                        class_1799 itemStackx = this.getItem(position);
                        if (itemStackx.method_7909() instanceof class_1829 || itemStackx.method_7909() == class_1802.field_8301 || itemStackx.method_7909() == class_1802.field_23141 || itemStackx.method_7909() instanceof class_1748) {
                           threat = true;
                        }
                     }

                     if (threat) {
                        friendText = "Threat";
                        friendColor = RED;
                     }
                  }
               }

               TextRenderer.get().begin(0.45D * (Double)this.scale.get(), false, true);
               double breakWidth = TextRenderer.get().getWidth(breakText);
               double pingWidth = TextRenderer.get().getWidth(pingText);
               double friendWidth = TextRenderer.get().getWidth(friendText);
               TextRenderer.get().render(nameText, x, y, nameColor != null ? nameColor : primaryColor);
               y += TextRenderer.get().getHeight();
               TextRenderer.get().render(friendText, x, y, friendColor);
               if ((Boolean)this.displayPing.get()) {
                  TextRenderer.get().render(breakText, x + friendWidth, y, secondaryColor);
                  TextRenderer.get().render(pingText, x + friendWidth + breakWidth, y, pingColor);
                  if ((Boolean)this.displayDistance.get()) {
                     TextRenderer.get().render(breakText, x + friendWidth + breakWidth + pingWidth, y, secondaryColor);
                     TextRenderer.get().render(distText, x + friendWidth + breakWidth + pingWidth + breakWidth, y, distColor);
                  }
               } else if ((Boolean)this.displayDistance.get()) {
                  TextRenderer.get().render(breakText, x + friendWidth, y, secondaryColor);
                  TextRenderer.get().render(distText, x + friendWidth + breakWidth, y, distColor);
               }

               TextRenderer.get().end();
               y += 10.0D * (Double)this.scale.get();
               int slot = 5;
               Matrix4fStack matrices = RenderSystem.getModelViewStack();
               matrices.pushMatrix();
               matrices.scale(((Double)this.scale.get()).floatValue(), ((Double)this.scale.get()).floatValue(), 1.0F);
               x /= (Double)this.scale.get();
               y /= (Double)this.scale.get();
               TextRenderer.get().begin(0.35D, false, true);

               double enchX;
               for(int positionxx = 0; positionxx < 6; ++positionxx) {
                  double armorX = x + (double)(positionxx * 20);
                  class_1799 itemStackxx = this.getItem(slot);
                  renderer.item(itemStackxx, (int)(armorX * (Double)this.scale.get()), (int)(y * (Double)this.scale.get()), ((Double)this.scale.get()).floatValue(), true);
                  double armorY = y + 18.0D;
                  class_9304 enchantments = class_1890.method_57532(itemStackxx);
                  List<ObjectIntPair<class_6880<class_1887>>> enchantmentsToShow = new ArrayList();
                  Iterator var36 = enchantments.method_57539().iterator();

                  while(var36.hasNext()) {
                     Entry<class_6880<class_1887>> entryx = (Entry)var36.next();
                     class_6880 var10000 = (class_6880)entryx.getKey();
                     Set var10001 = (Set)this.displayedEnchantments.get();
                     Objects.requireNonNull(var10001);
                     if (var10000.method_40224(var10001::contains)) {
                        enchantmentsToShow.add(new ObjectIntImmutablePair((class_6880)entryx.getKey(), entryx.getIntValue()));
                     }
                  }

                  for(var36 = enchantmentsToShow.iterator(); var36.hasNext(); armorY += TextRenderer.get().getHeight()) {
                     ObjectIntPair<class_6880<class_1887>> entry = (ObjectIntPair)var36.next();
                     String var45 = Utils.getEnchantSimpleName((class_6880)entry.left(), 3);
                     String enchantName = var45 + " " + entry.rightInt();
                     enchX = armorX + 8.0D - TextRenderer.get().getWidth(enchantName) / 2.0D;
                     TextRenderer.get().render(enchantName, enchX, armorY, ((class_6880)entry.left()).method_40220(class_9636.field_51551) ? RED : (Color)this.enchantmentTextColor.get());
                  }

                  --slot;
               }

               TextRenderer.get().end();
               y = (double)((int)((double)this.y + 75.0D * (Double)this.scale.get()));
               x = (double)this.x;
               x /= (Double)this.scale.get();
               y /= (Double)this.scale.get();
               x += 5.0D;
               y += 5.0D;
               Renderer2D.COLOR.begin();
               Renderer2D.COLOR.boxLines(x, y, 165.0D, 11.0D, BLACK);
               Renderer2D.COLOR.render((class_4587)null);
               x += 2.0D;
               y += 2.0D;
               float maxHealth = this.playerEntity.method_6063();
               int maxAbsorb = 16;
               int maxTotal = (int)(maxHealth + (float)maxAbsorb);
               int totalHealthWidth = (int)(161.0F * maxHealth / (float)maxTotal);
               int totalAbsorbWidth = 161 * maxAbsorb / maxTotal;
               float health = this.playerEntity.method_6032();
               float absorb = this.playerEntity.method_6067();
               enchX = (double)(health / maxHealth);
               double absorbPercent = (double)(absorb / (float)maxAbsorb);
               int healthWidth = (int)((double)totalHealthWidth * enchX);
               int absorbWidth = (int)((double)totalAbsorbWidth * absorbPercent);
               Renderer2D.COLOR.begin();
               Renderer2D.COLOR.quad(x, y, (double)healthWidth, 7.0D, (Color)this.healthColor1.get(), (Color)this.healthColor2.get(), (Color)this.healthColor2.get(), (Color)this.healthColor1.get());
               Renderer2D.COLOR.quad(x + (double)healthWidth, y, (double)absorbWidth, 7.0D, (Color)this.healthColor2.get(), (Color)this.healthColor3.get(), (Color)this.healthColor3.get(), (Color)this.healthColor2.get());
               Renderer2D.COLOR.render((class_4587)null);
               matrices.popMatrix();
            }
         }
      });
   }

   private class_1799 getItem(int i) {
      class_1799 var10000;
      if (this.isInEditor()) {
         switch(i) {
         case 0:
            var10000 = class_1802.field_8301.method_7854();
            break;
         case 1:
            var10000 = class_1802.field_22030.method_7854();
            break;
         case 2:
            var10000 = class_1802.field_22029.method_7854();
            break;
         case 3:
            var10000 = class_1802.field_22028.method_7854();
            break;
         case 4:
            var10000 = class_1802.field_22027.method_7854();
            break;
         case 5:
            var10000 = class_1802.field_8288.method_7854();
            break;
         default:
            var10000 = class_1799.field_8037;
         }

         return var10000;
      } else if (this.playerEntity == null) {
         return class_1799.field_8037;
      } else {
         switch(i) {
         case 4:
            var10000 = this.playerEntity.method_6079();
            break;
         case 5:
            var10000 = this.playerEntity.method_6047();
            break;
         default:
            var10000 = this.playerEntity.method_31548().method_7372(i);
         }

         return var10000;
      }
   }

   static {
      INFO = new HudElementInfo(Hud.GROUP, "combat", "Displays information about your combat target.", CombatHud::new);
   }
}
