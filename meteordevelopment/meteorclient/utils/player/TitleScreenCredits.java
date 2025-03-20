package meteordevelopment.meteorclient.utils.player;

import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.addons.AddonManager;
import meteordevelopment.meteorclient.addons.GithubRepo;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.gui.GuiThemes;
import meteordevelopment.meteorclient.gui.screens.CommitsScreen;
import meteordevelopment.meteorclient.mixininterface.IText;
import meteordevelopment.meteorclient.utils.network.Http;
import meteordevelopment.meteorclient.utils.network.MeteorExecutor;
import meteordevelopment.meteorclient.utils.render.MeteorToast;
import net.minecraft.class_124;
import net.minecraft.class_1802;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_5250;

public class TitleScreenCredits {
   private static final List<TitleScreenCredits.Credit> credits = new ArrayList();

   private TitleScreenCredits() {
   }

   private static void init() {
      Iterator var0 = AddonManager.ADDONS.iterator();

      while(var0.hasNext()) {
         MeteorAddon addon = (MeteorAddon)var0.next();
         add(addon);
      }

      credits.sort(Comparator.comparingInt((value) -> {
         return value.addon == MeteorClient.ADDON ? Integer.MIN_VALUE : -MeteorClient.mc.field_1772.method_27525(value.text);
      }));
      MeteorExecutor.execute(() -> {
         Iterator var0 = credits.iterator();

         while(var0.hasNext()) {
            TitleScreenCredits.Credit credit = (TitleScreenCredits.Credit)var0.next();
            if (credit.addon.getRepo() != null && credit.addon.getCommit() != null) {
               GithubRepo repo = credit.addon.getRepo();
               Http.Request request = Http.get("https://api.github.com/repos/%s/branches/%s".formatted(new Object[]{repo.getOwnerName(), repo.branch()}));
               request.exceptionHandler((e) -> {
                  MeteorClient.LOG.error("Could not fetch repository information for addon '%s'.".formatted(new Object[]{credit.addon.name}), e);
               });
               repo.authenticate(request);
               HttpResponse<TitleScreenCredits.Response> res = request.sendJsonResponse(TitleScreenCredits.Response.class);
               switch(res.statusCode()) {
               case 200:
                  if (!credit.addon.getCommit().equals(((TitleScreenCredits.Response)res.body()).commit.sha)) {
                     synchronized(credit.text) {
                        credit.text.method_10852(class_2561.method_43470("*").method_27692(class_124.field_1061));
                        ((IText)credit.text).meteor$invalidateCache();
                     }
                  }
                  break;
               case 401:
                  String message = "Invalid authentication token for repository '%s'".formatted(new Object[]{repo.getOwnerName()});
                  MeteorClient.mc.method_1566().method_1999(new MeteorToast(class_1802.field_8077, "GitHub: Unauthorized", message));
                  MeteorClient.LOG.warn(message);
                  if (System.getenv("meteor.github.authorization") == null) {
                     MeteorClient.LOG.info("Consider setting an authorization token with the 'meteor.github.authorization' environment variable.");
                     MeteorClient.LOG.info("See: https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/managing-your-personal-access-tokens");
                  }
                  break;
               case 403:
                  MeteorClient.LOG.warn("Could not fetch updates for addon '%s': Rate-limited by GitHub.".formatted(new Object[]{credit.addon.name}));
                  break;
               case 404:
                  MeteorClient.LOG.warn("Could not fetch updates for addon '%s': GitHub repository '%s' not found.".formatted(new Object[]{credit.addon.name, repo.getOwnerName()}));
               }
            }
         }

      });
   }

   private static void add(MeteorAddon addon) {
      TitleScreenCredits.Credit credit = new TitleScreenCredits.Credit(addon);
      credit.text.method_10852(class_2561.method_43470(addon.name).method_27694((style) -> {
         return style.method_36139(addon.color.getPacked());
      }));
      credit.text.method_10852(class_2561.method_43470(" by ").method_27692(class_124.field_1080));

      for(int i = 0; i < addon.authors.length; ++i) {
         if (i > 0) {
            credit.text.method_10852(class_2561.method_43470(i == addon.authors.length - 1 ? " & " : ", ").method_27692(class_124.field_1080));
         }

         credit.text.method_10852(class_2561.method_43470(addon.authors[i]).method_27692(class_124.field_1068));
      }

      credits.add(credit);
   }

   public static void render(class_332 context) {
      if (credits.isEmpty()) {
         init();
      }

      int y = 3;

      for(Iterator var2 = credits.iterator(); var2.hasNext(); y += 9 + 2) {
         TitleScreenCredits.Credit credit = (TitleScreenCredits.Credit)var2.next();
         synchronized(credit.text) {
            int x = MeteorClient.mc.field_1755.field_22789 - 3 - MeteorClient.mc.field_1772.method_27525(credit.text);
            context.method_27535(MeteorClient.mc.field_1772, credit.text, x, y, -1);
         }

         Objects.requireNonNull(MeteorClient.mc.field_1772);
      }

   }

   public static boolean onClicked(double mouseX, double mouseY) {
      int y = 3;

      for(Iterator var5 = credits.iterator(); var5.hasNext(); y += 9 + 2) {
         TitleScreenCredits.Credit credit = (TitleScreenCredits.Credit)var5.next();
         int width;
         synchronized(credit.text) {
            width = MeteorClient.mc.field_1772.method_27525(credit.text);
         }

         int x = MeteorClient.mc.field_1755.field_22789 - 3 - width;
         if (mouseX >= (double)x && mouseX <= (double)(x + width) && mouseY >= (double)y) {
            Objects.requireNonNull(MeteorClient.mc.field_1772);
            if (mouseY <= (double)(y + 9 + 2) && credit.addon.getRepo() != null && credit.addon.getCommit() != null) {
               MeteorClient.mc.method_1507(new CommitsScreen(GuiThemes.get(), credit.addon));
               return true;
            }
         }

         Objects.requireNonNull(MeteorClient.mc.field_1772);
      }

      return false;
   }

   private static class Credit {
      public final MeteorAddon addon;
      public final class_5250 text = class_2561.method_43473();

      public Credit(MeteorAddon addon) {
         this.addon = addon;
      }
   }

   private static class Response {
      public TitleScreenCredits.Commit commit;
   }

   private static class Commit {
      public String sha;
   }
}
