package meteordevelopment.meteorclient.systems.modules.combat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.Map.Entry;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.BlockListSetting;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.LogoutSpots;
import meteordevelopment.meteorclient.utils.entity.EntityUtils;
import meteordevelopment.meteorclient.utils.entity.SortPriority;
import meteordevelopment.meteorclient.utils.entity.TargetUtils;
import meteordevelopment.meteorclient.utils.entity.fakeplayer.FakePlayerEntity;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1657;
import net.minecraft.class_1792;
import net.minecraft.class_1802;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_3959;
import net.minecraft.class_3965;
import net.minecraft.class_2350.class_2353;
import net.minecraft.class_239.class_240;
import net.minecraft.class_3959.class_242;
import net.minecraft.class_3959.class_3960;

public class AutoTrap extends Module {
   private static final double GRAVITY = -0.08D;
   private static final double TERMINAL_VELOCITY = -3.92D;
   private final SettingGroup sgGeneral;
   private final SettingGroup sgPrediction;
   private final SettingGroup sgRender;
   private final Setting<List<class_2248>> blocks;
   private final Setting<Integer> range;
   private final Setting<SortPriority> priority;
   private final Setting<Boolean> pauseEat;
   private final Setting<Boolean> prediction;
   private final Setting<Double> predictionSeconds;
   private final Setting<Boolean> targetLogoutSpots;
   private final Setting<Boolean> render;
   private final Setting<ShapeMode> shapeMode;
   private final Setting<SettingColor> sideColor;
   private final Setting<SettingColor> lineColor;
   private class_1657 target;

