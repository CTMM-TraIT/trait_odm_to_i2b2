/**
 * Copyright(c)  2011-2012 Recombinant Data Corp., All rights Reserved
 *
 * @author Alex Wu
 * @date August 30, 2011
 */

package com.recomdata.i2b2.entity;

public class I2B2StudyInfo
{
	  int chlevel = 0;
	  String cfullname = null;
      String namePath = null;
	  String cname = null;
	  String csynonmCd = null;
	  String cvisualAttributes = null;
	  int ctotalNum = 0;
	  String cbasecode = null;
	  String cmetadataxml = null;
	  String cfactTableColumn = null;
	  String ctablename = null;
	  String ccolumnname = null;
	  String ccolumnDatatype = null;
	  String coperator = null;
	  String cdimcode = null;
	  String ccomment = null;
	  String ctooltip = null;
	  String mappliedPath = "@";
	  java.util.Date updateDate = null;
	  java.util.Date downloadDate = null;
	  java.util.Date importDate = null;
	  String sourceSystemCd = null;
	  String valuetype = null;
    String preferredName = null;

    public String getPreferredName() {
        return preferredName;
    }

    public void setPreferredName(String preferredName) {
        this.preferredName = preferredName;
    }

	/**
	 * @return the chlevel
	 */
	public int getChlevel() {
		return chlevel;
	}
	/**
	 * @param chlevel the chlevel to set
	 */
	public void setChlevel(int chlevel) {
		this.chlevel = chlevel;
	}
    /**
     * @return the cfullname
     */
    public String getCfullname() {
        return cfullname;
    }
    /**
     * @param cfullname the cfullname to set
     */
    public void setCfullname(String cfullname) {
        this.cfullname = cfullname;
    }
    /**
     * @return the name path
     */
    public String getNamePath() {
        return namePath;
    }
    /**
     * @param namePath the name path to set
     */
    public void setNamePath(String namePath) {
        this.namePath = namePath;
    }
	/**
	 * @return the cname
	 */
	public String getCname() {
		return cname;
	}
	/**
	 * @param cname the cname to set
	 */
	public void setCname(String cname) {
		this.cname = cname;
	}
	/**
	 * @return the csynonmCd
	 */
	public String getCsynonmCd() {
		return csynonmCd;
	}
	/**
	 * @param csynonmCd the csynonmCd to set
	 */
	public void setCsynonmCd(String csynonmCd) {
		this.csynonmCd = csynonmCd;
	}
	/**
	 * @return the cvisualAttributes
	 */
	public String getCvisualAttributes() {
		return cvisualAttributes;
	}
	/**
	 * @param cvisualAttributes the cvisualAttributes to set
	 */
	public void setCvisualAttributes(String cvisualAttributes) {
		this.cvisualAttributes = cvisualAttributes;
	}
	/**
	 * @return the ctotalNum
	 */
	public int getCtotalNum() {
		return ctotalNum;
	}
	/**
	 * @param ctotalNum the ctotalNum to set
	 */
	public void setCtotalNum(int ctotalNum) {
		this.ctotalNum = ctotalNum;
	}
	/**
	 * @return the cbasecode
	 */
	public String getCbasecode() {
		return cbasecode;
	}
	/**
	 * @param cbasecode the cbasecode to set
	 */
	public void setCbasecode(String cbasecode) {
		this.cbasecode = cbasecode;
	}
	/**
	 * @return the cmetadataxml
	 */
	public String getCmetadataxml() {
		return cmetadataxml;
	}
	/**
	 * @param cmetadataxml the cmetadataxml to set
	 */
	public void setCmetadataxml(String cmetadataxml) {
		this.cmetadataxml = cmetadataxml;
	}
	/**
	 * @return the cfactTableColumn
	 */
	public String getCfactTableColumn() {
		return cfactTableColumn;
	}
	/**
	 * @param cfactTableColumn the cfactTableColumn to set
	 */
	public void setCfactTableColumn(String cfactTableColumn) {
		this.cfactTableColumn = cfactTableColumn;
	}
	/**
	 * @return the ctablename
	 */
	public String getCtablename() {
		return ctablename;
	}
	/**
	 * @param ctablename the ctablename to set
	 */
	public void setCtablename(String ctablename) {
		this.ctablename = ctablename;
	}
	/**
	 * @return the ccolumnname
	 */
	public String getCcolumnname() {
		return ccolumnname;
	}
	/**
	 * @param ccolumnname the ccolumnname to set
	 */
	public void setCcolumnname(String ccolumnname) {
		this.ccolumnname = ccolumnname;
	}
	/**
	 * @return the ccolumnDatatype
	 */
	public String getCcolumnDatatype() {
		return ccolumnDatatype;
	}
	/**
	 * @param ccolumnDatatype the ccolumnDatatype to set
	 */
	public void setCcolumnDatatype(String ccolumnDatatype) {
		this.ccolumnDatatype = ccolumnDatatype;
	}
	/**
	 * @return the coperator
	 */
	public String getCoperator() {
		return coperator;
	}
	/**
	 * @param coperator the coperator to set
	 */
	public void setCoperator(String coperator) {
		this.coperator = coperator;
	}
	/**
	 * @return the cdimcode
	 */
	public String getCdimcode() {
		return cdimcode;
	}
	/**
	 * @param cdimcode the cdimcode to set
	 */
	public void setCdimcode(String cdimcode) {
		this.cdimcode = cdimcode;
	}
	/**
	 * @return the ccomment
	 */
	public String getCcomment() {
		return ccomment;
	}
	/**
	 * @param ccomment the ccomment to set
	 */
	public void setCcomment(String ccomment) {
		this.ccomment = ccomment;
	}
	/**
	 * @return the ctooltip
	 */
	public String getCtooltip() {
		return ctooltip;
	}
	/**
	 * @param ctooltip the ctooltip to set
	 */
	public void setCtooltip(String ctooltip) {
		this.ctooltip = ctooltip;
	}
	
	public String getMappliedPath() {
		return mappliedPath;
	}
	
	public void setMappliedPath(String mappliedPath) {
		this.mappliedPath = mappliedPath;
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
	 * @return the sourceSystemCd
	 */
	public String getSourceSystemCd() {
		return sourceSystemCd;
	}
	/**
	 * @param sourceSystemCd the sourceSystemCd to set
	 */
	public void setSourceSystemCd(String sourceSystemCd) {
		this.sourceSystemCd = sourceSystemCd;
	}
	/**
	 * @return the valuetype
	 */
	public String getValuetype() {
		return valuetype;
	}
	/**
	 * @param valuetype the valuetype to set
	 */
	public void setValuetype(String valuetype) {
		this.valuetype = valuetype;
	}
	@Override
	public String toString() {
		return "I2B2StudyInfo [cbasecode=" + cbasecode + ", cdimcode="
				+ cdimcode + ", chlevel=" + chlevel + ", cname=" + cname + "]";
	}
}