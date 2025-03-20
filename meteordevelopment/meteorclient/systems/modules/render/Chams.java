package meteordevelopment.meteorclient.systems.modules.render;

import java.util.Objects;
import java.util.Set;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EntityTypeListSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.render.postprocess.PostProcessShaders;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_2960;

public class Chams extends Module {
   private final SettingGroup sgThroughWalls;
   private final SettingGroup sgPlayers;
   private final SettingGroup sgCrystals;
   private final SettingGroup sgHand;
   public final Setting<Set<class_1299<?>>> entities;
   public final Setting<Chams.Shader> shader;
   public final Setting<SettingColor> shaderColor;
   public final Setting<Boolean> ignoreSelfDepth;
   public final Setting<Boolean> players;
   public final Setting<Boolean> ignoreSelf;
   public final Setting<Boolean> playersTexture;
   public final Setting<SettingColor> playersColor;
   public final Setting<Double> playersScale;
   public final Setting<Boolean> crystals;
   public final Setting<Double> crystalsScale;
   public final Setting<Double> crystalsBounce;
   public final Setting<Double> crystalsRotationSpeed;
   public final Setting<Boolean> crystalsTexture;
   public final Setting<Boolean> renderCore;
   public final Setting<SettingColor> crystalsCoreColor;
   public final Setting<Boolean> renderFrame1;
   public final Setting<SettingColor> crystalsFrame1Color;
   public final Setting<Boolean> renderFrame2;
   public final Setting<SettingColor> crystalsFrame2Color;
   public final Setting<Boolean> hand;
   public final Setting<Boolean> handTexture;
   public final Setting<SettingColor> handColor;
   public static final class_2960 BLANK = MeteorClient.identifier("textures/blank.png");

