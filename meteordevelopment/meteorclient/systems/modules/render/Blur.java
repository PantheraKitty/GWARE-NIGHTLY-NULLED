package meteordevelopment.meteorclient.systems.modules.render;

import it.unimi.dsi.fastutil.ints.IntDoubleImmutablePair;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.game.ResolutionChangedEvent;
import meteordevelopment.meteorclient.events.render.RenderAfterWorldEvent;
import meteordevelopment.meteorclient.gui.WidgetScreen;
import meteordevelopment.meteorclient.renderer.Framebuffer;
import meteordevelopment.meteorclient.renderer.GL;
import meteordevelopment.meteorclient.renderer.PostProcessRenderer;
import meteordevelopment.meteorclient.renderer.Shader;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.listeners.ConsumerListener;
import meteordevelopment.orbit.listeners.IListener;
import net.minecraft.class_310;
import net.minecraft.class_408;
import net.minecraft.class_437;
import net.minecraft.class_465;

public class Blur extends Module {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgScreens;
   private final IntDoubleImmutablePair[] strengths;
   private final Setting<Integer> strength;
   private final Setting<Integer> fadeTime;
   private final Setting<Boolean> meteor;
   private final Setting<Boolean> inventories;
   private final Setting<Boolean> chat;
   private final Setting<Boolean> other;
   private Shader shaderDown;
   private Shader shaderUp;
   private Shader shaderPassthrough;
   private final Framebuffer[] fbos;
   private boolean enabled;
   private long fadeEndAt;

