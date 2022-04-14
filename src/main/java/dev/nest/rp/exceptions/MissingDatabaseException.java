package dev.nest.rp.exceptions;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class MissingDatabaseException extends RoutePoliceException {

    public MissingDatabaseException(EmbedBuilder builder) {
        super(builder);
    }

    public MessageEmbed getEmbed() {
        EmbedBuilder builder = getGenericException();
        builder.setDescription("Failed to find a route for this flight in our database. It should be considered valid unless a route is added.");
        MessageEmbed embed = builder.build();
        builder.clear();
        return embed;
    }

}
