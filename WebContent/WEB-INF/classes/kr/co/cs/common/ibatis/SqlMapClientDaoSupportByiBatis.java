﻿/*
 * Copyright 2002-2005 the original author or authors.
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

package kr.co.cs.common.ibatis;

import javax.sql.DataSource;

import org.springframework.dao.support.DaoSupport;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * Convenient super class for iBATIS SqlMapClient data access objects.
 * Requires a SqlMapClient to be set, providing a SqlMapClientTemplate
 * based on it to subclasses.
 *
 * <p>Instead of a plain SqlMapClient, you can also pass a preconfigured
 * SqlMapClientTemplate instance in. This allows you to share your
 * SqlMapClientTemplate configuration for all your DAOs, for example
 * a custom SQLExceptionTranslator to use.
 *
 * @author Juergen Hoeller
 * @since 24.02.2004
 * @see #setSqlMapClient
 * @see #setSqlMapClientTemplate 
 * @see org.springframework.orm.ibatis.SqlMapClientTemplate
 * @see org.springframework.orm.ibatis.SqlMapClientTemplate#setExceptionTranslator
 */
public abstract class SqlMapClientDaoSupportByiBatis extends DaoSupport {

	private SqlMapClientTemplateByiBatis sqlMapClientTemplate = new SqlMapClientTemplateByiBatis();

	private boolean externalTemplate = false;

	/**
	 * Set the JDBC DataSource to be used by this DAO.
	 * Not required: The SqlMapClient might carry a shared DataSource.
	 * @see #setSqlMapClient
	 */
	public final void setDataSource(DataSource dataSource) {
	  this.sqlMapClientTemplate.setDataSource(dataSource);
	}

	/**
	 * Return the JDBC DataSource used by this DAO.
	 */
	public final DataSource getDataSource() {
		return (this.sqlMapClientTemplate != null ? this.sqlMapClientTemplate.getDataSource() : null);
	}

	/**
	 * Set the iBATIS Database Layer SqlMapClient to work with.
	 * Either this or a "sqlMapClientTemplate" is required.
	 * @see #setSqlMapClientTemplate
	 */
	public final void setSqlMapClient(SqlMapClient sqlMapClient) {
		this.sqlMapClientTemplate.setSqlMapClient(sqlMapClient);
	}

	/**
	 * Return the iBATIS Database Layer SqlMapClient that this template works with.
	 */
	public final SqlMapClient getSqlMapClient() {
		return this.sqlMapClientTemplate.getSqlMapClient();
	}

	/**
	 * Set the SqlMapClientTemplate for this DAO explicitly,
	 * as an alternative to specifying a SqlMapClient.
	 * @see #setSqlMapClient
	 */
	public final void setSqlMapClientTemplate(SqlMapClientTemplateByiBatis sqlMapClientTemplate) {
		if (sqlMapClientTemplate == null) {
			throw new IllegalArgumentException("Cannot set sqlMapClientTemplate to null");
		}
		this.sqlMapClientTemplate = sqlMapClientTemplate;
		this.externalTemplate = true;
	}

	/**
	 * Return the SqlMapClientTemplate for this DAO,
	 * pre-initialized with the SqlMapClient or set explicitly.
	 */
	public final SqlMapClientTemplateByiBatis getSqlMapClientTemplate() {
	  return sqlMapClientTemplate;
	}

	protected final void checkDaoConfig() {
		if (!this.externalTemplate) {
			this.sqlMapClientTemplate.afterPropertiesSet();
		}
	}

}