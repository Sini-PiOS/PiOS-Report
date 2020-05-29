# PiOS Report v1.0
**PiOS Report** est un plugin permettant de rapporter des joueurs et d'en avertir les modérateurs par plusieurs biais: le chat, la console et un webhook Discord.
## Note
**[ENGLISH]**
*Since the plugin is only French, the Readme is only French.
In a future releases, an English version will be added.
This is my first public plugin!*
**[FRENCH]**
*Étant donné que le plugin est uniquement en français, le fichier Readme est uniquement en français.
Dans une prochaine version, une version anglaise sera ajoutée.
Ceci est mon premier plugin public!*

## Commandes
*[ ] : Argument obligatoire*
*{ } : Argument facultatif*
|Commande|Permission|Description|
|--|--|--|
|/report [Joueur] [Raison]|pios.report (par défaut)|Permet de rapporter un joueur.|
|/pios-report help|pios.report.read|Permet d'afficher l'aide de l'interface d'administration.|
|/pios-report info [ID]|pios.report.read|Permet d'afficher l'aide de l'interface d'administration.|
|/pios-report list {page} {-rs} {-rm}|pios.report.read|Permet d'afficher la liste des rapports. -rs permet d'afficher les rapports résolus. -rm permet d'afficher les rapports supprimés.|
|/pios-report player [Joueur] {page} {-rs} {-rm}|pios.report.read|Permet d'afficher la liste des rapports effectués par un joueur. -rs permet d'afficher les rapports résolus. -rm permet d'afficher les rapports supprimés.|
|/pios-report reported [Joueur] {page} {-rs} {-rm}|pios.report.read|Permet d'afficher la liste des rapports contre un joueur. -rs permet d'afficher les rapports résolus. -rm permet d'afficher les rapports supprimés.|
|/pios-report set help|pios.report.read|Permet d'afficher l'aide de l'interface d'édition de rapport.|
|/pios-report set resolved [ID]|pios.report.set.resolved|Permet de définir un rapport comme résolu.|
|/pios-report set removed [ID]|pios.report.set.removed|Permet de définir un rapport comme supprimé.|
|/pios-report set reported [Joueur]|pios.report.set.edit|Permet de redéfinir la cible d'un rapport.|

## Permissions
|Permission|Description|
|--|--|
|pios.report|Permet de rapporter un joueur.|
|pios.report.receive|Permet de recevoir les rapports.|
|pios.report.read|Permet de consulter les anciens rapports.|
|pios.report.set.resolved|Permet de définir un rapport comme résolu.|
|pios.report.set.removed|Permet de définir un rapport comme supprimé.|
|pios.report.set.edit|Permet de redéfinir certains éléments d'un rapport.|
|pios.report.*|Englobe toutes les permissions du plugin.|
|pios.report.admin|Englobe toutes les permissions de modérateur du plugin.|
|pios.report.set.*|Englobe toutes les permissions d'édition plugin.|
|pios.report.*|Englobe toutes les permissions du plugin.|

## Screenshots
![/report Ltms055 A détruit mon champ dans le monde créatif](http://siniup.com/resources/pios/report/report.png)
![Reçu du rapport aux modérateurs](http://siniup.com/resources/pios/report/result.png)
![Résultat du rapport sur Discord](http://siniup.com/resources/pios/report/discord.png)
![Commande /pios-report help](http://siniup.com/resources/pios/report/help.png)
![Commande /pios-report list](http://siniup.com/resources/pios/report/list.png)![Commande /pios-report player ch_pi](http://siniup.com/resources/pios/report/player.png)
![Commande /pios-report list -rs](http://siniup.com/resources/pios/report/list-rs.png)![Commande /pios-report list -rm](http://siniup.com/resources/pios/report/list-rm.png)![Commande /pios-report list -rm -rs](http://siniup.com/resources/pios/report/list-rm-rs.png)
![Commande /pios-report info 28](http://siniup.com/resources/pios/report/info.png)