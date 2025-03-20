package meteordevelopment.meteorclient.gui.themes.gonbleware;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.gui.DefaultSettingsWidgetFactory;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.WidgetScreen;
import meteordevelopment.meteorclient.gui.renderer.packer.GuiTexture;
import meteordevelopment.meteorclient.gui.themes.gonbleware.widgets.WGonbleWareAccount;
import meteordevelopment.meteorclient.gui.themes.gonbleware.widgets.WGonbleWareHorizontalSeparator;
import meteordevelopment.meteorclient.gui.themes.gonbleware.widgets.WGonbleWareLabel;
import meteordevelopment.meteorclient.gui.themes.gonbleware.widgets.WGonbleWareModule;
import meteordevelopment.meteorclient.gui.themes.gonbleware.widgets.WGonbleWareMultiLabel;
import meteordevelopment.meteorclient.gui.themes.gonbleware.widgets.WGonbleWareQuad;
import meteordevelopment.meteorclient.gui.themes.gonbleware.widgets.WGonbleWareSection;
import meteordevelopment.meteorclient.gui.themes.gonbleware.widgets.WGonbleWareTooltip;
import meteordevelopment.meteorclient.gui.themes.gonbleware.widgets.WGonbleWareTopBar;
import meteordevelopment.meteorclient.gui.themes.gonbleware.widgets.WGonbleWareVerticalSeparator;
import meteordevelopment.meteorclient.gui.themes.gonbleware.widgets.WGonbleWareView;
import meteordevelopment.meteorclient.gui.themes.gonbleware.widgets.WGonbleWareWindow;
import meteordevelopment.meteorclient.gui.themes.gonbleware.widgets.input.WGbonleWareDropdown;
import meteordevelopment.meteorclient.gui.themes.gonbleware.widgets.input.WGonbleWareSlider;
import meteordevelopment.meteorclient.gui.themes.gonbleware.widgets.input.WGonbleWareTextBox;
import meteordevelopment.meteorclient.gui.themes.gonbleware.widgets.pressable.WGonbleWareButton;
import meteordevelopment.meteorclient.gui.themes.gonbleware.widgets.pressable.WGonbleWareCheckbox;
import meteordevelopment.meteorclient.gui.themes.gonbleware.widgets.pressable.WGonbleWareFavorite;
import meteordevelopment.meteorclient.gui.themes.gonbleware.widgets.pressable.WGonbleWareMinus;
import meteordevelopment.meteorclient.gui.themes.gonbleware.widgets.pressable.WGonbleWarePlus;
import meteordevelopment.meteorclient.gui.themes.gonbleware.widgets.pressable.WGonbleWareTriangle;
import meteordevelopment.meteorclient.gui.utils.AlignmentX;
import meteordevelopment.meteorclient.gui.utils.CharFilter;
import meteordevelopment.meteorclient.gui.widgets.WAccount;
import meteordevelopment.meteorclient.gui.widgets.WHorizontalSeparator;
import meteordevelopment.meteorclient.gui.widgets.WLabel;
import meteordevelopment.meteorclient.gui.widgets.WQuad;
import meteordevelopment.meteorclient.gui.widgets.WTooltip;
import meteordevelopment.meteorclient.gui.widgets.WTopBar;
import meteordevelopment.meteorclient.gui.widgets.WVerticalSeparator;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WSection;
import meteordevelopment.meteorclient.gui.widgets.containers.WView;
import meteordevelopment.meteorclient.gui.widgets.containers.WWindow;
import meteordevelopment.meteorclient.gui.widgets.input.WDropdown;
import meteordevelopment.meteorclient.gui.widgets.input.WSlider;
import meteordevelopment.meteorclient.gui.widgets.input.WTextBox;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.gui.widgets.pressable.WCheckbox;
import meteordevelopment.meteorclient.gui.widgets.pressable.WFavorite;
import meteordevelopment.meteorclient.gui.widgets.pressable.WMinus;
import meteordevelopment.meteorclient.gui.widgets.pressable.WPlus;
import meteordevelopment.meteorclient.gui.widgets.pressable.WTriangle;
import meteordevelopment.meteorclient.renderer.text.TextRenderer;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.accounts.Account;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.class_310;

