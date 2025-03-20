package meteordevelopment.meteorclient.settings;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.class_2487;

public class EnumSetting<T extends Enum<?>> extends Setting<T> {
   private final T[] values;
   private final List<String> suggestions;

   public EnumSetting(String name, String description, T defaultValue, Consumer<T> onChanged, Consumer<Setting<T>> onModuleActivated, IVisible visible) {
      super(name, description, defaultValue, onChanged, onModuleActivated, visible);
      this.values = (Enum[])defaultValue.getDeclaringClass().getEnumConstants();
      this.suggestions = new ArrayList(this.values.length);
      Enum[] var7 = this.values;
      int var8 = var7.length;

      for(int var9 = 0; var9 < var8; ++var9) {
         T value = var7[var9];
         this.suggestions.add(value.toString());
      }

   }

   protected T parseImpl(String str) {
      Enum[] var2 = this.values;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         T possibleValue = var2[var4];
         if (str.equalsIgnoreCase(possibleValue.toString())) {
            return possibleValue;
         }
      }

      return null;
   }

   protected boolean isValueValid(T value) {
      return true;
   }

   public List<String> getSuggestions() {
      return this.suggestions;
   }

   public class_2487 save(class_2487 tag) {
      tag.method_10582("value", ((Enum)this.get()).toString());
      return tag;
   }

   public T load(class_2487 tag) {
      this.parse(tag.method_10558("value"));
      return (Enum)this.get();
   }

   public static class Builder<T extends Enum<?>> extends Setting.SettingBuilder<EnumSetting.Builder<T>, T, EnumSetting<T>> {
      public Builder() {
         super((Object)null);
      }

      public EnumSetting<T> build() {
         return new EnumSetting(this.name, this.description, (Enum)this.defaultValue, this.onChanged, this.onModuleActivated, this.visible);
      }
   }
}
