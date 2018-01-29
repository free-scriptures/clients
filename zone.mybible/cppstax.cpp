/* Copyright (C) 2017-2018 Stephan Kreutzer
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License version 3 or any later
 * version of the license, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License 3 for more details.
 *
 * You should have received a copy of the GNU Affero General Public License 3
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * @author Stephan Kreutzer
 * @since 2018-01-29
 */

#include "XMLInputFactory.h"
#include <memory>
#include <iostream>
#include <fstream>
#include <locale>

typedef std::unique_ptr<cppstax::XMLEventReader> XMLEventReader;
typedef std::unique_ptr<cppstax::XMLEvent> XMLEvent;

int Run(std::istream& aStream);
int HandleStartElement(const cppstax::StartElement& aElement, const cppstax::QName& aName);
int HandleTextElement(cppstax::XMLEventReader& aReader);
int HandleStrongElement(cppstax::XMLEventReader& aReader, std::string& strNumber);



int main(int argc, char* argv[])
{
    std::cout << "Copyright (C) 2017-2018 Stephan Kreutzer\n"
              << "This program comes with ABSOLUTELY NO WARRANTY.\n"
              << "This is free software, and you are welcome to redistribute it\n"
              << "under certain conditions. See the GNU Affero General Public License 3\n"
              << "or any later version for details. Also, see the source code repository\n"
              << "https://github.com/publishing-systems/CppStAX/ and\n"
              << "the project website http://www.publishing-systems.org.\n"
              << std::endl;

    std::unique_ptr<std::ifstream> pStream = nullptr;

    try
    {
        if (argc >= 2)
        {
            pStream = std::unique_ptr<std::ifstream>(new std::ifstream);
            pStream->open(argv[1]);

            if (pStream->is_open() != true)
            {
                std::cout << "Couldn't open input file '" << argv[1] << "'.";
                return -1;
            }

            Run(*pStream);

            pStream->close();
        }
        else
        {
            std::cout << "Usage:\tcppstax <input-file>\n" << std::endl;
            return 0;
        }
    }
    catch (std::exception* pException)
    {
        std::cout << "Exception: " << pException->what() << std::endl;

        if (pStream != nullptr)
        {
            if (pStream->is_open() == true)
            {
                pStream->close();
            }
        }

        return -1;
    }

    return 0;
}

int Run(std::istream& aStream)
{
    std::cout << "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

    cppstax::XMLInputFactory aFactory;
    XMLEventReader pReader = aFactory.createXMLEventReader(aStream);

    while (pReader->hasNext() == true)
    {
        XMLEvent pEvent = pReader->nextEvent();

        if (pEvent->isStartElement() == true)
        {
            cppstax::StartElement& aStartElement = pEvent->asStartElement();
            const cppstax::QName& aName = aStartElement.getName();
            std::string strName(aName.getLocalPart());

            if (strName == "text")
            {
                HandleTextElement(*pReader);
            }
            else
            {
                HandleStartElement(aStartElement, aName);
            }
        }
        else if (pEvent->isEndElement() == true)
        {
            cppstax::EndElement& aEndElement = pEvent->asEndElement();
            cppstax::QName aName = aEndElement.getName();

            std::string strName("</");
            std::string strPrefix(aName.getPrefix());

            if (!strPrefix.empty())
            {
                strName += strPrefix;
                strName += ":";
            }

            strName += aName.getLocalPart();
            strName += ">";

            std::cout << strName;
        }
        else if (pEvent->isCharacters() == true)
        {
            cppstax::Characters& aCharacters = pEvent->asCharacters();
            const std::string& strCharacters(aCharacters.getData());

            for (std::string::const_iterator iter = strCharacters.begin();
                 iter != strCharacters.end();
                 iter++)
            {
                switch (*iter)
                {
                case '&':
                    std::cout << "&amp;";
                    break;
                case '<':
                    std::cout << "&lt;";
                    break;
                case '>':
                    std::cout << "&gt;";
                    break;
                default:
                    std::cout << *iter;
                    break;
                }
            }
        }
        else if (pEvent->isComment() == true)
        {
            std::cout << "<!--" << pEvent->asComment().getText() << "-->";
        }
        else if (pEvent->isProcessingInstruction() == true)
        {
            const cppstax::ProcessingInstruction& aPI = pEvent->asProcessingInstruction();

            std::cout << "<?" << aPI.getTarget() << " " << aPI.getData() << "?>";
        }
        else
        {

        }
    }

    return 0;
}

int HandleStartElement(const cppstax::StartElement& aElement, const cppstax::QName& aName)
{
    std::string strTag("<");
    std::string strPrefix(aName.getPrefix());

    if (!strPrefix.empty())
    {
        strTag += strPrefix;
        strTag += ":";
    }

    strTag += aName.getLocalPart();

    for (std::list<std::shared_ptr<cppstax::Attribute>>::iterator iter = aElement.getAttributes()->begin();
         iter != aElement.getAttributes()->end();
         iter++)
    {
        const cppstax::QName& aAttributeName = (*iter)->getName();

        strTag += " ";

        std::string strAttributePrefix(aAttributeName.getPrefix());

        if (!strAttributePrefix.empty())
        {
            strTag += strAttributePrefix;
            strTag += ":";
        }

        strTag += aAttributeName.getLocalPart();
        strTag += "=\"";

        const std::string& strCharacters((*iter)->getValue());

        for (std::string::const_iterator iter = strCharacters.begin();
            iter != strCharacters.end();
            iter++)
        {
            switch (*iter)
            {
            case '\"':
                strTag += "&quot;";
                break;
            default:
                strTag += *iter;
                break;
            }
        }

        strTag += "\"";
    }

    strTag += ">";

    std::cout << strTag;

    return 0;
}

