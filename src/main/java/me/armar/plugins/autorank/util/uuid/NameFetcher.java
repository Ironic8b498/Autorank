package me.armar.plugins.autorank.util.uuid;

import com.google.common.collect.ImmutableList;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.Callable;

public class NameFetcher implements Callable<Map<UUID, String>> {
    private static final String PROFILE_URL = "https://sessionserver.mojang.com/session/minecraft/profile/";
    private final JSONParser jsonParser = new JSONParser();
    private final List<UUID> uuids;

    public static String fromStream(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder out = new StringBuilder();
        String newLine = System.getProperty("line.separator");

        String line;
        while((line = reader.readLine()) != null) {
            out.append(line);
            out.append(newLine);
        }

        return out.toString();
    }

    public NameFetcher(List<UUID> uuids) {
        this.uuids = ImmutableList.copyOf(uuids);
    }

    public Map<UUID, String> call() throws Exception {
        Map<UUID, String> uuidStringMap = new HashMap();
        Iterator var2 = this.uuids.iterator();

        while(var2.hasNext()) {
            UUID uuid = (UUID)var2.next();
            HttpURLConnection connection = (HttpURLConnection)(new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.toString().replace("-", ""))).openConnection();
            JSONObject response = null;
            String name = null;
            String fromStream = null;

            try {
                response = (JSONObject)this.jsonParser.parse(new InputStreamReader(connection.getInputStream()));
                name = (String)response.get("name");
            } catch (ParseException var15) {
                fromStream = fromStream(connection.getInputStream()).replaceAll(" ", "");
                response = (JSONObject)this.jsonParser.parse(fromStream);
                name = (String)response.get("name");
                if (name == null) {
                    System.out.print("[Autorank] Could not parse uuid '" + uuid + "' to name!");
                    continue;
                }

                String error = (String)response.get("error");
                String errorMessage = (String)response.get("errorMessage");
                if (error != null && error.length() > 0) {
                    throw new IllegalStateException(errorMessage);
                }
            } catch (IOException var16) {
                if (var16.getMessage().contains("response code") && var16.getMessage().contains("429")) {
                    System.out.println("[Autorank] Sent too many request to the Mojang API server, so couldn't retrieve name of " + uuid);
                    continue;
                }

                var16.printStackTrace();
            } finally {
                if (name == null || response == null) {
                    System.out.println("[Autorank] Could not find name of account with uuid: '" + uuid + "'");
                }

            }

            uuidStringMap.put(uuid, name);
        }

        return uuidStringMap;
    }
}
