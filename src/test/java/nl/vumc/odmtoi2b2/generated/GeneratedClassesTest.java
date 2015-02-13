package nl.vumc.odmtoi2b2.generated;

import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import org.cdisk.odm.jaxb.CLDataType;
import org.cdisk.odm.jaxb.CommentType;
import org.cdisk.odm.jaxb.Comparator;
import org.cdisk.odm.jaxb.DataType;
import org.cdisk.odm.jaxb.EditPointType;
import org.cdisk.odm.jaxb.EventType;
import org.cdisk.odm.jaxb.FileType;
import org.cdisk.odm.jaxb.Granularity;
import org.cdisk.odm.jaxb.LocationType;
import org.cdisk.odm.jaxb.MethodType;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionAddress;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionAdminData;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionAlias;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionAnnotation;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionAnnotations;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionArchiveLayout;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionArchiveLayoutRef;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionAssociation;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionAuditRecord;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionAuditRecords;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionBasicDefinitions;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionCertificate;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionCheckValue;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionCity;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionClinicalData;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionCodeListItem;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionCodeListRef;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionComment;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionConditionDef;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionCountry;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionCryptoBindingManifest;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionDateTimeStamp;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionDecode;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionDescription;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionDisplayName;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionEmail;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionEnumeratedItem;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionErrorMessage;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionExternalCodeList;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionExternalQuestion;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionFax;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionFirstName;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionFlag;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionFlagType;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionFlagValue;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionFormData;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionFormDef;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionFormRef;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionFormalExpression;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionFullName;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionGlobalVariables;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionImputationMethod;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionInclude;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionInvestigatorRef;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionItemData;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionItemDataAny;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionItemDataBase64Binary;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionItemDataBase64Float;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionItemDataBoolean;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionItemDataDate;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionItemDataDatetime;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionItemDataDouble;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionItemDataDurationDatetime;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionItemDataFloat;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionItemDataHexBinary;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionItemDataHexFloat;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionItemDataIncompleteDate;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionItemDataIncompleteDatetime;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionItemDataIncompleteTime;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionItemDataInteger;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionItemDataIntervalDatetime;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionItemDataPartialDate;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionItemDataPartialDatetime;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionItemDataPartialTime;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionItemDataString;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionItemDataTime;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionItemDataURI;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionItemGroupData;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionItemGroupRef;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionItemRef;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionKeySet;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionLastName;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionLegalReason;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionLocation;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionLocationRef;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionLoginName;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionMeaning;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionMeasurementUnit;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionMeasurementUnitRef;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionMetaDataVersion;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionMetaDataVersionRef;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionMethodDef;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionOrganization;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionOtherText;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionPager;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionPhone;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionPicture;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionPostalCode;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionPresentation;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionProtocol;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionProtocolName;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionQuestion;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionRangeCheck;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionReasonForChange;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionReferenceData;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionRole;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionSignature;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionSignatureDef;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionSignatureRef;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionSignatures;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionSiteRef;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionSourceID;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionStateProv;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionStreetName;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionStudy;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionStudyDescription;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionStudyEventData;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionStudyEventDef;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionStudyEventRef;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionStudyName;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionSubjectData;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionSymbol;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionTranslatedText;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionUser;
import org.cdisk.odm.jaxb.ODMcomplexTypeDefinitionUserRef;
import org.cdisk.odm.jaxb.SignMethod;
import org.cdisk.odm.jaxb.SoftOrHard;
import org.cdisk.odm.jaxb.TransactionType;
import org.cdisk.odm.jaxb.UserType;
import org.cdisk.odm.jaxb.YesOnly;
import org.cdisk.odm.jaxb.YesOrNo;
import org.junit.Test;
import org.w3.xmldsig.jaxb.CanonicalizationMethodType;
import org.w3.xmldsig.jaxb.DSAKeyValueType;
import org.w3.xmldsig.jaxb.DigestMethodType;
import org.w3.xmldsig.jaxb.KeyInfoType;
import org.w3.xmldsig.jaxb.KeyValueType;
import org.w3.xmldsig.jaxb.ManifestType;
import org.w3.xmldsig.jaxb.ObjectType;
import org.w3.xmldsig.jaxb.PGPDataType;
import org.w3.xmldsig.jaxb.RSAKeyValueType;
import org.w3.xmldsig.jaxb.ReferenceType;
import org.w3.xmldsig.jaxb.RetrievalMethodType;
import org.w3.xmldsig.jaxb.SPKIDataType;
import org.w3.xmldsig.jaxb.SignatureMethodType;
import org.w3.xmldsig.jaxb.SignaturePropertiesType;
import org.w3.xmldsig.jaxb.SignaturePropertyType;
import org.w3.xmldsig.jaxb.SignatureType;
import org.w3.xmldsig.jaxb.SignatureValueType;
import org.w3.xmldsig.jaxb.SignedInfoType;
import org.w3.xmldsig.jaxb.TransformType;
import org.w3.xmldsig.jaxb.TransformsType;
import org.w3.xmldsig.jaxb.X509DataType;
import org.w3.xmldsig.jaxb.X509IssuerSerialType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Unit tests for all the ODM reader classes that are automatically generated by the JAXB xjc tool.
 *
 * @author <a href="mailto:f.debruijn@vumc.nl">Freek de Bruijn</a>
 */
