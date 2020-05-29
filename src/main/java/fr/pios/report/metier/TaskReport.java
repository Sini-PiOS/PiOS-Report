package fr.pios.report.metier;

import fr.pios.report.client.Main;
import java.util.Date;
import java.util.logging.Level;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Molzonas
 */
public class TaskReport extends BukkitRunnable {
    private final Player sender;
    private final String reported;
    private final String reason;

    public TaskReport(Player sender, String reported, String reason) {
        this.sender = sender;
        this.reported = reported;
        this.reason = reason;
    }
    
    @Override
    public void run() {
        try {
            DiscordHook hook = new DiscordHook(Main.config.getString("Discord.WebHookURL"));
            DiscordHook.EmbedObject eo = new DiscordHook.EmbedObject();
            if(Main.config.getString("Discord.AvatarWebhooked").equals("REPORTER")) {
                eo.setThumbnail("https://cravatar.eu/helmhead/" + sender.getName() + ".png");
            }
            hook.setTts(Main.config.getBoolean("Discord.TextToSpeech"));
            eo.setColor(java.awt.Color.RED);
            eo.addField("Reporteur", sender.getName(), true);
            eo.addField("Reporté", reported, true);
            eo.setTitle("Report (" + Main.dateFormat.format(new Date(System.currentTimeMillis())) + ")");
            eo.addField("Raison", reason, false);
            hook.addEmbed(eo);
            hook.execute();
        } catch (Exception ex) {
            Main.logger.log(Level.SEVERE, "ERREUR: Echec de l''envoi du message par le Hook Discord! [{0}]", ex.getMessage());
        }
    }
}
