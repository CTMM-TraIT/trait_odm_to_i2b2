/**
 * Copyright(c)  2011-2012 Recombinant Data Corp., All rights Reserved
 */

package com.recomdata.i2b2.dao;

import java.sql.SQLException;
import com.recomdata.i2b2.entity.I2B2StudyInfo;

/**
 *
 * IStudyDao.java
 * author: Alex Wu  on 09/01/2011
 *
 *
 */
public interface IStudyDao {
	public static final int BATCH_SIZE = 100;

	//insert sql statement
	public static final String INSERT_SQL =  "INSERT INTO STUDY (C_HLEVEL,"+
															"C_FULLNAME,"+
															"C_NAME,"+
															"C_SYNONYM_CD,"+
															"C_VISUALATTRIBUTES,"+
															"C_TOTALNUM,"+
															"C_BASECODE,"+
															"C_METADATAXML,"+
															"C_FACTTABLECOLUMN,"+
															"C_TABLENAME,"+
															"C_COLUMNNAME,"+
															"C_COLUMNDATATYPE,"+
															"C_OPERATOR,"+
															"C_DIMCODE,"+
															"C_COMMENT,"+
															"C_TOOLTIP,"+
															"M_APPLIED_PATH,"+
															"UPDATE_DATE,"+
															"DOWNLOAD_DATE,"+
															"IMPORT_DATE,"+
															"SOURCESYSTEM_CD,"+
															"VALUETYPE_CD) "+
												"VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

	/**
	 * interface method for deleting existed records in i2b2 SDUDY and set up level 0.
	 *
	 * @return
	 * @throws SQLException
	 */
	public void preSetupI2B2Study(String projectID, String sourceSystem) throws SQLException;


	/**
	 * interface method for insert ODM data into i2b2metadata SDUDY.
	 *
	 * @param I2B2StudyInfo
	 * @param studyInfo
	 * @return
	 * @throws SQLException
	 */
	public void insertMetadata(I2B2StudyInfo studyInfo) throws SQLException;
	
	public void executeBatch() throws SQLException;
}