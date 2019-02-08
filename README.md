ImageJ plugin auto_qc

Created by group 4 of the NEUBIAS trainingschool 11.

Erick Ratamero, Cesar Augusto Valades Cruz, Christopher Schmied, Jan Eckhardt, Paul Stroe.

Based on the .py script auto_PSF.py from Erick Ratamero:
https://github.com/erickmartins/autoQC/blob/master/auto_PSF.py

Special thanks goes to:
Robert Haase
Jean-Yves Tinevez
For their help and guidance

NEUBIAS and NEUBIAS TS organisers for creating the environment
Fiji and ImageJ1/2

==========================================================================================


Title: Automatic PSF quality measurement

Description:

Input file:

Input parameters - mostly hardcoded for now

- Input file - GUI interaction that points to any file where the input files are
    From this the base directory is extracted

- File extension: self-explanatory
- Number of beads: how many beads should be analysed
- Correction factors: multiplying factor for the FWHM resolution output from MetroloJ, per dimension
´
Finds the relevant image with beads in the input
directory, crops a number of beads and uses MetroloJ
to generate resolution values.

Output: summary table with quality measurements per bead
        Will be written into the base input directory

Functions: