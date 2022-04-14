package dev.nest.rp;

import com.zaxxer.hikari.HikariDataSource;
import dev.nest.rp.api.VatApi;
import dev.nest.rp.api.WxApi;
import dev.nest.rp.cache.RouteCache;
import dev.nest.rp.sql.AuthSource;
import dev.nest.rp.sql.DBManager;
import dev.nest.rp.sql.DataSource;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

public class RoutePolice {

    private static JDA jda;

    public static void main(String[] args) {
        try {
            EmbedBuilder builder = new EmbedBuilder();
            RouteCache routeCache = new RouteCache();
            WxApi wxApi = new WxApi();
            VatApi vatApi = new VatApi(builder);
            AuthSource authSource = new AuthSource();
            DataSource dataSource = new DataSource(authSource);
            HikariDataSource hikariDataSource = new HikariDataSource(dataSource.getHikariConfig());
            DBManager dbManager = new DBManager(hikariDataSource);
            dbManager.createMandatoryTable();
            dbManager.createOptionalTable();
            dbManager.cacheMandatoryRoutes(routeCache);
            dbManager.cacheOptionalRoutes(routeCache);
            RouteValidator routeValidator = new RouteValidator(routeCache, builder, vatApi);
            jda = JDABuilder.createDefault(authSource.getAuthData().BOT_TOKEN())
                    .addEventListeners(new CommandListener(vatApi, wxApi, routeValidator))
                    .build()
                    .awaitReady();
            registerCommands();
            jda.getPresence().setActivity(Activity.watching("OEJD_1_CTR"));
            jda.getPresence().setStatus(OnlineStatus.DO_NOT_DISTURB);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void registerCommands() {
        CommandListUpdateAction commands = jda.updateCommands();
        commands.addCommands(Commands.slash("metar", "Gets the metar of a specified airport.")
                .addOptions(new OptionData(OptionType.STRING, "icao", "The ICAO of the airport.")
                        .setRequired(true))
        ).queue();
        commands.addCommands(Commands.slash("fpl", "Retrieves your most recent filed plan from the vatsim database.")
                .addOptions(new OptionData(OptionType.INTEGER, "cid", "Your VATSIM certificate ID.").setRequired(true))).queue();
        SlashCommandData checkRouteCmd = Commands.slash("checkroute", "Checks if your route is valid according to latest Middle East FIRs.");
        OptionData cid = new OptionData(OptionType.INTEGER, "cid", "VATSIM certificate ID.").setRequired(true);
        checkRouteCmd.addOptions(cid);
        commands.addCommands(checkRouteCmd).queue();
        SlashCommandData validManualCmd = Commands.slash("manvalid", "Performs a manual route validation check.");
        OptionData dep = new OptionData(OptionType.STRING, "dep", "Departure Airport.").setRequired(true);
        OptionData dest = new OptionData(OptionType.STRING, "dest", "Destination Airport.").setRequired(true);
        OptionData route = new OptionData(OptionType.STRING, "route", "Paste your route here for manual validation.").setRequired(true);
        validManualCmd.addOptions(dep, dest, route);
        commands.addCommands(validManualCmd).queue();
        commands.queue();
    }


}
