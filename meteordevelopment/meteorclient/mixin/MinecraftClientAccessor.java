package meteordevelopment.meteorclient.mixin;

import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.authlib.yggdrasil.ProfileResult;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import java.net.Proxy;
import java.util.concurrent.CompletableFuture;
import net.minecraft.class_1071;
import net.minecraft.class_310;
import net.minecraft.class_320;
import net.minecraft.class_5520;
import net.minecraft.class_6360;
import net.minecraft.class_7574;
import net.minecraft.class_7853;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({class_310.class})
public interface MinecraftClientAccessor {
   @Accessor("currentFps")
   static int getFps() {
      return 0;
   }

   @Mutable
   @Accessor("session")
   void setSession(class_320 var1);

   @Accessor("networkProxy")
   Proxy getProxy();

   @Accessor("resourceReloadLogger")
   class_6360 getResourceReloadLogger();

   @Invoker("doAttack")
   boolean leftClick();

   @Mutable
   @Accessor("profileKeys")
   void setProfileKeys(class_7853 var1);

   @Accessor("authenticationService")
   YggdrasilAuthenticationService getAuthenticationService();

   @Mutable
   @Accessor
   void setUserApiService(UserApiService var1);

   @Mutable
   @Accessor("sessionService")
   void setSessionService(MinecraftSessionService var1);

   @Mutable
   @Accessor("authenticationService")
   void setAuthenticationService(YggdrasilAuthenticationService var1);

   @Mutable
   @Accessor("skinProvider")
   void setSkinProvider(class_1071 var1);

   @Mutable
   @Accessor("socialInteractionsManager")
   void setSocialInteractionsManager(class_5520 var1);

   @Mutable
   @Accessor("abuseReportContext")
   void setAbuseReportContext(class_7574 var1);

   @Mutable
   @Accessor("gameProfileFuture")
   void setGameProfileFuture(CompletableFuture<ProfileResult> var1);
}
