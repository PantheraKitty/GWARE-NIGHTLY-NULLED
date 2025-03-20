package meteordevelopment.meteorclient.systems.config;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.renderer.Fonts;
import meteordevelopment.meteorclient.renderer.text.FontFace;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.FontFaceSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.Settings;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.systems.System;
import meteordevelopment.meteorclient.systems.Systems;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.class_2487;
import net.minecraft.class_2499;
import net.minecraft.class_2519;
import net.minecraft.class_2520;

public class Config extends System<Config> {
   public final Settings settings = new Settings();
   private final SettingGroup sgVisual;
   private final SettingGroup sgChat;
   private final SettingGroup sgMisc;
   public final Setting<Boolean> customFont;
   public final Setting<FontFace> font;
   public final Setting<Double> rainbowSpeed;
   public final Setting<Boolean> titleScreenCredits;
   public final Setting<Boolean> titleScreenSplashes;
   public final Setting<Boolean> customWindowTitle;
   public final Setting<String> customWindowTitleText;
   public final Setting<SettingColor> friendColor;
   public final Setting<SettingColor> enemyColor;
   public final Setting<String> prefix;
   public final Setting<Boolean> chatFeedback;
   public final Setting<Boolean> deleteChatFeedback;
   public final Setting<Integer> rotationHoldTicks;
   public final Setting<Boolean> useTeamColor;
   public final Setting<Integer> moduleSearchCount;
   public final Setting<Boolean> moduleAliases;
   public List<String> dontShowAgainPrompts;

   public Config() {
      super("config");
      this.sgVisual = this.settings.createGroup("Visual");
      this.sgChat = this.settings.createGroup("Chat");
      this.sgMisc = this.settings.createGroup("Misc");
      this.customFont = this.sgVisual.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("custom-font")).description("Use a custom font.")).defaultValue(true)).build());
      SettingGroup var10001 = this.sgVisual;
      FontFaceSetting.Builder var10002 = (FontFaceSetting.Builder)((FontFaceSetting.Builder)(new FontFaceSetting.Builder()).name("font")).description("Custom font to use.");
      Setting var10003 = this.customFont;
      Objects.requireNonNull(var10003);
      this.font = var10001.add(((FontFaceSetting.Builder)((FontFaceSetting.Builder)var10002.visible(var10003::get)).onChanged(Fonts::load)).build());
      this.rainbowSpeed = this.sgVisual.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("rainbow-speed")).description("The global rainbow speed.")).defaultValue(0.5D).range(0.0D, 10.0D).sliderMax(5.0D).build());
      this.titleScreenCredits = this.sgVisual.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("title-screen-credits")).description("Show Meteor credits on title screen")).defaultValue(true)).build());
      this.titleScreenSplashes = this.sgVisual.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("title-screen-splashes")).description("Show Meteor splash texts on title screen")).defaultValue(true)).build());
      this.customWindowTitle = this.sgVisual.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("custom-window-title")).description("Show custom text in the window title.")).defaultValue(false)).onModuleActivated((setting) -> {
         MeteorClient.mc.method_24288();
      })).onChanged((value) -> {
         MeteorClient.mc.method_24288();
      })).build());
      var10001 = this.sgVisual;
      StringSetting.Builder var1 = (StringSetting.Builder)((StringSetting.Builder)(new StringSetting.Builder()).name("window-title-text")).description("The text it displays in the window title.");
      var10003 = this.customWindowTitle;
      Objects.requireNonNull(var10003);
      this.customWindowTitleText = var10001.add(((StringSetting.Builder)((StringSetting.Builder)((StringSetting.Builder)var1.visible(var10003::get)).defaultValue("Minecraft {mc_version} - {meteor.name} {meteor.version}")).onChanged((value) -> {
         MeteorClient.mc.method_24288();
      })).build());
      this.friendColor = this.sgVisual.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("friend-color")).description("The color used to show friends.")).defaultValue(new SettingColor(0, 255, 180)).build());
      this.enemyColor = this.sgVisual.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("enemy-color")).description("The color used to show enemy.")).defaultValue(new SettingColor(240, 10, 10)).build());
      this.prefix = this.sgChat.add(((StringSetting.Builder)((StringSetting.Builder)((StringSetting.Builder)(new StringSetting.Builder()).name("prefix")).description("Prefix.")).defaultValue(".")).build());
      this.chatFeedback = this.sgChat.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("chat-feedback")).description("Sends chat feedback when meteor performs certain actions.")).defaultValue(true)).build());
      var10001 = this.sgChat;
      BoolSetting.Builder var2 = (BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("delete-chat-feedback")).description("Delete previous matching chat feedback to keep chat clear.");
      var10003 = this.chatFeedback;
      Objects.requireNonNull(var10003);
      this.deleteChatFeedback = var10001.add(((BoolSetting.Builder)((BoolSetting.Builder)var2.visible(var10003::get)).defaultValue(true)).build());
      this.rotationHoldTicks = this.sgMisc.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("rotation-hold")).description("Hold long to hold server side rotation when not sending any packets.")).defaultValue(4)).build());
      this.useTeamColor = this.sgMisc.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("use-team-color")).description("Uses player's team color for rendering things like esp and tracers.")).defaultValue(true)).build());
      this.moduleSearchCount = this.sgMisc.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("module-search-count")).description("Amount of modules and settings to be shown in the module search bar.")).defaultValue(8)).min(1).sliderMax(12).build());
      this.moduleAliases = this.sgMisc.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("search-module-aliases")).description("Whether or not module aliases will be used in the module search bar.")).defaultValue(true)).build());
      this.dontShowAgainPrompts = new ArrayList();
   }

   public static Config get() {
      return (Config)Systems.get(Config.class);
   }

   public class_2487 toTag() {
      class_2487 tag = new class_2487();
      tag.method_10582("version", MeteorClient.VERSION.toString());
      tag.method_10566("settings", this.settings.toTag());
      tag.method_10566("dontShowAgainPrompts", this.listToTag(this.dontShowAgainPrompts));
      return tag;
   }

   public Config fromTag(class_2487 tag) {
      if (tag.method_10545("settings")) {
         this.settings.fromTag(tag.method_10562("settings"));
      }

      if (tag.method_10545("dontShowAgainPrompts")) {
         this.dontShowAgainPrompts = this.listFromTag(tag, "dontShowAgainPrompts");
      }

      return this;
   }

   private class_2499 listToTag(List<String> list) {
      class_2499 nbt = new class_2499();
      Iterator var3 = list.iterator();

      while(var3.hasNext()) {
         String item = (String)var3.next();
         nbt.add(class_2519.method_23256(item));
      }

      return nbt;
   }

   private List<String> listFromTag(class_2487 tag, String key) {
      List<String> list = new ArrayList();
      Iterator var4 = tag.method_10554(key, 8).iterator();

      while(var4.hasNext()) {
         class_2520 item = (class_2520)var4.next();
         list.add(item.method_10714());
      }

      return list;
   }
}
