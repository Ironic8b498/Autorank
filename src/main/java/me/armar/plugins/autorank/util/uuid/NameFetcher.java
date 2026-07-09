package me.armar.plugins.autorank.util.uuid;

import com.google.common.collect.ImmutableList;
import me.armar.plugins.autorank.Autorank;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;

public class NameFetcher implements Callable<Map<UUID, String>> {
    private final JSONParser jsonParser = new JSONParser();
    private final List<UUID> uuids;
    private final Autorank plugin;

    public static String fromStream(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder out = new StringBuilder();
        String newLine = System.lineSeparator();

        String line;
        while((line = reader.readLine()) != null) {
            out.append(line);
            out.append(newLine);
        }

        return out.toString();
    }

    public NameFetcher(List<UUID> uuids, Autorank plugin) {
        this.uuids = ImmutableList.copyOf(uuids);
        this.plugin = plugin;
    }

    public Map<UUID, String> call() throws Exception {
        Map<UUID, String> uuidStringMap = new HashMap<>();

        for(UUID uuid : this.uuids) {
            String var10002 = uuid.toString();
            HttpURLConnection connection = (HttpURLConnection) new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + var10002.replace("-", "")).openConnection();
            String name = null;

            try {
                JSONObject response = (JSONObject)this.jsonParser.parse(new InputStreamReader(connection.getInputStream()));
                name = (String)response.get("name");
            } catch (ParseException var15) {
                String fromStream = fromStream(connection.getInputStream()).replaceAll(" ", "");
                JSONObject response = (JSONObject)this.jsonParser.parse(fromStream);
                name = (String)response.get("name");
                if (name == null) {
                    this.plugin.getLogger().warning("[Autorank] Could not parse uuid '" + uuid + "' to name!");
                    continue;
                }

                String error = (String)response.get("error");
                String errorMessage = (String)response.get("errorMessage");
                if (error != null && !error.isEmpty()) {
                    throw new IllegalStateException(errorMessage);
                }
            } catch (IOException var16) {
                if (var16.getMessage().contains("response code") && var16.getMessage().contains("429")) {
                    this.plugin.getLogger().warning("[Autorank] Sent too many request to the Mojang API server, so couldn't retrieve name of " + uuid);
                    continue;
                }

                var16.printStackTrace();
            } finally {
                if (name == null) {
                    this.plugin.getLogger().warning("[Autorank] Could not find name of account with uuid: '" + uuid + "'");
                }

            }

            uuidStringMap.put(uuid, name);
        }

        return uuidStringMap;
    }
}