   public Blur() {
      super(Categories.Render, "blur", "Blurs background when in GUI screens.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgScreens = this.settings.createGroup("Screens");
      this.strengths = new IntDoubleImmutablePair[]{IntDoubleImmutablePair.of(1, 1.25D), IntDoubleImmutablePair.of(1, 2.25D), IntDoubleImmutablePair.of(2, 2.0D), IntDoubleImmutablePair.of(2, 3.0D), IntDoubleImmutablePair.of(2, 4.25D), IntDoubleImmutablePair.of(3, 2.5D), IntDoubleImmutablePair.of(3, 3.25D), IntDoubleImmutablePair.of(3, 4.25D), IntDoubleImmutablePair.of(3, 5.5D), IntDoubleImmutablePair.of(4, 3.25D), IntDoubleImmutablePair.of(4, 4.0D), IntDoubleImmutablePair.of(4, 5.0D), IntDoubleImmutablePair.of(4, 6.0D), IntDoubleImmutablePair.of(4, 7.25D), IntDoubleImmutablePair.of(4, 8.25D), IntDoubleImmutablePair.of(5, 4.5D), IntDoubleImmutablePair.of(5, 5.25D), IntDoubleImmutablePair.of(5, 6.25D), IntDoubleImmutablePair.of(5, 7.25D), IntDoubleImmutablePair.of(5, 8.5D)};
      this.strength = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("strength")).description("How strong the blur should be.")).defaultValue(5)).min(1).max(20).sliderRange(1, 20).build());
      this.fadeTime = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("fade-time")).description("How long the fade will last in milliseconds.")).defaultValue(100)).min(0).sliderMax(500).build());
      this.meteor = this.sgScreens.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("meteor")).description("Applies blur to Meteor screens.")).defaultValue(true)).build());
      this.inventories = this.sgScreens.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("inventories")).description("Applies blur to inventory screens.")).defaultValue(true)).build());
      this.chat = this.sgScreens.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("chat")).description("Applies blur when in chat.")).defaultValue(false)).build());
      this.other = this.sgScreens.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("other")).description("Applies blur to all other screen types.")).defaultValue(true)).build());
      this.fbos = new Framebuffer[6];
      MeteorClient.EVENT_BUS.subscribe((IListener)(new ConsumerListener(ResolutionChangedEvent.class, (event) -> {
         for(int i = 0; i < this.fbos.length; ++i) {
            if (this.fbos[i] != null) {
               this.fbos[i].resize();
            } else {
               this.fbos[i] = new Framebuffer(1.0D / Math.pow(2.0D, (double)i));
            }
         }

      })));
      MeteorClient.EVENT_BUS.subscribe((IListener)(new ConsumerListener(RenderAfterWorldEvent.class, (event) -> {
         this.onRenderAfterWorld();
      })));
   }

   private void onRenderAfterWorld() {
      boolean shouldRender = this.shouldRender();
      long time = System.currentTimeMillis();
      if (this.enabled) {
         if (!shouldRender) {
            if (this.fadeEndAt == -1L) {
               this.fadeEndAt = System.currentTimeMillis() + (long)(Integer)this.fadeTime.get();
            }

            if (time >= this.fadeEndAt) {
               this.enabled = false;
               this.fadeEndAt = -1L;
            }
         }
      } else if (shouldRender) {
         this.enabled = true;
         this.fadeEndAt = System.currentTimeMillis() + (long)(Integer)this.fadeTime.get();
      }

      if (this.enabled) {
         if (this.shaderDown == null) {
            this.shaderDown = new Shader("blur.vert", "blur_down.frag");
            this.shaderUp = new Shader("blur.vert", "blur_up.frag");
            this.shaderPassthrough = new Shader("passthrough.vert", "passthrough.frag");

            for(int i = 0; i < this.fbos.length; ++i) {
               if (this.fbos[i] == null) {
                  this.fbos[i] = new Framebuffer(1.0D / Math.pow(2.0D, (double)i));
               }
            }
         }

         double progress = 1.0D;
         if (time < this.fadeEndAt) {
            if (shouldRender) {
               progress = 1.0D - (double)(this.fadeEndAt - time) / ((Integer)this.fadeTime.get()).doubleValue();
            } else {
               progress = (double)(this.fadeEndAt - time) / ((Integer)this.fadeTime.get()).doubleValue();
            }
         } else {
            this.fadeEndAt = -1L;
         }

         IntDoubleImmutablePair strength = this.strengths[(int)((double)((Integer)this.strength.get() - 1) * progress)];
         int iterations = strength.leftInt();
         double offset = strength.rightDouble();
         PostProcessRenderer.beginRender();
         this.renderToFbo(this.fbos[0], class_310.method_1551().method_1522().method_30277(), this.shaderDown, offset);

         int i;
         for(i = 0; i < iterations; ++i) {
            this.renderToFbo(this.fbos[i + 1], this.fbos[i].texture, this.shaderDown, offset);
         }

         for(i = iterations; i >= 1; --i) {
            this.renderToFbo(this.fbos[i - 1], this.fbos[i].texture, this.shaderUp, offset);
         }

         class_310.method_1551().method_1522().method_1235(true);
         this.shaderPassthrough.bind();
         GL.bindTexture(this.fbos[0].texture);
         this.shaderPassthrough.set("uTexture", 0);
         PostProcessRenderer.render();
         PostProcessRenderer.endRender();
      }
   }

   private void renderToFbo(Framebuffer targetFbo, int sourceText, Shader shader, double offset) {
      targetFbo.bind();
      targetFbo.setViewport();
      shader.bind();
      GL.bindTexture(sourceText);
      shader.set("uTexture", 0);
      shader.set("uHalfTexelSize", 0.5D / (double)targetFbo.width, 0.5D / (double)targetFbo.height);
      shader.set("uOffset", offset);
      PostProcessRenderer.render();
   }

   private boolean shouldRender() {
      if (!this.isActive()) {
         return false;
      } else {
         class_437 screen = this.mc.field_1755;
         if (screen instanceof WidgetScreen) {
            return (Boolean)this.meteor.get();
         } else if (screen instanceof class_465) {
            return (Boolean)this.inventories.get();
         } else if (screen instanceof class_408) {
            return (Boolean)this.chat.get();
         } else {
            return screen != null ? (Boolean)this.other.get() : false;
         }
      }
   }
}