public class GeneratedClassesTest {
    /**
     * Test whether all fields can be set and get.
     */
    @Test
    public void testFields() {
        // org.w3.xmldsig.jaxb
        testFieldsForClasses(CanonicalizationMethodType.class, DigestMethodType.class, DSAKeyValueType.class,
                             KeyInfoType.class, KeyValueType.class, ObjectType.class, PGPDataType.class,
                             ManifestType.class, ReferenceType.class, RetrievalMethodType.class, RSAKeyValueType.class,
                             SignatureMethodType.class, SignaturePropertiesType.class, SignaturePropertyType.class,
                             SignatureType.class, SignatureValueType.class, SignedInfoType.class, SPKIDataType.class,
                             TransformsType.class, TransformType.class, X509DataType.class, X509IssuerSerialType.class);

        // todo: some classes have some issues because of lower/upper case field names.
        // org.cdisk.odm.jaxb
        testFieldsForClasses(CLDataType.class
                , CommentType.class
                , Comparator.class
                , DataType.class
                , EditPointType.class
                , EventType.class
                , FileType.class
                , Granularity.class
                , LocationType.class
                , MethodType.class
//                , ODM.class
                , ODMcomplexTypeDefinitionAddress.class
                , ODMcomplexTypeDefinitionAdminData.class
                , ODMcomplexTypeDefinitionAlias.class
                , ODMcomplexTypeDefinitionAnnotation.class
                , ODMcomplexTypeDefinitionAnnotations.class
                , ODMcomplexTypeDefinitionArchiveLayout.class
                , ODMcomplexTypeDefinitionArchiveLayoutRef.class
                , ODMcomplexTypeDefinitionAssociation.class
                , ODMcomplexTypeDefinitionAuditRecord.class
                , ODMcomplexTypeDefinitionAuditRecords.class
                , ODMcomplexTypeDefinitionBasicDefinitions.class
                , ODMcomplexTypeDefinitionCertificate.class
                , ODMcomplexTypeDefinitionCheckValue.class
                , ODMcomplexTypeDefinitionCity.class
                , ODMcomplexTypeDefinitionClinicalData.class
//                , ODMcomplexTypeDefinitionCodeList.class
                , ODMcomplexTypeDefinitionCodeListItem.class
                , ODMcomplexTypeDefinitionCodeListRef.class
                , ODMcomplexTypeDefinitionComment.class
                , ODMcomplexTypeDefinitionConditionDef.class
                , ODMcomplexTypeDefinitionCountry.class
                , ODMcomplexTypeDefinitionCryptoBindingManifest.class
                , ODMcomplexTypeDefinitionDateTimeStamp.class
                , ODMcomplexTypeDefinitionDecode.class
                , ODMcomplexTypeDefinitionDescription.class
                , ODMcomplexTypeDefinitionDisplayName.class
                , ODMcomplexTypeDefinitionEmail.class
                , ODMcomplexTypeDefinitionEnumeratedItem.class
                , ODMcomplexTypeDefinitionErrorMessage.class
                , ODMcomplexTypeDefinitionExternalCodeList.class
                , ODMcomplexTypeDefinitionExternalQuestion.class
                , ODMcomplexTypeDefinitionFax.class
                , ODMcomplexTypeDefinitionFirstName.class
                , ODMcomplexTypeDefinitionFlag.class
                , ODMcomplexTypeDefinitionFlagType.class
                , ODMcomplexTypeDefinitionFlagValue.class
                , ODMcomplexTypeDefinitionFormalExpression.class
                , ODMcomplexTypeDefinitionFormData.class
                , ODMcomplexTypeDefinitionFormDef.class
                , ODMcomplexTypeDefinitionFormRef.class
                , ODMcomplexTypeDefinitionFullName.class
                , ODMcomplexTypeDefinitionGlobalVariables.class
                , ODMcomplexTypeDefinitionImputationMethod.class
                , ODMcomplexTypeDefinitionInclude.class
                , ODMcomplexTypeDefinitionInvestigatorRef.class
                , ODMcomplexTypeDefinitionItemData.class
                , ODMcomplexTypeDefinitionItemDataAny.class
                , ODMcomplexTypeDefinitionItemDataBase64Binary.class
                , ODMcomplexTypeDefinitionItemDataBase64Float.class
                , ODMcomplexTypeDefinitionItemDataBoolean.class
                , ODMcomplexTypeDefinitionItemDataDate.class
                , ODMcomplexTypeDefinitionItemDataDatetime.class
                , ODMcomplexTypeDefinitionItemDataDouble.class
                , ODMcomplexTypeDefinitionItemDataDurationDatetime.class
                , ODMcomplexTypeDefinitionItemDataFloat.class
                , ODMcomplexTypeDefinitionItemDataHexBinary.class
                , ODMcomplexTypeDefinitionItemDataHexFloat.class
                , ODMcomplexTypeDefinitionItemDataIncompleteDate.class
                , ODMcomplexTypeDefinitionItemDataIncompleteDatetime.class
                , ODMcomplexTypeDefinitionItemDataIncompleteTime.class
                , ODMcomplexTypeDefinitionItemDataInteger.class
                , ODMcomplexTypeDefinitionItemDataIntervalDatetime.class
                , ODMcomplexTypeDefinitionItemDataPartialDate.class
                , ODMcomplexTypeDefinitionItemDataPartialDatetime.class
                , ODMcomplexTypeDefinitionItemDataPartialTime.class
                , ODMcomplexTypeDefinitionItemDataString.class
                , ODMcomplexTypeDefinitionItemDataTime.class
                , ODMcomplexTypeDefinitionItemDataURI.class
//                , ODMcomplexTypeDefinitionItemDef.class
                , ODMcomplexTypeDefinitionItemGroupData.class
//                , ODMcomplexTypeDefinitionItemGroupDef.class
                , ODMcomplexTypeDefinitionItemGroupRef.class
                , ODMcomplexTypeDefinitionItemRef.class
                , ODMcomplexTypeDefinitionKeySet.class
                , ODMcomplexTypeDefinitionLastName.class
                , ODMcomplexTypeDefinitionLegalReason.class
                , ODMcomplexTypeDefinitionLocation.class
                , ODMcomplexTypeDefinitionLocationRef.class
                , ODMcomplexTypeDefinitionLoginName.class
                , ODMcomplexTypeDefinitionMeaning.class
                , ODMcomplexTypeDefinitionMeasurementUnit.class
                , ODMcomplexTypeDefinitionMeasurementUnitRef.class
                , ODMcomplexTypeDefinitionMetaDataVersion.class
                , ODMcomplexTypeDefinitionMetaDataVersionRef.class
                , ODMcomplexTypeDefinitionMethodDef.class
                , ODMcomplexTypeDefinitionOrganization.class
                , ODMcomplexTypeDefinitionOtherText.class
                , ODMcomplexTypeDefinitionPager.class
                , ODMcomplexTypeDefinitionPhone.class
                , ODMcomplexTypeDefinitionPicture.class
                , ODMcomplexTypeDefinitionPostalCode.class
                , ODMcomplexTypeDefinitionPresentation.class
                , ODMcomplexTypeDefinitionProtocol.class
                , ODMcomplexTypeDefinitionProtocolName.class
                , ODMcomplexTypeDefinitionQuestion.class
                , ODMcomplexTypeDefinitionRangeCheck.class
                , ODMcomplexTypeDefinitionReasonForChange.class
                , ODMcomplexTypeDefinitionReferenceData.class
                , ODMcomplexTypeDefinitionRole.class
                , ODMcomplexTypeDefinitionSignature.class
                , ODMcomplexTypeDefinitionSignatureDef.class
                , ODMcomplexTypeDefinitionSignatureRef.class
                , ODMcomplexTypeDefinitionSignatures.class
                , ODMcomplexTypeDefinitionSiteRef.class
                , ODMcomplexTypeDefinitionSourceID.class
                , ODMcomplexTypeDefinitionStateProv.class
                , ODMcomplexTypeDefinitionStreetName.class
                , ODMcomplexTypeDefinitionStudy.class
                , ODMcomplexTypeDefinitionStudyDescription.class
                , ODMcomplexTypeDefinitionStudyEventData.class
                , ODMcomplexTypeDefinitionStudyEventDef.class
                , ODMcomplexTypeDefinitionStudyEventRef.class
                , ODMcomplexTypeDefinitionStudyName.class
                , ODMcomplexTypeDefinitionSubjectData.class
                , ODMcomplexTypeDefinitionSymbol.class
                , ODMcomplexTypeDefinitionTranslatedText.class
                , ODMcomplexTypeDefinitionUser.class
                , ODMcomplexTypeDefinitionUserRef.class);

        // todo: check value and fromValue methods for enum classes like SignMethod.class.
    }

