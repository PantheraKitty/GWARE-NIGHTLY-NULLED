package meteordevelopment.meteorclient.systems.modules.misc.swarm;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import meteordevelopment.meteorclient.utils.network.MeteorExecutor;
import meteordevelopment.meteorclient.utils.player.ChatUtils;

public class SwarmHost extends Thread {
   private ServerSocket socket;
   private final SwarmConnection[] clientConnections = new SwarmConnection[50];

   public SwarmHost(int port) {
      try {
         this.socket = new ServerSocket(port);
      } catch (IOException var3) {
         this.socket = null;
         ChatUtils.errorPrefix("Swarm", "Couldn't start a server on port %s.", port);
         var3.printStackTrace();
      }

      if (this.socket != null) {
         this.start();
      }

   }

   public void run() {
      ChatUtils.infoPrefix("Swarm", "Listening for incoming connections on port %s.", this.socket.getLocalPort());

      while(!this.isInterrupted()) {
         try {
            Socket connection = this.socket.accept();
            this.assignConnectionToSubServer(connection);
         } catch (IOException var2) {
            ChatUtils.errorPrefix("Swarm", "Error making a connection to worker.");
            var2.printStackTrace();
         }
      }

   }

   public void assignConnectionToSubServer(Socket connection) {
      for(int i = 0; i < this.clientConnections.length; ++i) {
         if (this.clientConnections[i] == null) {
            this.clientConnections[i] = new SwarmConnection(connection);
            break;
         }
      }

   }

   public void disconnect() {
      SwarmConnection[] var1 = this.clientConnections;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         SwarmConnection connection = var1[var3];
         if (connection != null) {
            connection.disconnect();
         }
      }

      try {
         this.socket.close();
      } catch (IOException var5) {
         var5.printStackTrace();
      }

      ChatUtils.infoPrefix("Swarm", "Server closed on port %s.", this.socket.getLocalPort());
      this.interrupt();
   }

   public void sendMessage(String s) {
      MeteorExecutor.execute(() -> {
         SwarmConnection[] var2 = this.clientConnections;
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            SwarmConnection connection = var2[var4];
            if (connection != null) {
               connection.messageToSend = s;
            }
         }

      });
   }

   public SwarmConnection[] getConnections() {
      return this.clientConnections;
   }

   public int getConnectionCount() {
      int count = 0;
      SwarmConnection[] var2 = this.clientConnections;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         SwarmConnection clientConnection = var2[var4];
         if (clientConnection != null) {
            ++count;
         }
      }

      return count;
   }
}