int HandleTextElement(cppstax::XMLEventReader& aReader)
{
    std::cout << "<text>";

    std::locale aLocale;
    std::string strBuffer;

    while (aReader.hasNext() == true)
    {
        XMLEvent pEvent = aReader.nextEvent();

        if (pEvent->isCharacters() == true)
        {
            cppstax::Characters& aCharacters = pEvent->asCharacters();
            const std::string& strCharacters(aCharacters.getData());

            for (std::string::const_iterator iter = strCharacters.begin();
                 iter != strCharacters.end();
                 iter++)
            {
                switch (*iter)
                {
                case '&':
                    strBuffer += "&amp;";
                    break;
                case '<':
                    strBuffer += "&lt;";
                    break;
                case '>':
                    strBuffer += "&gt;";
                    break;
                default:
                    strBuffer += *iter;
                    break;
                }
            }
        }
        else if (pEvent->isStartElement() == true)
        {
            cppstax::StartElement& aStartElement = pEvent->asStartElement();
            const cppstax::QName& aName = aStartElement.getName();
            std::string strName(aName.getLocalPart());

            if (strName == "S")
            {
                std::string strongNumber;

                HandleStrongElement(aReader, strongNumber);

                if (!strBuffer.empty())
                {
                    for (int i = strBuffer.length(); i >= 0; i--)
                    {
                        if (i == 0)
                        {
                            break;
                        }

                        if (std::isspace(strBuffer.at(i - 1), aLocale) != 0)
                        {
                            std::cout << strBuffer.substr(0, i - 1) << strBuffer.at(i - 1);
                            strBuffer = strBuffer.substr(i);
                            break;
                        }
                    }

                    std::cout << "<S number=\"" << strongNumber << "\">" << strBuffer << "</S>";
                    strBuffer.clear();
                }
                else
                {
                    throw new std::runtime_error("Strong number found, but text buffer is empty!");
                }
            }
            else
            {
                HandleStartElement(aStartElement, aName);
            }
        }
        else if (pEvent->isEndElement() == true)
        {
            cppstax::EndElement& aEndElement = pEvent->asEndElement();
            cppstax::QName aName = aEndElement.getName();
            std::string strName(aName.getLocalPart());

            if (strName == "text")
            {
                break;
            }
            else
            {
                std::string strName("</");
                std::string strPrefix(aName.getPrefix());

                if (!strPrefix.empty())
                {
                    strName += strPrefix;
                    strName += ":";
                }

                strName += strName;
                strName += ">";

                std::cout << strName;
            }
        }
        else if (pEvent->isComment() == true)
        {
            std::cout << "<!--" << pEvent->asComment().getText() << "-->";
        }
        else if (pEvent->isProcessingInstruction() == true)
        {
            const cppstax::ProcessingInstruction& aPI = pEvent->asProcessingInstruction();

            std::cout << "<?" << aPI.getTarget() << " " << aPI.getData() << "?>";
        }
        else
        {

        }
    }

    std::cout << "</text>";

    return 0;
}

int HandleStrongElement(cppstax::XMLEventReader& aReader, std::string& strNumber)
{
    while (aReader.hasNext() == true)
    {
        XMLEvent pEvent = aReader.nextEvent();

        if (pEvent->isCharacters() == true)
        {
            cppstax::Characters& aCharacters = pEvent->asCharacters();
            const std::string& strCharacters(aCharacters.getData());

            for (std::string::const_iterator iter = strCharacters.begin();
                 iter != strCharacters.end();
                 iter++)
            {
                switch (*iter)
                {
                case '&':
                    strNumber += "&amp;";
                    break;
                case '<':
                    strNumber += "&lt;";
                    break;
                case '>':
                    strNumber += "&gt;";
                    break;
                default:
                    strNumber += *iter;
                    break;
                }
            }
        }
        else if (pEvent->isEndElement() == true)
        {
            cppstax::EndElement& aEndElement = pEvent->asEndElement();
            cppstax::QName aName = aEndElement.getName();

            if (aName.getLocalPart() == "S")
            {
                return 0;
            }
        }
        else if (pEvent->isComment() == true)
        {
            strNumber += "<!--";
            strNumber += pEvent->asComment().getText();
            strNumber += "-->";
        }
        else if (pEvent->isProcessingInstruction() == true)
        {
            const cppstax::ProcessingInstruction& aPI = pEvent->asProcessingInstruction();

            strNumber += "<?";
            strNumber += aPI.getTarget();
            strNumber += " ";
            strNumber += aPI.getData();
            strNumber += "?>";
        }
        else
        {

        }
    }

    return 0;
}
