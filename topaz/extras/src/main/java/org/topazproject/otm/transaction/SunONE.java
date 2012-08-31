/*
 * Copyright 2004-2006 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.topazproject.otm.transaction;

/**
 * TransactionManager lookup strategy for Sun ONE Application Server 7
 * 
 * @author kimchy
 */
public class SunONE extends JNDITransactionManagerLookup {
  protected String getName() {
    return "java:pm/TransactionManager";
  }

  public String getUserTransactionName() {
    return "java:comp/UserTransaction";
  }
}