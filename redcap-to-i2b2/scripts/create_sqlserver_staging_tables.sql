
--------------------------------------------------------
--  Create Table STUDY
--------------------------------------------------------
CREATE TABLE STUDY
   (  "C_HLEVEL" INT      NOT NULL, 
  "C_FULLNAME" VARCHAR(700) NOT NULL, 
  "C_NAME" VARCHAR(2000)    NOT NULL, 
  "C_SYNONYM_CD" CHAR(1)    NOT NULL, 
  "C_VISUALATTRIBUTES" CHAR(3)  NOT NULL, 
  "C_TOTALNUM" INT      NULL, 
  "C_BASECODE" VARCHAR(50)  NULL, 
  "C_METADATAXML" TEXT    NULL, 
  "C_FACTTABLECOLUMN" VARCHAR(50) NOT NULL, 
  "C_TABLENAME" VARCHAR(50) NOT NULL, 
  "C_COLUMNNAME" VARCHAR(50)  NOT NULL, 
  "C_COLUMNDATATYPE" VARCHAR(50)  NOT NULL, 
  "C_OPERATOR" VARCHAR(10)  NOT NULL, 
  "C_DIMCODE" VARCHAR(700)  NOT NULL, 
  "C_COMMENT" TEXT      NULL, 
  "C_TOOLTIP" VARCHAR(900)  NULL,
  "M_APPLIED_PATH" VARCHAR(700) NOT NULL, 
  "UPDATE_DATE" DATETIME    NOT NULL, 
  "DOWNLOAD_DATE" DATETIME  NULL, 
  "IMPORT_DATE" DATETIME  NULL, 
  "SOURCESYSTEM_CD" VARCHAR(50) NULL, 
  "VALUETYPE_CD" VARCHAR(50)  NULL,
  "M_EXCLUSION_CD"  VARCHAR(25) NULL,
  "C_PATH"  VARCHAR(700)   NULL,
  "C_SYMBOL"  VARCHAR(50) NULL
   ) ;
   
GO

/* create concept_dimension table with clustered PK on concept_path */
CREATE TABLE concept_dimension ( 
	concept_path   	varchar(700) NOT NULL,
	concept_cd     	varchar(50) NULL,
	name_char      	varchar(2000) NULL,
	concept_blob   	text NULL,
	update_date    	datetime NULL,
	download_date  	datetime NULL,
	import_date    	datetime NULL,
	sourcesystem_cd	varchar(50) NULL,
      UPLOAD_ID       INT NULL,
    CONSTRAINT CONCEPT_DIMENSION_PK PRIMARY KEY(concept_path)
	);
CREATE INDEX CD_IDX_UPLOADID ON CONCEPT_DIMENSION(UPLOAD_ID)
;

/* create observation_fact table with NONclustered PK on encounter_num,concept_cd,provider_id,start_date,modifier_cd  */
CREATE TABLE Observation_Fact ( 
	Encounter_Num  	int NOT NULL,
	REDCap_Subject_ID varchar(50),
	Patient_Num    	int,
	Concept_Cd     	varchar(50) NOT NULL,
	Provider_Id    	varchar(50) NOT NULL,
	Start_Date     	datetime NOT NULL,
	Modifier_Cd    	varchar(100) NOT NULL,
	ValType_Cd     	varchar(50) NULL,
	TVal_Char      	varchar(255) NULL,
	NVal_Num       	decimal(18,5) NULL,
	INSTANCE_NUM	int NULL,
	ValueFlag_Cd   	varchar(50) NULL,
	Quantity_Num   	decimal(18,5) NULL,
	Units_Cd       	varchar(50) NULL,
	End_Date       	datetime NULL,
	Location_Cd    	varchar(50) NULL,
	Observation_Blob text NULL,
	Confidence_Num 	decimal(18,5) NULL,
	Update_Date    	datetime NULL,
	Download_Date  	datetime NULL,
	Import_Date    	datetime NULL,
	Sourcesystem_Cd	varchar(50) NULL, 
    UPLOAD_ID         	INT NULL,
    CONSTRAINT OBSERVATION_FACT_PK PRIMARY KEY nonclustered (encounter_num,concept_cd,provider_id,start_date,modifier_cd)
	)
;