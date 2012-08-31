/* $HeadURL::                                                                                     $
 * $Id$
 *
 * Copyright (c) 2006-2008 by Topaz, Inc.
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

import java.io.Serializable;
import java.net.URI;

import org.topazproject.otm.Rdf;
import org.topazproject.otm.annotations.Entity;
import org.topazproject.otm.annotations.GeneratedValue;
import org.topazproject.otm.annotations.Id;
import org.topazproject.otm.annotations.Predicate;

/**
 * An authentication id for a user's account. These are internal authentication tokens
 * sent to and received from the login servers.
 *
 * @author Ronald Tschalär
 */
@Entity(model = "users")
public class AuthenticationId implements Serializable {
  private static final String DEF_REALM = "local";

  @Id @GeneratedValue(uriPrefix = "id:authids/")
  private URI id;
  @Predicate(uri = Rdf.topaz + "realm")
  private String realm = DEF_REALM;
  @Predicate(uri = Rdf.rdf + "value")
  private String value;

  /** 
   * Create a new empty auth-id. 
   */
  public AuthenticationId() {
  }

  /** 
   * Create a new auth-id with the given value. 
   *
   * @param value the auth-id value
   */
  public AuthenticationId(String value) {
    this.value = value;
  }

  /**
   * @return the id
   */
  public URI getId() {
    return id;
  }

  /**
   * @param id the id to set
   */
  public void setId(URI id) {
    this.id = id;
  }

  /**
   * Get the realm.
   *
   * @return the realm.
   */
  public String getRealm() {
    return realm;
  }

  /**
   * Set the realm.
   *
   * @param realm the realm.
   */
  public void setRealm(String realm) {
    this.realm = realm;
  }

  /**
   * Get the value.
   *
   * @return the value.
   */
  public String getValue() {
    return value;
  }

  /**
   * Set the value.
   *
   * @param value the value.
   */
  public void setValue(String value) {
    this.value = value;
  }
}