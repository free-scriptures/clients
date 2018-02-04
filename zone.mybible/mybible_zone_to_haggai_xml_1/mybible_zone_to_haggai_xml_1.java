/* Copyright (C) 2018  Stephan Kreutzer
 *
 * This file is part of mybible_zone_to_haggai_xml_1.
 *
 * mybible_zone_to_haggai_xml_1 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License version 3 or any later version,
 * as published by the Free Software Foundation.
 *
 * mybible_zone_to_haggai_xml_1 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License 3 for more details.
 *
 * You should have received a copy of the GNU Affero General Public License 3
 * along with mybible_zone_to_haggai_xml_1. If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * @file $/mybible_zone_to_haggai_xml/mybible_zone_to_haggai_xml_1.java
 * @brief Converts the XML version of the CSV export of a SQLite bible module
 *     from mybible.zone to Haggai XML.
 * @author Stephan Kreutzer
 * @since 2018-02-03
 */



import java.io.File;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.xml.stream.XMLInputFactory;
import java.io.InputStream;
import java.io.FileInputStream;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.Attribute;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;



public class mybible_zone_to_haggai_xml_1
{
    public static void main(String args[])
    {
        System.out.print("mybible_zone_to_haggai_xml_1  Copyright (C) 2018  Stephan Kreutzer\n" +
                         "This program comes with ABSOLUTELY NO WARRANTY.\n" +
                         "This is free software, and you are welcome to redistribute it\n" +
                         "under certain conditions. See the GNU Affero General Public\n" +
                         "License 3 or any later version for details. Also, see the source code\n" +
                         "repository https://github.com/free-scriptures/clients/\n" +
                         "and the project website http://www.free-scriptures.org.\n\n");

        if (args.length < 2)
        {
            System.out.print("Usage:\n" +
                             "\tmybible_zone_to_haggai_xml_1 in-xml-file out-haggai-file\n\n");

            System.exit(1);
        }


        File xmlFile = new File(args[0]);

        if (xmlFile.exists() != true)
        {
            System.out.println("mybible_zone_to_haggai_xml_1: Input XML file '" + xmlFile.getAbsolutePath() + "' doesn't exist.");
            System.exit(-1);
        }

        if (xmlFile.isFile() != true)
        {
            System.out.println("mybible_zone_to_haggai_xml_1: Input XML path '" + xmlFile.getAbsolutePath() + "' isn't a file.");
            System.exit(-1);
        }

        if (xmlFile.canRead() != true)
        {
            System.out.println("mybible_zone_to_haggai_xml_1: Input XML file '" + xmlFile.getAbsolutePath() + "' isn't readable.");
            System.exit(-1);
        }

        File haggaiFile = new File(args[1]);

        if (haggaiFile.exists() == true)
        {
            if (haggaiFile.isFile() == true)
            {
                if (haggaiFile.canWrite() != true)
                {
                    System.out.println("mybible_zone_to_haggai_xml_1: Output Haggai XML file '" + haggaiFile.getAbsolutePath() + "' isn't writable.");
                    System.exit(-1);
                }
            }
            else
            {
                System.out.println("mybible_zone_to_haggai_xml_1: Output Haggai XML path '" + haggaiFile.getAbsolutePath() + "' exists, but isn't a file.");
                System.exit(-1);
            }
        }

        String todayDate = "2018-02-03";

        {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            todayDate = dateFormat.format(new Date());
        }

        try
        {
            BufferedWriter writer = new BufferedWriter(
                                    new OutputStreamWriter(
                                    new FileOutputStream(haggaiFile),
                                    "UTF-8"));

            try
            {
                writer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
                writer.append("<!-- This file was created by mybible_zone_to_haggai_xml_1, which is free software licensed under the GNU Affero General Public License 3 or any later version (see https://github.com/free-scriptures/clients/ and http://www.free-scriptures.org). -->\n");
                writer.append("<XMLBIBLE xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"haggai_20130620.xsd\" biblename=\"\" status=\"\" version=\"haggai_20130620\" revision=\"0\">\n");
                writer.append("  <INFORMATION>\n");
                writer.append("    <title></title>\n");
                writer.append("    <creator></creator>\n");
                writer.append("    <description></description>\n");
                writer.append("    <publisher></publisher>\n");
                writer.append("    <contributor></contributor>\n");
                writer.append("    <date>" + todayDate + "</date>\n");
                writer.append("    <type>Text</type>\n");
                writer.append("    <format>Haggai XML Bible Markup Language</format>\n");
                writer.append("    <identifier></identifier>\n");
                writer.append("    <source></source>\n");
                writer.append("    <language></language>\n");
                writer.append("    <coverage></coverage>\n");
                writer.append("    <rights></rights>\n");
                writer.append("  </INFORMATION>\n");

                String bookNumber = null;
                String chapterNumber = null;
                String verseNumber = null;
                StringBuilder sbText = null;

                String bookNumberPrevious = null;
                String chapterNumberPrevious = null;

                try
                {
                    XMLInputFactory inputFactory = XMLInputFactory.newInstance();
                    InputStream in = new FileInputStream(xmlFile);
                    XMLEventReader eventReader = inputFactory.createXMLEventReader(in);

                    while (eventReader.hasNext() == true)
                    {
                        XMLEvent event = eventReader.nextEvent();

                        if (event.isStartElement() == true)
                        {
                            String tagName = event.asStartElement().getName().getLocalPart();

                            if (tagName.equals("verse") == true)
                            {
                                if (bookNumber != null)
                                {
                                    System.out.println("mybible_zone_to_haggai_xml_1: Entered a new verse and bookNumber was still set from the previous verse.");
                                    System.exit(-1);
                                }

                                if (chapterNumber != null)
                                {
                                    System.out.println("mybible_zone_to_haggai_xml_1: Entered a new verse and chapterNumber was still set from the previous verse.");
                                    System.exit(-1);
                                }

                                if (verseNumber != null)
                                {
                                    System.out.println("mybible_zone_to_haggai_xml_1: Entered a new verse and verseNumber was still set from the previous verse.");
                                    System.exit(-1);
                                }

                                if (sbText != null)
                                {
                                    System.out.println("mybible_zone_to_haggai_xml_1: Entered a new verse and sbText was still set from the previous verse.");
                                    System.exit(-1);
                                }
                            }
                            else if (tagName.equals("book-number") == true)
                            {
                                if (bookNumber != null)
                                {
                                    System.out.println("mybible_zone_to_haggai_xml_1: bookNumber is already set.");
                                    System.exit(-1);
                                }

                                bookNumber = new String();

                                while (eventReader.hasNext() == true)
                                {
                                    event = eventReader.nextEvent();

                                    if (event.isCharacters() == true)
                                    {
                                        bookNumber += event.asCharacters().getData();
                                    }
                                    else if (event.isEndElement() == true)
                                    {
                                        if (event.asEndElement().getName().getLocalPart().equals("book-number") == true)
                                        {
                                            break;
                                        }
                                    }
                                }
                            }
                            else if (tagName.equals("chapter-number") == true)
                            {
                                if (chapterNumber != null)
                                {
                                    System.out.println("mybible_zone_to_haggai_xml_1: chapterNumber is already set.");
                                    System.exit(-1);
                                }

                                chapterNumber = new String();

                                while (eventReader.hasNext() == true)
                                {
                                    event = eventReader.nextEvent();

                                    if (event.isCharacters() == true)
                                    {
                                        chapterNumber += event.asCharacters().getData();
                                    }
                                    else if (event.isEndElement() == true)
                                    {
                                        if (event.asEndElement().getName().getLocalPart().equals("chapter-number") == true)
                                        {
                                            break;
                                        }
                                    }
                                }
                            }
                            else if (tagName.equals("verse-number") == true)
                            {
                                if (verseNumber != null)
                                {
                                    System.out.println("mybible_zone_to_haggai_xml_1: verseNumber is already set.");
                                    System.exit(-1);
                                }

                                verseNumber = new String();

                                while (eventReader.hasNext() == true)
                                {
                                    event = eventReader.nextEvent();

                                    if (event.isCharacters() == true)
                                    {
                                        verseNumber += event.asCharacters().getData();
                                    }
                                    else if (event.isEndElement() == true)
                                    {
                                        if (event.asEndElement().getName().getLocalPart().equals("verse-number") == true)
                                        {
                                            break;
                                        }
                                    }
                                }
                            }
                            else if (tagName.equals("text") == true)
                            {
                                if (sbText != null)
                                {
                                    System.out.println("mybible_zone_to_haggai_xml_1: sbText is already set.");
                                    System.exit(-1);
                                }

                                sbText = new StringBuilder();

                                while (eventReader.hasNext() == true)
                                {
                                    event = eventReader.nextEvent();

                                    if (event.isCharacters() == true)
                                    {
                                        sbText.append(event.asCharacters().getData());
                                    }
                                    else if (event.isStartElement() == true)
                                    {
                                        String tagName2 = event.asStartElement().getName().getLocalPart();

                                        if (tagName2.equals("text") == true)
                                        {
                                            System.out.println("mybible_zone_to_haggai_xml_1: Text element in text element.");
                                            System.exit(-1);
                                        }
                                        else if (tagName2.equals("S") == true)
                                        {
                                            Attribute attributeNumber = event.asStartElement().getAttributeByName(new QName("number"));

                                            if (attributeNumber != null)
                                            {
                                                sbText.append("<GRAM str=\"" + attributeNumber.getValue() + "\">");
                                            }
                                            else
                                            {
                                                System.out.println("mybible_zone_to_haggai_xml_1: 'S' element is missing the 'number' attribute.");
                                                System.exit(-1);
                                            }
                                        }
                                    }
                                    else if (event.isEndElement() == true)
                                    {
                                        String tagName2 = event.asEndElement().getName().getLocalPart();

                                        if (tagName2.equals("text") == true)
                                        {
                                            break;
                                        }
                                        else if (tagName2.equals("S") == true)
                                        {
                                            sbText.append("</GRAM>");
                                        }
                                    }
                                }
                            }
                        }
                        else if (event.isEndElement() == true)
                        {
                            if (event.asEndElement().getName().getLocalPart().equals("verse") == true)
                            {
                                if (bookNumber == null)
                                {
                                    System.out.println("mybible_zone_to_haggai_xml_1: bookNumber isn't set at end of verse.");
                                    System.exit(-1);
                                }

                                if (chapterNumber == null)
                                {
                                    System.out.println("mybible_zone_to_haggai_xml_1: chapterNumber isn't set at end of verse.");
                                    System.exit(-1);
                                }

                                if (verseNumber == null)
                                {
                                    System.out.println("mybible_zone_to_haggai_xml_1: verseNumber isn't set at end of verse.");
                                    System.exit(-1);
                                }

                                if (sbText == null)
                                {
                                    System.out.println("mybible_zone_to_haggai_xml_1: sbText isn't set at end of verse.");
                                    System.exit(-1);
                                }

                                if (bookNumberPrevious != null &&
                                    !bookNumberPrevious.equals(bookNumber))
                                {
                                    writer.append("  </BIBLEBOOK>\n");
                                }

                                if (!bookNumber.equals(bookNumberPrevious))
                                {
                                    writer.append("  <BIBLEBOOK bnumber=\"" + bookNumber + "\" bname=\"\">\n");
                                }

                                bookNumberPrevious = bookNumber;

                                if (chapterNumberPrevious != null &&
                                    !chapterNumberPrevious.equals(chapterNumber))
                                {
                                    writer.append("    </CHAPTER>\n");
                                }

                                if (!chapterNumber.equals(chapterNumberPrevious))
                                {
                                    writer.append("    <CHAPTER cnumber=\"" + chapterNumber + "\">\n");
                                }

                                chapterNumberPrevious = chapterNumber;

                                writer.append("      <VERSE vnumber=\"" + verseNumber + "\">" + sbText.toString() + "</VERSE>\n");

                                bookNumber = null;
                                chapterNumber = null;
                                verseNumber = null;
                                sbText = null;
                            }
                        }
                    }
                }
                catch (XMLStreamException ex)
                {
                    System.out.println("mybible_zone_to_haggai_xml_1: An error occurred while reading input XML file '" + xmlFile.getAbsolutePath() + "'.");
                    System.exit(-1);
                }
                catch (SecurityException ex)
                {
                    System.out.println("mybible_zone_to_haggai_xml_1: An error occurred while reading input XML file '" + xmlFile.getAbsolutePath() + "'.");
                    System.exit(-1);
                }
                catch (IOException ex)
                {
                    System.out.println("mybible_zone_to_haggai_xml_1: An error occurred while reading input XML file '" + xmlFile.getAbsolutePath() + "'.");
                    System.exit(-1);
                }

                if (chapterNumberPrevious != null)
                {
                    writer.append("    </CHAPTER>\n");
                }

                if (bookNumberPrevious != null)
                {
                    writer.append("  </BIBLEBOOK>\n");
                }

                writer.append("</XMLBIBLE>\n");
            }
            finally
            {
                writer.close();
            }
        }
        catch (FileNotFoundException ex)
        {
            System.out.println("mybible_zone_to_haggai_xml_1: An error occurred while writing output Haggai XML file '" + haggaiFile.getAbsolutePath() + "'.");
            System.exit(-1);
        }
        catch (UnsupportedEncodingException ex)
        {
            System.out.println("mybible_zone_to_haggai_xml_1: An error occurred while writing output Haggai XML file '" + haggaiFile.getAbsolutePath() + "'.");
            System.exit(-1);
        }
        catch (IOException ex)
        {
            System.out.println("mybible_zone_to_haggai_xml_1: An error occurred while writing output Haggai XML file '" + haggaiFile.getAbsolutePath() + "'.");
            System.exit(-1);
        }
    }
}
