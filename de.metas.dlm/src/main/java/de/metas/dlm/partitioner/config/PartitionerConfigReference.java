package de.metas.dlm.partitioner.config;

import org.adempiere.util.Check;
import org.adempiere.util.lang.EqualsBuilder;
import org.adempiere.util.lang.HashcodeBuilder;

import de.metas.dlm.partitioner.config.PartitionerConfigLine.LineBuilder;

/*
 * #%L
 * metasfresh-dlm
 * %%
 * Copyright (C) 2016 metas GmbH
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program. If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

/**
 * Each instance of this class belongs to one {@link PartitionerConfigLine} (parent) and specifies one column of its parent's {@link PartitionerConfigLine#getTableName()}.
 * <p>
 * E.g. if the parent's table name is <code>C_Invoice</code>, then this instance might be about
 * <li>{@link #getReferencingColumnName()} <code>=_C_Order_ID</code></li>
 * <li>{@link #getReferencedTableName()} <code>= C_Order</code></li>
 * Meaning that this instance, together with its parents tells the engine that <code>C_Invoice</code> records can reference <code>C_Order</code> records via the foreign key column <code>C_Invoice.C_Order_ID</code>.
 *
 * Further, if {@link #getReferencedConfigLine()} is not <code>null</code>, it means that <code>C_Order</code> is also a DLM'ed table and might have its own list of config references.
 *
 * @author metas-dev <dev@metasfresh.com>
 *
 */
public class PartitionerConfigReference
{
	private final String referencedTableName;
	private final String referencingColumnName;
	private final PartitionerConfigLine referencedConfigLine;
	private final PartitionerConfigLine parent;
	private int DLM_Partition_Config_Reference_ID;

	private PartitionerConfigReference(final PartitionerConfigLine parent,
			final String referencingColumnName,
			final String referencedTableName,
			final PartitionerConfigLine referencedConfigLine)
	{
		this.parent = parent;
		this.referencingColumnName = referencingColumnName;
		this.referencedTableName = referencedTableName;
		this.referencedConfigLine = referencedConfigLine;
	}

	public String getReferencedTableName()
	{
		return referencedTableName;
	}

	public String getReferencingColumnName()
	{
		return referencingColumnName;
	}

	// public PartitionerConfigLine getReferencedConfigLine()
	// {
	// return referencedConfigLine;
	// }

	public PartitionerConfigLine getParent()
	{
		return parent;
	}

	@Override
	public String toString()
	{
		return "PartitionConfigReference [referencingColumnName=" + referencingColumnName + ", referencedTableName=" + referencedTableName + ", referencedConfigLine=" + referencedConfigLine + "]";
	}

	public int getDLM_Partition_Config_Reference_ID()
	{
		return DLM_Partition_Config_Reference_ID;
	}

	public void setDLM_Partition_Config_Reference_ID(final int DLM_Partition_Config_Reference_ID)
	{
		this.DLM_Partition_Config_Reference_ID = DLM_Partition_Config_Reference_ID;
	}

	public static class RefBuilder
	{
		private String referencedTableName = "";
		private String referencingColumnName = "";

		private final String referencedConfigLineTableName = "";

		private final PartitionerConfigLine.LineBuilder parentbuilder;
		private int DLM_Partition_Config_Reference;

		RefBuilder(final PartitionerConfigLine.LineBuilder parentBuilder)
		{
			parentbuilder = parentBuilder;
		}

		public RefBuilder setReferencedTableName(final String referencedTableName)
		{
			this.referencedTableName = referencedTableName;
			return this;
		}

		public RefBuilder setReferencingColumnName(final String referencingColumnName)
		{
			this.referencingColumnName = referencingColumnName;
			return this;
		}

		public RefBuilder setDLM_Partition_Config_Reference(final int dlm_Partition_Config_Reference_ID)
		{
			DLM_Partition_Config_Reference = dlm_Partition_Config_Reference_ID;
			return this;
		}

		public LineBuilder endRef()
		{
			return parentbuilder.endRef();
		}

		/**
		 * Convenience method to end the current ref builder and start a new one.
		 *
		 * @return the new reference builder, <b>not</b> this instance.
		 */
		public RefBuilder newRef()
		{
			return endRef().ref();
		}

		/**
		 * Supposed to be called only by the parent builder.
		 *
		 * @param parent
		 * @return
		 */
		/* package */ PartitionerConfigReference build(final PartitionerConfigLine parent)
		{
			final PartitionerConfigLine referencedConfigLine;
			if (Check.isEmpty(referencedConfigLineTableName, true))
			{
				referencedConfigLine = null;
			}
			else
			{
				referencedConfigLine = parent.getParent().getLineNotNull(referencedConfigLineTableName);
			}

			final PartitionerConfigReference partitionerConfigReference = new PartitionerConfigReference(parent, referencingColumnName, referencedTableName, referencedConfigLine);
			partitionerConfigReference.setDLM_Partition_Config_Reference_ID(DLM_Partition_Config_Reference);
			return partitionerConfigReference;
		}

		@Override
		public int hashCode()
		{
			return new HashcodeBuilder()
					.append(referencedTableName.toLowerCase())
					.append(referencingColumnName.toLowerCase())
					.append(parentbuilder)
					.toHashcode();

		};

		@Override
		public boolean equals(final Object obj)
		{
			if (this == obj)
			{
				return true;
			}
			final RefBuilder other = EqualsBuilder.getOther(this, obj);
			if (other == null)
			{
				return false;
			}
			return new EqualsBuilder()
					.append(referencedTableName.toLowerCase(), other.referencedTableName.toLowerCase())
					.append(referencingColumnName.toLowerCase(), other.referencingColumnName.toLowerCase())
					.append(parentbuilder, other.parentbuilder)
					.isEqual();
		}
	}
}
