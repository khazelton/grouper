/*
  Copyright 2004-2005 University Corporation for Advanced Internet Development, Inc.
  Copyright 2004-2005 The University Of Chicago

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/

package edu.internet2.middleware.grouper;

import java.io.Serializable;
import java.util.*;
import org.apache.commons.lang.builder.*;


/** 
 * A namespace within the Groups Registry.
 * <p />
 * @author  blair christensen.
 * @version $Id: Stem.java,v 1.1.2.6 2005-10-20 02:32:28 blair Exp $
 *     
*/
public class Stem implements Serializable {

  // Hibernate Properties
  private String  id;
  private Set     child_groups;
  private Set     child_stems;
  private String  create_source;
  private Date    create_time;
  private Member  creator_id;
  private String  description;
  private String  display_extension;
  private String  display_name;
  private String  extension;
  private Member  modifier_id;
  private String  modify_source;
  private Date    modify_time;
  private String  name;
  private Stem    parent_stem;
  private String  uuid;
  private Integer version;

  // Constructors

  /**
   * Default constructor for Hibernate.
   */
  public Stem() {
    // Nothing
  }

  // Public Instance Methods

  /**
   * Add a new group to the registry.
   * <pre class="eg">
   * // Add a group with the extension "edu" beneath this stem.
   * try {
   *   Group edu = ns.addChildGroup("edu", "edu domain");
   * }
   * catch (GroupAddException e) {
   *   // Group not added
   * }
   * </pre>
   * @param   extension         Group's extension
   * @param   displayExtension  Groups' displayExtension
   * @return  The added {@link Group}
   * @throws  GroupAddException 
   */
  public Group addChildGroup(String extension, String displayExtension) 
    throws GroupAddException 
  {
    throw new RuntimeException("Not implemented");
  }

  /**
   * Add a new stem to the registry.
   * <pre class="eg">
   * // Add a stem with the extension "edu" beneath this stem.
   * try {
   *   Stem edu = ns.addChildStem("edu", "edu domain");
   * }
   * catch (StemAddException e) {
   *   // Stem not added
   * }
   * </pre>
   * @param   extension         Stem's extension
   * @param   displayExtension  Stem' displayExtension
   * @return  The added {@link Stem}
   * @throws  StemAddException 
   */
  public Stem addChildStem(String extension, String displayExtension) 
    throws StemAddException 
  {
    throw new RuntimeException("Not implemented");
  }

  /**
   * Get child groups of this stem.
   * <pre class="eg">
   * // Get child groups 
   * Set childGroups = ns.getChildGroups();
   * </pre>
   * @return  Set of {@link Group} objects
   */
  public Set getChildGroups() {
    throw new RuntimeException("Not implemented");
  }

  /**
   * Get child stems of this stem.
   * <pre class="eg">
   * // Get child stems 
   * Set childStems = ns.getChildStems();
   * </pre>
   * @return  Set of {@link Stem} objects
   */
  public Set getChildStems() {
    throw new RuntimeException("Not implemented");
  }

  /**
   * Get (optional and questionable) create source for this stem.
   * <pre class="eg">
   * // Get create source
`  * String source = ns.getCreateSource();
   * </pre>
   * @return  Create source for this stem.
   */
  public String getCreateSource() {
    throw new RuntimeException("Not implemented");
  }
  
  /**
   * Get subject that created this stem.
   * <pre class="eg">
   * // Get creator of this stem.
   * try {
   *   Subject creator = ns.getCreateSubject();
   * }
   * catch (SubjectNotFoundException e) {
   *   // Couldn't find subject
   * }
   * </pre>
   * @return  {@link Subject} that created this stem.
   * @throws  SubjectNotFoundException
   */
  public Subject getCreateSubject() 
    throws SubjectNotFoundException
  {
    throw new RuntimeException("Not implemented");
  }
  
  /**
   * Get creation time for this stem.
   * <pre class="eg">
   * // Get create time.
   * Date created = ns.getCreateTime();
   * </pre>
   * @return  {@link Date} that this stem was created.
   */
  public Date getCreateTime() {
    throw new RuntimeException("Not implemented");
  }
  
  /**
   * Get (optional and questionable) modify source for this stem.
   * <pre class="eg">
   * // Get modify source
`  * String source = ns.getModifySource();
   * </pre>
   * @return  Modify source for this stem.
   */
  public String getModifySource() {
    throw new RuntimeException("Not implemented");
  }
  
