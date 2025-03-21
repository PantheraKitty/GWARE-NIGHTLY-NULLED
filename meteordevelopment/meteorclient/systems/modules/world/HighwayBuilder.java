package meteordevelopment.meteorclient.systems.modules.world;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.BlockListSetting;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.ItemListSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.combat.KillAura;
import meteordevelopment.meteorclient.systems.modules.player.AutoEat;
import meteordevelopment.meteorclient.systems.modules.player.AutoGap;
import meteordevelopment.meteorclient.systems.modules.player.AutoTool;
import meteordevelopment.meteorclient.systems.modules.player.InstantRebreak;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.misc.HorizontalDirection;
import meteordevelopment.meteorclient.utils.misc.MBlockPos;
import meteordevelopment.meteorclient.utils.player.CustomPlayerInput;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.meteorclient.utils.world.Dir;
import meteordevelopment.meteorclient.utils.world.TickRate;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_124;
import net.minecraft.class_1268;
import net.minecraft.class_1297;
import net.minecraft.class_1542;
import net.minecraft.class_1747;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1810;
import net.minecraft.class_1893;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_2561;
import net.minecraft.class_2680;
import net.minecraft.class_2682;
import net.minecraft.class_2846;
import net.minecraft.class_310;
import net.minecraft.class_5250;
import net.minecraft.class_744;
import net.minecraft.class_2846.class_2847;
import org.jetbrains.annotations.NotNull;

public class HighwayBuilder extends Module {
   private static final class_2338 ZERO = new class_2338(0, 0, 0);
   private final SettingGroup sgGeneral;
   private final SettingGroup sgDigging;
   private final SettingGroup sgPaving;
   private final SettingGroup sgInventory;
   private final SettingGroup sgRenderDigging;
   private final SettingGroup sgRenderPaving;
   private final Setting<Integer> width;
   private final Setting<Integer> height;
   private final Setting<HighwayBuilder.Floor> floor;
   private final Setting<Boolean> railings;
   private final Setting<Boolean> mineAboveRailings;
   private final Setting<HighwayBuilder.Rotation> rotation;
   private final Setting<Boolean> disconnectOnToggle;
   private final Setting<Boolean> pauseOnLag;
   private final Setting<Boolean> dontBreakTools;
   private final Setting<Integer> savePickaxes;
   private final Setting<Integer> breakDelay;
   private final Setting<Integer> blocksPerTick;
   private final Setting<List<class_2248>> blocksToPlace;
   private final Setting<Integer> placeDelay;
   private final Setting<Integer> placementsPerTick;
   private final Setting<List<class_1792>> trashItems;
   private final Setting<Boolean> mineEnderChests;
   private final Setting<Integer> saveEchests;
   private final Setting<Boolean> rebreakEchests;
   private final Setting<Integer> rebreakTimer;
   private final Setting<Boolean> renderMine;
   private final Setting<ShapeMode> renderMineShape;
   private final Setting<SettingColor> renderMineSideColor;
   private final Setting<SettingColor> renderMineLineColor;
   private final Setting<Boolean> renderPlace;
   private final Setting<ShapeMode> renderPlaceShape;
   private final Setting<SettingColor> renderPlaceSideColor;
   private final Setting<SettingColor> renderPlaceLineColor;
   private HorizontalDirection dir;
   private HorizontalDirection leftDir;
   private HorizontalDirection rightDir;
   private class_744 prevInput;
   private CustomPlayerInput input;
   private HighwayBuilder.State state;
   private HighwayBuilder.State lastState;
   private HighwayBuilder.IBlockPosProvider blockPosProvider;
   public class_243 start;
   public int blocksBroken;
   public int blocksPlaced;
   private final MBlockPos lastBreakingPos;
   private boolean displayInfo;
   private int placeTimer;
   private int breakTimer;
   private int count;
   private final MBlockPos posRender2;
   private final MBlockPos posRender3;

