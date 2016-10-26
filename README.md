# M2SDR_ADProjet
Projet de cours d'algorithme distribués visant à simuler un système distribué sur le simulateur Visidia avec l'algorithme de Naimi-Tréhel


A faire : 
- [x] implémentation de l’algorithme de Naimi-Tréhel sur le simulateur ViSiDiA
	- [x] reception rules
	- [x] messages
	- [x] initialisation (règle 1)
	- [x] règles :
		- [x] Pi demande accès SC
		- [x] Pi reçoit le message REQ(k) de j
		- [x] Pi reçoit le message Jeton de j
- [x] ajout de frames 
- [ ] implémentation d'une élection du processus possédant le jeton en premier
- [ ] adaptation de l'algo pour un réseau quelconque 
	- [ ] besoin de faire l'initialisation, pour cela, l'idée serait de choisir l'owner de façon pertinente pour chaque processus, pour ne jamais tourner en rond et toujours pouvoir récupérer le jeton, on devrait pouvoir toujours remonter au jeton, cela voudrait peut-être dire qu'il faut mieux gérer les messages de requête
	- [ ] ensuite il faut toujours choisir l'owner de façon pertinente : 
		- [ ] quand on reçoit REQ(k)
			- idées : 
				-faire remonter HEY jusqu'à retourner à l'origine de REQ(k) en changeant l'owner
				-  
		- [ ] quand on sort de SC, il n'est pas forcément nécessaire de repasser à -1, peut-être que l'on peut choisir l'owner judicieusement


