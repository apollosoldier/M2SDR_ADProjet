# M2SDR_ADProjet
Projet de cours d'algorithme distribués visant à simuler un système distribué sur le simulateur Visidia implémentant l'algorithme d'exclusion mutuelle de Naimi-Tréhel. 
Cet algortihme est notament réputé sur les réseaux complets pour échanger un nombre moyen de message de l'ordre de O(Log(n)). 

L'algorithme de Naimi-Tréhel suit ces idées :
- chaque processus implémente le même algorithme décrit dans ce README. 
- chaque processus cherche par moment à accéder à la section critique;
- chaque processus a au moins un processus voisin, avec qui il peut communiquer;
- les processus font tourner entre eux un jeton, unique droit d'accès à la section critique;
- l'owner est le dernier processus que l'on connait qui a ou qui aura le jeton, et qui est dans notre voisinage. Pour le déterminer, on se base sur toutes les informations que l'on connait : algorithme d'élection à l'initiation, requêtes reçues, jetons envoyés. 
- next est celui à qui on doit envoyer le jeton si l'on ne pouvait pas accéder à la requête lorsqu'on a reçue celle-ci, soit parce qu'on était en train d'accéder à la section critique, soit parce que l'on était en attente du jeton pour accéder à la section critique. 


L'algorithme d'élection est l'algorithme d'élection par extinction qui permet d'obtenir un leader même sur un réseau quelconque et de fixer un owner initial pour chaque processus : 
- chaque processus initiateur diffuse son id; 
- un processus va diffuser l'id qu'il reçoit que s'il n'a pas reçu de numéro d'id plus important avant
- lorsqu'un processus initiateur reçoit un message de la part de tout ces voisins avec son id, alors il sait qu'il est le leader. Il diffuse le message leader avec son id sur le réseau pour informer tout le monde. 
- lorsqu'un processus apprend pour la première fois qui est le leader, il enregistre celui-ci comme son owner s'ils sont voisins, et sinon celui qui lui a envoeyr l'information comme son owner. 


Liste des tâches : 
- [x] implémentation de l’algorithme de Naimi-Tréhel sur le simulateur ViSiDiA
	- [x] reception rules
	- [x] messages
	- [x] règles :
		- [x] p demande accès SC : envoi d'une requête REQ(Pi, Pi) à son owner pour obenir le jeton à moins qu'on l'ait déjà auquel cas on accède directement;  
		- [x] p reçoit le message REQ(k, envoyeur) de envoyeur : on transmet la requête REQ(k, p) à notre owner, si on est en section critique ou en attente de section critique, on enregistre k comme owner et on lui renverra le jeton plus tard. 
		- [x] p reçoit le message Jeton(k, envoyeur) de envoyeur : si le processus a pour id k, il passe en SC et met à jour son owner à -1, sinon il transmet à celui qui lui avait envoyé la requête de k et met à jour son owner. 
		- [x] Pi sort de section critique : transmission éventuelle du jeton à next
- [x] ajout de frames 
- [x] implémentation d'une élection du processus possédant le jeton en premier
	- [x] envoi d'une demande d'élection par un initiateur
	- [x] reception d'une demande l'élection
	- [x] envoi du message leader par un processus qui a compris qu'il était le leader
- [x] adaptation de l'algo pour un réseau quelconque 
	- [x] besoin de faire l'initialisation proprement, pour cela, l'idée est de choisir l'owner de façon pertinente pour chaque processus, pour ne jamais tourner en rond et toujours pouvoir récupérer le jeton, on doit pouvoir toujours remonter au jeton, 
	- [x] lors de la réception d'une requête, l'owner devient soit l'emeteur de la requête s'il est voisin direct du processus, soit l'envoeyeur originel de la requête. Il est nécessaire de se souvenir de l'envoyeur correspondant à l'envoyeur originel pour pouvoir lui renvoyer le jeton. 
	- [x] quand on reçoit un jeton, la cible du jeton devient l'owner si voisin direct et sinon on fait passer le jeton à celui qui nous avait fait passer la requête et ce dernier devient le nouvel owner.  

