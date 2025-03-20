package meteordevelopment.meteorclient.systems.hud.elements;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.ModuleListSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.hud.Alignment;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;

public class ActiveModulesHud extends HudElement {
   public static final HudElementInfo<ActiveModulesHud> INFO;
   private static final Color WHITE;
   private final SettingGroup sgGeneral;
   private final Setting<List<Module>> shownModules;
   private final Setting<ActiveModulesHud.Sort> sort;
   private final Setting<Boolean> activeInfo;
   private final Setting<SettingColor> moduleInfoColor;
   private final Setting<ActiveModulesHud.ColorMode> colorMode;
   private final Setting<SettingColor> flatColor;
   private final Setting<Boolean> shadow;
   private final Setting<Alignment> alignment;
   private final Setting<Boolean> outlines;
   private final Setting<Integer> outlineWidth;
   private final Setting<Boolean> customScale;
   private final Setting<Double> scale;
   private final Setting<Double> rainbowSpeed;
   private final Setting<Double> rainbowSpread;
   private final Setting<Double> rainbowSaturation;
   private final Setting<Double> rainbowBrightness;
   private final List<Module> modules;
   private final Color rainbow;
   private double rainbowHue1;
   private double rainbowHue2;
   private double prevX;
   private double prevTextLength;
   private Color prevColor;