   public AutoTrap() {
      super(Categories.Combat, "auto-trap", "Traps people in a box to prevent them from moving.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgPrediction = this.settings.createGroup("Prediction");
      this.sgRender = this.settings.createGroup("Render");
      this.blocks = this.sgGeneral.add(((BlockListSetting.Builder)((BlockListSetting.Builder)(new BlockListSetting.Builder()).name("whitelist")).description("Which blocks to use.")).defaultValue(class_2246.field_10540).build());
      this.range = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("target-range")).description("The range players can be targeted.")).defaultValue(4)).build());
      this.priority = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("target-priority")).description("How to select the player to target.")).defaultValue(SortPriority.LowestHealth)).build());
      this.pauseEat = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("pause-eat")).description("Pauses while eating.")).defaultValue(true)).build());
      this.prediction = this.sgPrediction.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("predicition")).description("Places blocks where the player will be in the future.")).defaultValue(true)).build());
      this.predictionSeconds = this.sgPrediction.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("prediction-amount")).description("The number of seconds to calculate movement into the future. Should be around 1.5x your ping.")).defaultValue(0.1D).min(0.0D).sliderMax(0.4D).build());
      this.targetLogoutSpots = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("target-logout-spots")).description("Targets logout spots that aren't friends.")).defaultValue(true)).build());
      this.render = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("render")).description("Renders an overlay where blocks will be placed.")).defaultValue(true)).build());
      this.shapeMode = this.sgRender.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("shape-mode")).description("How the shapes are rendered.")).defaultValue(ShapeMode.Both)).build());
      this.sideColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("side-color")).description("The side color of the target block rendering.")).defaultValue(new SettingColor(197, 137, 232, 10)).build());
      this.lineColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("line-color")).description("The line color of the target block rendering.")).defaultValue(new SettingColor(197, 137, 232)).build());
   }

   public void onActivate() {
      this.target = null;
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      if (this.target == null || TargetUtils.isBadTarget(this.target, (double)(Integer)this.range.get())) {
         this.target = TargetUtils.getPlayerTarget((double)(Integer)this.range.get(), (SortPriority)this.priority.get());
         if (TargetUtils.isBadTarget(this.target, (double)(Integer)this.range.get())) {
            if ((Boolean)this.targetLogoutSpots.get()) {
               this.target = this.getLogoutSpotTarget();
            }

            if (this.target == null) {
               return;
            }
         }
      }

      if (!(Boolean)this.pauseEat.get() || !this.mc.field_1724.method_6115()) {
         class_1792 useItem = this.findUseItem();
         if (useItem != null) {
            List<class_2338> placePoses = this.getBlockPoses();
            class_243 predictedPoint = this.target.method_33571();
            if ((Boolean)this.prediction.get()) {
               predictedPoint = this.predictPosition(this.target);
            }

            placePoses.sort((x, y) -> {
               return Double.compare(x.method_19770(predictedPoint), y.method_19770(predictedPoint));
            });
            if (MeteorClient.BLOCK.beginPlacement(placePoses, useItem)) {
               placePoses.forEach((blockPos) -> {
                  MeteorClient.BLOCK.placeBlock(class_1802.field_8281, blockPos);
               });
               MeteorClient.BLOCK.endPlacement();
            }
         }
      }
   }

   public boolean isFriend(class_1657 player) {
      return Friends.get().isFriend(player);
   }

   private class_1792 findUseItem() {
      FindItemResult result = InvUtils.findInHotbar((itemStack) -> {
         Iterator var2 = ((List)this.blocks.get()).iterator();

         class_2248 blocks;
         do {
            if (!var2.hasNext()) {
               return false;
            }

            blocks = (class_2248)var2.next();
         } while(blocks.method_8389() != itemStack.method_7909());

         return true;
      });
      return !result.found() ? null : this.mc.field_1724.method_31548().method_5438(result.slot()).method_7909();
   }

   private List<class_2338> getBlockPoses() {
      List<class_2338> placePoses = new ArrayList();
      if (this.target == null) {
         return placePoses;
      } else {
         class_243 targetPos = this.target.method_19538();
         class_2338 targetBlockPos = class_2338.method_49638(targetPos);
         new class_238((double)targetBlockPos.method_10263() - 0.3D, (double)targetBlockPos.method_10264(), (double)targetBlockPos.method_10260() - 0.3D, (double)targetBlockPos.method_10263() + 0.3D, (double)targetBlockPos.method_10264() + 1.8D, (double)targetBlockPos.method_10260() + 0.3D);

         for(int y = -1; y < 3; ++y) {
            if (y != 0) {
               Iterator var6 = class_2353.field_11062.iterator();

               while(var6.hasNext()) {
                  class_2350 dir = (class_2350)var6.next();
                  class_2338 pos = targetBlockPos.method_10069(0, y, 0).method_10093(dir);
                  if (!placePoses.contains(pos)) {
                     placePoses.add(pos);
                  }
               }

               class_2338 pos = targetBlockPos.method_10069(0, y, 0);
               if (!placePoses.contains(pos)) {
                  placePoses.add(pos);
               }
            }
         }

         return placePoses;
      }
   }

   private class_1657 getLogoutSpotTarget() {
      LogoutSpots logoutSpots = (LogoutSpots)Modules.get().get(LogoutSpots.class);
      if (logoutSpots != null) {
         Iterator var2 = LogoutSpots.getLoggedPlayers().entrySet().iterator();

         while(var2.hasNext()) {
            Entry<UUID, LogoutSpots.GhostPlayer> entry = (Entry)var2.next();
            LogoutSpots.GhostPlayer ghost = (LogoutSpots.GhostPlayer)entry.getValue();
            if (!this.isFriend(ghost.playerEntity) && ghost.pos.method_1022(this.mc.field_1724.method_19538()) <= (double)(Integer)this.range.get()) {
               FakePlayerEntity fakePlayer = new FakePlayerEntity(this.mc.field_1724, ghost.name, 20.0F, false);
               fakePlayer.method_5814(ghost.pos.field_1352, ghost.pos.field_1351, ghost.pos.field_1350);
               return fakePlayer;
            }
         }
      }

      return null;
   }

   @EventHandler
   private void onRender(Render3DEvent event) {
      if ((Boolean)this.render.get()) {
         if (this.target != null) {
            List<class_2338> poses = this.getBlockPoses();
            class_243 predictedPoint = this.target.method_33571();
            if ((Boolean)this.prediction.get()) {
               predictedPoint = this.predictPosition(this.target);
            }

            poses.sort((x, y) -> {
               return Double.compare(x.method_19770(predictedPoint), y.method_19770(predictedPoint));
            });
            event.renderer.box((class_238)class_238.method_30048(predictedPoint, 0.1D, 0.1D, 0.1D), Color.RED.a(50), Color.RED.a(50), ShapeMode.Both, 0);
            Iterator var5 = poses.iterator();

            while(var5.hasNext()) {
               class_2338 pos = (class_2338)var5.next();
               if (BlockUtils.canPlace(pos, true)) {
                  event.renderer.box((class_2338)pos, (Color)this.sideColor.get(), (Color)this.lineColor.get(), (ShapeMode)this.shapeMode.get(), 0);
               }
            }
         }

         if ((Boolean)this.targetLogoutSpots.get()) {
            LogoutSpots logoutSpots = (LogoutSpots)Modules.get().get(LogoutSpots.class);
            if (logoutSpots != null) {
               Iterator var8 = LogoutSpots.getLoggedPlayers().entrySet().iterator();

               while(var8.hasNext()) {
                  Entry<UUID, LogoutSpots.GhostPlayer> entry = (Entry)var8.next();
                  LogoutSpots.GhostPlayer ghost = (LogoutSpots.GhostPlayer)entry.getValue();
                  if (!this.isFriend(ghost.playerEntity)) {
                     event.renderer.box((class_238)ghost.hitbox, (Color)this.sideColor.get(), (Color)this.lineColor.get(), (ShapeMode)this.shapeMode.get(), 0);
                  }
               }
            }
         }

      }
   }

   public String getInfoString() {
      return EntityUtils.getName(this.target);
   }

   public class_243 predictPosition(class_1657 player) {
      class_243 currentPosition = player.method_19538();
      class_243 currentVelocity = player.method_18798();
      int ticks = (int)Math.ceil((Double)this.predictionSeconds.get() * 20.0D);
      class_243 predictedPosition = currentPosition;
      class_243 velocity = currentVelocity;

      for(int i = 0; i < ticks; ++i) {
         if (!player.method_6128()) {
            velocity = velocity.method_1031(0.0D, -0.08D, 0.0D);
            if (velocity.field_1351 < -3.92D) {
               velocity = new class_243(velocity.field_1352, -3.92D, velocity.field_1350);
            }
         }

         predictedPosition = predictedPosition.method_1019(velocity);
         double groundLevel = this.getGroundLevel(predictedPosition);
         if (predictedPosition.field_1351 <= groundLevel) {
            predictedPosition = new class_243(predictedPosition.field_1352, groundLevel, predictedPosition.field_1350);
            velocity = new class_243(velocity.field_1352, 0.0D, velocity.field_1350);
         }
      }

      return predictedPosition;
   }

   private double getGroundLevel(class_243 position) {
      if (this.mc.field_1687 == null) {
         return 0.0D;
      } else {
         class_243 rayStart = new class_243(position.field_1352, position.field_1351, position.field_1350);
         class_243 rayEnd = new class_243(position.field_1352, position.field_1351 - 256.0D, position.field_1350);
         class_3965 hitResult = this.mc.field_1687.method_17742(new class_3959(rayStart, rayEnd, class_3960.field_17559, class_242.field_1348, this.mc.field_1724));
         return hitResult != null && hitResult.method_17783() == class_240.field_1332 ? hitResult.method_17784().field_1351 : 0.0D;
      }
   }

   public static enum BottomMode {
      Single,
      Platform,
      Full,
      None;

      // $FF: synthetic method
      private static AutoTrap.BottomMode[] $values() {
         return new AutoTrap.BottomMode[]{Single, Platform, Full, None};
      }
   }

   public static enum TopMode {
      Full,
      Top,
      Face,
      None;

      // $FF: synthetic method
      private static AutoTrap.TopMode[] $values() {
         return new AutoTrap.TopMode[]{Full, Top, Face, None};
      }
   }
}
