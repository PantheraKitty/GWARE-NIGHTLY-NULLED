package meteordevelopment.meteorclient.utils.misc;

import com.mojang.logging.LogUtils;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.misc.TridentDupe;
import org.slf4j.Logger;

public class TridentUtil extends Modules {
   public static final Logger LOG = LogUtils.getLogger();

   public void onInitialize() {
      LOG.info("Initializing Meteor Addon Template");
      Modules.get().add(new TridentDupe());
   }
}