   public ActiveModulesHud() {
      super(INFO);
      this.sgGeneral = this.settings.getDefaultGroup();
      this.shownModules = this.sgGeneral.add(((ModuleListSetting.Builder)((ModuleListSetting.Builder)(new ModuleListSetting.Builder()).name("visible-modules")).description("Which modules to show in the list")).build());
      this.sort = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("sort")).description("How to sort active modules.")).defaultValue(ActiveModulesHud.Sort.Biggest)).build());
      this.activeInfo = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("additional-info")).description("Shows additional info from the module next to the name in the active modules list.")).defaultValue(true)).build());
      SettingGroup var10001 = this.sgGeneral;
      ColorSetting.Builder var10002 = ((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("module-info-color")).description("Color of module info text.")).defaultValue(new SettingColor(175, 175, 175));
      Setting var10003 = this.activeInfo;
      Objects.requireNonNull(var10003);
      this.moduleInfoColor = var10001.add(((ColorSetting.Builder)var10002.visible(var10003::get)).build());
      this.colorMode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("color-mode")).description("What color to use for active modules.")).defaultValue(ActiveModulesHud.ColorMode.Rainbow)).build());
      this.flatColor = this.sgGeneral.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("flat-color")).description("Color for flat color mode.")).defaultValue(new SettingColor(225, 25, 25)).visible(() -> {
         return this.colorMode.get() == ActiveModulesHud.ColorMode.Flat;
      })).build());
      this.shadow = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("shadow")).description("Renders shadow behind text.")).defaultValue(true)).build());
      this.alignment = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("alignment")).description("Horizontal alignment.")).defaultValue(Alignment.Auto)).build());
      this.outlines = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("outlines")).description("Whether or not to render outlines")).defaultValue(false)).build());
      var10001 = this.sgGeneral;
      IntSetting.Builder var1 = ((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("outline-width")).description("Outline width")).defaultValue(2)).min(1).sliderMin(1);
      var10003 = this.outlines;
      Objects.requireNonNull(var10003);
      this.outlineWidth = var10001.add(((IntSetting.Builder)var1.visible(var10003::get)).build());
      this.customScale = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("custom-scale")).description("Applies custom text scale rather than the global one.")).defaultValue(false)).build());
      var10001 = this.sgGeneral;
      DoubleSetting.Builder var2 = (DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("scale")).description("Custom scale.");
      var10003 = this.customScale;
      Objects.requireNonNull(var10003);
      this.scale = var10001.add(((DoubleSetting.Builder)var2.visible(var10003::get)).defaultValue(1.0D).min(0.5D).sliderRange(0.5D, 3.0D).build());
      this.rainbowSpeed = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("rainbow-speed")).description("Rainbow speed of rainbow color mode.")).defaultValue(0.05D).sliderMin(0.01D).sliderMax(0.2D).decimalPlaces(4).visible(() -> {
         return this.colorMode.get() == ActiveModulesHud.ColorMode.Rainbow;
      })).build());
      this.rainbowSpread = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("rainbow-spread")).description("Rainbow spread of rainbow color mode.")).defaultValue(0.01D).sliderMin(0.001D).sliderMax(0.05D).decimalPlaces(4).visible(() -> {
         return this.colorMode.get() == ActiveModulesHud.ColorMode.Rainbow;
      })).build());
      this.rainbowSaturation = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("rainbow-saturation")).defaultValue(1.0D).sliderRange(0.0D, 1.0D).visible(() -> {
         return this.colorMode.get() == ActiveModulesHud.ColorMode.Rainbow;
      })).build());
      this.rainbowBrightness = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("rainbow-brightness")).defaultValue(1.0D).sliderRange(0.0D, 1.0D).visible(() -> {
         return this.colorMode.get() == ActiveModulesHud.ColorMode.Rainbow;
      })).build());
      this.modules = new ArrayList();
      this.rainbow = new Color(255, 255, 255);
      this.prevColor = new Color();
   }

   public void tick(HudRenderer renderer) {
      this.modules.clear();
      Iterator var2 = Modules.get().getActive().iterator();

      while(var2.hasNext()) {
         Module module = (Module)var2.next();
         if (((List)this.shownModules.get()).contains(module)) {
            this.modules.add(module);
         }
      }

      if (this.modules.isEmpty()) {
         if (this.isInEditor()) {
            this.setSize(renderer.textWidth("Active Modules", (Boolean)this.shadow.get(), this.getScale()), renderer.textHeight((Boolean)this.shadow.get(), this.getScale()));
         }

      } else {
         this.modules.sort((e1, e2) -> {
            int var10000;
            switch(((ActiveModulesHud.Sort)this.sort.get()).ordinal()) {
            case 0:
               var10000 = e1.title.compareTo(e2.title);
               break;
            case 1:
               var10000 = Double.compare(this.getModuleWidth(renderer, e2), this.getModuleWidth(renderer, e1));
               break;
            case 2:
               var10000 = Double.compare(this.getModuleWidth(renderer, e1), this.getModuleWidth(renderer, e2));
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
            }

            return var10000;
         });
         double width = 0.0D;
         double height = 0.0D;

         for(int i = 0; i < this.modules.size(); ++i) {
            Module module = (Module)this.modules.get(i);
            width = Math.max(width, this.getModuleWidth(renderer, module));
            height += renderer.textHeight((Boolean)this.shadow.get(), this.getScale());
            if (i > 0) {
               height += 2.0D;
            }
         }

         this.setSize(width, height);
      }
   }

   public void render(HudRenderer renderer) {
      double x = (double)this.x;
      double y = (double)this.y;
      if (this.modules.isEmpty()) {
         if (this.isInEditor()) {
            renderer.text("Active Modules", x, y, WHITE, (Boolean)this.shadow.get(), this.getScale());
         }

      } else {
         this.rainbowHue1 += (Double)this.rainbowSpeed.get() * renderer.delta;
         if (this.rainbowHue1 > 1.0D) {
            --this.rainbowHue1;
         } else if (this.rainbowHue1 < -1.0D) {
            ++this.rainbowHue1;
         }

         this.rainbowHue2 = this.rainbowHue1;
         this.prevX = x;

         for(int i = 0; i < this.modules.size(); ++i) {
            double offset = this.alignX(this.getModuleWidth(renderer, (Module)this.modules.get(i)), (Alignment)this.alignment.get());
            this.renderModule(renderer, this.modules, i, x + offset, y);
            this.prevX = x + offset;
            y += 2.0D + renderer.textHeight((Boolean)this.shadow.get(), this.getScale());
         }

      }
   }

   private void renderModule(HudRenderer renderer, List<Module> modules, int index, double x, double y) {
      Module module = (Module)modules.get(index);
      Color color = (Color)this.flatColor.get();
      switch(((ActiveModulesHud.ColorMode)this.colorMode.get()).ordinal()) {
      case 1:
         color = module.color;
         break;
      case 2:
         this.rainbowHue2 += (Double)this.rainbowSpread.get();
         int c = java.awt.Color.HSBtoRGB((float)this.rainbowHue2, ((Double)this.rainbowSaturation.get()).floatValue(), ((Double)this.rainbowBrightness.get()).floatValue());
         this.rainbow.r = Color.toRGBAR(c);
         this.rainbow.g = Color.toRGBAG(c);
         this.rainbow.b = Color.toRGBAB(c);
         color = this.rainbow;
      }

      renderer.text(module.title, x, y, color, (Boolean)this.shadow.get(), this.getScale());
      double emptySpace = renderer.textWidth(" ", (Boolean)this.shadow.get(), this.getScale());
      double textHeight = renderer.textHeight((Boolean)this.shadow.get(), this.getScale());
      double textLength = renderer.textWidth(module.title, (Boolean)this.shadow.get(), this.getScale());
      if ((Boolean)this.activeInfo.get()) {
         String info = module.getInfoString();
         if (info != null) {
            renderer.text(info, x + emptySpace + textLength, y, (Color)this.moduleInfoColor.get(), (Boolean)this.shadow.get(), this.getScale());
            textLength += emptySpace + renderer.textWidth(info, (Boolean)this.shadow.get(), this.getScale());
         }
      }

      if ((Boolean)this.outlines.get()) {
         if (index == 0) {
            renderer.quad(x - 2.0D - (double)(Integer)this.outlineWidth.get(), y - 2.0D, (double)(Integer)this.outlineWidth.get(), textHeight + 4.0D, this.prevColor, this.prevColor, color, color);
            renderer.quad(x + textLength + 2.0D, y - 2.0D, (double)(Integer)this.outlineWidth.get(), textHeight + 4.0D, this.prevColor, this.prevColor, color, color);
            renderer.quad(x - 2.0D - (double)(Integer)this.outlineWidth.get(), y - 2.0D - (double)(Integer)this.outlineWidth.get(), textLength + 4.0D + (double)((Integer)this.outlineWidth.get() * 2), (double)(Integer)this.outlineWidth.get(), this.prevColor, this.prevColor, color, color);
            if (index == modules.size() - 1) {
               renderer.quad(x - 2.0D - (double)(Integer)this.outlineWidth.get(), y + textHeight + 2.0D, textLength + 4.0D + (double)((Integer)this.outlineWidth.get() * 2), (double)(Integer)this.outlineWidth.get(), this.prevColor, this.prevColor, color, color);
            }
         } else if (index == modules.size() - 1) {
            renderer.quad(x - 2.0D - (double)(Integer)this.outlineWidth.get(), y, (double)(Integer)this.outlineWidth.get(), textHeight + 2.0D + (double)(Integer)this.outlineWidth.get(), this.prevColor, this.prevColor, color, color);
            renderer.quad(x + textLength + 2.0D, y, (double)(Integer)this.outlineWidth.get(), textHeight + 2.0D + (double)(Integer)this.outlineWidth.get(), this.prevColor, this.prevColor, color, color);
            renderer.quad(x - 2.0D - (double)(Integer)this.outlineWidth.get(), y + textHeight + 2.0D, textLength + 4.0D + (double)((Integer)this.outlineWidth.get() * 2), (double)(Integer)this.outlineWidth.get(), this.prevColor, this.prevColor, color, color);
         }

         if (index > 0) {
            if (index < modules.size() - 1) {
               renderer.quad(x - 2.0D - (double)(Integer)this.outlineWidth.get(), y, (double)(Integer)this.outlineWidth.get(), textHeight + 2.0D, this.prevColor, this.prevColor, color, color);
               renderer.quad(x + textLength + 2.0D, y, (double)(Integer)this.outlineWidth.get(), textHeight + 2.0D, this.prevColor, this.prevColor, color, color);
            }

            renderer.quad(Math.min(this.prevX, x) - 2.0D - (double)(Integer)this.outlineWidth.get(), Math.max(this.prevX, x) == x ? y : y - (double)(Integer)this.outlineWidth.get(), Math.max(this.prevX, x) - 2.0D - (Math.min(this.prevX, x) - 2.0D - (double)(Integer)this.outlineWidth.get()), (double)(Integer)this.outlineWidth.get(), this.prevColor, this.prevColor, color, color);
            renderer.quad(Math.min(this.prevX + this.prevTextLength, x + textLength) + 2.0D, Math.min(this.prevX + this.prevTextLength, x + textLength) == x + textLength ? y : y - (double)(Integer)this.outlineWidth.get(), Math.max(this.prevX + this.prevTextLength, x + textLength) + 2.0D + (double)(Integer)this.outlineWidth.get() - (Math.min(this.prevX + this.prevTextLength, x + textLength) + 2.0D), (double)(Integer)this.outlineWidth.get(), this.prevColor, this.prevColor, color, color);
         }
      }

      this.prevTextLength = textLength;
      this.prevColor = color;
   }

   private double getModuleWidth(HudRenderer renderer, Module module) {
      double width = renderer.textWidth(module.title, (Boolean)this.shadow.get(), this.getScale());
      if ((Boolean)this.activeInfo.get()) {
         String info = module.getInfoString();
         if (info != null) {
            width += renderer.textWidth(" ", (Boolean)this.shadow.get(), this.getScale()) + renderer.textWidth(info, (Boolean)this.shadow.get(), this.getScale());
         }
      }

      return width;
   }

   private double getScale() {
      return (Boolean)this.customScale.get() ? (Double)this.scale.get() : -1.0D;
   }

   static {
      INFO = new HudElementInfo(Hud.GROUP, "active-modules", "Displays your active modules.", ActiveModulesHud::new);
      WHITE = new Color();
   }

   public static enum Sort {
      Alphabetical,
      Biggest,
      Smallest;

      // $FF: synthetic method
      private static ActiveModulesHud.Sort[] $values() {
         return new ActiveModulesHud.Sort[]{Alphabetical, Biggest, Smallest};
      }
   }

   public static enum ColorMode {
      Flat,
      Random,
      Rainbow;

      // $FF: synthetic method
      private static ActiveModulesHud.ColorMode[] $values() {
         return new ActiveModulesHud.ColorMode[]{Flat, Random, Rainbow};
      }
   }
}
