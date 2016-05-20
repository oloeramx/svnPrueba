/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.util.ArrayList;
import test.entities.Properties;
import test.proxy.Internet;
import test.proxy.ProxyCloudWithICAP;
import test.proxy.ProxyCloudWithOutICAP;
import test.proxy.ProxyRouter;
import test.tools.Tools;

/**
 *
 * @author lgalicia
 */
public class join {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // El programa se ejecuta java -cp ./target/url.validation-1.0-SNAPSHOT.jar test.app ./ properties.txt
        // +++++++++++++++++++++++++++++++++++++
        
        // Se asignan los parámetros introducidos por la línea de comandos
        String path = args[0];//"D:/workplace/ToDo/pruebas/cloud/";
        String file = args[1];//"properties.txt";
        String workDirectory = file = args[2];
        //String path = "D:\\Public\\test.app\\";
        //String file = "properties.txt";
        //String workDirectory = "unir.archivos\\";
        
        // Se lee los parametros del archivos properties.txt
        Properties properties = new Properties();
        properties.getProperties(path+file);
        
        // Se asigna el directorio de trabajo
        properties.setWorkDirectory(workDirectory);
        
        // Esta es la herramienta genérica de trabajo
        Tools tools = new Tools();
        // Este es el array que contendrá los n hilos
        ArrayList testArray = new ArrayList();
        
        // Se crea el directorio de trabajo
        if (true){
            // Se crean las variables para el nombre del archivo de trabajo (los n archivos creados para los n hilos)
            String fileTmp;
            int initialPort = properties.getInitialPort();
            
            // Se preparan los parametros del test para todos los hilos
            for (int i=1;i<=properties.getThreads();i++){
                Test test = new Test();
                // Se lee los parametros del archivos properties.txt
                Properties newProperties = new Properties();
                newProperties.getProperties(path+file);
                // Se asigna el directorio de trabajo
                newProperties.setWorkDirectory(workDirectory);

                // Si es un sólo hilo, el archivo de URL no se dividió
                if (newProperties.getThreads() > 1) {fileTmp = newProperties.getFileIn() + "."+ i +".txt";} 
                else {fileTmp = newProperties.getFileIn();}

                // Parametros generales del test
                newProperties.setWorkFile(fileTmp);
                test.setProperties(newProperties);

                // Se agregan los requester
                if (newProperties.isInternet()) {
                    // Este es quien hace la petición simple del URL sin configuración alguna
                    Internet internet = new Internet();
                    test.addRequest(internet);
                }
                if (newProperties.isProxyCloudWithICAP()) {
                    // Este es quien hace la petición del URL usando el cloud proxy con ICAP
                    ProxyCloudWithICAP proxyCloudWithICAP = new ProxyCloudWithICAP();
                    proxyCloudWithICAP.setHost(newProperties.getCloudWithICAP().getHost());

                    if (newProperties.isMultiPorts()) {
                        proxyCloudWithICAP.setPort(initialPort);
                        initialPort = initialPort + newProperties.getIncrement();
                    } else proxyCloudWithICAP.setPort(newProperties.getCloudWithICAP().getPort());

                    proxyCloudWithICAP.setLogin(newProperties.getCloudWithICAP().getLogin());
                    proxyCloudWithICAP.setPassword(newProperties.getCloudWithICAP().getPassword());

                    test.addRequest(proxyCloudWithICAP);
                }
                if (newProperties.isProxyCloudWithOutICAP()) {
                    // Este es quien hace la petición del URL usando el cloud proxy sin ICAP
                    ProxyCloudWithOutICAP proxyCloudWithOutICAP = new ProxyCloudWithOutICAP();
                    proxyCloudWithOutICAP.setHost(newProperties.getCloudWithOutICAP().getHost());
                    proxyCloudWithOutICAP.setPort(newProperties.getCloudWithOutICAP().getPort());
                    proxyCloudWithOutICAP.setLogin(newProperties.getCloudWithOutICAP().getLogin());
                    proxyCloudWithOutICAP.setPassword(newProperties.getCloudWithOutICAP().getPassword());

                    test.addRequest(proxyCloudWithOutICAP);
                }

                if (newProperties.isProxyRouter()) {
                    // Este es quien hace la petición del URL usando el proxy del router
                    ProxyRouter proxyRouter = new ProxyRouter();
                    proxyRouter.setHost(newProperties.getRouter().getHost());
                    proxyRouter.setPort(newProperties.getRouter().getPort());

                    test.addRequest(proxyRouter);
                }

                // Se crean los hilos y se ejecutan
                test.process.Process hilo = new test.process.Process(test);
                testArray.add(hilo);
                //((test.process.Process)testArray.get(i-1)).start();
            }
        } else {
            System.out.println("Error, el directorio ya existe");
        }
        // Une los archivos
        tools.createReport(testArray,properties.isIncludeLinks());
    }
}
