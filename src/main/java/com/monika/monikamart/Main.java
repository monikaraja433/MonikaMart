package com.monika.monikamart;

import java.io.File;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;

public class Main {
    public static void main(String[] args) throws Exception {
        int port = 8080;
        String portProp = System.getProperty("server.port");
        if (portProp != null && !portProp.trim().isEmpty()) {
            port = Integer.parseInt(portProp.trim());
        }

        Tomcat tomcat = new Tomcat();
        tomcat.setPort(port);
        tomcat.setBaseDir("temp-tomcat");

        // Force creation of default HTTP connector
        tomcat.getConnector();

        File explodedDir = new File("target/monikamart");
        String webappDirLocation = explodedDir.exists() ? explodedDir.getAbsolutePath() : new File("src/main/webapp").getAbsolutePath();
        StandardContext ctx = (StandardContext) tomcat.addWebapp("", webappDirLocation);
        ctx.setReloadable(true);
        ctx.setParentClassLoader(Main.class.getClassLoader());

        // Map compiled classes to WEB-INF/classes in embedded context if running from source
        File additionWebInfClasses = new File("target/classes");
        if (additionWebInfClasses.exists() && !explodedDir.exists()) {
            WebResourceRoot resources = new StandardRoot(ctx);
            resources.addPreResources(new DirResourceSet(resources, "/WEB-INF/classes",
                    additionWebInfClasses.getAbsolutePath(), "/"));
            ctx.setResources(resources);
        }

        System.out.println("==================================================================");
        System.out.println(" 🛒 MonikaMart E-Commerce Platform is Starting...");
        System.out.println(" 🌐 Catalog URL:    http://localhost:" + port + "/products");
        System.out.println(" 🔐 Login URL:      http://localhost:" + port + "/login");
        System.out.println(" 🏥 Health Check:   http://localhost:" + port + "/api/v1/health");
        System.out.println(" 🤖 AI Chatbot:     Integrated Floating Widget on all pages");
        System.out.println("==================================================================");

        tomcat.start();
        tomcat.getServer().await();
    }
}
