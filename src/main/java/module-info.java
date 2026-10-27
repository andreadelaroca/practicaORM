module ni.edu.uam.practicaorm {
    requires javafx.controls;
    requires javafx.fxml;
    requires jakarta.persistence;
    requires org.hibernate.orm.core;
    requires java.naming;
    requires java.sql;
    requires org.postgresql.jdbc;

    opens ni.edu.uam.practicaorm to javafx.fxml;
    opens ni.edu.uam.practicaorm.controller to javafx.fxml;

    opens ni.edu.uam.practicaorm.model to org.hibernate.orm.core, javafx.base;
    opens ni.edu.uam.practicaorm.config to org.hibernate.orm.core;

    exports ni.edu.uam.practicaorm;
    exports ni.edu.uam.practicaorm.controller;
    exports ni.edu.uam.practicaorm.model;
    exports ni.edu.uam.practicaorm.repository;
    exports ni.edu.uam.practicaorm.config;
}