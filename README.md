# M2SDR_ADProjet
Projet de cours d'algorithme distribués visant à simuler un système distribué sur le simulateur Visidia avec l'algorithme de Naimi-Tréhel

L'algorithme de Naimi-Tréhel suit ces idées :
- l'owner est le dernier possesseur que l'on connait qui a ou qui aura le jeton. Pour ce faire, on se base sur toutes les informations que l'on connait. 
- next est celui à qui on doit envoyer le jeton si l'on monopolisait le jeton quand quelqu'un nous l'a demandé. 

Liste des tâches : 
- [x] implémentation de l’algorithme de Naimi-Tréhel sur le simulateur ViSiDiA
	- [x] reception rules
	- [x] messages
	- [x] initialisation (règle 1)
	- [x] règles :
		- [x] Pi demande accès SC
		- [x] Pi reçoit le message REQ(k, envoyeur) de j
		- [x] Pi reçoit le message Jeton(k, envoyeur) de j
- [x] ajout de frames 
- [x] implémentation d'une élection du processus possédant le jeton en premier
	- [x] envoi d'une demande d'élection par un initiateur
	- [x] reception d'une demande l'élection
	- [x] envoi du message leader par un processus qui a compris qu'il était le leader
- [x] adaptation de l'algo pour un réseau quelconque 
	- [x] besoin de faire l'initialisation proprement, pour cela, l'idée est de choisir l'owner de façon pertinente pour chaque processus, pour ne jamais tourner en rond et toujours pouvoir récupérer le jeton, on doit pouvoir toujours remonter au jeton, 
	- [x] lors de la réception d'une requête, l'owner devient soit l'emeteur de la requête s'il est voisin direct du processus, soit l'envoeyeur originel de la requête. Il est nécessaire de se souvenir de l'envoyeur correspondant à l'envoyeur originel pour pouvoir lui renvoyer le jeton. 
	- [x] quand on reçoit un jeton, la cible du jeton devient l'owner si voisin direct et sinon on fait passer le jeton à celui qui nous avait fait passer la requête et ce dernier devient le nouvel owner.  

