package meteordevelopment.meteorclient.systems.modules.player;

import java.util.Iterator;
import java.util.Objects;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.entity.player.StartBreakingBlockEvent;
import meteordevelopment.meteorclient.events.meteor.SilentMineFinishedEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.managers.RotationManager;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.render.RenderUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1802;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_2596;
import net.minecraft.class_265;
import net.minecraft.class_2680;
import net.minecraft.class_2846;
import net.minecraft.class_2846.class_2847;

public class SilentMine extends Module {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgRender;
   private final Setting<Double> range;
   public final Setting<Boolean> antiRubberband;
   public final Setting<Boolean> preSwitchSinglebreak;
   private final Setting<Integer> singleBreakFailTicks;
   public final Setting<Boolean> rebreakSetBlockBroken;
   private final Setting<Double> speedPercentage;
   private final Setting<Boolean> render;
   private final Setting<Boolean> renderBlock;
   private final Setting<ShapeMode> shapeMode;
   private final Setting<SettingColor> sideColor;
   private final Setting<SettingColor> lineColor;
   private final Setting<Boolean> debugRenderPrimary;
   private SilentMine.SilentMineBlock rebreakBlock;
   private SilentMine.SilentMineBlock delayedDestroyBlock;
   private class_2338 lastDelayedDestroyBlockPos;
   private double currentGameTickCalculated;
   private boolean needDelayedDestroySwapBack;
   private boolean needRebreakSwapBack;
   private int totemPopTicks;
   private boolean isBeingTotemPopped;

