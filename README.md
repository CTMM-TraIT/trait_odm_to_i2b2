ODM-to-i2b2
===========

[![Build Status](https://travis-ci.org/CTMM-TraIT/trait_odm_to_i2b2.png)](https://travis-ci.org/CTMM-TraIT/trait_odm_to_i2b2)
[![Coverage Status](https://coveralls.io/repos/CTMM-TraIT/trait_odm_to_i2b2/badge.png)](https://coveralls.io/r/CTMM-TraIT/trait_odm_to_i2b2)

A [presentation](https://github.com/CTMM-TraIT/trait_odm_to_i2b2/blob/master/src/documentation/ODM%20to%20i2b2%20F2F%20The%20Hyve.pdf) of the project, [users information](https://github.com/CTMM-TraIT/trait_odm_to_i2b2/blob/master/src/documentation/UsersInformation.md) and [developers information](https://github.com/CTMM-TraIT/trait_odm_to_i2b2/blob/master/src/documentation/DevelopersInformation.md) are all included in this repository.

The ODM-to-i2b2 project is a forked version of the REDCap-to-i2b2 project with added functionality:
[community.i2b2.org/wiki/display/ODM2i2b2](https://community.i2b2.org/wiki/display/ODM2i2b2/Home).

This tools allows you to convert an ODM file to tabular data files that are ready to be imported in an i2b2-like system like [tranSMART](http://transmartfoundation.org/). The '[OpenClinica](https://openclinica.com/) to tranSMART' conversion, which exports tabular text files, builds on the same automatically generated Java sources as the '[REDCap](http://project-redcap.org/) to i2b2' conversion, which exports to a database:

![Image project structure](https://github.com/CTMM-TraIT/trait_odm_to_i2b2/blob/master/src/documentation/flag_RedCap_to_OCTM.png)

Status at release 3.0 (June 12th 2015): 3 tabular files are produced for each study:
- a clinical data file (with the data),
- a columns file (with metadata about each column in the clinical data file) and
- a wordmap file (with the full word values that come in place of the numerical codes that can be used in the clinical data file).

This conversion runs without errors for a wide variety of tested ODM exports from OpenClinica. It also deals appropriately with repeating elements, like events and item groups.

Main developers:
- Ward Blondé
- Freek de Bruijn
