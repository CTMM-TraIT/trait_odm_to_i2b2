/**
 * Copyright(c)  2011-2012 Recombinant Data Corp., All rights Reserved
 */

package com.recomdata.i2b2.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import com.recomdata.i2b2.entity.I2B2ClinicalDataInfo;

/**
 * ConceptDao.java the implementation for IConceptDao
 *
 * DAO used to turn ODM clinical data into i2b2 observation and concept records.
 * author: Alex Wu on 10/19/2011
 */
public class ClinicalDataDao implements IClinicalDataDao {
	private int observationBatchCount = 100;
	private PreparedStatement insertObservationStatement = null;

	public ClinicalDataDao() throws SQLException {
		Connection con = I2B2DBUtils.getI2B2DBConnection();
		insertObservationStatement = con.prepareStatement(IClinicalDataDao.INSERT_OBSERV_FACT_SQL);
	}

	/**
	 * Method to delete records in observation_fact and concept_dimension
	 */
	public void cleanupClinicalData(String projectID, String sourceSystem) throws SQLException {
		String conceptCodePattern = "STUDY|" + projectID + "|%";

		// delete observation records for the project
		String deleteFactsSql = "DELETE FROM observation_fact WHERE concept_cd LIKE '" + conceptCodePattern
				+ "' AND sourcesystem_cd = '" + sourceSystem + "'";

		Statement stmt = I2B2DBUtils.getI2B2DBConnection().createStatement();
		stmt.executeUpdate(deleteFactsSql);

		// delete concept records for the project
		String deleteConceptsSql = "DELETE FROM concept_dimension WHERE concept_cd LIKE '" + conceptCodePattern
				+ "' AND sourcesystem_cd = '" + sourceSystem + "'";

		stmt.executeUpdate(deleteConceptsSql);

		// populate concept dimension from study table
		String insertConceptsSql = "INSERT INTO Concept_Dimension (concept_path, concept_cd, name_char, "
				+ "update_date, download_date, import_date, sourcesystem_cd) "
				+ "SELECT C_DIMCODE, C_BASECODE, C_NAME, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD "
				+ "FROM STUDY WHERE C_BASECODE LIKE '" + conceptCodePattern + "'";
		stmt.executeUpdate(insertConceptsSql);

		stmt.close();
	}

	/**
	 * Method to insert ODM clinical data to observation_fact
	 */
	public void insertObservation(I2B2ClinicalDataInfo clinicalDataInfo) throws SQLException {
		insertObservationStatement.setInt(1, clinicalDataInfo.getEncounterNum());
		insertObservationStatement.setString(2, clinicalDataInfo.getPatientNum());
		insertObservationStatement.setString(3, clinicalDataInfo.getConceptCd());
		insertObservationStatement.setString(4, clinicalDataInfo.getProviderId());
		insertObservationStatement.setDate(5, I2B2DBUtils.getSQLDateFromUtilDate(clinicalDataInfo.getStartDate()));
		insertObservationStatement.setString(6, clinicalDataInfo.getModifierCd());
		insertObservationStatement.setString(7, clinicalDataInfo.getValTypeCd());
		insertObservationStatement.setString(8, clinicalDataInfo.getTvalChar());
		insertObservationStatement.setBigDecimal(9, clinicalDataInfo.getNvalNum());
		insertObservationStatement.setInt(10, clinicalDataInfo.getInstanceNum());
		insertObservationStatement.setString(11, clinicalDataInfo.getValueFlagCd());
		insertObservationStatement.setBigDecimal(12, clinicalDataInfo.getQuantityNum());
		insertObservationStatement.setString(13, clinicalDataInfo.getUnitsCd());
		insertObservationStatement.setDate(14, I2B2DBUtils.getSQLDateFromUtilDate(clinicalDataInfo.getEndDate()));
		insertObservationStatement.setString(15, clinicalDataInfo.getLocationCd());
		insertObservationStatement.setString(16, clinicalDataInfo.getObservationBlob());
		insertObservationStatement.setBigDecimal(17, clinicalDataInfo.getConfidenceNum());
		insertObservationStatement.setDate(18, I2B2DBUtils.getSQLDateFromUtilDate(clinicalDataInfo.getUpdateDate()));
		insertObservationStatement.setDate(19, I2B2DBUtils.getSQLDateFromUtilDate(clinicalDataInfo.getDownloadDate()));
		insertObservationStatement.setDate(20, I2B2DBUtils.getSQLDateFromUtilDate(clinicalDataInfo.getImportDate()));
		insertObservationStatement.setString(21, clinicalDataInfo.getSourcesystemCd());
		insertObservationStatement.setInt(22, clinicalDataInfo.getUploadId());

		if (Boolean.getBoolean("batch.disabled")) {
			insertObservationStatement.execute();
		} else {
			insertObservationStatement.addBatch();

			if (++observationBatchCount > BATCH_SIZE) {
				executeBatch();
			}
		}
	}

	@Override
	public void executeBatch() throws SQLException {
		insertObservationStatement.executeBatch();
		observationBatchCount = 0;
	}
}