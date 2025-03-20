package meteordevelopment.meteorclient.systems.modules.combat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.managers.RotationManager;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.player.SilentMine;
import meteordevelopment.meteorclient.utils.entity.EntityUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1297;
import net.minecraft.class_1511;
import net.minecraft.class_1802;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_2680;
import net.minecraft.class_2824;

public class Surround extends Module {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgRender;
   private final Setting<Boolean> pauseEat;
   private final Setting<Boolean> protect;
   private final Setting<Boolean> protectOverrideBlockCooldown;
   private final Setting<Boolean> selfTrapEnabled;
   private final Setting<Surround.SelfTrapMode> autoSelfTrapMode;
   private final Setting<Boolean> selfTrapHead;
   private final Setting<Boolean> extendEnabled;
   private final Setting<Surround.ExtendMode> extendMode;
   private final Setting<Boolean> render;
   private final Setting<Double> fadeTime;
   private final Setting<ShapeMode> shapeMode;
   private final Setting<SettingColor> sideColor;
   private final Setting<SettingColor> lineColor;
   private List<class_2338> placePoses;
   private Map<class_2338, Long> renderLastPlacedBlock;
   private long lastTimeOfCrystalNearHead;
   private long lastTimeOfExtendCrystal;
   private long lastAttackTime;

