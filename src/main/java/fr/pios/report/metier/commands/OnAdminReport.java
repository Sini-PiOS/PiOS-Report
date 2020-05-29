package fr.pios.report.metier.commands;

import fr.pios.report.client.Main;
import fr.pios.report.metier.entity.Report;
import java.util.ArrayList;
import java.util.Arrays;
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
public class OnAdminReport implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!sender.hasPermission("pios.report.read")) {
            sender.sendMessage("§4§l[Report]§r§c Vous n'avez pas le droit de lancer cette commande!");
            return true;
        }
        if(args == null || args.length < 1) {
            return onHelpCommand(sender, args);
        }
        switch(args[0].toLowerCase()) {
            case "list": return onListCommand(sender, args);
            case "player": return onPlayerCommand(sender, args);
            case "reported": return onReportedCommand(sender, args);
            case "info": return onInfoCommand(sender, args);
            case "help": return onHelpCommand(sender, args);
            case "set": return onSetCommand(sender, args);
            default: return true;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if(args.length == 1) {
            List<String> list = new ArrayList<>();
            list.add("list");
            list.add("player");
            list.add("info");
            list.add("help");
            list.add("set");
            list.add("reported");
            return list;
        }
        if(args.length == 2) {
            if(args[0].equalsIgnoreCase("player") || args[0].equalsIgnoreCase("reported")) {
                List<String> list = new ArrayList<>();
                sender.getServer().getOnlinePlayers().forEach((p) -> {
                    list.add(p.getName());
                });
                return list;
            }
            if(args[0].equalsIgnoreCase("set")) {
                List<String> list = new ArrayList<>();
                list.add("removed");
                list.add("resolved");
                list.add("reported");
                list.add("help");
                return list;
            }
        }
        
        if((args.length >= 2 && args[0].equalsIgnoreCase("list")) || 
                (args.length >= 3 && (args[0].equalsIgnoreCase("player") || 
                args[0].equalsIgnoreCase("reported")))) {
            List<String> list = new ArrayList<>();
            if(!Arrays.asList(args).contains("-rm")) {
                list.add("-rm");
            }
            if(!Arrays.asList(args).contains("-rs")) {
                list.add("-rs");
            }
            return list;
        }
        return new ArrayList<>();
    }
    
    public boolean onInfoCommand(CommandSender sender, String[] args) {
        if(args.length > 1) {
            int id = 0;
            try {
                id = Integer.parseInt(args[1]);
            } catch (NumberFormatException ex) {
                sender.sendMessage("§4§l[Report]§r§c Argument manquant: ID [sous forme de chiffre(s)]!");
                return true;
            }
            if(id < 0) {
                sender.sendMessage("§4§l[Report]§r§c ID invalide: doit être supérieur ou égal à 0!");
                return true;
            }
            Report report = Main.db.getReportById(id);
            if(report == null) {
                sender.sendMessage("§4§l[Report]§r§c ID invalide: Aucun report trouvé!");
                return true;
            } else {
                if(sender instanceof Player) {
                    JSONMessage msg = JSONMessage.create("§4§l[Report N°" + report.getId() + "]§r§3 Date: " + Main.dateFormat.format(report.getDate()) + "§r").
                            newline().then("§3 De : §9" + report.getSender()).
                            newline().then("§3 Envers : §9" + report.getReported()).
                            newline().then("§3 Raison : §7" + report.getReason());
                    if(report.isRemoved() || report.isResolved()) {
                        msg.newline();
                        if(report.isRemoved()) {
                            msg.then("§4§l (Supprimé) §r");
                        }
                        if(report.isResolved()) {
                            msg.then("§2§l (Résolu) §r");
                        }
                    }
                    boolean hasResolvedPerm = sender.hasPermission("pios.report.set.resolved");
                    boolean hasRemovedPerm = sender.hasPermission("pios.report.set.removed");
                    if(hasResolvedPerm || hasRemovedPerm) {
                        msg.newline();
                        if(hasResolvedPerm) {
                            msg.then("§l§2Déclarer " + (report.isResolved() ? "non-" : " ") + "résolu§r").tooltip("Déclare le présent report §2" + (report.isResolved() ? "non-" : " ") + "résolu§r.").runCommand("/pios-report set resolved " + id);
                        }
                        if(hasResolvedPerm && hasRemovedPerm) {
                            msg.then(" - ");
                        }
                        if(hasResolvedPerm) {
                            msg.then("§l§4" + (report.isRemoved() ? "Restaurer" : "Supprimer") +"§r").tooltip("Déclare le présent report §4" + (report.isRemoved() ? "restauré" : "supprimé") +"§r.").runCommand("/pios-report set removed " + id);
                        }
                    }
                    msg.send((Player)sender);
                } else {
                    Main.logger.log(Level.INFO, "Report n\u00b0{0} venant de {1}", new Object[]{report.getId(), report.getSender()});
                    Main.logger.log(Level.INFO, "Envers {0}. Raison: {1}", new Object[]{report.getReported(), report.getReason()});
                    Main.logger.log(Level.INFO, "R\u00e9solu: {0}, Supprim\u00e9: {1}", new Object[]{report.isResolved() ? "Oui" : "Non", report.isRemoved()? "Oui" : "Non"});
                }
            }
        } else {
            sender.sendMessage("§4§l[Report]§r§c Argument manquant: ID!");
            return true;
        }
        return true;
    }
    
    public boolean onPlayerCommand(CommandSender sender, String[] args) {
        if(!(sender.hasPermission("pios.report.read"))) {
            sender.sendMessage("§4§l[Report]§r§c Vous n'avez pas la permission d'utiliser cete commande!");
            return true; 
        }
        boolean removed = Arrays.asList(args).contains("-rm");
        boolean resolved = Arrays.asList(args).contains("-rs");
        int page = 0;
        if(!(args.length >= 2)) {
            sender.sendMessage("§4§l[Report]§r§c /pios-report player [pseudo] {page} {-rm} {-rs}");
            return true;
        }
        if(args.length > 2) {
            try {
                page = Integer.parseInt(args[2]);
                page--;
            } catch (NumberFormatException ex) {}
        }
        List<Report> reports = Main.db.GetReportByPseudo(args[1], (page*10), 10, removed, resolved);
        //List<Report> reports = Main.db.getAllReports((page)*10, 10, removed, resolved);
        if(reports.isEmpty()) {
            sender.sendMessage("§4§l[Report]§r§c Aucun résultat trouvé.");
        } else {
            if(sender instanceof Player) {
                JSONMessage msg = JSONMessage.create("§4§l[Report]§r§c Reports: §r");
                reports.forEach((r) -> {
                    msg.newline().then(" - §3[" + r.getId() + "]§e " + r.getReported()+ " (" + Main.dateFormat.format(r.getDate()) + ")§r").runCommand("/pios-report info " + r.getId());
                    if(r.isRemoved()) {
                        msg.then(" §4§lRM");
                    }
                    if(r.isResolved()) {
                        msg.then(" §2§lRS");
                    }
                });
                //int rs = (Main.db.getCountReport(removed, resolved)/10)+1;
                int rs = (int)(Math.floor((Main.db.getCountReportByPseudo(args[1], removed, resolved)-1)/10))+1;
                /*if((rs % 10) == 0) {
                    rs--;
                }*/
                    msg.newline();
                    if((page) > 0) {
                        msg.then("§f<<< ").tooltip("Page Précédente").runCommand("/pios-report player " + args[1] + " " + (page) + (removed ? " -rm" : "" ) + (resolved ? " -rs" : "" ));
                    } else {
                        msg.then("§7<<< ");
                    }
                    msg.then("§9" + (page+1) + "§7/§9" + rs + "§r");
                    if((page+2) <= rs) {
                        msg.then(" §f>>>").tooltip("Page Suivante").runCommand("/pios-report player " + args[1] + " " + (page+2) + (removed ? " -rm" : "" ) + (resolved ? " -rs" : "" ));
                    } else {
                        msg.then(" §7>>>");
                    }
                msg.send((Player)sender);
            } else {
                //Main.plugin.getLogger().log(Level.WARNING, "Commande exécutable uniquement depuis un joueur présent sur le serveur.");
            }
        }
        return true;
    }
    
    public boolean onReportedCommand(CommandSender sender, String[] args) {
        if(!(sender.hasPermission("pios.report.read"))) {
            sender.sendMessage("§4§l[Report]§r§c Vous n'avez pas la permission d'utiliser cete commande!");
            return true; 
        }
        boolean removed = Arrays.asList(args).contains("-rm");
        boolean resolved = Arrays.asList(args).contains("-rs");
        int page = 0;
        if(!(args.length >= 2)) {
            sender.sendMessage("§4§l[Report]§r§c /pios-report reported [pseudo] {page} {-rm} {-rs}");
            return true;
        }
        if(args.length > 2) {
            try {
                page = Integer.parseInt(args[2]);
                page--;
            } catch (NumberFormatException ex) {}
        }
        List<Report> reports = Main.db.GetReportByReported(args[1], (page*10), 10, removed, resolved);
        //List<Report> reports = Main.db.getAllReports((page)*10, 10, removed, resolved);
        if(reports.isEmpty()) {
            sender.sendMessage("§4§l[Report]§r§c Aucun résultat trouvé.");
        } else {
            if(sender instanceof Player) {
                JSONMessage msg = JSONMessage.create("§4§l[Report]§r§c Reports de " + args[1] + ": §r");
                reports.forEach((r) -> {
                    msg.newline().then(" - §3[" + r.getId() + "] §cRapporté par §e" + r.getSender()+ " §c(" + Main.dateFormat.format(r.getDate()) + ")§r").runCommand("/pios-report info " + r.getId());
                    if(r.isRemoved()) {
                        msg.then(" §4§lRM");
                    }
                    if(r.isResolved()) {
                        msg.then(" §2§lRS");
                    }
                });
                //int rs = (Main.db.getCountReport(removed, resolved)/10)+1;
                int rs = (int)(Math.floor((Main.db.getCountReportByReported(args[1], removed, resolved)-1)/10))+1;
                /*if((rs % 10) == 0) {
                    rs--;
                }*/
                    msg.newline();
                    if((page) > 0) {
                        msg.then("§f<<< ").tooltip("Page Précédente").runCommand("/pios-report reported " + args[1] + " " + (page) + (removed ? " -rm" : "" ) + (resolved ? " -rs" : "" ));
                    } else {
                        msg.then("§7<<< ");
                    }
                    msg.then("§9" + (page+1) + "§7/§9" + rs + "§r");
                    if((page+2) <= rs) {
                        msg.then(" §f>>>").tooltip("Page Suivante").runCommand("/pios-report reported " + args[1] + " " + (page+2) + (removed ? " -rm" : "" ) + (resolved ? " -rs" : "" ));
                    } else {
                        msg.then(" §7>>>");
                    }
                msg.send((Player)sender);
            } else {
                //Main.plugin.getLogger().log(Level.WARNING, "Commande exécutable uniquement depuis un joueur présent sur le serveur.");
            }
        }
        return true;
    }

    public boolean onHelpCommand(CommandSender sender, String[] args) {
        if(sender instanceof Player) {
            JSONMessage msg = JSONMessage.create("§4§l[Report]§c Commandes de PiOS Report")
                    .newline().then("§f/pios-report help §6- Accède à cette interface d'aide.").tooltip("Alias: /pir help").runCommand("/pios-report")
                    .newline().then("§f/pios-report list {page} {-rm} {-rs} §6- Affiche tous les reports.").tooltip(JSONMessage.create("Alias: /pir list {page} {-rm} {-rs}").newline().
                            then("Variables: ").newline().
                            then("§rpage §7- Page de la liste, facultatif").newline().
                            then("§r-rm §7- Affiche les reports supprimés, facultatif").newline().
                            then("§r-rs §7- Affiche les reports résolus, facultatif")).runCommand("/pios-report list")
                    .newline().then("§f/pios-report player [Joueur] {page} {-rm} {-rs} §6- Accède à la liste des reports d'un joueur.").tooltip(JSONMessage.create("Alias: /pir player [Joueur] {page} {-rm} {-rs}").
                            then("Variables: ").newline().
                            then("§rjoueur §7- pseudonyme du joueur, obligatoire").newline().
                            then("§rpage §7- Page de la liste, facultatif").newline().
                            then("§r-rm §7- Affiche les reports supprimés, facultatif").newline().
                            then("§r-rs §7- Affiche les reports résolus, facultatif")).suggestCommand("/pios-report player ")
                    .newline().then("§f/pios-report reported [Joueur] {page} {-rm} {-rs} §6- Accède à la liste des rapports contre un joueur.").tooltip(JSONMessage.create("Alias: /pir reported [Joueur] {page} {-rm} {-rs}").
                            then("Variables: ").newline().
                            then("§rjoueur §7- pseudonyme du joueur, obligatoire").newline().
                            then("§rpage §7- Page de la liste, facultatif").newline().
                            then("§r-rm §7- Affiche les reports supprimés, facultatif").newline().
                            then("§r-rs §7- Affiche les reports résolus, facultatif")).suggestCommand("/pios-report reported ")
                    .newline().then("§f/pios-report set §6- Accède à l'édition des reports.").tooltip("Alias: /pir set").runCommand("/pios-report set");
            msg.send((Player)sender);
        } else {
            sender.sendMessage("Support console à venir.");
        }
        return true;
    }

    public boolean onSetCommand(CommandSender sender, String[] allArgs) {
        String[] args = Arrays.copyOfRange(allArgs, 1, allArgs.length);
        if(args.length > 0) {
            switch(args[0]) {
                case "resolved": return onSetResolvedCommand(sender, args);
                case "removed": return onSetRemovedCommand(sender, args);
                case "reported": return onSetReportedCommand(sender, args);
                case "reason": return onSetHelpCommand(sender, args);
                case "help": return onSetHelpCommand(sender, args);
                default: return onSetHelpCommand(sender, args);
            }
        } else {
            return onSetHelpCommand(sender, args);
        }
    }

    private boolean onSetResolvedCommand(CommandSender sender, String[] args) {
        if(!(sender.hasPermission("pios.report.set.resolved"))) {
            sender.sendMessage("§4§l[Report]§r§c Vous n'avez pas la permission d'utiliser cete commande!");
            return true; 
        }
        if(args.length < 2) {
            sender.sendMessage("§4§l[Report]§r§c Argument manquant: ID!");
            return true;
        }
        int id = 0;
        try {
            id = Integer.parseInt(args[1]);
            if(id < 0) {
                sender.sendMessage("§4§l[Report]§r§c Argument manquant: ID supérieur à 0!");
                return true;
            }
        } catch (NumberFormatException ex) {
            sender.sendMessage("§4§l[Report]§r§c Argument manquant: ID sous forme de chiffre!");
            return true;
        }
        int rs = Main.db.toggleResolved(id);
        if(rs == -1) {
            sender.sendMessage("§4§l[Report]§r§c Report introuvable!");
            return true;
        } else {
            sender.sendMessage("§4§l[Report]§r§c Opération réussie!");
            return true;
        }
    }

    private boolean onSetHelpCommand(CommandSender sender, String[] args) {
        if(!(sender.hasPermission("pios.report.read"))) {
            sender.sendMessage("§4§l[Report]§r§c Vous n'avez pas la permission d'utiliser cete commande!");
            return true;
        }
        if(sender instanceof Player) {
            JSONMessage msg = JSONMessage.create("§4§l[Report]§r§c Commandes de modification:")
                    .newline().then("§f/pios-report set resolved [ID]§6 - Change l'état \"résolu\" du report").suggestCommand("/pios-report set resolved ").tooltip("§4Exemple: §c/pios-report set resolved 3§r")
                    .newline().then("§f/pios-report set removed [ID]§6 - Supprime ou restaure un report").suggestCommand("/pios-report set removed ").tooltip("§4Exemple: §c/pios-report set removed 3§r")
                    .newline().then("§f/pios-report set reported [ID] [Pseudo]§6 - Change le pseudonyme d'un rapporté dans un report").suggestCommand("/pios-report set reported ").tooltip("§4Exemple: §c/pios-report set reported 3 Jean-Louis§r")
                    .newline().then("§f/pios-report set help§6 - Consulte ce paragraphe").suggestCommand("/pios-report set help").tooltip("§4Fonctionne également sous la forme §c/pios-report set§4 sans arguments.");
            msg.send((Player)sender);
        } else {
            Main.logger.log(Level.INFO, "/pios-report set resolved [ID] - Change l'état \"résolu du report\"");
            Main.logger.log(Level.INFO, "/pios-report set removed [ID] - Supprime ou restaure un report");
            Main.logger.log(Level.INFO, "/pios-report set reported [ID] [Pseudo] - Change le pseudonyme d'un rapporté dans un report");
            Main.logger.log(Level.INFO, "/pios-report set help - Consulte ce paragraphe");
        }
        return true;
    }

    private boolean onSetRemovedCommand(CommandSender sender, String[] args) {
        if(!(sender.hasPermission("pios.report.set.resolved"))) {
            sender.sendMessage("§4§l[Report]§r§c Vous n'avez pas la permission d'utiliser cete commande!");
            return true; 
        }
        if(args.length < 2) {
            sender.sendMessage("§4§l[Report]§r§c Argument manquant: ID!");
            return true;
        }
        int id = 0;
        try {
            id = Integer.parseInt(args[1]);
            if(id < 0) {
                sender.sendMessage("§4§l[Report]§r§c Argument manquant: ID supérieur à 0!");
                return true;
            }
        } catch (NumberFormatException ex) {
            sender.sendMessage("§4§l[Report]§r§c Argument manquant: ID sous forme de chiffre!");
            return true;
        }
        int rs = Main.db.toggleRemoved(id);
        if(rs == -1) {
            sender.sendMessage("§4§l[Report]§r§c Report introuvable!");
            return true;
        } else {
            sender.sendMessage("§4§l[Report]§r§c Opération réussie!");
            return true;
        }
    }

    private boolean onSetReportedCommand(CommandSender sender, String[] args) {
        if(!(sender.hasPermission("pios.report.set.resolved"))) {
            sender.sendMessage("§4§l[Report]§r§c Vous n'avez pas la permission d'utiliser cete commande!");
            return true; 
        }
        if(args.length < 2) {
            sender.sendMessage("§4§l[Report]§r§c Argument manquant: ID!");
            return true;
        }
        if(args.length < 3) {
            sender.sendMessage("§4§l[Report]§r§c Argument manquant: Pseudonyme!");
            return true;
        }
        int id = 0;
        try {
            id = Integer.parseInt(args[1]);
            if(id < 0) {
                sender.sendMessage("§4§l[Report]§r§c Argument manquant: ID supérieur à 0!");
                return true;
            }
        } catch (NumberFormatException ex) {
            sender.sendMessage("§4§l[Report]§r§c Argument manquant: ID sous forme de chiffre!");
            return true;
        }
        Report r = Main.db.getReportById(id);
        if(r == null) {
            sender.sendMessage("§4§l[Report]§r§c Report introuvable!");
            return true;
        }
        r.setReported(args[2]);
        int rs = Main.db.UpdateReport(r);
        if(rs == -1) {
            sender.sendMessage("§4§l[Report]§r§c Erreur lors de l'opération!");
            return true;
        } else {
            sender.sendMessage("§4§l[Report]§r§c Opération réussie!");
            return true;
        }
    }

    private boolean onListCommand(CommandSender sender, String[] args) {
        if(!(sender.hasPermission("pios.report.read"))) {
            sender.sendMessage("§4§l[Report]§r§c Vous n'avez pas la permission d'utiliser cete commande!");
            return true; 
        }
        if(Arrays.asList(args).contains("help")) {
            
        }
        boolean removed = Arrays.asList(args).contains("-rm");
        boolean resolved = Arrays.asList(args).contains("-rs");
        int page = 0;
        if(args.length > 1) {
            try {
                page = Integer.parseInt(args[1]);
                page--;
            } catch (NumberFormatException ex) {}
        }
        List<Report> reports = Main.db.getAllReports((page)*10, 10, removed, resolved);
        if(reports.isEmpty()) {
            sender.sendMessage("§4§l[Report]§r§c Aucun résultat trouvé.");
        } else {
            if(sender instanceof Player) {
                JSONMessage msg = JSONMessage.create("§4§l[Report]§r§c Reports: §r");
                reports.forEach((r) -> {
                    msg.newline().then(" - §3[" + r.getId() + "]§e "+ r.getSender() + " §3==> §e" + r.getReported()+ " (" + Main.dateFormat.format(r.getDate()) + ")§r").runCommand("/pios-report info " + r.getId());
                    if(r.isRemoved()) {
                        msg.then(" §4§lRM");
                    }
                    if(r.isResolved()) {
                        msg.then(" §2§lRS");
                    }
                });
                //int rs = (Main.db.getCountReport(removed, resolved)/10)+1;
                int rs = (int)(Math.floor((Main.db.getCountReport(removed, resolved)-1)/10))+1;
                /*if((rs % 10) == 0) {
                    rs--;
                }*/
                    msg.newline();
                    if((page) > 0) {
                        msg.then("§f<<< ").tooltip("Page Précédente").runCommand("/pios-report list " + (page) + (removed ? " -rm" : "" ) + (resolved ? " -rs" : "" ));
                    } else {
                        msg.then("§7<<< ");
                    }
                    msg.then("§9" + (page+1) + "§7/§9" + rs + "§r");
                    if((page+2) <= rs) {
                        msg.then(" §f>>>").tooltip("Page Suivante").runCommand("/pios-report list " + (page+2) + (removed ? " -rm" : "" ) + (resolved ? " -rs" : "" ));
                    } else {
                        msg.then(" §7>>>");
                    }
                msg.send((Player)sender);
            } else {
                //Main.plugin.getLogger().log(Level.WARNING, "Commande exécutable uniquement depuis un joueur présent sur le serveur.");
            }
        }
        return true;
    }
}