    /**
     * Test whether all fields can be set and get for objects of a range of classes.
     *
     * @param dataTypeClasses all the data type classes to test.
     */
    private void testFieldsForClasses(final Class... dataTypeClasses) {
        // todo: iterate over all classes in the two packages.
        //"org.cdisk.odm.jaxb"))
//        String packageName = "org.w3.xmldsig.jaxb";
//        Package.getPackage(packageName)...

        for (final Class dataTypeClass : dataTypeClasses)
            testFieldsForClass(dataTypeClass);
    }

    /**
     * Test whether all fields can be set and get for objects of a specific class.
     *
     * @param dataTypeClass the data type class to test.
     */
    private void testFieldsForClass(final Class dataTypeClass) {
        for (final Method method : dataTypeClass.getDeclaredMethods())
            if (method.getName().startsWith("get")) {
                final String propertyName = method.getName().substring(3);
                if (method.getReturnType() != List.class)
                    testSingleField(dataTypeClass, propertyName);
                else
                    testListField(dataTypeClass, propertyName);
            }
    }

    /**
     * Test whether a single field of a specific data type class can be set and get.
     *
     * @param dataTypeClass the data type class to test.
     * @param propertyName  the name of the property (starting with one or more upper case characters).
     */
    private void testSingleField(final Class dataTypeClass, final String propertyName) {
        try {
            final String fieldName = getFieldName(propertyName);
            final Object dataType = createDataType(dataTypeClass);
            final Class propertyClass = dataType.getClass().getDeclaredField(fieldName).getType();

            final Object propertyValue;
            if (propertyClass == Object.class)
                propertyValue = "object";
            else if (propertyClass == String.class)
                propertyValue = "string";
            else if (propertyClass == byte[].class)
                propertyValue = "byte array".getBytes();
            else if (propertyClass == BigDecimal.class)
                propertyValue = new BigDecimal("123456.7890");
            else if (propertyClass == BigInteger.class)
                propertyValue = new BigInteger("123456");
            // todo: can we pick a value for enum classes automatically?
            else if (propertyClass == CommentType.class)
                propertyValue = CommentType.SPONSOR;
            else if (propertyClass == Comparator.class)
                propertyValue = Comparator.NOTIN;
            else if (propertyClass == EditPointType.class)
                propertyValue = EditPointType.MONITORING;
            else if (propertyClass == EventType.class)
                propertyValue = EventType.COMMON;
            else if (propertyClass == FileType.class)
                propertyValue = FileType.TRANSACTIONAL;
            else if (propertyClass == Granularity.class)
                propertyValue = Granularity.ALL_CLINICAL_DATA;
            else if (propertyClass == LocationType.class)
                propertyValue = LocationType.LAB;
            else if (propertyClass == MethodType.class)
                propertyValue = MethodType.COMPUTATION;
            else if (propertyClass == SignMethod.class)
                propertyValue = SignMethod.DIGITAL;
            else if (propertyClass == SoftOrHard.class)
                propertyValue = SoftOrHard.SOFT;
            else if (propertyClass == TransactionType.class)
                propertyValue = TransactionType.UPSERT;
            else if (propertyClass == UserType.class)
                propertyValue = UserType.INVESTIGATOR;
            else if (propertyClass == YesOnly.class)
                propertyValue = YesOnly.YES;
            else if (propertyClass == YesOrNo.class)
                propertyValue = YesOrNo.NO;
            else if (propertyClass == XMLGregorianCalendar.class)
                propertyValue = new XMLGregorianCalendarImpl();
            else {
//                System.out.println("Calling createDataType in testSingleField - dataTypeClass: " + dataTypeClass.getName()
//                                   + "; propertyName: " + propertyName);
                propertyValue = createDataType(propertyClass);
            }

            final Method setValueMethod = dataType.getClass().getMethod("set" + propertyName, propertyClass);
            final Method getValueMethod = dataType.getClass().getMethod("get" + propertyName);

            setValueMethod.invoke(dataType, propertyValue);
            assertEquals(propertyValue, getValueMethod.invoke(dataType));
        } catch (final ReflectiveOperationException e) {
            System.err.println("Exception in testSingleField - dataTypeClass: " + dataTypeClass.getName()
                               + "; propertyName: " + propertyName);
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    /**
     * Attempt to determine the field name (starting with one or more lower case characters) from the property name
     * (starting with one or more upper case characters).
     *
     * @param propertyName the property name (starting with one or more upper case characters).
     * @return the predicted field name (starting with one or more lower case characters).
     */
    private String getFieldName(final String propertyName) {
        int upperCaseLength = 1;
        while (upperCaseLength < propertyName.length() && Character.isUpperCase(propertyName.charAt(upperCaseLength)))
            upperCaseLength++;

        return propertyName.substring(0, upperCaseLength).toLowerCase() + propertyName.substring(upperCaseLength);
    }

    /**
     * Create an object of a certain data type class (using the default constructor).
     *
     * @param dataTypeClass the data type class to create an instance from.
     * @return the instance of the data type class or null if object construction failed.
     */
    private Object createDataType(final Class dataTypeClass) {
        Object dataType = null;

        try {
            final Object objectFactory;
            if (dataTypeClass.getName().startsWith("org.cdisk.odm.jaxb"))
                objectFactory = org.cdisk.odm.jaxb.ObjectFactory.class.newInstance();
            else if (dataTypeClass.getName().startsWith("org.w3.xmldsig.jaxb"))
                objectFactory = org.w3.xmldsig.jaxb.ObjectFactory.class.newInstance();
            else
                objectFactory = null;

            if (objectFactory != null) {
                final String creatorMethodName = "create" + dataTypeClass.getSimpleName();
                final Method createDataTypeMethod = objectFactory.getClass().getMethod(creatorMethodName);
                dataType = createDataTypeMethod.invoke(objectFactory);
            } else {
                fail("Error creating object factory for data type class " + dataTypeClass.getName());
            }
        } catch (final ReflectiveOperationException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

        return dataType;
    }

    /**
     * Test whether a list field of a specific data type class can be set and get.
     *
     * @param dataTypeClass the data type class to create an instance from.
     * @param propertyName  the property name (starting with one or more upper case characters).
     */
    @SuppressWarnings("unchecked")
    private void testListField(final Class dataTypeClass, final String propertyName) {
        try {
            final Object dataType = createDataType(dataTypeClass);
            final Method getListMethod = dataType.getClass().getMethod("get" + propertyName);
            final Object item = "item";

            // Note: even when the list has a type that does not support strings, adding the item works because of type
            // erasure (Java generics only work at compile time; each List<SomeClass> is a non-generic List at run-time).
            // See for example https://en.wikipedia.org/wiki/Generics_in_Java#Problems_with_type_erasure for more
            // information.

            // For example, the ManifestType class has a field called reference which has type List<ReferenceType>. We
            // can add a string to this list at run-time without problems. The @SuppressWarnings("unchecked") annotation
            // at the top of this method suppresses compiler messages for the call to add on the non-generic List.

            ((List) getListMethod.invoke(dataType)).add(item);

            assertEquals(1, ((List) getListMethod.invoke(dataType)).size());
            assertEquals(item, ((List) getListMethod.invoke(dataType)).get(0));
        } catch (final ReflectiveOperationException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
}
