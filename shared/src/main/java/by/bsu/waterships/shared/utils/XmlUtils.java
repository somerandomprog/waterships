package by.bsu.waterships.shared.utils;

import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.XMLConstants;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class XmlUtils {
    public record XmlResult(boolean success, String error, Object data) {
    }

    public static <T> XmlResult marshal(T what) {
        String simpleName = what.getClass().getSimpleName();
        if (XmlUtils.class.getResource("/by/bsu/waterships/shared/protocol/xsd/" + simpleName + ".xsd") == null)
            return new XmlResult(false, "cannot marshal object of type " + simpleName + " since it's missing the XML-schema (.xsd) definition", null);
        if (XmlUtils.class.getResource("/by/bsu/waterships/shared/protocol/dtd/" + simpleName + ".dtd") == null)
            return new XmlResult(false, "cannot marshal object of type " + simpleName + " since it's missing the DTD (.dtd)", null);

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            JAXBContext context = JAXBContext.newInstance(what.getClass());
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
            marshaller.marshal(what, baos);
            baos.flush();
            return new XmlResult(true, null, baos.toString(StandardCharsets.UTF_8));
        } catch (JAXBException e) {
            return new XmlResult(false, "failed to marshal object of type " + simpleName + ": " + e.getMessage(), null);
        } catch (IOException e) {
            return new XmlResult(false, "failed to marshal object of type " + simpleName + " (io error): " + e.getMessage(), null);
        }
    }

    public static XmlResult unmarshal(String className, String data) {
        XmlResult validationResult = validate(className, data);
        if (!validationResult.success) return validationResult;

        try {
            Class<?> what = Class.forName(className);
            JAXBContext context = JAXBContext.newInstance(what);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            return new XmlResult(true, null, unmarshaller.unmarshal(new InputSource(new StringReader(data))));
        } catch (ClassNotFoundException e) {
            return new XmlResult(false, "failed to locate class " + className, null);
        } catch (JAXBException e) {
            return new XmlResult(false, "failed to unmarshal xml: " + e.getMessage(), null);
        }
    }

    public static XmlResult validate(String className, String data) {
        String simpleName;
        try {
            simpleName = Class.forName(className).getSimpleName();
        } catch (ClassNotFoundException e) {
            return new XmlResult(false, "failed to locate class " + className, null);
        }

        Source xmlSource = new StreamSource(new StringReader(data));

        try {
            Source schemaSource = new StreamSource(XmlUtils.class.getResourceAsStream("/by/bsu/waterships/shared/protocol/xsd/" + simpleName + ".xsd"));
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = schemaFactory.newSchema(schemaSource);
            Validator validator = schema.newValidator();
            validator.validate(xmlSource);
        } catch (SAXException e) {
            return new XmlResult(false, "schema (xsd) validation failed (couldn't create schema/xml does not match schema): " + e.getMessage(), null);
        } catch (IOException e) {
            return new XmlResult(false, "schema (xsd) validation failed (io error): " + e.getMessage(), null);
        }

        try {
            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            domFactory.setValidating(true);
            domFactory.setNamespaceAware(true);

            DocumentBuilder builder = domFactory.newDocumentBuilder();
            builder.setEntityResolver((publicId, systemId) -> new InputSource(XmlUtils.class.getResourceAsStream("/by/bsu/waterships/shared/protocol/dtd/" + simpleName + ".dtd")));

            List<String> errors = new ArrayList<>();
            builder.setErrorHandler(new ErrorHandler() {
                @Override
                public void warning(SAXParseException exception) {
                    errors.add(exception.getMessage());
                }

                @Override
                public void error(SAXParseException exception) {
                    errors.add(exception.getMessage());
                }

                @Override
                public void fatalError(SAXParseException exception) {
                    errors.add(exception.getMessage());
                }
            });

            String newData = injectDoctype(data, simpleName, simpleName + ".dtd");
            builder.parse(new InputSource(new StringReader(newData)));
            if (!errors.isEmpty())
                return new XmlResult(false, "schema (dtd) validation failed: " + String.join(", ", errors), null);
        } catch (IOException e) {
            return new XmlResult(false, "schema (dtd) validation failed (io error): " + e.getMessage(), null);
        } catch (ParserConfigurationException e) {
            return new XmlResult(false, "schema (dtd) validation failed (failed to configure parser): " + e.getMessage(), null);
        } catch (SAXException e) {
            return new XmlResult(false, "schema (dtd) validation failed (failed to parse xml): " + e.getMessage(), null);
        }

        return new XmlResult(true, null, null);
    }

    private static String injectDoctype(String xml, String rootElement, String dtdName) {
        String doctype = "<!DOCTYPE " + rootElement + " SYSTEM \"" + dtdName + "\">";
        if (xml.trim().startsWith("<?xml")) return xml.replaceFirst("(<\\?xml.*\\?>)", "$1\n" + doctype);
        else return doctype + "\n" + xml;
    }
}
