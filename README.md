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


![Image of the simulator Visidia while using Naimi-Tréhel algorithm](https://github.com/JulienPerrin/M2SDR_ADProjet/blob/master/VisidiaNaimiTrehel.png)
