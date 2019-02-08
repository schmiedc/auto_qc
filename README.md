ImageJ plugin auto_qc

Created by group 4 of the NEUBIAS trainingschool 11.

Erick Ratamero, Cesar Augusto Valades Cruz, Christopher Schmied, Jan Eckhardt, Paul Stroe.

Based on the .py script auto_PSF.py from Erick Ratamero:
https://github.com/erickmartins/autoQC/blob/master/auto_PSF.py

Uses the following plugins:
bioformats (https://www.openmicroscopy.org/bio-formats/)

MetroloJ (http://imagejdocu.tudor.lu/doku.php?id=plugin:analysis:metroloj:start)

==========================================================================================

Special thanks goes to:
Robert Haase
Jean-Yves Tinevez
For their help and guidance

NEUBIAS and NEUBIAS TS organisers for creating the environment
Fiji and ImageJ1/2 for being an awesome software and community!

==========================================================================================

Title: Automatic PSF quality measurement

Input file: a file of the specified file format (file extension) containing the string "psf"
Example data used for testing this was named 100_psf_auxin_sf01_R3D.dv
Dimension 1024x1024x21 16Bit
Pixelsize: 0.04031x0.04031x0.15 micron (Metadata matters)
Content: PSFs from flourescent beads
         The usual PSF measurement criteria apply:
         Well separated beads, not too dense, subresolution.

Output: summary table with quality measurements per bead
        Will be written into the base input directory

Input parameters - mostly hardcoded for now

- Input file - GUI interaction that points to any file where the input files are
    From this the base directory is extracted

- File extension: self-explanatory
- Number of beads: how many beads should be analysed
- Correction factors: multiplying factor for the FWHM resolution output from MetroloJ, per dimension
- minSeparation: minimal distance (probably in pixels?) that two PSFs need to be separated by to be considered

Description:

Finds the relevant stack within the specified diretory that contains the bead measurement.
Opens the stack using bioformats importer.
Crops the stack to get the center of the field of view.
Gets the slice that is in focus (max standard deviation).
Detects the beads via maximum detection.
Takes the location of the point detection.
Extracts the intensity of the pixel at that point.
Selects the specified number of beads with the lowest intensity that are well separated.
Crops out these selected beads from the stack.
Applies PSFprofiler of the MetroloJ plugin to get resolution of microscope.

Functions of the plugin:

run
readFile
processing
WriteFile
GetRes

----------------------------------------------------------------------------------------
ToDo:

User interaction with GUI
Images are shown at the moment (batchmode?)
Close opened images
Documentation of the code
Refactor


