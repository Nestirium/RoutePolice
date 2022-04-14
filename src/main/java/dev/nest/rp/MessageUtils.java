package dev.nest.rp;

import dev.nest.rp.api.VatApi;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;

public class MessageUtils {

    private static void commonFooter(EmbedBuilder builder) {
        builder.setFooter("© Nestirium - 2022");
    }

    public static MessageEmbed err(EmbedBuilder builder, String msg) {
        commonFooter(builder);
        builder.setTitle("Error");
        builder.setColor(Color.RED);
        builder.setDescription(msg);
        MessageEmbed embed = builder.build();
        builder.clear();
        return embed;
    }

    public static MessageEmbed pass(EmbedBuilder builder, String msg) {
        commonFooter(builder);
        builder.setTitle("Result");
        builder.setColor(Color.GREEN);
        builder.setDescription(msg);
        MessageEmbed embed = builder.build();
        builder.clear();
        return embed;
    }

    public static MessageEmbed buildFPLEmbed(EmbedBuilder builder, VatApi.FlightPlan plan) {
        commonFooter(builder);
        builder.setTitle("FPL INFO - " + plan.cid());
        builder.setColor(Color.CYAN);
        builder.addField("Callsign:", plan.callsign(), true);
        builder.addField("Type:", plan.flightRule(), true);
        builder.addField("Aircraft:", plan.aircraft(), true);
        builder.addField("Departure:", plan.departure(), true);
        builder.addField("Destination:", plan.destination(), true);
        builder.addField("Cruise:", plan.cruise(), true);
        builder.addField("File Time (MM-DD-YYYY):", plan.filedTimestamp(), true);
        builder.addField("Route:", plan.route(), false);
        MessageEmbed embed = builder.build();
        builder.clear();
        return embed;
    }

}
