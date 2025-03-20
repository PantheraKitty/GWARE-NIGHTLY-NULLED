package meteordevelopment.meteorclient.systems.modules.render;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixin.AbstractBlockAccessor;
import meteordevelopment.meteorclient.renderer.Renderer3D;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.misc.Pool;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.BlockIterator;
import meteordevelopment.meteorclient.utils.world.Dir;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2680;
import net.minecraft.class_2818;
import net.minecraft.class_4076;
import net.minecraft.class_2338.class_2339;

public class HoleESP extends Module {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgRender;
   private final Setting<Integer> horizontalRadius;
   private final Setting<Integer> verticalRadius;
   private final Setting<Integer> holeHeight;
   private final Setting<Boolean> doubles;
   private final Setting<Boolean> ignoreOwn;
   private final Setting<Boolean> webs;
   private final Setting<ShapeMode> shapeMode;
   private final Setting<Double> height;
   private final Setting<Boolean> topQuad;
   private final Setting<Boolean> bottomQuad;
   private final Setting<SettingColor> bedrockColorTop;
   private final Setting<SettingColor> bedrockColorBottom;
   private final Setting<SettingColor> obsidianColorTop;
   private final Setting<SettingColor> obsidianColorBottom;
   private final Setting<SettingColor> mixedColorTop;
   private final Setting<SettingColor> mixedColorBottom;
   private final Pool<HoleESP.Hole> holePool;
   private final List<HoleESP.Hole> holes;
   private final byte NULL;

