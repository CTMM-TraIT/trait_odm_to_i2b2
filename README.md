ODM-to-i2b2
===========

Fork of the REDCap-to-i2b2 project: 
https://community.i2b2.org/wiki/display/ODM2i2b2/Home

We investigate the conversion of ODM data to i2b2 with respect to OpenClinica and tranSMART.
Status at first release (2014 May 28): 3 tabular files are produced for each study: a columns file,
a wordmap file and a clinical data file. This conversion runs now without errors for a wide
variety of tested ODM exports from OpenClinica. However, the tool is not finished since there are
still exported tabular files that cannot be loaded in the i2b2 database of tranSMART. Remaining
issues are:
- repeating elements, like events and item groups
- OpenClinica extensions
- the exact convention for writing tab-separated files
- the pom-file and logging
