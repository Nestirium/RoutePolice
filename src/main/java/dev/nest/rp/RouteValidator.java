package dev.nest.rp;

import dev.nest.rp.api.VatApi;
import dev.nest.rp.cache.RouteCache;
import dev.nest.rp.exceptions.*;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record RouteValidator(RouteCache routeCache, EmbedBuilder builder, VatApi vatApi) {

    public boolean isSaudiDepAirport(String departure) {
        return departure.startsWith("OE");
    }

    public boolean isDestActiveMENA(String destination) {
        Pattern pattern = Pattern.compile("^OE|^OB|^OT|^OM|^OI|^OK|^OR|^OJ|^HE|^HH|^HS|^HD|^HC|^HJ");
        Matcher matcher = pattern.matcher(destination);
        return matcher.find();
    }

    public void checkRoute(String dep, String dest, String route) throws RoutePoliceException {
        String formattedRoute = vatApi.formatRoute(route);
        if (!isSaudiDepAirport(dep)) {
            throw new InvalidDepartureException(builder);
        }
        if (isDestActiveMENA(dest)) {
            if (!isMandatoryValid(dep, dest, formattedRoute)) {
                throw new InvalidMandatoryRouteException(builder, dep, dest, formattedRoute, formatList(getMandatories(dep, dest)));
            }
            return;
        }
        if (!isOptionalValid(dep, dest, formattedRoute)) {
            throw new InvalidOptionalRouteException(builder, dep, dest, formattedRoute, formatList(getOptionals(dep, dest)), findBestOptional(formattedRoute, getOptionals(dep, dest)));
        }
    }

    public void checkRoute(VatApi.FlightPlan plan) throws RoutePoliceException {
        if (!isSaudiDepAirport(plan.departure())) {
            throw new InvalidDepartureException(builder);
        }
        if (isDestActiveMENA(plan.destination())) {
            if (!isMandatoryValid(plan)) {
                throw new InvalidMandatoryRouteException(builder, plan, formatList(getMandatories(plan)));
            }
            return;
        }
        if (!isOptionalValid(plan)) {
            throw new InvalidOptionalRouteException(builder, plan, formatList(getOptionals(plan)), findBestOptional(plan, getOptionals(plan)));
        }
    }

    private String formatList(List<RouteCache.Route> routeList) {
        StringBuilder builder = new StringBuilder();
        int num = 0;
        for (RouteCache.Route route : routeList) {
            num++;
            builder.append(num).append("- ").append(route.route()).append("\n");
        }
        return builder.toString();
    }


    public List<RouteCache.Route> getMandatories(String dep, String dest) {
        List<RouteCache.Route> routeList = new ArrayList<>();
        for (RouteCache.Route route : routeCache.getMandatoryRoutes().values()) {
            if (route.dep().equals(dep) && route.dest().equals(dest)) {
                routeList.add(route);
            }
        }
        return routeList;
    }

    public List<RouteCache.Route> getMandatories(VatApi.FlightPlan plan) {
        List<RouteCache.Route> routeList = new ArrayList<>();
        for (RouteCache.Route route : routeCache.getMandatoryRoutes().values()) {
            if (route.dep().equals(plan.departure()) && route.dest().equals(plan.destination())) {
                routeList.add(route);
            }
        }
        return routeList;
    }

    public boolean isMandatoryValid(String dep, String dest, String route) throws MissingDatabaseException {
        Iterator<RouteCache.Route> routeIterator = getMandatories(dep, dest).iterator();
        while (routeIterator.hasNext()) {
            RouteCache.Route rte = routeIterator.next();
            if (rte.route().equals(route)) {
                return true;
            }
            if (routeIterator.hasNext()) {
                continue;
            }
            return false;
        }
        throw new MissingDatabaseException(builder);
    }

    public boolean isMandatoryValid(VatApi.FlightPlan plan) throws MissingDatabaseException {
        String originalRoute = plan.route();
        Iterator<RouteCache.Route> routeIterator = getMandatories(plan).iterator();
        while (routeIterator.hasNext()) {
            RouteCache.Route route = routeIterator.next();
            if (route.route().equals(originalRoute)) {
                return true;
            }
            if (routeIterator.hasNext()) {
                continue;
            }
            return false;
        }
        throw new MissingDatabaseException(builder);
    }

    public List<RouteCache.Route> getOptionals(String dep, String dest) {
        List<RouteCache.Route> routeList = new ArrayList<>();
        for (RouteCache.Route route : routeCache.getOptionalRoutes().values()) {
            if (route.dep().equals(dep) && (route.dest().charAt(0) == dest.charAt(0))) {
                routeList.add(route);
            }
        }
        return routeList;
    }

    public List<RouteCache.Route> getOptionals(VatApi.FlightPlan plan) {
        List<RouteCache.Route> routeList = new ArrayList<>();
        for (RouteCache.Route route : routeCache.getOptionalRoutes().values()) {
            if (route.dep().equals(plan.departure()) && route.dest().charAt(0) == plan.destination().charAt(0)) {
                routeList.add(route);
            }
        }
        return routeList;
    }

    public boolean isOptionalValid(String dep, String dest, String route) throws MissingDatabaseException {
        Iterator<RouteCache.Route> routeIterator = getOptionals(dep, dest).iterator();
        while (routeIterator.hasNext()) {
            RouteCache.Route rte = routeIterator.next();
            if (route.startsWith(rte.route())) {
                return true;
            }
            if (routeIterator.hasNext()) {
                continue;
            }
            return false;
        }
        throw new MissingDatabaseException(builder);
    }

    public boolean isOptionalValid(VatApi.FlightPlan plan) throws MissingDatabaseException {
        String originalRoute = plan.route();
        Iterator<RouteCache.Route> routeIterator = getOptionals(plan).iterator();
        while (routeIterator.hasNext()) {
            RouteCache.Route route = routeIterator.next();
            if (originalRoute.startsWith(route.route())) {
                return true;
            }
            if (routeIterator.hasNext()) {
                continue;
            }
            return false;
        }
        throw new MissingDatabaseException(builder);
    }

    public String findBestOptional(VatApi.FlightPlan plan, List<RouteCache.Route> routeList) {
        String originalRoute = plan.route();
        Iterator<RouteCache.Route> routeIterator = routeList.iterator();
        String[] originalRouteWpts = originalRoute.split(" ");
        while (routeIterator.hasNext()) {
            RouteCache.Route route = routeIterator.next();
            String[] waypoints = route.route().split(" ");
            String lastWptOfRoute = waypoints[waypoints.length - 1];
            for (int i = 0; i < originalRouteWpts.length; i++) {
                if (originalRouteWpts[i].equals(lastWptOfRoute)) {
                    if (i == originalRouteWpts.length - 1) {
                        return route.route();
                    }
                    String[] restOfOriginalRteArr = Arrays.copyOfRange(originalRouteWpts, i + 1, originalRouteWpts.length);
                    String restOfPlannedRte = String.join(" ", restOfOriginalRteArr);
                    String boldedRoute = "**" + plan.route() + "**";
                    return boldedRoute + " " + restOfPlannedRte;
                }
            }
        }
        return "No best route found. Can be ignored.";
    }

    public String findBestOptional(String route, List<RouteCache.Route> routeList) {
        Iterator<RouteCache.Route> routeIterator = routeList.iterator();
        String[] originalRouteWpts = route.split(" ");
        while (routeIterator.hasNext()) {
            RouteCache.Route rte = routeIterator.next();
            String[] waypoints = rte.route().split(" ");
            String lastWptOfRoute = waypoints[waypoints.length - 1];
            for (int i = 0; i < originalRouteWpts.length; i++) {
                if (originalRouteWpts[i].equals(lastWptOfRoute)) {
                    if (i == originalRouteWpts.length - 1) {
                        return rte.route();
                    }
                    String[] restOfOriginalRteArr = Arrays.copyOfRange(originalRouteWpts, i + 1, originalRouteWpts.length);
                    String restOfPlannedRte = String.join(" ", restOfOriginalRteArr);
                    String boldedRoute = "**" + rte.route() + "**";
                    return boldedRoute + " " + restOfPlannedRte;
                }
            }
        }
        return "No best route found. Can be ignored.";
    }

}
