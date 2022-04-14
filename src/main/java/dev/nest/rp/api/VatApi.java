package dev.nest.rp.api;

import dev.nest.rp.exceptions.InvalidFormatException;
import net.dv8tion.jda.api.EmbedBuilder;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VatApi {

    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM-dd-yyyy @ HH:mm:ss");
    private final StringBuilder builder;
    private final EmbedBuilder embedBuilder;

    public VatApi(EmbedBuilder embedBuilder) {
        builder = new StringBuilder();
        this.embedBuilder = embedBuilder;
    }

    public FlightPlan retrieveLastFPL(int cid) throws InvalidFormatException {
        try {
            final String FLIGHT_PLAN_URL = "https://api.vatsim.net/api/ratings/%s/flight_plans/";
            URL url = new URL(String.format(FLIGHT_PLAN_URL, cid));
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            bufferedReader.lines().forEach(builder::append);
            String rawResult = builder.toString();
            builder.setLength(0);
            builder.trimToSize();
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(rawResult);
            JSONObject jsonObject = (JSONObject) obj;
            JSONArray results = (JSONArray) jsonObject.get("results");
            List<LocalDateTime> timestamps = new ArrayList<>();
            for (Object flightPlans : results) {
                JSONObject flightPlan = (JSONObject) flightPlans;
                String timestamp = (String) flightPlan.get("filed");
                timestamps.add(LocalDateTime.parse(timestamp));
            }
            LocalDateTime mostRecentDate = Collections.max(timestamps);
            timestamps.clear();
            for (Object flightPlans : results) {
                JSONObject flightPlan = (JSONObject) flightPlans;
                String timestamp = (String) flightPlan.get("filed");
                if (!timestamp.equals(mostRecentDate.toString())) {
                    continue;
                }
                return new FlightPlan(
                        Integer.parseInt((String) flightPlan.get("vatsim_id")),
                        (String) flightPlan.get("callsign"),
                        (String) flightPlan.get("dep"),
                        (String) flightPlan.get("arr"),
                        formatAircraft((String) flightPlan.get("aircraft")),
                        (String) flightPlan.get("altitude"),
                        (String) flightPlan.get("flight_type"),
                        formatRoute((String) flightPlan.get("route")),
                        formatTime((String) flightPlan.get("filed"))
                );
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            return null;
        }
        return null;
    }

    private String formatTime(String timestamp) {
        LocalDateTime ldt = LocalDateTime.parse(timestamp);
        return dtf.format(ldt);
    }

    private String formatAircraft(String aircraft) {
        int i = aircraft.indexOf("/");
        if (i == -1) {
            return aircraft;
        }
        return aircraft.substring(0, i);
    }


    public String formatRoute(String route) throws InvalidFormatException {
        String[] parts = StringUtils.normalizeSpace(route.replaceAll("[~!@#$%^&*()_+{}\\[\\]:;,.<>?-]", "").toUpperCase().trim()).split(" ");
        if (parts.length < 2) {
            throw new InvalidFormatException("Route must contain at least 2 waypoints.", embedBuilder);
        }
        for (String part : parts) {
            int i = part.indexOf("/");
            if (i == -1) {
                int digits = 0;
                for (int x = 0; x < part.length(); x++) {
                    if (Character.isDigit(part.charAt(x))) {
                        digits++;
                    }
                }
                if (digits > 5) {
                    continue;
                }
                builder.append(part).append(" ");
                continue;
            }
            String waypoint = part.substring(0, i);
            builder.append(waypoint).append(" ");
        }
        String[] partsFormatted = builder.toString().split(" ");
        builder.setLength(0);
        builder.trimToSize();
        String firstPart = partsFormatted[0];
        String lastPart = partsFormatted[partsFormatted.length - 1];
        Pattern pattern = Pattern.compile(".*\\d.*");
        if (firstPart.length() == 4) {
            Matcher matcher = pattern.matcher(firstPart);
            if (!matcher.find()) {
                partsFormatted = ArrayUtils.remove(partsFormatted, 0);
            }
        }
        if (lastPart.length() == 4) {
            Matcher matcher = pattern.matcher(lastPart);
            if (!matcher.find()) {
                partsFormatted = ArrayUtils.remove(partsFormatted, partsFormatted.length - 1);
            }
        }
        String routedSID = partsFormatted[0];
        String sid = partsFormatted[1];
        if (routedSID.contains(sid)) {
            partsFormatted = ArrayUtils.remove(partsFormatted, 0);
        } else {
            builder.append(sid);
            builder.deleteCharAt(sid.length() - 1);
            String compactSID = builder.toString();
            builder.setLength(0);
            builder.trimToSize();
            if (routedSID.contains(compactSID)) {
                partsFormatted = ArrayUtils.remove(partsFormatted, 0);
            }
        }
        String routedSTAR = partsFormatted[partsFormatted.length - 1];
        String star = partsFormatted[partsFormatted.length - 2];
        if (routedSTAR.contains(star)) {
            partsFormatted = ArrayUtils.remove(partsFormatted, partsFormatted.length - 1);
        } else {
            builder.append(star);
            builder.deleteCharAt(star.length() - 1);
            String compactSTAR = builder.toString();
            builder.setLength(0);
            builder.trimToSize();
            if (routedSTAR.contains(compactSTAR)) {
                partsFormatted = ArrayUtils.remove(partsFormatted, partsFormatted.length - 1);
            }
        }
        return String.join(" ", partsFormatted);
    }

    public record FlightPlan(
            int cid,
            String callsign,
            String departure,
            String destination,
            String aircraft,
            String cruise,
            String flightRule,
            String route,
            String filedTimestamp
    ) {
    }

}
