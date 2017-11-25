# WoME - World Of Middle Earth

> By JavaDabaDuu  ([JavaDabaDuu](https://github.com/JavaDabaDuu))


## INSTRUCTIVO DE INSTALACIÓN Y EJECUCIÓN


# OBTENER REPOSITORIOS

Primero que nada, para poder correr el juego es necesario que la computadora tenga instalada
la versión JDK 1.7 o superior. Se utiliza JUnit 4.

El juego está colgado en Github. Descargue los tres repositorios, presionando download.
En este caso, deberá descomprimir los .zip descargados.

* Dominio:
https://github.com/JavaDabaDuu/jrpg-2017b-dominio

* Cliente:
https://github.com/JavaDabaDuu/jrpg-2017b-cliente

* Servidor:
https://github.com/JavaDabaDuu/jrpg-2017b-servidor

O bien, clone los repositorios mediante usando los .git:

* Dominio:
https://github.com/JavaDabaDuu/jrpg-2017b-dominio.git

* Cliente:
https://github.com/JavaDabaDuu/jrpg-2017b-cliente.git

* Servidor:
https://github.com/JavaDabaDuu/jrpg-2017b-servidor.git


# INSTALAR PROYECTOS

Abrir los proyectos en el IDE Eclipse.
Para cada uno, cambiar el encoding para que Eclipse sepa con qué juego de caracteres debe interpretar los archivos.
Diríjase a Proyect --> Properties -> Resource --> Text file encoding --> Other --> UTF-8,
y luego presione Apply y OK.

Los proyectos deben estar enlazados.
Verifique que el BuildPath de cada proyecto sea el correcto.

Diríjase a Proyect --> Properties --> Java BuildPath --> Proyects.
Una vez allí, si los proyectos enlazados son los correspondientes, no haga nada.
Caso contrario, presione Add para agregar proyectos.

* Dominio no va enlazado a los demás proyectos.

* Cliente va enlazado a Dominio --> jrpg-2017b-dominio

* Servidor va enlazado a Dominio --> jrpg-2017b-dominio y a Ciente jrpg-2017b-cliente


Diríjase a Proyect --> Properties --> Java BuildPath --> Proyects. 
Una vez allí, si los bibliotecas enlazadas son las correspondientes, no haga nada.
Caso contrario, presione Add Libraries y Add Jars, para agregar biblotecas y jars.

* Dominio va enlazado con:
JRE System Library
JUnit 4

* Cliente va enlazado con:
JRE System Library
JUnit 4
gson-2.8.0

* Servidor va enlazado con:
hibernate-core-5.2.11.Final
hibernate-jpa-2.1-api-1.0.0.Final
gson-2.8.0


# EJECUTAR JUEGO

Este juego utiliza arquitectura cliente - servidor.
Por los tanto, primero debe correr el servidor (solo una vez) y luego los clientes (una vez para cada cliente que quiera jugar).

* Para correr el Servidor, debe ejecutar como Java Aplication a la clase Servidor.java,
ubicada en /jrpg-2017b-servidor-master/src/main/java/servidor/Servidor.java.
Una vez que visualice el log del servidor, presione Iniciar y espere a que en el log indique que está escuchando conexiones.

* Para correr el Cliente debe ejecutar como Java Aplication a la clase MenuInicio.java,
ubicada en /jrpg-2017b-cliente-master/src/main/java/frames/MenuInicio.java.


Listo, no queda más que jugar. Diviértase.