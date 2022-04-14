package dev.nest.rp.exceptions;

import dev.nest.rp.api.VatApi;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;


public class InvalidMandatoryRouteException extends RoutePoliceException {

    private final String mandatories;
    private final String dep;
    private final String dest;
    private final String route;

    public InvalidMandatoryRouteException(EmbedBuilder builder, VatApi.FlightPlan plan, String mandatories) {
        super(builder);
        this.mandatories = mandatories;
        this.dep = plan.departure();
        this.dest = plan.destination();
        this.route = plan.route();
    }

    public InvalidMandatoryRouteException(EmbedBuilder builder, String dep, String dest, String route, String mandatories) {
        super(builder);
        this.mandatories = mandatories;
        this.dep = dep;
        this.dest = dest;
        this.route = route;
    }

    public MessageEmbed getEmbed() {
        EmbedBuilder builder = getGenericException();
        builder.setDescription("Your route from " + dep + " to " + dest + " is invalid.");
        builder.addField("Your Route:", route, false);
        builder.addField("Valid Routes:", mandatories, false);
        MessageEmbed embed = builder.build();
        builder.clear();
        return embed;
    }

}
