package dev.nest.rp.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.concurrent.ConcurrentMap;

public class RouteCache {

    private final Cache<Integer, Route> mandatoryRoutes;
    private final Cache<Integer, Route> optionalRoutes;

    public RouteCache() {
        mandatoryRoutes = Caffeine.newBuilder().build();
        optionalRoutes = Caffeine.newBuilder().build();
    }

    public void cacheMandatory(int id, Route route) {
        mandatoryRoutes.put(id, route);
    }

    public void cacheOptional(int id, Route route) {
        optionalRoutes.put(id, route);
    }

    public ConcurrentMap<Integer, Route> getMandatoryRoutes() {
        return mandatoryRoutes.asMap();
    }

    public ConcurrentMap<Integer, Route> getOptionalRoutes() {
        return optionalRoutes.asMap();
    }

    public record Route(String dep, String dest, String rvsm, String cnst, String route) {
    }

}
