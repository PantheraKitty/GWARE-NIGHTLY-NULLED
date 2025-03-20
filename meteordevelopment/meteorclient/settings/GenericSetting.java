package meteordevelopment.meteorclient.settings;

import java.util.function.Consumer;
import meteordevelopment.meteorclient.gui.utils.IScreenFactory;
import meteordevelopment.meteorclient.utils.misc.ICopyable;
import meteordevelopment.meteorclient.utils.misc.ISerializable;
import net.minecraft.class_2487;

public class GenericSetting<T extends ICopyable<T> & ISerializable<T> & IScreenFactory> extends Setting<T> {
   public GenericSetting(String name, String description, T defaultValue, Consumer<T> onChanged, Consumer<Setting<T>> onModuleActivated, IVisible visible) {
      super(name, description, defaultValue, onChanged, onModuleActivated, visible);
   }

   public void resetImpl() {
      if (this.value == null) {
         this.value = ((ICopyable)this.defaultValue).copy();
      }

      ((ICopyable)this.value).set((ICopyable)this.defaultValue);
   }

   protected T parseImpl(String str) {
      return ((ICopyable)this.defaultValue).copy();
   }

   protected boolean isValueValid(T value) {
      return true;
   }

   public class_2487 save(class_2487 tag) {
      tag.method_10566("value", ((ISerializable)((ICopyable)this.get())).toTag());
      return tag;
   }

   public T load(class_2487 tag) {
      ((ISerializable)((ICopyable)this.get())).fromTag(tag.method_10562("value"));
      return (ICopyable)this.get();
   }

   public static class Builder<T extends ICopyable<T> & ISerializable<T> & IScreenFactory> extends Setting.SettingBuilder<GenericSetting.Builder<T>, T, GenericSetting<T>> {
      public Builder() {
         super((Object)null);
      }

      public GenericSetting<T> build() {
         return new GenericSetting(this.name, this.description, (ICopyable)this.defaultValue, this.onChanged, this.onModuleActivated, this.visible);
      }
   }
}
