//
Desarrollado: Leonardo Galicia Jiménez
Fecha: 25 de abril de 2016

Descripción: 
	- Aplicación desarrollada en Java que permite solicitar peticiones GET a un conjunto de URL definidas
y obtener su respuesta, ya sea exitosa o no; de tal manera que una vez obtenido el HTML, se analiza éste y se 
lanzan nuevas peticiones a los links necesarios tales como css, js, imágenes, etc, con el objeto de conocer 
el estado de éstos. La aplicación es creada para solicitar peticiones a una lista de URL definidas en un 
archivo de texto, para de esta manera probar el router, el proxy de éste, y el proxy en la nube en un determinado
ambiente. La aplicación ocupa un archivo de configuración que define el comportamiento de ésta, como por ejemplo
si la aplicación hará peticiones secuenciales o lanzará varias al mismo tiempo, o que tipo de proxy utilizará, 
etc, etc.

Ejecución de la aplicación:
La aplicación se ejecuta de la siguiente manera
java -cp [nombre.de.la.aplicación].jar test.app [directorio]/ [archivo.properties]

Donde:
 [nombre.de.la.aplicación] es el nombre de archivo jar que hay que ejecutar
 [directorio] es la ruta donde se encuentra el archivo properties a leer
 [archivo.properties] es el archivo de texto que contiene las características de la prueba
 
 Todos estos parámetros son obligatorios incluyendo el /

Ejemplo suponiendo que se ejecuta la aplicación desde dentro del directorio c:/app/
java -cp tool.jar test.app ./tmp/ properties.txt

Esto ejecutará el archivo tool.jar 

Donde:
 El parámetro test.app le indica cuál es la clase a ejecutar, la cual es app.class en el paquete test.
 El parámetro ./tmp/ le indica que el archivo de propiedades se encuentra en la carpeta tmp del directorio actual. 
 Es decir, en c:/app/tmp/ es importante terminar este parámetro con /
 El parámetro properties.txt es el archivo de texto que contiene las características de la prueba

El archivo properties:
El archivo properties es un documento de texto definido en contenido, es decir, debe siempre contener todos 
los parámetros y en ese orden, sólo es posible cambiar sus valores. El archivo properties contiene lo siguiente:

workingPath    = C:\tmp\test\		  <-- Directorio de trabajo que se usará de base para trabajar.
fileIn         = set.pruebas.txt	  <-- Archivo de texto que contiene las URL a solicitar y ubicada en workingPath.
testName       = lan-router-sin-proxy <-- Es el nombre representativo de la prueba, se usa para dar nombre al reporte.
Threads        = 400				  <-- Número de hilos que se levantarán, es decir, número de peticiones simultáneas
                                          Si es uno, las peticiones se realizan de manera secuencial.
repeat         = 1					  <-- Le indica a cada hilo cuantas veces habrá de repetir cada URL antes de pasar 
										  a la siguiente.
divideFiles    = true				  <-- Indica si el archivo definido en fileIn se dividirá en forma proporcional entre
										  cada hilo definido en Threads, si por ejemplo hay 10 hilos y 20 URL en el archivo
										  definido en fileIn, cada hilo tomará 2 URL. Si el valor es false, la lista. 
										  completa será tomada por cada hilo, es decir, cada hilo ejecuitaría las 20 URL.
IncludeLinks   = true				  <-- Indica si el la petición de la URL original, se incluirá también la peticion a 
										  todos los links relativos del HTML principal. Si el valor es false, sólo se 
										  considerará el HTML principal. NOTA: Al incluir los links, las peticiones a éstos 
										  se realizarán de manera secuencial, es decir, uno detrás de otro.
multiports = false					  <-- Indica si se probará un ambiente multipuerto, esto es, si al proxy en la nube se 
										  le realizarán diferentes peticiones usando puertos distintos. Si el valor es true
										  se parte del valor en start y se incrementa según el valor definido en increment.
start      = 1322					  <-- Valor que sólo es usado cuando el valor multiports es verdadero.
increment  = 0						  <-- Valor que sólo es usado cuando el valor multiports es verdadero.

									      Las siguientes opciones definen que tipo de peticiones se realizarán, cada uno de 
										  los siguientes valores indican un tipo particular de proxy que se usará. Se debe 
										  indicar por lo menos un proxy como true, de otra manera no se realizarán peticiones.
										  Cuando más de un tipo de proxy es activado, por cada hilo definido se agrega aquellos
										  marcados como true, lo que hará que cada hilo al tomar una URL realize una petición 
										  o n peticiones según se definió en el parámetro repeat, con cada uno de estos proxys
										  marcados como true. Esto es así ya que es posible marcar dos proxys distintos en 
										  un periodo determinado y conocer comportamientos o comparar la respuesta de dichos
										  proxys.
internet              = true		  <-- Este no es ningún tipo de proxy y realiza peticiones directas a la infraestructura
										  actual, se usa mayormente como elemento de control y punto de comparación. Por ejemplo, 
										  se realiza peticiones directas usando la infraestructura de telnor.
proxyCloudWithICAP    = false		  <-- Este proxy realiza una petición conectándose a un proxy en la nube con ICAP activado,
										  aunque esto último depende de la infraestructura, pero el propósito de este proxy es ese.
proxyCloudWithOutICAP = false		  <-- Este proxy realiza una petición conectándose a un proxy en la nube sin ICAP activado,
										  aunque esto último depende de la infraestructura, pero el propósito de este proxy es ese.
proxyRouter           = false		  <-- Este proxy realiza una petición conectándose al router según la configuración que tenga,
										  pero el propósito este proxy es ese.
										  
										  Las siguientes opciones definen los parámetros de cada uno de los proxy definidos en la
										  sección anterior. Cada uno de ellos tiene características particulares, por ejemplo, los
										  dos primeros están configurados para autenticarse, mientras que el último no.
        
proxyCloudWithICAPHost     = 67.138.106.149
proxyCloudWithICAPPort     = 1322
proxyCloudWithICAPLogin    = admin
proxyCloudWithICAPPassword = admin
        
proxyHostCloudWithOutICAP     = 67.138.106.151
proxyCloudWithOutICAPPort     = 1322
proxyCloudWithOutICAPLogin    = admin
proxyCloudWithOutICAPPassword = admin
        
proxyRouterHost = 172.16.5.89
proxyRouterPort = 3128