   public HighwayBuilder() {
      super(Categories.World, "highway-builder", "Automatically builds highways.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgDigging = this.settings.createGroup("Digging");
      this.sgPaving = this.settings.createGroup("Paving");
      this.sgInventory = this.settings.createGroup("Inventory");
      this.sgRenderDigging = this.settings.createGroup("Render Digging");
      this.sgRenderPaving = this.settings.createGroup("Render Paving");
      this.width = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("width")).description("Width of the highway.")).defaultValue(4)).range(1, 5).sliderRange(1, 5).build());
      this.height = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("height")).description("Height of the highway.")).defaultValue(3)).range(2, 5).sliderRange(2, 5).build());
      this.floor = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("floor")).description("What floor placement mode to use.")).defaultValue(HighwayBuilder.Floor.Replace)).build());
      this.railings = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("railings")).description("Builds railings next to the highway.")).defaultValue(true)).build());
      SettingGroup var10001 = this.sgGeneral;
      BoolSetting.Builder var10002 = (BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("mine-above-railings")).description("Mines blocks above railings.");
      Setting var10003 = this.railings;
      Objects.requireNonNull(var10003);
      this.mineAboveRailings = var10001.add(((BoolSetting.Builder)((BoolSetting.Builder)var10002.visible(var10003::get)).defaultValue(true)).build());
      this.rotation = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("rotation")).description("Mode of rotation.")).defaultValue(HighwayBuilder.Rotation.Both)).build());
      this.disconnectOnToggle = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("disconnect-on-toggle")).description("Automatically disconnects when the module is turned off, for example for not having enough blocks.")).defaultValue(false)).build());
      this.pauseOnLag = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("pause-on-lag")).description("Pauses the current process while the server stops responding.")).defaultValue(true)).build());
      this.dontBreakTools = this.sgDigging.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("dont-break-tools")).description("Don't break tools.")).defaultValue(false)).build());
      this.savePickaxes = this.sgDigging.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("save-pickaxes")).description("How many pickaxes to ensure are saved.")).defaultValue(0)).range(0, 36).sliderRange(0, 36).visible(() -> {
         return !(Boolean)this.dontBreakTools.get();
      })).build());
      this.breakDelay = this.sgDigging.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("break-delay")).description("The delay between breaking blocks.")).defaultValue(0)).min(0).build());
      this.blocksPerTick = this.sgDigging.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("blocks-per-tick")).description("The maximum amount of blocks that can be mined in a tick. Only applies to blocks instantly breakable.")).defaultValue(1)).range(1, 100).sliderRange(1, 25).build());
      this.blocksToPlace = this.sgPaving.add(((BlockListSetting.Builder)((BlockListSetting.Builder)(new BlockListSetting.Builder()).name("blocks-to-place")).description("Blocks it is allowed to place.")).defaultValue(class_2246.field_10540).filter((block) -> {
         return class_2248.method_9614(block.method_9564().method_26220(class_2682.field_12294, ZERO));
      }).build());
      this.placeDelay = this.sgPaving.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("place-delay")).description("The delay between placing blocks.")).defaultValue(0)).min(0).build());
      this.placementsPerTick = this.sgPaving.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("placements-per-tick")).description("The maximum amount of blocks that can be placed in a tick.")).defaultValue(1)).min(1).build());
      this.trashItems = this.sgInventory.add(((ItemListSetting.Builder)((ItemListSetting.Builder)(new ItemListSetting.Builder()).name("trash-items")).description("Items that are considered trash and can be thrown out.")).defaultValue(class_1802.field_8328, class_1802.field_8155, class_1802.field_8397, class_1802.field_8845, class_1802.field_8601, class_1802.field_8801, class_1802.field_23843, class_1802.field_22000, class_1802.field_8070, class_1802.field_8067, class_1802.field_21999, class_1802.field_8511).build());
      this.mineEnderChests = this.sgInventory.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("mine-ender-chests")).description("Mines ender chests for obsidian.")).defaultValue(true)).build());
      var10001 = this.sgInventory;
      IntSetting.Builder var1 = ((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("save-ender-chests")).description("How many ender chests to ensure are saved.")).defaultValue(1)).range(0, 64).sliderRange(0, 64);
      var10003 = this.mineEnderChests;
      Objects.requireNonNull(var10003);
      this.saveEchests = var10001.add(((IntSetting.Builder)var1.visible(var10003::get)).build());
      var10001 = this.sgInventory;
      var10002 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("instantly-rebreak-echests")).description("Whether or not to use the instant rebreak exploit to break echests.")).defaultValue(false);
      var10003 = this.mineEnderChests;
      Objects.requireNonNull(var10003);
      this.rebreakEchests = var10001.add(((BoolSetting.Builder)var10002.visible(var10003::get)).build());
      this.rebreakTimer = this.sgInventory.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("rebreak-delay")).description("Delay between rebreak attempts.")).defaultValue(0)).sliderMax(20).visible(() -> {
         return (Boolean)this.mineEnderChests.get() && (Boolean)this.rebreakEchests.get();
      })).build());
      this.renderMine = this.sgRenderDigging.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("render-blocks-to-mine")).description("Render blocks to be mined.")).defaultValue(true)).build());
      this.renderMineShape = this.sgRenderDigging.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("blocks-to-mine-shape-mode")).description("How the blocks to be mined are rendered.")).defaultValue(ShapeMode.Both)).build());
      this.renderMineSideColor = this.sgRenderDigging.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("blocks-to-mine-side-color")).description("Color of blocks to be mined.")).defaultValue(new SettingColor(225, 25, 25, 25)).build());
      this.renderMineLineColor = this.sgRenderDigging.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("blocks-to-mine-line-color")).description("Color of blocks to be mined.")).defaultValue(new SettingColor(225, 25, 25)).build());
      this.renderPlace = this.sgRenderPaving.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("render-blocks-to-place")).description("Render blocks to be placed.")).defaultValue(true)).build());
      this.renderPlaceShape = this.sgRenderPaving.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("blocks-to-place-shape-mode")).description("How the blocks to be placed are rendered.")).defaultValue(ShapeMode.Both)).build());
      this.renderPlaceSideColor = this.sgRenderPaving.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("blocks-to-place-side-color")).description("Color of blocks to be placed.")).defaultValue(new SettingColor(25, 25, 225, 25)).build());
      this.renderPlaceLineColor = this.sgRenderPaving.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("blocks-to-place-line-color")).description("Color of blocks to be placed.")).defaultValue(new SettingColor(25, 25, 225)).build());
      this.lastBreakingPos = new MBlockPos();
      this.posRender2 = new MBlockPos();
      this.posRender3 = new MBlockPos();
   }

   public void onActivate() {
      this.dir = HorizontalDirection.get(this.mc.field_1724.method_36454());
      this.leftDir = this.dir.rotateLeftSkipOne();
      this.rightDir = this.leftDir.opposite();
      this.prevInput = this.mc.field_1724.field_3913;
      this.mc.field_1724.field_3913 = this.input = new CustomPlayerInput();
      this.state = HighwayBuilder.State.Forward;
      this.setState(HighwayBuilder.State.Center);
      this.blockPosProvider = (HighwayBuilder.IBlockPosProvider)(this.dir.diagonal ? new HighwayBuilder.DiagonalBlockPosProvider() : new HighwayBuilder.StraightBlockPosProvider());
      this.start = this.mc.field_1724.method_19538();
      this.blocksBroken = this.blocksPlaced = 0;
      this.lastBreakingPos.set(0, 0, 0);
      this.displayInfo = true;
      this.placeTimer = 0;
      this.breakTimer = 0;
      this.count = 0;
      if ((Integer)this.blocksPerTick.get() > 1 && ((HighwayBuilder.Rotation)this.rotation.get()).mine) {
         this.warning("With rotations enabled, you can break at most 1 block per tick.", new Object[0]);
      }

      if ((Integer)this.placementsPerTick.get() > 1 && ((HighwayBuilder.Rotation)this.rotation.get()).place) {
         this.warning("With rotations enabled, you can place at most 1 block per tick.", new Object[0]);
      }

      if (((InstantRebreak)Modules.get().get(InstantRebreak.class)).isActive()) {
         this.warning("It's recommended to disable the Instant Rebreak module and instead use the 'instantly-rebreak-echests' setting to avoid errors.", new Object[0]);
      }

   }

   public void onDeactivate() {
      this.mc.field_1724.field_3913 = this.prevInput;
      this.mc.field_1724.method_36456(this.dir.yaw);
      if (this.displayInfo) {
         this.info("Distance: (highlight)%.0f", new Object[]{PlayerUtils.distanceTo(this.start)});
         this.info("Blocks broken: (highlight)%d", new Object[]{this.blocksBroken});
         this.info("Blocks placed: (highlight)%d", new Object[]{this.blocksPlaced});
      }

   }

   public void error(String message, Object... args) {
      super.error(message, args);
      this.toggle();
      if ((Boolean)this.disconnectOnToggle.get()) {
         this.disconnect(message, args);
      }

   }

   private void errorEarly(String message, Object... args) {
      super.error(message, args);
      this.displayInfo = false;
      this.toggle();
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      if ((Integer)this.width.get() < 3 && this.dir.diagonal) {
         this.errorEarly("Diagonal highways with width less than 3 are not supported.");
      } else if (!((AutoEat)Modules.get().get(AutoEat.class)).eating) {
         if (!((AutoGap)Modules.get().get(AutoGap.class)).isEating()) {
            if (!((KillAura)Modules.get().get(KillAura.class)).attacking) {
               if (!(Boolean)this.pauseOnLag.get() || !(TickRate.INSTANCE.getTimeSinceLastTick() >= 2.0F)) {
                  this.count = 0;
                  this.state.tick(this);
                  if (this.breakTimer > 0) {
                     --this.breakTimer;
                  }

                  if (this.placeTimer > 0) {
                     --this.placeTimer;
                  }

               }
            }
         }
      }
   }

   @EventHandler
   private void onRender3D(Render3DEvent event) {
      if ((Boolean)this.renderMine.get()) {
         this.render(event, this.blockPosProvider.getFront(), (mBlockPos) -> {
            return this.canMine(mBlockPos, true);
         }, true);
         if (this.floor.get() == HighwayBuilder.Floor.Replace) {
            this.render(event, this.blockPosProvider.getFloor(), (mBlockPos) -> {
               return this.canMine(mBlockPos, false);
            }, true);
         }

         if ((Boolean)this.railings.get()) {
            this.render(event, this.blockPosProvider.getRailings(true), (mBlockPos) -> {
               return this.canMine(mBlockPos, false);
            }, true);
         }

         if (this.state == HighwayBuilder.State.MineEChestBlockade) {
            this.render(event, this.blockPosProvider.getEChestBlockade(true), (mBlockPos) -> {
               return this.canMine(mBlockPos, true);
            }, true);
         }
      }

      if ((Boolean)this.renderPlace.get()) {
         this.render(event, this.blockPosProvider.getLiquids(), (mBlockPos) -> {
            return this.canPlace(mBlockPos, true);
         }, false);
         if ((Boolean)this.railings.get()) {
            this.render(event, this.blockPosProvider.getRailings(false), (mBlockPos) -> {
               return this.canPlace(mBlockPos, false);
            }, false);
         }

         this.render(event, this.blockPosProvider.getFloor(), (mBlockPos) -> {
            return this.canPlace(mBlockPos, false);
         }, false);
         if (this.state == HighwayBuilder.State.PlaceEChestBlockade) {
            this.render(event, this.blockPosProvider.getEChestBlockade(false), (mBlockPos) -> {
               return this.canPlace(mBlockPos, false);
            }, false);
         }
      }

   }

   private void render(Render3DEvent event, HighwayBuilder.MBPIterator it, Predicate<MBlockPos> predicate, boolean mine) {
      Color sideColor = mine ? (Color)this.renderMineSideColor.get() : (Color)this.renderPlaceSideColor.get();
      Color lineColor = mine ? (Color)this.renderMineLineColor.get() : (Color)this.renderPlaceLineColor.get();
      ShapeMode shapeMode = mine ? (ShapeMode)this.renderMineShape.get() : (ShapeMode)this.renderPlaceShape.get();
      Iterator var8 = it.iterator();

      while(true) {
         do {
            if (!var8.hasNext()) {
               return;
            }

            MBlockPos pos = (MBlockPos)var8.next();
            this.posRender2.set(pos);
         } while(!predicate.test(this.posRender2));

         int excludeDir = 0;
         class_2350[] var11 = class_2350.values();
         int var12 = var11.length;

         for(int var13 = 0; var13 < var12; ++var13) {
            class_2350 side = var11[var13];
            this.posRender3.set(this.posRender2).add(side.method_10148(), side.method_10164(), side.method_10165());
            it.save();
            Iterator var15 = it.iterator();

            while(var15.hasNext()) {
               MBlockPos p = (MBlockPos)var15.next();
               if (p.equals(this.posRender3) && predicate.test(p)) {
                  excludeDir |= Dir.get(side);
               }
            }

            it.restore();
         }

         event.renderer.box(this.posRender2.getBlockPos(), sideColor, lineColor, shapeMode, excludeDir);
      }
   }

   private void setState(HighwayBuilder.State state) {
      this.lastState = this.state;
      this.state = state;
      this.input.stop();
      state.start(this);
   }

   private int getWidthLeft() {
      byte var10000;
      switch((Integer)this.width.get()) {
      case 2:
      case 3:
         var10000 = 1;
         break;
      case 4:
      case 5:
         var10000 = 2;
         break;
      default:
         var10000 = 0;
      }

      return var10000;
   }

   private int getWidthRight() {
      byte var10000;
      switch((Integer)this.width.get()) {
      case 3:
      case 4:
         var10000 = 1;
         break;
      case 5:
         var10000 = 2;
         break;
      default:
         var10000 = 0;
      }

      return var10000;
   }

   private boolean canMine(MBlockPos pos, boolean ignoreBlocksToPlace) {
      class_2680 state = pos.getState();
      return BlockUtils.canBreak(pos.getBlockPos(), state) && (ignoreBlocksToPlace || !((List)this.blocksToPlace.get()).contains(state.method_26204()));
   }

   private boolean canPlace(MBlockPos pos, boolean liquids) {
      return liquids ? !pos.getState().method_26227().method_15769() : BlockUtils.canPlace(pos.getBlockPos());
   }

   private void disconnect(String message, Object... args) {
      String var10000 = String.format("%s[%s%s%s] %s", class_124.field_1080, class_124.field_1078, this.title, class_124.field_1080, class_124.field_1061);
      class_5250 text = class_2561.method_43470(var10000 + String.format(message, args)).method_27693("\n");
      text.method_10852(this.getStatsText());
      this.mc.method_1562().method_48296().method_10747(text);
   }

   public class_5250 getStatsText() {
      class_5250 text = class_2561.method_43470(String.format("%sDistance: %s%.0f\n", class_124.field_1080, class_124.field_1068, this.mc.field_1724 == null ? 0.0D : PlayerUtils.distanceTo(this.start)));
      text.method_27693(String.format("%sBlocks broken: %s%d\n", class_124.field_1080, class_124.field_1068, this.blocksBroken));
      text.method_27693(String.format("%sBlocks placed: %s%d", class_124.field_1080, class_124.field_1068, this.blocksPlaced));
      return text;
   }

   // $FF: synthetic method
   static class_310 access$000(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$100(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$200(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$300(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$400(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$500(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$600(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$700(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$800(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$900(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$1000(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$1100(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$1200(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$1300(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$1400(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$1500(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$1600(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$1700(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$1800(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$1900(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$2000(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$2100(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$2200(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$2300(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$2400(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$2500(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$2600(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$2700(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$2800(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$2900(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$3000(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$3100(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$3200(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$5000(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$5200(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$5300(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$5400(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$5600(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$5700(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$5800(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$6000(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$6100(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$6200(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$6400(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$6500(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$6600(HighwayBuilder x0) {
      return x0.mc;
   }

   // $FF: synthetic method
   static class_310 access$6700(HighwayBuilder x0) {
      return x0.mc;
   }

   public static enum Floor {
      Replace,
      PlaceMissing;

      // $FF: synthetic method
      private static HighwayBuilder.Floor[] $values() {
         return new HighwayBuilder.Floor[]{Replace, PlaceMissing};
      }
   }

   public static enum Rotation {
      None(false, false),
      Mine(true, false),
      Place(false, true),
      Both(true, true);

      public final boolean mine;
      public final boolean place;

      private Rotation(boolean mine, boolean place) {
         this.mine = mine;
         this.place = place;
      }

      // $FF: synthetic method
      private static HighwayBuilder.Rotation[] $values() {
         return new HighwayBuilder.Rotation[]{None, Mine, Place, Both};
      }
   }

   private static enum State {
      Center {
         protected void tick(HighwayBuilder b) {
            double x = Math.abs(HighwayBuilder.access$000(b).field_1724.method_23317() - (double)((int)HighwayBuilder.access$100(b).field_1724.method_23317())) - 0.5D;
            double z = Math.abs(HighwayBuilder.access$200(b).field_1724.method_23321() - (double)((int)HighwayBuilder.access$300(b).field_1724.method_23321())) - 0.5D;
            boolean isX = Math.abs(x) <= 0.1D;
            boolean isZ = Math.abs(z) <= 0.1D;
            if (isX && isZ) {
               b.input.stop();
               HighwayBuilder.access$400(b).field_1724.method_18800(0.0D, 0.0D, 0.0D);
               HighwayBuilder.access$1000(b).field_1724.method_5814((double)((int)HighwayBuilder.access$500(b).field_1724.method_23317()) + (HighwayBuilder.access$600(b).field_1724.method_23317() < 0.0D ? -0.5D : 0.5D), HighwayBuilder.access$700(b).field_1724.method_23318(), (double)((int)HighwayBuilder.access$800(b).field_1724.method_23321()) + (HighwayBuilder.access$900(b).field_1724.method_23321() < 0.0D ? -0.5D : 0.5D));
               b.setState(b.lastState);
            } else {
               HighwayBuilder.access$1100(b).field_1724.method_36456(0.0F);
               boolean right;
               if (!isZ) {
                  b.input.field_3910 = z < 0.0D;
                  b.input.field_3909 = z > 0.0D;
                  if (HighwayBuilder.access$1200(b).field_1724.method_23321() < 0.0D) {
                     right = b.input.field_3910;
                     b.input.field_3910 = b.input.field_3909;
                     b.input.field_3909 = right;
                  }
               }

               if (!isX) {
                  b.input.field_3906 = x > 0.0D;
                  b.input.field_3908 = x < 0.0D;
                  if (HighwayBuilder.access$1300(b).field_1724.method_23317() < 0.0D) {
                     right = b.input.field_3906;
                     b.input.field_3906 = b.input.field_3908;
                     b.input.field_3908 = right;
                  }
               }

               b.input.field_3903 = true;
            }

         }
      },
      Forward {
         protected void start(HighwayBuilder b) {
            HighwayBuilder.access$1400(b).field_1724.method_36456(b.dir.yaw);
            this.checkTasks(b);
         }

         protected void tick(HighwayBuilder b) {
            this.checkTasks(b);
            if (b.state == Forward) {
               b.input.field_3910 = true;
            }

         }

         private void checkTasks(HighwayBuilder b) {
            if (this.needsToPlace(b, b.blockPosProvider.getLiquids(), true)) {
               b.setState(FillLiquids);
            } else if (this.needsToMine(b, b.blockPosProvider.getFront(), true)) {
               b.setState(MineFront);
            } else if (b.floor.get() == HighwayBuilder.Floor.Replace && this.needsToMine(b, b.blockPosProvider.getFloor(), false)) {
               b.setState(MineFloor);
            } else if ((Boolean)b.railings.get() && this.needsToMine(b, b.blockPosProvider.getRailings(true), false)) {
               b.setState(MineRailings);
            } else if ((Boolean)b.railings.get() && this.needsToPlace(b, b.blockPosProvider.getRailings(false), false)) {
               b.setState(PlaceRailings);
            } else if (this.needsToPlace(b, b.blockPosProvider.getFloor(), false)) {
               b.setState(PlaceFloor);
            }

         }

         private boolean needsToMine(HighwayBuilder b, HighwayBuilder.MBPIterator it, boolean ignoreBlocksToPlace) {
            Iterator var4 = it.iterator();

            MBlockPos pos;
            do {
               if (!var4.hasNext()) {
                  return false;
               }

               pos = (MBlockPos)var4.next();
            } while(!b.canMine(pos, ignoreBlocksToPlace));

            return true;
         }

         private boolean needsToPlace(HighwayBuilder b, HighwayBuilder.MBPIterator it, boolean liquids) {
            Iterator var4 = it.iterator();

            MBlockPos pos;
            do {
               if (!var4.hasNext()) {
                  return false;
               }

               pos = (MBlockPos)var4.next();
            } while(!b.canPlace(pos, liquids));

            return true;
         }
      },
      FillLiquids {
         protected void tick(HighwayBuilder b) {
            int slot = this.findBlocksToPlacePrioritizeTrash(b);
            if (slot != -1) {
               this.place(b, new HighwayBuilder.MBPIteratorFilter(b.blockPosProvider.getLiquids(), (pos) -> {
                  return !pos.getState().method_26227().method_15769();
               }), slot, Forward);
            }
         }

         // $FF: synthetic method
         private static boolean lambda$tick$0(MBlockPos pos) {
            return !pos.getState().method_26227().method_15769();
         }
      },
      MineFront {
         protected void tick(HighwayBuilder b) {
            this.mine(b, b.blockPosProvider.getFront(), true, MineFloor, this);
         }
      },
      MineFloor {
         protected void start(HighwayBuilder b) {
            this.mine(b, b.blockPosProvider.getFloor(), false, MineRailings, this);
         }

         protected void tick(HighwayBuilder b) {
            this.mine(b, b.blockPosProvider.getFloor(), false, MineRailings, this);
         }
      },
      MineRailings {
         protected void start(HighwayBuilder b) {
            this.mine(b, b.blockPosProvider.getRailings(true), false, PlaceRailings, this);
         }

         protected void tick(HighwayBuilder b) {
            this.mine(b, b.blockPosProvider.getRailings(true), false, PlaceRailings, this);
         }
      },
      PlaceRailings {
         protected void tick(HighwayBuilder b) {
            int slot = this.findBlocksToPlace(b);
            if (slot != -1) {
               this.place(b, b.blockPosProvider.getRailings(false), slot, Forward);
            }
         }
      },
      PlaceFloor {
         protected void start(HighwayBuilder b) {
            int slot = this.findBlocksToPlace(b);
            if (slot != -1) {
               this.place(b, b.blockPosProvider.getFloor(), slot, Forward);
            }
         }

         protected void tick(HighwayBuilder b) {
            int slot = this.findBlocksToPlace(b);
            if (slot != -1) {
               this.place(b, b.blockPosProvider.getFloor(), slot, Forward);
            }
         }
      },
      ThrowOutTrash {
         private int skipSlot;
         private boolean timerEnabled;
         private boolean firstTick;
         private int timer;

         protected void start(HighwayBuilder b) {
            int biggestCount = 0;

            for(int i = 0; i < HighwayBuilder.access$1500(b).field_1724.method_31548().field_7547.size(); ++i) {
               class_1799 itemStack = HighwayBuilder.access$1600(b).field_1724.method_31548().method_5438(i);
               if (itemStack.method_7909() instanceof class_1747 && ((List)b.trashItems.get()).contains(itemStack.method_7909()) && itemStack.method_7947() > biggestCount) {
                  biggestCount = itemStack.method_7947();
                  this.skipSlot = i;
                  if (biggestCount >= 64) {
                     break;
                  }
               }
            }

            if (biggestCount == 0) {
               this.skipSlot = -1;
            }

            this.timerEnabled = false;
            this.firstTick = true;
         }

         protected void tick(HighwayBuilder b) {
            if (this.timerEnabled) {
               if (this.timer > 0) {
                  --this.timer;
               } else {
                  b.setState(b.lastState);
               }

            } else {
               HighwayBuilder.access$1700(b).field_1724.method_36456(b.dir.opposite().yaw);
               HighwayBuilder.access$1800(b).field_1724.method_36457(-25.0F);
               if (this.firstTick) {
                  this.firstTick = false;
               } else if (!HighwayBuilder.access$1900(b).field_1724.field_7512.method_34255().method_7960()) {
                  InvUtils.dropHand();
               } else {
                  for(int i = 0; i < HighwayBuilder.access$2000(b).field_1724.method_31548().field_7547.size(); ++i) {
                     if (i != this.skipSlot) {
                        class_1799 itemStack = HighwayBuilder.access$2100(b).field_1724.method_31548().method_5438(i);
                        if (((List)b.trashItems.get()).contains(itemStack.method_7909())) {
                           InvUtils.drop().slot(i);
                           return;
                        }
                     }
                  }

                  this.timerEnabled = true;
                  this.timer = 10;
               }
            }
         }
      },
      PlaceEChestBlockade {
         protected void tick(HighwayBuilder b) {
            int slot = this.findBlocksToPlacePrioritizeTrash(b);
            if (slot != -1) {
               this.place(b, b.blockPosProvider.getEChestBlockade(false), slot, MineEnderChests);
            }
         }
      },
      MineEChestBlockade {
         protected void tick(HighwayBuilder b) {
            this.mine(b, b.blockPosProvider.getEChestBlockade(true), true, Center, Forward);
         }
      },
      MineEnderChests {
         private static final MBlockPos pos = new MBlockPos();
         private int minimumObsidian;
         private boolean first;
         private boolean primed;
         private boolean stopTimerEnabled;
         private int stopTimer;
         private int moveTimer;
         private int rebreakTimer;

         protected void start(HighwayBuilder b) {
            if (b.lastState != Center && b.lastState != ThrowOutTrash && b.lastState != PlaceEChestBlockade) {
               b.setState(Center);
            } else if (b.lastState == Center) {
               b.setState(ThrowOutTrash);
            } else if (b.lastState == ThrowOutTrash) {
               b.setState(PlaceEChestBlockade);
            } else {
               int emptySlots = 0;

               int minimumSlots;
               for(minimumSlots = 0; minimumSlots < HighwayBuilder.access$2200(b).field_1724.method_31548().field_7547.size(); ++minimumSlots) {
                  if (HighwayBuilder.access$2300(b).field_1724.method_31548().method_5438(minimumSlots).method_7960()) {
                     ++emptySlots;
                  }
               }

               if (emptySlots == 0) {
                  b.error("No empty slots.");
               } else {
                  minimumSlots = Math.max(emptySlots - 4, 1);
                  this.minimumObsidian = minimumSlots * 64;
                  this.first = true;
                  this.moveTimer = 0;
                  this.stopTimerEnabled = false;
                  this.primed = false;
               }
            }
         }

         protected void tick(HighwayBuilder b) {
            if (this.stopTimerEnabled) {
               if (this.stopTimer > 0) {
                  --this.stopTimer;
               } else {
                  b.setState(MineEChestBlockade);
               }

            } else {
               HorizontalDirection dir = b.dir.diagonal ? b.dir.rotateLeft().rotateLeftSkipOne() : b.dir.opposite();
               pos.set((class_1297)HighwayBuilder.access$2400(b).field_1724).offset(dir);
               if (this.moveTimer > 0) {
                  HighwayBuilder.access$2500(b).field_1724.method_36456(dir.yaw);
                  b.input.field_3910 = this.moveTimer > 2;
                  --this.moveTimer;
               } else {
                  int obsidianCount = 0;
                  double var10004 = (double)pos.x;
                  double var10005 = (double)pos.y;
                  double var10006 = (double)pos.z;
                  Iterator var4 = HighwayBuilder.access$2700(b).field_1687.method_8335(HighwayBuilder.access$2600(b).field_1724, new class_238(var10004, var10005, var10006, (double)(pos.x + 1), (double)(pos.y + 2), (double)(pos.z + 1))).iterator();

                  while(var4.hasNext()) {
                     class_1297 entity = (class_1297)var4.next();
                     if (entity instanceof class_1542) {
                        class_1542 itemEntity = (class_1542)entity;
                        if (itemEntity.method_6983().method_7909() == class_1802.field_8281) {
                           obsidianCount += itemEntity.method_6983().method_7947();
                        }
                     }
                  }

                  for(int i = 0; i < HighwayBuilder.access$2800(b).field_1724.method_31548().field_7547.size(); ++i) {
                     class_1799 itemStack = HighwayBuilder.access$2900(b).field_1724.method_31548().method_5438(i);
                     if (itemStack.method_7909() == class_1802.field_8281) {
                        obsidianCount += itemStack.method_7947();
                     }
                  }

                  if (obsidianCount >= this.minimumObsidian) {
                     this.stopTimerEnabled = true;
                     this.stopTimer = 8;
                  } else {
                     class_2338 bp = pos.getBlockPos();
                     class_2680 blockState = HighwayBuilder.access$3000(b).field_1687.method_8320(bp);
                     int slot;
                     if (blockState.method_26204() == class_2246.field_10443) {
                        if (this.first) {
                           this.moveTimer = 8;
                           this.first = false;
                           return;
                        }

                        slot = this.findAndMoveBestToolToHotbar(b, blockState, true);
                        if (slot == -1) {
                           b.error("Cannot find pickaxe without silk touch to mine ender chests.");
                           return;
                        }

                        InvUtils.swap(slot, false);
                        if ((Boolean)b.rebreakEchests.get() && this.primed) {
                           if (this.rebreakTimer > 0) {
                              --this.rebreakTimer;
                              return;
                           }

                           class_2846 p = new class_2846(class_2847.field_12973, bp, BlockUtils.getDirection(bp));
                           this.rebreakTimer = (Integer)b.rebreakTimer.get();
                           if (((HighwayBuilder.Rotation)b.rotation.get()).mine) {
                              Rotations.rotate(Rotations.getYaw(bp), Rotations.getPitch(bp), () -> {
                                 HighwayBuilder.access$3200(b).method_1562().method_52787(p);
                              });
                           } else {
                              HighwayBuilder.access$3100(b).method_1562().method_52787(p);
                           }
                        } else if (((HighwayBuilder.Rotation)b.rotation.get()).mine) {
                           Rotations.rotate(Rotations.getYaw(bp), Rotations.getPitch(bp), () -> {
                              BlockUtils.breakBlock(bp, true);
                           });
                        } else {
                           BlockUtils.breakBlock(bp, true);
                        }
                     } else {
                        slot = this.findAndMoveToHotbar(b, (itemStack) -> {
                           return itemStack.method_7909() == class_1802.field_8466;
                        }, false);
                        if (slot == -1 || this.countItem(b, (stack) -> {
                           return stack.method_7909().equals(class_1802.field_8466);
                        }) <= (Integer)b.saveEchests.get()) {
                           this.stopTimerEnabled = true;
                           this.stopTimer = 4;
                           return;
                        }

                        if (!this.first) {
                           this.primed = true;
                        }

                        BlockUtils.place(bp, class_1268.field_5808, slot, ((HighwayBuilder.Rotation)b.rotation.get()).place, 0, true, true, false);
                     }

                  }
               }
            }
         }

         // $FF: synthetic method
         private static boolean lambda$tick$3(class_1799 stack) {
            return stack.method_7909().equals(class_1802.field_8466);
         }

         // $FF: synthetic method
         private static boolean lambda$tick$2(class_1799 itemStack) {
            return itemStack.method_7909() == class_1802.field_8466;
         }

         // $FF: synthetic method
         private static void lambda$tick$1(class_2338 bp) {
            BlockUtils.breakBlock(bp, true);
         }

         // $FF: synthetic method
         private static void lambda$tick$0(HighwayBuilder b, class_2846 p) {
            HighwayBuilder.access$3200(b).method_1562().method_52787(p);
         }
      };

      protected void start(HighwayBuilder b) {
      }

      protected abstract void tick(HighwayBuilder var1);

      protected void mine(HighwayBuilder b, HighwayBuilder.MBPIterator it, boolean ignoreBlocksToPlace, HighwayBuilder.State nextState, HighwayBuilder.State lastState) {
         boolean breaking = false;
         boolean finishedBreaking = false;
         Iterator var8 = it.iterator();

         while(true) {
            class_2338 mcPos;
            while(true) {
               if (var8.hasNext()) {
                  MBlockPos pos = (MBlockPos)var8.next();
                  if (b.count >= (Integer)b.blocksPerTick.get()) {
                     return;
                  }

                  if (b.breakTimer > 0) {
                     return;
                  }

                  class_2680 state = pos.getState();
                  if (state.method_26215() || !ignoreBlocksToPlace && ((List)b.blocksToPlace.get()).contains(state.method_26204())) {
                     continue;
                  }

                  int slot = this.findAndMoveBestToolToHotbar(b, state, false);
                  if (slot == -1) {
                     return;
                  }

                  InvUtils.swap(slot, false);
                  mcPos = pos.getBlockPos();
                  if (!BlockUtils.canBreak(mcPos)) {
                     break;
                  }

                  if (((HighwayBuilder.Rotation)b.rotation.get()).mine) {
                     Rotations.rotate(Rotations.getYaw(mcPos), Rotations.getPitch(mcPos), () -> {
                        BlockUtils.breakBlock(mcPos, true);
                     });
                  } else {
                     BlockUtils.breakBlock(mcPos, true);
                  }

                  breaking = true;
                  b.breakTimer = (Integer)b.breakDelay.get();
                  if (!b.lastBreakingPos.equals(pos)) {
                     b.lastBreakingPos.set(pos);
                     ++b.blocksBroken;
                  }

                  ++b.count;
                  if ((Integer)b.blocksPerTick.get() != 1 && BlockUtils.canInstaBreak(mcPos) && !((HighwayBuilder.Rotation)b.rotation.get()).mine) {
                     break;
                  }
               }

               if (finishedBreaking || !breaking) {
                  b.setState(nextState);
                  b.lastState = lastState;
               }

               return;
            }

            if (!it.hasNext() && BlockUtils.canInstaBreak(mcPos)) {
               finishedBreaking = true;
            }
         }
      }

      protected void place(HighwayBuilder b, HighwayBuilder.MBPIterator it, int slot, HighwayBuilder.State nextState) {
         boolean placed = false;
         boolean finishedPlacing = false;
         Iterator var7 = it.iterator();

         while(true) {
            label42: {
               if (var7.hasNext()) {
                  MBlockPos pos = (MBlockPos)var7.next();
                  if (b.count >= (Integer)b.placementsPerTick.get()) {
                     return;
                  }

                  if (b.placeTimer > 0) {
                     return;
                  }

                  if (!BlockUtils.place(pos.getBlockPos(), class_1268.field_5808, slot, ((HighwayBuilder.Rotation)b.rotation.get()).place, 0, true, true, true)) {
                     break label42;
                  }

                  placed = true;
                  ++b.blocksPlaced;
                  b.placeTimer = (Integer)b.placeDelay.get();
                  ++b.count;
                  if ((Integer)b.placementsPerTick.get() != 1) {
                     break label42;
                  }
               }

               if (finishedPlacing || !placed) {
                  b.setState(nextState);
               }

               return;
            }

            if (!it.hasNext()) {
               finishedPlacing = true;
            }
         }
      }

      private int findSlot(HighwayBuilder b, Predicate<class_1799> predicate, boolean hotbar) {
         for(int i = hotbar ? 0 : 9; i < (hotbar ? 9 : b.mc.field_1724.method_31548().field_7547.size()); ++i) {
            if (predicate.test(b.mc.field_1724.method_31548().method_5438(i))) {
               return i;
            }
         }

         return -1;
      }

      private int findHotbarSlot(HighwayBuilder b, boolean replaceTools) {
         int thrashSlot = -1;
         int slotsWithBlocks = 0;
         int slotWithLeastBlocks = -1;
         int slotWithLeastBlocksCount = Integer.MAX_VALUE;

         for(int i = 0; i < 9; ++i) {
            class_1799 itemStack = b.mc.field_1724.method_31548().method_5438(i);
            if (itemStack.method_7960()) {
               return i;
            }

            if (replaceTools && AutoTool.isTool(itemStack)) {
               return i;
            }

            if (((List)b.trashItems.get()).contains(itemStack.method_7909())) {
               thrashSlot = i;
            }

            class_1792 var10 = itemStack.method_7909();
            if (var10 instanceof class_1747) {
               class_1747 blockItem = (class_1747)var10;
               if (((List)b.blocksToPlace.get()).contains(blockItem.method_7711())) {
                  ++slotsWithBlocks;
                  if (itemStack.method_7947() < slotWithLeastBlocksCount) {
                     slotWithLeastBlocksCount = itemStack.method_7947();
                     slotWithLeastBlocks = i;
                  }
               }
            }
         }

         if (thrashSlot != -1) {
            return thrashSlot;
         } else if (slotsWithBlocks > 1) {
            return slotWithLeastBlocks;
         } else {
            b.error("No empty space in hotbar.");
            return -1;
         }
      }

      private boolean hasItem(HighwayBuilder b, class_1792 item) {
         for(int i = 0; i < b.mc.field_1724.method_31548().field_7547.size(); ++i) {
            if (b.mc.field_1724.method_31548().method_5438(i).method_7909() == item) {
               return true;
            }
         }

         return false;
      }

      protected int countItem(HighwayBuilder b, Predicate<class_1799> predicate) {
         int count = 0;

         for(int i = 0; i < b.mc.field_1724.method_31548().field_7547.size(); ++i) {
            class_1799 stack = b.mc.field_1724.method_31548().method_5438(i);
            if (predicate.test(stack)) {
               count += stack.method_7947();
            }
         }

         return count;
      }

      protected int findAndMoveToHotbar(HighwayBuilder b, Predicate<class_1799> predicate, boolean required) {
         int slot = this.findSlot(b, predicate, true);
         if (slot != -1) {
            return slot;
         } else {
            int hotbarSlot = this.findHotbarSlot(b, false);
            if (hotbarSlot == -1) {
               return -1;
            } else {
               slot = this.findSlot(b, predicate, false);
               if (slot == -1) {
                  if (required) {
                     b.error("Out of items.");
                  }

                  return -1;
               } else {
                  InvUtils.move().from(slot).toHotbar(hotbarSlot);
                  InvUtils.dropHand();
                  return hotbarSlot;
               }
            }
         }
      }

      protected int findAndMoveBestToolToHotbar(HighwayBuilder b, class_2680 blockState, boolean noSilkTouch) {
         if (b.mc.field_1724.method_7337()) {
            return b.mc.field_1724.method_31548().field_7545;
         } else {
            double bestScore = -1.0D;
            int bestSlot = -1;

            int hotbarSlot;
            for(hotbarSlot = 0; hotbarSlot < b.mc.field_1724.method_31548().field_7547.size(); ++hotbarSlot) {
               double score = AutoTool.getScore(b.mc.field_1724.method_31548().method_5438(hotbarSlot), blockState, false, false, AutoTool.EnchantPreference.None, (itemStack) -> {
                  if (noSilkTouch && Utils.hasEnchantment(itemStack, class_1893.field_9099)) {
                     return false;
                  } else {
                     return !(Boolean)b.dontBreakTools.get() || itemStack.method_7936() - itemStack.method_7919() > 1;
                  }
               });
               if (score > bestScore) {
                  bestScore = score;
                  bestSlot = hotbarSlot;
               }
            }

            if (bestSlot == -1) {
               return b.mc.field_1724.method_31548().field_7545;
            } else {
               if (b.mc.field_1724.method_31548().method_5438(bestSlot).method_7909() instanceof class_1810) {
                  hotbarSlot = this.countItem(b, (stack) -> {
                     return stack.method_7909() instanceof class_1810;
                  });
                  if (hotbarSlot <= (Integer)b.savePickaxes.get()) {
                     b.error("Found less than the selected amount of pickaxes required: " + hotbarSlot + "/" + ((Integer)b.savePickaxes.get() + 1));
                     return -1;
                  }
               }

               if (bestSlot < 9) {
                  return bestSlot;
               } else {
                  hotbarSlot = this.findHotbarSlot(b, true);
                  if (hotbarSlot == -1) {
                     return -1;
                  } else {
                     InvUtils.move().from(bestSlot).toHotbar(hotbarSlot);
                     InvUtils.dropHand();
                     return hotbarSlot;
                  }
               }
            }
         }
      }

      protected int findBlocksToPlace(HighwayBuilder b) {
         int slot = this.findAndMoveToHotbar(b, (itemStack) -> {
            class_1792 patt0$temp = itemStack.method_7909();
            boolean var10000;
            if (patt0$temp instanceof class_1747) {
               class_1747 blockItem = (class_1747)patt0$temp;
               if (((List)b.blocksToPlace.get()).contains(blockItem.method_7711())) {
                  var10000 = true;
                  return var10000;
               }
            }

            var10000 = false;
            return var10000;
         }, false);
         if (slot != -1) {
            return slot;
         } else {
            if ((Boolean)b.mineEnderChests.get() && this.hasItem(b, class_1802.field_8466) && this.countItem(b, (stack) -> {
               return stack.method_7909().equals(class_1802.field_8466);
            }) > (Integer)b.saveEchests.get()) {
               b.setState(MineEnderChests);
            } else {
               b.error("Out of blocks to place.");
            }

            return -1;
         }
      }

      protected int findBlocksToPlacePrioritizeTrash(HighwayBuilder b) {
         int slot = this.findAndMoveToHotbar(b, (itemStack) -> {
            return !(itemStack.method_7909() instanceof class_1747) ? false : ((List)b.trashItems.get()).contains(itemStack.method_7909());
         }, false);
         return slot != -1 ? slot : this.findBlocksToPlace(b);
      }

      // $FF: synthetic method
      private static HighwayBuilder.State[] $values() {
         return new HighwayBuilder.State[]{Center, Forward, FillLiquids, MineFront, MineFloor, MineRailings, PlaceRailings, PlaceFloor, ThrowOutTrash, PlaceEChestBlockade, MineEChestBlockade, MineEnderChests};
      }
   }

   private class DiagonalBlockPosProvider implements HighwayBuilder.IBlockPosProvider {
      private final MBlockPos pos = new MBlockPos();
      private final MBlockPos pos2 = new MBlockPos();

      public HighwayBuilder.MBPIterator getFront() {
         this.pos.set((class_1297)HighwayBuilder.this.mc.field_1724).offset(HighwayBuilder.this.dir.rotateLeft()).offset(HighwayBuilder.this.leftDir, HighwayBuilder.this.getWidthLeft() - 1);
         return new HighwayBuilder.MBPIterator() {
            private int i;
            private int w;
            private int y;
            private int pi;
            private int pw;
            private int py;
            // $FF: synthetic field
            final HighwayBuilder.DiagonalBlockPosProvider this$1;

            {
               this.this$1 = this$1;
            }

            public boolean hasNext() {
               return this.i < 2 && this.w < (Integer)this.this$1.this$0.width.get() && this.y < (Integer)this.this$1.this$0.height.get();
            }

            public MBlockPos next() {
               this.this$1.pos2.set(this.this$1.pos).offset(this.this$1.this$0.rightDir, this.w).add(0, this.y++, 0);
               if (this.y >= (Integer)this.this$1.this$0.height.get()) {
                  this.y = 0;
                  ++this.w;
                  if (this.w >= (this.i == 0 ? (Integer)this.this$1.this$0.width.get() - 1 : (Integer)this.this$1.this$0.width.get())) {
                     this.w = 0;
                     ++this.i;
                     this.this$1.pos.set((class_1297)HighwayBuilder.access$5200(this.this$1.this$0).field_1724).offset(this.this$1.this$0.dir).offset(this.this$1.this$0.leftDir, this.this$1.this$0.getWidthLeft());
                  }
               }

               return this.this$1.pos2;
            }

            private void initPos() {
               if (this.i == 0) {
                  this.this$1.pos.set((class_1297)HighwayBuilder.access$5300(this.this$1.this$0).field_1724).offset(this.this$1.this$0.dir.rotateLeft()).offset(this.this$1.this$0.leftDir, this.this$1.this$0.getWidthLeft() - 1);
               } else {
                  this.this$1.pos.set((class_1297)HighwayBuilder.access$5400(this.this$1.this$0).field_1724).offset(this.this$1.this$0.dir).offset(this.this$1.this$0.leftDir, this.this$1.this$0.getWidthLeft());
               }

            }

            public void save() {
               this.pi = this.i;
               this.pw = this.w;
               this.py = this.y;
               this.i = this.w = this.y = 0;
               this.initPos();
            }

            public void restore() {
               this.i = this.pi;
               this.w = this.pw;
               this.y = this.py;
               this.initPos();
            }
         };
      }

      public HighwayBuilder.MBPIterator getFloor() {
         this.pos.set((class_1297)HighwayBuilder.this.mc.field_1724).add(0, -1, 0).offset(HighwayBuilder.this.dir.rotateLeft()).offset(HighwayBuilder.this.leftDir, HighwayBuilder.this.getWidthLeft() - 1);
         return new HighwayBuilder.MBPIterator() {
            private int i;
            private int w;
            private int pi;
            private int pw;
            // $FF: synthetic field
            final HighwayBuilder.DiagonalBlockPosProvider this$1;

            {
               this.this$1 = this$1;
            }

            public boolean hasNext() {
               return this.i < 2 && this.w < (Integer)this.this$1.this$0.width.get();
            }

            public MBlockPos next() {
               this.this$1.pos2.set(this.this$1.pos).offset(this.this$1.this$0.rightDir, this.w++);
               if (this.w >= (this.i == 0 ? (Integer)this.this$1.this$0.width.get() - 1 : (Integer)this.this$1.this$0.width.get())) {
                  this.w = 0;
                  ++this.i;
                  this.this$1.pos.set((class_1297)HighwayBuilder.access$5600(this.this$1.this$0).field_1724).add(0, -1, 0).offset(this.this$1.this$0.dir).offset(this.this$1.this$0.leftDir, this.this$1.this$0.getWidthLeft());
               }

               return this.this$1.pos2;
            }

            private void initPos() {
               if (this.i == 0) {
                  this.this$1.pos.set((class_1297)HighwayBuilder.access$5700(this.this$1.this$0).field_1724).add(0, -1, 0).offset(this.this$1.this$0.dir.rotateLeft()).offset(this.this$1.this$0.leftDir, this.this$1.this$0.getWidthLeft() - 1);
               } else {
                  this.this$1.pos.set((class_1297)HighwayBuilder.access$5800(this.this$1.this$0).field_1724).add(0, -1, 0).offset(this.this$1.this$0.dir).offset(this.this$1.this$0.leftDir, this.this$1.this$0.getWidthLeft());
               }

            }

            public void save() {
               this.pi = this.i;
               this.pw = this.w;
               this.i = this.w = 0;
               this.initPos();
            }

            public void restore() {
               this.i = this.pi;
               this.w = this.pw;
               this.initPos();
            }
         };
      }

      public HighwayBuilder.MBPIterator getRailings(boolean mine) {
         boolean mineAll = mine && (Boolean)HighwayBuilder.this.mineAboveRailings.get();
         this.pos.set((class_1297)HighwayBuilder.this.mc.field_1724).offset(HighwayBuilder.this.dir.rotateLeft()).offset(HighwayBuilder.this.leftDir, HighwayBuilder.this.getWidthLeft());
         return new HighwayBuilder.MBPIterator(mineAll) {
            private int i;
            private int y;
            private int pi;
            private int py;
            // $FF: synthetic field
            final boolean val$mineAll;
            // $FF: synthetic field
            final HighwayBuilder.DiagonalBlockPosProvider this$1;

            {
               this.val$mineAll = var2;
               this.this$1 = this$1;
            }

            public boolean hasNext() {
               return this.i < 2 && this.y < (this.val$mineAll ? (Integer)this.this$1.this$0.height.get() : 1);
            }

            public MBlockPos next() {
               this.this$1.pos2.set(this.this$1.pos).add(0, this.y++, 0);
               if (this.y >= (this.val$mineAll ? (Integer)this.this$1.this$0.height.get() : 1)) {
                  this.y = 0;
                  ++this.i;
                  this.this$1.pos.set((class_1297)HighwayBuilder.access$6000(this.this$1.this$0).field_1724).offset(this.this$1.this$0.dir.rotateRight()).offset(this.this$1.this$0.rightDir, this.this$1.this$0.getWidthRight());
               }

               return this.this$1.pos2;
            }

            private void initPos() {
               if (this.i == 0) {
                  this.this$1.pos.set((class_1297)HighwayBuilder.access$6100(this.this$1.this$0).field_1724).offset(this.this$1.this$0.dir.rotateLeft()).offset(this.this$1.this$0.leftDir, this.this$1.this$0.getWidthLeft());
               } else {
                  this.this$1.pos.set((class_1297)HighwayBuilder.access$6200(this.this$1.this$0).field_1724).offset(this.this$1.this$0.dir.rotateRight()).offset(this.this$1.this$0.rightDir, this.this$1.this$0.getWidthRight());
               }

            }

            public void save() {
               this.pi = this.i;
               this.py = this.y;
               this.i = this.y = 0;
               this.initPos();
            }

            public void restore() {
               this.i = this.pi;
               this.y = this.py;
               this.initPos();
            }
         };
      }

      public HighwayBuilder.MBPIterator getLiquids() {
         boolean m = (Boolean)HighwayBuilder.this.railings.get() && (Boolean)HighwayBuilder.this.mineAboveRailings.get();
         this.pos.set((class_1297)HighwayBuilder.this.mc.field_1724).offset(HighwayBuilder.this.dir).offset(HighwayBuilder.this.dir.rotateLeft()).offset(HighwayBuilder.this.leftDir, HighwayBuilder.this.getWidthLeft());
         return new HighwayBuilder.MBPIterator(m) {
            private int i;
            private int w;
            private int y;
            private int pi;
            private int pw;
            private int py;
            // $FF: synthetic field
            final boolean val$m;
            // $FF: synthetic field
            final HighwayBuilder.DiagonalBlockPosProvider this$1;

            {
               this.val$m = var2;
               this.this$1 = this$1;
            }

            private int getWidth() {
               return (Integer)this.this$1.this$0.width.get() + (this.i == 0 ? 1 : 0) + (this.val$m && this.i == 1 ? 2 : 0);
            }

            public boolean hasNext() {
               if (this.val$m && this.i == 1 && this.y == (Integer)this.this$1.this$0.height.get() && this.w == this.getWidth() - 1) {
                  return false;
               } else {
                  return this.i < 2 && this.w < this.getWidth() && this.y < (Integer)this.this$1.this$0.height.get() + 1;
               }
            }

            private void updateW() {
               ++this.w;
               if (this.w >= this.getWidth()) {
                  this.w = 0;
                  ++this.i;
                  this.this$1.pos.set((class_1297)HighwayBuilder.access$6400(this.this$1.this$0).field_1724).offset(this.this$1.this$0.dir, 2).offset(this.this$1.this$0.leftDir, this.this$1.this$0.getWidthLeft() + (this.val$m ? 1 : 0));
               }

            }

            public MBlockPos next() {
               if (this.i == (this.val$m ? 1 : 0) && this.y == (Integer)this.this$1.this$0.height.get() && (this.w == 0 || this.w == this.getWidth() - 1)) {
                  this.y = 0;
                  this.updateW();
               }

               this.this$1.pos2.set(this.this$1.pos).offset(this.this$1.this$0.rightDir, this.w).add(0, this.y++, 0);
               if (this.y >= (Integer)this.this$1.this$0.height.get() + 1) {
                  this.y = 0;
                  this.updateW();
               }

               return this.this$1.pos2;
            }

            private void initPos() {
               if (this.i == 0) {
                  this.this$1.pos.set((class_1297)HighwayBuilder.access$6500(this.this$1.this$0).field_1724).offset(this.this$1.this$0.dir).offset(this.this$1.this$0.dir.rotateLeft()).offset(this.this$1.this$0.leftDir, this.this$1.this$0.getWidthLeft());
               } else {
                  this.this$1.pos.set((class_1297)HighwayBuilder.access$6600(this.this$1.this$0).field_1724).offset(this.this$1.this$0.dir, 2).offset(this.this$1.this$0.leftDir, this.this$1.this$0.getWidthLeft() + (this.val$m ? 1 : 0));
               }

            }

            public void save() {
               this.pi = this.i;
               this.pw = this.w;
               this.py = this.y;
               this.i = this.w = this.y = 0;
               this.initPos();
            }

            public void restore() {
               this.i = this.pi;
               this.w = this.pw;
               this.y = this.py;
               this.initPos();
            }
         };
      }

      public HighwayBuilder.MBPIterator getEChestBlockade(boolean mine) {
         return new HighwayBuilder.MBPIterator(mine) {
            private int i;
            private int y;
            private int pi;
            private int py;
            // $FF: synthetic field
            final boolean val$mine;
            // $FF: synthetic field
            final HighwayBuilder.DiagonalBlockPosProvider this$1;

            {
               this.val$mine = var2;
               this.this$1 = this$1;
               this.i = this.val$mine ? -1 : 0;
            }

            private MBlockPos get(int i) {
               HorizontalDirection dir2 = this.this$1.this$0.dir.rotateLeft().rotateLeftSkipOne();
               this.this$1.pos.set((class_1297)HighwayBuilder.access$6700(this.this$1.this$0).field_1724).offset(dir2);
               MBlockPos var10000;
               switch(i) {
               case -1:
                  var10000 = this.this$1.pos;
                  break;
               case 0:
               default:
                  var10000 = this.this$1.pos.offset(dir2);
                  break;
               case 1:
                  var10000 = this.this$1.pos.offset(dir2.rotateLeftSkipOne());
                  break;
               case 2:
                  var10000 = this.this$1.pos.offset(dir2.rotateLeftSkipOne().opposite());
                  break;
               case 3:
                  var10000 = this.this$1.pos.offset(dir2.opposite(), 2);
               }

               return var10000;
            }

            public boolean hasNext() {
               return this.i < 4 && this.y < 2;
            }

            public MBlockPos next() {
               MBlockPos pos = this.get(this.i).add(0, this.y, 0);
               ++this.y;
               if (this.y > 1) {
                  this.y = 0;
                  ++this.i;
               }

               return pos;
            }

            public void save() {
               this.pi = this.i;
               this.py = this.y;
               this.i = this.y = 0;
            }

            public void restore() {
               this.i = this.pi;
               this.y = this.py;
            }
         };
      }
   }

   private class StraightBlockPosProvider implements HighwayBuilder.IBlockPosProvider {
      private final MBlockPos pos = new MBlockPos();
      private final MBlockPos pos2 = new MBlockPos();

      public HighwayBuilder.MBPIterator getFront() {
         this.pos.set((class_1297)HighwayBuilder.this.mc.field_1724).offset(HighwayBuilder.this.dir).offset(HighwayBuilder.this.leftDir, HighwayBuilder.this.getWidthLeft());
         return new HighwayBuilder.MBPIterator() {
            private int w;
            private int y;
            private int pw;
            private int py;
            // $FF: synthetic field
            final HighwayBuilder.StraightBlockPosProvider this$1;

            {
               this.this$1 = this$1;
            }

            public boolean hasNext() {
               return this.w < (Integer)this.this$1.this$0.width.get() && this.y < (Integer)this.this$1.this$0.height.get();
            }

            public MBlockPos next() {
               this.this$1.pos2.set(this.this$1.pos).offset(this.this$1.this$0.rightDir, this.w).add(0, this.y, 0);
               ++this.w;
               if (this.w >= (Integer)this.this$1.this$0.width.get()) {
                  this.w = 0;
                  ++this.y;
               }

               return this.this$1.pos2;
            }

            public void save() {
               this.pw = this.w;
               this.py = this.y;
               this.w = this.y = 0;
            }

            public void restore() {
               this.w = this.pw;
               this.y = this.py;
            }
         };
      }

      public HighwayBuilder.MBPIterator getFloor() {
         this.pos.set((class_1297)HighwayBuilder.this.mc.field_1724).offset(HighwayBuilder.this.dir).offset(HighwayBuilder.this.leftDir, HighwayBuilder.this.getWidthLeft()).add(0, -1, 0);
         return new HighwayBuilder.MBPIterator() {
            private int w;
            private int pw;
            // $FF: synthetic field
            final HighwayBuilder.StraightBlockPosProvider this$1;

            {
               this.this$1 = this$1;
            }

            public boolean hasNext() {
               return this.w < (Integer)this.this$1.this$0.width.get();
            }

            public MBlockPos next() {
               return this.this$1.pos2.set(this.this$1.pos).offset(this.this$1.this$0.rightDir, this.w++);
            }

            public void save() {
               this.pw = this.w;
               this.w = 0;
            }

            public void restore() {
               this.w = this.pw;
            }
         };
      }

      public HighwayBuilder.MBPIterator getRailings(boolean mine) {
         boolean mineAll = mine && (Boolean)HighwayBuilder.this.mineAboveRailings.get();
         this.pos.set((class_1297)HighwayBuilder.this.mc.field_1724).offset(HighwayBuilder.this.dir);
         return new HighwayBuilder.MBPIterator(mineAll) {
            private int i;
            private int y;
            private int pi;
            private int py;
            // $FF: synthetic field
            final boolean val$mineAll;
            // $FF: synthetic field
            final HighwayBuilder.StraightBlockPosProvider this$1;

            {
               this.val$mineAll = var2;
               this.this$1 = this$1;
            }

            public boolean hasNext() {
               return this.i < 2 && this.y < (this.val$mineAll ? (Integer)this.this$1.this$0.height.get() : 1);
            }

            public MBlockPos next() {
               if (this.i == 0) {
                  this.this$1.pos2.set(this.this$1.pos).offset(this.this$1.this$0.leftDir, this.this$1.this$0.getWidthLeft() + 1).add(0, this.y, 0);
               } else {
                  this.this$1.pos2.set(this.this$1.pos).offset(this.this$1.this$0.rightDir, this.this$1.this$0.getWidthRight() + 1).add(0, this.y, 0);
               }

               ++this.y;
               if (this.y >= (this.val$mineAll ? (Integer)this.this$1.this$0.height.get() : 1)) {
                  this.y = 0;
                  ++this.i;
               }

               return this.this$1.pos2;
            }

            public void save() {
               this.pi = this.i;
               this.py = this.y;
               this.i = this.y = 0;
            }

            public void restore() {
               this.i = this.pi;
               this.y = this.py;
            }
         };
      }

      public HighwayBuilder.MBPIterator getLiquids() {
         this.pos.set((class_1297)HighwayBuilder.this.mc.field_1724).offset(HighwayBuilder.this.dir, 2).offset(HighwayBuilder.this.leftDir, HighwayBuilder.this.getWidthLeft() + ((Boolean)HighwayBuilder.this.railings.get() && (Boolean)HighwayBuilder.this.mineAboveRailings.get() ? 2 : 1));
         return new HighwayBuilder.MBPIterator() {
            private int w;
            private int y;
            private int pw;
            private int py;
            // $FF: synthetic field
            final HighwayBuilder.StraightBlockPosProvider this$1;

            {
               this.this$1 = this$1;
            }

            private int getWidth() {
               return (Integer)this.this$1.this$0.width.get() + ((Boolean)this.this$1.this$0.railings.get() && (Boolean)this.this$1.this$0.mineAboveRailings.get() ? 2 : 0);
            }

            public boolean hasNext() {
               return this.w < this.getWidth() + 2 && this.y < (Integer)this.this$1.this$0.height.get() + 1;
            }

            public MBlockPos next() {
               this.this$1.pos2.set(this.this$1.pos).offset(this.this$1.this$0.rightDir, this.w).add(0, this.y, 0);
               ++this.w;
               if (this.w >= this.getWidth() + 2) {
                  this.w = 0;
                  ++this.y;
               }

               return this.this$1.pos2;
            }

            public void save() {
               this.pw = this.w;
               this.py = this.y;
               this.w = this.y = 0;
            }

            public void restore() {
               this.w = this.pw;
               this.y = this.py;
            }
         };
      }

      public HighwayBuilder.MBPIterator getEChestBlockade(boolean mine) {
         return new HighwayBuilder.MBPIterator(mine) {
            private int i;
            private int y;
            private int pi;
            private int py;
            // $FF: synthetic field
            final boolean val$mine;
            // $FF: synthetic field
            final HighwayBuilder.StraightBlockPosProvider this$1;

            {
               this.val$mine = var2;
               this.this$1 = this$1;
               this.i = this.val$mine ? -1 : 0;
            }

            private MBlockPos get(int i) {
               this.this$1.pos.set((class_1297)HighwayBuilder.access$5000(this.this$1.this$0).field_1724).offset(this.this$1.this$0.dir.opposite());
               MBlockPos var10000;
               switch(i) {
               case -1:
                  var10000 = this.this$1.pos;
                  break;
               case 0:
               default:
                  var10000 = this.this$1.pos.offset(this.this$1.this$0.dir.opposite());
                  break;
               case 1:
                  var10000 = this.this$1.pos.offset(this.this$1.this$0.leftDir);
                  break;
               case 2:
                  var10000 = this.this$1.pos.offset(this.this$1.this$0.rightDir);
                  break;
               case 3:
                  var10000 = this.this$1.pos.offset(this.this$1.this$0.dir, 2);
               }

               return var10000;
            }

            public boolean hasNext() {
               return this.i < 4 && this.y < 2;
            }

            public MBlockPos next() {
               if ((Integer)this.this$1.this$0.width.get() == 1 && (Boolean)this.this$1.this$0.railings.get() && this.i > 0 && this.y == 0) {
                  ++this.y;
               }

               MBlockPos pos = this.get(this.i).add(0, this.y, 0);
               ++this.y;
               if (this.y > 1) {
                  this.y = 0;
                  ++this.i;
               }

               return pos;
            }

            public void save() {
               this.pi = this.i;
               this.py = this.y;
               this.i = this.y = 0;
            }

            public void restore() {
               this.i = this.pi;
               this.y = this.py;
            }
         };
      }
   }

   private interface IBlockPosProvider {
      HighwayBuilder.MBPIterator getFront();

      HighwayBuilder.MBPIterator getFloor();

      HighwayBuilder.MBPIterator getRailings(boolean var1);

      HighwayBuilder.MBPIterator getLiquids();

      HighwayBuilder.MBPIterator getEChestBlockade(boolean var1);
   }

   private interface MBPIterator extends Iterator<MBlockPos>, Iterable<MBlockPos> {
      void save();

      void restore();

      @NotNull
      default Iterator<MBlockPos> iterator() {
         return this;
      }
   }

   private static class MBPIteratorFilter implements HighwayBuilder.MBPIterator {
      private final HighwayBuilder.MBPIterator it;
      private final Predicate<MBlockPos> predicate;
      private MBlockPos pos;
      private boolean isOld = true;
      private boolean pisOld = true;

      public MBPIteratorFilter(HighwayBuilder.MBPIterator it, Predicate<MBlockPos> predicate) {
         this.it = it;
         this.predicate = predicate;
      }

      public void save() {
         this.it.save();
         this.pisOld = this.isOld;
         this.isOld = true;
      }

      public void restore() {
         this.it.restore();
         this.isOld = this.pisOld;
      }

      public boolean hasNext() {
         if (this.isOld) {
            this.isOld = false;

            for(this.pos = null; this.it.hasNext(); this.pos = null) {
               this.pos = (MBlockPos)this.it.next();
               if (this.predicate.test(this.pos)) {
                  return true;
               }
            }
         }

         return this.pos != null && this.predicate.test(this.pos);
      }

      public MBlockPos next() {
         this.isOld = true;
         return this.pos;
      }
   }
}
