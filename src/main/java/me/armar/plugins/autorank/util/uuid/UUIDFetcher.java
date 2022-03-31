package me.armar.plugins.autorank.util.uuid;

import com.google.common.collect.ImmutableList;
import org.bukkit.Bukkit;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.Callable;

public class UUIDFetcher implements Callable<Map<String, UUID>> {
    private static final String PROFILE_URL = "https://api.mojang.com/profiles/minecraft";
    private static final double PROFILES_PER_REQUEST = 100.0D;
    private final JSONParser jsonParser;
    private final List<String> names;
    private final boolean rateLimiting;

    private static HttpURLConnection createConnection() throws Exception {
        URL url = new URL("https://api.mojang.com/profiles/minecraft");
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setUseCaches(false);
        connection.setDoInput(true);
        connection.setDoOutput(true);
        return connection;
    }

    public static UUID fromBytes(byte[] array) {
        if (array.length != 16) {
            throw new IllegalArgumentException("Illegal byte array length: " + array.length);
        } else {
            ByteBuffer byteBuffer = ByteBuffer.wrap(array);
            long mostSignificant = byteBuffer.getLong();
            long leastSignificant = byteBuffer.getLong();
            return new UUID(mostSignificant, leastSignificant);
        }
    }

    private static UUID getUUID(String id) {
        return UUID.fromString(id.substring(0, 8) + "-" + id.substring(8, 12) + "-" + id.substring(12, 16) + "-" + id.substring(16, 20) + "-" + id.substring(20, 32));
    }

    public static UUID getUUIDOf(String name) throws Exception {
        return (new UUIDFetcher(Arrays.asList(name))).call().get(name);
    }

    public static byte[] toBytes(UUID uuid) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[16]);
        byteBuffer.putLong(uuid.getMostSignificantBits());
        byteBuffer.putLong(uuid.getLeastSignificantBits());
        return byteBuffer.array();
    }

    private static void writeBody(HttpURLConnection connection, String body) throws Exception {
        OutputStream stream = connection.getOutputStream();
        stream.write(body.getBytes());
        stream.flush();
        stream.close();
    }

    public UUIDFetcher(List<String> names) {
        this(names, true);
    }

    public UUIDFetcher(List<String> names, boolean rateLimiting) {
        this.jsonParser = new JSONParser();
        this.names = ImmutableList.copyOf(names);
        this.rateLimiting = rateLimiting;
    }

    public Map<String, UUID> call() throws Exception {
        Map<String, UUID> uuidMap = new HashMap();
        int requests = (int)Math.ceil((double)this.names.size() / 100.0D);

        for(int i = 0; i < requests; ++i) {
            HttpURLConnection connection = createConnection();
            String body = JSONArray.toJSONString(this.names.subList(i * 100, Math.min((i + 1) * 100, this.names.size())));
            writeBody(connection, body);

            JSONArray array;
            try {
                array = (JSONArray)this.jsonParser.parse(new InputStreamReader(connection.getInputStream()));
            } catch (Exception var13) {
                System.out.print("[Autorank] Could not fetch UUID of player '" + this.names.get(i) + "'!");
                continue;
            }

            Iterator var7 = array.iterator();

            while(var7.hasNext()) {
                Object profile = var7.next();
                JSONObject jsonProfile = (JSONObject)profile;
                String id = (String)jsonProfile.get("id");
                String name = (String)jsonProfile.get("name");
                UUID uuid = getUUID(id);
                uuidMap.put(name, uuid);
            }

            if (this.rateLimiting && i != requests - 1) {
                Bukkit.getServer().getLogger().info("[Autorank] Waiting for 10 minutes");
                Thread.sleep(60000L);
            }
        }

        return uuidMap;
    }
}
