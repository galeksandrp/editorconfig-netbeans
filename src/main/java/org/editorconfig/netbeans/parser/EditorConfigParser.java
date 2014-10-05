package org.editorconfig.netbeans.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EditorConfigParser {

  private static final Logger LOG = Logger.getLogger(EditorConfigParser.class.getName());

  public EditorConfigParser() {
  }

  public Map<String, List<EditorConfigProperty>> parseConfig(URL resource) {
    Map<String, List<EditorConfigProperty>> result = new HashMap<>();
    String section = null;

    File file = new File(resource.getFile());
    String line;

    try (
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            BufferedReader br = new BufferedReader(isr)) {
      while ((line = br.readLine()) != null) {
        boolean isInteresting = !(line.startsWith("#") || line.isEmpty());

        if (isInteresting) {
          if (line.startsWith("[")) {
            // RegEx
            String regex = line.substring(1, line.lastIndexOf("]"));
            // TODO: Convert RegEx to Java compliant RegEx
            section = regex;
          } else if (section != null) {
            // Key / Value
            String[] splitted = line.split("=");
            String key = splitted[0].trim();
            String value = splitted[1].trim();
            // TODO: Save Key / Value pairs together with RegEx in result set
            List<EditorConfigProperty> properties = result.get(section);

            if (properties == null) {
              properties = new ArrayList<>();
              result.put(section, properties);
            }

            properties.add(new EditorConfigProperty(key, value));
          }
        }
      }
    } catch (IOException ex) {
      LOG.log(Level.SEVERE, "Error reading file: {0}", ex.getMessage());
    }

    return result;
  }

  public void printConfig(Map<String, List<EditorConfigProperty>> config) {
    for (String section : config.keySet()) {
      System.out.println("Section: " + section);
      List<EditorConfigProperty> properties = config.get(section);
      for (EditorConfigProperty property : properties) {
        String output = String.format("\t%s: %s", property.getKey(), property.getValue());
        System.out.println(output);
      }
    }
  }
}
