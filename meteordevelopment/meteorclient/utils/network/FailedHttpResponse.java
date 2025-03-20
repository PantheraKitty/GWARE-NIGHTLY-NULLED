package meteordevelopment.meteorclient.utils.network;

import java.net.URI;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient.Version;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import javax.net.ssl.SSLSession;

public record FailedHttpResponse<T>(HttpRequest request, Exception exception) implements HttpResponse<T> {
   public FailedHttpResponse(HttpRequest request, Exception exception) {
      this.request = request;
      this.exception = exception;
   }

   public int statusCode() {
      return 400;
   }

   public Optional<HttpResponse<T>> previousResponse() {
      return Optional.empty();
   }

   public HttpHeaders headers() {
      return HttpHeaders.of(Map.of(), (s1, s2) -> {
         return true;
      });
   }

   public T body() {
      return null;
   }

   public Optional<SSLSession> sslSession() {
      return Optional.empty();
   }

   public URI uri() {
      return this.request.uri();
   }

   @Nullable
   public Version version() {
      return (Version)this.request.version().orElse((Object)null);
   }

   public HttpRequest request() {
      return this.request;
   }

   public Exception exception() {
      return this.exception;
   }
}
