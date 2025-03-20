package meteordevelopment.meteorclient.systems.accounts;

import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.authlib.yggdrasil.ServicesKeyType;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.mixin.FileCacheAccessor;
import meteordevelopment.meteorclient.mixin.MinecraftClientAccessor;
import meteordevelopment.meteorclient.mixin.PlayerSkinProviderAccessor;
import meteordevelopment.meteorclient.utils.misc.ISerializable;
import meteordevelopment.meteorclient.utils.misc.NbtException;
import net.minecraft.class_1071;
import net.minecraft.class_156;
import net.minecraft.class_2487;
import net.minecraft.class_320;
import net.minecraft.class_5520;
import net.minecraft.class_7500;
import net.minecraft.class_7569;
import net.minecraft.class_7574;
import net.minecraft.class_7853;
import net.minecraft.class_1071.class_8687;

public abstract class Account<T extends Account<?>> implements ISerializable<T> {
   protected AccountType type;
   protected String name;
   protected final AccountCache cache;

   protected Account(AccountType type, String name) {
      this.type = type;
      this.name = name;
      this.cache = new AccountCache();
   }

   public abstract boolean fetchInfo();

   public boolean login() {
      YggdrasilAuthenticationService authenticationService = new YggdrasilAuthenticationService(((MinecraftClientAccessor)MeteorClient.mc).getProxy());
      applyLoginEnvironment(authenticationService, authenticationService.createMinecraftSessionService());
      return true;
   }

   public String getUsername() {
      return this.cache.username.isEmpty() ? this.name : this.cache.username;
   }

   public AccountType getType() {
      return this.type;
   }

   public AccountCache getCache() {
      return this.cache;
   }

   public static void setSession(class_320 session) {
      MinecraftClientAccessor mca = (MinecraftClientAccessor)MeteorClient.mc;
      mca.setSession(session);
      UserApiService apiService = mca.getAuthenticationService().createUserApiService(session.method_1674());
      mca.setUserApiService(apiService);
      mca.setSocialInteractionsManager(new class_5520(MeteorClient.mc, apiService));
      mca.setProfileKeys(class_7853.method_46532(apiService, session, MeteorClient.mc.field_1697.toPath()));
      mca.setAbuseReportContext(class_7574.method_44599(class_7569.method_44586(), apiService));
      mca.setGameProfileFuture(CompletableFuture.supplyAsync(() -> {
         return MeteorClient.mc.method_1495().fetchProfile(MeteorClient.mc.method_1548().method_44717(), true);
      }, class_156.method_27958()));
   }

   public static void applyLoginEnvironment(YggdrasilAuthenticationService authService, MinecraftSessionService sessService) {
      MinecraftClientAccessor mca = (MinecraftClientAccessor)MeteorClient.mc;
      mca.setAuthenticationService(authService);
      class_7500.method_44172(authService.getServicesKeySet(), ServicesKeyType.PROFILE_KEY);
      mca.setSessionService(sessService);
      class_8687 skinCache = ((PlayerSkinProviderAccessor)MeteorClient.mc.method_1582()).getSkinCache();
      Path skinCachePath = ((FileCacheAccessor)skinCache).getDirectory();
      mca.setSkinProvider(new class_1071(MeteorClient.mc.method_1531(), skinCachePath, sessService, MeteorClient.mc));
   }

   public class_2487 toTag() {
      class_2487 tag = new class_2487();
      tag.method_10582("type", this.type.name());
      tag.method_10582("name", this.name);
      tag.method_10566("cache", this.cache.toTag());
      return tag;
   }

   public T fromTag(class_2487 tag) {
      if (tag.method_10545("name") && tag.method_10545("cache")) {
         this.name = tag.method_10558("name");
         this.cache.fromTag(tag.method_10562("cache"));
         return this;
      } else {
         throw new NbtException();
      }
   }
}