   public Surround() {
      super(Categories.Combat, "surround", "Surrounds you in blocks to prevent massive crystal damage.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgRender = this.settings.createGroup("Render");
      this.pauseEat = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("pause-eat")).description("Pauses while eating.")).defaultValue(true)).build());
      this.protect = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("protect")).description("Attempts to break crystals around surround positions to prevent surround break.")).defaultValue(true)).build());
      this.protectOverrideBlockCooldown = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("protect-override-block-cooldown")).description("Overrides the cooldown for block placements when you break a crystal. May result in more packet kicks")).visible(() -> {
         return (Boolean)this.protect.get();
      })).defaultValue(true)).build());
      this.selfTrapEnabled = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("self-trap")).description("Enables self trap")).defaultValue(true)).build());
      this.autoSelfTrapMode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("self-trap-mode")).description("When to build double high")).defaultValue(Surround.SelfTrapMode.Smart)).visible(() -> {
         return (Boolean)this.selfTrapEnabled.get();
      })).build());
      this.selfTrapHead = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("self-trap-head")).description("Places a block above your head to prevent you from velo failing upwards")).visible(() -> {
         return (Boolean)this.selfTrapEnabled.get();
      })).defaultValue(true)).build());
      this.extendEnabled = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("extend")).description("Enables extend placing")).defaultValue(true)).build());
      this.extendMode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("extend-mode")).description("When to place extend blocks")).defaultValue(Surround.ExtendMode.Smart)).visible(() -> {
         return (Boolean)this.extendEnabled.get();
      })).build());
      this.render = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("render")).description("Renders a block overlay when you try to place obsidian.")).defaultValue(true)).build());
      this.fadeTime = this.sgRender.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("fadeTime")).description("How many seconds it takes to fade.")).defaultValue(0.2D).min(0.0D).sliderMax(1.0D).build());
      this.shapeMode = this.sgRender.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("shape-mode")).description("How the shapes are rendered.")).defaultValue(ShapeMode.Both)).build());
      this.sideColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("side-color")).description("The side color.")).defaultValue(new SettingColor(85, 0, 255, 40)).visible(() -> {
         return (Boolean)this.render.get() && this.shapeMode.get() != ShapeMode.Lines;
      })).build());
      this.lineColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("line-color")).description("The line color.")).defaultValue(new SettingColor(255, 255, 255, 60)).visible(() -> {
         return (Boolean)this.render.get() && this.shapeMode.get() != ShapeMode.Sides;
      })).build());
      this.placePoses = new ArrayList();
      this.renderLastPlacedBlock = new HashMap();
      this.lastTimeOfCrystalNearHead = 0L;
      this.lastTimeOfExtendCrystal = 0L;
      this.lastAttackTime = 0L;
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      this.placePoses.clear();
      long currentTime = System.currentTimeMillis();
      class_238 boundingBox = this.mc.field_1724.method_5829().method_1002(0.01D, 0.1D, 0.01D);
      int feetY = this.mc.field_1724.method_24515().method_10264();
      SilentMine silentMine = (SilentMine)Modules.get().get(SilentMine.class);
      int minX = (int)Math.floor(boundingBox.field_1323);
      int maxX = (int)Math.floor(boundingBox.field_1320);
      int minZ = (int)Math.floor(boundingBox.field_1321);
      int maxZ = (int)Math.floor(boundingBox.field_1324);

      for(int x = minX; x <= maxX; ++x) {
         for(int z = minZ; z <= maxZ; ++z) {
            class_2338 feetPos = new class_2338(x, feetY, z);

            for(int offsetX = -1; offsetX <= 1; ++offsetX) {
               for(int offsetZ = -1; offsetZ <= 1; ++offsetZ) {
                  if (Math.abs(offsetX) + Math.abs(offsetZ) == 1) {
                     class_2350 dir = class_2350.method_50026(offsetX, 0, offsetZ);
                     class_2338 adjacentPos = feetPos.method_10069(offsetX, 0, offsetZ);
                     class_2680 adjacentState = this.mc.field_1687.method_8320(adjacentPos);
                     if (adjacentState.method_26215() || adjacentState.method_45474()) {
                        this.placePoses.add(adjacentPos);
                     }

                     if (this.autoSelfTrapMode.get() != Surround.SelfTrapMode.None && (Boolean)this.selfTrapEnabled.get()) {
                        this.checkSelfTrap(this.placePoses, adjacentPos);
                     }

                     if (this.extendMode.get() != Surround.ExtendMode.None && (Boolean)this.extendEnabled.get()) {
                        this.checkExtend(this.placePoses, feetPos, dir);
                     }
                  }
               }
            }

            class_2338 belowFeetPos = new class_2338(x, feetY - 1, z);
            class_2680 belowFeetState = this.mc.field_1687.method_8320(belowFeetPos);
            if (!belowFeetPos.equals(silentMine.getRebreakBlockPos()) && !belowFeetPos.equals(silentMine.getDelayedDestroyBlockPos()) && (belowFeetState.method_26215() || belowFeetState.method_45474())) {
               this.placePoses.add(belowFeetPos);
            }
         }
      }

      if ((Boolean)this.selfTrapEnabled.get() && (Boolean)this.selfTrapHead.get()) {
         this.placePoses.add(this.mc.field_1724.method_24515().method_10079(class_2350.field_11036, 2));
      }

      if (!(Boolean)this.pauseEat.get() || !this.mc.field_1724.method_6115()) {
         if ((Boolean)this.protect.get()) {
            this.placePoses.forEach((blockPos) -> {
               class_238 box = new class_238((double)(blockPos.method_10263() - 1), (double)(blockPos.method_10264() - 1), (double)(blockPos.method_10260() - 1), (double)(blockPos.method_10263() + 1), (double)(blockPos.method_10264() + 1), (double)(blockPos.method_10260() + 1));
               Predicate<class_1297> entityPredicate = (entity) -> {
                  return entity instanceof class_1511;
               };
               class_1297 blocking = (class_1297)this.mc.field_1687.method_8333((class_1297)null, box, entityPredicate).stream().findFirst().orElse((Object)null);
               if (blocking != null && System.currentTimeMillis() - this.lastAttackTime >= 50L) {
                  MeteorClient.ROTATION.requestRotation(blocking.method_19538(), 900.0D);
                  if (!MeteorClient.ROTATION.lookingAt(blocking.method_5829()) && RotationManager.lastGround) {
                     MeteorClient.ROTATION.snapAt(blocking.method_19538());
                  }

                  if (MeteorClient.ROTATION.lookingAt(blocking.method_5829())) {
                     this.mc.method_1562().method_52787(class_2824.method_34206(blocking, this.mc.field_1724.method_5715()));
                     blocking.method_31472();
                     if ((Boolean)this.protectOverrideBlockCooldown.get()) {
                        MeteorClient.BLOCK.forceResetPlaceCooldown(blockPos);
                     }
                  }
               }

            });
         }

         if (MeteorClient.BLOCK.beginPlacement(this.placePoses, class_1802.field_8281)) {
            this.placePoses.forEach((blockPos) -> {
               if (!blockPos.equals(silentMine.getRebreakBlockPos()) && !blockPos.equals(silentMine.getLastDelayedDestroyBlockPos())) {
                  if (MeteorClient.BLOCK.placeBlock(class_1802.field_8281, blockPos)) {
                     this.renderLastPlacedBlock.put(blockPos, currentTime);
                  }

               }
            });
            MeteorClient.BLOCK.endPlacement();
         }
      }
   }

   private void checkSelfTrap(List<class_2338> placePoses, class_2338 adjacentPos) {
      long currentTime = System.currentTimeMillis();
      class_2338 facePlacePos = adjacentPos.method_10069(0, 1, 0);
      boolean shouldBuildDoubleHigh = this.autoSelfTrapMode.get() == Surround.SelfTrapMode.Always;
      class_238 box = class_238.method_30048(facePlacePos.method_46558().method_1031(0.0D, 0.5D, 0.0D), 0.1D, 0.1D, 0.1D);
      if (this.autoSelfTrapMode.get() == Surround.SelfTrapMode.Smart) {
         if (EntityUtils.intersectsWithEntity(box, (e) -> {
            return e instanceof class_1511;
         })) {
            this.lastTimeOfCrystalNearHead = currentTime;
         }

         if ((double)(currentTime - this.lastTimeOfCrystalNearHead) / 1000.0D < 1.0D) {
            shouldBuildDoubleHigh = true;
         }
      }

      if (shouldBuildDoubleHigh) {
         class_2680 facePlaceState = this.mc.field_1687.method_8320(facePlacePos);
         if (facePlaceState.method_26215() || facePlaceState.method_45474()) {
            placePoses.add(facePlacePos);
         }
      }

   }

   private void checkExtend(List<class_2338> placePoses, class_2338 feetPos, class_2350 dir) {
      long currentTime = System.currentTimeMillis();
      class_2338 extendPos = feetPos.method_10079(dir, 2);
      boolean shouldPlaceExtend = this.extendMode.get() == Surround.ExtendMode.Always;
      class_238 box = class_238.method_30048(extendPos.method_46558(), 0.1D, 0.1D, 0.1D);
      if (this.extendMode.get() == Surround.ExtendMode.Smart) {
         if (EntityUtils.intersectsWithEntity(box, (e) -> {
            return e instanceof class_1511;
         })) {
            this.lastTimeOfExtendCrystal = currentTime;
         }

         if ((double)(currentTime - this.lastTimeOfExtendCrystal) / 1000.0D < 1.0D) {
            shouldPlaceExtend = true;
         }
      }

      if (shouldPlaceExtend) {
         class_2680 extendState = this.mc.field_1687.method_8320(extendPos);
         if (this.isCrystalBlock(extendPos.method_10074()) && (extendState.method_26215() || extendState.method_45474())) {
            placePoses.add(extendPos);
         }
      }

   }

   private boolean isCrystalBlock(class_2338 blockPos) {
      class_2680 blockState = this.mc.field_1687.method_8320(blockPos);
      return blockState.method_27852(class_2246.field_10540) || blockState.method_27852(class_2246.field_9987);
   }

   @EventHandler
   private void onRender3D(Render3DEvent event) {
      if ((Boolean)this.render.get()) {
         this.draw(event);
      }

   }

   private void draw(Render3DEvent event) {
      long currentTime = System.currentTimeMillis();
      Iterator var4 = this.renderLastPlacedBlock.entrySet().iterator();

      while(var4.hasNext()) {
         Entry<class_2338, Long> entry = (Entry)var4.next();
         if (!((double)(currentTime - (Long)entry.getValue()) > (Double)this.fadeTime.get() * 1000.0D)) {
            double time = (double)(currentTime - (Long)entry.getValue()) / 1000.0D;
            double timeCompletion = time / (Double)this.fadeTime.get();
            Color fadedSideColor = ((SettingColor)this.sideColor.get()).copy().a((int)((double)((SettingColor)this.sideColor.get()).a * (1.0D - timeCompletion)));
            Color fadedLineColor = ((SettingColor)this.lineColor.get()).copy().a((int)((double)((SettingColor)this.lineColor.get()).a * (1.0D - timeCompletion)));
            event.renderer.box((class_2338)((class_2338)entry.getKey()), fadedSideColor, fadedLineColor, (ShapeMode)this.shapeMode.get(), 0);
         }
      }

   }

   public static enum SelfTrapMode {
      None,
      Smart,
      Always;

      // $FF: synthetic method
      private static Surround.SelfTrapMode[] $values() {
         return new Surround.SelfTrapMode[]{None, Smart, Always};
      }
   }

   public static enum ExtendMode {
      None,
      Smart,
      Always;

      // $FF: synthetic method
      private static Surround.ExtendMode[] $values() {
         return new Surround.ExtendMode[]{None, Smart, Always};
      }
   }
}
