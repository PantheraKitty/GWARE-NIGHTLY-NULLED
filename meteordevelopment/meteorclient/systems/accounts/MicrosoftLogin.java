package meteordevelopment.meteorclient.systems.accounts;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import meteordevelopment.meteorclient.utils.network.Http;
import meteordevelopment.meteorclient.utils.network.MeteorExecutor;
import net.minecraft.class_156;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

public class MicrosoftLogin {
   private static final String CLIENT_ID = "4673b348-3efa-4f6a-bbb6-34e141cdc638";
   private static final int PORT = 9675;
   private static HttpServer server;
   private static Consumer<String> callback;

   private MicrosoftLogin() {
   }

   public static void getRefreshToken(Consumer<String> callback) {
      MicrosoftLogin.callback = callback;
      startServer();
      class_156.method_668().method_670("https://login.live.com/oauth20_authorize.srf?client_id=4673b348-3efa-4f6a-bbb6-34e141cdc638&response_type=code&redirect_uri=http://127.0.0.1:9675&scope=XboxLive.signin%20offline_access&prompt=select_account");
   }

   public static MicrosoftLogin.LoginData login(String refreshToken) {
      MicrosoftLogin.AuthTokenResponse res = (MicrosoftLogin.AuthTokenResponse)Http.post("https://login.live.com/oauth20_token.srf").bodyForm("client_id=4673b348-3efa-4f6a-bbb6-34e141cdc638&refresh_token=" + refreshToken + "&grant_type=refresh_token&redirect_uri=http://127.0.0.1:9675").sendJson(MicrosoftLogin.AuthTokenResponse.class);
      if (res == null) {
         return new MicrosoftLogin.LoginData();
      } else {
         String accessToken = res.access_token;
         refreshToken = res.refresh_token;
         MicrosoftLogin.XblXstsResponse xblRes = (MicrosoftLogin.XblXstsResponse)Http.post("https://user.auth.xboxlive.com/user/authenticate").bodyJson("{\"Properties\":{\"AuthMethod\":\"RPS\",\"SiteName\":\"user.auth.xboxlive.com\",\"RpsTicket\":\"d=" + accessToken + "\"},\"RelyingParty\":\"http://auth.xboxlive.com\",\"TokenType\":\"JWT\"}").sendJson(MicrosoftLogin.XblXstsResponse.class);
         if (xblRes == null) {
            return new MicrosoftLogin.LoginData();
         } else {
            MicrosoftLogin.XblXstsResponse xstsRes = (MicrosoftLogin.XblXstsResponse)Http.post("https://xsts.auth.xboxlive.com/xsts/authorize").bodyJson("{\"Properties\":{\"SandboxId\":\"RETAIL\",\"UserTokens\":[\"" + xblRes.Token + "\"]},\"RelyingParty\":\"rp://api.minecraftservices.com/\",\"TokenType\":\"JWT\"}").sendJson(MicrosoftLogin.XblXstsResponse.class);
            if (xstsRes == null) {
               return new MicrosoftLogin.LoginData();
            } else {
               MicrosoftLogin.McResponse mcRes = (MicrosoftLogin.McResponse)Http.post("https://api.minecraftservices.com/authentication/login_with_xbox").bodyJson("{\"identityToken\":\"XBL3.0 x=" + xblRes.DisplayClaims.xui[0].uhs + ";" + xstsRes.Token + "\"}").sendJson(MicrosoftLogin.McResponse.class);
               if (mcRes == null) {
                  return new MicrosoftLogin.LoginData();
               } else {
                  MicrosoftLogin.GameOwnershipResponse gameOwnershipRes = (MicrosoftLogin.GameOwnershipResponse)Http.get("https://api.minecraftservices.com/entitlements/mcstore").bearer(mcRes.access_token).sendJson(MicrosoftLogin.GameOwnershipResponse.class);
                  if (gameOwnershipRes != null && gameOwnershipRes.hasGameOwnership()) {
                     MicrosoftLogin.ProfileResponse profileRes = (MicrosoftLogin.ProfileResponse)Http.get("https://api.minecraftservices.com/minecraft/profile").bearer(mcRes.access_token).sendJson(MicrosoftLogin.ProfileResponse.class);
                     return profileRes == null ? new MicrosoftLogin.LoginData() : new MicrosoftLogin.LoginData(mcRes.access_token, refreshToken, profileRes.id, profileRes.name);
                  } else {
                     return new MicrosoftLogin.LoginData();
                  }
               }
            }
         }
      }
   }

