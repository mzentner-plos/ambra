/*
 * $HeadURL$
 * $Id$
 * Copyright (c) 2006-2012 by Public Library of Science http://plos.org http://ambraproject.org
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ambraproject.service.article;

import org.ambraproject.util.DocumentBuilderFactoryCreator;
import org.ambraproject.util.XPathUtil;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Alex Kudlick
 *         Date: 7/3/12
 */
public class AIArticleClassifier implements ArticleClassifier {

  private static final Logger log = LoggerFactory.getLogger(AIArticleClassifier.class);

  private static final String MESSAGE_BEGIN = "<TMMAI project='%s' location = '.'>\n" +
      "  <Method name='getSuggestedTermsFullPaths' returnType='java.util.Vector'/>\n" +
      "  <VectorParam>\n" +
      "    <VectorElement>";

  private static final String MESSAGE_END = "</VectorElement>\n" +
      "  </VectorParam>\n" +
      "</TMMAI>";


  private String serviceUrl;
  private String thesaurus;
  private HttpClient httpClient;

  @Required
  public void setServiceUrl(String serviceUrl) {
    this.serviceUrl = serviceUrl;
  }

  @Required
  public void setThesaurus(String thesaurus) {
    this.thesaurus = thesaurus;
  }

  @Required
  public void setHttpClient(HttpClient httpClient) {
    this.httpClient = httpClient;
  }

  @Override
  public List<String> classifyArticle(Document articleXml) throws Exception {
    String toCategorize = getCategorizationContent(articleXml);
    String aiMessage = String.format(MESSAGE_BEGIN, thesaurus) + toCategorize + MESSAGE_END;
    PostMethod post = new PostMethod(serviceUrl);
    post.setRequestEntity(new StringRequestEntity(aiMessage, "application/xml", "UTF-8"));
    httpClient.executeMethod(post);

    Document response = DocumentBuilderFactoryCreator.createFactory()
        .newDocumentBuilder().parse(post.getResponseBodyAsStream());

    //parse result
    NodeList vectorElements = response.getElementsByTagName("VectorElement");
    List<String> results = new ArrayList<String>(vectorElements.getLength());
    //The first and last elements of the vector response are just MAITERMS
    for (int i = 1; i < vectorElements.getLength() - 1; i++) {
      String term = parseVectorElement(vectorElements.item(i).getTextContent());

      // When the new taxonomy launched, we had a problem where lots of PLOS ONE
      // papers were being tagged with subcategories of
      // "/Earth sciences/Geography/Locations" (see Jira TAX-30).  So we're just
      // blacklisting this category for now.
      //
      // TODO: tweak the AI taxonomy server rulebase to make this unnecessary, and
      // remove the hack.
      if (term != null && !term.startsWith("/Earth sciences/Geography/Locations/")) {
        results.add(term);
      }
    }
    return results;
  }

  /**
   * Parses a single line of the XML response from the taxonomy server.
   *
   * @param vectorElement The text body of a line of the response
   * @return the term to be returned, or null if the line is not valid
   */
  static String parseVectorElement(String vectorElement) {
    String text = vectorElement.split("\\|")[0].replaceFirst("<TERM>", "");

    // There appears to be a bug in the AI getSuggestedTermsFullPath method.
    // It's supposed to return a slash-delimited path that starts with a slash,
    // like an absolute Unix file path.  However, rarely, it just returns "naked"
    // terms without the leading slash.  Discard these, since the calling
    // code won't be able to handle this.
    if (text.charAt(0) == '/') {
      return text;
    } else {
      return null;
    }
  }

  /**
   * Adds the text content of the given element to the StringBuilder, if it exists.
   * If more than one element exists with the given name, only appends the first one.
   *
   * @param sb StringBuilder to be modified
   * @param dom DOM tree of an article
   * @param elementName name of element to search for in the dom
   * @return true if the StringBuilder was modified
   */
  boolean appendElementIfExists(StringBuilder sb, Document dom, String elementName) {
    NodeList list = dom.getElementsByTagName(elementName);
    if (list != null && list.getLength() > 0) {
      sb.append(list.item(0).getTextContent());
      sb.append("\n");
      return true;
    } else {
      return false;
    }
  }

