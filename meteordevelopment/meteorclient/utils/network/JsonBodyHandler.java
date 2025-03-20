package meteordevelopment.meteorclient.utils.network;

import com.google.gson.Gson;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.http.HttpResponse.BodyHandler;
import java.net.http.HttpResponse.BodySubscriber;
import java.net.http.HttpResponse.BodySubscribers;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Flow.Subscription;

public record JsonBodyHandler<T>(BodySubscriber<InputStream> delegate, Gson gson, Type type) implements BodySubscriber<T> {
   public JsonBodyHandler(BodySubscriber<InputStream> delegate, Gson gson, Type type) {
      this.delegate = delegate;
      this.gson = gson;
      this.type = type;
   }

   public static <T> BodyHandler<T> ofJson(Gson gson, Type type) {
      return (responseInfo) -> {
         return new JsonBodyHandler(BodySubscribers.ofInputStream(), gson, type);
      };
   }

   public CompletionStage<T> getBody() {
      return this.delegate.getBody().thenApply((in) -> {
         return in == null ? null : this.gson.fromJson(new InputStreamReader(in), this.type);
      });
   }

   public void onSubscribe(Subscription subscription) {
      this.delegate.onSubscribe(subscription);
   }

   public void onNext(List<ByteBuffer> item) {
      this.delegate.onNext(item);
   }

   public void onError(Throwable throwable) {
      this.delegate.onError(throwable);
   }

   public void onComplete() {
      this.delegate.onComplete();
   }

   public BodySubscriber<InputStream> delegate() {
      return this.delegate;
   }

   public Gson gson() {
      return this.gson;
   }

   public Type type() {
      return this.type;
   }
}
