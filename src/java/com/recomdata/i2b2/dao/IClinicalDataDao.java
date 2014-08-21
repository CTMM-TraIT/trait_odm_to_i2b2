/**
 * Copyright(c)  2011-2012 Recombinant Data Corp., All rights Reserved
 */

package com.recomdata.i2b2.dao;

import java.sql.SQLException;

import com.recomdata.i2b2.entity.I2B2ClinicalDataInfo;

/**
 *
 * IConceptDao.java
 * author: Alex Wu  on 10/19/2011
 */
public interface IClinicalDataDao {
	public static final int BATCH_SIZE = 100;

	public static final String INSERT_OBSERV_FACT_SQL =  "INSERT INTO Observation_Fact (Encounter_Num,"+
												"REDCap_Subject_ID,"+
												"Concept_Cd,"+
												"Provider_Id,"+
												"Start_Date,"+
												"Modifier_Cd,"+
												"ValType_Cd,"+
												"TVal_Char,"+
												"NVal_Num,"+
												"INSTANCE_NUM,"+
												"ValueFlag_Cd,"+
												"Quantity_Num,"+
												"Units_Cd,"+
												"End_Date,"+
												"Location_Cd,"+
												"Observation_Blob,"+
												"Confidence_Num,"+
												"UPDATE_DATE,"+
												"DOWNLOAD_DATE,"+
												"IMPORT_DATE,"+
												"SOURCESYSTEM_CD,"+
												"UPLOAD_ID) "+
												"VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";


	/**
	 * interface method for deleting existed clinical records for the subject
	 *
	 * @return
	 * @throws SQLException
	 */
	public void cleanupClinicalData(String projectID, String sourceSystem) throws SQLException;

	/**
	 * interface method for insert ODM data into i2b2demodata Observation.
	 *
	 * @param I2B2ClinicalDataInfo
	 * @param clinicalDataInfo
	 * @return
	 * @throws SQLException
	 */
	public void insertObservation(I2B2ClinicalDataInfo clinicalDataInfo) throws SQLException;

	public void executeBatch() throws SQLException;

}