  /**
   * Appends a given section of the article, with one of the given titles, to the
   * StringBuilder passed in.  (Examples include "Results", "Materials and Methods",
   * "Discussion", etc.)
   *
   * @param sb StringBuilder to be modified
   * @param dom DOM tree of an article
   * @param sectionTitles list of titles to look for.  The first one found will be
   *     appended.
   * @return true if the StringBuilder was modified
   * @throws XPathException
   */
  boolean appendSectionIfExists(StringBuilder sb, Document dom, String... sectionTitles)
      throws XPathException {
    XPathUtil xPathUtil = new XPathUtil();
    for (String title : sectionTitles) {
      Node node = xPathUtil.selectSingleNode(dom,
          String.format("/article/body/sec[title='%s']", title));
      if (node != null) {
        sb.append(node.getTextContent());
        sb.append("\n");
        return true;
      }
    }
    return false;
  }

  /**
   * Returns a string containing only the parts of the article that should be examined
   * by the taxonomy server.  For research articles, this is presently the title, the
   * abstract, the Materials and Methods section, and the Results section.  (If any of
   * these sections are not present, they are not sent, but this is not a fatal error.)
   * If none of these sections (abstract, materials/methods, or results) are present,
   * then this method will return the entire body text.  This is usually the case for
   * non-research-articles, such as corrections, opinion pieces, etc.
   *
   * @param dom DOM tree of an article
   * @return raw text content, XML-escaped, of the relevant article sections
   * @throws TransformerException
   * @throws XPathException
   */
  String getCategorizationContent(Document dom) throws TransformerException, XPathException {
    StringBuilder sb = new StringBuilder();
    appendElementIfExists(sb, dom, "article-title");
    appendElementIfExists(sb, dom, "abstract");
    appendElementIfExists(sb, dom, "body");
    return StringEscapeUtils.escapeXml(sb.toString().trim());
  }

  // Utility main method and associated code useful for grabbing categories for individual
  // articles.
  // TODO: consider moving this somewhere else.

  private static final Pattern DOI_REGEX = Pattern.compile("(p[a-z]{3}\\.\\d{7})");

  private static final String XML_URL = "http://www.plosone.org/article/fetchObjectAttachment.action"
      + "?uri=info%%3Adoi%%2F10.1371%%2Fjournal.%s&representation=XML";

  /**
   * Returns the XML for an article.  Note that this fetches the article XML via a
   * web request to the live site, not using a filestore.
   *
   * @param doi doi specifying the article
   * @return String of the article XML, if found
   * @throws Exception
   */
  private static String fetchXml(String doi) throws Exception {
    URL url = new URL(String.format(XML_URL, doi));
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.connect();
    InputStream is = conn.getInputStream();
    String result = IOUtils.toString(is);
    is.close();
    return result;
  }

  /**
   * Main method that categorizes a single article, based on its DOI as input on
   * the command line.
   *
   * @param args
   * @throws Exception
   */
  public static void main(String... args) throws Exception {
    if (args.length != 2) {
      System.err.println("You must specify the thesaurus as the first argument, and the PLOS "
          + "article as the second.  You entered: " + Arrays.toString(args));
      System.exit(1);
    }

    Matcher matcher = DOI_REGEX.matcher(args[1]);
    matcher.find();
    String doi = matcher.group(1);
    if (doi != null) {
      String xml = fetchXml(doi);
      Document dom = DocumentBuilderFactoryCreator.createFactory().newDocumentBuilder().parse(
          new ByteArrayInputStream(xml.getBytes("utf-8")));
      AIArticleClassifier classifier = new AIArticleClassifier();
      System.out.println("Content to send to taxonomy server:");
      System.out.println("\n\n" + classifier.getCategorizationContent(dom) + "\n\n");

      classifier.setServiceUrl("http://tax.plos.org:9080/servlet/dh");
      classifier.setThesaurus(args[0].trim());
      classifier.setHttpClient(new HttpClient(new MultiThreadedHttpConnectionManager()));
      List<String> terms = classifier.classifyArticle(dom);
      System.out.println("\n\nTerms returned by taxonomy server:");
      for (String term : terms) {
        System.out.println(term);
      }
      System.out.println("\n\n");
    } else {
      System.out.println(args[1] + " is not a valid DOI");
      System.exit(1);
    }
  }
}
