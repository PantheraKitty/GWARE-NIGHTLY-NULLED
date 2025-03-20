package meteordevelopment.meteorclient.mixin;

import java.util.Objects;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.gui.GuiThemes;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.misc.NameProtect;
import meteordevelopment.meteorclient.systems.proxies.Proxies;
import meteordevelopment.meteorclient.systems.proxies.Proxy;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_437;
import net.minecraft.class_500;
import net.minecraft.class_4185.class_7840;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_500.class})
public abstract class MultiplayerScreenMixin extends class_437 {
   @Unique
   private int textColor1;
   @Unique
   private int textColor2;
   @Unique
   private String loggedInAs;
   @Unique
   private int loggedInAsLength;

   public MultiplayerScreenMixin(class_2561 title) {
      super(title);
   }

   @Inject(
      method = {"init"},
      at = {@At("TAIL")}
   )
   private void onInit(CallbackInfo info) {
      this.textColor1 = Color.fromRGBA(255, 255, 255, 255);
      this.textColor2 = Color.fromRGBA(175, 175, 175, 255);
      this.loggedInAs = "Logged in as ";
      this.loggedInAsLength = this.field_22793.method_1727(this.loggedInAs);
      this.method_37063((new class_7840(class_2561.method_43470("Accounts"), (button) -> {
         this.field_22787.method_1507(GuiThemes.get().accountsScreen());
      })).method_46433(this.field_22789 - 75 - 3, 3).method_46437(75, 20).method_46431());
      this.method_37063((new class_7840(class_2561.method_43470("Proxies"), (button) -> {
         this.field_22787.method_1507(GuiThemes.get().proxiesScreen());
      })).method_46433(this.field_22789 - 75 - 3 - 75 - 2, 3).method_46437(75, 20).method_46431());
   }

   @Inject(
      method = {"render"},
      at = {@At("TAIL")}
   )
   private void onRender(class_332 context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
      int x = 3;
      int y = 3;
      context.method_25303(MeteorClient.mc.field_1772, this.loggedInAs, x, y, this.textColor1);
      context.method_25303(MeteorClient.mc.field_1772, ((NameProtect)Modules.get().get(NameProtect.class)).getName(this.field_22787.method_1548().method_1676()), x + this.loggedInAsLength, y, this.textColor2);
      Objects.requireNonNull(this.field_22793);
      int y = y + 9 + 2;
      Proxy proxy = Proxies.get().getEnabled();
      String left = proxy != null ? "Using proxy " : "Not using a proxy";
      String right = proxy != null ? (proxy.name.get() != null && !((String)proxy.name.get()).isEmpty() ? "(" + (String)proxy.name.get() + ") " : "") + (String)proxy.address.get() + ":" + String.valueOf(proxy.port.get()) : null;
      context.method_25303(MeteorClient.mc.field_1772, left, x, y, this.textColor1);
      if (right != null) {
         context.method_25303(MeteorClient.mc.field_1772, right, x + this.field_22793.method_1727(left), y, this.textColor2);
      }

   }
}
