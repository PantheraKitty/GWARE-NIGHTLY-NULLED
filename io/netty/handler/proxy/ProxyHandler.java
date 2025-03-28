package io.netty.handler.proxy;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.PendingWriteQueue;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.SocketAddress;
import java.nio.channels.ConnectionPendingException;
import java.util.concurrent.TimeUnit;

public abstract class ProxyHandler extends ChannelDuplexHandler {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(ProxyHandler.class);
   private static final long DEFAULT_CONNECT_TIMEOUT_MILLIS = 10000L;
   static final String AUTH_NONE = "none";
   private final SocketAddress proxyAddress;
   private volatile SocketAddress destinationAddress;
   private volatile long connectTimeoutMillis = 10000L;
   private volatile ChannelHandlerContext ctx;
   private PendingWriteQueue pendingWrites;
   private boolean finished;
   private boolean suppressChannelReadComplete;
   private boolean flushedPrematurely;
   private final ProxyHandler.LazyChannelPromise connectPromise = new ProxyHandler.LazyChannelPromise();
   private Future<?> connectTimeoutFuture;
   private final ChannelFutureListener writeListener = new ChannelFutureListener() {
      public void operationComplete(ChannelFuture future) throws Exception {
         if (!future.isSuccess()) {
            ProxyHandler.this.setConnectFailure(future.cause());
         }

      }
   };

   protected ProxyHandler(SocketAddress proxyAddress) {
      this.proxyAddress = (SocketAddress)ObjectUtil.checkNotNull(proxyAddress, "proxyAddress");
   }

   public abstract String protocol();

   public abstract String authScheme();

   public final <T extends SocketAddress> T proxyAddress() {
      return this.proxyAddress;
   }

   public final <T extends SocketAddress> T destinationAddress() {
      return this.destinationAddress;
   }

   public final boolean isConnected() {
      return this.connectPromise.isSuccess();
   }

   public final Future<Channel> connectFuture() {
      return this.connectPromise;
   }

   public final long connectTimeoutMillis() {
      return this.connectTimeoutMillis;
   }

   public final void setConnectTimeoutMillis(long connectTimeoutMillis) {
      if (connectTimeoutMillis <= 0L) {
         connectTimeoutMillis = 0L;
      }

      this.connectTimeoutMillis = connectTimeoutMillis;
   }

   public final void handlerAdded(ChannelHandlerContext ctx) throws Exception {
      this.ctx = ctx;
      this.addCodec(ctx);
      if (ctx.channel().isActive()) {
         this.sendInitialMessage(ctx);
      }

   }

   protected abstract void addCodec(ChannelHandlerContext var1) throws Exception;

   protected abstract void removeEncoder(ChannelHandlerContext var1) throws Exception;

   protected abstract void removeDecoder(ChannelHandlerContext var1) throws Exception;

