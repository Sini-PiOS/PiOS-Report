package fr.pios.report.metier.commands;

import fr.pios.report.client.Main;
import fr.pios.report.metier.TaskReport;
import fr.pios.report.metier.entity.Report;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import me.rayzr522.jsonmessage.JSONMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

/**
 *
 * @author Molzonas
 */
public class OnReport implements TabExecutor{

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            Main.logger.log(Level.WARNING, "La console ne peut pas envoyer de REPORT.");
            return true;
        }
        if(args.length < 2) {
            sender.sendMessage("§4§l[Report]§c Format: /report [JOUEUR] [RAISON]");
            return true;
        }
        Player player = (Player) sender;
        String reported = args[0];
        String[] reasonArray = Arrays.copyOfRange(args, 1, args.length);
        String reason = String.join(" ", reasonArray);
        
        Report r = new Report(0, player.getName(), reported, reason, new Date(), false, false);
        
        if(Main.config.getBoolean("Discord.UseDiscord")) {
            TaskReport task = new TaskReport(player, reported, reason);
            task.runTaskAsynchronously(Main.plugin);
        }
        if(Main.config.getBoolean("Minecraft.UseMinecraft")) {
            sender.getServer().getOnlinePlayers().stream().filter((p) -> (p.hasPermission("pios.report.receive"))).forEachOrdered((p) -> {
                JSONMessage msg = JSONMessage.create("§4§l[Report]§r §3<§e").then(player.getName()).suggestCommand("/msg " + player.getName()).tooltip("Envoie un message à §3" + player.getName())
                        .then("§4 reporte §e").then(reported).suggestCommand("/msg " + reported).tooltip("Envoie un message à §c" + reported + "§r (s'il existe et est connecté)").then("§3>§4 : §c" + reason)
                        .newline().then("§f[§cTP vers Rapporteur§f]§r").runCommand("/tp " + player.getName()).tooltip("§4Attention: exécute immédiatement la commande /tp " + player.getName());
                msg.send(p);
            });
        }
        if(Main.config.getBoolean("Minecraft.SendToConsole")) {
            sender.getServer().getLogger().log(Level.INFO, "REPORT: [{0}] => [{1}] : {2}", 
                    new Object[]{player.getName(), reported, reason});
        }
        Main.db.InsertReport(r);
        sender.sendMessage("§4§l[Report]§c Report envoyé!");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if(args.length == 0) {
            List<String> list = new ArrayList<>();
            sender.getServer().getOnlinePlayers().forEach((p) -> {
                list.add(p.getName());
            });
            return list;
        }
        return new ArrayList<>();
    }
}