public class GonbleWareGuiTheme extends GuiTheme {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgColors;
   private final SettingGroup sgTextColors;
   private final SettingGroup sgBackgroundColors;
   private final SettingGroup sgOutline;
   private final SettingGroup sgSeparator;
   private final SettingGroup sgScrollbar;
   private final SettingGroup sgSlider;
   private final SettingGroup sgStarscript;
   public final Setting<Double> scale;
   public final Setting<AlignmentX> moduleAlignment;
   public final Setting<Boolean> categoryIcons;
   public final Setting<Boolean> hideHUD;
   public final Setting<SettingColor> accentColor;
   public final Setting<SettingColor> checkboxColor;
   public final Setting<SettingColor> plusColor;
   public final Setting<SettingColor> minusColor;
   public final Setting<SettingColor> favoriteColor;
   public final Setting<SettingColor> highlightColor;
   public final Setting<SettingColor> textColor;
   public final Setting<SettingColor> textDimColor;
   public final Setting<SettingColor> textHighlightColor;
   public final Setting<SettingColor> textSecondaryColor;
   public final Setting<SettingColor> titleTextColor;
   public final Setting<SettingColor> loggedInColor;
   public final Setting<SettingColor> placeholderColor;
   public final GonbleWareGuiTheme.ThreeStateColorSetting backgroundColor;
   public final Setting<SettingColor> moduleBackground;
   public final GonbleWareGuiTheme.ThreeStateColorSetting outlineColor;
   public final Setting<SettingColor> separatorText;
   public final Setting<SettingColor> separatorCenter;
   public final Setting<SettingColor> separatorEdges;
   public final GonbleWareGuiTheme.ThreeStateColorSetting scrollbarColor;
   public final GonbleWareGuiTheme.ThreeStateColorSetting sliderHandle;
   public final Setting<SettingColor> sliderLeft;
   public final Setting<SettingColor> sliderRight;
   private final Setting<SettingColor> starscriptText;
   private final Setting<SettingColor> starscriptBraces;
   private final Setting<SettingColor> starscriptParenthesis;
   private final Setting<SettingColor> starscriptDots;
   private final Setting<SettingColor> starscriptCommas;
   private final Setting<SettingColor> starscriptOperators;
   private final Setting<SettingColor> starscriptStrings;
   private final Setting<SettingColor> starscriptNumbers;
   private final Setting<SettingColor> starscriptKeywords;
   private final Setting<SettingColor> starscriptAccessedObjects;

