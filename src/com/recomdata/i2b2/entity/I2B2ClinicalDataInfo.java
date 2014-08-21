/**
 * Copyright(c)  2011-2012 Recombinant Data Corp., All rights Reserved
 *
 * @author Alex Wu
 * @date October 19, 2011
 */

package com.recomdata.i2b2.entity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Java Bean for Clinical Data: Observation_Fact
 * @author awu
 *
 */
public class I2B2ClinicalDataInfo
{
	//observation_fact
	int encounterNum = 0;
	String patientNum = null;
	String conceptCd = null;
	String providerId = "@";
	Date startDate = null;
	String modifierCd = "@";
	String valTypeCd = null;
	String tvalChar = null;
	BigDecimal nvalNum = null;
	int instanceNum = 0;
	String valueFlagCd = null;
	BigDecimal quantityNum = null;
	String unitsCd = null;
	Date endDate = null;
	String locationCd = null;
	String observationBlob = null;
	BigDecimal confidenceNum = null;
	Date updateDate = null;
	Date downloadDate = null;
	Date importDate = null;
	String sourcesystemCd = null;
	int uploadId = 0;
	
	/**
	 * @return the encounterNum
	 */
	public int getEncounterNum() {
		return encounterNum;
	}
	/**
	 * @param encounterNum the encounterNum to set
	 */
	public void setEncounterNum(int encounterNum) {
		this.encounterNum = encounterNum;
	}
	/**
	 * @return the patientNum
	 */
	public String getPatientNum() {
		return patientNum;
	}
	/**
	 * @param patientNum the patientNum to set
	 */
	public void setPatientNum(String patientNum) {
		this.patientNum = patientNum;
	}
	/**
	 * @return the conceptCd
	 */
	public String getConceptCd() {
		return conceptCd;
	}
	/**
	 * @param conceptCd the conceptCd to set
	 */
	public void setConceptCd(String conceptCd) {
		this.conceptCd = conceptCd;
	}
	/**
	 * @return the providerId
	 */
	public String getProviderId() {
		return providerId;
	}
	/**
	 * @param providerId the providerId to set
	 */
	public void setProviderId(String providerId) {
		this.providerId = providerId;
	}
	/**
	 * @return the startDate
	 */
	public java.util.Date getStartDate() {
		return startDate;
	}
	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(java.util.Date startDate) {
		this.startDate = startDate;
	}
	/**
	 * @return the modifierCd
	 */
	public String getModifierCd() {
		return modifierCd;
	}
	/**
	 * @param modifierCd the modifierCd to set
	 */
	public void setModifierCd(String modifierCd) {
		this.modifierCd = modifierCd;
	}
	/**
	 * @return the valTypeCd
	 */
	public String getValTypeCd() {
		return valTypeCd;
	}
	/**
	 * @param valTypeCd the valTypeCd to set
	 */
	public void setValTypeCd(String valTypeCd) {
		this.valTypeCd = valTypeCd;
	}
	/**
	 * @return the tvalChar
	 */
	public String getTvalChar() {
		return tvalChar;
	}
	/**
	 * @param tvalChar the tvalChar to set
	 */
	public void setTvalChar(String tvalChar) {
		this.tvalChar = tvalChar;
	}
	/**
	 * @return the nvalNum
	 */
	public BigDecimal getNvalNum() {
		return nvalNum;
	}
	/**
	 * @param number the nvalNum to set
	 */
	public void setNvalNum(BigDecimal number) {
		this.nvalNum = number;
	}
	/**
	 * @return the instanceNum
	 */
	public int getInstanceNum() {
		return instanceNum;
	}
	/**
	 * @param instanceNum the instanceNum to set
	 */
	public void setInstanceNum(int instanceNum) {
		this.instanceNum = instanceNum;
	}
	/**
	 * @return the valueFlagCd
	 */
	public String getValueFlagCd() {
		return valueFlagCd;
	}
	/**
	 * @param valueFlagCd the valueFlagCd to set
	 */
	public void setValueFlagCd(String valueFlagCd) {
		this.valueFlagCd = valueFlagCd;
	}
	/**
	 * @return the quantityNum
	 */
	public BigDecimal getQuantityNum() {
		return quantityNum;
	}
	/**
	 * @param quantityNum the quantityNum to set
	 */
	public void setQuantityNum(BigDecimal quantityNum) {
		this.quantityNum = quantityNum;
	}
	/**
	 * @return the unitsCd
	 */
	public String getUnitsCd() {
		return unitsCd;
	}
	/**
	 * @param unitsCd the unitsCd to set
	 */
	public void setUnitsCd(String unitsCd) {
		this.unitsCd = unitsCd;
	}
	/**
	 * @return the endDate
	 */
	public java.util.Date getEndDate() {
		return endDate;
	}
	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(java.util.Date endDate) {
		this.endDate = endDate;
	}
	/**
	 * @return the locationCd
	 */
	public String getLocationCd() {
		return locationCd;
	}
	/**
	 * @param locationCd the locationCd to set
	 */
	public void setLocationCd(String locationCd) {
		this.locationCd = locationCd;
	}
	/**
	 * @return the observationBlob
	 */
	public String getObservationBlob() {
		return observationBlob;
	}
	/**
	 * @param observationBlob the observationBlob to set
	 */
	public void setObservationBlob(String observationBlob) {
		this.observationBlob = observationBlob;
	}
	/**
	 * @return the confidenceNum
	 */
	public BigDecimal getConfidenceNum() {
		return confidenceNum;
	}
	/**
	 * @param confidenceNum the confidenceNum to set
	 */
	public void setConfidenceNum(BigDecimal confidenceNum) {
		this.confidenceNum = confidenceNum;
	}
	/**
	 * @return the updateDate
	 */
	public java.util.Date getUpdateDate() {
		return updateDate;
	}
	/**
	 * @param updateDate the updateDate to set
	 */
	public void setUpdateDate(java.util.Date updateDate) {
		this.updateDate = updateDate;
	}
	/**
	 * @return the downloadDate
	 */
	public java.util.Date getDownloadDate() {
		return downloadDate;
	}
	/**
	 * @param downloadDate the downloadDate to set
	 */
	public void setDownloadDate(java.util.Date downloadDate) {
		this.downloadDate = downloadDate;
	}
	/**
	 * @return the importDate
	 */
	public java.util.Date getImportDate() {
		return importDate;
	}
	/**
	 * @param importDate the importDate to set
	 */
	public void setImportDate(java.util.Date importDate) {
		this.importDate = importDate;
	}
	/**
	 * @return the sourcesystemCd
	 */
	public String getSourcesystemCd() {
		return sourcesystemCd;
	}
	/**
	 * @param sourcesystemCd the sourcesystemCd to set
	 */
	public void setSourcesystemCd(String sourcesystemCd) {
		this.sourcesystemCd = sourcesystemCd;
	}
	/**
	 * @return the uploadId
	 */
	public int getUploadId() {
		return uploadId;
	}
	/**
	 * @param uploadId the uploadId to set
	 */
	public void setUploadId(int uploadId) {
		this.uploadId = uploadId;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("I2B2ClinicalDataInfo [patientNum=");
		builder.append(patientNum);
		builder.append(", encounterNum=");
		builder.append(encounterNum);
		builder.append(", instanceNum=");
		builder.append(instanceNum);
		builder.append(", conceptCd=");
		builder.append(conceptCd);
		builder.append(", modifierCd=");
		builder.append(modifierCd);
		builder.append(", startDate=");
		builder.append(startDate);
		builder.append(", endDate=");
		builder.append(endDate);
		builder.append(", valueFlagCd=");
		builder.append(valueFlagCd);
		builder.append(", valTypeCd=");
		builder.append(valTypeCd);
		builder.append(", tvalChar=");
		builder.append(tvalChar);
		builder.append(", nvalNum=");
		builder.append(nvalNum);
		builder.append(", quantityNum=");
		builder.append(quantityNum);
		builder.append(", unitsCd=");
		builder.append(unitsCd);
		builder.append(", sourcesystemCd=");
		builder.append(sourcesystemCd);
		builder.append("]");
		return builder.toString();
	}	
}