   private static void startServer() {
      if (server == null) {
         try {
            server = HttpServer.create(new InetSocketAddress("127.0.0.1", 9675), 0);
            server.createContext("/", new MicrosoftLogin.Handler());
            server.setExecutor(MeteorExecutor.executor);
            server.start();
         } catch (IOException var1) {
            var1.printStackTrace();
         }

      }
   }

   public static void stopServer() {
      if (server != null) {
         server.stop(0);
         server = null;
         callback = null;
      }
   }

   private static class AuthTokenResponse {
      public String access_token;
      public String refresh_token;
   }

   public static class LoginData {
      public String mcToken;
      public String newRefreshToken;
      public String uuid;
      public String username;

      public LoginData() {
      }

      public LoginData(String mcToken, String newRefreshToken, String uuid, String username) {
         this.mcToken = mcToken;
         this.newRefreshToken = newRefreshToken;
         this.uuid = uuid;
         this.username = username;
      }

      public boolean isGood() {
         return this.mcToken != null;
      }
   }

   private static class XblXstsResponse {
      public String Token;
      public MicrosoftLogin.XblXstsResponse.DisplayClaims DisplayClaims;

      private static class DisplayClaims {
         private MicrosoftLogin.XblXstsResponse.DisplayClaims.Claim[] xui;

         private static class Claim {
            private String uhs;
         }
      }
   }

   private static class McResponse {
      public String access_token;
   }

   private static class GameOwnershipResponse {
      private MicrosoftLogin.GameOwnershipResponse.Item[] items;

      private boolean hasGameOwnership() {
         boolean hasProduct = false;
         boolean hasGame = false;
         MicrosoftLogin.GameOwnershipResponse.Item[] var3 = this.items;
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            MicrosoftLogin.GameOwnershipResponse.Item item = var3[var5];
            if (item.name.equals("product_minecraft")) {
               hasProduct = true;
            } else if (item.name.equals("game_minecraft")) {
               hasGame = true;
            }
         }

         return hasProduct && hasGame;
      }

      private static class Item {
         private String name;
      }
   }

   private static class ProfileResponse {
      public String id;
      public String name;
   }

   private static class Handler implements HttpHandler {
      public void handle(HttpExchange req) throws IOException {
         if (req.getRequestMethod().equals("GET")) {
            List<NameValuePair> query = URLEncodedUtils.parse(req.getRequestURI(), StandardCharsets.UTF_8);
            boolean ok = false;
            Iterator var4 = query.iterator();

            while(var4.hasNext()) {
               NameValuePair pair = (NameValuePair)var4.next();
               if (pair.getName().equals("code")) {
                  this.handleCode(pair.getValue());
                  ok = true;
                  break;
               }
            }

            if (!ok) {
               this.writeText(req, "Cannot authenticate.");
               MicrosoftLogin.callback.accept((Object)null);
            } else {
               this.writeText(req, "You may now close this page.");
            }
         }

         MicrosoftLogin.stopServer();
      }

      private void handleCode(String code) {
         MicrosoftLogin.AuthTokenResponse res = (MicrosoftLogin.AuthTokenResponse)Http.post("https://login.live.com/oauth20_token.srf").bodyForm("client_id=4673b348-3efa-4f6a-bbb6-34e141cdc638&code=" + code + "&grant_type=authorization_code&redirect_uri=http://127.0.0.1:9675").sendJson(MicrosoftLogin.AuthTokenResponse.class);
         if (res == null) {
            MicrosoftLogin.callback.accept((Object)null);
         } else {
            MicrosoftLogin.callback.accept(res.refresh_token);
         }

      }

      private void writeText(HttpExchange req, String text) throws IOException {
         OutputStream out = req.getResponseBody();
         req.sendResponseHeaders(200, (long)text.length());
         out.write(text.getBytes(StandardCharsets.UTF_8));
         out.flush();
         out.close();
      }
   }
}