   public final void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
      if (this.destinationAddress != null) {
         promise.setFailure(new ConnectionPendingException());
      } else {
         this.destinationAddress = remoteAddress;
         ctx.connect(this.proxyAddress, localAddress, promise);
      }
   }

   public final void channelActive(ChannelHandlerContext ctx) throws Exception {
      this.sendInitialMessage(ctx);
      ctx.fireChannelActive();
   }

   private void sendInitialMessage(ChannelHandlerContext ctx) throws Exception {
      long connectTimeoutMillis = this.connectTimeoutMillis;
      if (connectTimeoutMillis > 0L) {
         this.connectTimeoutFuture = ctx.executor().schedule(new Runnable() {
            public void run() {
               if (!ProxyHandler.this.connectPromise.isDone()) {
                  ProxyHandler.this.setConnectFailure(new ProxyConnectException(ProxyHandler.this.exceptionMessage("timeout")));
               }

            }
         }, connectTimeoutMillis, TimeUnit.MILLISECONDS);
      }

      Object initialMessage = this.newInitialMessage(ctx);
      if (initialMessage != null) {
         this.sendToProxyServer(initialMessage);
      }

      readIfNeeded(ctx);
   }

   protected abstract Object newInitialMessage(ChannelHandlerContext var1) throws Exception;

   protected final void sendToProxyServer(Object msg) {
      this.ctx.writeAndFlush(msg).addListener(this.writeListener);
   }

   public final void channelInactive(ChannelHandlerContext ctx) throws Exception {
      if (this.finished) {
         ctx.fireChannelInactive();
      } else {
         this.setConnectFailure(new ProxyConnectException(this.exceptionMessage("disconnected")));
      }

   }

   public final void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
      if (this.finished) {
         ctx.fireExceptionCaught(cause);
      } else {
         this.setConnectFailure(cause);
      }

   }

   public final void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
      if (this.finished) {
         this.suppressChannelReadComplete = false;
         ctx.fireChannelRead(msg);
      } else {
         this.suppressChannelReadComplete = true;
         Throwable cause = null;

         try {
            boolean done = this.handleResponse(ctx, msg);
            if (done) {
               this.setConnectSuccess();
            }
         } catch (Throwable var8) {
            cause = var8;
         } finally {
            ReferenceCountUtil.release(msg);
            if (cause != null) {
               this.setConnectFailure(cause);
            }

         }
      }

   }

   protected abstract boolean handleResponse(ChannelHandlerContext var1, Object var2) throws Exception;

   private void setConnectSuccess() {
      this.finished = true;
      this.cancelConnectTimeoutFuture();
      if (!this.connectPromise.isDone()) {
         boolean removedCodec = true;
         removedCodec &= this.safeRemoveEncoder();
         this.ctx.fireUserEventTriggered(new ProxyConnectionEvent(this.protocol(), this.authScheme(), this.proxyAddress, this.destinationAddress));
         removedCodec &= this.safeRemoveDecoder();
         if (removedCodec) {
            this.writePendingWrites();
            if (this.flushedPrematurely) {
               this.ctx.flush();
            }

            this.connectPromise.trySuccess(this.ctx.channel());
         } else {
            Exception cause = new ProxyConnectException("failed to remove all codec handlers added by the proxy handler; bug?");
            this.failPendingWritesAndClose(cause);
         }
      }

   }

   private boolean safeRemoveDecoder() {
      try {
         this.removeDecoder(this.ctx);
         return true;
      } catch (Exception var2) {
         logger.warn("Failed to remove proxy decoders:", var2);
         return false;
      }
   }

   private boolean safeRemoveEncoder() {
      try {
         this.removeEncoder(this.ctx);
         return true;
      } catch (Exception var2) {
         logger.warn("Failed to remove proxy encoders:", var2);
         return false;
      }
   }

   private void setConnectFailure(Throwable cause) {
      this.finished = true;
      this.cancelConnectTimeoutFuture();
      if (!this.connectPromise.isDone()) {
         if (!(cause instanceof ProxyConnectException)) {
            cause = new ProxyConnectException(this.exceptionMessage(((Throwable)cause).toString()), (Throwable)cause);
         }

         this.safeRemoveDecoder();
         this.safeRemoveEncoder();
         this.failPendingWritesAndClose((Throwable)cause);
      }

   }

   private void failPendingWritesAndClose(Throwable cause) {
      this.failPendingWrites(cause);
      this.connectPromise.tryFailure(cause);
      this.ctx.fireExceptionCaught(cause);
      this.ctx.close();
   }

   private void cancelConnectTimeoutFuture() {
      if (this.connectTimeoutFuture != null) {
         this.connectTimeoutFuture.cancel(false);
         this.connectTimeoutFuture = null;
      }

   }

   protected final String exceptionMessage(String msg) {
      if (msg == null) {
         msg = "";
      }

      StringBuilder buf = (new StringBuilder(128 + msg.length())).append(this.protocol()).append(", ").append(this.authScheme()).append(", ").append(this.proxyAddress).append(" => ").append(this.destinationAddress);
      if (!msg.isEmpty()) {
         buf.append(", ").append(msg);
      }

      return buf.toString();
   }

   public final void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
      if (this.suppressChannelReadComplete) {
         this.suppressChannelReadComplete = false;
         readIfNeeded(ctx);
      } else {
         ctx.fireChannelReadComplete();
      }

   }

   public final void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
      if (this.finished) {
         this.writePendingWrites();
         ctx.write(msg, promise);
      } else {
         this.addPendingWrite(ctx, msg, promise);
      }

   }

   public final void flush(ChannelHandlerContext ctx) throws Exception {
      if (this.finished) {
         this.writePendingWrites();
         ctx.flush();
      } else {
         this.flushedPrematurely = true;
      }

   }

   private static void readIfNeeded(ChannelHandlerContext ctx) {
      if (!ctx.channel().config().isAutoRead()) {
         ctx.read();
      }

   }

   private void writePendingWrites() {
      if (this.pendingWrites != null) {
         this.pendingWrites.removeAndWriteAll();
         this.pendingWrites = null;
      }

   }

   private void failPendingWrites(Throwable cause) {
      if (this.pendingWrites != null) {
         this.pendingWrites.removeAndFailAll(cause);
         this.pendingWrites = null;
      }

   }

   private void addPendingWrite(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
      PendingWriteQueue pendingWrites = this.pendingWrites;
      if (pendingWrites == null) {
         this.pendingWrites = pendingWrites = new PendingWriteQueue(ctx);
      }

      pendingWrites.add(msg, promise);
   }

   private final class LazyChannelPromise extends DefaultPromise<Channel> {
      private LazyChannelPromise() {
      }

      protected EventExecutor executor() {
         if (ProxyHandler.this.ctx == null) {
            throw new IllegalStateException();
         } else {
            return ProxyHandler.this.ctx.executor();
         }
      }

      // $FF: synthetic method
      LazyChannelPromise(Object x1) {
         this();
      }
   }
}