   public SilentMine() {
      super(Categories.Player, "silent-mine", "Allows you to mine blocks without holding a pickaxe");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgRender = this.settings.createGroup("Render");
      this.range = this.sgGeneral.add(((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("range")).defaultValue(5.14D).min(0.0D).sliderMax(7.0D).build());
      this.antiRubberband = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("strict-anti-rubberband")).defaultValue(true)).build());
      this.preSwitchSinglebreak = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("pre-switch-single-break")).defaultValue(true)).build());
      this.singleBreakFailTicks = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("single-break-fail-ticks")).defaultValue(20)).min(5).sliderMax(50).build());
      this.rebreakSetBlockBroken = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("set-rebreak-block-broken")).defaultValue(true)).build());
      this.speedPercentage = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("speed-percentage")).description("Percentage of vanilla mining speed (100% = vanilla, 70% = 70% of vanilla time).")).defaultValue(100.0D).min(0.0D).sliderMax(100.0D).build());
      this.render = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("do-render")).defaultValue(true)).build());
      this.renderBlock = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("render-block")).defaultValue(true)).build());
      SettingGroup var10001 = this.sgRender;
      EnumSetting.Builder var10002 = (EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("shape-mode")).defaultValue(ShapeMode.Both);
      Setting var10003 = this.renderBlock;
      Objects.requireNonNull(var10003);
      this.shapeMode = var10001.add(((EnumSetting.Builder)var10002.visible(var10003::get)).build());
      this.sideColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("side-color")).defaultValue(new SettingColor(255, 180, 255, 15)).visible(() -> {
         return (Boolean)this.renderBlock.get() && ((ShapeMode)this.shapeMode.get()).sides();
      })).build());
      this.lineColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("line-color")).defaultValue(new SettingColor(255, 255, 255, 60)).visible(() -> {
         return (Boolean)this.renderBlock.get() && ((ShapeMode)this.shapeMode.get()).lines();
      })).build());
      this.debugRenderPrimary = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("debug-render-primary")).defaultValue(false)).build());
      this.rebreakBlock = null;
      this.delayedDestroyBlock = null;
      this.lastDelayedDestroyBlockPos = null;
      this.currentGameTickCalculated = 0.0D;
      this.needDelayedDestroySwapBack = false;
      this.needRebreakSwapBack = false;
      this.totemPopTicks = 0;
      this.isBeingTotemPopped = false;
      this.currentGameTickCalculated = RenderUtils.getCurrentGameTickCalculated();
   }

   public void onDeactivate() {
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      this.currentGameTickCalculated = RenderUtils.getCurrentGameTickCalculated();
      if (this.mc.field_1724 != null && this.mc.field_1724.method_6115() && this.mc.field_1724.method_6030().method_7909() == class_1802.field_8288) {
         this.isBeingTotemPopped = true;
         this.totemPopTicks = 0;
      } else {
         ++this.totemPopTicks;
      }

      if (this.totemPopTicks > 30) {
         this.isBeingTotemPopped = false;
      }

      if (this.hasDelayedDestroy()) {
         this.lastDelayedDestroyBlockPos = this.delayedDestroyBlock.blockPos;
      } else {
         this.lastDelayedDestroyBlockPos = null;
      }

      if (this.hasDelayedDestroy() && (this.mc.field_1687.method_8320(this.delayedDestroyBlock.blockPos).method_26215() || !BlockUtils.canBreak(this.delayedDestroyBlock.blockPos))) {
         MeteorClient.EVENT_BUS.post((Object)(new SilentMineFinishedEvent.Post(this.delayedDestroyBlock.blockPos, false)));
         this.delayedDestroyBlock = null;
      }

      if (this.rebreakBlock != null && (this.mc.field_1687.method_8320(this.rebreakBlock.blockPos).method_26215() || !BlockUtils.canBreak(this.rebreakBlock.blockPos))) {
         this.rebreakBlock.beenAir = true;
      }

      if (this.hasRebreakBlock() && this.rebreakBlock.timesSendBreakPacket > 10 && !this.canRebreakRebreakBlock()) {
         this.rebreakBlock.cancelBreaking();
         this.rebreakBlock = null;
      }

      class_2680 blockState;
      FindItemResult result;
      if (this.hasDelayedDestroy() && this.delayedDestroyBlock.ticksHeldPickaxe <= (Integer)this.singleBreakFailTicks.get()) {
         blockState = this.mc.field_1687.method_8320(this.delayedDestroyBlock.blockPos);
         if (this.delayedDestroyBlock.isReady() && !this.isBeingTotemPopped) {
            result = InvUtils.findFastestTool(blockState);
            if (result.found() && this.mc.field_1724.method_31548().field_7545 != result.slot() && MeteorClient.SWAP.beginSwap(result, false)) {
               this.needDelayedDestroySwapBack = true;
            }

            if (!result.found() || this.mc.field_1724.method_31548().field_7545 == result.slot()) {
               ++this.delayedDestroyBlock.ticksHeldPickaxe;
            }
         }
      }

      if (this.rebreakBlock != null && !this.isBeingTotemPopped) {
         blockState = this.mc.field_1687.method_8320(this.rebreakBlock.blockPos);
         if (this.rebreakBlock.isReady()) {
            if (this.inBreakRange(this.rebreakBlock.blockPos)) {
               result = InvUtils.findFastestTool(blockState);
               MeteorClient.EVENT_BUS.post((Object)(new SilentMineFinishedEvent.Pre(this.rebreakBlock.blockPos, true)));
               if (result.found() && this.mc.field_1724.method_31548().field_7545 != result.slot() && MeteorClient.SWAP.beginSwap(result, true)) {
                  this.needRebreakSwapBack = true;
               }

               this.rebreakBlock.tryBreak();
               if (this.needRebreakSwapBack) {
                  MeteorClient.SWAP.endSwap(true);
               }

               if ((Boolean)this.rebreakSetBlockBroken.get() && this.canRebreakRebreakBlock()) {
                  this.mc.field_1687.method_8501(this.rebreakBlock.blockPos, class_2246.field_10124.method_9564());
               }
            } else {
               this.rebreakBlock = null;
            }
         }
      }

      if (this.hasDelayedDestroy() && this.delayedDestroyBlock.ticksHeldPickaxe > (Integer)this.singleBreakFailTicks.get()) {
         if (this.inBreakRange(this.delayedDestroyBlock.blockPos)) {
            this.delayedDestroyBlock.startBreaking(true);
         } else {
            this.delayedDestroyBlock.cancelBreaking();
            this.delayedDestroyBlock = null;
         }
      }

      boolean delayedDestroyFinished = !this.hasDelayedDestroy() || !this.delayedDestroyBlock.isReady();
      if (this.needDelayedDestroySwapBack && delayedDestroyFinished) {
         MeteorClient.SWAP.endSwap(false);
         this.needDelayedDestroySwapBack = false;
      }

   }

   public void silentBreakBlock(class_2338 blockPos, class_2350 direction, double priority) {
      if (this.isActive() && blockPos != null && !this.alreadyBreaking(blockPos) && BlockUtils.canBreak(blockPos, this.mc.field_1687.method_8320(blockPos)) && this.inBreakRange(blockPos)) {
         boolean isAntiSwimBlock = blockPos.equals(this.mc.field_1724.method_24515().method_10084());
         if (!this.hasDelayedDestroy()) {
            boolean willResetPrimary = this.rebreakBlock != null && !this.canRebreakRebreakBlock();
            if (willResetPrimary && this.rebreakBlock.priority < priority) {
               return;
            }

            this.currentGameTickCalculated -= 0.1D;
            this.delayedDestroyBlock = new SilentMine.SilentMineBlock(blockPos, direction, priority, false);
            this.delayedDestroyBlock.startBreaking(true);
            if (willResetPrimary) {
               this.rebreakBlock.startBreaking(false);
            }
         }

         if (!this.alreadyBreaking(blockPos)) {
            if (this.rebreakBlock != null && this.delayedDestroyBlock != null && (priority >= this.rebreakBlock.priority || this.canRebreakRebreakBlock()) && this.delayedDestroyBlock.getBreakProgress() <= 0.8D) {
               this.rebreakBlock = null;
            }

            if (this.rebreakBlock == null || isAntiSwimBlock) {
               this.rebreakBlock = new SilentMine.SilentMineBlock(blockPos, direction, priority, true);
               this.rebreakBlock.startBreaking(false);
            }

         }
      }
   }

   @EventHandler
   public void onStartBreakingBlock(StartBreakingBlockEvent event) {
      event.cancel();
      this.silentBreakBlock(event.blockPos, event.direction, 100.0D);
   }

   public boolean canSwapBack() {
      return this.needDelayedDestroySwapBack && (!this.hasDelayedDestroy() || !this.delayedDestroyBlock.isReady());
   }

   public boolean hasDelayedDestroy() {
      return this.delayedDestroyBlock != null;
   }

   public boolean hasRebreakBlock() {
      return this.rebreakBlock != null && !this.rebreakBlock.beenAir;
   }

   public class_2338 getDelayedDestroyBlockPos() {
      return this.delayedDestroyBlock != null ? this.delayedDestroyBlock.blockPos : null;
   }

   public void cancelBreaking() {
      if (this.rebreakBlock != null) {
         this.rebreakBlock.cancelBreaking();
         this.rebreakBlock = null;
      }

      if (this.delayedDestroyBlock != null) {
         this.delayedDestroyBlock.cancelBreaking();
         this.delayedDestroyBlock = null;
      }

   }

   public class_2338 getLastDelayedDestroyBlockPos() {
      return this.lastDelayedDestroyBlockPos;
   }

   public double getDelayedDestroyProgress() {
      return this.delayedDestroyBlock != null ? this.delayedDestroyBlock.getBreakProgress() : 0.0D;
   }

   public class_2338 getRebreakBlockPos() {
      return this.rebreakBlock != null ? this.rebreakBlock.blockPos : null;
   }

   public double getRebreakBlockProgress() {
      return this.rebreakBlock != null ? this.rebreakBlock.getBreakProgress() : 0.0D;
   }

   public boolean canRebreakRebreakBlock() {
      return this.rebreakBlock != null && this.rebreakBlock.beenAir;
   }

   public boolean inBreakRange(class_2338 blockPos) {
      return (new class_238(blockPos)).method_49271(this.mc.field_1724.method_33571()) <= (Double)this.range.get() * (Double)this.range.get();
   }

   public boolean alreadyBreaking(class_2338 blockPos) {
      return this.rebreakBlock != null && blockPos.equals(this.rebreakBlock.blockPos) || this.delayedDestroyBlock != null && blockPos.equals(this.delayedDestroyBlock.blockPos);
   }

   @EventHandler
   private void onRender(Render3DEvent event) {
      if ((Boolean)this.render.get()) {
         double calculatedDrawGameTick = RenderUtils.getCurrentGameTickCalculated();
         if (this.rebreakBlock != null) {
            this.rebreakBlock.render(event, calculatedDrawGameTick, true);
         }

         if (this.delayedDestroyBlock != null) {
            this.delayedDestroyBlock.render(event, calculatedDrawGameTick, false);
         }
      }

   }

   @EventHandler
   private void onPacket(PacketEvent.Send event) {
      class_2596 var3 = event.packet;
      if (var3 instanceof class_2846) {
         class_2846 packet = (class_2846)var3;
         if (packet.method_12363() == class_2847.field_12973 && (Boolean)this.antiRubberband.get() && (packet.method_12362().equals(this.getRebreakBlockPos()) || packet.method_12362().equals(this.getDelayedDestroyBlockPos()))) {
            this.mc.method_1562().method_52787(new class_2846(class_2847.field_12971, packet.method_12362(), packet.method_12360()));
         }
      }

   }

   private int getSeq() {
      return this.mc.field_1687.method_41925().method_41937().method_41942();
   }

   class SilentMineBlock {
      public class_2338 blockPos;
      public class_2350 direction;
      public boolean started = false;
      public int timesSendBreakPacket = 0;
      public int ticksHeldPickaxe = 0;
      public boolean beenAir = false;
      private double destroyProgressStart = 0.0D;
      private double priority = 0.0D;
      private boolean isRebreak;

      public SilentMineBlock(class_2338 blockPos, class_2350 direction, double priority, boolean isRebreak) {
         this.blockPos = blockPos;
         this.direction = direction;
         this.priority = priority;
         this.isRebreak = isRebreak;
      }

      public boolean isReady() {
         if (!BlockUtils.canBreak(this.blockPos)) {
            return false;
         } else {
            double breakProgressSingleTick = this.getBreakProgressSingleTick();
            double speedMultiplier = (Double)SilentMine.this.speedPercentage.get() / 100.0D;
            double baseThreshold = this.isRebreak ? 0.7D : 1.0D;
            double threshold = baseThreshold - ((Boolean)SilentMine.this.preSwitchSinglebreak.get() && !this.isRebreak ? breakProgressSingleTick / speedMultiplier / 2.0D : 0.0D);
            return this.getBreakProgress() >= threshold || this.timesSendBreakPacket > 0;
         }
      }

      public void startBreaking(boolean isDelayedDestroy) {
         this.ticksHeldPickaxe = 0;
         this.timesSendBreakPacket = 0;
         this.destroyProgressStart = SilentMine.this.currentGameTickCalculated;
         if (isDelayedDestroy && SilentMine.this.canRebreakRebreakBlock()) {
            SilentMine.this.rebreakBlock = null;
         }

         SilentMine.this.mc.method_1562().method_52787(new class_2846(class_2847.field_12973, this.blockPos, this.direction, SilentMine.this.getSeq()));
         SilentMine.this.mc.method_1562().method_52787(new class_2846(class_2847.field_12968, this.blockPos, this.direction, SilentMine.this.getSeq()));
         SilentMine.this.mc.method_1562().method_52787(new class_2846(class_2847.field_12973, this.blockPos, this.direction, SilentMine.this.getSeq()));
         SilentMine.this.mc.method_1562().method_52787(new class_2846(class_2847.field_12973, this.blockPos, this.direction, SilentMine.this.getSeq()));
         SilentMine.this.mc.method_1562().method_52787(new class_2846(class_2847.field_12968, this.blockPos, this.direction, SilentMine.this.getSeq()));
         SilentMine.this.mc.method_1562().method_52787(new class_2846(class_2847.field_12973, this.blockPos, this.direction, SilentMine.this.getSeq()));
         if (!(Boolean)SilentMine.this.antiRubberband.get()) {
            SilentMine.this.mc.method_1562().method_52787(new class_2846(class_2847.field_12971, this.blockPos, this.direction));
            SilentMine.this.mc.method_1562().method_52787(new class_2846(class_2847.field_12971, this.blockPos, this.direction));
         }

         this.started = true;
      }

      public void tryBreak() {
         SilentMine.this.mc.method_1562().method_52787(new class_2846(class_2847.field_12973, this.blockPos, this.direction, SilentMine.this.getSeq()));
         if (!(Boolean)SilentMine.this.antiRubberband.get()) {
            SilentMine.this.mc.method_1562().method_52787(new class_2846(class_2847.field_12971, this.blockPos, this.direction));
         }

         ++this.timesSendBreakPacket;
      }

      public void cancelBreaking() {
         SilentMine.this.mc.method_1562().method_52787(new class_2846(class_2847.field_12971, this.blockPos, this.direction));
      }

      public double getBreakProgress() {
         return this.getBreakProgress(SilentMine.this.currentGameTickCalculated);
      }

      public double getBreakProgress(double gameTick) {
         class_2680 state = SilentMine.this.mc.field_1687.method_8320(this.blockPos);
         FindItemResult slot = InvUtils.findFastestToolHotbar(state);
         class_238 boundingBox = SilentMine.this.mc.field_1724.method_5829();
         double playerFeetY = boundingBox.field_1322;
         class_238 groundBox = new class_238(boundingBox.field_1323, playerFeetY - 0.2D, boundingBox.field_1321, boundingBox.field_1320, playerFeetY, boundingBox.field_1324);
         boolean willBeOnGround = false;
         Iterator var10 = BlockUtils.iterate(groundBox).iterator();

         while(var10.hasNext()) {
            class_2338 pos = (class_2338)var10.next();
            class_2680 blockState = SilentMine.this.mc.field_1687.method_8320(pos);
            if (blockState.method_26212(SilentMine.this.mc.field_1687, pos)) {
               double blockTopY = (double)pos.method_10264() + 1.0D;
               double distanceToBlock = playerFeetY - blockTopY;
               if (distanceToBlock >= 0.0D && distanceToBlock < Math.abs(SilentMine.this.mc.field_1724.method_18798().field_1351 * 2.0D)) {
                  willBeOnGround = true;
               }
            }
         }

         double baseBreakingSpeed = BlockUtils.getBlockBreakingSpeed(slot.found() ? slot.slot() : SilentMine.this.mc.field_1724.method_31548().field_7545, state, RotationManager.lastGround || willBeOnGround && !this.isRebreak) * 1.25D;
         double speedMultiplier = (Double)SilentMine.this.speedPercentage.get() / 100.0D;
         double adjustedBreakingSpeed = baseBreakingSpeed / speedMultiplier;
         return Math.min(BlockUtils.getBreakDelta(adjustedBreakingSpeed, state) * (gameTick - this.destroyProgressStart), 1.0D);
      }

      public double getBreakProgressSingleTick() {
         return this.getBreakProgress(this.destroyProgressStart + 1.0D);
      }

      public double getPriority() {
         return this.priority;
      }

      public void render(Render3DEvent event, double renderTick, boolean isPrimary) {
         class_265 shape = SilentMine.this.mc.field_1687.method_8320(this.blockPos).method_26218(SilentMine.this.mc.field_1687, this.blockPos);
         if (shape != null && !shape.method_1110()) {
            class_238 orig = shape.method_1107();
            double shrinkFactor = 1.0D - Math.clamp(isPrimary ? this.getBreakProgress(renderTick) * 1.4285714285714286D : this.getBreakProgress(renderTick), 0.0D, 1.0D);
            class_2338 pos = this.blockPos;
            class_238 box = orig.method_1002(orig.method_17939() * shrinkFactor, orig.method_17940() * shrinkFactor, orig.method_17941() * shrinkFactor);
            double xShrink = orig.method_17939() * shrinkFactor / 2.0D;
            double yShrink = orig.method_17940() * shrinkFactor / 2.0D;
            double zShrink = orig.method_17941() * shrinkFactor / 2.0D;
            double x1 = (double)pos.method_10263() + box.field_1323 + xShrink;
            double y1 = (double)pos.method_10264() + box.field_1322 + yShrink;
            double z1 = (double)pos.method_10260() + box.field_1321 + zShrink;
            double x2 = (double)pos.method_10263() + box.field_1320 + xShrink;
            double y2 = (double)pos.method_10264() + box.field_1325 + yShrink;
            double z2 = (double)pos.method_10260() + box.field_1324 + zShrink;
            Color color = (Color)SilentMine.this.sideColor.get();
            if ((Boolean)SilentMine.this.debugRenderPrimary.get() && isPrimary) {
               color = Color.ORANGE.a(40);
            }

            event.renderer.box(x1, y1, z1, x2, y2, z2, color, (Color)SilentMine.this.lineColor.get(), (ShapeMode)SilentMine.this.shapeMode.get(), 0);
         } else {
            event.renderer.box((class_2338)this.blockPos, (Color)SilentMine.this.sideColor.get(), (Color)SilentMine.this.lineColor.get(), (ShapeMode)SilentMine.this.shapeMode.get(), 0);
         }
      }
   }
}
