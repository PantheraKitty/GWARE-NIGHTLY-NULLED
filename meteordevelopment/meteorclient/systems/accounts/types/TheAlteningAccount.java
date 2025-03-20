package meteordevelopment.meteorclient.systems.accounts.types;

import com.mojang.authlib.Environment;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import de.florianmichael.waybackauthlib.InvalidCredentialsException;
import de.florianmichael.waybackauthlib.WaybackAuthLib;
import java.util.Optional;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.mixin.MinecraftClientAccessor;
import meteordevelopment.meteorclient.mixin.YggdrasilMinecraftSessionServiceAccessor;
import meteordevelopment.meteorclient.systems.accounts.Account;
import meteordevelopment.meteorclient.systems.accounts.AccountType;
import meteordevelopment.meteorclient.systems.accounts.TokenAccount;
import meteordevelopment.meteorclient.utils.misc.NbtException;
import net.minecraft.class_2487;
import net.minecraft.class_320;
import net.minecraft.class_320.class_321;
import org.jetbrains.annotations.Nullable;

public class TheAlteningAccount extends Account<TheAlteningAccount> implements TokenAccount {
   private static final Environment ENVIRONMENT = new Environment("http://sessionserver.thealtening.com", "http://authserver.thealtening.com", "The Altening");
   private static final YggdrasilAuthenticationService SERVICE;
   private String token;
   @Nullable
   private WaybackAuthLib auth;

   public TheAlteningAccount(String token) {
      super(AccountType.TheAltening, token);
      this.token = token;
   }

   public boolean fetchInfo() {
      this.auth = this.getAuth();

      try {
         this.auth.logIn();
         this.cache.username = this.auth.getCurrentProfile().getName();
         this.cache.uuid = this.auth.getCurrentProfile().getId().toString();
         this.cache.loadHead();
         return true;
      } catch (InvalidCredentialsException var2) {
         MeteorClient.LOG.error("Invalid TheAltening credentials.");
         return false;
      } catch (Exception var3) {
         MeteorClient.LOG.error("Failed to fetch info for TheAltening account!");
         return false;
      }
   }

   public boolean login() {
      if (this.auth == null) {
         return false;
      } else {
         applyLoginEnvironment(SERVICE, YggdrasilMinecraftSessionServiceAccessor.createYggdrasilMinecraftSessionService(SERVICE.getServicesKeySet(), SERVICE.getProxy(), ENVIRONMENT));

         try {
            setSession(new class_320(this.auth.getCurrentProfile().getName(), this.auth.getCurrentProfile().getId(), this.auth.getAccessToken(), Optional.empty(), Optional.empty(), class_321.field_1988));
            return true;
         } catch (Exception var2) {
            MeteorClient.LOG.error("Failed to login with TheAltening.");
            return false;
         }
      }
   }

   private WaybackAuthLib getAuth() {
      WaybackAuthLib auth = new WaybackAuthLib(ENVIRONMENT.servicesHost());
      auth.setUsername(this.name);
      auth.setPassword("Meteor on Crack!");
      return auth;
   }

   public String getToken() {
      return this.token;
   }

   public class_2487 toTag() {
      class_2487 tag = new class_2487();
      tag.method_10582("type", this.type.name());
      tag.method_10582("name", this.name);
      tag.method_10582("token", this.token);
      tag.method_10566("cache", this.cache.toTag());
      return tag;
   }

   public TheAlteningAccount fromTag(class_2487 tag) {
      if (tag.method_10545("name") && tag.method_10545("cache") && tag.method_10545("token")) {
         this.name = tag.method_10558("name");
         this.token = tag.method_10558("token");
         this.cache.fromTag(tag.method_10562("cache"));
         return this;
      } else {
         throw new NbtException();
      }
   }

   static {
      SERVICE = new YggdrasilAuthenticationService(((MinecraftClientAccessor)MeteorClient.mc).getProxy(), ENVIRONMENT);
   }
}
