package meteordevelopment.meteorclient.addons;

import javax.annotation.Nullable;
import meteordevelopment.meteorclient.utils.network.Http;

public record GithubRepo(String owner, String name, String branch, @Nullable String accessToken) {
   public GithubRepo(String owner, String name, @Nullable String accessToken) {
      this(owner, name, "master", accessToken);
   }

   public GithubRepo(String owner, String name) {
      this(owner, name, "master", (String)null);
   }

   public GithubRepo(String owner, String name, String branch, @Nullable String accessToken) {
      this.owner = owner;
      this.name = name;
      this.branch = branch;
      this.accessToken = accessToken;
   }

   public String getOwnerName() {
      return this.owner + "/" + this.name;
   }

   public void authenticate(Http.Request request) {
      if (this.accessToken != null && !this.accessToken.isBlank()) {
         request.bearer(this.accessToken);
      } else {
         String personalAuthToken = System.getenv("meteor.github.authorization");
         if (personalAuthToken != null && !personalAuthToken.isBlank()) {
            request.bearer(personalAuthToken);
         }
      }

   }

   public String owner() {
      return this.owner;
   }

   public String name() {
      return this.name;
   }

   public String branch() {
      return this.branch;
   }

   @Nullable
   public String accessToken() {
      return this.accessToken;
   }
}
