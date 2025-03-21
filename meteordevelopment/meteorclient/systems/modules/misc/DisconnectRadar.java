package meteordevelopment.meteorclient.systems.modules.misc;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.gui.GuiThemes;
import meteordevelopment.meteorclient.gui.screens.DisconnectRadarScreen;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_437;
import net.minecraft.class_500;

public class DisconnectRadar extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Double> visualRange;
   public final Setting<Boolean> showPops;
   public final Setting<Boolean> showDistance;
   public final Map<String, DisconnectRadar.PlayerInfo> lastSeenPlayers;
   private boolean wasConnected;
   private boolean disconnectPending;

   public DisconnectRadar() {
      super(Categories.Misc, "disconnect-radar", "Shows information about nearby players when you disconnect.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.visualRange = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("visual-range")).description("The range to detect players in blocks.")).defaultValue(64.0D).min(0.0D).sliderRange(0.0D, 128.0D).build());
      this.showPops = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("show-pops")).description("Shows the number of totem pops for each player.")).defaultValue(true)).build());
      this.showDistance = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("show-distance")).description("Shows the distance to each player.")).defaultValue(true)).build());
      this.lastSeenPlayers = new HashMap();
      this.wasConnected = false;
      this.disconnectPending = false;
   }

   public void onActivate() {
      this.lastSeenPlayers.clear();
      this.wasConnected = this.mc.field_1687 != null && this.mc.field_1724 != null;
      this.updatePlayerList();
   }

   public void onDeactivate() {
      this.lastSeenPlayers.clear();
      this.wasConnected = false;
      this.disconnectPending = false;
   }

   public void onTick() {
      if (this.isActive()) {
         boolean isConnected = this.mc.field_1687 != null && this.mc.field_1724 != null;
         if (this.wasConnected && !isConnected) {
            this.disconnectPending = true;
            this.updatePlayerList();
            this.showDisconnectScreen();
         } else if (isConnected) {
            this.updatePlayerList();
         }

         this.wasConnected = isConnected;
      }
   }

   @EventHandler
   private void onGameLeft(GameLeftEvent event) {
      if (this.isActive()) {
         this.updatePlayerList();
         this.showDisconnectScreen();
      }
   }

   private void updatePlayerList() {
      if (this.mc.field_1687 != null && this.mc.field_1724 != null) {
         this.lastSeenPlayers.clear();
         Iterator var1 = this.mc.field_1687.method_18456().iterator();

         while(var1.hasNext()) {
            class_1657 player = (class_1657)var1.next();
            if (player != this.mc.field_1724) {
               double distance = PlayerUtils.distanceTo((class_1297)player);
               if (distance <= (Double)this.visualRange.get()) {
                  int pops = -MeteorClient.INFO.getPops((class_1297)player);
                  this.lastSeenPlayers.put(player.method_5477().getString(), new DisconnectRadar.PlayerInfo(distance, pops));
               }
            }
         }

      }
   }

   private void showDisconnectScreen() {
      if (!(this.mc.field_1755 instanceof DisconnectRadarScreen)) {
         this.mc.execute(() -> {
            this.mc.method_1507(new DisconnectRadarScreen(GuiThemes.get(), new class_500((class_437)null)));
         });
         this.disconnectPending = false;
      }
   }

   public static class PlayerInfo {
      public final double distance;
      public final int pops;

      PlayerInfo(double distance, int pops) {
         this.distance = distance;
         this.pops = pops;
      }
   }
}
