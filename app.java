/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import test.entities.Properties;
import test.process.Process;
import test.proxy.Internet;
import test.proxy.ProxyCloudWithICAP;
import test.proxy.ProxyRouter;
import java.util.ArrayList;
import test.proxy.ProxyCloudWithOutICAP;
import test.tools.Tools;

/**
 *
 * @author lgalicia
 */
public class app {
    public static void main(String[] args) {
        // El programa se ejecuta java -cp ./target/url.validation-1.0-SNAPSHOT.jar test.app ./ properties.txt
        // +++++++++++++++++++++++++++++++++++++
        
        // Se asignan los parámetros introducidos por la línea de comandos
        String path = args[0];//"D:/workplace/ToDo/pruebas/cloud/";
        String file = args[1];//"properties.txt";
        //String path = "D:\\workplace\\ToDo\\pruebas\\url.limpias\\test.app\\";
        //String file = "properties0.txt";
        
        // Se lee los parametros del archivos properties.txt
        Properties properties = new Properties();
        properties.getProperties(path+file);
        
        // Se crea un directorio aleatorio de trabajo
        String workDirectory = "";
        int start = 65;int end = 90;
        for (int i=0;i<15;i++){
            workDirectory = workDirectory + ((char) ((end - start + 1) * Math.random() +start) + ""); 
        }
        workDirectory = workDirectory + "/";
        
        // Se asigna el directorio de trabajo
        properties.setWorkDirectory(workDirectory);
        
        // Esta es la herramienta genérica de trabajo
        Tools tools = new Tools();
        // Este es el array que contendrá los n hilos
        ArrayList testArray = new ArrayList();
        
        // Se crea el directorio de trabajo
        if (tools.makeDirectory(path+properties.getWorkDirectory())){
            // Se copia el archivo de las URL al directorio de trabajo
            tools.copyFile(
                    properties.getPath()+properties.getFileIn(),
                    properties.getPath()+properties.getWorkDirectory()+properties.getFileIn()
            );
            // Se divide el archivo de las URl en n archivos para cada uno de los n hilos
            tools.divideFile(properties);
            // Se crea el archivo done.txt que indica que hilos han terminado
            tools.createDone(properties.getPath()+properties.getWorkDirectory());

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
                Process hilo = new Process(test);
                testArray.add(hilo);
                ((Process)testArray.get(i-1)).start();
            }
        } else {
            System.out.println("Error, el directorio ya existe");
        }
        
        // Supervisa el archivo done.txt para revisar si han terminado todos los hilos
        tools.waitDone(properties);
        // Une los archivos
        tools.createReport(testArray,properties.isIncludeLinks());
        // Se borra el directorio de trabajo
        tools.deleteDirectory(properties.getPath()+properties.getWorkDirectory());
    }
}
