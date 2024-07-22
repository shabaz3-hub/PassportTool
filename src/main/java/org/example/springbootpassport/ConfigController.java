package org.example.springbootpassport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class ConfigController {

    private final AppConfig appConfig;

    @Autowired
    public ConfigController(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    @GetMapping("/config")
    public Map<String, Object> getConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("tags", appConfig.getTags());
        config.put("carriers", appConfig.getCarriers());
        config.put("shipMethods", appConfig.getShipMethods());

        return config;
    }

    @PostMapping("/submit")
    public void handleSubmitForm(@RequestBody List<Map<String, String>> formData) {
        formData.forEach(row -> {
            System.out.println("Checkbox: " + row.get("checkbox"));
            System.out.println("Label1: " + row.get("label1"));
            System.out.println("Label2: " + row.get("label2"));
            System.out.println("Tag: " + row.get("dropdown1"));
            System.out.println("Carrier: " + row.get("dropdown2"));
            System.out.println("Ship Method: " + row.get("dropdown3"));
        });
    }

    @PostMapping("/generateXmlFiles")
    public void generateXmlFiles(@RequestBody List<Map<String, String>> formData, @RequestParam String type) throws ParserConfigurationException, TransformerException {
        Map<String, Map<String, List<Map<String, String>>>> groupedData = formData.stream()
                .filter(row -> Boolean.parseBoolean(row.get("checkbox")))
                .collect(Collectors.groupingBy(
                        row -> row.get("dropdown2"),
                        Collectors.groupingBy(row -> row.get("dropdown3"))
                ));

        for (Map.Entry<String, Map<String, List<Map<String, String>>>> carrierEntry : groupedData.entrySet()) {
            String carrier = carrierEntry.getKey();
            for (Map.Entry<String, List<Map<String, String>>> smEntry : carrierEntry.getValue().entrySet()) {
                String shipMethod = smEntry.getKey();
                List<Map<String, String>> items = smEntry.getValue();

                generateXmlFile(type, carrier, shipMethod, items);
            }
        }
    }

    private void generateXmlFile(String type, String carrier, String shipMethod, List<Map<String, String>> items) throws ParserConfigurationException, TransformerException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        org.w3c.dom.Document doc = docBuilder.newDocument();
        org.w3c.dom.Element rootElement = doc.createElement("Items");
        doc.appendChild(rootElement);

        for (Map<String, String> item : items) {
            String tag = item.get("dropdown1");
            String description = item.get("label1");

            org.w3c.dom.Element tagElement = doc.createElement(tag);
            tagElement.appendChild(doc.createTextNode(description));
            rootElement.appendChild(tagElement);
        }

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource source = new DOMSource(doc);

        String fileName = String.format("%s_%s_%s.xml", type, carrier, shipMethod);
        StreamResult result = new StreamResult(new File(fileName));

        transformer.transform(source, result);
    }
}
