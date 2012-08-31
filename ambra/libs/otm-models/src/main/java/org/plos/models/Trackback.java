/* $HeadURL::                                                                            $
 * $Id$
 *
 * Copyright (c) 2007-2008 by Topaz, Inc.
 * http://topazproject.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.plos.models;

import java.net.URL;

import org.topazproject.otm.Rdf;
import org.topazproject.otm.annotations.Entity;
import org.topazproject.otm.annotations.Predicate;

/**
 * Represents a trackback on a resource.
 *
 * @author Stephen Cheng
 */
@Entity(type = Rdf.topaz + "TrackbackAnnotation")
public class Trackback extends Annotation {
  private static final long serialVersionUID = -1224471659454172666L;

  @Predicate(uri = Annotea.W3C_NS + "body")
  private TrackbackContent body;

  /**
   * @return Returns the body.
   */
  public TrackbackContent getBody() {
    return body;
  }

  /**
   * @param body The body to set.
   */
  public void setBody(TrackbackContent body) {
    this.body = body;
  }

  /**
   * @return Returns the url.
   */
  public URL getUrl() {
    if (body != null)
      return body.getUrl();
    return null;
  }

  /**
   * @return Returns the title.
   */
  public String getTitle() {
    if (body != null)
      return body.getTitle();
    return "";
  }

  /**
   * @return Returns the excerpt.
   */
  public String getExcerpt() {
    if (body != null)
      return body.getExcerpt();
    return "";
  }

  /**
   * @return Returns the blog_name.
   */
  public String getBlog_name() {
    if (body != null)
      return body.getBlog_name();
    return "";
  }

}