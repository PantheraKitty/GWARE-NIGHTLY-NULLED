package meteordevelopment.meteorclient.systems.waypoints;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.renderer.GL;
import meteordevelopment.meteorclient.renderer.Renderer2D;
import meteordevelopment.meteorclient.settings.BlockPosSetting;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.ProvidedStringSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.Settings;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.utils.misc.ISerializable;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.Dimension;
import net.minecraft.class_1044;
import net.minecraft.class_2338;
import net.minecraft.class_2487;
import net.minecraft.class_2520;
import net.minecraft.class_4587;

public class Waypoint implements ISerializable<Waypoint> {
   public final Settings settings = new Settings();
   private final SettingGroup sgVisual;
   private final SettingGroup sgPosition;
   public Setting<String> name;
   public Setting<String> icon;
   public Setting<SettingColor> color;
   public Setting<Boolean> visible;
   public Setting<Integer> maxVisible;
   public Setting<Double> scale;
   public Setting<class_2338> pos;
   public Setting<Dimension> dimension;
   public Setting<Boolean> opposite;
   public final UUID uuid;

   private Waypoint() {
      this.sgVisual = this.settings.createGroup("Visual");
      this.sgPosition = this.settings.createGroup("Position");
      this.name = this.sgVisual.add(((StringSetting.Builder)((StringSetting.Builder)((StringSetting.Builder)(new StringSetting.Builder()).name("name")).description("The name of the waypoint.")).defaultValue("Home")).build());
      this.icon = this.sgVisual.add(((ProvidedStringSetting.Builder)((ProvidedStringSetting.Builder)((ProvidedStringSetting.Builder)((ProvidedStringSetting.Builder)(new ProvidedStringSetting.Builder()).name("icon")).description("The icon of the waypoint.")).defaultValue("Square")).supplier(() -> {
         return Waypoints.BUILTIN_ICONS;
      }).onChanged((v) -> {
         this.validateIcon();
      })).build());
      this.color = this.sgVisual.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("color")).description("The color of the waypoint.")).defaultValue(MeteorClient.ADDON.color.toSetting()).build());
      this.visible = this.sgVisual.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("visible")).description("Whether to show the waypoint.")).defaultValue(true)).build());
      this.maxVisible = this.sgVisual.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("max-visible-distance")).description("How far away to render the waypoint.")).defaultValue(5000)).build());
      this.scale = this.sgVisual.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("scale")).description("The scale of the waypoint.")).defaultValue(1.0D).build());
      this.pos = this.sgPosition.add(((BlockPosSetting.Builder)((BlockPosSetting.Builder)((BlockPosSetting.Builder)(new BlockPosSetting.Builder()).name("location")).description("The location of the waypoint.")).defaultValue(class_2338.field_10980)).build());
      this.dimension = this.sgPosition.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("dimension")).description("Which dimension the waypoint is in.")).defaultValue(Dimension.Overworld)).build());
      this.opposite = this.sgPosition.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("opposite-dimension")).description("Whether to show the waypoint in the opposite dimension.")).defaultValue(true)).visible(() -> {
         return this.dimension.get() != Dimension.End;
      })).build());
      this.uuid = UUID.randomUUID();
   }

   public Waypoint(class_2520 tag) {
      this.sgVisual = this.settings.createGroup("Visual");
      this.sgPosition = this.settings.createGroup("Position");
      this.name = this.sgVisual.add(((StringSetting.Builder)((StringSetting.Builder)((StringSetting.Builder)(new StringSetting.Builder()).name("name")).description("The name of the waypoint.")).defaultValue("Home")).build());
      this.icon = this.sgVisual.add(((ProvidedStringSetting.Builder)((ProvidedStringSetting.Builder)((ProvidedStringSetting.Builder)((ProvidedStringSetting.Builder)(new ProvidedStringSetting.Builder()).name("icon")).description("The icon of the waypoint.")).defaultValue("Square")).supplier(() -> {
         return Waypoints.BUILTIN_ICONS;
      }).onChanged((v) -> {
         this.validateIcon();
      })).build());
      this.color = this.sgVisual.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("color")).description("The color of the waypoint.")).defaultValue(MeteorClient.ADDON.color.toSetting()).build());
      this.visible = this.sgVisual.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("visible")).description("Whether to show the waypoint.")).defaultValue(true)).build());
      this.maxVisible = this.sgVisual.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("max-visible-distance")).description("How far away to render the waypoint.")).defaultValue(5000)).build());
      this.scale = this.sgVisual.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("scale")).description("The scale of the waypoint.")).defaultValue(1.0D).build());
      this.pos = this.sgPosition.add(((BlockPosSetting.Builder)((BlockPosSetting.Builder)((BlockPosSetting.Builder)(new BlockPosSetting.Builder()).name("location")).description("The location of the waypoint.")).defaultValue(class_2338.field_10980)).build());
      this.dimension = this.sgPosition.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("dimension")).description("Which dimension the waypoint is in.")).defaultValue(Dimension.Overworld)).build());
      this.opposite = this.sgPosition.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("opposite-dimension")).description("Whether to show the waypoint in the opposite dimension.")).defaultValue(true)).visible(() -> {
         return this.dimension.get() != Dimension.End;
      })).build());
      class_2487 nbt = (class_2487)tag;
      if (nbt.method_25928("uuid")) {
         this.uuid = nbt.method_25926("uuid");
      } else {
         this.uuid = UUID.randomUUID();
      }

      this.fromTag(nbt);
   }

   public void renderIcon(double x, double y, double a, double size) {
      class_1044 texture = (class_1044)Waypoints.get().icons.get(this.icon.get());
      if (texture != null) {
         int preA = ((SettingColor)this.color.get()).a;
         SettingColor var10000 = (SettingColor)this.color.get();
         var10000.a = (int)((double)var10000.a * a);
         GL.bindTexture(texture.method_4624());
         Renderer2D.TEXTURE.begin();
         Renderer2D.TEXTURE.texQuad(x, y, size, size, (Color)this.color.get());
         Renderer2D.TEXTURE.render((class_4587)null);
         ((SettingColor)this.color.get()).a = preA;
      }
   }

   public class_2338 getPos() {
      Dimension dim = (Dimension)this.dimension.get();
      class_2338 pos = (class_2338)this.pos.get();
      Dimension currentDim = PlayerUtils.getDimension();
      if (dim != currentDim && !dim.equals(Dimension.End)) {
         class_2338 var10000;
         switch(dim) {
         case Overworld:
            var10000 = new class_2338(pos.method_10263() / 8, pos.method_10264(), pos.method_10260() / 8);
            break;
         case Nether:
            var10000 = new class_2338(pos.method_10263() * 8, pos.method_10264(), pos.method_10260() * 8);
            break;
         default:
            var10000 = null;
         }

         return var10000;
      } else {
         return (class_2338)this.pos.get();
      }
   }

   private void validateIcon() {
      Map<String, class_1044> icons = Waypoints.get().icons;
      class_1044 texture = (class_1044)icons.get(this.icon.get());
      if (texture == null && !icons.isEmpty()) {
         this.icon.set((String)icons.keySet().iterator().next());
      }

   }

   public class_2487 toTag() {
      class_2487 tag = new class_2487();
      tag.method_25927("uuid", this.uuid);
      tag.method_10566("settings", this.settings.toTag());
      return tag;
   }

   public Waypoint fromTag(class_2487 tag) {
      if (tag.method_10545("settings")) {
         this.settings.fromTag(tag.method_10562("settings"));
      }

      return this;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         Waypoint waypoint = (Waypoint)o;
         return Objects.equals(this.uuid, waypoint.uuid);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hashCode(this.uuid);
   }

   public String toString() {
      return (String)this.name.get();
   }

   public static class Builder {
      private String name = "";
      private String icon = "";
      private class_2338 pos;
      private Dimension dimension;

      public Builder() {
         this.pos = class_2338.field_10980;
         this.dimension = Dimension.Overworld;
      }

      public Waypoint.Builder name(String name) {
         this.name = name;
         return this;
      }

      public Waypoint.Builder icon(String icon) {
         this.icon = icon;
         return this;
      }

      public Waypoint.Builder pos(class_2338 pos) {
         this.pos = pos;
         return this;
      }

      public Waypoint.Builder dimension(Dimension dimension) {
         this.dimension = dimension;
         return this;
      }

      public Waypoint build() {
         Waypoint waypoint = new Waypoint();
         if (!this.name.equals(waypoint.name.getDefaultValue())) {
            waypoint.name.set(this.name);
         }

         if (!this.icon.equals(waypoint.icon.getDefaultValue())) {
            waypoint.icon.set(this.icon);
         }

         if (!this.pos.equals(waypoint.pos.getDefaultValue())) {
            waypoint.pos.set(this.pos);
         }

         if (!this.dimension.equals(waypoint.dimension.getDefaultValue())) {
            waypoint.dimension.set(this.dimension);
         }

         return waypoint;
      }
   }
}
