module by.bsu.waterships.shared {
    requires jakarta.xml.bind;
    requires java.xml;
    exports by.bsu.waterships.shared;
    exports by.bsu.waterships.shared.types;
    exports by.bsu.waterships.shared.utils;
    exports by.bsu.waterships.shared.protocol.results;
    exports by.bsu.waterships.shared.protocol;
    opens by.bsu.waterships.shared.protocol.dtd;
    opens by.bsu.waterships.shared.protocol.xsd;
    opens by.bsu.waterships.shared.types to jakarta.xml.bind, com.sun.xml.bind;
    opens by.bsu.waterships.shared.protocol to jakarta.xml.bind, com.sun.xml.bind;
    opens by.bsu.waterships.shared.protocol.results to jakarta.xml.bind, com.sun.xml.bind;
}