  /**
   * Get subject that last modified this stem.
   * <pre class="eg">
   * // Get last modifier of this stem.
   * try {
   *   Subject modifier = ns.getModifySubject();
   * }
   * catch (SubjectNotFoundException e) {
   *   // Couldn't find subject
   * }
   * </pre>
   * @return  {@link Subject} that last modified this stem.
   * @throws  SubjectNotFoundException
   */
  public Subject getModifySubject() 
    throws SubjectNotFoundException
  {
    throw new RuntimeException("Not implemented");
  }
  
  /**
   * Get last modified time for this stem.
   * <pre class="eg">
   * // Get last modified time.
   * Date modified = ns.getModifyTime();
   * </pre>
   * @return  {@link Date} that this stem was last modified.
   */
  public Date getModifyTime() {
    throw new RuntimeException("Not implemented");
  }
  
  public String toString() {
    return new ToStringBuilder(this)
           .append("display_name", getDisplay_name())
           .append("name", getName())
           .append("uuid", getUuid())
           .append("creator_id", getCreator_id())
           .append("modifier_id", getModifier_id())
           .toString();
  }

  public boolean equals(Object other) {
    if ( (this == other ) ) return true;
    if ( !(other instanceof Stem) ) return false;
    Stem castOther = (Stem) other;
    return new EqualsBuilder()
           .append(this.getUuid(), castOther.getUuid())
           .append(this.getCreator_id(), castOther.getCreator_id())
           .append(this.getModifier_id(), castOther.getModifier_id())
           .isEquals();
  }

  public int hashCode() {
    return new HashCodeBuilder()
           .append(getUuid())
           .append(getCreator_id())
           .append(getModifier_id())
           .toHashCode();
  }

  // Hibernate Accessors
  private String getId() {
    return this.id;
  }

  private void setId(String id) {
    this.id = id;
  }

  private String getCreate_source() {
    return this.create_source;
  }

  private void setCreate_source(String create_source) {
    this.create_source = create_source;
  }

  private Date getCreate_time() {
    return this.create_time;
  }

  private void setCreate_time(Date create_time) {
    this.create_time = create_time;
  }

  private String getDescription() {
    return this.description;
  }

  private void setDescription(String description) {
    this.description = description;
  }

  private String getDisplay_extension() {
    return this.display_extension;
  }

  private void setDisplay_extension(String display_extension) {
    this.display_extension = display_extension;
  }

  private String getDisplay_name() {
    return this.display_name;
  }

  private void setDisplay_name(String display_name) {
    this.display_name = display_name;
  }

  private String getExtension() {
    return this.extension;
  }

  private void setExtension(String extension) {
    this.extension = extension;
  }

  private String getModify_source() {
    return this.modify_source;
  }

  private void setModify_source(String modify_source) {
    this.modify_source = modify_source;
  }

  private Date getModify_time() {
    return this.modify_time;
  }

  private void setModify_time(Date modify_time) {
    this.modify_time = modify_time;
  }

  private String getName() {
    return this.name;
  }

  private void setName(String name) {
    this.name = name;
  }

  private String getUuid() {
    return this.uuid;
  }

  private void setUuid(String uuid) {
    this.uuid = uuid;
  }

  private Integer getVersion() {
    return this.version;
  }

  private void setVersion(Integer version) {
    this.version = version;
  }

  private Member getCreator_id() {
    return this.creator_id;
  }

  private void setCreator_id(Member creator_id) {
    this.creator_id = creator_id;
  }

  private Member getModifier_id() {
    return this.modifier_id;
  }

  private void setModifier_id(Member modifier_id) {
      this.modifier_id = modifier_id;
  }

  private Stem getParent_stem() {
    return this.parent_stem;
  }

  private void setParent_stem(Stem parent_stem) {
    this.parent_stem = parent_stem;
  }

  private Set getChild_groups() {
    return this.child_groups;
  }

  private void setChild_groups(Set child_groups) {
    this.child_groups = child_groups;
  }

  private Set getChild_stems() {
    return this.child_stems;
  }

  private void setChild_stems(Set child_stems) {
    this.child_stems = child_stems;
  }

}

