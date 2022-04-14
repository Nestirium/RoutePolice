package dev.nest.rp;

import dev.nest.rp.api.VatApi;
import dev.nest.rp.api.WxApi;
import dev.nest.rp.exceptions.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.IOException;


public class CommandListener extends ListenerAdapter {

    private final VatApi vatApi;
    private final EmbedBuilder builder;
    private final WxApi wxApi;
    private final RouteValidator routeValidator;

    public CommandListener(VatApi vatApi, WxApi wxApi, RouteValidator routeValidator) {
        this.vatApi = vatApi;
        this.wxApi = wxApi;
        this.routeValidator = routeValidator;
        builder = new EmbedBuilder();
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.getGuild() == null) {
            return;
        }
        switch (event.getName()) {
            case "metar" -> {
                String icao = event.getOption("icao").getAsString();
                try {
                    String metar = wxApi.getMetar(icao);
                    builder.setTitle((metar == null) ? "Oops" : "METAR - " + icao.toUpperCase());
                    builder.setColor((metar == null) ? Color.RED : Color.GREEN);
                    builder.setDescription((metar == null) ? "Failed to get metar, probably invalid ICAO." : wxApi.getMetar(icao));
                    event.replyEmbeds(builder.build()).queue();
                    builder.clear();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            case "fpl" -> {
                int cid = event.getOption("cid").getAsInt();
                VatApi.FlightPlan plan = null;
                try {
                    plan = vatApi.retrieveLastFPL(cid);
                } catch (InvalidFormatException e) {
                    event.replyEmbeds(e.getEmbed()).queue();
                }
                if (plan == null) {
                    event.replyEmbeds(MessageUtils.err(builder, "Failed to fetch flight plan.")).queue();
                    return;
                }
                event.replyEmbeds(MessageUtils.buildFPLEmbed(builder, plan)).queue();
            }
            case "checkroute" -> {
                int cid = event.getOption("cid").getAsInt();

                VatApi.FlightPlan plan = null;
                try {
                    plan = vatApi.retrieveLastFPL(cid);
                } catch (InvalidFormatException e) {
                    event.replyEmbeds(e.getEmbed()).queue();
                }
                if (plan == null) {
                    event.replyEmbeds(MessageUtils.err(builder, "Failed to check route.")).queue();
                    return;
                }
                try {
                    routeValidator.checkRoute(plan);
                    event.replyEmbeds(MessageUtils.pass(builder, "Your route is fully valid.")).queue();
                } catch (InvalidDepartureException e) {
                    event.replyEmbeds(e.getEmbed()).queue();
                } catch (InvalidMandatoryRouteException e) {
                    event.replyEmbeds(e.getEmbed()).queue();
                } catch (InvalidOptionalRouteException e) {
                    event.replyEmbeds(e.getEmbed()).queue();
                } catch (MissingDatabaseException e) {
                    event.replyEmbeds(e.getEmbed()).queue();
                } catch (RoutePoliceException e) {
                    e.printStackTrace();
                }
            }
            case "manvalid" -> {
                String departure = event.getOption("dep").getAsString();
                String destination = event.getOption("dest").getAsString();
                String route = event.getOption("route").getAsString();
                try {
                    routeValidator.checkRoute(departure, destination, route);
                    event.replyEmbeds(MessageUtils.pass(builder, "Your route is fully valid.")).queue();
                } catch (InvalidDepartureException e) {
                    event.replyEmbeds(e.getEmbed()).queue();
                } catch (InvalidMandatoryRouteException e) {
                    event.replyEmbeds(e.getEmbed()).queue();
                } catch (InvalidOptionalRouteException e) {
                    event.replyEmbeds(e.getEmbed()).queue();
                } catch (MissingDatabaseException e) {
                    event.replyEmbeds(e.getEmbed()).queue();
                } catch (InvalidFormatException e) {
                    event.replyEmbeds(e.getEmbed()).queue();
                } catch (RoutePoliceException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
