package meteordevelopment.meteorclient.systems.modules.render.blockesp;

import java.util.Map;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.WindowScreen;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.BlockDataSetting;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.Settings;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.class_2248;

public class ESPBlockDataScreen extends WindowScreen {
   private final ESPBlockData blockData;
   private final class_2248 block;
   private final BlockDataSetting<ESPBlockData> setting;

   public ESPBlockDataScreen(GuiTheme theme, ESPBlockData blockData, class_2248 block, BlockDataSetting<ESPBlockData> setting) {
      super(theme, "Configure Block");
      this.blockData = blockData;
      this.block = block;
      this.setting = setting;
   }

   public void initWidgets() {
      Settings settings = new Settings();
      SettingGroup sgGeneral = settings.getDefaultGroup();
      SettingGroup sgTracer = settings.createGroup("Tracer");
      sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("shape-mode")).description("How the shape is rendered.")).defaultValue(ShapeMode.Lines)).onModuleActivated((shapeModeSetting) -> {
         shapeModeSetting.set(this.blockData.shapeMode);
      })).onChanged((shapeMode) -> {
         this.blockData.shapeMode = shapeMode;
         this.changed(this.blockData, this.block, this.setting);
      })).build());
      sgGeneral.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("line-color")).description("Color of lines.")).defaultValue(new SettingColor(0, 255, 200)).onModuleActivated((settingColorSetting) -> {
         settingColorSetting.set(this.blockData.lineColor);
      })).onChanged((settingColor) -> {
         this.blockData.lineColor.set((Color)settingColor);
         this.changed(this.blockData, this.block, this.setting);
      })).build());
      sgGeneral.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("side-color")).description("Color of sides.")).defaultValue(new SettingColor(0, 255, 200, 25)).onModuleActivated((settingColorSetting) -> {
         settingColorSetting.set(this.blockData.sideColor);
      })).onChanged((settingColor) -> {
         this.blockData.sideColor.set((Color)settingColor);
         this.changed(this.blockData, this.block, this.setting);
      })).build());
      sgTracer.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("tracer")).description("If tracer line is allowed to this block.")).defaultValue(true)).onModuleActivated((booleanSetting) -> {
         booleanSetting.set(this.blockData.tracer);
      })).onChanged((aBoolean) -> {
         this.blockData.tracer = aBoolean;
         this.changed(this.blockData, this.block, this.setting);
      })).build());
      sgTracer.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("tracer-color")).description("Color of tracer line.")).defaultValue(new SettingColor(0, 255, 200, 125)).onModuleActivated((settingColorSetting) -> {
         settingColorSetting.set(this.blockData.tracerColor);
      })).onChanged((settingColor) -> {
         this.blockData.tracerColor = settingColor;
         this.changed(this.blockData, this.block, this.setting);
      })).build());
      settings.onActivated();
      this.add(this.theme.settings(settings)).expandX();
   }

   private void changed(ESPBlockData blockData, class_2248 block, BlockDataSetting<ESPBlockData> setting) {
      if (!blockData.isChanged() && block != null && setting != null) {
         ((Map)setting.get()).put(block, blockData);
         setting.onChanged();
      }

      blockData.changed();
   }
}
