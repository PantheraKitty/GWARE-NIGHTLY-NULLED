package meteordevelopment.meteorclient.systems.modules.render;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.render.RenderUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_2596;
import net.minecraft.class_2620;
import net.minecraft.class_265;
import net.minecraft.class_2680;

public class BreakIndicators extends Module {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgRender;
   private final Setting<Boolean> useDoubleminePrediction;
   private final Setting<Double> rebreakCompletionAmount;
   private final Setting<Double> completionAmount;
   private final Setting<Double> removeCompletionAmount;
   private final Setting<Boolean> ignoreFriends;
   private final Setting<Boolean> render;
   private final Setting<ShapeMode> shapeMode;
   private final Setting<SettingColor> sideColor;
   private final Setting<SettingColor> lineColor;
   private final Queue<BreakIndicators.BlockBreak> _breakPackets;
   private final Map<class_2338, BreakIndicators.BlockBreak> breakStartTimes;

   public BreakIndicators() {
      super(Categories.Render, "break-indicators", "Renders the progress of a block being broken.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgRender = this.settings.createGroup("Render");
      this.useDoubleminePrediction = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("use-doublemine-predicition")).description("Does some fancy stuff to make indicators more accurate.")).defaultValue(false)).build());
      this.rebreakCompletionAmount = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("rebreak-completion-amount")).description("Determines how fast rendering increases of a suspected rebreak block. Smaller is faster.")).defaultValue(0.7D).min(0.0D).sliderMax(1.5D).build());
      this.completionAmount = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("full-completion-amount")).description("Determines how fast rendering increases. Smaller is faster.")).defaultValue(1.0D).min(0.0D).sliderMax(1.5D).build());
      this.removeCompletionAmount = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("force-remove-completion-amount")).description("Determines how long it takes to forcibly remove a block from being rendered.")).defaultValue(1.3D).min(0.0D).sliderMax(1.5D).build());
      this.ignoreFriends = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("ignore-friends")).description("Doesn't render blocks that friends are breaking.")).defaultValue(false)).build());
      this.render = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("do-render")).description("Renders the blocks in queue to be broken.")).defaultValue(true)).build());
      SettingGroup var10001 = this.sgRender;
      EnumSetting.Builder var10002 = (EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("shape-mode")).description("How the shapes are rendered.")).defaultValue(ShapeMode.Both);
      Setting var10003 = this.render;
      Objects.requireNonNull(var10003);
      this.shapeMode = var10001.add(((EnumSetting.Builder)var10002.visible(var10003::get)).build());
      this.sideColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("side-color")).description("The side color of the rendering.")).defaultValue(new SettingColor(255, 0, 80, 10)).visible(() -> {
         return (Boolean)this.render.get() && ((ShapeMode)this.shapeMode.get()).sides();
      })).build());
      this.lineColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("line-color")).description("The line color of the rendering.")).defaultValue(new SettingColor(255, 255, 255, 40)).visible(() -> {
         return (Boolean)this.render.get() && ((ShapeMode)this.shapeMode.get()).lines();
      })).build());
      this._breakPackets = new ConcurrentLinkedQueue();
      this.breakStartTimes = new HashMap();
   }

   @EventHandler
   private void onPacket(PacketEvent.Receive event) {
      class_2596 var3 = event.packet;
      if (var3 instanceof class_2620) {
         class_2620 packet = (class_2620)var3;
         class_1297 entity = this.mc.field_1687.method_8469(packet.method_11280());
         this._breakPackets.add(new BreakIndicators.BlockBreak(packet.method_11277(), RenderUtils.getCurrentGameTickCalculated(), entity));
      }

   }

   public boolean isBlockBeingBroken(class_2338 blockPos) {
      return this.breakStartTimes.containsKey(blockPos);
   }

   @EventHandler
   private void onRender(Render3DEvent event) {
      double currentGameTickCalculated = RenderUtils.getCurrentGameTickCalculated();

      while(!this._breakPackets.isEmpty()) {
         BreakIndicators.BlockBreak breakEvent = (BreakIndicators.BlockBreak)this._breakPackets.remove();
         if ((Boolean)this.useDoubleminePrediction.get() && breakEvent.entity != null && breakEvent.entity instanceof class_1657) {
            List<BreakIndicators.BlockBreak> playerBreakingBlocks = this.breakStartTimes.values().stream().filter((x) -> {
               return x.entity == breakEvent.entity && !x.blockPos.equals(breakEvent.blockPos);
            }).sorted((block1, block2) -> {
               return Double.compare(block1.startTick, block2.startTick);
            }).toList();
            if (playerBreakingBlocks.size() >= 2) {
               this.breakStartTimes.remove(((BreakIndicators.BlockBreak)playerBreakingBlocks.getLast()).blockPos);
            }
         }

         if (!this.breakStartTimes.containsKey(breakEvent.blockPos)) {
            this.breakStartTimes.put(breakEvent.blockPos, breakEvent);
         }
      }

      Iterator iterator = this.breakStartTimes.entrySet().iterator();

      while(true) {
         Entry entry;
         do {
            if (!iterator.hasNext()) {
               Iterator var11 = this.breakStartTimes.entrySet().iterator();

               while(true) {
                  Entry entry;
                  class_1657 player;
                  do {
                     if (!var11.hasNext()) {
                        if ((Boolean)this.useDoubleminePrediction.get()) {
                           Map<class_1657, List<BreakIndicators.BlockBreak>> playerBreakingBlocks = (Map)this.breakStartTimes.values().stream().sorted(Comparator.comparingDouble((blockBreak) -> {
                              return blockBreak.startTick;
                           })).filter((blockBreak) -> {
                              return blockBreak.entity instanceof class_1657;
                           }).collect(Collectors.groupingBy((blockBreak) -> {
                              return (class_1657)blockBreak.entity;
                           }, Collectors.toList()));
                           Iterator var13 = playerBreakingBlocks.entrySet().iterator();

                           while(var13.hasNext()) {
                              Entry<class_1657, List<BreakIndicators.BlockBreak>> entry = (Entry)var13.next();
                              ((List)entry.getValue()).forEach((x) -> {
                                 x.isRebreak = false;
                              });
                              if (((List)entry.getValue()).size() >= 2) {
                                 ((BreakIndicators.BlockBreak)((List)entry.getValue()).getLast()).isRebreak = true;
                              }
                           }
                        }

                        return;
                     }

                     entry = (Entry)var11.next();
                     if (!(Boolean)this.ignoreFriends.get() || ((BreakIndicators.BlockBreak)entry.getValue()).entity == null) {
                        break;
                     }

                     class_1297 var8 = ((BreakIndicators.BlockBreak)entry.getValue()).entity;
                     if (!(var8 instanceof class_1657)) {
                        break;
                     }

                     player = (class_1657)var8;
                  } while(Friends.get().isFriend(player));

                  ((BreakIndicators.BlockBreak)entry.getValue()).renderBlock(event, currentGameTickCalculated);
               }
            }

            entry = (Entry)iterator.next();
         } while(!this.mc.field_1687.method_8320((class_2338)entry.getKey()).method_26215() && !(((BreakIndicators.BlockBreak)entry.getValue()).getBreakProgress(currentGameTickCalculated) > (Double)this.removeCompletionAmount.get()) && BlockUtils.canBreak((class_2338)entry.getKey()));

         iterator.remove();
      }
   }

   private class BlockBreak {
      public class_2338 blockPos;
      public double startTick;
      public class_1297 entity;
      public boolean isRebreak = false;

      public BlockBreak(class_2338 blockPos, double startTick, class_1297 entity) {
         this.blockPos = blockPos;
         this.startTick = startTick;
         this.entity = entity;
      }

      public void renderBlock(Render3DEvent event, double currentTick) {
         class_265 shape = BreakIndicators.this.mc.field_1687.method_8320(this.blockPos).method_26218(BreakIndicators.this.mc.field_1687, this.blockPos);
         if (shape != null && !shape.method_1110()) {
            class_238 orig = shape.method_1107();
            double completion = this.isRebreak ? (Double)BreakIndicators.this.rebreakCompletionAmount.get() : (Double)BreakIndicators.this.completionAmount.get();
            double shrinkFactor = Math.clamp(1.0D - this.getBreakProgress(currentTick) * (1.0D / completion), 0.0D, 1.0D);
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
            Color color = (Color)BreakIndicators.this.sideColor.get();
            event.renderer.box(x1, y1, z1, x2, y2, z2, color, (Color)BreakIndicators.this.lineColor.get(), (ShapeMode)BreakIndicators.this.shapeMode.get(), 0);
         } else {
            event.renderer.box((class_2338)this.blockPos, (Color)BreakIndicators.this.sideColor.get(), (Color)BreakIndicators.this.lineColor.get(), (ShapeMode)BreakIndicators.this.shapeMode.get(), 0);
         }
      }

      private double getBreakProgress(double currentTick) {
         class_2680 state = BreakIndicators.this.mc.field_1687.method_8320(this.blockPos);
         FindItemResult slot = InvUtils.findFastestToolHotbar(BreakIndicators.this.mc.field_1687.method_8320(this.blockPos));
         double breakingSpeed = BlockUtils.getBlockBreakingSpeed(slot.found() ? slot.slot() : BreakIndicators.this.mc.field_1724.method_31548().field_7545, state, true);
         return BlockUtils.getBreakDelta(breakingSpeed, state) * (currentTick - this.startTick);
      }
   }
}
