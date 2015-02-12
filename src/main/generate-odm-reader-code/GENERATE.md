Java classes are generated for handling ODM files using the JAXB (Java Architecture for XML Binding) related xjc binding
compiler. The source code of these ODM reader classes are created by the xjc tool with xsd files that describe the ODM
standard as input. See for example https://jaxb.java.net/2.2.11/docs/ch04.html#tools-xjc or
https://en.wikipedia.org/wiki/Java_Architecture_for_XML_Binding for more information on xjc and JAXB.

The main schema file (ODM1-3-1.xsd) depends on several other schema files: ODM1-3-1-foundation.xsd, xml.xsd, and
xmldsig-core-schema.xsd. The bindings file (bindings.xml) modifies the default package names that are used. The Java
classes are put in two packages: org.cdisk.odm.jaxb (ODM1-3-1.xsd) and org.w3.xmldsig.jaxb (xmldsig-core-schema.xsd).

The call to xjc looks something like this:
xjc [schema file] -b [bindings file] -d [destination directory]

For example, on the Windows platform using ODM 1.3.1, you could generate the ODM reader classes like this:
mkdir java-generated
"\Programs\jaxb-ri\bin\xjc.bat" xsd\cdisc-odm-1.3.1\ODM1-3-1.xsd -b xsd\cdisc-odm-1.3.1\bindings.xml -d java-generated
