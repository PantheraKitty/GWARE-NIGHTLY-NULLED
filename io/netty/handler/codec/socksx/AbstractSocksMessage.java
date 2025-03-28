package io.netty.handler.codec.socksx;

import io.netty.handler.codec.DecoderResult;
import io.netty.util.internal.ObjectUtil;

public abstract class AbstractSocksMessage implements SocksMessage {
   private DecoderResult decoderResult;

   public AbstractSocksMessage() {
      this.decoderResult = DecoderResult.SUCCESS;
   }

   public DecoderResult decoderResult() {
      return this.decoderResult;
   }

   public void setDecoderResult(DecoderResult decoderResult) {
      this.decoderResult = (DecoderResult)ObjectUtil.checkNotNull(decoderResult, "decoderResult");
   }
}
