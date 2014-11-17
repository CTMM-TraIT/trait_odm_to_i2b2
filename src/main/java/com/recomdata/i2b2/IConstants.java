/**
 * Copyright(c)  2011-2012 Recombinant Data Corp., All rights Reserved
 */

package com.recomdata.i2b2;


/**
 *
 * IConstants.java
 * Hold constant values that will insert to i2b2metadate table STUDY
 * author: Alex Wu  on 09/01/2011
 *
 */
public interface IConstants{
	//constant values
	int C_HLEVEL_1 = 1;
	int C_HLEVEL_2 = 2;
	int C_HLEVEL_3 = 3;
	int C_HLEVEL_4 = 4;
	int C_HLEVEL_5 = 5;
	int C_HLEVEL_6 = 6;
	String C_SYNONYM_CD = "N";
	String C_VISUALATTRIBUTES_FOLDER = "FA";
	String C_VISUALATTRIBUTES_CONTAINER = "CA";
	String C_VISUALATTRIBUTES_MULTIPLE = "MA";
	String C_VISUALATTRIBUTES_LEAF = "LA";
	String C_FACTTABLECOLUMN = "concept_cd";
	String C_TABLENAME = "concept_dimension";
	String C_COLUMNNAME = "concept_path";
	String C_COLUMNDATATYPE = "T";
	String C_OPERATOR = "LIKE";
	String SOURCESYSTEM_CD = "ODM";

	//c_metadataxml
	String C_METADATAXML_HEIGHT = "<?xml version=\"1.0\"?><ValueMetadata><Version>3.02</Version><CreationDateTime>09/20/2011 10:23:07</CreationDateTime><TestID>HEIGHT</TestID><TestName>Height</TestName><DataType>PosFloat</DataType><CodeType>GRP</CodeType><Loinc>1111-0</Loinc><Flagstouse>HL</Flagstouse><Oktousevalues>Y</Oktousevalues><MaxStringLength></MaxStringLength><LowofLowValue>100</LowofLowValue><HighofLowValue>150</HighofLowValue><LowofHighValue>180</LowofHighValue><HighofHighValue>250</HighofHighValue><LowofToxicValue></LowofToxicValue><HighofToxicValue></HighofToxicValue><EnumValues></EnumValues><CommentsDeterminingExclusion><Com></Com></CommentsDeterminingExclusion><UnitValues><NormalUnits>cm</NormalUnits><NormalUnits>cm</NormalUnits><EqualUnits>cm</EqualUnits><EqualUnits>cm</EqualUnits><ExcludingUnits></ExcludingUnits><ConvertingUnits><Units></Units><MultiplyingFactor></MultiplyingFactor></ConvertingUnits></UnitValues><Analysis><Enums /><Counts /><New /></Analysis></ValueMetadata>";

	String C_METADATAXML_AGE = "<?xml version=\"1.0\"?><ValueMetadata><Version>3.02</Version><CreationDateTime>09/20/2011 10:23:07</CreationDateTime><TestID>Age</TestID><TestName>Age(years)</TestName><DataType>PosInteger</DataType><CodeType>GRP</CodeType><Loinc>1111-0</Loinc><Flagstouse>LNH</Flagstouse><Oktousevalues>Y</Oktousevalues><MaxStringLength></MaxStringLength><LowofLowValue>1</LowofLowValue><HighofLowValue>100</HighofLowValue><LowofHighValue>90</LowofHighValue><HighofHighValue>150</HighofHighValue><LowofToxicValue></LowofToxicValue><HighofToxicValue></HighofToxicValue><EnumValues></EnumValues><CommentsDeterminingExclusion><Com></Com></CommentsDeterminingExclusion><UnitValues><NormalUnits>years</NormalUnits><NormalUnits>years</NormalUnits><EqualUnits>years</EqualUnits><EqualUnits>years</EqualUnits><ExcludingUnits></ExcludingUnits><ConvertingUnits><Units></Units><MultiplyingFactor></MultiplyingFactor></ConvertingUnits></UnitValues><Analysis><Enums /><Counts /><New /></Analysis></ValueMetadata>";

	//public static final String C_METADATAXML_SEX = "<?xml version=\"1.0\"?><ValueMetadata><Version>3.02</Version><CreationDateTime>10/31/2011 14:58:34</CreationDateTime><TestID>sex</TestID><TestName>Gender</TestName><DataType>Enum</DataType><CodeType>GRP</CodeType><Loinc>5774-5</Loinc><Flagstouse>HL</Flagstouse><Oktousevalues>Y</Oktousevalues><MaxStringLength></MaxStringLength><LowofLowValue></LowofLowValue><HighofLowValue></HighofLowValue><LowofHighValue></LowofHighValue><HighofHighValue></HighofHighValue><LowofToxicValue></LowofToxicValue><HighofToxicValue></HighofToxicValue><EnumValues><Val description=\"0\">Female</Val><Val description=\"1\">Male</Val></EnumValues><CommentsDeterminingExclusion><Com></Com></CommentsDeterminingExclusion><UnitValues><NormalUnits></NormalUnits><EqualUnits></EqualUnits><ExcludingUnits></ExcludingUnits><ConvertingUnits><Units></Units><MultiplyingFactor></MultiplyingFactor></ConvertingUnits></UnitValues><Analysis><Enums /><Counts /><New /></Analysis></ValueMetadata>";

}