   public GonbleWareGuiTheme() {
      super("GonbleWare");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgColors = this.settings.createGroup("Colors");
      this.sgTextColors = this.settings.createGroup("Text");
      this.sgBackgroundColors = this.settings.createGroup("Background");
      this.sgOutline = this.settings.createGroup("Outline");
      this.sgSeparator = this.settings.createGroup("Separator");
      this.sgScrollbar = this.settings.createGroup("Scrollbar");
      this.sgSlider = this.settings.createGroup("Slider");
      this.sgStarscript = this.settings.createGroup("Starscript");
      this.scale = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("scale")).description("Scale of the GUI.")).defaultValue(1.1D).min(0.75D).sliderRange(0.75D, 4.0D).onSliderRelease().onChanged((aDouble) -> {
         if (MeteorClient.mc.field_1755 instanceof WidgetScreen) {
            ((WidgetScreen)MeteorClient.mc.field_1755).invalidate();
         }

      })).build());
      this.moduleAlignment = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("module-alignment")).description("How module titles are aligned.")).defaultValue(AlignmentX.Left)).build());
      this.categoryIcons = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("category-icons")).description("Adds item icons to module categories.")).defaultValue(false)).build());
      this.hideHUD = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("hide-HUD")).description("Hide HUD when in GUI.")).defaultValue(false)).onChanged((v) -> {
         if (MeteorClient.mc.field_1755 instanceof WidgetScreen) {
            MeteorClient.mc.field_1690.field_1842 = v;
         }

      })).build());
      this.accentColor = this.color("accent", "Main color of the GUI.", new SettingColor(200, 165, 255, 50));
      this.checkboxColor = this.color("checkbox", "Color of checkbox.", new SettingColor(115, 65, 225, 250));
      this.plusColor = this.color("plus", "Color of plus button.", new SettingColor(50, 255, 50));
      this.minusColor = this.color("minus", "Color of minus button.", new SettingColor(255, 50, 50));
      this.favoriteColor = this.color("favorite", "Color of checked favorite button.", new SettingColor(250, 215, 0));
      this.highlightColor = this.color(this.sgTextColors, "highlight", "Color of highlighting.", new SettingColor(45, 125, 245, 255));
      this.textColor = this.color(this.sgTextColors, "text", "Color of text.", new SettingColor(255, 255, 255));
      this.textDimColor = this.color(this.sgTextColors, "text-deactive-color", "Color of deactivated text.", new SettingColor(40, 40, 40, 80));
      this.textHighlightColor = this.color(this.sgTextColors, "text-highlight", "Color of text highlighting.", new SettingColor(45, 125, 245, 255));
      this.textSecondaryColor = this.color(this.sgTextColors, "text-secondary-text", "Color of secondary text.", new SettingColor(150, 150, 150));
      this.titleTextColor = this.color(this.sgTextColors, "title-text", "Color of title text.", new SettingColor(255, 255, 255));
      this.loggedInColor = this.color(this.sgTextColors, "logged-in-text", "Color of logged in account name.", new SettingColor(45, 225, 45));
      this.placeholderColor = this.color(this.sgTextColors, "placeholder", "Color of placeholder text.", new SettingColor(255, 255, 255, 20));
      this.backgroundColor = new GonbleWareGuiTheme.ThreeStateColorSetting(this.sgBackgroundColors, "background", new SettingColor(20, 20, 20, 200), new SettingColor(30, 30, 30, 200), new SettingColor(40, 40, 40, 200));
      this.moduleBackground = this.color(this.sgBackgroundColors, "module-background", "Color of module background when active.", new SettingColor(50, 50, 50));
      this.outlineColor = new GonbleWareGuiTheme.ThreeStateColorSetting(this.sgOutline, "outline", new SettingColor(75, 30, 110, 40), new SettingColor(40, 15, 65, 40), new SettingColor(75, 30, 110, 60));
      this.separatorText = this.color(this.sgSeparator, "separator-text", "Color of separator text", new SettingColor(255, 255, 255));
      this.separatorCenter = this.color(this.sgSeparator, "separator-center", "Center color of separators.", new SettingColor(255, 255, 255));
      this.separatorEdges = this.color(this.sgSeparator, "separator-edges", "Color of separator edges.", new SettingColor(225, 225, 225, 150));
      this.scrollbarColor = new GonbleWareGuiTheme.ThreeStateColorSetting(this.sgScrollbar, "Scrollbar", new SettingColor(30, 30, 30, 200), new SettingColor(40, 40, 40, 200), new SettingColor(50, 50, 50, 200));
      this.sliderHandle = new GonbleWareGuiTheme.ThreeStateColorSetting(this.sgSlider, "slider-handle", new SettingColor(100, 0, 255, 250), new SettingColor(110, 5, 255, 250), new SettingColor(130, 10, 255, 250));
      this.sliderLeft = this.color(this.sgSlider, "slider-left", "Color of slider left part.", new SettingColor(100, 45, 170, 140));
      this.sliderRight = this.color(this.sgSlider, "slider-right", "Color of slider right part.", new SettingColor(50, 50, 50));
      this.starscriptText = this.color(this.sgStarscript, "starscript-text", "Color of text in Starscript code.", new SettingColor(169, 183, 198));
      this.starscriptBraces = this.color(this.sgStarscript, "starscript-braces", "Color of braces in Starscript code.", new SettingColor(150, 150, 150));
      this.starscriptParenthesis = this.color(this.sgStarscript, "starscript-parenthesis", "Color of parenthesis in Starscript code.", new SettingColor(169, 183, 198));
      this.starscriptDots = this.color(this.sgStarscript, "starscript-dots", "Color of dots in starscript code.", new SettingColor(169, 183, 198));
      this.starscriptCommas = this.color(this.sgStarscript, "starscript-commas", "Color of commas in starscript code.", new SettingColor(169, 183, 198));
      this.starscriptOperators = this.color(this.sgStarscript, "starscript-operators", "Color of operators in Starscript code.", new SettingColor(169, 183, 198));
      this.starscriptStrings = this.color(this.sgStarscript, "starscript-strings", "Color of strings in Starscript code.", new SettingColor(106, 135, 89));
      this.starscriptNumbers = this.color(this.sgStarscript, "starscript-numbers", "Color of numbers in Starscript code.", new SettingColor(104, 141, 187));
      this.starscriptKeywords = this.color(this.sgStarscript, "starscript-keywords", "Color of keywords in Starscript code.", new SettingColor(204, 120, 50));
      this.starscriptAccessedObjects = this.color(this.sgStarscript, "starscript-accessed-objects", "Color of accessed objects (before a dot) in Starscript code.", new SettingColor(152, 118, 170));
      this.settingsFactory = new DefaultSettingsWidgetFactory(this);
   }

   private Setting<SettingColor> color(SettingGroup group, String name, String description, SettingColor color) {
      return group.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name(name + "-color")).description(description)).defaultValue(color).build());
   }

   private Setting<SettingColor> color(String name, String description, SettingColor color) {
      return this.color(this.sgColors, name, description, color);
   }

   public WWindow window(WWidget icon, String title) {
      return (WWindow)this.w(new WGonbleWareWindow(icon, title));
   }

   public WLabel label(String text, boolean title, double maxWidth) {
      return maxWidth == 0.0D ? (WLabel)this.w(new WGonbleWareLabel(text, title)) : (WLabel)this.w(new WGonbleWareMultiLabel(text, title, maxWidth));
   }

   public WHorizontalSeparator horizontalSeparator(String text) {
      return (WHorizontalSeparator)this.w(new WGonbleWareHorizontalSeparator(text));
   }

   public WVerticalSeparator verticalSeparator() {
      return (WVerticalSeparator)this.w(new WGonbleWareVerticalSeparator());
   }

   protected WButton button(String text, GuiTexture texture) {
      return (WButton)this.w(new WGonbleWareButton(text, texture));
   }

   public WMinus minus() {
      return (WMinus)this.w(new WGonbleWareMinus());
   }

   public WPlus plus() {
      return (WPlus)this.w(new WGonbleWarePlus());
   }

   public WCheckbox checkbox(boolean checked) {
      return (WCheckbox)this.w(new WGonbleWareCheckbox(checked));
   }

   public WSlider slider(double value, double min, double max) {
      return (WSlider)this.w(new WGonbleWareSlider(value, min, max));
   }

   public WTextBox textBox(String text, String placeholder, CharFilter filter, Class<? extends WTextBox.Renderer> renderer) {
      return (WTextBox)this.w(new WGonbleWareTextBox(text, placeholder, filter, renderer));
   }

   public <T> WDropdown<T> dropdown(T[] values, T value) {
      return (WDropdown)this.w(new WGbonleWareDropdown(values, value));
   }

   public WTriangle triangle() {
      return (WTriangle)this.w(new WGonbleWareTriangle());
   }

   public WTooltip tooltip(String text) {
      return (WTooltip)this.w(new WGonbleWareTooltip(text));
   }

   public WView view() {
      return (WView)this.w(new WGonbleWareView());
   }

   public WSection section(String title, boolean expanded, WWidget headerWidget) {
      return (WSection)this.w(new WGonbleWareSection(title, expanded, headerWidget));
   }

   public WAccount account(WidgetScreen screen, Account<?> account) {
      return (WAccount)this.w(new WGonbleWareAccount(screen, account));
   }

   public WWidget module(Module module) {
      return this.w(new WGonbleWareModule(module));
   }

   public WQuad quad(Color color) {
      return (WQuad)this.w(new WGonbleWareQuad(color));
   }

   public WTopBar topBar() {
      return (WTopBar)this.w(new WGonbleWareTopBar());
   }

   public WFavorite favorite(boolean checked) {
      return (WFavorite)this.w(new WGonbleWareFavorite(checked));
   }

   public Color textColor() {
      return (Color)this.textColor.get();
   }

   public Color textSecondaryColor() {
      return (Color)this.textSecondaryColor.get();
   }

   public Color starscriptTextColor() {
      return (Color)this.starscriptText.get();
   }

   public Color starscriptBraceColor() {
      return (Color)this.starscriptBraces.get();
   }

   public Color starscriptParenthesisColor() {
      return (Color)this.starscriptParenthesis.get();
   }

   public Color starscriptDotColor() {
      return (Color)this.starscriptDots.get();
   }

   public Color starscriptCommaColor() {
      return (Color)this.starscriptCommas.get();
   }

   public Color starscriptOperatorColor() {
      return (Color)this.starscriptOperators.get();
   }

   public Color starscriptStringColor() {
      return (Color)this.starscriptStrings.get();
   }

   public Color starscriptNumberColor() {
      return (Color)this.starscriptNumbers.get();
   }

   public Color starscriptKeywordColor() {
      return (Color)this.starscriptKeywords.get();
   }

   public Color starscriptAccessedObjectColor() {
      return (Color)this.starscriptAccessedObjects.get();
   }

   public TextRenderer textRenderer() {
      return TextRenderer.get();
   }

   public double scale(double value) {
      double scaled = value * (Double)this.scale.get();
      if (class_310.field_1703) {
         scaled /= (double)MeteorClient.mc.method_22683().method_4480() / (double)MeteorClient.mc.method_22683().method_4489();
      }

      return scaled;
   }

   public boolean categoryIcons() {
      return (Boolean)this.categoryIcons.get();
   }

   public boolean hideHUD() {
      return (Boolean)this.hideHUD.get();
   }

   public class ThreeStateColorSetting {
      private final Setting<SettingColor> normal;
      private final Setting<SettingColor> hovered;
      private final Setting<SettingColor> pressed;

      public ThreeStateColorSetting(SettingGroup group, String name, SettingColor c1, SettingColor c2, SettingColor c3) {
         this.normal = GonbleWareGuiTheme.this.color(group, name, "Color of " + name + ".", c1);
         this.hovered = GonbleWareGuiTheme.this.color(group, "hovered-" + name, "Color of " + name + " when hovered.", c2);
         this.pressed = GonbleWareGuiTheme.this.color(group, "pressed-" + name, "Color of " + name + " when pressed.", c3);
      }

      public SettingColor get() {
         return (SettingColor)this.normal.get();
      }

      public SettingColor get(boolean pressed, boolean hovered, boolean bypassDisableHoverColor) {
         if (pressed) {
            return (SettingColor)this.pressed.get();
         } else {
            return !hovered || !bypassDisableHoverColor && GonbleWareGuiTheme.this.disableHoverColor ? (SettingColor)this.normal.get() : (SettingColor)this.hovered.get();
         }
      }

      public SettingColor get(boolean pressed, boolean hovered) {
         return this.get(pressed, hovered, false);
      }
   }
}
