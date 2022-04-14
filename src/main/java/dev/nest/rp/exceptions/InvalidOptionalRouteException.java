package dev.nest.rp.exceptions;

import dev.nest.rp.api.VatApi;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class InvalidOptionalRouteException extends RoutePoliceException {

    private final String optionals;
    private final String bestRoute;
    private final String dep;
    private final String dest;
    private final String route;

    public InvalidOptionalRouteException(EmbedBuilder builder, VatApi.FlightPlan plan, String optionals, String bestRoute) {
        super(builder);
        this.optionals = optionals;
        this.bestRoute = bestRoute;
        this.dep = plan.departure();
        this.dest = plan.destination();
        this.route = plan.route();
    }

    public InvalidOptionalRouteException(EmbedBuilder builder, String dep, String dest, String route, String optionals, String bestRoute) {
        super(builder);
        this.optionals = optionals;
        this.bestRoute = bestRoute;
        this.dep = dep;
        this.dest = dest;
        this.route = route;
    }

    public MessageEmbed getEmbed() {
        EmbedBuilder builder = getGenericException();
        builder.setDescription("Your route from " + dep + " to " + dest + " is invalid WITHIN the Saudi FIR. You need to modify it as per the recommended route.");
        builder.addField("Your Route:", route, false);
        builder.addField("Valid Routes:", optionals, false);
        builder.addField("Recommended Route:", bestRoute, false);
        MessageEmbed embed = builder.build();
        builder.clear();
        return embed;
    }

}
