package dev.nest.rp.exceptions;

import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

public class RoutePoliceException extends Exception {

    private final EmbedBuilder builder;

    public RoutePoliceException(EmbedBuilder builder) {
        this.builder = builder;
    }

    public EmbedBuilder getGenericException() {
        builder.setTitle("Error");
        builder.setColor(Color.RED);
        builder.setFooter("© Nestirium - 2022");
        return builder;
    }

}