   public Chams() {
      super(Categories.Render, "chams", "Tweaks rendering of entities.");
      this.sgThroughWalls = this.settings.createGroup("Through Walls");
      this.sgPlayers = this.settings.createGroup("Players");
      this.sgCrystals = this.settings.createGroup("Crystals");
      this.sgHand = this.settings.createGroup("Hand");
      this.entities = this.sgThroughWalls.add(((EntityTypeListSetting.Builder)((EntityTypeListSetting.Builder)(new EntityTypeListSetting.Builder()).name("entities")).description("Select entities to show through walls.")).build());
      this.shader = this.sgThroughWalls.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("shader")).description("Renders a shader over of the entities.")).defaultValue(Chams.Shader.Image)).onModuleActivated((setting) -> {
         this.updateShader((Chams.Shader)setting.get());
      })).onChanged(this::updateShader)).build());
      this.shaderColor = this.sgThroughWalls.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("color")).description("The color that the shader is drawn with.")).defaultValue(new SettingColor(255, 255, 255, 150)).visible(() -> {
         return this.shader.get() != Chams.Shader.None;
      })).build());
      this.ignoreSelfDepth = this.sgThroughWalls.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("ignore-self")).description("Ignores yourself drawing the player.")).defaultValue(true)).build());
      this.players = this.sgPlayers.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("players")).description("Enables model tweaks for players.")).defaultValue(false)).build());
      SettingGroup var10001 = this.sgPlayers;
      BoolSetting.Builder var10002 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("ignore-self")).description("Ignores yourself when tweaking player models.")).defaultValue(false);
      Setting var10003 = this.players;
      Objects.requireNonNull(var10003);
      this.ignoreSelf = var10001.add(((BoolSetting.Builder)var10002.visible(var10003::get)).build());
      var10001 = this.sgPlayers;
      var10002 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("texture")).description("Enables player model textures.")).defaultValue(false);
      var10003 = this.players;
      Objects.requireNonNull(var10003);
      this.playersTexture = var10001.add(((BoolSetting.Builder)var10002.visible(var10003::get)).build());
      var10001 = this.sgPlayers;
      ColorSetting.Builder var1 = ((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("color")).description("The color of player models.")).defaultValue(new SettingColor(198, 135, 254, 150));
      var10003 = this.players;
      Objects.requireNonNull(var10003);
      this.playersColor = var10001.add(((ColorSetting.Builder)var1.visible(var10003::get)).build());
      var10001 = this.sgPlayers;
      DoubleSetting.Builder var2 = ((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("scale")).description("Players scale.")).defaultValue(1.0D).min(0.0D);
      var10003 = this.players;
      Objects.requireNonNull(var10003);
      this.playersScale = var10001.add(((DoubleSetting.Builder)var2.visible(var10003::get)).build());
      this.crystals = this.sgCrystals.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("crystals")).description("Enables model tweaks for end crystals.")).defaultValue(false)).build());
      var10001 = this.sgCrystals;
      var2 = ((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("scale")).description("Crystal scale.")).defaultValue(0.6D).min(0.0D);
      var10003 = this.crystals;
      Objects.requireNonNull(var10003);
      this.crystalsScale = var10001.add(((DoubleSetting.Builder)var2.visible(var10003::get)).build());
      var10001 = this.sgCrystals;
      var2 = ((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("bounce")).description("How high crystals bounce.")).defaultValue(0.6D).min(0.0D);
      var10003 = this.crystals;
      Objects.requireNonNull(var10003);
      this.crystalsBounce = var10001.add(((DoubleSetting.Builder)var2.visible(var10003::get)).build());
      var10001 = this.sgCrystals;
      var2 = ((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("rotation-speed")).description("Multiplies the rotation speed of the crystal.")).defaultValue(0.3D).min(0.0D);
      var10003 = this.crystals;
      Objects.requireNonNull(var10003);
      this.crystalsRotationSpeed = var10001.add(((DoubleSetting.Builder)var2.visible(var10003::get)).build());
      var10001 = this.sgCrystals;
      var10002 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("texture")).description("Whether to render crystal model textures.")).defaultValue(true);
      var10003 = this.crystals;
      Objects.requireNonNull(var10003);
      this.crystalsTexture = var10001.add(((BoolSetting.Builder)var10002.visible(var10003::get)).build());
      var10001 = this.sgCrystals;
      var10002 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("render-core")).description("Enables rendering of the core of the crystal.")).defaultValue(false);
      var10003 = this.crystals;
      Objects.requireNonNull(var10003);
      this.renderCore = var10001.add(((BoolSetting.Builder)var10002.visible(var10003::get)).build());
      this.crystalsCoreColor = this.sgCrystals.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("core-color")).description("The color of the core of the crystal.")).defaultValue(new SettingColor(198, 135, 254, 255)).visible(() -> {
         return (Boolean)this.crystals.get() && (Boolean)this.renderCore.get();
      })).build());
      var10001 = this.sgCrystals;
      var10002 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("render-inner-frame")).description("Enables rendering of the inner frame of the crystal.")).defaultValue(true);
      var10003 = this.crystals;
      Objects.requireNonNull(var10003);
      this.renderFrame1 = var10001.add(((BoolSetting.Builder)var10002.visible(var10003::get)).build());
      this.crystalsFrame1Color = this.sgCrystals.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("inner-frame-color")).description("The color of the inner frame of the crystal.")).defaultValue(new SettingColor(198, 135, 254, 255)).visible(() -> {
         return (Boolean)this.crystals.get() && (Boolean)this.renderFrame1.get();
      })).build());
      var10001 = this.sgCrystals;
      var10002 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("render-outer-frame")).description("Enables rendering of the outer frame of the crystal.")).defaultValue(true);
      var10003 = this.crystals;
      Objects.requireNonNull(var10003);
      this.renderFrame2 = var10001.add(((BoolSetting.Builder)var10002.visible(var10003::get)).build());
      this.crystalsFrame2Color = this.sgCrystals.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("outer-frame-color")).description("The color of the outer frame of the crystal.")).defaultValue(new SettingColor(198, 135, 254, 255)).visible(() -> {
         return (Boolean)this.crystals.get() && (Boolean)this.renderFrame2.get();
      })).build());
      this.hand = this.sgHand.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("enabled")).description("Enables tweaks of hand rendering.")).defaultValue(false)).build());
      var10001 = this.sgHand;
      var10002 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("texture")).description("Whether to render hand textures.")).defaultValue(false);
      var10003 = this.hand;
      Objects.requireNonNull(var10003);
      this.handTexture = var10001.add(((BoolSetting.Builder)var10002.visible(var10003::get)).build());
      var10001 = this.sgHand;
      var1 = ((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("hand-color")).description("The color of your hand.")).defaultValue(new SettingColor(198, 135, 254, 150));
      var10003 = this.hand;
      Objects.requireNonNull(var10003);
      this.handColor = var10001.add(((ColorSetting.Builder)var1.visible(var10003::get)).build());
   }

   public boolean shouldRender(class_1297 entity) {
      return this.isActive() && !this.isShader() && ((Set)this.entities.get()).contains(entity.method_5864()) && (entity != this.mc.field_1724 || (Boolean)this.ignoreSelfDepth.get());
   }

   public boolean isShader() {
      return this.isActive() && this.shader.get() != Chams.Shader.None;
   }

   public void updateShader(Chams.Shader value) {
      if (value != Chams.Shader.None) {
         PostProcessShaders.CHAMS.init(Utils.titleToName(value.name()));
      }
   }

   public static enum Shader {
      Image,
      None;

      // $FF: synthetic method
      private static Chams.Shader[] $values() {
         return new Chams.Shader[]{Image, None};
      }
   }
}
