package dev.nest.rp.exceptions;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class InvalidFormatException extends RoutePoliceException {

    private final String msg;

    public InvalidFormatException(String msg, EmbedBuilder builder) {
        super(builder);
        this.msg = msg;
    }

    public MessageEmbed getEmbed() {
        EmbedBuilder builder = getGenericException();
        builder.setDescription(msg);
        MessageEmbed embed = builder.build();
        builder.clear();
        return embed;
    }

}