   public HoleESP() {
      super(Categories.Render, "hole-esp", "Displays holes that you will take less damage in.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgRender = this.settings.createGroup("Render");
      this.horizontalRadius = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("horizontal-radius")).description("Horizontal radius in which to search for holes.")).defaultValue(10)).min(0).sliderMax(32).build());
      this.verticalRadius = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("vertical-radius")).description("Vertical radius in which to search for holes.")).defaultValue(5)).min(0).sliderMax(32).build());
      this.holeHeight = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("min-height")).description("Minimum hole height required to be rendered.")).defaultValue(3)).min(1).sliderMin(1).build());
      this.doubles = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("doubles")).description("Highlights double holes that can be stood across.")).defaultValue(true)).build());
      this.ignoreOwn = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("ignore-own")).description("Ignores rendering the hole you are currently standing in.")).defaultValue(false)).build());
      this.webs = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("webs")).description("Whether to show holes that have webs inside of them.")).defaultValue(false)).build());
      this.shapeMode = this.sgRender.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("shape-mode")).description("How the shapes are rendered.")).defaultValue(ShapeMode.Both)).build());
      this.height = this.sgRender.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("height")).description("The height of rendering.")).defaultValue(0.2D).min(0.0D).build());
      this.topQuad = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("top-quad")).description("Whether to render a quad at the top of the hole.")).defaultValue(true)).build());
      this.bottomQuad = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("bottom-quad")).description("Whether to render a quad at the bottom of the hole.")).defaultValue(false)).build());
      this.bedrockColorTop = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("bedrock-top")).description("The top color for holes that are completely bedrock.")).defaultValue(new SettingColor(100, 255, 0, 200)).build());
      this.bedrockColorBottom = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("bedrock-bottom")).description("The bottom color for holes that are completely bedrock.")).defaultValue(new SettingColor(100, 255, 0, 0)).build());
      this.obsidianColorTop = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("obsidian-top")).description("The top color for holes that are completely obsidian.")).defaultValue(new SettingColor(255, 0, 0, 200)).build());
      this.obsidianColorBottom = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("obsidian-bottom")).description("The bottom color for holes that are completely obsidian.")).defaultValue(new SettingColor(255, 0, 0, 0)).build());
      this.mixedColorTop = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("mixed-top")).description("The top color for holes that have mixed bedrock and obsidian.")).defaultValue(new SettingColor(255, 127, 0, 200)).build());
      this.mixedColorBottom = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("mixed-bottom")).description("The bottom color for holes that have mixed bedrock and obsidian.")).defaultValue(new SettingColor(255, 127, 0, 0)).build());
      this.holePool = new Pool(HoleESP.Hole::new);
      this.holes = new ArrayList();
      this.NULL = 0;
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      Iterator var2 = this.holes.iterator();

      while(var2.hasNext()) {
         HoleESP.Hole hole = (HoleESP.Hole)var2.next();
         this.holePool.free(hole);
      }

      this.holes.clear();
      BlockIterator.register((Integer)this.horizontalRadius.get(), (Integer)this.verticalRadius.get(), (blockPos, blockState) -> {
         if (this.validHole(blockPos)) {
            int bedrock = 0;
            int obsidian = 0;
            class_2350 air = null;
            class_2350[] var6 = class_2350.values();
            int var7 = var6.length;

            for(int var8 = 0; var8 < var7; ++var8) {
               class_2350 direction = var6[var8];
               if (direction != class_2350.field_11036) {
                  class_2338 offsetPos = blockPos.method_10093(direction);
                  class_2680 state = this.mc.field_1687.method_8320(offsetPos);
                  if (state.method_26204() == class_2246.field_9987) {
                     ++bedrock;
                  } else if (state.method_26204() == class_2246.field_10540) {
                     ++obsidian;
                  } else {
                     if (direction == class_2350.field_11033) {
                        return;
                     }

                     if ((Boolean)this.doubles.get() && air == null && this.validHole(offsetPos)) {
                        class_2350[] var12 = class_2350.values();
                        int var13 = var12.length;

                        for(int var14 = 0; var14 < var13; ++var14) {
                           class_2350 dir = var12[var14];
                           if (dir != direction.method_10153() && dir != class_2350.field_11036) {
                              class_2680 blockState1 = this.mc.field_1687.method_8320(offsetPos.method_10093(dir));
                              if (blockState1.method_26204() == class_2246.field_9987) {
                                 ++bedrock;
                              } else {
                                 if (blockState1.method_26204() != class_2246.field_10540) {
                                    return;
                                 }

                                 ++obsidian;
                              }
                           }
                        }

                        air = direction;
                     }
                  }
               }
            }

            if (obsidian + bedrock == 5 && air == null) {
               this.holes.add(((HoleESP.Hole)this.holePool.get()).set(blockPos, obsidian == 5 ? HoleESP.Hole.Type.Obsidian : (bedrock == 5 ? HoleESP.Hole.Type.Bedrock : HoleESP.Hole.Type.Mixed), (byte)0));
            } else if (obsidian + bedrock == 8 && (Boolean)this.doubles.get() && air != null) {
               this.holes.add(((HoleESP.Hole)this.holePool.get()).set(blockPos, obsidian == 8 ? HoleESP.Hole.Type.Obsidian : (bedrock == 8 ? HoleESP.Hole.Type.Bedrock : HoleESP.Hole.Type.Mixed), Dir.get(air)));
            }

         }
      });
   }

   private boolean validHole(class_2338 pos) {
      if ((Boolean)this.ignoreOwn.get() && this.mc.field_1724.method_24515().equals(pos)) {
         return false;
      } else {
         class_2818 chunk = this.mc.field_1687.method_8497(class_4076.method_18675(pos.method_10263()), class_4076.method_18675(pos.method_10260()));
         class_2248 block = chunk.method_8320(pos).method_26204();
         if (!(Boolean)this.webs.get() && block == class_2246.field_10343) {
            return false;
         } else if (((AbstractBlockAccessor)block).isCollidable()) {
            return false;
         } else {
            for(int i = 0; i < (Integer)this.holeHeight.get(); ++i) {
               if (((AbstractBlockAccessor)chunk.method_8320(pos.method_10086(i)).method_26204()).isCollidable()) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   @EventHandler
   private void onRender(Render3DEvent event) {
      Iterator var2 = this.holes.iterator();

      while(var2.hasNext()) {
         HoleESP.Hole hole = (HoleESP.Hole)var2.next();
         hole.render(event.renderer, (ShapeMode)this.shapeMode.get(), (Double)this.height.get(), (Boolean)this.topQuad.get(), (Boolean)this.bottomQuad.get());
      }

   }

   private static class Hole {
      public class_2339 blockPos = new class_2339();
      public byte exclude;
      public HoleESP.Hole.Type type;

      public HoleESP.Hole set(class_2338 blockPos, HoleESP.Hole.Type type, byte exclude) {
         this.blockPos.method_10101(blockPos);
         this.exclude = exclude;
         this.type = type;
         return this;
      }

      public Color getTopColor() {
         SettingColor var10000;
         switch(this.type.ordinal()) {
         case 0:
            var10000 = (SettingColor)((HoleESP)Modules.get().get(HoleESP.class)).bedrockColorTop.get();
            break;
         case 1:
            var10000 = (SettingColor)((HoleESP)Modules.get().get(HoleESP.class)).obsidianColorTop.get();
            break;
         default:
            var10000 = (SettingColor)((HoleESP)Modules.get().get(HoleESP.class)).mixedColorTop.get();
         }

         return var10000;
      }

      public Color getBottomColor() {
         SettingColor var10000;
         switch(this.type.ordinal()) {
         case 0:
            var10000 = (SettingColor)((HoleESP)Modules.get().get(HoleESP.class)).bedrockColorBottom.get();
            break;
         case 1:
            var10000 = (SettingColor)((HoleESP)Modules.get().get(HoleESP.class)).obsidianColorBottom.get();
            break;
         default:
            var10000 = (SettingColor)((HoleESP)Modules.get().get(HoleESP.class)).mixedColorBottom.get();
         }

         return var10000;
      }

      public void render(Renderer3D renderer, ShapeMode mode, double height, boolean topQuad, boolean bottomQuad) {
         int x = this.blockPos.method_10263();
         int y = this.blockPos.method_10264();
         int z = this.blockPos.method_10260();
         Color top = this.getTopColor();
         Color bottom = this.getBottomColor();
         int originalTopA = top.a;
         int originalBottompA = bottom.a;
         if (mode.lines()) {
            if (Dir.isNot(this.exclude, (byte)32) && Dir.isNot(this.exclude, (byte)8)) {
               renderer.line((double)x, (double)y, (double)z, (double)x, (double)y + height, (double)z, bottom, top);
            }

            if (Dir.isNot(this.exclude, (byte)32) && Dir.isNot(this.exclude, (byte)16)) {
               renderer.line((double)x, (double)y, (double)(z + 1), (double)x, (double)y + height, (double)(z + 1), bottom, top);
            }

            if (Dir.isNot(this.exclude, (byte)64) && Dir.isNot(this.exclude, (byte)8)) {
               renderer.line((double)(x + 1), (double)y, (double)z, (double)(x + 1), (double)y + height, (double)z, bottom, top);
            }

            if (Dir.isNot(this.exclude, (byte)64) && Dir.isNot(this.exclude, (byte)16)) {
               renderer.line((double)(x + 1), (double)y, (double)(z + 1), (double)(x + 1), (double)y + height, (double)(z + 1), bottom, top);
            }

            if (Dir.isNot(this.exclude, (byte)8)) {
               renderer.line((double)x, (double)y, (double)z, (double)(x + 1), (double)y, (double)z, bottom);
            }

            if (Dir.isNot(this.exclude, (byte)8)) {
               renderer.line((double)x, (double)y + height, (double)z, (double)(x + 1), (double)y + height, (double)z, top);
            }

            if (Dir.isNot(this.exclude, (byte)16)) {
               renderer.line((double)x, (double)y, (double)(z + 1), (double)(x + 1), (double)y, (double)(z + 1), bottom);
            }

            if (Dir.isNot(this.exclude, (byte)16)) {
               renderer.line((double)x, (double)y + height, (double)(z + 1), (double)(x + 1), (double)y + height, (double)(z + 1), top);
            }

            if (Dir.isNot(this.exclude, (byte)32)) {
               renderer.line((double)x, (double)y, (double)z, (double)x, (double)y, (double)(z + 1), bottom);
            }

            if (Dir.isNot(this.exclude, (byte)32)) {
               renderer.line((double)x, (double)y + height, (double)z, (double)x, (double)y + height, (double)(z + 1), top);
            }

            if (Dir.isNot(this.exclude, (byte)64)) {
               renderer.line((double)(x + 1), (double)y, (double)z, (double)(x + 1), (double)y, (double)(z + 1), bottom);
            }

            if (Dir.isNot(this.exclude, (byte)64)) {
               renderer.line((double)(x + 1), (double)y + height, (double)z, (double)(x + 1), (double)y + height, (double)(z + 1), top);
            }
         }

         if (mode.sides()) {
            top.a = originalTopA / 2;
            bottom.a = originalBottompA / 2;
            if (Dir.isNot(this.exclude, (byte)2) && topQuad) {
               renderer.quad((double)x, (double)y + height, (double)z, (double)x, (double)y + height, (double)(z + 1), (double)(x + 1), (double)y + height, (double)(z + 1), (double)(x + 1), (double)y + height, (double)z, top);
            }

            if (Dir.isNot(this.exclude, (byte)4) && bottomQuad) {
               renderer.quad((double)x, (double)y, (double)z, (double)x, (double)y, (double)(z + 1), (double)(x + 1), (double)y, (double)(z + 1), (double)(x + 1), (double)y, (double)z, bottom);
            }

            if (Dir.isNot(this.exclude, (byte)8)) {
               renderer.gradientQuadVertical((double)x, (double)y, (double)z, (double)(x + 1), (double)y + height, (double)z, top, bottom);
            }

            if (Dir.isNot(this.exclude, (byte)16)) {
               renderer.gradientQuadVertical((double)x, (double)y, (double)(z + 1), (double)(x + 1), (double)y + height, (double)(z + 1), top, bottom);
            }

            if (Dir.isNot(this.exclude, (byte)32)) {
               renderer.gradientQuadVertical((double)x, (double)y, (double)z, (double)x, (double)y + height, (double)(z + 1), top, bottom);
            }

            if (Dir.isNot(this.exclude, (byte)64)) {
               renderer.gradientQuadVertical((double)(x + 1), (double)y, (double)z, (double)(x + 1), (double)y + height, (double)(z + 1), top, bottom);
            }

            top.a = originalTopA;
            bottom.a = originalBottompA;
         }

      }

      public static enum Type {
         Bedrock,
         Obsidian,
         Mixed;

         // $FF: synthetic method
         private static HoleESP.Hole.Type[] $values() {
            return new HoleESP.Hole.Type[]{Bedrock, Obsidian, Mixed};
         }
      }
   }
}
