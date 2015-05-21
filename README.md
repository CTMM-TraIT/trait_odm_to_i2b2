ODM-to-i2b2
===========

[![Build Status](https://travis-ci.org/CTMM-TraIT/trait_odm_to_i2b2.png)](https://travis-ci.org/CTMM-TraIT/trait_odm_to_i2b2)
[![Coverage Status](https://coveralls.io/repos/CTMM-TraIT/trait_odm_to_i2b2/badge.png)](https://coveralls.io/r/CTMM-TraIT/trait_odm_to_i2b2)

Fork of the REDCap-to-i2b2 project: 
https://community.i2b2.org/wiki/display/ODM2i2b2/Home

This tools allows you to convert an ODM file to tabular data files that are ready to import in an i2b2-like system like tranSMART.

Status at third release (2015 May 21): 3 tabular files are produced for each study: a clinical data file (with the data),
a columns file (with metadata about each column in the clinical data file) and a wordmap file (with the full word values
that come in place of the numerical codes in the clinical data). This conversion runs without errors for a wide
variety of tested ODM exports from OpenClinica. It also deals appropriately with repeating elements,
like events and item groups.

Main developers:
- Ward Blondé
- Freek de Bruijn

