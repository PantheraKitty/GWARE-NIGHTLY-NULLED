package meteordevelopment.orbit.listeners;

public interface IListener {
   void call(Object var1);

   Class<?> getTarget();

   int getPriority();

   /** @deprecated */
   @Deprecated
   boolean isStatic();
}
