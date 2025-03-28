package meteordevelopment.meteorclient.utils.network;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import meteordevelopment.meteorclient.utils.PreInit;

public class MeteorExecutor {
   public static ExecutorService executor;

   private MeteorExecutor() {
   }

   @PreInit
   public static void init() {
      AtomicInteger threadNumber = new AtomicInteger(1);
      executor = Executors.newCachedThreadPool((task) -> {
         Thread thread = new Thread(task);
         thread.setDaemon(true);
         thread.setName("Meteor-Executor-" + threadNumber.getAndIncrement());
         return thread;
      });
   }

   public static void execute(Runnable task) {
      executor.execute(task);
   }
}
