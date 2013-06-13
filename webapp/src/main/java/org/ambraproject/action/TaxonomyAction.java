/*
 * Copyright (c) 2006-2013 by Public Library of Science http://plos.org http://ambraproject.org
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ambraproject.action;

import org.ambraproject.service.taxonomy.TaxonomyService;
import org.ambraproject.util.CategoryUtils;
import org.ambraproject.views.CategoryView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

/**
 * Action class for displaying a list of all top-level and second-level categories
 * in the taxonomy associated with any PLOS ONE article.
 *
 * @author John Callaway
 * @author Joe Osowski
 */
public class TaxonomyAction extends BaseActionSupport {

  private static final Logger log = LoggerFactory.getLogger(TaxonomyAction.class);

  private Map<String, List<String>> topAndSecondLevelCategories;
  private CategoryView categories;
  private TaxonomyService taxonomyService;
  private String root;
  private String journal;
  private String[] filter;

  @Override
  public String execute() throws Exception {
    //topAndSecondLevelCategories defaults to current journal
    topAndSecondLevelCategories = taxonomyService.parseTopAndSecondLevelCategories(getCurrentJournal());

    //categories defaults to all journals (the categories journal can be set via parameter)
    categories = taxonomyService.parseCategories(this.journal);

    return SUCCESS;
  }

  /**
   * @return a map from top-level category to list of second-level subcategories
   *     associated with that top-level category
   */
  public Map<String, List<String>> getTopAndSecondLevelCategories() {
    return topAndSecondLevelCategories;
  }

  @Required
  public void setTaxonomyService(TaxonomyService taxonomyService) {
    this.taxonomyService = taxonomyService;
  }

  /**
   * Returns a set of categories and the count of children
   *
   * Note: This is will return a subset of categories post filter applied.
   *
   * If there is no filter, the set returned will be the root nodes
   * If "/Biotech" is set as the filter, the set returned will have "/BioTech" as the root node.
   * If "/Biotech/Genetics" is set as the filter, the set returned will have "/BioTech/Genetics" as the root node.
   *
   * @return A map of categories
   */
  @SuppressWarnings("unchecked")
  public Map<String, SortedSet<String>> getCategories() {
    //Should probably implement this in the setter if the getter ever starts to get
    //called more then once

    if(this.root == null) {
      return CategoryUtils.getShortTree(categories);
    }

    //Ignore first slash if it exists
    if(this.root.trim().length() > 0 && this.root.trim().charAt(0) == '/') {
      this.root = this.root.trim().substring(1);
    }

    if(this.root.trim().length() == 0) {
      return CategoryUtils.getShortTree(categories);
    } else {
      String[] levels = this.root.split("/");
      CategoryView res = categories;

      for(String level : levels) {
        res = res.getChild(level);
      }

      return CategoryUtils.getShortTree(res);
    }
  }

  /**
   * We never want to return the entire map as it is too large.  However,
   * if filters are defined, we can return a subset
   *
   * @return
   */
  public CategoryView getMap() {
    //Should probably implement this in the setter if the getter ever starts to get
    //called more then once
    if(this.filter != null && this.filter.length > 0) {
      for(String filter : this.filter) {
        if(filter.length() < 3) {
          addActionError("All filters defined must be at least 3 characters long");
        }
      }

      if(getActionErrors().size() == 0) {
        CategoryView cv = CategoryUtils.filterMap(categories, this.filter);
        return cv;
      }
    }

    return null;
  }

  /**
   * Set the root for what categories are to be returned
   */
  public void setFilter(String[] filter) {
    this.filter = filter;
  }

  /**
   * Set the root for what categories are to be returned
   */
  public void setRoot(String root) {
    this.root = root;
  }

  /**
   * Set the journal for what categories are to be returned
   */
  public void setJournal(String journal) {
    this.journal = journal;
  }
}
