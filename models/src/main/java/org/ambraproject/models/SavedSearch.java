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

package org.ambraproject.models;

import java.util.Calendar;
import java.util.Date;

/**
 * POJO Object to contain all the information about saved searches.
 * It is mapped with table savedSearch.
 */
public class SavedSearch extends AmbraEntity {

  private String searchName;
  private SavedSearchQuery searchQuery;
  private Date lastWeeklySearchTime;
  private Date lastMonthlySearchTime;
  private boolean weekly;
  private boolean monthly;
  private SavedSearchType searchType;

  public SavedSearch() {
    super();
    lastWeeklySearchTime = Calendar.getInstance().getTime();
    lastMonthlySearchTime= Calendar.getInstance().getTime();
  }

  public SavedSearch(String searchName, SavedSearchQuery searchQuery) {
    this();
    this.searchName = searchName;
    this.searchQuery = searchQuery;
  }
  
  /**
   * getter for search name
   * @return searchName
   */
  public String getSearchName() {
    return searchName;
  }

  /**
   * setter for search name
   * @param searchName
   */
  public void setSearchName(String searchName) {
    this.searchName = searchName;
  }

  public SavedSearchQuery getSearchQuery() {
    return searchQuery;
  }

  public void setSearchQuery(SavedSearchQuery searchQuery) {
    this.searchQuery = searchQuery;
  }

  public Date getLastWeeklySearchTime() {
    return lastWeeklySearchTime;
  }

  public void setLastWeeklySearchTime(Date lastWeeklySearchTime) {
    this.lastWeeklySearchTime = lastWeeklySearchTime;
  }

  public Date getLastMonthlySearchTime() {
    return lastMonthlySearchTime;
  }

  public void setLastMonthlySearchTime(Date lastMonthlySearchTime) {
    this.lastMonthlySearchTime = lastMonthlySearchTime;
  }

  /**
   * @return weekly
   */
  public boolean getWeekly() {
    return weekly;
  }

  /**
   * setter for weekly
   * @param weekly
   */
  public void setWeekly(boolean weekly) {
    this.weekly = weekly;
  }

  /**
   * @return monthly
   */
  public boolean getMonthly() {
    return monthly;
  }

  /**
   * setter for monthly
   * @param monthly
   */
  public void setMonthly(boolean monthly) {
    this.monthly = monthly;
  }

  public SavedSearchType getSearchType() {
    return searchType;
  }

  public void setSearchType(SavedSearchType searchType) {
    this.searchType = searchType;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    SavedSearch that = (SavedSearch) o;

    if (monthly != that.monthly) return false;
    if (weekly != that.weekly) return false;
    if (lastMonthlySearchTime != null ? !lastMonthlySearchTime.equals(that.lastMonthlySearchTime) : that.lastMonthlySearchTime != null)
      return false;
    if (lastWeeklySearchTime != null ? !lastWeeklySearchTime.equals(that.lastWeeklySearchTime) : that.lastWeeklySearchTime != null)
      return false;
    if (searchType != that.searchType) return false;
    if (searchName != null ? !searchName.equals(that.searchName) : that.searchName != null) return false;
    if (searchQuery != null ? !searchQuery.equals(that.searchQuery) : that.searchQuery != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = searchName != null ? searchName.hashCode() : 0;
    result = 31 * result + (searchQuery != null ? searchQuery.hashCode() : 0);
    result = 31 * result + (lastWeeklySearchTime != null ? lastWeeklySearchTime.hashCode() : 0);
    result = 31 * result + (lastMonthlySearchTime != null ? lastMonthlySearchTime.hashCode() : 0);
    result = 31 * result + (weekly ? 1 : 0);
    result = 31 * result + (monthly ? 1 : 0);
    result = 31 * result + (searchType != null ? searchType.hashCode() : 0);
    return result;
  }
}
