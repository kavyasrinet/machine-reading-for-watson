package edu.cmu.lti.oaqa.reader;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Di Wang.
 */
public class MannerXmlReader {

  public static void main(String[] args) throws XMLStreamException, FileNotFoundException {

    String filePath = "../data/manner-v2.0/manner.xml";

    List<MannerQASet> QASetList = null;
    MannerQASet currQASet = null;
    String tagContent = null;

    XMLInputFactory factory = XMLInputFactory.newInstance();
    XMLStreamReader reader = factory.createXMLStreamReader(new FileReader(filePath));

    while (reader.hasNext()) {
      int event = reader.next();
      switch (event) {

        case XMLStreamConstants.START_ELEMENT:
          if ("vespaadd".equals(reader.getLocalName())) {
            currQASet = new MannerQASet();
          }
          if ("ystfeed".equals(reader.getLocalName())) {
            QASetList = new ArrayList<>();
          }
          break;

        case XMLStreamConstants.CHARACTERS:
          tagContent = reader.getText().trim();
          break;

        case XMLStreamConstants.END_ELEMENT:
          switch (reader.getLocalName()) {
            case "vespaadd":
              QASetList.add(currQASet);
              break;
            case "subject":
              currQASet.subject = tagContent;
              break;
            case "content":
              currQASet.content = tagContent;
            case "bestAnswer":
              currQASet.bestAnswer = tagContent;
              break;
          }
          break;
      }
    }

    System.out.println(QASetList.size());
  }
}

class MannerQASet {
  String subject;

  String content;

  String bestAnswer;

  //cat maincat subcat

  @Override
  public String toString() {
    return "QASet{" +
            "subject='" + subject + '\'' +
            ", content='" + content + '\'' +
            ", bestAnswer='" + bestAnswer + '\'' +
            '}';
  }
}
