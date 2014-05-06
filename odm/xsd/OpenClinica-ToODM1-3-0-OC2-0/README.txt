
README for generating Java classes from the xsd files released by OpenClinica

Download ODM-xsd files from OpenClinica:
https://dev.openclinica.com/tools/odm-doc/

Assemble the xsd files in a directory below odm/xsd: (for instance in OpenClinica-ToODM1-3-0-OC2-0)
ODM1-3-0-foundation.xsd                     
ODM1-3-0.xsd                                
OpenClinica-ODM1-3-0-OC2-0-foundation.xsd   
OpenClinica-ToODM1-3-0-OC2-0.xsd            
bindings.xml                                
rules-ODM.xsd                               
xml.xsd                                     
xmldsig-core-schema.xsd 

In the build.xml file, have the following files referenced:
   <arg value="${xsd.dir}/OpenClinica-ToODM1-3-0-OC2-0/bindings.xml" />
   <arg value="${xsd.dir}/OpenClinica-ToODM1-3-0-OC2-0/ODM1-3-0.xsd" />
   <arg value="${xsd.dir}/OpenClinica-ToODM1-3-0-OC2-0/OpenClinica-ToODM1-3-0-OC2-0.xsd" />

In the bindings file have the following bindings:
  <bindings schemaLocation="OpenClinica-ToODM1-3-0-OC2-0.xsd">
    <schemaBindings>
      <package name="org.cdisk.odm.jaxb"/>
    </schemaBindings>
  </bindings>
  <bindings schemaLocation="xmldsig-core-schema.xsd">
    <schemaBindings>
      <package name="org.w3.xmldsig.jaxb"/>
    </schemaBindings>
  </bindings>
  
There seems to be a bug in the OpenClinica-ODM1-3-0-OC2-0-foundation.xsd file.
On line 3, change
  <xs:import schemaLocation="http://www.w3.org/2001/03/xml.xsd" namespace="http://www.w3.org/XML/1998/namespace"/>
into
  <xs:import schemaLocation="xml.xsd" namespace="http://www.w3.org/XML/1998/namespace"/>

Finally, type ant on the command line of the odm directory, which contains build.xml.
