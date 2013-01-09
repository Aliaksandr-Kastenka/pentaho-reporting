/*
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2005-2011 Pentaho Corporation.  All rights reserved.
 */
package org.pentaho.reporting.engine.classic.core.layout.model.table;

import org.pentaho.reporting.engine.classic.core.ReportAttributeMap;
import org.pentaho.reporting.engine.classic.core.filter.types.AutoLayoutBoxType;
import org.pentaho.reporting.engine.classic.core.layout.model.BlockRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.context.BoxDefinition;
import org.pentaho.reporting.engine.classic.core.layout.model.table.columns.TableColumnModel;
import org.pentaho.reporting.engine.classic.core.layout.model.table.rows.SeparateRowModel;
import org.pentaho.reporting.engine.classic.core.layout.model.table.rows.TableRowModel;
import org.pentaho.reporting.engine.classic.core.layout.style.SimpleStyleSheet;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;
import org.pentaho.reporting.engine.classic.core.states.ReportStateKey;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

/**
 * A table section box does not much rendering or layouting at all. It
 * represents one of the three possible sections and behaves like any other
 * block box. But (here it comes!) it refuses to be added to anything else than
 * a TableRenderBox (a small check to save me a lot of insanity ..).
 *
 * @author Thomas Morgner
 */
public class TableSectionRenderBox extends BlockRenderBox
{
  private static final int FLAG_TABLE_SECTION_STRUCTURE_VALIDATED = FLAG_BOX_TABLE_SECTION_RESERVED;
  private static final int FLAG_TABLE_SECTION_ACTIVE = FLAG_BOX_TABLE_SECTION_RESERVED4;
  private static final int FLAG_TABLE_SECTION_MARKED_ACTIVE = FLAG_BOX_TABLE_SECTION_RESERVED2;
  private static final int FLAG_TABLE_SECTION_APPLIED_ACTIVE = FLAG_BOX_TABLE_SECTION_RESERVED3;

  private Role displayRole;
  private TableRowModel rowModel;

  public static enum Role
  {
    BODY, HEADER, FOOTER
  }

  public TableSectionRenderBox()
  {
    this(SimpleStyleSheet.EMPTY_STYLE, new InstanceID(), BoxDefinition.EMPTY,
        AutoLayoutBoxType.INSTANCE, ReportAttributeMap.EMPTY_MAP, null);
  }


  public TableSectionRenderBox(final StyleSheet styleSheet,
                               final InstanceID instanceID,
                               final BoxDefinition boxDefinition,
                               final ElementType elementType,
                               final ReportAttributeMap attributes,
                               final ReportStateKey stateKey)
  {
    super(styleSheet, instanceID, boxDefinition, elementType, attributes, stateKey);
    this.rowModel = new SeparateRowModel();
    final Object layoutMode = styleSheet.getStyleProperty(BandStyleKeys.LAYOUT);
    if (BandStyleKeys.LAYOUT_TABLE_FOOTER.equals(layoutMode))
    {
      this.displayRole = Role.FOOTER;
    }
    else if (BandStyleKeys.LAYOUT_TABLE_HEADER.equals(layoutMode))
    {
      this.displayRole = Role.HEADER;
    }
    else
    {
      this.displayRole = Role.BODY;
    }
  }

  public boolean useMinimumChunkWidth()
  {
    return true;
  }

  /*  public void appyStyle(final LayoutContext context, final OutputProcessorMetaData metaData)
    {
      super.appyStyle(context, metaData);
      this.displayRole = context.getValue(BoxStyleKeys.DISPLAY_ROLE);
    }

  */
  public boolean isBody()
  {
    return Role.BODY.equals(displayRole);
  }

  public Role getDisplayRole()
  {
    return displayRole;
  }

  public int getNodeType()
  {
    return LayoutNodeTypes.TYPE_BOX_TABLE_SECTION;
  }

  public TableColumnModel getColumnModel()
  {
    final TableRenderBox table = TableHelper.lookupTable(this);
    if (table == null)
    {
      return null;
    }
    return table.getColumnModel();
  }

  public TableRowModel getRowModel()
  {
    return rowModel;
  }

  public boolean isStructureValidated()
  {
    return isFlag(FLAG_TABLE_SECTION_STRUCTURE_VALIDATED);
  }

  public void setStructureValidated(final boolean structureValidated)
  {
    setFlag(FLAG_TABLE_SECTION_STRUCTURE_VALIDATED, structureValidated);
  }


  public boolean isActive()
  {
    return isFlag(FLAG_TABLE_SECTION_ACTIVE);
  }

  protected void setActive(final boolean active)
  {
    setFlag(FLAG_TABLE_SECTION_ACTIVE, active);
  }

  public boolean isMarkedActive()
  {
    return isFlag(FLAG_TABLE_SECTION_MARKED_ACTIVE);
  }

  protected void setMarkedActive(final boolean active)
  {
    setFlag(FLAG_TABLE_SECTION_MARKED_ACTIVE, active);
  }

  public boolean isAppliedActive()
  {
    return isFlag(FLAG_TABLE_SECTION_APPLIED_ACTIVE);
  }

  protected void setAppliedActive(final boolean active)
  {
    setFlag(FLAG_TABLE_SECTION_APPLIED_ACTIVE, active);
  }

  public void markBoxSeen()
  {
    super.markBoxSeen();
    setMarkedActive(isActive());
  }

  public void commit()
  {
    super.commit();
    setAppliedActive(isMarkedActive());
  }

  public void rollback(final boolean deepDirty)
  {
    super.rollback(deepDirty);
    setActive(isAppliedActive());
  }
}