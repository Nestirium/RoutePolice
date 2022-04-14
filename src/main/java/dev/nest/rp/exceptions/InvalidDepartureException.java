package dev.nest.rp.exceptions;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class InvalidDepartureException extends RoutePoliceException {

    public InvalidDepartureException(EmbedBuilder builder) {
        super(builder);
    }

    public MessageEmbed getEmbed() {
        EmbedBuilder builder = getGenericException();
        builder.setDescription("Departure airport is not in Saudi Arabia.");
        MessageEmbed embed = builder.build();
        builder.clear();
        return embed;
    }

}
