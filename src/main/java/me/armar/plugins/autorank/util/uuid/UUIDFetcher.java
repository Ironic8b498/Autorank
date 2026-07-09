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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

public class UUIDFetcher implements Callable<Map<String, UUID>> {
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

    private static UUID getUUID(String id) {
        String var10000 = id.substring(0, 8);
        return UUID.fromString(var10000 + "-" + id.substring(8, 12) + "-" + id.substring(12, 16) + "-" + id.substring(16, 20) + "-" + id.substring(20, 32));
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
        Map<String, UUID> uuidMap = new HashMap<>();
        int requests = (int)Math.ceil((double)this.names.size() / (double)100.0F);

        for(int i = 0; i < requests; ++i) {
            HttpURLConnection connection = createConnection();
            String body = JSONArray.toJSONString(this.names.subList(i * 100, Math.min((i + 1) * 100, this.names.size())));
            writeBody(connection, body);

            JSONArray array;
            try {
                array = (JSONArray)this.jsonParser.parse(new InputStreamReader(connection.getInputStream()));
            } catch (Exception var13) {
                Logger var10000 = Bukkit.getLogger();
                Object var10001 = this.names.get(i);
                var10000.info("[Autorank] Could not fetch UUID of player '" + var10001 + "'!");
                continue;
            }

            for(Object profile : array) {
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
